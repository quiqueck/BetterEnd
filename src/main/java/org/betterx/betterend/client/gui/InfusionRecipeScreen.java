package org.betterx.betterend.client.gui;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import de.ambertation.wover.recipe.api.SyncedRecipes;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * In-world recipe book for the infusion ritual, opened by clicking an infusion pedestal with an
 * empty hand once all eight catalyst pedestals are in place.
 * <p>
 * Two pages: every known infusion recipe on the left, the selected one laid out on the right over
 * the same compass-rose background the recipe-viewer integrations use, so the ring positions read
 * the same everywhere.
 */
@Environment(EnvType.CLIENT)
public class InfusionRecipeScreen extends Screen {
    /**
     * Page colours lifted from vanilla's {@code textures/gui/book.png} so this reads as the same
     * material as a written book. Drawn as flat rectangles rather than blitting the book texture:
     * that page is a fixed 146x180 and this panel is neither, and a stretched parchment looks worse
     * than a clean one.
     */
    private static final int PAGE_COLOR = 0xFFFFF9EC;
    private static final int PAGE_SHADE_COLOR = 0xFFEFE2C4;
    private static final int FRAME_COLOR = 0xFF7F3C28;
    private static final int FRAME_DARK_COLOR = 0xFF5A2A1C;
    private static final int TEXT_COLOR = 0xFF404040;
    private static final int TEXT_MUTED_COLOR = 0xFF8A7A5C;

    private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
    private static final Identifier ARROW_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow");
    private static final Identifier COMPASS = BetterEnd.C.mk("textures/gui/infusion.png");

    private static final int COMPASS_SIZE = 84;
    private static final int SLOT_SIZE = 18;
    private static final int ARROW_WIDTH = 20;
    private static final int ARROW_HEIGHT = 18;
    /** Distance from the ring centre to the middle of a catalyst slot. */
    private static final int RING_RADIUS = 52;

    private static final int PANEL_WIDTH = 330;
    private static final int PANEL_HEIGHT = 194;
    private static final int MARGIN = 8;
    private static final int HEADER_HEIGHT = 20;
    private static final int LIST_WIDTH = 118;
    private static final int ROW_HEIGHT = 20;
    /** Ticks each item of a multi-item ingredient (a tag) is shown before the next one. */
    private static final int INGREDIENT_CYCLE_TICKS = 20;

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

    private final List<RecipeHolder<InfusionRecipe>> recipes = new ArrayList<>();
    private @Nullable RecipeHolder<InfusionRecipe> selected;

    private int leftPos;
    private int topPos;

    public InfusionRecipeScreen() {
        super(Component.translatable(EndFunctionalBlocks.INFUSION_PEDESTAL.getDescriptionId()));
    }

    @Override
    protected void init() {
        if (recipes.isEmpty()) {
            recipes.addAll(loadRecipes());
            if (!recipes.isEmpty()) {
                selected = recipes.getFirst();
            }
        }

        leftPos = (width - PANEL_WIDTH) / 2;
        topPos = (height - PANEL_HEIGHT) / 2;

        addRenderableWidget(new RecipeList(
                leftPos + MARGIN,
                topPos + HEADER_HEIGHT,
                LIST_WIDTH,
                PANEL_HEIGHT - HEADER_HEIGHT - MARGIN
        ));
    }

    /**
     * The client only knows recipes the server sent it; {@link SyncedRecipes} is both halves of that.
     * Sorted by output name so the list is stable between openings and between worlds.
     */
    private List<RecipeHolder<InfusionRecipe>> loadRecipes() {
        if (minecraft == null || minecraft.level == null) return List.of();

        List<RecipeHolder<InfusionRecipe>> found = new ArrayList<>(
                SyncedRecipes.allOfType(minecraft.level, InfusionRecipe.TYPE)
        );
        found.sort(Comparator.comparing(holder -> displayName(outputOf(holder)).getString()));
        return found;
    }

    private static ItemStack outputOf(RecipeHolder<InfusionRecipe> holder) {
        return holder.value().assemble(null);
    }

