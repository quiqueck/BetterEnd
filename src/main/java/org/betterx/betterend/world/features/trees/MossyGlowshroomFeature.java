package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.*;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.sdf.primitive.SDFPrimitive;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.blocks.MossyGlowshroomCapBlock;
import org.betterx.betterend.blocks.basis.FurBlock;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class MossyGlowshroomFeature extends DefaultFeature {
    private static final Function<BlockState, Boolean> REPLACE;
    private static final Vector3f CENTER = new Vector3f();
    private static final SDFBinary FUNCTION;
    private static final SDFTranslate HEAD_POS;
    private static final SDFFlatWave ROOTS_ROT;

    private static final SDFPrimitive CONE1;
    private static final SDFPrimitive CONE2;
    private static final SDFPrimitive CONE_GLOW;
    private static final SDFPrimitive ROOTS;

    /**
     * The cap's nominal outer radius: {@code CONE2}'s {@code radius2} as the static shape below builds it.
     */
    private static final float CAP_RADIUS = 13F;
    private static final float GLOW_RADIUS = 12.5F;
    /**
     * The cap's widest copy is {@code innerCone}, which runs the same cone through
     * {@code SDFScale3D(1.2, 1, 1.2)}.
     */
    private static final float INNER_CONE_SCALE = 1.2F;
    /**
     * What the cap reaches beyond {@code CAP_RADIUS * INNER_CONE_SCALE}: the flat wave's intensity of 1.3
     * plus about a block of {@code SDFSmoothUnion} bulge at the seam.
     */
    private static final float CAP_BULGE = 2.3F;
    /**
     * The cap's horizontal reach at {@code scale == 1}, i.e. how many blocks of headroom one unit of
     * {@code scale} costs.
     */
    private static final float CAP_REACH = CAP_RADIUS * INNER_CONE_SCALE + CAP_BULGE;
    /**
     * Floor for the fitted scale. The natural range is 0.75 to 1.1 and the smallest fit a legal trunk can
     * produce is about 0.71, so this is a guard rather than a case that fires.
     */
    private static final float MIN_SCALE = 0.5F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos blockPos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        BlockState down = world.getBlockState(blockPos.below());
        if (!down.is(EndTerrainBlocks.END_MYCELIUM) && !down.is(EndTerrainBlocks.END_MOSS)) return false;

        // height reaches 25 and the cap's wave/roots geometry adds further spread - both the canGenerate
        // probe and the final flood-fill are otherwise bounded only by their own shape, past the 3x3 chunks
        // a feature may touch. Clip both to the write zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);

        CONE1.setBlock(EndWoodBlocks.MOSSY_GLOWSHROOM_CAP);
        CONE2.setBlock(EndWoodBlocks.MOSSY_GLOWSHROOM_CAP);
        CONE_GLOW.setBlock(EndWoodBlocks.MOSSY_GLOWSHROOM_HYMENOPHORE);
        ROOTS.setBlock(EndWoodBlocks.MOSSY_GLOWSHROOM.getBark());

        float height = MHelper.randRange(10F, 25F, random);
        int count = MHelper.floor(height / 4);
        List<Vector3f> spline = SplineHelper.makeSpline(0, 0, 0, 0, height, 0, count);
        SplineHelper.offsetParts(spline, random, 1F, 0, 1F);
        SDF sdf = SplineHelper.buildSDF(spline, 2.1F, 1.5F, (pos) -> {
            return EndWoodBlocks.MOSSY_GLOWSHROOM.getLog().defaultBlockState();
        });
        Vector3f pos = spline.get(spline.size() - 1);
        float scale = MHelper.randRange(0.75F, 1.1F, random);

        if (!SplineHelper.canGenerate(spline, scale, blockPos, world, REPLACE, zone.toBoundingBox())) {
            return false;
        }
        BlocksHelper.setWithoutUpdate(world, blockPos, AIR);

        // The cap is a single blob centred on the trunk top, and it is by far the widest thing this feature
        // builds: CAP_REACH * scale is up to ~20 blocks against the 16 a feature is guaranteed. What the
        // write bounds then take out of it is a straight chord through a smooth disc - shallow, but a
        // mushroom cap is a circle and a circle with a flat side reads as broken at a glance.
        //
        // The knob is `scale`, not the cone radii. The cap shape is a static SDF graph shared by every
        // placement (CONE1/CONE2/HEAD_POS/... are all re-set per call, on whatever thread gets there
        // first), so re-setting a radius per tree would add a second tree's worth of geometry to that
        // existing race - and unlike a wrong translate, a wrong radius is exactly the clipped cap this is
        // meant to prevent. `scale` is a local, applied through the per-call SDFScale below, so shrinking
        // it is thread-safe. It costs a slightly shorter trunk as well as a smaller cap, which is not
        // distinguishable from natural variation: the roll is 0.75..1.1 to begin with, and the tightest
        // legal trunk position only pulls it down to about 0.71.
        final float room = zone.headroom(
                blockPos.getX() + Mth.floor(pos.x() * scale),
                blockPos.getZ() + Mth.floor(pos.z() * scale)
        );
        scale = Math.max(Math.min(scale, room / CAP_REACH), MIN_SCALE);

        CENTER.set(blockPos.getX(), 0, blockPos.getZ());
        HEAD_POS.setTranslate(pos.x(), pos.y(), pos.z());
        ROOTS_ROT.setAngle(random.nextFloat() * MHelper.PI2);
        FUNCTION.setSourceA(sdf);

        new SDFScale().setScale(scale).setSource(FUNCTION).setReplaceFunction(REPLACE).addPostProcess((info) -> {
            if (EndWoodBlocks.MOSSY_GLOWSHROOM.isTreeLog(info.getState())) {
                if (random.nextBoolean() && info.getStateUp().getBlock() == EndWoodBlocks.MOSSY_GLOWSHROOM_CAP) {
                    info.setState(EndWoodBlocks.MOSSY_GLOWSHROOM_CAP.defaultBlockState()
                                                                .setValue(MossyGlowshroomCapBlock.TRANSITION, true));
                    return info.getState();
                } else if (!EndWoodBlocks.MOSSY_GLOWSHROOM.isTreeLog(info.getStateUp()) || !EndWoodBlocks.MOSSY_GLOWSHROOM.isTreeLog(
                        info.getStateDown())) {
                    info.setState(EndWoodBlocks.MOSSY_GLOWSHROOM.getBark().defaultBlockState());
                    return info.getState();
                }
            } else if (info.getState().getBlock() == EndWoodBlocks.MOSSY_GLOWSHROOM_CAP) {
                if (EndWoodBlocks.MOSSY_GLOWSHROOM.isTreeLog(info.getStateDown().getBlock())) {
                    info.setState(EndWoodBlocks.MOSSY_GLOWSHROOM_CAP.defaultBlockState()
                                                                .setValue(MossyGlowshroomCapBlock.TRANSITION, true));
                    return info.getState();
                }

                info.setState(EndWoodBlocks.MOSSY_GLOWSHROOM_CAP.defaultBlockState());
                return info.getState();
            } else if (info.getState().getBlock() == EndWoodBlocks.MOSSY_GLOWSHROOM_HYMENOPHORE) {
                for (Direction dir : BlocksHelper.HORIZONTAL) {
                    if (info.getState(dir) == AIR) {
                        info.setBlockPos(
                                info.getPos().relative(dir),
                                EndWoodBlocks.MOSSY_GLOWSHROOM_FUR.defaultBlockState().setValue(FurBlock.FACING, dir)
                        );
                    }
                }

                if (info.getStateDown().getBlock() != EndWoodBlocks.MOSSY_GLOWSHROOM_HYMENOPHORE) {
                    info.setBlockPos(
                            info.getPos().below(),
                            EndWoodBlocks.MOSSY_GLOWSHROOM_FUR.defaultBlockState().setValue(FurBlock.FACING, Direction.DOWN)
                    );
                }
            }
            return info.getState();
        }).fillRecursive(world, blockPos, zone.toBoundingBox());

        EndTreeHelper.waterlogSubmerged(world, blockPos, 26);
        return true;
    }

    static {
        SDFCappedCone cone1 = new SDFCappedCone().setHeight(2.5F).setRadius1(1.5F).setRadius2(2.5F);
        SDFCappedCone cone2 = new SDFCappedCone().setHeight(3F).setRadius1(2.5F).setRadius2(CAP_RADIUS);
        SDF posedCone2 = new SDFTranslate().setTranslate(0, 5, 0).setSource(cone2);
        SDF posedCone3 = new SDFTranslate().setTranslate(0, 12F, 0)
                                           .setSource(new SDFScale().setScale(2).setSource(cone2));
        SDF upCone = new SDFSubtraction().setSourceA(posedCone2).setSourceB(posedCone3);
        SDF wave = new SDFFlatWave().setRaysCount(12).setIntensity(1.3F).setSource(upCone);
        SDF cones = new SDFSmoothUnion().setRadius(3).setSourceA(cone1).setSourceB(wave);

        CONE1 = cone1;
        CONE2 = cone2;

        SDF innerCone = new SDFTranslate().setTranslate(0, 1.25F, 0).setSource(upCone);
        innerCone = new SDFScale3D().setScale(1.2F, 1F, 1.2F).setSource(innerCone);
        cones = new SDFUnion().setSourceA(cones).setSourceB(innerCone);

        SDF glowCone = new SDFCappedCone().setHeight(3F).setRadius1(2F).setRadius2(GLOW_RADIUS);
        CONE_GLOW = (SDFPrimitive) glowCone;
        glowCone = new SDFTranslate().setTranslate(0, 4.25F, 0).setSource(glowCone);
        glowCone = new SDFSubtraction().setSourceA(glowCone).setSourceB(posedCone3);

        cones = new SDFUnion().setSourceA(cones).setSourceB(glowCone);

        OpenSimplexNoise noise = new OpenSimplexNoise(1234);
        cones = new SDFCoordModify().setFunction((pos) -> {
            float dist = MHelper.length(pos.x(), pos.z());
            float y = pos.y() + (float) noise.eval(
                    pos.x() * 0.1 + CENTER.x(),
                    pos.z() * 0.1 + CENTER.z()
            ) * dist * 0.3F - dist * 0.15F;
            pos.set(pos.x(), y, pos.z());
        }).setSource(cones);

        HEAD_POS = (SDFTranslate) new SDFTranslate().setSource(new SDFTranslate().setTranslate(0, 2.5F, 0)
                                                                                 .setSource(cones));

        SDF roots = new SDFSphere().setRadius(4F);
        ROOTS = (SDFPrimitive) roots;
        roots = new SDFScale3D().setScale(1, 0.7F, 1).setSource(roots);
        ROOTS_ROT = (SDFFlatWave) new SDFFlatWave().setRaysCount(5).setIntensity(1.5F).setSource(roots);

        FUNCTION = new SDFSmoothUnion().setRadius(4)
                                       .setSourceB(new SDFUnion().setSourceA(HEAD_POS).setSourceB(ROOTS_ROT));

        REPLACE = BlocksHelper::replaceableOrPlant;
    }
}
