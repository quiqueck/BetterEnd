package org.betterx.betterend.blocks;

import org.betterx.bclib.util.BlocksHelper;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;

import com.google.common.collect.Lists;

import java.util.List;

public class RunedFlavolite extends Block {
    public static final BooleanProperty ACTIVATED = BlockProperties.ACTIVE;

    public RunedFlavolite(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(ACTIVATED);
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return !BlocksHelper.isInvulnerableUnsafe(this.defaultBlockState());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (BlocksHelper.isInvulnerableUnsafe(this.defaultBlockState())) {
            return Lists.newArrayList();
        }
        return super.getDrops(state, builder);
    }
}
