package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.bclib.blocks.BasePlantWithAgeBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class LanceleafFeature extends ScatterFeature<ScatterFeatureConfig> {
    public LanceleafFeature() {
        super(ScatterFeatureConfig.CODEC);
    }

    @Override
    public boolean canGenerate(
            ScatterFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos center,
            BlockPos blockPos,
            float radius
    ) {
        //noinspection deprecation
        return EndPlantBlocks.LANCELEAF_SEED.defaultBlockState().canSurvive(world, blockPos);
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        BasePlantWithAgeBlock seed = ((BasePlantWithAgeBlock) EndPlantBlocks.LANCELEAF_SEED);
        seed.growAdult(world, random, blockPos);
    }

    @Override
    protected int getChance() {
        return 5;
    }
}
