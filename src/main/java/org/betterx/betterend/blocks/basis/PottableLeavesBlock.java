package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseLeavesBlock;
import org.betterx.bclib.interfaces.SurvivesOnBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class PottableLeavesBlock extends BaseLeavesBlock implements SurvivesOnBlocks {
    public PottableLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public List<Block> getSurvivableBlocks() {
        return List.of();
    }

    @Override
    public String prefixComponent() {
        return "tooltip.bclib.pottable_on";
    }
}
