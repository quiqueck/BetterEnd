package org.betterx.betterend.world.features;

import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class DoublePlantFeature extends ScatterFeature<DoublePlantFeatureConfig> {
    private BlockState plant;

    public DoublePlantFeature() {
        super(DoublePlantFeatureConfig.CODEC);
    }

    @Override
    public boolean canGenerate(
            DoublePlantFeatureConfig cfg,
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
        plant = d < 0.5F ? cfg.getLargePlantState(random, blockPos) : cfg.getSmallPlantState(random, blockPos);
        //noinspection deprecation
        return plant.canSurvive(world, blockPos);
    }

    @Override
    public void generate(
            DoublePlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos blockPos
    ) {
        if (plant.getBlock() instanceof BaseDoublePlantBlock dpb) {
            int rot = random.nextInt(4);
            BlockState state = plant.setValue(BaseDoublePlantBlock.ROTATION, rot);
            BlocksHelper.setWithoutUpdate(world, blockPos, state);
            BlocksHelper.setWithoutUpdate(world, blockPos.above(), state.setValue(BaseDoublePlantBlock.TOP, true));
        } else {
            BlocksHelper.setWithoutUpdate(world, blockPos, plant);
        }
    }
}
