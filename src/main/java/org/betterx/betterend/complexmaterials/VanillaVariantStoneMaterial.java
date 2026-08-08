package org.betterx.betterend.complexmaterials;

import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.slots.StoneSlots;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class VanillaVariantStoneMaterial extends VanillaStoneSet {
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
                ),
                color
        );
    }
}
