package org.betterx.betterend.world.features.terrain.caves;

import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import org.jetbrains.annotations.Nullable;

/**
 * Scatters single-stalactite sub-features across a radial disc, dripstone-style: each column inside
 * the disc rolls against a density that falls off toward the edge, and a column that hits may place a
 * floor stalagmite, a ceiling stalactite, or (with {@code pairChance}) both — mirroring vanilla
 * {@code DripstoneClusterFeature}. Columns outside the origin chunk are skipped so the feature stays
 * chunk-safe, consistent with the End cave carver conventions.
 */
public class StalactiteClusterFeature extends Feature<StalactiteClusterConfig> {
    private static final int SEARCH_RANGE = 32;

    public StalactiteClusterFeature() {
        super(StalactiteClusterConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<StalactiteClusterConfig> ctx) {
        final StalactiteClusterConfig cfg = ctx.config();
        final RandomSource random = ctx.random();
        final BlockPos origin = ctx.origin();
        final WorldGenLevel world = ctx.level();
        final ChunkGenerator generator = ctx.chunkGenerator();

        final int r = Math.max(1, cfg.radius().sample(random));
        final float density = cfg.density().sample(random);
        final int sx = (origin.getX() >> 4) << 4;
        final int sz = (origin.getZ() >> 4) << 4;

        boolean placed = false;
        final MutableBlockPos mut = new MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                final int distSq = dx * dx + dz * dz;
                if (distSq > r * r) {
                    continue;
                }
                final int wx = origin.getX() + dx;
                final int wz = origin.getZ() + dz;
                // Chunk-safe: skip columns outside the origin chunk.
                if (wx < sx || wx > sx + 15 || wz < sz || wz > sz + 15) {
                    continue;
                }

                final double dist = Math.sqrt(distSq);
                final float falloff = (float) (1.0 - (dist / r) * (dist / r));
                if (random.nextFloat() >= density * falloff) {
                    continue;
                }

                final BlockPos floorPos = findFloor(world, wx, origin.getY(), wz, mut);
                final BlockPos ceilPos = findCeiling(world, wx, origin.getY(), wz, mut);
                if (floorPos == null && ceilPos == null) {
                    continue;
                }

                if (floorPos != null) {
                    placed |= cfg.floorFeature().value().place(world, generator, random, floorPos);
                }
                if (ceilPos != null && random.nextFloat() < cfg.pairChance()) {
                    placed |= cfg.ceilFeature().value().place(world, generator, random, ceilPos);
                }
            }
        }

        return placed;
    }

    /**
     * Scans downward from {@code startY}; the first non-air block must be End-stone-tagged, in which
     * case the floor position (the air block resting on it) is returned.
     */
    @Nullable
    private BlockPos findFloor(WorldGenLevel world, int wx, int startY, int wz, MutableBlockPos mut) {
        for (int i = 0; i <= SEARCH_RANGE; i++) {
            final int y = startY - i;
            mut.set(wx, y, wz);
            BlockState state = world.getBlockState(mut);
            if (!state.isAir()) {
                return state.is(CommonBlockTags.END_STONES) ? new BlockPos(wx, y + 1, wz) : null;
            }
        }
        return null;
    }

    /**
     * Scans upward from {@code startY}; the first non-air block must be End-stone-tagged, in which
     * case the ceiling position (the air block hanging under it) is returned.
     */
    @Nullable
    private BlockPos findCeiling(WorldGenLevel world, int wx, int startY, int wz, MutableBlockPos mut) {
        for (int i = 0; i <= SEARCH_RANGE; i++) {
            final int y = startY + i;
            mut.set(wx, y, wz);
            BlockState state = world.getBlockState(mut);
            if (!state.isAir()) {
                return state.is(CommonBlockTags.END_STONES) ? new BlockPos(wx, y - 1, wz) : null;
            }
        }
        return null;
    }
}
