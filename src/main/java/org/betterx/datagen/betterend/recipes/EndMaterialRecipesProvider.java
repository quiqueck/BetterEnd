package org.betterx.datagen.betterend.recipes;


import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.complexmaterials.MaterialManager;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

public class EndMaterialRecipesProvider extends WoverRecipeProvider {
    public EndMaterialRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Material Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        EndEquipmentItems.AETERNIUM_SET.registerRecipes(context);
        MaterialManager.stream().forEach(m -> m.registerRecipes(context));
    }
}
