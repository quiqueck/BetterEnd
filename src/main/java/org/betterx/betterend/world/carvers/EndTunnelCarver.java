package org.betterx.betterend.world.carvers;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndTags;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.util.function.Function;

/**
 * Real {@link WorldCarver} port of the legacy {@code TunelCaveFeature}: carves a continuous,
 * noise-field driven tunnel network. {@link #getRange()} is {@code 0} because the underlying
 * noise field is spatially continuous &ndash; each chunk carves only its own columns and the
 * tunnels line up across chunk borders automatically.
 * <p>
 * The three {@link OpenSimplexNoise} instances ({@code noiseH}/{@code noiseV}/{@code noiseD}) are
 * built in the exact same order as the legacy feature: {@code new LegacyRandomSource(seed)} then
 * three {@code nextInt()} draws. The seed is derived from the world seed via the carving context's
 * {@link net.minecraft.world.level.levelgen.RandomState} (a positional random factory keyed on a
 * fixed {@link ResourceLocation}), so no accessor mixin is needed &ndash; see {@link #getNoises}.
 * <p>
 * The legacy per-corner density lerp is preserved, but the four corner factors are now sourced from
 * biomes instead of the old {@code hasCaves} land flags: a corner is {@code 1.0} when its biome
 * holder {@code is(EndTags.IS_END_CAVE)} else {@code 0.0}. This bilinear blend tapers tunnels to
 * nothing at cave-biome borders and replaces the legacy near-origin guard (which is intentionally
 * not ported).
 */
public class EndTunnelCarver extends WorldCarver<EndTunnelCarverConfiguration> {
    private static final ResourceLocation NOISE_SEED_KEY = BetterEnd.C.mk("tunnel_cave_noise");

    private record Noises3(long seed, OpenSimplexNoise h, OpenSimplexNoise v, OpenSimplexNoise d) {}

    // Carving runs per chunk; cache the three noises so we don't reallocate them for every chunk.
    // Keyed on the derived seed so a world reload with a different seed rebuilds them.
    private static volatile Noises3 cached;

