package org.betterx.betterend.world.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.betterend.blocks.basis.PottableCropBlock;
import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.blocks.BasePlantWithAgeBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class SinglePlantFeature extends ScatterFeature<SinglePlantFeatureConfig> {

    BlockState plant;

    public SinglePlantFeature() {
        super(SinglePlantFeatureConfig.CODEC);
    }

    @Override
    protected BlockPos getCenterGround(SinglePlantFeatureConfig cfg, WorldGenLevel world, BlockPos pos) {
        return cfg.rawHeightmap
                ? DefaultFeature.getPosOnSurfaceWG(world, pos)
                : DefaultFeature.getPosOnSurface(world, pos);
    }

    @Override
    public boolean canGenerate(
            SinglePlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos center,
            BlockPos blockPos,
            float radius
    ) {
        this.plant = cfg.getPlantState(random, blockPos);
        //noinspection deprecation
        return plant.canSurvive(world, blockPos);
    }

    @Override
    public void generate(SinglePlantFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        if (this.plant.getBlock() instanceof BaseDoublePlantBlock) {
            int rot = random.nextInt(4);
            BlockState state = this.plant.setValue(BaseDoublePlantBlock.ROTATION, rot);
            BlocksHelper.setWithoutUpdate(world, blockPos, state);
            BlocksHelper.setWithoutUpdate(world, blockPos.above(), state.setValue(BaseDoublePlantBlock.TOP, true));
        } else if (this.plant.getBlock() instanceof PottableCropBlock) {
            BlockState state = this.plant.setValue(PottableCropBlock.AGE, 3);
            BlocksHelper.setWithoutUpdate(world, blockPos, state);
        } else if (this.plant.getBlock() instanceof BasePlantWithAgeBlock) {
            int age = random.nextInt(4);
            BlockState state = this.plant.setValue(BasePlantWithAgeBlock.AGE, age);
            BlocksHelper.setWithoutUpdate(world, blockPos, state);
        } else {
            BlocksHelper.setWithoutUpdate(world, blockPos, this.plant);
        }
    }
}
