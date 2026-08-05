package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.EndEnchantments;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.util.LootTableUtil;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the two survival routes to the End Veil enchantment.
 * <p>
 * BetterEnd emits no enchantment tags at all, so {@code betterend:end_veil} is not in
 * {@code #minecraft:in_enchanting_table} and an enchanting table can never roll it. That leaves the
 * {@code end_veil_book} infusion recipe and the End City treasure pool as the only ways in - lose
 * either silently and a fully working enchantment becomes unobtainable outside of commands, which is
 * exactly the state this suite was written to catch.
 */
public class EndVeilObtainableGameTest {
    private static final Identifier RECIPE_ID = Identifier.parse("betterend:end_veil_book");

    /** 0.25 chance per roll, so 40 rolls put a false negative at about one in ten thousand. */
    private static final int LOOT_ROLLS = 40;

    private static boolean isEndVeilBook(ItemStack stack) {
        if (!stack.is(Items.ENCHANTED_BOOK)) return false;
        final ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null) return false;
        return stored.keySet()
                     .stream()
                     .anyMatch(holder -> holder.is(EndEnchantments.END_VEIL.key()));
    }

    /**
     * The infusion route. Resolves the real recipe from the recipe manager, so a datagen change that
     * drops it - or writes the enchantment into {@code enchantments} instead of the
     * {@code stored_enchantments} a book actually reads - fails here.
     */
    @GameTest
    public void endVeilBookIsInfusable(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // Recipe no longer exposes a generic result accessor in 26.2, so the recipe is looked up by id
        // and assembled - which also proves the output patch survives assembly rather than only sitting
        // in the json.
        final RecipeHolder<?> holder = helper.getLevel()
                                             .recipeAccess()
                                             .getRecipes()
                                             .stream()
                                             .filter(r -> r.id().identifier().equals(RECIPE_ID))
                                             .findFirst()
                                             .orElse(null);

        if (holder == null) {
            failures.add(RECIPE_ID + " is not registered");
        } else if (!(holder.value() instanceof InfusionRecipe infusion)) {
            failures.add(RECIPE_ID + " is no longer an infusion recipe: " + holder.value().getClass());
        } else {
            final ItemStack result = infusion.assemble(null);
            if (!isEndVeilBook(result)) {
                failures.add(RECIPE_ID + " assembled into " + result
                        + " rather than a book carrying betterend:end_veil"
                        + " (a book reads stored_enchantments, not enchantments)");
            }
        }

        failIfAny(helper, "End Veil infusion recipe regression", failures);
        helper.succeed();
    }

    /** The End City route, asserted by actually rolling the table BetterEnd splices into the vanilla one. */
    @GameTest
    public void endVeilBookDropsFromEndCityTreasure(GameTestHelper helper) {
        final LootTable table = helper.getLevel()
                                      .getServer()
                                      .reloadableRegistries()
                                      .getLootTable(LootTableUtil.END_CITY_EXTRA);

        final LootParams params = new LootParams.Builder(helper.getLevel())
                .withParameter(
                        LootContextParams.ORIGIN,
                        Vec3.atCenterOf(helper.absolutePos(new BlockPos(1, 2, 1)))
                )
                .create(LootContextParamSets.CHEST);

        boolean sawBook = false;
        for (int roll = 0; roll < LOOT_ROLLS && !sawBook; roll++) {
            sawBook = table.getRandomItems(params).stream().anyMatch(EndVeilObtainableGameTest::isEndVeilBook);
        }

        if (!sawBook) {
            helper.fail(Component.literal(
                    "rolled the End City extra table " + LOOT_ROLLS
                            + " times without ever seeing an End Veil book"
            ));
            return;
        }
        helper.succeed();
    }

    /**
     * BetterEnd's own obsidian has to carry {@code #wover:is_obsidian}, because that shared tag is what
     * BetterNether's Obsidian Breaker keys off - a cross-mod link that neither mod's tests can see from
     * the other side, so each end of it is asserted where it lives.
     */
    @GameTest
    public void mossyObsidianIsTaggedAsObsidian(GameTestHelper helper) {
        if (!EndStoneBlocks.MOSSY_OBSIDIAN.defaultBlockState().is(CommonBlockTags.IS_OBSIDIAN)) {
            helper.fail(Component.literal(
                    "betterend:mossy_obsidian is not in #wover:is_obsidian, so BetterNether's Obsidian"
                            + " Breaker will not apply to it"
            ));
            return;
        }
        helper.succeed();
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
