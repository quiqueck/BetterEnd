package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.item.EndFoodItems;

import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code betterend:cave_pumpkin_pie}: food stats and recipe presence.
 * <p>
 * <b>{@code foodStatsMatchVanillaPumpkinPie} caught a real, fairly serious, cross-mod bug, since fixed.
 * </b> Root cause traced to {@code WorldWeaver/wover-item-api}'s {@code FoodItemDefinition}: its
 * {@code beforeBuild()} unconditionally ran {@code this.food(this.foodProperties.build(),
 * consumable.build())} using its own *internal*, never-touched {@code FoodProperties.Builder} (defaults:
 * 0 nutrition, 0 saturation). The public {@code food(FoodProperties)} overload - what
 * {@code EndItems.registerEndFood(String, FoodProperties)} calls to copy an existing vanilla food like
 * {@code Foods.PUMPKIN_PIE} - instead queued a *separate* property setter, and {@code beforeBuild()}'s
 * own setter was added after it and won, silently overwriting whatever the caller had configured. Every
 * BetterEnd item registered through that overload was affected, not just this one:
 * {@code end_fish_cooked}, {@code blossom_berry}, {@code chorus_mushroom_cooked},
 * {@code bolux_mushroom_cooked}, and {@code cave_pumpkin_pie} all ended up with a 0/0
 * {@code FoodProperties} in-game - eating any of them restored no hunger or saturation at all. Fixed in
 * {@code FoodItemDefinition} itself (a {@code foodSetDirectly} flag makes {@code beforeBuild()} a no-op
 * once the direct {@code food(FoodProperties)} overload has already been called), not in any one mod,
 * since the bug lived in shared WorldWeaver code.
 * <p>
 * The crafting recipe's berry slot accepts either {@code EndFoodItems.BLOSSOM_BERRY} or
 * {@code EndFoodItems.SHADOW_BERRY_RAW} as one multi-item {@code Ingredient} (both berries in the same
 * shape cell), rather than being two separate recipes - so "does the recipe exist and produce the
 * right item" is checked here; proving both berries are independently accepted by that one ingredient
 * would need a real 3x3 crafting-grid simulation, which no code in this repo does anywhere yet and
 * isn't attempted here to avoid guessing at unverified {@code CraftingInput}/{@code ShapedRecipe}
 * ingredient-introspection APIs for this MC version.
 */
public class CavePumpkinPieGameTest {
    @GameTest
    public void foodStatsMatchVanillaPumpkinPie(GameTestHelper helper) {
        final ItemStack stack = new ItemStack(EndFoodItems.CAVE_PUMPKIN_PIE);
        final FoodProperties food = stack.get(DataComponents.FOOD);
        final FoodProperties vanilla = Foods.PUMPKIN_PIE;

        final List<String> failures = new ArrayList<>();
        if (food == null) {
            failures.add("cave_pumpkin_pie has no FOOD component at all");
        } else {
            if (food.nutrition() != vanilla.nutrition()) {
                failures.add("nutrition is " + food.nutrition() + ", expected " + vanilla.nutrition()
                        + " (vanilla pumpkin pie)");
            }
            if (food.saturation() != vanilla.saturation()) {
                failures.add("saturation is " + food.saturation() + ", expected " + vanilla.saturation()
                        + " (vanilla pumpkin pie)");
            }
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "cave_pumpkin_pie food-stat regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void craftingRecipeProducesTheItem(GameTestHelper helper) {
        final var holder = helper.getLevel()
                                  .recipeAccess()
                                  .byKey(net.minecraft.resources.ResourceKey.create(
                                          net.minecraft.core.registries.Registries.RECIPE,
                                          ResourceLocation.parse("betterend:cave_pumpkin_pie")
                                  ));

        if (holder.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betterend:cave_pumpkin_pie recipe is not registered at all"
            ));
        }

        final RecipeHolder<?> recipe = holder.get();
        final ItemStack result;
        try {
            @SuppressWarnings({"unchecked", "rawtypes"})
            final Recipe raw = recipe.value();
            //noinspection unchecked
            result = (ItemStack) raw.assemble(null, helper.getLevel().registryAccess());
        } catch (Exception e) {
            throw helper.assertionException(Component.literal(
                    "betterend:cave_pumpkin_pie recipe threw while assembling with a null input: " + e
            ));
        }

        if (!result.is(EndFoodItems.CAVE_PUMPKIN_PIE)) {
            throw helper.assertionException(Component.literal(
                    "betterend:cave_pumpkin_pie recipe assembles to " + result + " instead of the pie"
            ));
        }
        helper.succeed();
    }
}
