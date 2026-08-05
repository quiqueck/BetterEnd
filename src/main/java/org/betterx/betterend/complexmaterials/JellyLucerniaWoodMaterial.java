package org.betterx.betterend.complexmaterials;


import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.sets.api.blocks.types.HangingSign;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JellyLucerniaWoodMaterial extends EndWoodenComplexMaterial {
    public JellyLucerniaWoodMaterial() {
        super("lucernia_jellyshroom", MapColor.COLOR_PURPLE, MapColor.COLOR_ORANGE);
    }

    /**
     * This material has no wood of its own - it only defines a hanging sign and the three pieces of furniture.
     * Both are built from Lucernia's planks (see {@link #getBlock(SlotType)}), so the furniture slots' defaults
     * (source {@link SlotType#SLAB}, texture {@link SlotType#PLANKS}) resolve there without further parameters.
     * Note that {@code super.createDefaultDefinitions()} is deliberately not called: it would add the full set of
     * wooden slots this material does not have.
     */
    @Override
    protected SlotMap createDefaultDefinitions() {
        return addFurniture(SlotMap.of(new HangingSign() {
            @Override
            protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
                return BlockTraits.RECIPE.with(
                        (key, block, context) -> RecipeBuilder
                                .crafting(key.identifier(), block)
                                .outputCount(3)
                                .shape("I I", "o#o", "o#o")
                                .addMaterial('#', EndWoodBlocks.LUCERNIA.getBlock(WoodSlots.STRIPPED_LOG))
                                .addMaterial('o', EndWoodBlocks.JELLYSHROOM.getBlock(WoodSlots.LOG))
                                .addMaterial('I', Items.IRON_CHAIN)
                                .group("sign")
                                .category(RecipeCategory.DECORATIONS)
                                .build(context)
                );
            }
        }));
    }

    @Override
    public @Nullable Block getBlock(@NotNull SlotType type) {
        if (type == SlotType.PLANKS || type == SlotType.SLAB) return EndWoodBlocks.LUCERNIA.getBlock(SlotType.PLANKS);
        // This material defines no log of its own - the hanging sign's model needs a stripped-log texture
        // for its chain/particle model, so reuse Lucernia's, matching the material already used for this
        // sign's crafting recipe above.
        if (type == SlotType.STRIPPED_LOG) return EndWoodBlocks.LUCERNIA.getBlock(WoodSlots.STRIPPED_LOG);
        return super.getBlock(type);
    }
}
