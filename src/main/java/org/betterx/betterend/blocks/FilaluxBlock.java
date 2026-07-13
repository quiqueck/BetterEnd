package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseVineBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class FilaluxBlock extends BaseVineBlock {
    public FilaluxBlock(BlockBehaviour.Properties props) {
        super(props, 15, true, p -> p.offsetType(BlockBehaviour.OffsetType.NONE));
    }
}
