package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.LeveledAnvilBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class AeterniumAnvil extends LeveledAnvilBlock {
    private static final int AETERNIUM_ANVIL_LEVEL = 5;

    public AeterniumAnvil(BlockBehaviour.Properties props) {
        super(props, AETERNIUM_ANVIL_LEVEL);
    }

    @Override
    public int getMaxDurability() {
        return 12;
    }
}
