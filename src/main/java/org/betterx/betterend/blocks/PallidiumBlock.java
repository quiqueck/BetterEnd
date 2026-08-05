package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.bclib.blocks.BaseTerrainBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;

public class PallidiumBlock extends BaseTerrainBlock {
    private final Block nextLevel;

    public PallidiumBlock(BlockBehaviour.Properties props, String thickness, Block nextLevel) {
        super(props, Blocks.END_STONE);
        this.nextLevel = nextLevel;
    }

    @Override
    public Block getBaseBlock() {
        return EndStoneBlocks.UMBRALITH.getBaseBlock();
    }


    @Override
    protected @NotNull InteractionResult useItemOn(
            @NotNull ItemStack itemStack,
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand interactionHand,
            @NotNull BlockHitResult blockHitResult
    ) {
        if (nextLevel == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else if (level.isClientSide()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (itemStack.is(Items.BONE_MEAL)) {
            BlocksHelper.setWithUpdate(level, pos, nextLevel);
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
