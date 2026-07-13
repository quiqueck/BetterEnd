package org.betterx.betterend.world.structures.features;

import org.betterx.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.piece.PaintedMountainPiece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class PaintedMountainStructure extends FeatureBaseStructure {
    public static final BlockState[] VARIANTS;

    public PaintedMountainStructure(StructureSettings s) {
        super(s);
    }

    @Override
    public StructureType<PaintedMountainStructure> type() {
        return EndStructures.PAINTED_MOUNTAIN.type();
    }

    protected void generatePieces(StructurePiecesBuilder structurePiecesBuilder, GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final LevelHeightAccessor levelHeightAccessor = context.heightAccessor();
        final RandomState rState = context.randomState();

        int x = chunkPos.getBlockX(MHelper.randRange(4, 12, random));
        int z = chunkPos.getBlockZ(MHelper.randRange(4, 12, random));
        int y = chunkGenerator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG, levelHeightAccessor, rState);
        if (y > 50) {
            Holder<Biome> biome = getNoiseBiome(chunkGenerator, rState, x >> 2, y >> 2, z >> 2);
            float radius = MHelper.randRange(50, 100, random);
            float height = radius * MHelper.randRange(0.4F, 0.6F, random);
            int count = MHelper.floor(height * MHelper.randRange(0.1F, 0.35F, random) + 1);
            BlockState[] slices = new BlockState[count];
            for (int i = 0; i < count; i++) {
                slices[i] = VARIANTS[random.nextInt(VARIANTS.length)];
            }
            HolderLookup<Block> blockHolderLookup = context.registryAccess().lookup(Registries.BLOCK).orElseThrow();
            structurePiecesBuilder.addPiece(new PaintedMountainPiece(
                    new BlockPos(x, y, z),
                    radius,
                    height,
                    random,
                    biome,
                    slices
            ));
        }

        //this.calculateBoundingBox();
    }


    static {
        VARIANTS = new BlockState[]{
                Blocks.END_STONE.defaultBlockState(),
                EndBlocks.FLAVOLITE.getBlock(SlotType.SOURCE).defaultBlockState(),
                EndBlocks.VIOLECITE.getBlock(SlotType.SOURCE).defaultBlockState(),
        };
    }
}
