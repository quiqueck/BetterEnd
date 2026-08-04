package org.betterx.betterend.world.features.trees;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.basis.FurBlock;
import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayDeque;
import java.util.Deque;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

/**
 * Shared water-awareness fix-up for the End tree features.
 * <p>
 * Trees decorate at {@code VEGETAL_DECORATION}, which runs AFTER the {@code LAKES} structure step has
 * already flooded shore lakes. Their SDF canopies and splines fill leaves through
 * {@link BlocksHelper#setWithoutUpdate} which overwrites whatever is there - including lake water - with a
 * dry block. That strands submerged leaves in dry pockets, so the water body reads as full of "air
 * bubbles" around the canopy.
 * <p>
 * {@link org.betterx.bclib.sdf.PosInfo} (the argument the per-feature SDF {@code POST} functions receive)
 * only exposes the states the SDF itself is placing, never the pre-existing world block, so the leaf's
 * original water cannot be seen at fill time. The fix is therefore a post-placement pass: after a tree is
 * placed, {@link #waterlogSubmerged} scans the tree's own footprint and re-floods every waterloggable
 * block that still touches genuine lake water, making it {@code WATERLOGGED=true}.
 */
public final class EndTreeHelper {
    // How far below/above the tree origin the flood pass looks. End lake bowls reach ~12 blocks below the
    // waterline (depth = radius * 0.5 * [0.8..1.2], radius 10-20) and shore water can sit a couple blocks
    // above the origin, so 16 down / 8 up covers the flooded band with margin. Tunable.
    private static final int SCAN_DOWN = 16;
    private static final int SCAN_UP = 8;
    // A thin slab around the origin, scanned first purely to decide whether this tree overlaps a lake at
    // all. The lake water body reaches up to the shore waterline (~origin height), so this reliably detects
    // it while costing a fraction of the full band - keeping the common "tree nowhere near water" case cheap.
    private static final int GATE_DOWN = 3;
    private static final int GATE_UP = 2;

    private EndTreeHelper() {
    }

    /**
     * A leaf ball's radius, shrunk to the room its centre actually has.
     * <p>
     * The write zone gives a feature 16 blocks of room in the worst case and 23 in the best, measured from
     * the trunk; a branch tip several blocks out with a ball on the end of it routinely asks for more than
     * that, and {@code fillRecursive}'s {@code writeBounds} then takes a flat chord out of the ball. Sizing
     * the ball to its headroom instead leaves a complete, slightly smaller ball - and because a ball's
     * centre always fits (it sits on a branch, and a branch that long could not have been drawn) this is
     * always well defined.
     *
     * @param bulge     how much further than {@code radius} the finished surface reaches. Every one of
     *                  these balls runs through {@code SDFDisplacement}, and that <em>adds</em> its
     *                  function to the distance, so a negative displacement grows the shape: a ball under
     *                  {@code noise * 3} and {@code randRange(-1.5, 1.5)} reaches {@code radius + 4.5}.
     *                  The bulge is additive on the surface, so it is added before fitting and subtracted
     *                  afterwards rather than scaled.
     * @param minRadius floor below which the ball is not shrunk any further. Nothing useful is left of a
     *                  leaf ball under a couple of blocks, and a branch that carries one has by
     *                  construction more headroom than that, so this is a guard rather than a case that
     *                  fires.
     */
    public static float fitBallRadius(
            WriteZone zone,
            BlockPos center,
            float radius,
            float bulge,
            float minRadius
    ) {
        if (zone.isUnbounded()) return radius;
        final float fitted = zone.fitRadius(center.getX(), center.getZ(), radius + bulge, 0F) - bulge;
        return Math.max(Math.min(radius, fitted), minRadius);
    }

