package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.util.WorldgenDebug;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

/**
 * Carves a shallow rim pond into the top of a small void-ring island and lets it spill one or
 * two REAL, self-flowing waterfalls over the island edge into the void. Used only by
 * {@link org.betterx.betterend.world.biome.air.WaterfallPondsBiome}.
 * <p>
 * Chunk-safety: the feature is placed once per chunk with NO {@code squarePlacement} modifier,
 * so {@link FeaturePlaceContext#origin()} is the chunk's SW corner. All geometry is centred on
 * the chunk centre ({@code origin + (8, 0, 8)}) and the pond radius is capped at {@value #MAX_RADIUS}
 * (+ noise jitter {@value #JITTER}), keeping the whole bowl inside the origin chunk. The spill notch
 * is a short radial channel clamped to the chunk bounds, so no block is written outside the origin
 * chunk.
 * <p>
 * Real waterfalls (second refinement): instead of stamping a static wall-hugging water column
 * (which read as "water blocks stuck to the island side"), the bowl is left a closed pool of WATER
 * <b>source</b> blocks and a single notch is carved through the rim + shallow moss ring, down to one
 * block below the pond surface, giving those sources a genuine outlet. Every pond water block is
 * handed a fluid tick via {@link net.minecraft.world.level.ScheduledTickAccess#scheduleTick} (the
 * {@code SulphuricLakeFeature} / {@code BlockFixer} precedent {@code world.scheduleTick(pos,
 * Fluids.WATER, 0)}) so, once the chunk is ticked after load, the pool re-evaluates, finds the notch
 * and streams out and down the island side on its own. Because the bowl is full of source blocks the
 * spill is self-replenishing (an infinite waterfall) while the pool stays full. Blocks are also
 * marked via {@link ChunkAccess#markPosForPostProcessing(BlockPos)} so the fluid state settles.
 */
public class PondWithWaterfallFeature extends DefaultFeature {
    private static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();
    private static final BlockState END_MOSS = EndTerrainBlocks.END_MOSS.defaultBlockState();
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(6114);

    // The pond is sized to the island: it fills the flat plateau MINUS a DRY_RIM ring of solid ground
    // so shore plants and the dragon-helix tree always have somewhere to root. MAX_POND caps the reach
    // (a big island's pond spills a few blocks into neighbour chunks - safe, since the island exists
    // before this LAKES-step feature runs and every column is checked for island terrain first).
    private static final int MIN_POND = 3;
    private static final int MAX_POND = 10;
    private static final int DRY_RIM = 3;
    private static final int MAX_PLATEAU_PROBE = 16;
    // A column counts as "on the flat plateau" when its WG surface is within this of the centre top.
    private static final int PLATEAU_FLAT_TOL = 2;
    private static final double JITTER = 1.5;
    private static final int MAX_FLATNESS_VARIANCE = 3;
    private static final int MIN_TERRAIN_ABOVE_FLOOR = 5;
    // Bowl depth range (deeper, wider dips) and the minimum count of solid island blocks that must
    // remain under the deepest (centre) bowl floor so we never punch through a thin island.
    private static final int MIN_DEPTH = 4;
    private static final int MAX_DEPTH = 7;
    private static final int FALLBACK_DEPTH = 2;
    private static final int MIN_SUPPORT_BELOW_BOWL = 2;
    private static final int MAX_THICKNESS_PROBE = 24;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        final WorldGenLevel world = ctx.level();
        final RandomSource random = ctx.random();
        final BlockPos origin = ctx.origin();

        // Centre on the chunk (origin + 8). The pond is island-aware and cross-chunk safe, so the old
        // chunk-bound clamps (minX/maxX/minZ/maxZ) are gone.
        final int centerX = origin.getX() + 8;
        final int centerZ = origin.getZ() + 8;

