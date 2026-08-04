package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import org.betterx.bclib.blocks.UpDownPlantBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BlueVineBlock extends UpDownPlantBlock {
    public static final EnumProperty<BlockProperties.TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    public BlueVineBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    protected boolean isTerrain(BlockState state) {
        return state.getBlock() == EndTerrainBlocks.END_MOSS || state.getBlock() == EndTerrainBlocks.END_MYCELIUM;
    }
}
