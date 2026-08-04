package org.betterx.betterend.blocks;

import de.ambertation.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmeraldIceBlock extends HalfTransparentBlock {
    public EmeraldIceBlock(BlockBehaviour.Properties props) {
        super(props);
    }


    @Override
    public void playerDestroy(
            @NotNull Level world,
            @NotNull Player player,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @Nullable BlockEntity blockEntity,
            @NotNull ItemStack stack
    ) {
        super.playerDestroy(world, player, pos, state, blockEntity, stack);
        if (EnchantmentUtils.getItemEnchantmentLevel(world, Enchantments.SILK_TOUCH, stack) == 0) {
            if (world.dimensionType().ultraWarm()) {
                world.removeBlock(pos, false);
                return;
            }

            BlockState belowState = world.getBlockState(pos.below());
            if (belowState.blocksMotion() || belowState.liquid()) {
                world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }
        }

    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (world.getBrightness(LightLayer.BLOCK, pos) > 11 - state.getLightBlock()) {
            this.melt(world, pos);
        }

    }

    protected void melt(Level world, BlockPos pos) {
        if (world.dimensionType().ultraWarm()) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            world.neighborChanged(pos, Blocks.WATER, null);
        }
    }
}
