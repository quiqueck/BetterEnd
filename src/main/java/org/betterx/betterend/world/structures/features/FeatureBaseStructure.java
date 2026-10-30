package org.betterx.betterend.world.structures.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import org.betterx.betterend.util.WorldgenDebug;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public abstract class FeatureBaseStructure extends Structure {
    protected static final BlockState AIR = Blocks.AIR.defaultBlockState();

    /** Generation points whose lowest sampled surface is below this have no terrain to build on. */
    private static final int MIN_TERRAIN_Y = 10;

    // Every BetterEnd structure type asks for the same surface height of every chunk, and with a
    // density-function End (for example Stellarity's) each height is a full noise column evaluation. The
    // answer only depends on the RandomState and the position, so it is shared between structure types.
    private static final int HEIGHT_CACHE_SIZE = 16384;
    private static final Map<RandomState, Map<Long, Integer>> HEIGHT_CACHE =
            Collections.synchronizedMap(new WeakHashMap<>());

    private static int getCachedFirstOccupiedHeight(
            int x,
            int z,
            ChunkGenerator chunkGenerator,
            LevelHeightAccessor levelHeightAccessor,
            RandomState rState
    ) {
        final Map<Long, Integer> heights = HEIGHT_CACHE.computeIfAbsent(
                rState,
                k -> Collections.synchronizedMap(new LinkedHashMap<>(256, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
                        return size() > HEIGHT_CACHE_SIZE;
                    }
                })
        );
        final long key = ((long) x << 32) | (z & 0xFFFFFFFFL);
        final Integer known = heights.get(key);
        if (known != null) return known;

        final int height = chunkGenerator.getFirstOccupiedHeight(
                x, z, Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor, rState
        );
        heights.put(key, height);
        return height;
    }

    public FeatureBaseStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!mayStartInBiome(context, anchorColumn(context.chunkPos()))) {
            WorldgenDebug.log(
                    "%s: generation point SKIPPED (biome not valid, height not sampled) at chunk %s",
                    getClass().getSimpleName(), context.chunkPos()
            );
            return Optional.empty();
        }
        BlockPos pos = getGenerationHeight(
                context.chunkPos(),
                context.chunkGenerator(),
                context.heightAccessor(),
                context.randomState()
        );
        if (pos.getY() >= MIN_TERRAIN_Y) {
            WorldgenDebug.log(
                    "%s: generation point ACCEPTED at %s (chunk %s)",
                    getClass().getSimpleName(), pos, context.chunkPos()
            );
            return Optional.of(new Structure.GenerationStub(pos, (structurePiecesBuilder) -> {
                generatePieces(structurePiecesBuilder, context);
            }));
        }
        WorldgenDebug.log(
                "%s: generation point REJECTED (sampled surface y=%d < 10, i.e. no terrain) at chunk %s",
                getClass().getSimpleName(), pos.getY(), context.chunkPos()
        );
        return Optional.empty();
    }

    /**
     * Generation point for structures that build SELF-SUPPORTING shapes in the open void (SDF
     * islands, giant ice stars): no terrain gate, because their whole purpose is to generate where
     * nothing exists - the default {@link #findGenerationPoint}'s y >= 10 terrain gate would reject
     * exactly the chunks they are meant for. The stub sits at the chunk centre at {@code stubY};
     * the structure's biome check at that position still applies and is the only gate needed.
     */
    protected Optional<GenerationStub> findVoidGenerationPoint(GenerationContext context, int stubY) {
        final BlockPos pos = new BlockPos(
                context.chunkPos().getBlockX(8),
                stubY,
                context.chunkPos().getBlockZ(8)
        );
        WorldgenDebug.log(
                "%s: void generation point at %s (chunk %s)",
                getClass().getSimpleName(), pos, context.chunkPos()
        );
        return Optional.of(new Structure.GenerationStub(pos, (structurePiecesBuilder) -> {
            generatePieces(structurePiecesBuilder, context);
        }));
    }

    protected Holder<Biome> getNoiseBiome(ChunkGenerator cg, RandomState rState, int i, int j, int k) {
        return cg.getBiomeSource().getNoiseBiome(i, j, k, rState.sampler());
    }

    protected abstract void generatePieces(
            StructurePiecesBuilder structurePiecesBuilder,
            Structure.GenerationContext context
    );

    /**
     * The column a chunk's generation point is measured at: a corner of the 2x2 footprint around block 7 of the
     * chunk, picked by the chunk's seeded rotation. Only x and z are meaningful.
     */
    private static BlockPos anchorColumn(ChunkPos chunkPos) {
        LegacyRandomSource random = new LegacyRandomSource(chunkPos.x() + chunkPos.z() * 10387313);
        Rotation blockRotation = Rotation.getRandom(random);

        int offsetX = 5;
        int offsetZ = 5;
        if (blockRotation == Rotation.CLOCKWISE_90) {
            offsetX = -5;
        } else if (blockRotation == Rotation.CLOCKWISE_180) {
            offsetX = -5;
            offsetZ = -5;
        } else if (blockRotation == Rotation.COUNTERCLOCKWISE_90) {
            offsetZ = -5;
        }

        int blockX = chunkPos.getBlockX(7);
        int blockZ = chunkPos.getBlockZ(7);

        return new BlockPos(chunkPos.getBlockX(7) + offsetX, 0, chunkPos.getBlockZ(7) + offsetZ);
    }

    /**
     * Whether this structure may start in the biome at the generation column. Vanilla only asks that after
     * {@link #findGenerationPoint} has returned, but by then the height scan is paid for. Over the void, which
     * is where most candidates are rejected, that scan runs the whole density function down an empty column.
     * The biome is probed at the top of the world, above the islands, so cave biomes cannot hide the surface
     * biome; the later vanilla check at the real height still applies.
     */
    private static boolean mayStartInBiome(GenerationContext context, BlockPos anchor) {
        return context.validBiome().test(context.chunkGenerator().getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(anchor.getX()),
                QuartPos.fromBlock(context.heightAccessor().getMaxY()),
                QuartPos.fromBlock(anchor.getZ()),
                context.randomState().sampler()
        ));
    }

    // Package-visible so LakeWaterLevels can replicate the generation-point gate for neighbouring
    // megalake placements without duplicating this logic.
    static BlockPos getGenerationHeight(
            ChunkPos chunkPos,
            ChunkGenerator chunkGenerator,
            LevelHeightAccessor levelHeightAccessor,
            RandomState rState
    ) {
        final BlockPos anchor = anchorColumn(chunkPos);

        // One height sample at the anchor corner of the footprint. Earlier versions sampled all four corners
        // for a "lowest corner wins" test whose minimum was never updated, so only the last corner ever
        // counted and the other three samples were wasted. The footprint is not guarded against island edges
        // here: a sample is a full noise column scan, and with a density-function End (for example
        // Stellarity's) several hundred of them per chunk's structure range block the server for over a minute.
        final int x = anchor.getX();
        final int z = anchor.getZ();
        return new BlockPos.MutableBlockPos(
                x,
                getCachedFirstOccupiedHeight(x, z, chunkGenerator, levelHeightAccessor, rState),
                z
        );
    }
}