    public EndTunnelCarver(Codec<EndTunnelCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean isStartChunk(EndTunnelCarverConfiguration cfg, RandomSource random) {
        return random.nextFloat() <= cfg.probability;
    }

    @Override
    public int getRange() {
        return 0;
    }

    @Override
    public boolean carve(
            CarvingContext context,
            EndTunnelCarverConfiguration cfg,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeGetter,
            RandomSource random,
            Aquifer aquifer,
            ChunkPos startChunkPos,
            CarvingMask mask
    ) {
        final ChunkPos cp = chunk.getPos();

        // Vanilla NoiseBasedChunkGenerator.applyCarvers iterates a FIXED 17x17 chunk neighborhood
        // (j,k in -8..8) around every generating chunk and, for each neighbor that isStartChunk,
        // calls this carve() with chunk = the CURRENT chunk being generated and startChunkPos = the
        // NEIGHBOR chunk. getRange() is NOT consulted by that loop in 1.21.6, so up to 17*17 = 289
        // invocations hit the same current chunk. This tunnel carver's noise field is chunk-local
        // and fully deterministic: it reads only the current chunk's own columns/noise and ignores
        // startChunkPos entirely, so all 289 invocations produce byte-for-byte identical output.
        // Carving once - on the chunk's own start invocation - therefore yields the identical world
        // at 1/289th the cost. isStartChunk stays probability-based; probability is 1.0 in data, so
        // every chunk still self-carves. This also skips the getNoises() RandomState lookup 288x.
        if (!startChunkPos.equals(cp)) {
            return false;
        }

        final int x1 = cp.getMinBlockX();
        final int z1 = cp.getMinBlockZ();

        // Legacy a/b/c/d corner blend, now sourced from the IS_END_CAVE biome tag.
        final float a = caveFactor(biomeGetter, x1, z1);
        final float b = caveFactor(biomeGetter, x1 + 16, z1);
        final float c = caveFactor(biomeGetter, x1, z1 + 16);
        final float d = caveFactor(biomeGetter, x1 + 16, z1 + 16);
        if (a == 0F && b == 0F && c == 0F && d == 0F) {
            return false;
        }

        final Noises3 noises = getNoises(context);
        final int minGenY = context.getMinGenY();
        final int maxGenY = minGenY + context.getGenDepth() - 1;
        // Caves are the vertical cave-biome band; never carve terrain above its ceiling, or a tunnel
        // eats into floating islands that sit above the band (see EndCaveCarver#caveBandCeiling).
        final int bandCeiling = EndCaveCarver.caveBandCeiling();
        final float threshold = cfg.threshold;

        boolean carved = false;
        // Positions this invocation turns into cave air, in carve-loop order; coated after the loop.
        final LongArrayList carvedPositions = new LongArrayList();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                final int wheight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, lx, lz);
                final float dx = lx / 16F;
                final float dz = lz / 16F;
                final int wx = lx + x1;
                final int wz = lz + z1;
                pos.setX(wx);
                pos.setZ(wz);

                final float da = Mth.lerp(dx, a, b);
                final float db = Mth.lerp(dx, c, d);
                final float density = 1 - Mth.lerp(dz, da, db);
                if (density < 0.5F) {
                    for (int y = minGenY + 1; y < wheight && y <= maxGenY && y <= bandCeiling; y++) {
                        pos.setY(y);
                        final float gradient = 1 - Mth.clamp((wheight - y) * 0.1F, 0F, 1F);
                        if (gradient > 0.5F) {
                            break;
                        }
                        float val = Mth.abs((float) noises.h.eval(wx * 0.02, y * 0.01, wz * 0.02));
                        final float vert = Mth.sin((y + (float) noises.v.eval(wx * 0.01, wz * 0.01) * 20) * 0.1F) * 0.9F;
                        final float dist = (float) noises.d.eval(wx * 0.1, y * 0.1, wz * 0.1) * 0.12F;
                        val = (val + vert * vert + dist) + density + gradient;
                        if (val < threshold
                                && chunk.getBlockState(pos).is(CommonBlockTags.END_STONES)
                                && !EndCaveCarver.isWaterNear(chunk, cp, pos, neighbor, minGenY, maxGenY)) {
                            chunk.setBlockState(pos, CAVE_AIR);
                            mask.set(lx, y, lz);
                            carvedPositions.add(BlockPos.asLong(wx, y, wz));
                            carved = true;
                        }
                    }
                }
            }
        }

        // Coat the exposed End-stone faces of the blocks THIS invocation carved with the per-column cave
        // biome's materials. This carver makes no geometry-relevant random draws, so the wall-shell draws
        // made inside the coater cannot affect the tunnel shape (see CaveSurfaceCoater).
        CaveSurfaceCoater.coat(context, chunk, biomeGetter, random, carvedPositions);

        return carved;
    }

    private static float caveFactor(Function<BlockPos, Holder<Biome>> biomeGetter, int x, int z) {
        final Holder<Biome> biome = biomeGetter.apply(new BlockPos(x, 0, z));
        return biome != null && biome.is(EndTags.IS_END_CAVE) ? 1F : 0F;
    }

    /**
     * Builds (or returns the cached) three noises. The seed is drawn from the world-seed-derived
     * {@link net.minecraft.world.level.levelgen.RandomState}: a positional random factory keyed on
     * {@link #NOISE_SEED_KEY} yields a stable {@code long}, which then seeds a
     * {@link LegacyRandomSource} whose first three {@code nextInt()} draws feed the three noises &ndash;
     * exactly the legacy construction order.
     */
    private static Noises3 getNoises(CarvingContext context) {
        // at(0,0,0) mixes the factory's world-derived seed into the result (legacy factories XOR it
        // with the position hash). fromSeed(long) must NOT be used here: the legacy implementation -
        // which the End's noise settings select - ignores the factory seed entirely, which would give
        // every world the same tunnel layout.
        final long seed = context.randomState()
                                 .getOrCreateRandomFactory(NOISE_SEED_KEY)
                                 .at(0, 0, 0)
                                 .nextLong();
        final Noises3 local = cached;
        if (local != null && local.seed == seed) {
            return local;
        }
        final RandomSource rand = new LegacyRandomSource(seed);
        final Noises3 built = new Noises3(
                seed,
                new OpenSimplexNoise(rand.nextInt()),
                new OpenSimplexNoise(rand.nextInt()),
                new OpenSimplexNoise(rand.nextInt())
        );
        cached = built;
        return built;
    }
}
