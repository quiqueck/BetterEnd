package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.PosInfo;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.*;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.google.common.collect.Lists;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class DragonTreeFeature extends DefaultFeature {
    private static final Function<BlockState, Boolean> REPLACE;
    private static final Function<BlockState, Boolean> IGNORE;
    private static final Function<PosInfo, BlockState> POST;
    private static final List<Vector3f> BRANCH;
    private static final List<Vector3f> SIDE1;
    private static final List<Vector3f> SIDE2;
    private static final List<Vector3f> ROOT;

    /**
     * How far past its nominal radius the leaf ball's surface reaches in a way worth reserving room for.
     * <p>
     * Two {@code SDFDisplacement} passes grow the ball, and a displacement is <em>added</em> to the
     * distance, so the negative half of each range is what pushes the surface out. Their extremes sum to 3,
     * but only half of that is a shape:
     * <ul>
     *   <li>{@code noise * 1.5} is simplex noise sampled at 0.2 scale, so it is coherent over ~5 blocks. It
     *   grows real lobes, and a wall cutting one leaves exactly the flat chord this fitting exists to
     *   prevent. Worth reserving 1.5 blocks for.</li>
     *   <li>{@code random.nextFloat() * 3 - 1.5} is drawn afresh for every sampled position, so its extreme
     *   is isolated voxels rather than a surface. Reserving room for it would cost radius on every ball to
     *   protect speckle that is invisible when clipped.</li>
     * </ul>
     * Fitting to the coherent term alone keeps the cut gone and gives back part of the volume that fitting
     * to the full 3 gave up: measured over the same 4480 chunks, leaf blocks went 83028 -> 88632 while the
     * wall planes stayed at or below the interior-column control. The rest of the shortfall against an
     * unfitted 104453 is the intrinsic price of sizing a ball to its headroom at all, not bulge
     * bookkeeping, so there is little more to win here by lowering this further - and below 1.5 the
     * coherent lobe starts being cut again.
     */
    private static final float LEAF_BALL_BULGE = 1.5F;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(BlockTags.NYLIUM)) return false;

        // The cap/root branches below fan out to `radius` (up to ~17.5) in any direction via
        // SplineHelper.fillSpline/canGenerate, which otherwise read the world with no bound at all - past
        // the 3x3 chunks a feature may touch on an unlucky angle. Clip every spline read/write to the write
        // zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);

        float size = MHelper.randRange(10, 25, random);
        List<Vector3f> spline = SplineHelper.makeSpline(0, 0, 0, 0, size, 0, 6);
        SplineHelper.offsetParts(spline, random, 1F, 0, 1F);

        if (!SplineHelper.canGenerate(spline, pos, world, REPLACE, zone.toBoundingBox())) {
            return false;
        }
        BlocksHelper.setWithoutUpdate(world, pos, AIR);

        Vector3f last = SplineHelper.getPos(spline, 3.5F);
        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
        float radius = size * MHelper.randRange(0.5F, 0.7F, random);
        makeCap(world, pos.offset((int) last.x(), (int) last.y(), (int) last.z()), radius, random, noise, zone);

        last = spline.get(0);
        makeRoots(world, pos.offset((int) last.x(), (int) last.y(), (int) last.z()), radius, random, zone);

        radius = MHelper.randRange(1.2F, 2.3F, random);
        SDF function = SplineHelper.buildSDF(
                spline,
                radius,
                1.2F,
                (bpos) -> EndWoodBlocks.DRAGON_TREE.getBark().defaultBlockState()
        );

        function.setReplaceFunction(REPLACE);
        function.addPostProcess(POST);
        // size is up to 25, and the spline's flood-fill is otherwise bounded only by its own shape - clip it
        // to the write zone for the same reason as the leaf-ball flood in leavesBall() below. See
        // WriteZone.
        function.fillRecursiveIgnore(world, pos, zone.toBoundingBox(), IGNORE);

        EndTreeHelper.waterlogSubmerged(world, pos, 24);
        return true;
    }

    private void makeCap(
            WorldGenLevel world,
            BlockPos pos,
            float radius,
            RandomSource random,
            OpenSimplexNoise noise,
            WriteZone zone
    ) {
        int count = (int) radius;
        final BoundingBox bounds = zone.toBoundingBox();

        // Size the canopy first, then shrink the branch fan by whatever it lost.
        //
        // fitBallRadius clamps to the *smallest* headroom in any direction, so a wall close on one side
        // shrinks the ball on all four. Fitting the branches to the write zone independently - which is all
        // fillSpline's `fitTo` does - leaves the ones pointing away from that wall at full length, sticking
        // out of a canopy that is no longer wide enough to cover them. Scaling the fan by the same factor
        // keeps the cap in proportion: it just grows a smaller tree near a chunk edge instead of a
        // full-size skeleton in a shrunken wig.
        //
        // The fit is horizontal only (fitRadius reads x/z), so it can be taken at `pos` rather than at the
        // ball's centre - which is what lets the offset below depend on the scale without the two becoming
        // circular.
        final float nominalBallRadius = radius * 1.15F + 2;
        final float ballRadius = EndTreeHelper.fitBallRadius(
                zone,
                pos,
                nominalBallRadius,
                LEAF_BALL_BULGE,
                4F
        );
        final float capScale = ballRadius / nominalBallRadius;

        // The offset has to be scaled too, or the canopy hangs in the air above the shortened branches:
        // the branch tips land at offset*capScale while the ball would still be centred at offset. That
        // gap is (1-capScale)*offset, about a block for a mid-sized tree - visible, and exactly the
        // "floating canopy" this produced before the capScale factor was applied here as well.
        final int offset = (int) (BRANCH.get(BRANCH.size() - 1).y() * radius * capScale);
        final BlockPos ballCenter = pos.above(offset);

        for (int i = 0; i < count; i++) {
            float angle = (float) i / (float) count * MHelper.PI2;
            float scale = radius * MHelper.randRange(0.85F, 1.15F, random) * capScale;

            // The cap is a fan of `count` branches at even angles, reaching up to ~20 blocks. Handing the
            // zone in as `fitTo` shortens the ones that run out of room instead of cutting them off at the
            // wall; fitSegment only ever shortens, so the fan keeps its angles and loses length only on
            // the side that has none. `bounds` stays behind it as the safety net for the last rounding
            // step.
            List<Vector3f> branch = SplineHelper.copySpline(BRANCH);
            SplineHelper.rotateSpline(branch, angle);
            SplineHelper.scale(branch, scale);
            SplineHelper.fillSpline(branch, world, EndWoodBlocks.DRAGON_TREE.getBark().defaultBlockState(), pos, REPLACE, bounds, zone);

            branch = SplineHelper.copySpline(SIDE1);
            SplineHelper.rotateSpline(branch, angle);
            SplineHelper.scale(branch, scale);
            SplineHelper.fillSpline(branch, world, EndWoodBlocks.DRAGON_TREE.getBark().defaultBlockState(), pos, REPLACE, bounds, zone);

            branch = SplineHelper.copySpline(SIDE2);
            SplineHelper.rotateSpline(branch, angle);
            SplineHelper.scale(branch, scale);
            SplineHelper.fillSpline(branch, world, EndWoodBlocks.DRAGON_TREE.getBark().defaultBlockState(), pos, REPLACE, bounds, zone);
        }
        // Already fitted above; leavesBall refits, which is idempotent, so it stays the same radius.
        leavesBall(world, ballCenter, ballRadius, random, noise, zone);
    }

    private void makeRoots(
            WorldGenLevel world,
            BlockPos pos,
            float radius,
            RandomSource random,
            WriteZone zone
    ) {
        int count = (int) (radius * 1.5F);
        for (int i = 0; i < count; i++) {
            float angle = (float) i / (float) count * MHelper.PI2;
            float scale = radius * MHelper.randRange(0.85F, 1.15F, random);

            List<Vector3f> branch = SplineHelper.copySpline(ROOT);
            SplineHelper.rotateSpline(branch, angle);
            SplineHelper.scale(branch, scale);
            // Shorten first: that also makes the end-stone probe below a legal read, since the tip is now
            // inside the zone rather than potentially a chunk past it.
            branch = SplineHelper.fitSpline(branch, pos, zone, 1F);
            Vector3f last = branch.get(branch.size() - 1);
            if (world.getBlockState(pos.offset((int) last.x(), (int) last.y(), (int) last.z()))
                     .is(CommonBlockTags.END_STONES)) {
                SplineHelper.fillSpline(
                        branch,
                        world,
                        EndWoodBlocks.DRAGON_TREE.getBark().defaultBlockState(),
                        pos,
                        REPLACE,
                        zone.toBoundingBox()
                );
            }
        }
    }

    private void leavesBall(
            WorldGenLevel world,
            BlockPos pos,
            float radius,
            RandomSource random,
            OpenSimplexNoise noise,
            WriteZone zone
    ) {
        // The leaf ball is the part of a dragon tree that hits the wall - it reaches radius + 3 (the two
        // displacements below subtract as well as add, and a negative displacement grows the shape), which
        // is up to ~23 blocks, against 16 of guaranteed room. Size it to what its centre actually has:
        // the ball then stays a ball instead of losing a chord to fillRecursiveIgnore's writeBounds.
        radius = EndTreeHelper.fitBallRadius(zone, pos, radius, LEAF_BALL_BULGE, 4F);

        SDF sphere = new SDFSphere().setRadius(radius)
                                    .setBlock(EndWoodBlocks.DRAGON_TREE_LEAVES.defaultBlockState()
                                                                          .setValue(LeavesBlock.DISTANCE, 6));
        SDF sub = new SDFScale().setScale(5).setSource(sphere);
        sub = new SDFTranslate().setTranslate(0, -radius * 5, 0).setSource(sub);
        sphere = new SDFSubtraction().setSourceA(sphere).setSourceB(sub);
        sphere = new SDFScale3D().setScale(1, 0.5F, 1).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> (float) noise.eval(
                vec.x() * 0.2,
                vec.y() * 0.2,
                vec.z() * 0.2
        ) * 1.5F).setSource(sphere);
        sphere = new SDFDisplacement().setFunction((vec) -> random.nextFloat() * 3F - 1.5F).setSource(sphere);
        MutableBlockPos mut = new MutableBlockPos();
        sphere.addPostProcess((info) -> {
            if (random.nextInt(5) == 0) {
                for (Direction dir : Direction.values()) {
                    BlockState state = info.getState(dir, 2);
                    if (state.isAir()) {
                        return info.getState();
                    }
                }
                info.setState(EndWoodBlocks.DRAGON_TREE.getBark().defaultBlockState());
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
        // radius can reach ~22 blocks (size * 0.7 * 1.15 + 2, size up to 25) - clip the flood-fill to the
        // write zone; see WriteZone.
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
                    if (!EndWoodBlocks.DRAGON_TREE.isTreeLog(state) && !state.is(EndWoodBlocks.DRAGON_TREE_LEAVES)) {
                        place = false;
                        break;
                    }
                }
                if (place) {
                    BlocksHelper.setWithoutUpdate(world, p, EndWoodBlocks.DRAGON_TREE.getBark());
                }
            }
        }

        BlocksHelper.setWithoutUpdate(world, pos, EndWoodBlocks.DRAGON_TREE.getBark());
    }

    static {
        REPLACE = (state) -> {
			/*if (state.is(CommonBlockTags.END_STONES)) {
				return true;
			}*/
            if (state.getBlock() == EndWoodBlocks.DRAGON_TREE_LEAVES) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };

        IGNORE = EndWoodBlocks.DRAGON_TREE::isTreeLog;

        POST = (info) -> {
            if (EndWoodBlocks.DRAGON_TREE.isTreeLog(info.getStateUp()) && EndWoodBlocks.DRAGON_TREE.isTreeLog(info.getStateDown())) {
                return EndWoodBlocks.DRAGON_TREE.getLog().defaultBlockState();
            }
            return info.getState();
        };

        BRANCH = Lists.newArrayList(
                new Vector3f(0, 0, 0),
                new Vector3f(0.1F, 0.3F, 0),
                new Vector3f(0.4F, 0.6F, 0),
                new Vector3f(0.8F, 0.8F, 0),
                new Vector3f(1, 1, 0)
        );
        SIDE1 = Lists.newArrayList(new Vector3f(0.4F, 0.6F, 0), new Vector3f(0.8F, 0.8F, 0), new Vector3f(1, 1, 0));
        SIDE2 = SplineHelper.copySpline(SIDE1);

        Vector3f offset1 = new Vector3f(-0.4F, -0.6F, 0);
        Vector3f offset2 = new Vector3f(0.4F, 0.6F, 0);

        SplineHelper.offset(SIDE1, offset1);
        SplineHelper.offset(SIDE2, offset1);
        SplineHelper.rotateSpline(SIDE1, 0.5F);
        SplineHelper.rotateSpline(SIDE2, -0.5F);
        SplineHelper.offset(SIDE1, offset2);
        SplineHelper.offset(SIDE2, offset2);

        ROOT = Lists.newArrayList(
                new Vector3f(0F, 1F, 0),
                new Vector3f(0.1F, 0.7F, 0),
                new Vector3f(0.3F, 0.3F, 0),
                new Vector3f(0.7F, 0.05F, 0),
                new Vector3f(0.8F, -0.2F, 0)
        );
        SplineHelper.offset(ROOT, new Vector3f(0, -0.45F, 0));
    }
}
