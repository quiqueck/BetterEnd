package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndUnderwaterPlantWithAgeBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class HydraluxSaplingBlock extends EndUnderwaterPlantWithAgeBlock {
    public HydraluxSaplingBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void grow(WorldGenLevel world, RandomSource random, BlockPos pos) {
        int h = MHelper.randRange(4, 8, random);
        MutableBlockPos mut = new MutableBlockPos().set(pos);

        for (int i = 1; i < h; i++) {
            mut.setY(pos.getY() + i);
            if (!world.getBlockState(mut).is(Blocks.WATER)) {
                return;
            }
        }

        mut.setY(pos.getY());
        BlockState state = EndBlocks.HYDRALUX.defaultBlockState();
        BlocksHelper.setWithoutUpdate(
                world,
                pos,
                state.setValue(EndBlockProperties.HYDRALUX_SHAPE, EndBlockProperties.HydraluxShape.ROOTS)
        );
        for (int i = 1; i < h - 2; i++) {
            mut.setY(pos.getY() + i);
            BlocksHelper.setWithoutUpdate(
                    world,
                    mut,
                    state.setValue(EndBlockProperties.HYDRALUX_SHAPE, EndBlockProperties.HydraluxShape.VINE)
            );
        }

        mut.setY(mut.getY() + 1);
        boolean big = random.nextBoolean();
        BlocksHelper.setWithoutUpdate(
                world,
                mut,
                state.setValue(
                        EndBlockProperties.HYDRALUX_SHAPE,
                        big
                                ? EndBlockProperties.HydraluxShape.FLOWER_BIG_BOTTOM
                                : EndBlockProperties.HydraluxShape.FLOWER_SMALL_BOTTOM
                )
        );

        mut.setY(mut.getY() + 1);
        BlocksHelper.setWithoutUpdate(
                world,
                mut,
                state.setValue(
                        EndBlockProperties.HYDRALUX_SHAPE,
                        big
                                ? EndBlockProperties.HydraluxShape.FLOWER_BIG_TOP
                                : EndBlockProperties.HydraluxShape.FLOWER_SMALL_TOP
                )
        );
    }

}
