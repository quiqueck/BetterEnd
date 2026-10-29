package org.betterx.betterend.world.structures.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class BasePiece extends StructurePiece {
    protected BasePiece(StructurePieceType type, int i, BoundingBox boundingBox) {
        super(type, i, boundingBox);
    }

    protected BasePiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        fromNbt(tag);
    }

    protected abstract void fromNbt(CompoundTag tag);

    /**
     * The first free y above the base terrain at a column, i.e. what {@code WORLD_SURFACE_WG} reports
     * for a chunk that is still being generated - independent of any blocks features or structure pieces
     * have placed since.
     * <p>
     * A chunk's {@code WORLD_SURFACE_WG} heightmap is primed from the noise terrain and stops changing once the
     * chunk reaches the carver stage (from then on block changes update the final heightmaps only), so reading
     * it gives the same answer in every generation order. It is only persisted up to the BIOMES status though:
     * a chunk saved at TERRAIN or later comes back without it, and {@code getOrCreateHeightmapUnprimed} would
     * then hand out an empty heightmap (#556). For such a chunk the generator's noise height - what the
     * heightmap was primed from in the first place - stands in, cached per piece and column because a noise
     * column is expensive.
     * <p>
     * (For a while every call went to the noise height, and then to an 8-block grid of it to keep that fast;
     * the grid put slopes several blocks off, which moved lake shorelines up steps and carved pit ponds.)
     */
    protected int worldgenSurfaceHeight(WorldGenLevel world, int x, int z) {
        final ChunkAccess chunk = world.getChunk(x >> 4, z >> 4);
        if (chunk.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG)) {
            return chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
        }
        final long key = ((long) x << 32) | (z & 0xFFFFFFFFL);
        Integer h = noiseSurface.get(key);
        if (h == null) {
            final ServerChunkCache source = world.getLevel().getChunkSource();
            h = source.getGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, world, source.randomState());
            noiseSurface.put(key, h);
        }
        return h;
    }

    // Noise heights of columns whose chunk has lost its worldgen heightmap; concurrent because a piece is
    // post-processed for several chunks at once on different worker threads.
    private final Map<Long, Integer> noiseSurface = new ConcurrentHashMap<>();

    /**
     * Writes {@code state} straight into {@code chunk} like {@link ChunkAccess#setBlockState}, but also
     * drops the block entity of the block it replaces.
     * <p>
     * {@code ProtoChunk.setBlockState} only swaps the block state - unlike {@code WorldGenRegion.setBlock}
     * it never touches the chunk's block entities. Pieces run in their chunk's own decoration step, which
     * can come after a feature of a neighbouring chunk already wrote into this chunk: the crashed ship
     * ({@code CrashedShipFeature}, SURFACE_STRUCTURES) regularly reaches into a chunk whose mountain piece
     * (RAW_GENERATION) has not run yet. The mountain then buries the ship's dragon head under end stone or
     * an aurora crystal, the {@code SkullBlockEntity} stays behind, and the server logs "Trying to set
     * block entity ... but state ... does not allow" or "Failed to create block entity" once the chunk is
     * promoted or saved. A ship's brewing stand or chests can end up the same way.
     * <p>
     * Several pieces (the mountains among them) address the chunk with chunk-local x/z. The position is
     * converted to a world position first: the chunk's block entities are keyed by it, and a full chunk
     * passes it on to the block's {@code onPlace}.
     */
    protected static void setChunkBlock(ChunkAccess chunk, BlockPos pos, BlockState state) {
        // Always hand the chunk a world position: a full chunk (a structure placed with /place) runs the
        // block's onPlace with the position it is given, and water or plants then scheduled their ticks at
        // the chunk-local position ("Trying to schedule tick in not loaded position").
        final BlockPos worldPos = new BlockPos(
                chunk.getPos().getBlockX(pos.getX() & 15),
                pos.getY(),
                chunk.getPos().getBlockZ(pos.getZ() & 15)
        );
        final BlockState old = chunk.setBlockState(worldPos, state, Block.UPDATE_ALL);
        if (old != null && old.hasBlockEntity() && !old.is(state.getBlock())) {
            chunk.removeBlockEntity(worldPos);
        }
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(
            StructurePieceSerializationContext structurePieceSerializationContext,
            CompoundTag compoundTag
    ) {
        addAdditionalSaveData(compoundTag);
    }
}