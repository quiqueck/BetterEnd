package org.betterx.betterend.world.structures.piece;

import org.betterx.betterend.blocks.EndLilyBlock;
import org.betterx.betterend.blocks.EndLotusSeedBlock;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import org.jetbrains.annotations.Nullable;

/**
 * The underwater life a lake piece grows itself: charnia, end lotus, wet menger sponges, bubble coral, pond
 * anemones and end lilies, at the density a megalake had when its biome's vegetation still reached its floor.
 * <p>
 * Why the piece has to do this: since cave biomes are real vertical biomes, everything below the cave band
 * (about y 40-56) belongs to a cave biome. A lake floor deeper than the band top is therefore not in the lake's
 * biome any more, and the biome's own underwater features - which check the biome where they are placed - skip
 * it. Measured on seed 391391, a megalake whose floor lies in the band had 0.26 water plants per lake column
 * against 0.58 on 1.20.1 (and almost no lotus). So the life is placed here exactly where the biome's features
 * cannot reach: columns whose floor is not in the lake's biome (or every column, for a lake whose biome has no
 * underwater features of its own, e.g. a lake placed into the plain End). Columns whose floor is still in a
 * lake biome get only a small top-up on top of the biome's features.
 * <p>
 * Rates per lake column are those measured on 1.20.1 megalakes. Every decision is a hash of the column's
 * position (no chunk random), clustered a little by a low-frequency noise, and every block is written into the
 * chunk being generated: deterministic, and independent of the order chunks are generated in. End lotus also
 * places its leaves next to the stem, so it is only grown at least two columns away from the chunk border.
 */
final class LakeFloorLife {
    private LakeFloorLife() {
    }

    private static final OpenSimplexNoise CLUSTER = new OpenSimplexNoise(0x1A4EF100L);

    // Chance per lake column, before the cluster factor (x0.2 .. x1.8, mean about 1). Tuned so that a lake whose
    // floor lies in the cave band reaches the 1.20.1 density (lotus is weighted up because it only grows
    // away from chunk borders and in water at least two blocks deep).
    private static final double LIFE_PER_COLUMN = 0.7;
    // Extra chance on columns whose floor the biome's own features already plant, on top of those.
    private static final double BIOME_TOPUP = 0.2;
    // Plants and their weights, proportional to their measured share on 1.20.1 megalakes.
    private static BlockState[] plants;
    private static int[] weights;
    private static final int LOTUS = -1;

    private static void init() {
        if (plants != null) return;
        weights = new int[]{115, 110, 21, 12, 6, 6, 16, 11, 4, 2, 40};
        plants = new BlockState[]{
                EndWaterPlantBlocks.CHARNIA_CYAN.defaultBlockState(),
                EndWaterPlantBlocks.CHARNIA_LIGHT_BLUE.defaultBlockState(),
                EndWaterPlantBlocks.CHARNIA_RED.defaultBlockState(),
                EndWaterPlantBlocks.CHARNIA_GREEN.defaultBlockState(),
                EndWaterPlantBlocks.CHARNIA_PURPLE.defaultBlockState(),
                EndWaterPlantBlocks.CHARNIA_ORANGE.defaultBlockState(),
                EndDecorBlocks.MENGER_SPONGE_WET.defaultBlockState(),
                EndWaterPlantBlocks.BUBBLE_CORAL.defaultBlockState(),
                EndWaterPlantBlocks.POND_ANEMONE.defaultBlockState(),
                EndWaterPlantBlocks.END_LILY.defaultBlockState(),
                null // end lotus, grown from its seed
        };
    }

    private static boolean isLakeBiome(Holder<Biome> holder) {
        return holder.unwrapKey().map(k -> k.identifier().getPath().startsWith("megalake")).orElse(false);
    }

