package org.betterx.datagen.betterend.recipes;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class AlloyingRecipesProvider extends WoverRecipeProvider {
    public AlloyingRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Alloying Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_iron"), Items.IRON_INGOT)
                        .setInput(EndTags.ALLOYING_IRON, EndTags.ALLOYING_IRON)
                        .outputCount(3)
                        .setExperience(2.1F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_gold"), Items.GOLD_INGOT)
                        .setInput(EndTags.ALLOYING_GOLD, EndTags.ALLOYING_GOLD)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_copper"), Items.COPPER_INGOT)
                        .setInput(EndTags.ALLOYING_COPPER, EndTags.ALLOYING_COPPER)
                        .outputCount(3)
                        .setExperience(3F)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("additional_netherite"), Items.NETHERITE_SCRAP)
                        .setInput(Blocks.ANCIENT_DEBRIS, Blocks.ANCIENT_DEBRIS)
                        .outputCount(3)
                        .setExperience(6F)
                        .setSmeltTime(1000)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("terminite_ingot"), EndBlocks.TERMINITE.equipment.ingot)
                        .setInput(Items.IRON_INGOT, EndItems.ENDER_DUST)
                        .outputCount(1)
                        .setExperience(2.5F)
                        .setSmeltTime(450)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("aeternium_ingot"), EndItems.AETERNIUM_SET.ingot)
                        .setInput(EndBlocks.TERMINITE.equipment.ingot, Items.NETHERITE_INGOT)
                        .outputCount(1)
                        .setExperience(4.5F)
                        .setSmeltTime(850)
                        .build(context);
        BCLRecipeBuilder.alloying(BetterEnd.C.mk("terminite_ingot_thallasium"), EndBlocks.TERMINITE.equipment.ingot)
                        .setInput(EndBlocks.THALLASIUM.equipment.ingot, EndItems.ENDER_DUST)
                        .outputCount(1)
                        .setExperience(2.5F)
                        .setSmeltTime(450)
                        .build(context);
    }
}
