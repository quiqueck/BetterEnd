package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndUnderwaterPlantBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CharniaBlock extends EndUnderwaterPlantBlock {
    public CharniaBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected boolean isValidGround(BlockState state) {
        return state.isSolid();
    }
}
