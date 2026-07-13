package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.betterend.blocks.basis.EndPlantBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainPlantBlock extends EndPlantBlock implements BehaviourPlant {
    public TerrainPlantBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected boolean isTerrain(BlockState state) {
        return state.isSolid();
    }
}
