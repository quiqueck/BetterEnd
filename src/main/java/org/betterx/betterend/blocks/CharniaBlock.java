package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.UnderwaterPlantBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CharniaBlock extends UnderwaterPlantBlock {
    public CharniaBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected boolean isTerrain(BlockState state) {
        return state.isSolid();
    }
}
