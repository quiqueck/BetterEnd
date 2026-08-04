package org.betterx.datagen.betterend.recipes;


import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.GuideBookItem;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;

public class PatchouliBookProvider extends WoverRecipeProvider {
    public PatchouliBookProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Patchouli Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.crafting(BetterEnd.C.mk("guide_book"), GuideBookItem.GUIDE_BOOK)
                     .shape("D", "B", "C")
                     .addMaterial('D', EndResourceItems.ENDER_DUST)
                     .addMaterial('B', Items.BOOK)
                     .addMaterial('C', EndResourceItems.CRYSTAL_SHARDS)
                     .build(context);
    }
}
