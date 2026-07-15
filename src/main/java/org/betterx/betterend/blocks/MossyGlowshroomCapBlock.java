package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class MossyGlowshroomCapBlock extends BaseBlock {
    public static final BooleanProperty TRANSITION = EndBlockProperties.TRANSITION;

    public MossyGlowshroomCapBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRANSITION, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                   .setValue(
                           TRANSITION,
                           EndBlocks.MOSSY_GLOWSHROOM.isTreeLog(ctx.getLevel()
                                                                   .getBlockState(ctx.getClickedPos().below()))
                   );
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRANSITION);
    }
}
