package org.betterx.datagen.betterend.recipes;



import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.item.EndFoodItems;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class EndFurnaceRecipeProvider extends WoverRecipeProvider {
    public EndFurnaceRecipeProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Furnace Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.smelting(BetterEnd.C.mk("end_lily_leaf_dried"), EndResourceItems.END_LILY_LEAF_DRIED)
                     .input(EndResourceItems.END_LILY_LEAF)
                     .build(context);

        RecipeBuilder.smelting(BetterEnd.C.mk("end_glass"), Blocks.GLASS)
                     .input(EndTerrainBlocks.ENDSTONE_DUST)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("end_berry"), EndFoodItems.SHADOW_BERRY_COOKED)
                     .input(EndFoodItems.SHADOW_BERRY_RAW)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("end_fish"), EndFoodItems.END_FISH_COOKED)
                     .input(EndFoodItems.END_FISH_RAW)
                     .build(context);

        RecipeBuilder.smelting(BetterEnd.C.mk("slime_ball"), Items.SLIME_BALL)
                     .input(EndWoodBlocks.JELLYSHROOM_CAP_PURPLE)
                     .build(context);

        RecipeBuilder.smelting(BetterEnd.C.mk("menger_sponge"), EndDecorBlocks.MENGER_SPONGE)
                     .input(EndDecorBlocks.MENGER_SPONGE_WET)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("chorus_mushroom"), EndFoodItems.CHORUS_MUSHROOM_COOKED)
                     .input(EndFoodItems.CHORUS_MUSHROOM_RAW)
                     .build(context);

        RecipeBuilder.cookableFood(BetterEnd.C.mk("bolux_mushroom"), EndFoodItems.BOLUX_MUSHROOM_COOKED)
                     .input(EndMushroomBlocks.BOLUX_MUSHROOM)
                     .build(context);
    }
}
