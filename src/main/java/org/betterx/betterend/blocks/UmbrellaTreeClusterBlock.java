package org.betterx.betterend.blocks;



import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.item.EndFoodItems;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class UmbrellaTreeClusterBlock extends Block {
    public static final BooleanProperty NATURAL = BlockProperties.NATURAL;

    public UmbrellaTreeClusterBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(NATURAL);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            Player player,
            BlockHitResult blockHitResult
    ) {
        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            Player player,
            InteractionHand interactionHand,
            BlockHitResult blockHitResult
    ) {
        if (itemStack.getItem() == Items.GLASS_BOTTLE) {
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            itemStack = new ItemStack(EndFoodItems.UMBRELLA_CLUSTER_JUICE);
            player.addItem(itemStack);
            level.playLocalSound(
                    blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5,
                    SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS,
                    1, 1, false
            );
            BlocksHelper.setWithUpdate(
                    level, blockPos,
                    EndWoodBlocks.UMBRELLA_TREE_CLUSTER_EMPTY.defaultBlockState()
                                                         .setValue(NATURAL, blockState.getValue(NATURAL))
            );
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
