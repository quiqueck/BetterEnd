package org.betterx.betterend.world.structures.piece;

import org.betterx.bclib.api.v2.levelgen.structures.StructureWorld;
import org.betterx.betterend.registry.EndStructures;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.EnumSet;
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
        if (this.world.placeChunk(world, chunkPos)) {
            // StructureWorld writes through ChunkAccess.setBlockState. During the FEATURES generation
            // step, however, the ProtoChunk only refreshes its FINAL heightmaps (WORLD_SURFACE,
            // OCEAN_FLOOR, MOTION_BLOCKING...) - never the *_WG worldgen heightmaps. Those were primed
            // as "void" right after the NOISE step (these SDF structures build in the open void, where
            // no base terrain exists) and are never touched again. Downstream LAKES/VEGETAL decorators
            // probe the *_WG heightmaps - PondWithWaterfallFeature reads WORLD_SURFACE_WG for its floor,
            // and heightmap-anchored plant placement uses them too - so every one of them saw an empty
            // column above our freshly written island and bailed out ("no terrain"), which is why no
            // ponds, waterfalls or plants ever appeared on the islands. Re-prime the WG heightmaps from
            // the now-populated chunk sections so those features find the island surface. This lives in
            // VoxelPiece so every SDF structure (islands, giant ice stars, glowshrooms) benefits.
            ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
            Heightmap.primeHeightmaps(
                    chunk,
                    EnumSet.of(Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.OCEAN_FLOOR_WG)
            );
        }
    }
}
