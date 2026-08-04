package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import org.jetbrains.annotations.NotNull;

public class UmbrellaTreeClusterEmptyBlock extends Block {
    public static final BooleanProperty NATURAL = EndBlockProperties.NATURAL;

    public UmbrellaTreeClusterEmptyBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(NATURAL);
    }

    @Override
    public void tick(
            BlockState state,
            @NotNull ServerLevel world,
            @NotNull BlockPos pos,
            @NotNull RandomSource random
    ) {
        if (state.getValue(NATURAL) && random.nextInt(16) == 0) {
            BlocksHelper.setWithUpdate(
                    world,
                    pos,
                    EndWoodBlocks.UMBRELLA_TREE_CLUSTER.defaultBlockState().setValue(UmbrellaTreeClusterBlock.NATURAL, true)
            );
        }
    }
}
