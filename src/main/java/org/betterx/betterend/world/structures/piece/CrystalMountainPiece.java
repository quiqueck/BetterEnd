package org.betterx.betterend.world.structures.piece;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.CrystalMossCoverBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.util.GlobalState;
import org.betterx.betterend.world.surface.SplitNoiseCondition;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
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

import java.util.Arrays;

public class CrystalMountainPiece extends MountainPiece {
    private BlockState top;

    public CrystalMountainPiece(BlockPos center, float radius, float height, RandomSource random, Holder<Biome> biome) {
        super(EndStructures.MOUNTAIN_PIECE, center, radius, height, random, biome);
        top = EndBlocks.CRYSTAL_MOSS.defaultBlockState(); //EndBiome.findTopMaterial(biome.value()); //biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
    }

    public CrystalMountainPiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.MOUNTAIN_PIECE, tag);
        top = EndBlocks.CRYSTAL_MOSS.defaultBlockState();
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
        int sx = chunkPos.getMinBlockX();
        int sz = chunkPos.getMinBlockZ();
        final MutableBlockPos pos = GlobalState.stateForThread().POS;
        ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
        Heightmap map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);
        // y of the crystal moss cover above each column (or MIN_VALUE for none). The cover is placed in a second
        // pass once all columns of this chunk are built, so it sees the final neighbour columns.
        final int[] coverY = new int[256];
        Arrays.fill(coverY, Integer.MIN_VALUE);
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
                    while (!chunk.getBlockState(pos).is(CommonBlockTags.GEN_END_STONES)
                            && pos.getY() > 56
                            && chunk.getBlockState(pos.below()).is(Blocks.CAVE_AIR)
                    ) {
                        pos.setY(pos.getY() - 1);
                    }
                    minY = pos.getY();
                    minY = Math.max(minY, getWorldgenSurface(world, px, pz));
                    if (minY > center.getY() - 8) {
                        float maxY = dist * height * getHeightClamp(world, 12, px, pz);
                        if (maxY > 0) {
                            maxY *= (float) noise1.eval(px * 0.05, pz * 0.05) * 0.3F + 0.7F;
                            maxY *= (float) noise1.eval(px * 0.1, pz * 0.1) * 0.1F + 0.8F;
                            maxY += center.getY();
                            int maxYI = (int) (maxY);
                            int cover = maxYI - 1;

                            final double noise = SplitNoiseCondition.DEFAULT.getNoise(px, pz);
                            boolean needCover = noise > 0;
                            boolean needSurroundCover = Math.abs(noise) < 0.2;
                            if (needSurroundCover && maxYI > minY - 1) {
                                coverY[(x << 4) | z] = maxYI;
                            }
                            for (int y = minY - 1; y < maxYI; y++) {
                                pos.setY(y);
                                if (needCover && y == cover) {
                                    chunk.setBlockState(pos, top, false);
                                } else {
                                    chunk.setBlockState(pos, Blocks.END_STONE.defaultBlockState(), false);
                                }
                            }
                        }
                    }
                }
            }
        }

        placeMossCover(chunk, coverY);

        map = chunk.getOrCreateHeightmapUnprimed(Types.WORLD_SURFACE);

        // Big crystals
        int count = (map.getFirstAvailable(8, 8) - (center.getY() + 24)) / 7;
        count = Mth.clamp(count, 0, 8);
        for (int i = 0; i < count; i++) {
            int radius = MHelper.randRange(2, 3, random);
            float fill = MHelper.randRange(0F, 1F, random);
            int x = MHelper.randRange(radius, 15 - radius, random);
            int z = MHelper.randRange(radius, 15 - radius, random);
            int y = map.getFirstAvailable(x, z);
            if (y > 60) {
                pos.set(x, y, z);
                if (chunk.getBlockState(pos.below()).is(Blocks.END_STONE)) {
                    int height = MHelper.floor(radius * MHelper.randRange(1.5F, 3F, random) + (y - 80) * 0.3F);
                    crystal(chunk, pos, radius, height, fill, random);
                }
            }
        }

        // Small crystals
        count = (map.getFirstAvailable(8, 8) - (center.getY() + 24)) / 2;
        count = Mth.clamp(count, 4, 8);
        for (int i = 0; i < count; i++) {
            int radius = MHelper.randRange(1, 2, random);
            float fill = random.nextBoolean() ? 0 : 1;
            int x = MHelper.randRange(radius, 15 - radius, random);
            int z = MHelper.randRange(radius, 15 - radius, random);
            int y = map.getFirstAvailable(x, z);
            if (y > 20) {
                pos.set(x, y, z);
                if (chunk.getBlockState(pos.below()).getBlock() == Blocks.END_STONE) {
                    int height = MHelper.floor(radius * MHelper.randRange(1.5F, 3F, random) + (y - 80) * 0.3F);
                    crystal(chunk, pos, radius, height, fill, random);
                }
            }
        }
    }

    /**
     * Places the crystal moss cover on top of the columns collected in {@code coverY} and attaches it to every
     * neighbouring end stone. Only neighbours inside this chunk are checked: {@link ChunkAccess#getBlockState} wraps
     * local coordinates outside 0..15 back into the same chunk, which attached the cover to blocks on the opposite
     * side of the chunk and left floating moss walls along chunk borders.
     */
    private void placeMossCover(ChunkAccess chunk, int[] coverY) {
        final MutableBlockPos above = new MutableBlockPos();
        final MutableBlockPos side = new MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int y = coverY[(x << 4) | z];
                if (y == Integer.MIN_VALUE) continue;
                above.set(x, y, z);
                if (!chunk.getBlockState(above).is(Blocks.AIR)) continue;

                BlockState coverState = EndBlocks.CRYSTAL_MOSS_COVER.defaultBlockState();
                boolean didChange = false;
                for (Direction dir : Direction.values()) {
                    side.setWithOffset(above, dir);
                    if (side.getX() < 0 || side.getX() > 15 || side.getZ() < 0 || side.getZ() > 15) continue;
                    if (chunk.getBlockState(side).is(CommonBlockTags.END_STONES)) {
                        coverState = coverState.setValue(CrystalMossCoverBlock.getFaceProperty(dir), true);
                        didChange = true;
                    }
                }
                if (didChange) chunk.setBlockState(above, coverState, false);
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
                                chunk.setBlockState(mut, EndBlocks.AURORA_CRYSTAL.defaultBlockState(), false);
                            }
                        }
                    }
                }
            }
        }
    }
}
