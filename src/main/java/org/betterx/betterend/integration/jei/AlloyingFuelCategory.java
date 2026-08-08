package org.betterx.betterend.integration.jei;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;

import java.util.List;

/**
 * Only surfaces the End Stone Smelter's custom-registered fuels ({@code
 * EndStoneSmelterBlockEntity#availableFuels()}), not every vanilla item that happens to burn -
 * those already show up in JEI's own built-in vanilla fuel category.
 */
public class AlloyingFuelCategory implements IRecipeCategory<FuelEntry> {
    public static final IRecipeType<FuelEntry> TYPE = IRecipeType.create(BetterEnd.C.mk("alloying_fuel"), FuelEntry.class);

    private static final int WIDTH = 60;
    private static final int HEIGHT = 36;

    private final IDrawable icon;

    public AlloyingFuelCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(EndFunctionalBlocks.END_STONE_SMELTER);
    }

    @Override
    public IRecipeType<FuelEntry> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.betterend.jei.alloying_fuel");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FuelEntry entry, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
               .add(new ItemStack(entry.item()));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FuelEntry entry, IFocusGroup focuses) {
        builder.addAnimatedRecipeFlame(200).setPosition(1, 19);
        builder.addText(
                List.of(Component.translatable(
                        "category.betterend.jei.alloying_fuel.burn_time",
                        entry.burnTimeTicks() / 20.0
                )),
                0, HEIGHT - 9
        ).setColor(0xFF808080);
    }
}