    /**
     * Waterlog every submerged waterloggable block in a tree's footprint.
     *
     * @param world    the generating world (writes are masked to the current generation region)
     * @param origin   the tree's origin (trunk base / ground level)
     * @param radiusXZ half-extent of the tree's canopy horizontally; the scan box is
     *                 {@code origin +/- radiusXZ} on X/Z, clipped to the chunk write zone
     *                 <p>
     *                 Only reads and writes inside the {@code origin +/- radiusXZ} x {@code [-SCAN_DOWN,
     *                 SCAN_UP]} box - the same region the feature already wrote to - so it needs no
     *                 biome/heightmap lookups. That box is <em>not</em> automatically inside the chunks
     *                 worldgen lets a feature touch, though: callers pass radii of up to 32 while
     *                 {@code ChunkStatus.FEATURES} permits only the 3x3 chunks around the one being
     *                 decorated (see {@link WriteZone}). Unclipped, the two scans below were by far
     *                 the loudest source of "Detected unsafe terrain read during worldgen" warnings on
     *                 26.3 - {@link #hasWater} alone probes {@code (2r+1)^2 * 6} positions for every single
     *                 tree, and the underlying defect (deciding from a chunk that has no terrain in it yet)
     *                 is present on 26.1 too even without the warning. Clipping is behaviour-neutral: the
     *                 canopy blocks this pass exists to re-flood could never have been written outside the
     *                 zone in the first place.
     */
    /**
     * Deletes every outer-leaf / fur block in {@code placed} that ended up with nothing to cling to.
     * <p>
     * The SDF canopies write their outer leaves with {@link BlocksHelper#setWithoutUpdate}, which never
     * consults {@code canSurvive}, and two things can orphan one after the fact: the sphere cell that spawned
     * it may post-process to air, and - far more often - a second, overlapping tree may overwrite the cube
     * leaf it was clinging to. That last one is not a rare corner: the features' {@code REPLACE} predicate
     * accepts both leaves and anything plant-like, which is exactly what an outer leaf is, so overlapping
     * canopies freely swap the two. The result is an outer leaf hanging off another outer leaf, which fills
     * only the half of its block nearest its own support - a visible float.
     * <p>
     * Rather than trying to make the fill order collision-proof, this runs once at the end of a feature, when
     * the world finally holds the tree's real blocks, and asks each placed decoration the same question the
     * game asks on a neighbour update: {@link BlockState#canSurvive}. Removing one can orphan another that was
     * clinging to it, so it repeats until a pass changes nothing.
     *
     * Each written position is checked together with its six neighbours, because the block a fur loses is
     * usually not one of its own: when this tree overwrites a neighbouring tree's cube leaf with a fur of its
     * own, the fur left dangling belongs to that earlier tree and is merely adjacent to a position in
     * {@code placed}. Scanning the neighbours is what makes the pass work across overlapping canopies.
     *
     * @param world  the level being generated
     * @param placed positions the feature wrote an outer leaf / fur to; may contain duplicates, positions
     *               that were later overwritten by something else, and positions outside the write zone -
     *               anything that no longer holds a {@link FurBlock} is skipped
     */
    public static void pruneUnsupportedFur(WorldGenLevel world, Iterable<BlockPos> placed) {
        final MutableBlockPos probe = new MutableBlockPos();
        boolean removedAny = true;
        while (removedAny) {
            removedAny = false;
            for (BlockPos pos : placed) {
                removedAny |= dropIfUnsupported(world, pos);
                for (Direction dir : Direction.values()) {
                    removedAny |= dropIfUnsupported(world, probe.set(pos).move(dir));
                }
            }
        }
    }

