package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.blocks.BasePlantWithAgeBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public class BlueVineFeature extends ScatterFeature<ScatterFeatureConfig> {
    private boolean small;

    public BlueVineFeature() {
        super(ScatterFeatureConfig.CODEC);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canGenerate(
            ScatterFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos center,
            BlockPos blockPos,
            float radius
    ) {
        float d = MHelper.length(
                center.getX() - blockPos.getX(),
                center.getZ() - blockPos.getZ()
        ) / radius * 0.6F + random.nextFloat() * 0.4F;
        small = d > 0.5F;
        return EndVineBlocks.BLUE_VINE_SEED.defaultBlockState().canSurvive(world, blockPos);
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        if (small) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    blockPos,
                    EndVineBlocks.BLUE_VINE_SEED.defaultBlockState().setValue(BasePlantWithAgeBlock.AGE, random.nextInt(4))
            );
        } else {
            BasePlantWithAgeBlock seed = ((BasePlantWithAgeBlock) EndVineBlocks.BLUE_VINE_SEED);
            seed.growAdult(world, random, blockPos);
        }
    }
}
