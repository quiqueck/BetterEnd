package org.betterx.betterend.item.material;

import de.ambertation.wover.complex.api.equipment.ToolTier;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.BlockTags;

public class EndToolTier {
    public static ToolTier THALLASIUM = ToolTier
            .builder("thallasium")
            .level(ToolTiers.IRON_TOOL.level)
            .toolMaterial(EndToolMaterial.THALLASIUM)
            .blockTag(BlockTags.NEEDS_IRON_TOOL)
            .toolValuesWithOffset(ToolTiers.IRON_TOOL, ToolTier.ToolValues.NO_OFFSET)
            .build();

    public static ToolTier TERMINITE = ToolTier
            .builder("terminite")
            .level(ToolTiers.DIAMOND_TOOL.level)
            .toolMaterial(EndToolMaterial.TERMINITE)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValuesWithOffset(ToolTiers.DIAMOND_TOOL, ToolTier.ToolValues.NO_OFFSET)
            .build();

    public static ToolTier AETERNIUM = ToolTier
            .builder("aeternium")
            .level(ToolTiers.NETHERITE_TOOL.level)
            .toolMaterial(EndToolMaterial.AETERNIUM)
            .blockTag(MineableTags.NEEDS_NETHERITE_TOOL)
            .toolValuesWithOffset(ToolTiers.NETHERITE_TOOL, ToolTier.ToolValues.NO_OFFSET)
            .build();
}
