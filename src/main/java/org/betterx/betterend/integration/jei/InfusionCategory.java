package org.betterx.betterend.integration.jei;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;

import java.util.List;

/**
 * Ports the EMI-era layout (trigonometric slot placement + per-slot compass tooltips), not the
 * REI-era one (fixed pixel offsets, no tooltips) - the EMI version is the more complete of the two.
 */
public class InfusionCategory implements IRecipeCategory<RecipeHolder<InfusionRecipe>> {
    public static final IRecipeHolderType<InfusionRecipe> TYPE = IRecipeHolderType.create(InfusionRecipe.TYPE);

    private static final Identifier BACKGROUND = BetterEnd.C.mk("textures/gui/infusion.png");
    private static final Component[] ORIENTATIONS = {
            Component.translatable("betterend.infusion.north"),
            Component.translatable("betterend.infusion.north_east"),
            Component.translatable("betterend.infusion.east"),
            Component.translatable("betterend.infusion.south_east"),
            Component.translatable("betterend.infusion.south"),
            Component.translatable("betterend.infusion.south_west"),
            Component.translatable("betterend.infusion.west"),
            Component.translatable("betterend.infusion.north_west"),
    };

    private static final int LEFT = 30;
    private static final int TOP = 24;
    private static final int BOX_SIZE = 64;
    private static final int RADIUS = 42;
    private static final int HALF_SLOT = 9;
    private static final int CENTER_X = LEFT + BOX_SIZE / 2;
    private static final int CENTER_Y = TOP + BOX_SIZE / 2;
    private static final int RIGHT = LEFT + BOX_SIZE;

    // infusion.png's ring isn't perfectly centered in its own canvas - nudges the items drawn on
    // top of it without moving the background itself. Flip the sign if this goes the wrong way.
    private static final int RING_ITEM_X_OFFSET = 2;

    private static final int OUTPUT_X = RIGHT + 64;
    private static final int WIDTH = OUTPUT_X + 20;
    private static final int HEIGHT = Math.max(TOP + BOX_SIZE + 4, CENTER_Y + RADIUS + HALF_SLOT + 16);

    private final IDrawable icon;
    private final IDrawable background;

    public InfusionCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(EndFunctionalBlocks.INFUSION_PEDESTAL);
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, BOX_SIZE, BOX_SIZE)
                                   .setTextureSize(BOX_SIZE, BOX_SIZE)
                                   .build();
    }

    @Override
    public IRecipeHolderType<InfusionRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(EndFunctionalBlocks.INFUSION_PEDESTAL.getDescriptionId());
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

    private static int catalystX(int index) {
        return CENTER_X + RING_ITEM_X_OFFSET + (int) Math.round(RADIUS * Math.sin(index * Math.PI / 4)) - HALF_SLOT;
    }

    private static int catalystY(int index) {
        return CENTER_Y - (int) Math.round(RADIUS * Math.cos(index * Math.PI / 4)) - HALF_SLOT;
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            RecipeHolder<InfusionRecipe> recipeHolder,
            IFocusGroup focuses
    ) {
        InfusionRecipe recipe = recipeHolder.value();

        builder.addSlot(RecipeIngredientRole.INPUT, CENTER_X + RING_ITEM_X_OFFSET - HALF_SLOT, CENTER_Y - HALF_SLOT)
               .add(recipe.getInput());

        Ingredient[] catalysts = recipe.getCatalysts();
        for (int i = 0; i < catalysts.length; i++) {
            if (catalysts[i] == null) continue;
            Component orientation = ORIENTATIONS[i];
            builder.addSlot(RecipeIngredientRole.INPUT, catalystX(i), catalystY(i))
                   .add(catalysts[i])
                   .addRichTooltipCallback((view, tooltip) -> tooltip.add(orientation));
        }

        ItemStack output = recipe.assemble(null);
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X - HALF_SLOT, CENTER_Y - HALF_SLOT)
               .add(output);
    }

    @Override
    public void draw(
            RecipeHolder<InfusionRecipe> recipeHolder,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor guiGraphics,
            double mouseX,
            double mouseY
    ) {
        // JEI draws IRecipeCategory.draw() before the ingredient slots, but createRecipeExtras()'s
        // widgets draw after them - the background has to go here, not there, or it paints over
        // the items instead of sitting behind them.
        background.draw(guiGraphics, LEFT, TOP);
    }

    @Override
    public void createRecipeExtras(
            IRecipeExtrasBuilder builder,
            RecipeHolder<InfusionRecipe> recipeHolder,
            IFocusGroup focuses
    ) {
        InfusionRecipe recipe = recipeHolder.value();

        builder.addRecipeArrow().setPosition(RIGHT + 26, CENTER_Y - 8);
        builder.addText(List.of(Component.literal("N")), CENTER_X + RING_ITEM_X_OFFSET - 2, 4);
        builder.addText(
                List.of(Component.translatable("category.rei.infusion.time&val", recipe.getInfusionTime() / 20.0)),
                0, WIDTH
        ).setPosition(0, HEIGHT - 9).setColor(0xFF808080);
    }
}
