package org.betterx.betterend.world.features.terrain.caves;

import org.betterx.betterend.world.biome.cave.EndCaveBiome;
import org.betterx.bclib.util.BlocksHelper;
import de.ambertation.wover.biome.api.BiomeManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Paints a cave biome's own surface materials onto an already-carved cave face. This is the decoration-time
 * replacement for the {@code CaveSurfaceCoater} that 26.3's carver rewrite deleted: carvers can no longer
 * write blocks at all (they only mark chunk-local positions via {@code CarverOutput#carve}), so the coat has
 * to run afterwards, in {@code UNDERGROUND_DECORATION}.
 *
 * <h2>Why one biome-driven feature and not one baked feature per biome</h2>
 * Vanilla's lush-cave moss is a plain {@code vegetation_patch} with a fixed {@code moss_block}
 * {@code ground_state}, and BetterEnd already uses that verbatim for {@code cave_lush_*_patch}. That shape
 * cannot express what the coater did, for two reasons:
 * <ul>
 *     <li>{@code JadeCaveBiome} picks its wall block from two {@code OpenSimplexNoise} fields <em>per
 *     position</em> ({@link EndCaveBiome#getWall(BlockPos)}). A {@code BlockStateProvider} in a datapack
 *     cannot reproduce that - it would have to be flattened to a single jadestone or a dumb random mix,
 *     and the jade caves' banded look is exactly what would be lost.</li>
 *     <li>{@link EndCaveBiome#getCeil(BlockPos)}/{@link EndCaveBiome#getWall(BlockPos)} and the biome's
 *     {@code getTopMaterial()} are the existing, already-maintained description of "what this cave is made
 *     of". Baking the same materials a second time into datagen would leave those methods as dead code and
 *     create two sources of truth that can silently drift (a new cave biome would look right in the biome
 *     class and generate plain End stone).</li>
 * </ul>
 * So the materials are resolved from the {@link EndCaveBiome} at placement time and the configuration
 * carries only geometry. That also means a single placed feature covers <em>every</em> cave biome, present
 * and future: {@code EndFeatures#addDefaultFeatures} puts it on every End biome (see the note there on why
 * the land ones carry it too), and a biome that declares no materials simply makes the feature a no-op.
 *
 * <h2>Shape: one sweep of the chunk, not a scatter of patches</h2>
 * The coater this replaces ran inside the carver and coated <em>every</em> block the carve had just exposed,
 * including sideways - which is what gave the Jade Caves jadestone walls at any height. Two decoration-time
 * shapes were tried before this one and both lost that:
 * <ul>
 *     <li>vanilla's {@code moss_patch} geometry (a disc of columns, each searching vertically for a face,
 *     plus spheres of wall material from a sampled quarter of them). A column search only ever finds floors
 *     and ceilings, so every wall sphere was anchored to the floor and reached at most its radius up the
 *     wall. Round caverns are {@code UniformInt.of(10, 30)} wide with a vertical squash of 1.6 (see
 *     {@code CarverProvider}), i.e. 12 to 38 blocks tall - so everything above the lowest few blocks stayed
 *     plain End stone and the Jade Caves read as End-stone caves with a jade floor.</li>
 *     <li>a flood fill of the cave air connected to the placement origin, inside a window around it. That
 *     coated walls and ceilings correctly but only out to the window: a census over 25 chunks of a jade cave
 *     put 76% of the visible cave surface in jadestone and left the rest End stone, and raising the
 *     placement count from 5 to 10 did not move that number at all - the misses were the parts of the cave
 *     further from a seed than the window was wide, which more seeds cannot fix.</li>
 * </ul>
 * So this version sweeps the whole chunk instead. It reads its own 16x16 columns between
 * {@link CaveSurfaceCoatConfig#minY} and {@code maxY} once - plus a {@link #MARGIN}-block ring around them,
 * which it only looks at - marks the enclosed air, collects every coatable block that bounds it (floor,
 * ceiling <em>and</em> wall alike) and paints the biome's wall material {@code shellDepth} blocks inward with
 * a breadth-first distance transform, which is the "5 layers thick" shell the sphere sampling only
 * approximated. Floor and ceiling materials are applied afterwards, in that order, so a biome that defines
 * both keeps the coater's precedence. Coverage no longer depends on where a placement happened to land, and
 * one sweep costs less than the ten windowed placements it replaces.
 * <p>
 * The pass consumes no randomness at all: given the same carved chunk it always produces the same coat.
 *
 * <h2>Telling a cave from the sky</h2>
 * Nothing in the block state distinguishes a carved cavern from the sky above an island or the void beside
 * it - 26.3's {@code applyCarvingMask} resolves carved positions through the aquifer, which hands back plain
 * {@code AIR} in the End, not {@code CAVE_AIR}. (The whole-column scan of the pre-1.21.6 coat leaked onto
 * island tops for exactly this reason.) The sweep uses the shape of the air instead: walking a column
 * bottom-up gives maximal runs of air, and a run only counts as cave air if <em>rock closes it at both
 * ends</em> inside the band. Sky above an island runs to the top of the band, open void between and below
 * islands runs to the bottom of it, and both are dropped. This is also why the configured band is wider than
 * the carved cave band: reaching an end of the band is the signal.
 * <p>
 * A cavern that breaches an island's underside therefore stops being coated at the breach, which is the
 * conservative direction to fail in.
 * <p>
 * The same classification then does double duty as the rule that keeps the coat off the outside of the
 * world: no block with open air against any of its six sides is ever painted, however close the cave behind
 * it comes. Together with {@link CaveSurfaceCoatConfig#replaceable} being bare End stone rather than the
 * whole {@code END_STONES} tag, that is what makes "this pass never alters a surface a player can see from
 * outside a cave" true by construction rather than by luck of the geometry.
 *
 * <h2>Chunk safety</h2>
 * The sweep writes only inside the chunk it is decorating and reads at most {@link #MARGIN} block beyond it,
 * so it stays far inside the 3x3 chunk window {@code ChunkStatus.FEATURES} guarantees and needs no reach
 * budget at all. A cave crossing a chunk border is coated by each chunk's own sweep; only the shell's
 * innermost layers stop at the border, where nothing can see them.
 */
public class CaveSurfaceCoatFeature extends Feature<CaveSurfaceCoatConfig> {
    /** Cell has not been looked at, or holds rock this feature may not touch. */
    private static final byte SOLID = 0;
    /** Cell holds rock the coat may paint. */
    private static final byte ROCK = 1;
    /** Cell holds air whose run is closed by rock at both ends: cave. */
    private static final byte CAVE_AIR = 2;
    /** Cell holds air that reaches an end of the band: sky or void, not cave. */
    private static final byte OPEN_AIR = 3;

    /**
     * How far outside the swept chunk the air is <em>looked at</em> (never written). A cave face on the
     * chunk border has its air in the next chunk over; without the margin neither chunk's sweep would see
     * that face, and every chunk border kept a one-block seam of bare End stone.
     */
    private static final int MARGIN = 1;
    /** Width of the read window: the chunk plus {@link #MARGIN} on each side. */
    private static final int SPAN = 16 + 2 * MARGIN;

    /** {@link #readColumn} is between air runs. */
    private static final int NO_RUN = -1;
    /** {@link #readColumn} is inside an air run that started at the bottom of the band, i.e. an open one. */
    private static final int OPEN_RUN = -2;

    public CaveSurfaceCoatFeature() {
        super(CaveSurfaceCoatConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<CaveSurfaceCoatConfig> ctx) {
        // 26.1/26.2 still separate Feature from its configuration, so the config arrives per call rather
        // than living on the instance. Everything below is the 26.3 sweep verbatim.
        final CaveSurfaceCoatConfig config = ctx.config();
        final WorldGenLevel world = ctx.level();
        final BlockPos origin = ctx.origin();
        final int height = config.maxY() - config.minY() + 1;
        if (height <= 2) {
            return false;
        }
        // The origin only says which chunk to sweep and gates the whole pass on its biome; every block
        // below is addressed from the chunk corner, never from the origin itself.
        final int minX = origin.getX() & ~15;
        final int minZ = origin.getZ() & ~15;

        final Columns columns = new Columns(world, origin.getY());
        final byte[] cells = new byte[SPAN * SPAN * height];
        final MutableBlockPos mut = new MutableBlockPos();

        // 1) Which columns may be painted at all. This runs first and on its own because it is cheap (one
        //    biome lookup per column) and it is what decides whether the expensive part happens: the
        //    placement deliberately carries no biome filter, so this is the pass's own gate.
        final boolean[] paintable = new boolean[SPAN * SPAN];
        boolean anyCaveColumn = false;
        for (int dx = -MARGIN; dx < 16 + MARGIN; dx++) {
            for (int dz = -MARGIN; dz < 16 + MARGIN; dz++) {
                final EndCaveBiome biome = columns.biomeAt(minX + dx, minZ + dz);
                final boolean paint = biome != null && paintsAnything(config, biome, origin);
                paintable[(dx + MARGIN) * SPAN + (dz + MARGIN)] = paint;
                anyCaveColumn |= paint && dx >= 0 && dx < 16 && dz >= 0 && dz < 16;
            }
        }
        if (!anyCaveColumn) {
            return false;
        }

        // 2) Read the window, column by column, and mark which air is cave air. Air is classified in EVERY
        //    column, including the margin and columns belonging to another biome - a cave face is a face no
        //    matter which side of a border its air sits on. Only rock in a paintable column is marked ROCK;
        //    everywhere else it stays SOLID, which is what keeps a jade cave from smearing jadestone into
        //    the empty cave next door.
        for (int dx = -MARGIN; dx < 16 + MARGIN; dx++) {
            for (int dz = -MARGIN; dz < 16 + MARGIN; dz++) {
                readColumn(
                        config, world, cells, mut, minX + dx, minZ + dz, dx, dz, height,
                        paintable[(dx + MARGIN) * SPAN + (dz + MARGIN)]
                );
            }
        }

        // 3) Collect the rock that bounds cave air, split by which face of the cave it is. `frontier` seeds
        //    the shell; a block bounding several air blocks appears in it once.
        final IntArrayList frontier = new IntArrayList();
        final IntArrayList floors = new IntArrayList();
        final IntArrayList ceilings = new IntArrayList();
        final boolean[] seen = new boolean[cells.length];
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                for (int dy = 0; dy < height; dy++) {
                    final int idx = index(dx, dy, dz, height);
                    // A block that faces open air as well is the outside of an island, not just a cave
                    // face; it keeps whatever the land biome's surface rules gave it.
                    if (cells[idx] != ROCK || touchesOpenAir(cells, dx, dy, dz, height)) {
                        continue;
                    }
                    boolean bounds = false;
                    if (isCaveAir(cells, dx, dy + 1, dz, height)) {
                        floors.add(idx);
                        bounds = true;
                    }
                    if (isCaveAir(cells, dx, dy - 1, dz, height)) {
                        ceilings.add(idx);
                        bounds = true;
                    }
                    if (!bounds) {
                        bounds = isCaveAir(cells, dx + 1, dy, dz, height)
                                || isCaveAir(cells, dx - 1, dy, dz, height)
                                || isCaveAir(cells, dx, dy, dz + 1, height)
                                || isCaveAir(cells, dx, dy, dz - 1, height);
                    }
                    if (bounds) {
                        seen[idx] = true;
                        frontier.add(idx);
                    }
                }
            }
        }
        if (frontier.isEmpty()) {
            return false;
        }

        // 4) The shell: a breadth-first distance transform inward from the cave face, so the wall material
        //    forms an even layer instead of the sampled spheres the first version grew. Runs BEFORE the
        //    floor and ceiling materials so a jade floor keeps its jadestone shell instead of being reset by
        //    it (CaveSurfaceCoater ordered its steps for the same reason). It walks the `cells` array, so
        //    the whole transform costs no further world reads.
        IntArrayList current = frontier;
        for (int depth = 1; depth <= config.shellDepth() && !current.isEmpty(); depth++) {
            final IntArrayList next = new IntArrayList();
            for (int i = 0; i < current.size(); i++) {
                final int idx = current.getInt(i);
                toPos(config, mut, idx, minX, minZ, height);
                final int dx = mut.getX() - minX;
                final int dy = mut.getY() - config.minY();
                final int dz = mut.getZ() - minZ;
                // Deeper shell layers can reach the outside of an island where the rock between a cave and
                // the sky is thinner than shellDepth. Painting there would show jadestone on the island's
                // skin, so those cells are skipped - but the shell still spreads THROUGH them, or it would
                // stop dead at every thin spot.
                if (!touchesOpenAir(cells, dx, dy, dz, height)) {
                    final EndCaveBiome biome = columns.biomeAt(mut.getX(), mut.getZ());
                    if (biome != null) {
                        final BlockState wall = biome.getWall(mut);
                        if (wall != null) {
                            BlocksHelper.setWithoutUpdate(world, mut, wall);
                        }
                    }
                }
                if (depth == config.shellDepth()) {
                    continue;
                }
                expand(cells, seen, next, dx + 1, dy, dz, height);
                expand(cells, seen, next, dx - 1, dy, dz, height);
                expand(cells, seen, next, dx, dy + 1, dz, height);
                expand(cells, seen, next, dx, dy - 1, dz, height);
                expand(cells, seen, next, dx, dy, dz + 1, height);
                expand(cells, seen, next, dx, dy, dz - 1, height);
            }
            current = next;
        }

        // 5) Floor top material, applied ONLY if the biome defines one that is not plain End stone (jade's
        //    getTopMaterial() is the default End stone, so its jadestone shell is preserved; lush caves
        //    overwrite the floor with their cave moss).
        for (int i = 0; i < floors.size(); i++) {
            toPos(config, mut, floors.getInt(i), minX, minZ, height);
            final EndCaveBiome biome = columns.biomeAt(mut.getX(), mut.getZ());
            if (biome == null) {
                continue;
            }
            final BlockState top = biome.getTopMaterial();
            if (top != null && !top.is(Blocks.END_STONE)) {
                BlocksHelper.setWithoutUpdate(world, mut, top);
            }
        }

        // 6) Ceiling material.
        for (int i = 0; i < ceilings.size(); i++) {
            toPos(config, mut, ceilings.getInt(i), minX, minZ, height);
            final EndCaveBiome biome = columns.biomeAt(mut.getX(), mut.getZ());
            if (biome == null) {
                continue;
            }
            final BlockState ceil = biome.getCeil(mut);
            if (ceil != null) {
                BlocksHelper.setWithoutUpdate(world, mut, ceil);
            }
        }
        return true;
    }

    /**
     * Reads one column of the band into {@code cells} and classifies its air: a maximal run of air counts as
     * {@link #CAVE_AIR} only when rock closes it both below and above <em>within the band</em>, otherwise it
     * is {@link #OPEN_AIR} (sky, or the void under an island) - see the class doc.
     */
    private static void readColumn(
            CaveSurfaceCoatConfig config,
            WorldGenLevel world,
            byte[] cells,
            MutableBlockPos mut,
            int x,
            int z,
            int dx,
            int dz,
            int height,
            boolean paintable
    ) {
        int runStart = NO_RUN;
        for (int dy = 0; dy < height; dy++) {
            mut.set(x, config.minY() + dy, z);
            final BlockState state = world.getBlockState(mut);
            final int idx = index(dx, dy, dz, height);
            if (state.isAir()) {
                cells[idx] = OPEN_AIR;
                if (runStart == NO_RUN) {
                    // A run that starts at the very bottom of the band has no rock below it inside the band;
                    // OPEN_RUN is sticky for the rest of the run, so it can never be closed from below later.
                    runStart = dy == 0 ? OPEN_RUN : dy;
                }
                continue;
            }
            cells[idx] = paintable && coatable(config, state) ? ROCK : SOLID;
            if (runStart >= 0) {
                // Rock closed the run from above, and runStart >= 0 means rock closed it from below too.
                for (int y = runStart; y < dy; y++) {
                    cells[index(dx, y, dz, height)] = CAVE_AIR;
                }
            }
            runStart = NO_RUN;
        }
        // A run still open at the top of the band was never closed from above: leave it OPEN_AIR.
    }

    /**
     * Marks a coatable, not-yet-visited cell as the next shell layer. Bounded to the chunk itself, not to
     * the read window: the margin is there to be looked at, never to be written.
     */
    private static void expand(byte[] cells, boolean[] seen, IntArrayList next, int dx, int dy, int dz, int height) {
        if (dx < 0 || dx > 15 || dz < 0 || dz > 15 || dy < 0 || dy >= height) {
            return;
        }
        final int idx = index(dx, dy, dz, height);
        if (seen[idx] || cells[idx] != ROCK) {
            return;
        }
        seen[idx] = true;
        next.add(idx);
    }

    private static boolean isCaveAir(byte[] cells, int dx, int dy, int dz, int height) {
        return inWindow(dx, dy, dz, height) && cells[index(dx, dy, dz, height)] == CAVE_AIR;
    }

    /**
     * Whether a cell has open air - sky, or the void beside an island - against any of its six sides. Such a
     * block is part of the outside of the world, and the coat leaves it alone however close the cave behind
     * it comes: repainting it would put jadestone on an island's skin. The window's margin is what makes
     * this answerable for cells on the chunk border.
     */
    private static boolean touchesOpenAir(byte[] cells, int dx, int dy, int dz, int height) {
        return isOpenAir(cells, dx + 1, dy, dz, height)
                || isOpenAir(cells, dx - 1, dy, dz, height)
                || isOpenAir(cells, dx, dy + 1, dz, height)
                || isOpenAir(cells, dx, dy - 1, dz, height)
                || isOpenAir(cells, dx, dy, dz + 1, height)
                || isOpenAir(cells, dx, dy, dz - 1, height);
    }

    private static boolean isOpenAir(byte[] cells, int dx, int dy, int dz, int height) {
        return inWindow(dx, dy, dz, height) && cells[index(dx, dy, dz, height)] == OPEN_AIR;
    }

    private static boolean inWindow(int dx, int dy, int dz, int height) {
        return dx >= -MARGIN && dx < 16 + MARGIN
                && dz >= -MARGIN && dz < 16 + MARGIN
                && dy >= 0 && dy < height;
    }

    private static int index(int dx, int dy, int dz, int height) {
        return ((dx + MARGIN) * SPAN + (dz + MARGIN)) * height + dy;
    }

    private static void toPos(CaveSurfaceCoatConfig config, MutableBlockPos mut, int idx, int minX, int minZ, int height) {
        final int dy = idx % height;
        final int plane = idx / height;
        final int dz = plane % SPAN - MARGIN;
        final int dx = plane / SPAN - MARGIN;
        mut.set(minX + dx, config.minY() + dy, minZ + dz);
    }

    /**
     * Whether the coat may touch this block: rock it is allowed to replace, and not something a
     * {@code UNDERGROUND_ORES} feature put there first (see {@link CaveSurfaceCoatConfig#protect}).
     */
    private static boolean coatable(CaveSurfaceCoatConfig config, BlockState state) {
        return state.is(config.replaceable()) && !state.is(config.protect());
    }

    /**
     * Whether this biome wants anything painted at all, i.e. whether sweeping its columns can have any
     * effect. Plain End stone as a top material counts as "leave alone": repainting the rock the floor is
     * made of with itself is not worth the read.
     */
    private static boolean paintsAnything(CaveSurfaceCoatConfig config, EndCaveBiome biome, BlockPos pos) {
        if (config.shellDepth() > 0 && biome.getWall(pos) != null) {
            return true;
        }
        final BlockState top = biome.getTopMaterial();
        if (top != null && !top.is(Blocks.END_STONE)) {
            return true;
        }
        return biome.getCeil(pos) != null;
    }

    /**
     * Per-column cache of the {@link EndCaveBiome} that owns a column, sampled once at a fixed Y - the model
     * {@code CaveSurfaceCoater.ColumnResolver} used. A {@code null} entry is a real, cached answer meaning
     * "not a cave biome, skip this column".
     * <p>
     * The biome comes out of the column's own {@link ChunkAccess}, fetched with {@code requireChunk = false},
     * and <b>not</b> from {@link WorldGenLevel#getBiome(BlockPos)}. That one takes the "give me the chunk or
     * throw" path and runs the position through {@code BiomeManager}'s Voronoi fuzz first, which reaches a
     * few blocks past the position it was asked about - enough, at a chunk edge, to leave the 3x3 the
     * {@code FEATURES} step guarantees and die with {@code IllegalStateException: Requested chunk unavailable
     * during world generation} (it does; that is how this cache came to exist). Asking the chunk directly
     * cannot throw - an unavailable chunk simply answers {@code null}, which this treats as "not a cave
     * column", i.e. paint nothing. It also drops the Voronoi fuzz, so the coat follows the biome source's own
     * cell boundary exactly, which is what the per-column check wants in the first place.
     * <p>
     * {@code ChunkStatus.BIOMES} is all this needs, and every chunk in reach is at {@code CARVERS} or later
     * while features run.
     */
    private static final class Columns {
        private final WorldGenLevel world;
        private final int sampleY;
        private final Map<Long, ChunkAccess> chunks = new HashMap<>();
        private final Map<Long, EndCaveBiome> biomes = new HashMap<>();

        private Columns(WorldGenLevel world, int sampleY) {
            this.world = world;
            this.sampleY = sampleY;
        }

        private static long key(int x, int z) {
            return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
        }

        @Nullable
        private ChunkAccess chunkAt(int x, int z) {
            final int chunkX = x >> 4;
            final int chunkZ = z >> 4;
            final long key = key(chunkX, chunkZ);
            if (chunks.containsKey(key)) {
                return chunks.get(key);
            }
            final ChunkAccess chunk = world.getChunk(chunkX, chunkZ, ChunkStatus.BIOMES, false);
            chunks.put(key, chunk);
            return chunk;
        }

        @Nullable
        private EndCaveBiome biomeAt(int x, int z) {
            // Not computeIfAbsent: a null result is a common answer here and has to be cached too.
            final long key = key(x, z);
            if (biomes.containsKey(key)) {
                return biomes.get(key);
            }
            final ChunkAccess chunk = chunkAt(x, z);
            EndCaveBiome resolved = null;
            if (chunk != null) {
                final Holder<Biome> holder = chunk.getNoiseBiome(
                        QuartPos.fromBlock(x),
                        QuartPos.fromBlock(sampleY),
                        QuartPos.fromBlock(z)
                );
                if (BiomeManager.biomeDataForHolder(holder) instanceof EndCaveBiome cave) {
                    resolved = cave;
                }
            }
            biomes.put(key, resolved);
            return resolved;
        }
    }
}
