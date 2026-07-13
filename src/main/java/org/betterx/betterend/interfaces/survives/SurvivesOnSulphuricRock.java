package org.betterx.betterend.interfaces.survives;

import org.betterx.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.interfaces.SurvivesOnBlocks;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.world.level.block.Block;

import java.util.List;

public interface SurvivesOnSulphuricRock extends SurvivesOnBlocks {
    List<Block> BLOCKS = List.of(EndBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));

    @Override
    default List<Block> getSurvivableBlocks() {
        return BLOCKS;
    }

    @Override
    default String prefixComponent() {
        return "tooltip.bclib.place_underwater_on";
    }
}
