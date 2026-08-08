package org.betterx.betterend.integration.jei;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.entities.EndStoneSmelterBlockEntity;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import de.ambertation.wover.recipe.api.SyncedRecipes;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import java.util.List;

public class JeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = BetterEnd.C.mk("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new InfusionCategory(guiHelper),
                new AlloyingFuelCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(
                    InfusionCategory.TYPE,
                    List.copyOf(SyncedRecipes.allOfType(level, InfusionRecipe.TYPE))
            );
        }

        List<FuelEntry> fuels = EndStoneSmelterBlockEntity.availableFuels()
                                                          .entrySet()
                                                          .stream()
                                                          .map(e -> new FuelEntry(e.getKey(), e.getValue()))
                                                          .toList();
        registration.addRecipes(AlloyingFuelCategory.TYPE, fuels);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(InfusionCategory.TYPE, EndFunctionalBlocks.INFUSION_PEDESTAL);
        registration.addCraftingStation(AlloyingFuelCategory.TYPE, EndFunctionalBlocks.END_STONE_SMELTER);

        // EndStoneSmelterBlockEntity falls back to a vanilla BlastingRecipe when only one input
        // slot is filled - no custom category needed, just an extra catalyst on JEI's own one.
        registration.addCraftingStation(RecipeTypes.BLASTING, EndFunctionalBlocks.END_STONE_SMELTER);

        Block[] jadestoneFurnaces = {
                EndStoneBlocks.AZURE_JADESTONE.getBlock(StoneMaterial.FURNACE),
                EndStoneBlocks.SANDY_JADESTONE.getBlock(StoneMaterial.FURNACE),
                EndStoneBlocks.VIRID_JADESTONE.getBlock(StoneMaterial.FURNACE)
        };
        registration.addCraftingStation(RecipeTypes.SMELTING, jadestoneFurnaces);
    }

}
