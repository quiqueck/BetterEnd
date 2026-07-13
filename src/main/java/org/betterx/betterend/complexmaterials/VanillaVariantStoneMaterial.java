package org.betterx.betterend.complexmaterials;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.slots.StoneSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class VanillaVariantStoneMaterial extends VanillaStoneSet {
    private final MapColor color;

    public VanillaVariantStoneMaterial(
            String baseName,
            Block sourceBlock,
            MapColor color
    ) {
        super(
                baseName,
                sourceBlock,
                SlotMap.of(
                        StoneSlots.CRACKED_SOURCE,
                        StoneSlots.CRACKED_SLAB,
                        StoneSlots.CRACKED_STAIRS,
                        StoneSlots.CRACKED_WALL,
                        StoneSlots.WEATHERED_SOURCE,
                        StoneSlots.WEATHERED_SLAB,
                        StoneSlots.WEATHERED_STAIRS,
                        StoneSlots.WEATHERED_WALL
                )
        );
        this.color = color;
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);
        blockDefinition.mapColor(color);
    }
}
