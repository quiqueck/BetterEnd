package org.betterx.betterend.world.structures.piece;

import org.betterx.bclib.api.v2.levelgen.structures.StructureWorld;
import org.betterx.betterend.registry.EndStructures;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.function.Consumer;

public class VoxelPiece extends BasePiece {
    private StructureWorld world;

    public VoxelPiece(Consumer<StructureWorld> function, int id) {
        super(EndStructures.VOXEL_PIECE, id, null);
        world = new StructureWorld();
        function.accept(world);
        this.boundingBox = world.getBounds();
    }

    public VoxelPiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.VOXEL_PIECE, tag);
        this.boundingBox = world.getBounds();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("world", world.toBNT());
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        world = new StructureWorld(tag.getCompoundOrEmpty("world"));
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
        this.world.placeChunk(world, chunkPos);
    }
}
