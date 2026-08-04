package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.blocks.EndLilySeedBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class EndLilyFeature extends UnderwaterPlantScatter<ScatterFeatureConfig> {
    public EndLilyFeature() {
        super(ScatterFeatureConfig.CODEC);
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        EndLilySeedBlock seed = (EndLilySeedBlock) EndWaterPlantBlocks.END_LILY_SEED;
        seed.grow(world, random, blockPos);
    }

    @Override
    protected int getChance() {
        return 15;
    }
}
