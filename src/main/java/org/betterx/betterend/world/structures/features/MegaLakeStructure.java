package org.betterx.betterend.world.structures.features;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.piece.LakePiece;

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

public class MegaLakeStructure extends FeatureBaseStructure {


    public MegaLakeStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public StructureType<MegaLakeStructure> type() {
        return EndStructures.MEGALAKE.type();
    }

    protected void generatePieces(StructurePiecesBuilder structurePiecesBuilder, Structure.GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final RandomState rState = context.randomState();

        final LevelHeightAccessor levelHeightAccessor = context.heightAccessor();

        int x = chunkPos.getBlockX(MHelper.randRange(4, 12, random));
        int z = chunkPos.getBlockZ(MHelper.randRange(4, 12, random));
        int y = chunkGenerator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG, levelHeightAccessor, rState);

        if (y > 5) {
            Holder<Biome> biome = getNoiseBiome(chunkGenerator, rState, x >> 2, y >> 2, z >> 2);

            float radius = MHelper.randRange(32, 64, random);
            float depth = MHelper.randRange(7, 15, random);
            // Support gate: sample the terrain at the four lake "corners" (radius offsets on each
            // axis). If any of them lies more than 6 blocks below the water level, the lake would
            // hang over a drop and its shores could not reach the ground - reject the placement.
            final int dist = MHelper.floor(radius);
            final int[][] corners = {{-dist, 0}, {dist, 0}, {0, -dist}, {0, dist}};
            for (int[] o : corners) {
                int h = chunkGenerator.getBaseHeight(x + o[0], z + o[1], Types.WORLD_SURFACE_WG, levelHeightAccessor, rState);
                if (h < y - 6) return;
            }
            // Each lake keeps its own water level; overlapping bowls are kept from floating over one
            // another by the piece's downward grounding, not by merging water levels.
            LakePiece piece = new LakePiece(new BlockPos(x, y, z), radius, depth, random, biome, y);
            structurePiecesBuilder.addPiece(piece);
        }
    }

}
