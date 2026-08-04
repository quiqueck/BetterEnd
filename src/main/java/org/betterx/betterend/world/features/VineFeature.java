package org.betterx.betterend.world.features;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.util.BlocksHelper;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class VineFeature extends InvertedScatterFeature<VineFeatureConfig> {
    private BlockState plant;
    boolean vine;

    public VineFeature() {
        super(VineFeatureConfig.CODEC);
    }

    @Override
    public boolean canGenerate(
            VineFeatureConfig cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos center,
            BlockPos blockPos,
            float radius
    ) {
        plant = cfg.getPlantState(random, blockPos);

        BlockState state = world.getBlockState(blockPos);
        return state.canBeReplaced() && canPlaceBlock(state, world, blockPos);
    }

    @Override
    public void generate(VineFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        int h = BlocksHelper.downRay(world, blockPos, random.nextInt(cfg.maxLength)) - 1;
        if (h > 2) {
            BlockState top = getTopState();
            BlockState middle = getMiddleState();
            BlockState bottom = getBottomState();
            BlocksHelper.setWithoutUpdate(world, blockPos, top);
            for (int i = 1; i < h; i++) {
                BlocksHelper.setWithoutUpdate(world, blockPos.below(i), middle);
            }
            BlocksHelper.setWithoutUpdate(world, blockPos.below(h), bottom);
        }
    }

    private boolean canPlaceBlock(BlockState state, WorldGenLevel world, BlockPos blockPos) {
        if (plant == null) return false;
        if (plant.getBlock() instanceof BaseVineBlock vineBlock) {
            vine = true;
            return vineBlock.canGenerate(state, world, blockPos);
        } else {
            vine = false;
            return state.canSurvive(world, blockPos);
        }
    }

    private BlockState getTopState() {
        BlockState state = plant;
        return vine ? state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP) : state;
    }

    private BlockState getMiddleState() {
        BlockState state = plant;
        return vine ? state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE) : state;
    }

    private BlockState getBottomState() {
        BlockState state = plant;
        return vine ? state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM) : state;
    }
}