    /**
     * Grows the life on every open water column of this chunk whose surface water block is at {@code surfaceY}.
     *
     * @param lakeBiome the biome the lake piece belongs to
     * @param seed      the piece's own seed (decorrelates neighbouring lakes)
     */
    static void place(
            WorldGenLevel world,
            ChunkAccess chunk,
            int sx,
            int sz,
            int surfaceY,
            @Nullable ResourceKey<Biome> lakeBiome,
            long seed
    ) {
        init();
        final MutableBlockPos mut = new MutableBlockPos();
        final BlockState lily = EndWaterPlantBlocks.END_LILY.defaultBlockState();
        int total = 0;
        for (int w : weights) total += w;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int wx = sx + x;
                final int wz = sz + z;
                mut.set(x, surfaceY, z);
                if (!chunk.getBlockState(mut).is(Blocks.WATER) || !chunk.getFluidState(mut).isSource()) continue;
                mut.setY(surfaceY + 1);
                if (!chunk.getBlockState(mut).isAir()) continue;   // not the open surface of this lake
                int y = surfaceY;
                while (y > chunk.getMinY() + 1) {
                    mut.setY(y - 1);
                    if (!chunk.getBlockState(mut).is(Blocks.WATER)) break;
                    y--;
                }
                // y: lowest water block. Leave the column to the biome's own features if they reach this floor.
                final Holder<Biome> floorBiome = chunk.getNoiseBiome(
                        QuartPos.fromBlock(wx), QuartPos.fromBlock(y), QuartPos.fromBlock(wz));
                final boolean biomeCovers = isLakeBiome(floorBiome) && lakeBiome != null && floorBiome.is(lakeBiome);

                final double cluster = Mth.clamp(CLUSTER.eval(wx * 0.05, wz * 0.05) * 0.8 + 1.0, 0.2, 1.8);
                final double rate = biomeCovers ? BIOME_TOPUP : LIFE_PER_COLUMN;
                if (hash01(wx, wz, seed, 0xA11CEL) >= rate * cluster) continue;
                double pick = hash01(wx, wz, seed, 0xF100L) * total;
                int choice = 0;
                while (choice < weights.length - 1 && (pick -= weights[choice]) >= 0) choice++;
                final BlockState plant = plants[choice];
                final BlockPos at = new BlockPos(wx, y, wz);
                final int depth = surfaceY - y + 1;
                mut.setY(y);
                if (plant == null) {
                    // End lotus: stem from the floor, leaves on the surface next to it, a flower on top.
                    if (depth < 2 || x < 2 || x > 13 || z < 2 || z > 13) continue;
                    final EndLotusSeedBlock seedBlock = (EndLotusSeedBlock) EndWaterPlantBlocks.END_LOTUS_SEED;
                    seedBlock.grow(world, RandomSource.create(hash64(wx, wz, seed, 0x1075L)), at);
                } else if (plant == lily) {
                    if (depth < 2) continue;
                    final BlockState bottom = lily.setValue(EndLilyBlock.SHAPE, TripleShape.BOTTOM);
                    if (!bottom.canSurvive(world, at)) continue;
                    BasePiece.setChunkBlock(chunk, mut, bottom);
                    for (int yy = y + 1; yy <= surfaceY; yy++) {
                        BasePiece.setChunkBlock(chunk, mut.setY(yy), lily.setValue(EndLilyBlock.SHAPE, TripleShape.MIDDLE));
                    }
                    BasePiece.setChunkBlock(chunk, mut.setY(surfaceY + 1), lily.setValue(EndLilyBlock.SHAPE, TripleShape.TOP));
                } else if (plant.is(EndDecorBlocks.MENGER_SPONGE_WET)) {
                    if (!world.getBlockState(at.below()).is(CommonBlockTags.END_STONES)) continue;
                    BasePiece.setChunkBlock(chunk, mut, plant);
                } else {
                    if (!plant.canSurvive(world, at)) continue;
                    BasePiece.setChunkBlock(chunk, mut, plant);
                }
            }
        }
    }

    private static long hash64(int wx, int wz, long seed, long salt) {
        long h = (long) wx * 0x9E3779B97F4A7C15L ^ (long) wz * 0xC2B2AE3D27D4EB4FL ^ (seed + salt) * 0x165667B19E3779F9L;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        return h;
    }

    private static double hash01(int wx, int wz, long seed, long salt) {
        return (hash64(wx, wz, seed, salt) >>> 11) * 0x1.0p-53;
    }
}
