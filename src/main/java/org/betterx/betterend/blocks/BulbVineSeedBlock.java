package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.blocks.BasePlantWithAgeBlock;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BulbVineSeedBlock extends BasePlantWithAgeBlock {
    public BulbVineSeedBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos above = pos.above();
        return BlocksHelper.isDecorationSupport(world, above, world.getBlockState(above), Direction.DOWN);
    }

    @Override
    public void growAdult(WorldGenLevel world, RandomSource random, BlockPos pos) {
        int h = BlocksHelper.downRay(world, pos, random.nextInt(24)) - 1;
        if (h > 2) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    pos,
                    EndVineBlocks.BULB_VINE.defaultBlockState().setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP)
            );
            for (int i = 1; i < h; i++) {
                BlocksHelper.setWithoutUpdate(
                        world,
                        pos.below(i),
                        EndVineBlocks.BULB_VINE.defaultBlockState()
                                           .setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE)
                );
            }
            BlocksHelper.setWithoutUpdate(
                    world,
                    pos.below(h),
                    EndVineBlocks.BULB_VINE.defaultBlockState().setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM)
            );
        }
    }

}
