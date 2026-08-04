package org.betterx.betterend.world.carvers;

import org.betterx.betterend.world.biome.cave.EndCaveBiome;
import de.ambertation.wover.biome.api.BiomeManager;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Carve-time port of the deleted {@code CaveSurfaceCoatFeature}. After a carver has finished turning
 * blocks into {@link net.minecraft.world.level.levelgen.carver.WorldCarver#CAVE_AIR CAVE_AIR} for a
 * single {@code carve()} invocation, this helper coats the exposed End-stone faces of the blocks that
 * <em>this invocation carved</em> with the surrounding cave biome's surface materials (floor top
 * material, ceiling material and a thick noisy wall shell).
 *
 * <h2>Why carve-time instead of a decoration feature</h2>
 * The old feature scanned whole chunk columns for any air-over-{@code END_STONES} transition. The
 * island <em>top</em> surface is such a transition too ({@code END_STONES} tags the land biomes'
 * surface blocks), so the cave coat leaked onto the land surface and broke its look and vegetation.
 * Operating on the carved set only is correct by construction: carvers touch only the blocks they
 * carve, and the island top is never carved. (Vanilla {@code CaveWorldCarver} places grass under
 * carved openings the same way, inside its per-block carve routine.)
 *
 * <h2>Per-column cave-biome resolution</h2>
 * BetterEnd cave biomes are <em>vertical</em>: a column either is a cave column (at every Y) or it is
 * not, but the carvers cut <em>through</em> and above the cave band, so most exposed faces sit above
 * it. The biome is therefore resolved once per {@code (lx,lz)} column at a fixed low sample Y
 * ({@code minGenY + 8}) that is guaranteed inside the vertical band; columns that do not resolve to an
 * {@link EndCaveBiome} are skipped entirely. This restores full-height coverage while keeping cave
 * borders exact (a carved block reaching into a neighbouring non-cave column is never coated).
 *
 * <h2>Determinism</h2>
 * The only {@code random} draws this helper makes are the wall-shell {@code 1/4} samples. They are
 * emitted <em>after</em> every geometry-relevant draw of the calling {@code carve()} invocation
 * ({@link EndCaveCarver} draws center/radius before its carve loop; {@link EndTunnelCarver} makes no
 * geometry draws from {@code random} at all), so consuming {@code random} here cannot perturb the
 * cavern shape. Within a single invocation the draws are emitted while iterating the carved list in
 * insertion (= carve loop) order, so the sequence is fixed and reproducible.
 */
final class CaveSurfaceCoater {
    private static final Direction[] NEIGHBORS = Direction.values();
    /** Legacy radius-5 sphere offset table, replicated from the removed {@code CaveSurfaceCoatFeature#SPHERE}. */
    private static final Vec3i[] SPHERE;

    private CaveSurfaceCoater() {
    }

