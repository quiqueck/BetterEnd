package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.WallMushroomBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnEndStone;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class EndWallMushroom extends WallMushroomBlock implements SurvivesOnEndStone {

    public EndWallMushroom(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnEndStone.super.isTerrain(state);
    }
}
