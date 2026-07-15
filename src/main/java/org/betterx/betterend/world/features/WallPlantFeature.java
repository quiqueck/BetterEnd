package org.betterx.betterend.world.features;

import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.betterend.blocks.basis.EndWallPlantBlock;
import org.betterx.bclib.util.BlocksHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WallPlantFeature extends WallScatterFeature<WallPlantFeatureConfig> {
    protected BlockState plant;

    public WallPlantFeature() {
        super(WallPlantFeatureConfig.CODEC);
    }

    @Override
    public boolean canGenerate(
            WallPlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos pos,
            Direction dir
    ) {
        plant = cfg.getPlantState(random, pos);
        Block block = plant.getBlock();
        if (block instanceof EndWallPlantBlock) {
            BlockState state = plant.setValue(EndWallPlantBlock.FACING, dir);
            return state.canSurvive(world, pos);
        } else if (block instanceof BaseAttachedBlock) {
            BlockState state = plant.setValue(BlockStateProperties.FACING, dir);
            return state.canSurvive(world, pos);
        }
        return plant.canSurvive(world, pos);
    }

    @Override
    public void generate(
            WallPlantFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos pos,
            Direction dir
    ) {
        Block block = plant.getBlock();
        if (block instanceof EndWallPlantBlock) {
            plant = plant.setValue(EndWallPlantBlock.FACING, dir);
        } else if (block instanceof BaseAttachedBlock) {
            plant = plant.setValue(BlockStateProperties.FACING, dir);
        }
        BlocksHelper.setWithoutUpdate(world, pos, plant);
    }
}
