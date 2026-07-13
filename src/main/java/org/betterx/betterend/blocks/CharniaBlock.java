package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourWaterPlant;
import org.betterx.bclib.interfaces.SurvivesOnSpecialGround;
import org.betterx.betterend.blocks.basis.EndUnderwaterPlantBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CharniaBlock extends EndUnderwaterPlantBlock implements BehaviourWaterPlant {
    public CharniaBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected boolean isTerrain(BlockState state) {
        return state.isSolid();
    }
}
