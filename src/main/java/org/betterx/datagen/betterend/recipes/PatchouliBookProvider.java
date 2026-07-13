package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.GuideBookItem;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;

public class PatchouliBookProvider extends WoverRecipeProvider {
    public PatchouliBookProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Patchouli Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.crafting(BetterEnd.C.mk("guide_book"), GuideBookItem.GUIDE_BOOK)
                     .shape("D", "B", "C")
                     .addMaterial('D', EndItems.ENDER_DUST)
                     .addMaterial('B', Items.BOOK)
                     .addMaterial('C', EndItems.CRYSTAL_SHARDS)
                     .build(context);
    }
}