    private static boolean dropIfUnsupported(WorldGenLevel world, BlockPos pos) {
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof FurBlock) || state.canSurvive(world, pos)) {
            return false;
        }
        BlocksHelper.setWithoutUpdate(world, pos, Blocks.AIR.defaultBlockState());
        return true;
    }

    public static void waterlogSubmerged(WorldGenLevel world, BlockPos origin, int radiusXZ) {
        final WriteZone zone = WriteZone.of(world);
        if (zone.isDisjoint(
                origin.getX() - radiusXZ, origin.getZ() - radiusXZ,
                origin.getX() + radiusXZ, origin.getZ() + radiusXZ
        )) {
            return;
        }
        final int minX = zone.clampX(origin.getX() - radiusXZ);
        final int maxX = zone.clampX(origin.getX() + radiusXZ);
        final int minZ = zone.clampZ(origin.getZ() - radiusXZ);
        final int maxZ = zone.clampZ(origin.getZ() + radiusXZ);
        final MutableBlockPos p = new MutableBlockPos();

        // Cheap gate: bail unless this tree actually overlaps water. Most trees are nowhere near a lake, so
        // this thin-slab probe (and an early return) is the common path.
        if (!hasWater(world, p, minX, maxX, minZ, maxZ, origin.getY() - GATE_DOWN, origin.getY() + GATE_UP)) {
            return;
        }

        final int minY = origin.getY() - SCAN_DOWN;
        final int maxY = origin.getY() + SCAN_UP;

        // Pass 1: collect every genuine lake-water block in the box as a flood seed, and remember the highest
        // one - that Y is the local water surface. The flood is not allowed above it, so it fills the submerged
        // region (including the dry interior leaves the canopy displaced) without climbing the canopy into air.
        final Deque<BlockPos> queue = new ArrayDeque<>();
        final LongSet visited = new LongOpenHashSet();
        int waterSurfaceY = Integer.MIN_VALUE;
        for (int y = minY; y <= maxY; y++) {
            p.setY(y);
            for (int x = minX; x <= maxX; x++) {
                p.setX(x);
                for (int z = minZ; z <= maxZ; z++) {
                    p.setZ(z);
                    if (isLakeWater(world.getBlockState(p))) {
                        final long key = p.asLong();
                        if (visited.add(key)) {
                            queue.add(p.immutable());
                        }
                        if (y > waterSurfaceY) {
                            waterSurfaceY = y;
                        }
                    }
                }
            }
        }
        if (queue.isEmpty()) {
            return;
        }

        // Pass 2: BFS from the water seeds through face-neighbours, spreading into genuine water and into
        // not-yet-waterlogged waterloggable blocks (main leaves AND FurBlock outer leaves, both of which now
        // carry WATERLOGGED). Every waterloggable block reached is flooded, preserving its other properties
        // (FurBlock FACING, Leaves DISTANCE/PERSISTENT). Never crosses above the water surface and never leaves
        // the box, so it stays worldgen-safe (only the region the feature already wrote to).
        final MutableBlockPos n = new MutableBlockPos();
        while (!queue.isEmpty()) {
            final BlockPos cur = queue.poll();
            for (Direction dir : Direction.values()) {
                n.set(cur).move(dir);
                if (n.getX() < minX || n.getX() > maxX
                        || n.getY() < minY || n.getY() > maxY
                        || n.getZ() < minZ || n.getZ() > maxZ
                        || n.getY() > waterSurfaceY) {
                    continue;
                }
                final long key = n.asLong();
                if (visited.contains(key)) {
                    continue;
                }
                final BlockState state = world.getBlockState(n);
                final boolean genuineWater = isLakeWater(state);
                final boolean floodable = state.hasProperty(BlockStateProperties.WATERLOGGED)
                        && !state.getValue(BlockStateProperties.WATERLOGGED);
                if (!genuineWater && !floodable) {
                    continue;
                }
                visited.add(key);
                if (floodable) {
                    BlocksHelper.setWithoutUpdate(
                            world,
                            n,
                            state.setValue(BlockStateProperties.WATERLOGGED, true)
                    );
                }
                queue.add(n.immutable());
            }
        }
    }

    private static boolean hasWater(
            WorldGenLevel world,
            MutableBlockPos p,
            int minX,
            int maxX,
            int minZ,
            int maxZ,
            int minY,
            int maxY
    ) {
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (world.getFluidState(p.set(x, y, z)).is(FluidTags.WATER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * A real water block (the lake fill), as opposed to an already-waterlogged block. A plain water
     * {@code LiquidBlock} carries a water {@link net.minecraft.world.level.material.FluidState} but does not
     * have the {@code WATERLOGGED} property; a waterlogged leaf does. Excluding the latter prevents the
     * flood from propagating through leaves we just waterlogged.
     */
    private static boolean isLakeWater(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER)
                && !state.hasProperty(BlockStateProperties.WATERLOGGED);
    }
}