    /**
     * Coats the carved faces of one {@code carve()} invocation.
     *
     * @param context     the carving context (used for the {@code minGenY} sample band).
     * @param chunk       the chunk currently being carved (all writes stay inside it).
     * @param biomeGetter resolves a biome {@link Holder} for a world position.
     * @param random      the invocation's random; only the wall-shell draws are consumed, after all geometry draws.
     * @param carved      the positions this invocation turned into cave air, in carve-loop order.
     */
    static void coat(
            CarvingContext context,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeGetter,
            RandomSource random,
            LongList carved
    ) {
        if (carved.isEmpty()) {
            return;
        }

        final ChunkPos cp = chunk.getPos();
        final int minBX = cp.getMinBlockX();
        final int minBZ = cp.getMinBlockZ();
        final int gateY = context.getMinGenY() + 8;

        // PER-COLUMN cave-biome cache (indexed (lx<<4)|lz). `resolved` distinguishes "not looked up yet"
        // from "looked up, no cave biome" (null is a valid, cached result meaning skip the column).
        final EndCaveBiome[] columnBiome = new EndCaveBiome[256];
        final boolean[] columnResolved = new boolean[256];
        // Holder -> EndCaveBiome cache so a repeated holder is unwrapped/looked up only once per invocation.
        final IdentityHashMap<Holder<Biome>, EndCaveBiome> holderCache = new IdentityHashMap<>();
        final ColumnResolver resolver = new ColumnResolver(
                biomeGetter, minBX, minBZ, gateY, columnBiome, columnResolved, holderCache);

        final BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos n = new BlockPos.MutableBlockPos();

        // 1) Classify the carved set against the (post-carve, pre-coat) chunk state, exactly like the old
        //    feature scanned original air blocks. Floor = below is End stone; ceiling = above is End stone;
        //    exposed = any of the six neighbours is End stone (wall-shell centers). Columns that do not
        //    resolve to a cave biome are skipped here.
        final LongArrayList exposed = new LongArrayList();
        final LongArrayList floorBelow = new LongArrayList();
        final LongArrayList ceilAbove = new LongArrayList();
        for (int i = 0; i < carved.size(); i++) {
            final long packed = carved.getLong(i);
            m.set(packed);
            final int lx = m.getX() - minBX;
            final int lz = m.getZ() - minBZ;
            if (resolver.at(lx, lz) == null) {
                continue;
            }
            final int x = m.getX();
            final int y = m.getY();
            final int z = m.getZ();

            boolean isExposed = false;

            n.set(x, y - 1, z);
            if (chunk.getBlockState(n).is(CommonBlockTags.END_STONES)) {
                floorBelow.add(BlockPos.asLong(x, y - 1, z));
                isExposed = true;
            }
            n.set(x, y + 1, z);
            if (chunk.getBlockState(n).is(CommonBlockTags.END_STONES)) {
                ceilAbove.add(BlockPos.asLong(x, y + 1, z));
                isExposed = true;
            }
            if (!isExposed) {
                for (Direction dir : NEIGHBORS) {
                    if (dir.getStepY() != 0) {
                        continue; // below/above already handled
                    }
                    final int nx = x + dir.getStepX();
                    final int nz = z + dir.getStepZ();
                    if (nx < minBX || nx > minBX + 15 || nz < minBZ || nz > minBZ + 15) {
                        continue; // chunk-local only
                    }
                    n.set(nx, y, nz);
                    if (chunk.getBlockState(n).is(CommonBlockTags.END_STONES)) {
                        isExposed = true;
                        break;
                    }
                }
            }
            if (isExposed) {
                exposed.add(packed);
            }
        }

        // 2) Thick noisy wall shell (legacy EndCaveFeatures#placeWalls). Runs BEFORE floor-material
        //    application so a jade floor keeps its jadestone shell instead of being reset to End stone.
        //    Iterated in carve-loop order; the 1/4 sample is the only random draw and happens after all
        //    geometry draws of this invocation.
        final LongOpenHashSet shell = new LongOpenHashSet();
        final BlockPos.MutableBlockPos w = new BlockPos.MutableBlockPos();
        for (int i = 0; i < exposed.size(); i++) {
            if (random.nextInt(4) != 0) {
                continue; // legacy ~1/4 sampling of exposed surface blocks
            }
            m.set(exposed.getLong(i));
            final EndCaveBiome biome = resolver.at(m.getX() - minBX, m.getZ() - minBZ);
            if (biome == null || biome.getWall(m) == null) {
                continue; // biome has no wall material (e.g. lush caves) -> nothing to spread
            }
            for (Vec3i off : SPHERE) {
                final int wx = m.getX() + off.getX();
                final int wy = m.getY() + off.getY();
                final int wz = m.getZ() + off.getZ();
                if (wx < minBX || wx > minBX + 15 || wz < minBZ || wz > minBZ + 15) {
                    continue; // chunk-safe: only touch blocks inside this chunk
                }
                if (resolver.at(wx - minBX, wz - minBZ) == null) {
                    continue; // do not bleed a coat into a neighbouring non-cave column
                }
                w.set(wx, wy, wz);
                if (!chunk.getBlockState(w).is(CommonBlockTags.END_STONES)) {
                    continue;
                }
                if (!shell.add(BlockPos.asLong(wx, wy, wz))) {
                    continue;
                }
                final BlockState wall = biome.getWall(w);
                if (wall != null) {
                    chunk.setBlockState(w, wall);
                }
            }
        }

        // 3) Floor top material: apply ONLY if the biome defines one that is not plain End stone (jade's
        //    getTopMaterial() is the default End stone, so its jadestone shell from step 2 is preserved;
        //    lush caves overwrite the floor with their cave moss).
        for (int i = 0; i < floorBelow.size(); i++) {
            m.set(floorBelow.getLong(i));
            final EndCaveBiome biome = resolver.at(m.getX() - minBX, m.getZ() - minBZ);
            if (biome == null) {
                continue;
            }
            final BlockState top = biome.getTopMaterial();
            if (top != null && !top.is(Blocks.END_STONE)) {
                chunk.setBlockState(m, top);
            }
        }

        // 4) Ceiling material.
        for (int i = 0; i < ceilAbove.size(); i++) {
            m.set(ceilAbove.getLong(i));
            final EndCaveBiome biome = resolver.at(m.getX() - minBX, m.getZ() - minBZ);
            if (biome == null) {
                continue;
            }
            final BlockState ceil = biome.getCeil(m);
            if (ceil != null) {
                chunk.setBlockState(m, ceil);
            }
        }
    }