    /**
     * The name a recipe is listed under. Roughly half of the infusion recipes produce an enchanted
     * book, and every one of those is called "Enchanted Book" - so a book is listed under what it
     * actually stores, which is the only thing that tells those rows apart.
     * <p>
     * Falls back to the item's own name whenever the enchantment cannot be named: a book with no
     * stored enchantments, or one carrying a custom name that was deliberately set.
     */
    private static Component displayName(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME)) return stack.getHoverName();

        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.isEmpty()) return stack.getHoverName();

        MutableComponent name = null;
        for (Holder<Enchantment> enchantment : stored.keySet()) {
            MutableComponent one = enchantmentName(enchantment, stored.getLevel(enchantment));
            name = name == null ? one : name.append(", ").append(one);
        }
        return name == null ? stack.getHoverName() : name;
    }

    /**
     * {@link Enchantment#getFullname} with the styling left off - it paints the name gray (red for
     * curses), and neither reads on a near-white page.
     */
    private static MutableComponent enchantmentName(Holder<Enchantment> enchantment, int level) {
        MutableComponent name = enchantment.value().description().copy();
        if (level != 1 || enchantment.value().getMaxLevel() != 1) {
            name.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }
        return name;
    }

    /**
     * The item an ingredient shows right now. Tag ingredients cycle through their members so a
     * recipe that accepts several items does not silently advertise only the first.
     */
    private ItemStack displayStack(@Nullable Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) return ItemStack.EMPTY;

        List<Holder<Item>> items = ingredient.items().toList();
        if (items.isEmpty()) return ItemStack.EMPTY;

        long tick = minecraft == null || minecraft.level == null ? 0 : minecraft.level.getGameTime();
        int index = (int) ((tick / INGREDIENT_CYCLE_TICKS) % items.size());
        return new ItemStack(items.get(index));
    }

    private int ringCenterX() {
        return leftPos + MARGIN + LIST_WIDTH + MARGIN + RING_RADIUS + SLOT_SIZE / 2;
    }

    private int ringCenterY() {
        return topPos + HEADER_HEIGHT + RING_RADIUS + SLOT_SIZE / 2;
    }

    private static int catalystX(int centerX, int index) {
        return centerX + (int) Math.round(RING_RADIUS * Math.sin(index * Math.PI / 4));
    }

    private static int catalystY(int centerY, int index) {
        return centerY - (int) Math.round(RING_RADIUS * Math.cos(index * Math.PI / 4));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractPages(graphics);
        graphics.text(font, title, leftPos + MARGIN, topPos + 7, TEXT_COLOR, false);

        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (selected == null) {
            graphics.text(
                    font,
                    Component.translatable("gui.betterend.infusion.no_recipes"),
                    leftPos + MARGIN + LIST_WIDTH + MARGIN,
                    topPos + HEADER_HEIGHT + 4,
                    TEXT_MUTED_COLOR,
                    false
            );
            return;
        }

        extractRecipe(graphics, selected, mouseX, mouseY);
    }

    /**
     * The open book: a brown cover, two parchment pages, and a shaded gutter down the middle where
     * the binding would be.
     */
    private void extractPages(GuiGraphicsExtractor graphics) {
        int right = leftPos + PANEL_WIDTH;
        int bottom = topPos + PANEL_HEIGHT;
        graphics.fill(leftPos - 4, topPos - 4, right + 4, bottom + 4, FRAME_DARK_COLOR);
        graphics.fill(leftPos - 3, topPos - 3, right + 3, bottom + 3, FRAME_COLOR);
        graphics.fill(leftPos, topPos, right, bottom, PAGE_COLOR);

        int gutter = leftPos + MARGIN + LIST_WIDTH + MARGIN / 2;
        graphics.fill(gutter - 1, topPos + 2, gutter + 1, bottom - 2, PAGE_SHADE_COLOR);
    }

    private void extractRecipe(
            GuiGraphicsExtractor graphics,
            RecipeHolder<InfusionRecipe> holder,
            int mouseX,
            int mouseY
    ) {
        InfusionRecipe recipe = holder.value();
        int centerX = ringCenterX();
        int centerY = ringCenterY();

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                COMPASS,
                centerX - COMPASS_SIZE / 2,
                centerY - COMPASS_SIZE / 2,
                0.0F,
                0.0F,
                COMPASS_SIZE,
                COMPASS_SIZE,
                COMPASS_SIZE,
                COMPASS_SIZE
        );

        extractSlot(graphics, centerX, centerY, displayStack(recipe.getInput()), null, mouseX, mouseY);

        Ingredient[] catalysts = recipe.getCatalysts();
        for (int i = 0; i < catalysts.length; i++) {
            extractSlot(
                    graphics,
                    catalystX(centerX, i),
                    catalystY(centerY, i),
                    displayStack(catalysts[i]),
                    ORIENTATIONS[i],
                    mouseX,
                    mouseY
            );
        }

        int arrowX = centerX + RING_RADIUS + SLOT_SIZE;
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED, ARROW_SPRITE, arrowX, centerY - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT
        );
        extractSlot(
                graphics,
                arrowX + ARROW_WIDTH + SLOT_SIZE / 2 + 4,
                centerY,
                outputOf(holder),
                null,
                mouseX,
                mouseY
        );

        // Centred by hand rather than with centeredText(), which has no no-shadow overload - a black
        // drop shadow under muted text on a near-white page is all contrast and no legibility.
        Component time = Component.translatable("category.rei.infusion.time&val", recipe.getInfusionTime() / 20.0);
        graphics.text(
                font,
                time,
                centerX - font.width(time) / 2,
                topPos + PANEL_HEIGHT - MARGIN - font.lineHeight,
                TEXT_MUTED_COLOR,
                false
        );
    }

    /**
     * Draws a slot centred on {@code (centerX, centerY)}, and registers its tooltip when hovered.
     * {@code hint} names the compass direction the catalyst has to be placed in.
     */
    private void extractSlot(
            GuiGraphicsExtractor graphics,
            int centerX,
            int centerY,
            ItemStack stack,
            @Nullable Component hint,
            int mouseX,
            int mouseY
    ) {
        int x = centerX - SLOT_SIZE / 2;
        int y = centerY - SLOT_SIZE / 2;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, x, y, SLOT_SIZE, SLOT_SIZE);
        if (stack.isEmpty()) return;

        graphics.item(stack, x + 1, y + 1);
        graphics.itemDecorations(font, stack, x + 1, y + 1);

        if (mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE) {
            List<Component> lines = new ArrayList<>(getTooltipFromItem(minecraft, stack));
            if (hint != null) {
                lines.add(hint.copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTransparentBackground(graphics);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * The left page: one row per recipe, scrolled with the wheel or the scrollbar.
     */
    @Environment(EnvType.CLIENT)
    private class RecipeList extends AbstractScrollArea {
        private RecipeList(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), AbstractScrollArea.defaultSettings(ROW_HEIGHT));
        }

        @Override
        protected int contentHeight() {
            return recipes.size() * ROW_HEIGHT;
        }

        private int rowWidth() {
            return width - (scrollable() ? scrollbarWidth() + 2 : 0);
        }

        private int indexAt(double mouseX, double mouseY) {
            if (mouseX < getX() || mouseX >= getX() + rowWidth()) return -1;
            int index = (int) ((mouseY - getY() + scrollAmount()) / ROW_HEIGHT);
            return index >= 0 && index < recipes.size() ? index : -1;
        }

        @Override
        public void onClick(MouseButtonEvent event, boolean doubleClick) {
            if (updateScrolling(event)) return;

            int index = indexAt(event.x(), event.y());
            if (index >= 0) {
                selected = recipes.get(index);
            }
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            int hovered = isMouseOver(mouseX, mouseY) ? indexAt(mouseX, mouseY) : -1;

            // Clipped to the row area rather than the whole widget, so a long name is cut off at the
            // scrollbar instead of running under it.
            graphics.enableScissor(getX(), getY(), getX() + rowWidth(), getBottom());
            for (int i = 0; i < recipes.size(); i++) {
                int rowY = getY() + i * ROW_HEIGHT - (int) scrollAmount();
                if (rowY + ROW_HEIGHT < getY() || rowY > getBottom()) continue;

                RecipeHolder<InfusionRecipe> holder = recipes.get(i);
                // Darkening washes, not white ones - the page underneath is already near-white.
                if (holder == selected) {
                    graphics.fill(getX(), rowY, getX() + rowWidth(), rowY + ROW_HEIGHT, 0x407F3C28);
                } else if (i == hovered) {
                    graphics.fill(getX(), rowY, getX() + rowWidth(), rowY + ROW_HEIGHT, 0x1A7F3C28);
                }

                ItemStack output = outputOf(holder);
                graphics.item(output, getX() + 2, rowY + 2);
                graphics.itemDecorations(font, output, getX() + 2, rowY + 2);
                graphics.text(
                        font,
                        displayName(output),
                        getX() + 22,
                        rowY + (ROW_HEIGHT - font.lineHeight) / 2 + 1,
                        TEXT_COLOR,
                        false
                );
            }
            graphics.disableScissor();

            extractScrollbar(graphics, mouseX, mouseY);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, getMessage());
        }
    }
}
