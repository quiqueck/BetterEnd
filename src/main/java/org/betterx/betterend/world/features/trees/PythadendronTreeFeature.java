package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.PosInfo;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFScale3D;
import org.betterx.bclib.sdf.operator.SDFSubtraction;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class PythadendronTreeFeature extends DefaultFeature {
    private static final Function<BlockState, Boolean> REPLACE;
    private static final Function<BlockState, Boolean> IGNORE;
    private static final Function<PosInfo, BlockState> POST;

    /**
     * How far past its nominal radius a leaf ball's surface reaches: {@code noise * 3} plus
     * {@code nextFloat() * 3 - 1.5}, both applied through {@code SDFDisplacement}, which adds to the
     * distance - so the negative half of each range grows the shape.
     */
    private static final float LEAF_BALL_BULGE = 4.5F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (world.getBlockState(pos.below()).getBlock() != EndTerrainBlocks.CHORUS_NYLIUM) {
            return false;
        }
        BlocksHelper.setWithoutUpdate(world, pos, AIR);

        // The recursive branch() below fans out repeatedly (each level spawns two more, up to `depth`
        // levels), and the trunk/leaf-ball flood-fills are otherwise bounded only by their own shape - all
        // of it can reach past the 3x3 chunks a feature may touch. Clip every read/write to the write zone;
        // see WriteZone.
        final WriteZone zone = WriteZone.of(world);

        float size = MHelper.randRange(10, 20, random);
        List<Vector3f> spline = SplineHelper.makeSpline(0, 0, 0, 0, size, 0, 4);
        SplineHelper.offsetParts(spline, random, 0.7F, 0, 0.7F);
        Vector3f last = spline.get(spline.size() - 1);

        int depth = MHelper.floor((size - 10F) * 3F / 10F + 1F);
        float bsize = (10F - (size - 10F)) / 10F + 1.5F;
        branch(
                last.x(),
                last.y(),
                last.z(),
                size * bsize,
                MHelper.randRange(0, MHelper.PI2, random),
                random,
                depth,
                world,
                pos,
                zone
        );

        SDF function = SplineHelper.buildSDF(
                spline,
                1.7F,
                1.1F,
                (bpos) -> EndWoodBlocks.PYTHADENDRON.getBark().defaultBlockState()
        );
        function.setReplaceFunction(REPLACE);
        function.addPostProcess(POST);
        function.fillRecursive(world, pos, zone.toBoundingBox());

        EndTreeHelper.waterlogSubmerged(world, pos, 28);
        return true;
    }

    private void branch(
            float x,
            float y,
            float z,
            float size,
            float angle,
            RandomSource random,
            int depth,
            WorldGenLevel world,
            BlockPos pos,
            WriteZone zone
    ) {
        if (depth == 0) return;

        float dx = (float) Math.cos(angle) * size * 0.15F;
        float dz = (float) Math.sin(angle) * size * 0.15F;

        float x1 = x + dx;
        float z1 = z + dz;
        float x2 = x - dx;
        float z2 = z - dz;

        final BoundingBox bounds = zone.toBoundingBox();

        List<Vector3f> spline = SplineHelper.makeSpline(x, y, z, x1, y, z1, 5);
        SplineHelper.powerOffset(spline, size * MHelper.randRange(1.0F, 2.0F, random), 4);
        SplineHelper.offsetParts(spline, random, 0.3F, 0, 0.3F);
        Vector3f pos1 = spline.get(spline.size() - 1);

        boolean s1 = SplineHelper.fillSpline(
                spline,
                world,
                EndWoodBlocks.PYTHADENDRON.getBark().defaultBlockState(),
                pos,
                REPLACE,
                bounds
        );

        spline = SplineHelper.makeSpline(x, y, z, x2, y, z2, 5);
        SplineHelper.powerOffset(spline, size * MHelper.randRange(1.0F, 2.0F, random), 4);
        SplineHelper.offsetParts(spline, random, 0.3F, 0, 0.3F);
        Vector3f pos2 = spline.get(spline.size() - 1);

        boolean s2 = SplineHelper.fillSpline(
                spline,
                world,
                EndWoodBlocks.PYTHADENDRON.getBark().defaultBlockState(),
                pos,
                REPLACE,
                bounds
        );

        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextInt());
        if (depth < 3) {
            if (s1) {
                leavesBall(world, pos.offset((int) pos1.x(), (int) pos1.y(), (int) pos1.z()), random, noise, zone);
            }
            if (s2) {
                leavesBall(world, pos.offset((int) pos2.x(), (int) pos2.y(), (int) pos2.z()), random, noise, zone);
            }
        }

        float size1 = size * MHelper.randRange(0.75F, 0.95F, random);
        float size2 = size * MHelper.randRange(0.75F, 0.95F, random);
        float angle1 = angle + (float) Math.PI * 0.5F + MHelper.randRange(-0.1F, 0.1F, random);
        float angle2 = angle + (float) Math.PI * 0.5F + MHelper.randRange(-0.1F, 0.1F, random);

        if (s1) {
            branch(pos1.x(), pos1.y(), pos1.z(), size1, angle1, random, depth - 1, world, pos, zone);
        }
        if (s2) {
            branch(pos2.x(), pos2.y(), pos2.z(), size2, angle2, random, depth - 1, world, pos, zone);
        }
    }

    private void leavesBall(
            WorldGenLevel world,
            BlockPos pos,
            RandomSource random,
            OpenSimplexNoise noise,
            WriteZone zone
    ) {
        float radius = MHelper.randRange(4.5F, 6.5F, random);
        // Pythadendron's leaf balls are what hits the wall, not its branches: the recursive fan walks only
        // about 12 blocks out in total (four levels of size * 0.15 each), inside the 16 blocks of room a
        // feature is always guaranteed, while a ball on the end of one reaches radius + 4.5 further. Size
        // the ball to the room its own centre has and the write bounds have nothing left to cut.
        radius = EndTreeHelper.fitBallRadius(zone, pos, radius, LEAF_BALL_BULGE, 2.5F);

        SDF sphere = new SDFSphere().setRadius(radius)
                                    .setBlock(EndWoodBlocks.PYTHADENDRON_LEAVES.defaultBlockState()
                                                                           .setValue(LeavesBlock.DISTANCE, 6));
        sphere = new SDFScale3D().setScale(1, 0.6F, 1).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> (float) noise.eval(
                vec.x() * 0.2,
                vec.y() * 0.2,
                vec.z() * 0.2
        ) * 3).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> random.nextFloat() * 3F - 1.5F).setSource(sphere);
        sphere = new SDFSubtraction().setSourceA(sphere)
                                     .setSourceB(new SDFTranslate().setTranslate(0, -radius, 0).setSource(sphere));
        MutableBlockPos mut = new MutableBlockPos();
        sphere.addPostProcess((info) -> {
            if (random.nextInt(5) == 0) {
                for (Direction dir : Direction.values()) {
                    BlockState state = info.getState(dir, 2);
                    if (state.isAir()) {
                        return info.getState();
                    }
                }
                info.setState(EndWoodBlocks.PYTHADENDRON.getBark().defaultBlockState());
                for (int x = -6; x < 7; x++) {
                    int ax = Math.abs(x);
                    mut.setX(x + info.getPos().getX());
                    for (int z = -6; z < 7; z++) {
                        int az = Math.abs(z);
                        mut.setZ(z + info.getPos().getZ());
                        for (int y = -6; y < 7; y++) {
                            int ay = Math.abs(y);
                            int d = ax + ay + az;
                            if (d < 7) {
                                mut.setY(y + info.getPos().getY());
                                BlockState state = info.getState(mut);
                                if (state.getBlock() instanceof LeavesBlock) {
                                    int distance = state.getValue(LeavesBlock.DISTANCE);
                                    if (d < distance) {
                                        info.setState(mut, state.setValue(LeavesBlock.DISTANCE, d));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return info.getState();
        });
        sphere.fillRecursiveIgnore(world, pos, zone.toBoundingBox(), IGNORE);
    }

    static {
        REPLACE = (state) -> {
			/*if (state.is(CommonBlockTags.END_STONES)) {
				return true;
			}*/
            if (state.getBlock() == EndWoodBlocks.PYTHADENDRON_LEAVES) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };

        IGNORE = EndWoodBlocks.PYTHADENDRON::isTreeLog;

        POST = (info) -> {
            if (EndWoodBlocks.PYTHADENDRON.isTreeLog(info.getStateUp()) && EndWoodBlocks.PYTHADENDRON.isTreeLog(info.getStateDown())) {
                return EndWoodBlocks.PYTHADENDRON.getLog().defaultBlockState();
            }
            return info.getState();
        };
    }
}
