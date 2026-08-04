package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.blocks.EndLotusSeedBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class EndLotusFeature extends UnderwaterPlantScatter<ScatterFeatureConfig> {
    public EndLotusFeature() {
        super(ScatterFeatureConfig.CODEC);
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        EndLotusSeedBlock seed = (EndLotusSeedBlock) EndWaterPlantBlocks.END_LOTUS_SEED;
        seed.grow(world, random, blockPos);
    }

    @Override
    protected int getChance() {
        return 15;
    }
}
