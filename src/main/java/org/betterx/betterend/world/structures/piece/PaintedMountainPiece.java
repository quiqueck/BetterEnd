package org.betterx.betterend.world.structures.piece;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.util.GlobalState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class PaintedMountainPiece extends MountainPiece {
    private BlockState[] slices;

    public PaintedMountainPiece(
            BlockPos center,
            float radius,
            float height,
            RandomSource random,
            Holder<Biome> biome,
            BlockState[] slices
    ) {
        super(EndStructures.PAINTED_MOUNTAIN_PIECE, center, radius, height, random, biome);
        this.slices = slices;
    }

    public PaintedMountainPiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.PAINTED_MOUNTAIN_PIECE, tag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag slice = new ListTag();
        for (BlockState state : slices) {
            slice.add(NbtUtils.writeBlockState(state));
        }
        tag.put("slises", slice);
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        super.fromNbt(tag);
        final HolderLookup<Block> blockLookup = BuiltInRegistries.BLOCK;
        ListTag slise = tag.getListOrEmpty("slises");
        slices = new BlockState[slise.size()];
        for (int i = 0; i < slices.length; i++) {
            slices[i] = NbtUtils.readBlockState(blockLookup, slise.getCompoundOrEmpty(i));
        }
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
                    dist = 1 - dist / r2;
                    int minY = map.getFirstAvailable(x, z);
                    pos.setY(minY - 1);
                    while (chunk.getBlockState(pos).isAir() && pos.getY() > 50) {
                        pos.setY(minY--);
                    }
                    minY = pos.getY();
                    minY = Math.max(minY, map2.getFirstAvailable(x, z));
                    if (minY > center.getY() - 8) {
                        float maxY = dist * height * getHeightClamp(world, 10, px, pz);
                        if (maxY > 0) {
                            maxY *= (float) noise1.eval(px * 0.05, pz * 0.05) * 0.3F + 0.7F;
                            maxY *= (float) noise1.eval(px * 0.1, pz * 0.1) * 0.1F + 0.9F;
                            maxY += center.getY();
                            float offset = (float) (noise1.eval(px * 0.07, pz * 0.07) * 5 + noise1.eval(
                                    px * 0.2,
                                    pz * 0.2
                            ) * 2 + 7);
                            for (int y = minY - 1; y < maxY; y++) {
                                pos.setY(y);
                                int index = MHelper.floor((y + offset) * 0.65F) % slices.length;
                                chunk.setBlockState(pos, slices[index], 3);
                            }
                        }
                    }
                }
            }
        }
    }
}
