package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class EndFurnaceRecipeProvider extends WoverRecipeProvider {
    public EndFurnaceRecipeProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Furnace Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.smelting(BetterEnd.C.mk("end_lily_leaf_dried"), EndItems.END_LILY_LEAF_DRIED)
                     .input(EndItems.END_LILY_LEAF)
                     .build(context);

        RecipeBuilder.smelting(BetterEnd.C.mk("end_glass"), Blocks.GLASS)
                     .input(EndBlocks.ENDSTONE_DUST)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("end_berry"), EndItems.SHADOW_BERRY_COOKED)
                     .input(EndItems.SHADOW_BERRY_RAW)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("end_fish"), EndItems.END_FISH_COOKED)
                     .input(EndItems.END_FISH_RAW)
                     .build(context);

        RecipeBuilder.smelting(BetterEnd.C.mk("slime_ball"), Items.SLIME_BALL)
                     .input(EndBlocks.JELLYSHROOM_CAP_PURPLE)
                     .build(context);

        RecipeBuilder.smelting(BetterEnd.C.mk("menger_sponge"), EndBlocks.MENGER_SPONGE)
                     .input(EndBlocks.MENGER_SPONGE_WET)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("chorus_mushroom"), EndItems.CHORUS_MUSHROOM_COOKED)
                     .input(EndItems.CHORUS_MUSHROOM_RAW)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("bolux_mushroom"), EndItems.BOLUX_MUSHROOM_COOKED)
                     .input(EndBlocks.BOLUX_MUSHROOM)
                     .build(context);
    }
}
