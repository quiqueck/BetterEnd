package org.betterx.betterend.blocks;

import net.minecraft.world.level.block.Block;
import org.betterx.bclib.interfaces.Fuel;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class CharcoalBlock extends Block implements Fuel {
    public CharcoalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public int getFuelTime() {
        return 16000;
    }
}
