package org.betterx.betterend.blocks;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;
import org.betterx.betterend.blocks.basis.FurBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlueVineSeedBlock extends EndPlantWithAgeBlock {
    public BlueVineSeedBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void growAdult(WorldGenLevel world, RandomSource random, BlockPos pos) {
        int height = MHelper.randRange(2, 5, random);
        int h = BlocksHelper.upRay(world, pos, height + 2);
        if (h < height + 1) {
            return;
        }
        BlocksHelper.setWithoutUpdate(
                world,
                pos,
                EndBlocks.BLUE_VINE.defaultBlockState()
                                   .setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.BOTTOM)
        );
        for (int i = 1; i < height; i++) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    pos.above(i),
                    EndBlocks.BLUE_VINE.defaultBlockState()
                                       .setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.MIDDLE)
            );
        }
        BlocksHelper.setWithoutUpdate(
                world,
                pos.above(height),
                EndBlocks.BLUE_VINE.defaultBlockState()
                                   .setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.TOP)
        );
        placeLantern(world, pos.above(height + 1));
    }

    private void placeLantern(WorldGenLevel world, BlockPos pos) {
        BlocksHelper.setWithoutUpdate(
                world,
                pos,
                EndBlocks.BLUE_VINE_LANTERN.defaultBlockState().setValue(BlueVineLanternBlock.NATURAL, true)
        );
        for (Direction dir : BlocksHelper.HORIZONTAL) {
            BlockPos p = pos.relative(dir);
            if (world.isEmptyBlock(p)) {
                BlocksHelper.setWithoutUpdate(
                        world,
                        p,
                        EndBlocks.BLUE_VINE_FUR.defaultBlockState().setValue(FurBlock.FACING, dir)
                );
            }
        }
        if (world.isEmptyBlock(pos.above())) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    pos.above(),
                    EndBlocks.BLUE_VINE_FUR.defaultBlockState().setValue(FurBlock.FACING, Direction.UP)
            );
        }
    }

}
