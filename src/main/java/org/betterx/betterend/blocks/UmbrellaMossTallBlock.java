package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndDoublePlantBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class UmbrellaMossTallBlock extends EndDoublePlantBlock {
    public UmbrellaMossTallBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        ItemEntity item = new ItemEntity(
                world,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                new ItemStack(EndBlocks.UMBRELLA_MOSS)
        );
        world.addFreshEntity(item);
    }
}
