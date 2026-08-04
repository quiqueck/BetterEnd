package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.*;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.blocks.basis.FurBlock;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;

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

import com.google.common.collect.Lists;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class TenaneaFeature extends DefaultFeature {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Function<BlockState, Boolean> REPLACE;
    private static final Function<BlockState, Boolean> IGNORE;
    private static final List<Vector3f> SPLINE;

    /**
     * How far past its nominal radius a leaf ball's surface reaches: {@code noise * 2} plus
     * {@code randRange(-1.5, 1.5)}, both applied through {@code SDFDisplacement}, which adds to the
     * distance - so the negative half of each range grows the shape.
     */
    private static final float LEAF_BALL_BULGE = 3.5F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(BlockTags.NYLIUM)) return false;

        // Branch splines are scaled by up to `size * 1.5` and rotated to any angle, so fillSpline and the
        // leaf-ball flood-fill can read/write past the 3x3 chunks a feature may touch. Clip both to the
        // write zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);
        final List<BlockPos> outerLeaves = Lists.newArrayList();

        float size = MHelper.randRange(7, 10, random);
        int count = (int) (size * 0.45F);
        float var = MHelper.PI2 / (float) (count * 3);
        float start = MHelper.randRange(0, MHelper.PI2, random);
        for (int i = 0; i < count; i++) {
            float angle = (float) i / (float) count * MHelper.PI2 + MHelper.randRange(0, var, random) + start;
            List<Vector3f> spline = SplineHelper.copySpline(SPLINE);
            SplineHelper.rotateSpline(spline, angle);
            SplineHelper.scale(spline, size + MHelper.randRange(0, size * 0.5F, random));
            SplineHelper.offsetParts(spline, random, 1F, 0, 1F);
            SplineHelper.fillSpline(
                    spline,
                    world,
                    EndWoodBlocks.TENANEA.getBark().defaultBlockState(),
                    pos,
                    REPLACE,
                    zone.toBoundingBox()
            );
            Vector3f last = spline.get(spline.size() - 1);
            float leavesRadius = (size * 0.3F + MHelper.randRange(0.8F, 1.5F, random)) * 1.4F;
            OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
            leavesBall(
                    world,
                    pos.offset((int) last.x(), (int) last.y(), (int) last.z()),
                    leavesRadius,
                    random,
                    noise,
                    zone,
                    outerLeaves
            );
        }

        // Drop outer leaves the canopy left clinging to nothing - see EndTreeHelper.pruneUnsupportedFur.
        // Run once for the whole tree, after every ball: a later ball routinely overwrites an earlier one's
        // leaves.
        EndTreeHelper.pruneUnsupportedFur(world, outerLeaves);

        EndTreeHelper.waterlogSubmerged(world, pos, 16);
        return true;
    }

    private void leavesBall(
            WorldGenLevel world,
            BlockPos pos,
            float radius,
            RandomSource random,
            OpenSimplexNoise noise,
            WriteZone zone,
            List<BlockPos> outerLeaves
    ) {
        // Size the ball to the room its centre has rather than letting the write bounds take a chord out
        // of it; see EndTreeHelper.fitBallRadius. Barely visible here - about one tenanea in seventy - but
        // it is the same one line.
        radius = EndTreeHelper.fitBallRadius(zone, pos, radius, LEAF_BALL_BULGE, 2F);

        SDF sphere = new SDFSphere().setRadius(radius)
                                    .setBlock(EndWoodBlocks.TENANEA_LEAVES.defaultBlockState()
                                                                      .setValue(LeavesBlock.DISTANCE, 6));
        SDF sub = new SDFScale().setScale(5).setSource(sphere);
        sub = new SDFTranslate().setTranslate(0, -radius * 5, 0).setSource(sub);
        sphere = new SDFSubtraction().setSourceA(sphere).setSourceB(sub);
        sphere = new SDFScale3D().setScale(1, 0.75F, 1).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> (float) noise.eval(
                vec.x() * 0.2,
                vec.y() * 0.2,
                vec.z() * 0.2
        ) * 2F).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> MHelper.randRange(-1.5F, 1.5F, random)).setSource(sphere);

        MutableBlockPos mut = new MutableBlockPos();
        for (Direction d1 : BlocksHelper.HORIZONTAL) {
            BlockPos p = mut.set(pos).move(Direction.UP).move(d1).immutable();
            BlocksHelper.setWithoutUpdate(world, p, EndWoodBlocks.TENANEA.getBark().defaultBlockState());
            for (Direction d2 : BlocksHelper.HORIZONTAL) {
                mut.set(p).move(Direction.UP).move(d2);
                BlocksHelper.setWithoutUpdate(world, p, EndWoodBlocks.TENANEA.getBark().defaultBlockState());
            }
        }

        BlockState top = EndWoodBlocks.TENANEA_FLOWERS.defaultBlockState()
                                                  .setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP);
        BlockState middle = EndWoodBlocks.TENANEA_FLOWERS.defaultBlockState()
                                                     .setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE);
        BlockState bottom = EndWoodBlocks.TENANEA_FLOWERS.defaultBlockState()
                                                     .setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM);
        BlockState outer = EndWoodBlocks.TENANEA_OUTER_LEAVES.defaultBlockState();

        List<BlockPos> support = Lists.newArrayList();
        sphere.addPostProcess((info) -> {
            if (random.nextInt(6) == 0 && info.getStateDown().isAir()) {
                BlockPos d = info.getPos().below();
                support.add(d);
            }
            if (random.nextInt(5) == 0) {
                for (Direction dir : Direction.values()) {
                    BlockState state = info.getState(dir, 2);
                    if (state.isAir()) {
                        return info.getState();
                    }
                }
                info.setState(EndWoodBlocks.TENANEA.getBark().defaultBlockState());
            }

            MHelper.shuffle(DIRECTIONS, random);
            for (Direction d : DIRECTIONS) {
                if (info.getState(d).isAir()) {
                    final BlockPos outerPos = info.getPos().relative(d);
                    info.setBlockPos(outerPos, outer.setValue(FurBlock.FACING, d));
                    outerLeaves.add(outerPos);
                }
            }

            if (EndWoodBlocks.TENANEA.isTreeLog(info.getState())) {
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
        BlocksHelper.setWithoutUpdate(world, pos, EndWoodBlocks.TENANEA.getBark());

        support.forEach((bpos) -> {
            BlockState state = world.getBlockState(bpos);
            if (state.isAir() || state.is(EndWoodBlocks.TENANEA_OUTER_LEAVES)) {
                int count = MHelper.randRange(3, 8, random);
                mut.set(bpos);
                if (world.getBlockState(mut.above()).is(EndWoodBlocks.TENANEA_LEAVES)) {
                    BlocksHelper.setWithoutUpdate(world, mut, top);
                    for (int i = 1; i < count; i++) {
                        mut.setY(mut.getY() - 1);
                        if (world.isEmptyBlock(mut.below())) {
                            BlocksHelper.setWithoutUpdate(world, mut, middle);
                        } else {
                            break;
                        }
                    }
                    BlocksHelper.setWithoutUpdate(world, mut, bottom);
                }
            }
        });
    }

    static {
        REPLACE = (state) -> {
			/*if (state.is(CommonBlockTags.END_STONES)) {
				return true;
			}*/
            if (state.getBlock() == EndWoodBlocks.TENANEA_LEAVES) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };

        IGNORE = EndWoodBlocks.TENANEA::isTreeLog;

        SPLINE = Lists.newArrayList(
                new Vector3f(0.00F, 0.00F, 0.00F),
                new Vector3f(0.10F, 0.35F, 0.00F),
                new Vector3f(0.20F, 0.50F, 0.00F),
                new Vector3f(0.30F, 0.55F, 0.00F),
                new Vector3f(0.42F, 0.70F, 0.00F),
                new Vector3f(0.50F, 1.00F, 0.00F)
        );
    }
}
