package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndAnvilBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class AeterniumAnvil extends EndAnvilBlock {
    private static final int AETERNIUM_ANVIL_LEVEL = 5;

    public AeterniumAnvil(BlockBehaviour.Properties props) {
        super(props, EndBlocks.AETERNIUM_BLOCK.defaultMapColor(), AETERNIUM_ANVIL_LEVEL);
    }

    @Override
    public int getMaxDurability() {
        return 12;
    }
}
