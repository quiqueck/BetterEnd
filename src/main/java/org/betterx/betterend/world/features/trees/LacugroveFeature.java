package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.PosInfo;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFSubtraction;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class LacugroveFeature extends DefaultFeature {
    private static final Function<BlockState, Boolean> REPLACE;
    private static final Function<BlockState, Boolean> IGNORE;
    private static final Function<PosInfo, BlockState> POST;

    /**
     * How far past its nominal radius the leaf ball's surface reaches: {@code noise * 3} plus
     * {@code nextFloat() * 3 - 1.5}, both applied through {@code SDFDisplacement}, which adds to the
     * distance - so the negative half of each range grows the shape.
     */
    private static final float LEAF_BALL_BULGE = 4.5F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(BlockTags.NYLIUM)) return false;

        // size reaches 25, and the trunk/leaf-ball flood-fills and canGenerate probe are otherwise bounded
        // only by their own shape - past the 3x3 chunks a feature may touch. Clip every one to the write
        // zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);

        float size = MHelper.randRange(15, 25, random);
        List<Vector3f> spline = SplineHelper.makeSpline(0, 0, 0, 0, size, 0, 6);
        SplineHelper.offsetParts(spline, random, 1F, 0, 1F);

        if (!SplineHelper.canGenerate(spline, pos, world, REPLACE, zone.toBoundingBox())) {
            return false;
        }

        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());

        float radius = MHelper.randRange(6F, 8F, random);
        radius *= (size - 15F) / 20F + 1F;
        Vector3f center = spline.get(4);
        leavesBall(
                world,
                pos.offset((int) center.x(), (int) center.y(), (int) center.z()),
                radius,
                random,
                noise,
                zone
        );

        radius = MHelper.randRange(1.2F, 1.8F, random);
        SDF function = SplineHelper.buildSDF(
                spline,
                radius,
                0.7F,
                (bpos) -> EndWoodBlocks.LACUGROVE.getBark().defaultBlockState()
        );

        function.setReplaceFunction(REPLACE);
        function.addPostProcess(POST);
        function.fillRecursive(world, pos, zone.toBoundingBox());

        spline = spline.subList(4, 6);
        SplineHelper.fillSpline(
                spline,
                world,
                EndWoodBlocks.LACUGROVE.getBark().defaultBlockState(),
                pos,
                REPLACE,
                zone.toBoundingBox()
        );

        MutableBlockPos mut = new MutableBlockPos();
        int offset = random.nextInt(2);
        for (int i = 0; i < 100; i++) {
            double px = pos.getX() + MHelper.randRange(-5, 5, random);
            double pz = pos.getZ() + MHelper.randRange(-5, 5, random);
            mut.setX(MHelper.floor(px + 0.5));
            mut.setZ(MHelper.floor(pz + 0.5));
            if (((mut.getX() + mut.getZ() + offset) & 1) == 0) {
                double distance = 3.5 - MHelper.length(px - pos.getX(), pz - pos.getZ()) * 0.5;
                if (distance > 0) {
                    int minY = MHelper.floor(pos.getY() - distance * 0.5);
                    int maxY = MHelper.floor(pos.getY() + distance + random.nextDouble());
                    boolean generate = false;
                    for (int y = minY; y < maxY; y++) {
                        mut.setY(y);
                        if (world.getBlockState(mut).is(CommonBlockTags.END_STONES)) {
                            generate = true;
                            break;
                        }
                    }
                    if (generate) {
                        int top = maxY - 1;
                        for (int y = top; y >= minY; y--) {
                            mut.setY(y);
                            BlockState state = world.getBlockState(mut);
                            if (BlocksHelper.replaceableOrPlant(state) || state.is(CommonBlockTags.END_STONES)) {
                                BlocksHelper.setWithoutUpdate(
                                        world,
                                        mut,
                                        y == top ? EndWoodBlocks.LACUGROVE.getBark() : EndWoodBlocks.LACUGROVE.getLog()
                                );
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        EndTreeHelper.waterlogSubmerged(world, pos, 16);
        return true;
    }

    private void leavesBall(
            WorldGenLevel world,
            BlockPos pos,
            float radius,
            RandomSource random,
            OpenSimplexNoise noise,
            WriteZone zone
    ) {
        // Size the ball to the room its centre has rather than letting the write bounds take a chord out
        // of it; see EndTreeHelper.fitBallRadius. Rare here - about one lacugrove in seventy, and shallow
        // - but it is the same one line, and lacugrove runs a leaf-decay pass afterwards that a clipped
        // canopy feeds orphaned leaves into.
        radius = EndTreeHelper.fitBallRadius(zone, pos, radius, LEAF_BALL_BULGE, 2F);

        SDF sphere = new SDFSphere().setRadius(radius)
                                    .setBlock(EndWoodBlocks.LACUGROVE_LEAVES.defaultBlockState()
                                                                        .setValue(LeavesBlock.DISTANCE, 6));
        sphere = new SDFDisplacement().setFunction((vec) -> (float) noise.eval(
                vec.x() * 0.2,
                vec.y() * 0.2,
                vec.z() * 0.2
        ) * 3).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> random.nextFloat() * 3F - 1.5F).setSource(sphere);
        sphere = new SDFSubtraction().setSourceA(sphere)
                                     .setSourceB(new SDFTranslate().setTranslate(0, -radius - 2, 0).setSource(sphere));
        MutableBlockPos mut = new MutableBlockPos();
        sphere.addPostProcess((info) -> {
            if (random.nextInt(5) == 0) {
                for (Direction dir : Direction.values()) {
                    BlockState state = info.getState(dir, 2);
                    if (state.isAir()) {
                        return info.getState();
                    }
                }
                info.setState(EndWoodBlocks.LACUGROVE.getBark().defaultBlockState());
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

        if (radius > 5) {
            int count = (int) (radius * 2.5F);
            for (int i = 0; i < count; i++) {
                BlockPos p = pos.offset(
                        (int) (random.nextGaussian() * 1),
                        (int) (random.nextGaussian() * 1),
                        (int) (random.nextGaussian() * 1)
                );
                boolean place = true;
                for (Direction d : Direction.values()) {
                    BlockState state = world.getBlockState(p.relative(d));
                    if (!EndWoodBlocks.LACUGROVE.isTreeLog(state) && !state.is(EndWoodBlocks.LACUGROVE_LEAVES)) {
                        place = false;
                        break;
                    }
                }
                if (place) {
                    BlocksHelper.setWithoutUpdate(world, p, EndWoodBlocks.LACUGROVE.getBark());
                }
            }
        }

        BlocksHelper.setWithoutUpdate(world, pos, EndWoodBlocks.LACUGROVE.getBark());
    }

    static {
        REPLACE = (state) -> {
			/*if (state.is(CommonBlockTags.END_STONES)) {
				return true;
			}*/
            if (EndWoodBlocks.LACUGROVE.isTreeLog(state)) {
                return true;
            }
            if (state.getBlock() == EndWoodBlocks.LACUGROVE_LEAVES) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };

        IGNORE = EndWoodBlocks.LACUGROVE::isTreeLog;

        POST = (info) -> {
            if (EndWoodBlocks.LACUGROVE.isTreeLog(info.getStateUp()) && EndWoodBlocks.LACUGROVE.isTreeLog(info.getStateDown())) {
                return EndWoodBlocks.LACUGROVE.getLog().defaultBlockState();
            }
            return info.getState();
        };
    }
}
