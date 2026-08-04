package org.betterx.betterend.world.structures.features;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.piece.EndLakePiece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

/**
 * The per-chunk End lake structure. Replaces the old {@code EndLakeFeature} (a decoration feature
 * that carved across chunk boundaries and stranded trees over the water). Because a structure places
 * its piece during the chunk's own {@code LAKES} step - which runs before that same chunk's
 * {@code VEGETAL_DECORATION} (trees) - the ground is flooded before any tree can grow on it, so trees
 * never end up floating. This is the same pattern the working {@code MegaLakeStructure} uses.
 * <p>
 * Three registrations share this logic ({@code END_LAKE}, {@code END_LAKE_NORMAL},
 * {@code END_LAKE_RARE}); only their structure-set spacing (and hence density) and {@link #type()}
 * differ. The normal/rare variants are trivial subclasses that override {@link #type()}.
 */
public class EndLakeStructure extends FeatureBaseStructure {
    public EndLakeStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public StructureType<?> type() {
        return EndStructures.END_LAKE.type();
    }

    @Override
    protected void generatePieces(StructurePiecesBuilder structurePiecesBuilder, Structure.GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final RandomState rState = context.randomState();
        final LevelHeightAccessor levelHeightAccessor = context.heightAccessor();

        final int x = chunkPos.getBlockX(MHelper.randRange(4, 12, random));
        final int z = chunkPos.getBlockZ(MHelper.randRange(4, 12, random));
        final int y = chunkGenerator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG, levelHeightAccessor, rState);

        // Same guard as the old EndLakeFeature: nothing below the void floor.
        if (y < 10) return;

        final float radius = (float) MHelper.randRange(10.0, 20.0, random);
        final float depth = (float) (radius * 0.5 * MHelper.randRange(0.8, 1.2, random));
        final int dist = MHelper.floor(radius);

        // Flatness gate, ported from EndLakeFeature: bail if the surrounding terrain (sampled at
        // +/- dist on each axis) differs from the centre by more than 5 blocks, and use the lowest
        // sampled surface as the water level. getBaseHeight is the structure-safe equivalent of the
        // feature's getPosOnSurfaceRaycast.
        int waterLevel = y;
        final int[][] offsets = {{-dist, 0}, {dist, 0}, {0, -dist}, {0, dist}};
        for (int[] o : offsets) {
            int h = chunkGenerator.getBaseHeight(x + o[0], z + o[1], Types.WORLD_SURFACE_WG, levelHeightAccessor, rState);
            if (Math.abs(y - h) > 5) return;
            waterLevel = Math.min(waterLevel, h);
        }

        // Each lake keeps its own (flatness-min) water level; overlapping bowls are kept from floating
        // over one another by the piece's downward grounding, not by merging water levels.
        final Holder<Biome> biome = getNoiseBiome(chunkGenerator, rState, x >> 2, y >> 2, z >> 2);
        final EndLakePiece piece = new EndLakePiece(new BlockPos(x, y, z), waterLevel, radius, depth, random, biome);
        structurePiecesBuilder.addPiece(piece);
    }
}
