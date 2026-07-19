package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.BaseTerrainBlock;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EndTerrainBlock extends BaseTerrainBlock {
    public EndTerrainBlock(BlockBehaviour.Properties properties) {
        super(properties, Blocks.END_STONE);
    }
}
