package org.betterx.betterend.world.structures.piece;


import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.util.GlobalState;
import org.betterx.betterend.world.surface.SplitNoiseCondition;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class CrystalMountainPiece extends MountainPiece {
    // Chance for a crystal-moss surface column to still grow a crystal. End-stone columns always do
    // (1.0); the moss cover shows through the remaining fraction. Tune here.
    private static final float CRYSTAL_MOSS_CRYSTAL_CHANCE = 0.15F;

    private BlockState top;

    public CrystalMountainPiece(BlockPos center, float radius, float height, RandomSource random, Holder<Biome> biome) {
        super(EndStructures.MOUNTAIN_PIECE, center, radius, height, random, biome);
        top = EndTerrainBlocks.CRYSTAL_MOSS.defaultBlockState(); //EndBiome.findTopMaterial(biome.value()); //biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
    }

    public CrystalMountainPiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.MOUNTAIN_PIECE, tag);
        top = EndTerrainBlocks.CRYSTAL_MOSS.defaultBlockState();
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        super.fromNbt(tag);
        //top = EndBiome.findTopMaterial(BiomeAPI.getBiome(biomeID)); //BiomeAPI.getBiome(biomeID).getBiome().getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
    }

    @Override
    public void postProcess(
            WorldGenLevel world,
            StructureManager arg,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox blockBox,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        final MutableBlockPos pos = GlobalState.stateForThread().POS;
        ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
        Heightmap map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);

        placeMountain(world, chunkPos, chunk, pos);

        // Crystals cover the mountain: EVERY exposed end-stone column grows a crystal, while the
        // crystal-moss columns (placeMountain's noise-driven cover) grow one only
        // CRYSTAL_MOSS_CRYSTAL_CHANCE of the time, so the moss shows through in patches. Historically
        // the mountain cap was end stone and the peaks were fully crystal-covered; scattering a random
        // count of spikes (the old approach) left them mostly bare once the cap became crystal moss.
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = map.getFirstAvailable(x, z);
                if (y <= 20) {
                    continue;
                }
                pos.set(x, y, z);
                BlockState below = chunk.getBlockState(pos.below());
                boolean endStone = below.is(Blocks.END_STONE);
                boolean moss = below.is(EndTerrainBlocks.CRYSTAL_MOSS);
                if (!endStone && !moss) {
                    continue;
                }
                if (moss && random.nextFloat() >= CRYSTAL_MOSS_CRYSTAL_CHANCE) {
                    continue;
                }
                // Mostly small crystals, with occasional larger spikes higher up for variety.
                boolean big = y > 60 && random.nextInt(4) == 0;
                int radius = big ? MHelper.randRange(2, 3, random) : 1;
                float fill = big ? MHelper.randRange(0F, 1F, random) : (random.nextBoolean() ? 0 : 1);
                int height = MHelper.floor(radius * MHelper.randRange(1.5F, 3F, random) + (y - 80) * 0.3F);
                crystal(chunk, pos, radius, height, fill, random);
            }
        }


    }


    private void placeMountain(
            WorldGenLevel world,
            ChunkPos chunkPos,
            ChunkAccess chunk,
            MutableBlockPos pos
    ) {
        Heightmap map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);
        int sx = chunkPos.getMinBlockX();
        int sz = chunkPos.getMinBlockZ();
        Heightmap map2 = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE_WG);
        for (int x = 0; x < 16; x++) {
            int px = x + sx;
            int px2 = px - center.getX();
            px2 *= px2;
            pos.setX(x);
            for (int z = 0; z < 16; z++) {
                int pz = z + sz;
                int pz2 = pz - center.getZ();
                pz2 *= pz2;
                float dist = px2 + pz2;
                if (dist < r2) {
                    pos.setZ(z);
                    dist = 1 - (float) Math.pow(dist / r2, 0.3);
                    int minY = map.getFirstAvailable(x, z);
                    if (minY < 10) {
                        continue;
                    }
                    pos.setY(minY);
                    while (!chunk.getBlockState(pos).is(CommonBlockTags.END_STONES)
                            && pos.getY() > 56
                            && chunk.getBlockState(pos.below()).is(Blocks.CAVE_AIR)
                    ) {
                        pos.setY(pos.getY() - 1);
                    }
                    minY = pos.getY();
                    minY = Math.max(minY, map2.getFirstAvailable(x, z));
                    if (minY > center.getY() - 8) {
                        float maxY = dist * height * getHeightClamp(world, 12, px, pz);
                        if (maxY > 0) {
                            maxY *= (float) noise1.eval(px * 0.05, pz * 0.05) * 0.3F + 0.7F;
                            maxY *= (float) noise1.eval(px * 0.1, pz * 0.1) * 0.1F + 0.8F;
                            maxY += center.getY();
                            int maxYI = (int) (maxY);
                            int cover = maxYI - 1;

                            final double noise = SplitNoiseCondition.DEFAULT.getNoise(px, pz);
                            boolean needCover = noise > -0.2;
//                            boolean needSurroundCover = Math.abs(noise) < 0.2;
//                            BlockPos mossPos;
                            for (int y = minY - 1; y < maxYI; y++) {
                                pos.setY(y);
                                if (needCover && y == cover) {
                                    chunk.setBlockState(pos, top, 3);
                                } else {
                                    chunk.setBlockState(pos, Blocks.END_STONE.defaultBlockState(), 3);
                                }
//                                mossPos = pos.above();
//                                if (needSurroundCover && chunk.getBlockState(mossPos).is(Blocks.AIR)) {
//                                    BlockState coverState = EndPlantBlocks.CRYSTAL_MOSS_COVER
//                                            .defaultBlockState();
//
//                                    boolean didChange = false;
//                                    for (Direction dir : Direction.values()) {
//                                        if (!chunk.getBlockState(mossPos.relative(dir)).is(CommonBlockTags.END_STONES))
//                                            continue;
//
//                                        coverState = coverState.setValue(
//                                                CrystalMossCoverBlock.getFaceProperty(dir),
//                                                true
//                                        );
//                                        didChange = true;
//                                    }
//                                    if (didChange) chunk.setBlockState(mossPos, coverState, 3);
//
//                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void crystal(ChunkAccess chunk, BlockPos pos, int radius, int height, float fill, RandomSource random) {
        MutableBlockPos mut = new MutableBlockPos();
        int max = MHelper.floor(fill * radius + radius + 0.5F);
        height += pos.getY();
        Heightmap map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);
        int coefX = MHelper.randRange(-1, 1, random);
        int coefZ = MHelper.randRange(-1, 1, random);
        for (int x = -radius; x <= radius; x++) {
            mut.setX(x + pos.getX());
            if (mut.getX() >= 0 && mut.getX() < 16) {
                int ax = Math.abs(x);
                for (int z = -radius; z <= radius; z++) {
                    mut.setZ(z + pos.getZ());
                    if (mut.getZ() >= 0 && mut.getZ() < 16) {
                        int az = Math.abs(z);
                        if (ax + az < max) {
                            int minY = map.getFirstAvailable(mut.getX(), mut.getZ()) - MHelper.randRange(3, 7, random);
                            if (pos.getY() - minY > 8) {
                                minY = pos.getY() - 8;
                            }
                            int h = coefX * x + coefZ * z + height;
                            for (int y = minY; y < h; y++) {
                                mut.setY(y);
                                chunk.setBlockState(mut, EndCrystalBlocks.AURORA_CRYSTAL.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }
}
