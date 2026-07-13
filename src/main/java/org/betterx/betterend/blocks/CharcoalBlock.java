package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.bclib.interfaces.Fuel;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class CharcoalBlock extends BaseBlock implements Fuel, BehaviourStone {
    public CharcoalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public int getFuelTime() {
        return 16000;
    }
}
