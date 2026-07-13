package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnChorusNylium;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ChorusGrassBlock extends EndPlantBlock implements SurvivesOnChorusNylium, BehaviourPlant {
    public ChorusGrassBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnChorusNylium.super.isTerrain(state);
    }
}
