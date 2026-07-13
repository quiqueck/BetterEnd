package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnEndStone;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class EndWallPlantBlock extends BaseWallPlantBlock implements BehaviourPlant, SurvivesOnEndStone {
    public EndWallPlantBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnEndStone.super.isTerrain(state);
    }
}
