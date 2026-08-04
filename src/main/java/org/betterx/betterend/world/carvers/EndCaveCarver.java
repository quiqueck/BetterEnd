package org.betterx.betterend.world.carvers;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.world.generator.TerrainGenerator;
import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.util.function.Function;

/**
 * Real {@link WorldCarver} port of the legacy {@code RoundCaveFeature}: carves a single
 * noise-distorted ellipsoidal cavern per start chunk. Shape math is a 1:1 port of
 * {@code RoundCaveFeature#generate} &ndash; same {@link OpenSimplexNoise} seed
 * ({@code MHelper.getSeed(534, centerX, centerZ)}), same {@code hr = radius * 0.75},
 * {@code nr = radius * 0.25} noise radius blend, and same vertical squash (the {@code (y-cy)}
 * distance term is multiplied by {@code vertical_squash} before it enters the sphere test).
 */
public class EndCaveCarver extends WorldCarver<EndCaveCarverConfiguration> {
    public EndCaveCarver(Codec<EndCaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean isStartChunk(EndCaveCarverConfiguration cfg, RandomSource random) {
        return random.nextFloat() <= cfg.probability;
    }

    /**
     * Solid blocks kept between a cavern's ceiling and the column's surface. Round caverns reach up to
     * ~y59 (centre y&le;40 + radius&le;30 / squash 1.6), overlapping the island-surface band, and unlike the
     * tunnel carver (which has a gradient roof guard) this carver would otherwise carve straight through
     * an island top and open a pit to the sky. Keeping this many blocks below WORLD_SURFACE_WG uncarved
     * guarantees every cavern stays roofed - the caves that used to breach the surface as "carved-away"
     * rectangular pits now keep a solid cap.
     */
    private static final int SURFACE_ROOF = 5;
    /** Fallback cave-band ceiling used only before the End generator config is initialised. */
    private static final int DEFAULT_BAND_CEILING = WoverEndConfig.DEFAULT_CAVE_BIOMES_TOP_Y;

    @Override
    public int getRange() {
        return 3;
    }

    /**
     * The highest world Y a cave may carve: the top of the vertical cave-biome band
     * ({@link WoverEndConfig#caveBiomesTopY}). Caves ARE that band, so carving must never remove terrain
     * above it - otherwise a tall cavern reaches up across the void gap and hollows the underside of a
     * floating island that sits well above the band, leaving a thin flat-bottomed slab. Read from the
     * live End generator config (via {@link TerrainGenerator#config}); falls back to the default only
     * before {@code initNoise} has run.
     */
    static int caveBandCeiling() {
        final WoverEndConfig cfg = TerrainGenerator.config;
        return cfg != null ? cfg.caveBiomesTopY : DEFAULT_BAND_CEILING;
    }

    @Override
    public boolean carve(
            CarvingContext context,
            EndCaveCarverConfiguration cfg,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeGetter,
            RandomSource random,
            Aquifer aquifer,
            ChunkPos startChunkPos,
            CarvingMask mask
    ) {
        // Center is chosen inside the *start* chunk; the cavern may reach into the chunk we are
        // currently carving (getRange() == 3), so below we only touch columns of `chunk`.
        //
        // (a) Draw center x/z/y and radius EXACTLY as before. Vanilla applyCarvers visits this start
        //     chunk once per target chunk in its 17x17 neighborhood loop, and every visit MUST
        //     consume the same four `random` draws in the same order so the cavern geometry is
        //     identical no matter which target chunk triggered the invocation. These draws are
        //     therefore unconditional and precede any early-out.
        final int centerX = startChunkPos.getBlockX(random.nextInt(16));
        final int centerZ = startChunkPos.getBlockZ(random.nextInt(16));
        final int centerY = cfg.y.sample(random, context);
        final int radius = cfg.radius.sample(random);
        final float squash = cfg.verticalSquash;

        final int reach = radius + 5;

        final ChunkPos cp = chunk.getPos();
        final int minBX = cp.getMinBlockX();
        final int minBZ = cp.getMinBlockZ();

        // (b) O(1) early rejection. The cavern's horizontal footprint is the axis-aligned box
        //     [centerX +- reach] x [centerZ +- reach], where reach = radius + noise margin 5 is the
        //     widest the noise-distorted sphere can extend. If that box does not overlap this chunk's
        //     16-wide block-column range, no column here can satisfy dist < r*r, so nothing would be
        //     written. The four random draws above are already consumed, so this early-out cannot
        //     change the world: a rejected invocation could not have carved a single block anyway.
        //     radius <= 30 => reach <= 35 (~3 chunks), so a start chunk 4+ chunks away is rejected in
        //     constant time instead of scanning all 256 columns. This replaces relying on the
        //     per-column |x-centerX|>reach test to reject the whole (non-intersecting) chunk.
        if (centerX + reach < minBX || centerX - reach > minBX + 15
                || centerZ + reach < minBZ || centerZ - reach > minBZ + 15) {
            return false;
        }

        // Constructed only after the reject: the noise depends solely on centerX/centerZ (identical
        // for every target chunk of this start chunk), so building it here instead of before the
        // early-out changes no output - it just avoids allocating the noise for chunks the sphere
        // cannot reach.
        final OpenSimplexNoise noise = new OpenSimplexNoise(MHelper.getSeed(534, centerX, centerZ));

        final double hr = radius * 0.75;
        final double nr = radius * 0.25;

        final int minGenY = context.getMinGenY();
        final int maxGenY = minGenY + context.getGenDepth() - 1;

        // Cap the whole cavern at the cave-band ceiling so it can never carve terrain above the band
        // (e.g. the underside of a floating island sitting above it - the flat-slab bug).
        final int bandCeiling = caveBandCeiling();
        final int y1 = Math.max(MHelper.floor(centerY - reach / squash), minGenY);
        final int y2 = Math.min(Math.min(MHelper.floor(centerY + reach / squash), maxGenY), bandCeiling);
        if (y1 > y2) {
            return false;
        }

        boolean carved = false;
        // Positions this invocation turns into cave air, in carve-loop order; coated after the loop.
        final LongArrayList carvedPositions = new LongArrayList();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();

        for (int lx = 0; lx < 16; lx++) {
            final int x = minBX + lx;
            if (Math.abs(x - centerX) > reach) {
                continue;
            }
            final int xsq = MHelper.sqr(x - centerX);
            for (int lz = 0; lz < 16; lz++) {
                final int z = minBZ + lz;
                if (Math.abs(z - centerZ) > reach) {
                    continue;
                }
                final int zsq = MHelper.sqr(z - centerZ);
                final int dxz = xsq + zsq;
                // Keep a solid roof over the cavern: never carve within SURFACE_ROOF blocks of this
                // column's surface, so a large cavern can't breach the island top and open a sky pit.
                final int colTop = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, lx, lz) - 1 - SURFACE_ROOF;
                final int yTop = Math.min(y2, colTop);
                for (int y = y1; y <= yTop; y++) {
                    final int ysq = (int) MHelper.sqr((y - centerY) * squash);
                    final double r = noise.eval(x * 0.1, y * 0.1, z * 0.1) * nr + hr;
                    final double dist = dxz + ysq;
                    if (dist < r * r) {
                        pos.set(x, y, z);
                        final BlockState state = chunk.getBlockState(pos);
                        if (isReplaceable(cfg, state) && !isWaterNear(chunk, cp, pos, neighbor, minGenY, maxGenY)) {
                            chunk.setBlockState(pos, CAVE_AIR);
                            mask.set(lx, y, lz);
                            carvedPositions.add(BlockPos.asLong(x, y, z));
                            carved = true;
                        }
                    }
                }
            }
        }

        // Coat the exposed End-stone faces of the blocks THIS invocation carved with the per-column cave
        // biome's materials. The wall-shell draws are the only random draws made here and happen strictly
        // after every geometry draw above, so the cavern shape is unaffected (see CaveSurfaceCoater).
        CaveSurfaceCoater.coat(context, chunk, biomeGetter, random, carvedPositions);

        return carved;
    }