        // Island top at the placement column (WORLD_SURFACE_WG heightmap, pre-vegetation).
        final int topY = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centerX, centerZ) - 1;
        if (topY <= world.getMinY() + MIN_TERRAIN_ABOVE_FLOOR) {
            WorldgenDebug.log(
                    "PondWithWaterfall: REJECTED at (%d,%d) - no terrain (top y=%d, void column)",
                    centerX, centerZ, topY
            );
            return false;
        }

        // Require a reasonably flat area: sample 4 offsets +-4 blocks around the centre.
        int hMin = topY;
        int hMax = topY;
        final int[][] offsets = {{4, 0}, {-4, 0}, {0, 4}, {0, -4}};
        for (int[] off : offsets) {
            final int h = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centerX + off[0], centerZ + off[1]) - 1;
            hMin = Math.min(hMin, h);
            hMax = Math.max(hMax, h);
        }
        if (hMax - hMin > MAX_FLATNESS_VARIANCE) {
            WorldgenDebug.log(
                    "PondWithWaterfall: REJECTED at (%d,%d) - not flat enough (h %d..%d)",
                    centerX, centerZ, hMin, hMax
            );
            return false;
        }
        // Size the pond to the island: measure the flat plateau and fill it MINUS a DRY_RIM ring so the
        // shore plants and the tree keep solid ground. Islands too small for a pond + rim get no pond.
        final int plateauR = probeFlatPlateau(world, centerX, centerZ, topY);
        final int radius = Math.min(MAX_POND, plateauR - DRY_RIM);
        if (radius < MIN_POND) {
            WorldgenDebug.log(
                    "PondWithWaterfall: island plateau r=%d too small for a pond + rim at (%d,%d)",
                    plateauR, centerX, centerZ
            );
            return false;
        }
        WorldgenDebug.log(
                "PondWithWaterfall: carving pond at (%d,%d) top y=%d, plateau r=%d, pond r=%d",
                centerX, centerZ, topY, plateauR, radius
        );

        final int drawnDepth = MHelper.randRange(MIN_DEPTH, MAX_DEPTH, random);
        final int waterLevel = topY - 1;

        // Deeper dips (3..5) but clamped so at least MIN_SUPPORT_BELOW_BOWL solid island blocks remain
        // beneath the deepest (centre) bowl floor. Probe the island thickness straight down from the
        // centre; the centre floor sits at topY-depth and we always lay one END_STONE at topY-depth-1,
        // so the block at topY-depth-2 must still be natural solid island -> need thickness >= depth+3,
        // i.e. depth <= thickness-3. Reduce toward MIN_DEPTH (3); if even 3 does not fit, fall back to 2.
        final int solidThickness = probeSolidThickness(world, centerX, topY, centerZ);
        final int maxDepthAllowed = solidThickness - (MIN_SUPPORT_BELOW_BOWL + 1);
        final int depth;
        if (maxDepthAllowed >= drawnDepth) {
            depth = drawnDepth;
        } else if (maxDepthAllowed >= MIN_DEPTH) {
            depth = maxDepthAllowed;
        } else {
            depth = FALLBACK_DEPTH;
        }
        WorldgenDebug.log(
                "PondWithWaterfall: depth drawn=%d -> %d (island thickness=%d) at (%d,%d)",
                drawnDepth, depth, solidThickness, centerX, centerZ
        );

        final MutableBlockPos pos = new MutableBlockPos();

        // Carve the bowl: deeper at the centre, tapering to the rim. NO chunk clamp - the pond may reach
        // a few blocks into neighbour chunks on a large island (safe: the neighbour's island exists by
        // now, and each column is verified below before anything is written).
        for (int dx = -radius; dx <= radius; dx++) {
            final int x = centerX + dx;
            for (int dz = -radius; dz <= radius; dz++) {
                final int z = centerZ + dz;

                final double dist = Math.sqrt(dx * dx + dz * dz);
                final double jitteredR = radius + NOISE.eval(x * 0.2, z * 0.2) * JITTER;
                if (dist > jitteredR) continue;

                // Island-aware: only carve where THIS column's surface sits on the flat island top.
                // Off-island / sloped columns are skipped, so the pond never floats water over the void
                // and always stops short of the island edge.
                final int colTop = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                if (Math.abs(colTop - topY) > PLATEAU_FLAT_TOL) continue;

                final int localDepth = (int) Math.round(depth * (jitteredR - dist) / jitteredR);
                if (localDepth < 1) continue; // leave the outermost ring as an intact END_MOSS bank

                final int floorY = topY - localDepth;

                // Line the bowl floor: END_MOSS lip near the rim, end stone underneath.
                pos.set(x, floorY, z);
                BlocksHelper.setWithoutUpdate(world, pos, localDepth == 1 ? END_MOSS : END_STONE);
                pos.set(x, floorY - 1, z);
                BlocksHelper.setWithoutUpdate(world, pos, END_STONE);

                // Fill with water up to rim-1. These are SOURCE blocks; each gets a fluid tick so the
                // pool re-flows through the notch after load (the pool stays full, so the spill is
                // infinite/self-replenishing). markPosForPostProcessing on the column's OWN chunk.
                for (int y = floorY + 1; y <= waterLevel; y++) {
                    final BlockPos wpos = new BlockPos(x, y, z);
                    BlocksHelper.setWithoutUpdate(world, wpos, WATER);
                    world.getChunk(x >> 4, z >> 4).markPosForPostProcessing(wpos);
                    world.scheduleTick(wpos, Fluids.WATER, 0);
                }
                // Clear the lip above the water so the pond is open to the sky.
                for (int y = waterLevel + 1; y <= topY; y++) {
                    pos.set(x, y, z);
                    BlocksHelper.setWithoutUpdate(world, pos, AIR);
                }
            }
        }

        // 1-2 spill-over waterfalls in distinct rim directions.
        final Direction[] dirs = shuffledHorizontals(random);
        final int waterfalls = 1 + random.nextInt(2);
        int made = 0;
        for (int i = 0; i < dirs.length && made < waterfalls; i++) {
            if (spillWaterfall(world, dirs[i], centerX, centerZ, radius, waterLevel, topY)) {
                made++;
            }
        }

        return true;
    }

    /**
     * Measures the radius of the island's flat top around {@code (cx, cz)}: expands ring by ring and
     * returns the last radius at which all eight compass directions still sit within
     * {@value #PLATEAU_FLAT_TOL} of the centre surface {@code topY}. Capped at {@value #MAX_PLATEAU_PROBE}.
     * The pond is then sized to this minus {@link #DRY_RIM}, so it fills the flat top yet always leaves a
     * ring of solid ground for shore plants and the tree.
     */
    private int probeFlatPlateau(WorldGenLevel world, int cx, int cz, int topY) {
        final int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int r = 1; r <= MAX_PLATEAU_PROBE; r++) {
            for (int[] d : dirs) {
                final int x = cx + d[0] * r;
                final int z = cz + d[1] * r;
                final int h = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                if (Math.abs(h - topY) > PLATEAU_FLAT_TOL) {
                    return r - 1;
                }
            }
        }
        return MAX_PLATEAU_PROBE;
    }

    /**
     * Probes the solid island thickness straight down from {@code (x, topY, z)}: counts consecutive
     * non-air, non-fluid blocks from {@code topY} downward (inclusive), capped at
     * {@value #MAX_THICKNESS_PROBE}. The pond feature runs at RAW/LAKES before water and vegetation,
     * so the island body here is plain END_STONE.
     */
    private int probeSolidThickness(WorldGenLevel world, int x, int topY, int z) {
        final MutableBlockPos p = new MutableBlockPos();
        int count = 0;
        for (int k = 0; k < MAX_THICKNESS_PROBE; k++) {
            final int y = topY - k;
            if (y <= world.getMinY()) break;
            p.set(x, y, z);
            final BlockState s = world.getBlockState(p);
            if (s.isAir() || !s.getFluidState().isEmpty()) break;
            count++;
        }
        return count;
    }

    /**
     * Carves a 1-wide radial spill notch in {@code dir}: starting just inside the water body and
     * running outward through the shallow moss ring and the outer bank, it removes every SOLID block
     * from one block below the pond surface ({@code waterLevel-1}) up to just above the rim
     * ({@code topY+1}), leaving the pond's WATER source blocks untouched. This gives the pool a
     * genuine outlet one block below its surface.
     * <p>
     * No water is placed here and no descending column is stamped: the ticked pool (see the bowl
     * carve) flows out through this notch on its own after load. Because the cut band is a fixed
     * absolute Y range, cutting naturally stops once the island surface drops below the water line -
     * i.e. exactly at the plateau edge - so the notch is only a short breach across the flat top and
     * the water cascades off the plateau edge as a real waterfall. No chunk clamp: on a large island the
     * outlet may cross a chunk border, which is safe within the feature's generation region.
     */
    private boolean spillWaterfall(
            WorldGenLevel world, Direction dir,
            int centerX, int centerZ, int radius, int waterLevel, int topY
    ) {
        final MutableBlockPos pos = new MutableBlockPos();
        final int notchBottom = waterLevel - 1; // one block below the pond surface
        final int notchTop = topY + 1;
        final int rStart = Math.max(1, radius - 2);
        // Reach through the pond edge and the DRY_RIM ring to the plateau edge (+ a little) so the
        // outlet actually breaches the dry bank; no chunk clamp - the outlet may cross a chunk border.
        final int rEnd = radius + DRY_RIM + 3;

        boolean carvedAny = false;
        for (int r = rStart; r <= rEnd; r++) {
            final int x = centerX + dir.getStepX() * r;
            final int z = centerZ + dir.getStepZ() * r;

            for (int y = notchBottom; y <= notchTop; y++) {
                pos.set(x, y, z);
                final BlockState s = world.getBlockState(pos);
                // Only cut solid rim/moss - leave pond water sources (and open air) alone so the pool
                // keeps its volume and simply gains a drain.
                if (s.isAir() || !s.getFluidState().isEmpty()) continue;
                BlocksHelper.setWithoutUpdate(world, pos, AIR);
                carvedAny = true;
            }
        }
        return carvedAny;
    }

    private static Direction[] shuffledHorizontals(RandomSource random) {
        final Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (int i = dirs.length - 1; i > 0; i--) {
            final int j = random.nextInt(i + 1);
            final Direction tmp = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = tmp;
        }
        return dirs;
    }
}
