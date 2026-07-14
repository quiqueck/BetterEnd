package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.wover.sets.api.blocks.types.HangingSign;

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

    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(new HangingSign() {
            @Override
            protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
                return BlockTraits.RECIPE.with(
                        (key, block, context) -> RecipeBuilder
                                .crafting(key.location(), block)
                                .outputCount(3)
                                .shape("I I", "o#o", "o#o")
                                .addMaterial('#', EndBlocks.LUCERNIA.getBlock(WoodSlots.STRIPPED_LOG))
                                .addMaterial('o', EndBlocks.JELLYSHROOM.getBlock(WoodSlots.LOG))
                                .addMaterial('I', Items.CHAIN)
                                .group("sign")
                                .category(RecipeCategory.DECORATIONS)
                                .build(context)
                );
            }
        });
    }

    @Override
    public @Nullable Block getBlock(@NotNull SlotType type) {
        if (type == SlotType.PLANKS || type == SlotType.SLAB) return EndBlocks.LUCERNIA.getBlock(SlotType.PLANKS);
        // This material only defines a hanging-sign slot (no log of its own) - the hanging sign's
        // model needs a stripped-log texture for its chain/particle model, so reuse Lucernia's, matching
        // the material already used for this sign's crafting recipe above.
        if (type == SlotType.STRIPPED_LOG) return EndBlocks.LUCERNIA.getBlock(WoodSlots.STRIPPED_LOG);
        return super.getBlock(type);
    }
}