    private static boolean isReplaceable(EndCaveCarverConfiguration cfg, BlockState state) {
        return state.is(cfg.replaceable)
                || state.is(CommonBlockTags.END_STONES)
                || BlocksHelper.replaceableOrPlant(state)
                || state.is(BlockTags.LEAVES);
    }

    /**
     * Legacy {@code isWaterNear} parity, but clamped to the chunk currently being carved: any of the
     * six neighbours whose block leaves this chunk's X/Z or the gen-height bounds is simply skipped
     * (a carver may not read other chunks). The End disables aquifers, so this is defensive.
     */
    static boolean isWaterNear(
            ChunkAccess chunk,
            ChunkPos cp,
            BlockPos pos,
            BlockPos.MutableBlockPos neighbor,
            int minGenY,
            int maxGenY
    ) {
        for (Direction dir : Direction.values()) {
            final int nx = pos.getX() + dir.getStepX();
            final int ny = pos.getY() + dir.getStepY();
            final int nz = pos.getZ() + dir.getStepZ();
            if (ny < minGenY || ny > maxGenY) {
                continue;
            }
            if (SectionPos.blockToSectionCoord(nx) != cp.x || SectionPos.blockToSectionCoord(nz) != cp.z) {
                continue;
            }
            neighbor.set(nx, ny, nz);
            if (!chunk.getFluidState(neighbor).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