    /** Resolves and caches the {@link EndCaveBiome} for a chunk-local column, or {@code null} for non-cave columns. */
    private static final class ColumnResolver {
        private final Function<BlockPos, Holder<Biome>> biomeGetter;
        private final int minBX;
        private final int minBZ;
        private final int gateY;
        private final EndCaveBiome[] columnBiome;
        private final boolean[] columnResolved;
        private final IdentityHashMap<Holder<Biome>, EndCaveBiome> holderCache;

        private ColumnResolver(
                Function<BlockPos, Holder<Biome>> biomeGetter,
                int minBX,
                int minBZ,
                int gateY,
                EndCaveBiome[] columnBiome,
                boolean[] columnResolved,
                IdentityHashMap<Holder<Biome>, EndCaveBiome> holderCache
        ) {
            this.biomeGetter = biomeGetter;
            this.minBX = minBX;
            this.minBZ = minBZ;
            this.gateY = gateY;
            this.columnBiome = columnBiome;
            this.columnResolved = columnResolved;
            this.holderCache = holderCache;
        }

        private EndCaveBiome at(int lx, int lz) {
            final int idx = (lx << 4) | lz;
            if (!columnResolved[idx]) {
                // Pass an immutable position: the biome getter may retain the reference.
                final Holder<Biome> holder = biomeGetter.apply(new BlockPos(minBX + lx, gateY, minBZ + lz));
                EndCaveBiome resolved = null;
                if (holder != null) {
                    if (holderCache.containsKey(holder)) {
                        resolved = holderCache.get(holder);
                    } else {
                        final BiomeData data = BiomeManager.biomeDataForHolder(holder);
                        resolved = data instanceof EndCaveBiome cave ? cave : null;
                        holderCache.put(holder, resolved);
                    }
                }
                columnBiome[idx] = resolved;
                columnResolved[idx] = true;
            }
            return columnBiome[idx];
        }
    }

    static {
        final List<Vec3i> prePos = Lists.newArrayList();
        final int radius = 5;
        final int r2 = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            final int x2 = x * x;
            for (int y = -radius; y <= radius; y++) {
                final int y2 = y * y;
                for (int z = -radius; z <= radius; z++) {
                    final int z2 = z * z;
                    if (x2 + y2 + z2 < r2) {
                        prePos.add(new Vec3i(x, y, z));
                    }
                }
            }
        }
        SPHERE = prePos.toArray(new Vec3i[]{});
    }
}
