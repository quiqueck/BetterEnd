package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourGlass;
import org.betterx.betterend.blocks.basis.LitPillarBlock;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SmaragdantCrystalBlock extends LitPillarBlock implements BehaviourGlass {
    public SmaragdantCrystalBlock(BlockBehaviour.Properties props) {
        super(props);
    }
}
