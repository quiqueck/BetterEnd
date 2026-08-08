package org.betterx.betterend.testmod.gametest;

import de.ambertation.wover.test.api.gametest.RecipeCoverageSweep;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Sweeps every {@code betterend:} item for a recipe that produces it, via
 * {@code wover-test-api}'s {@link RecipeCoverageSweep}. This is the mechanism that finds gaps like "no
 * hammer exists for the terminite/thallasium/aeternium tiers" automatically, rather than needing them
 * hand-enumerated - a hand-written list would just repeat whatever omission is already in the code.
 * <p>
 * Deliberately excludes plain blocks with no equipment role (terrain, decoration) from the failure
 * list, since many of those are intentionally worldgen/loot-only with no crafting recipe (ores,
 * saplings) - the sweep only enforces coverage for items that are themselves equipment: tools, armor,
 * and other `*_SET`-registered gear, where "no recipe at all" is a real gap rather than a design choice.
 */
public class RecipeCoverageGameTest {
    @GameTest
    public void everyEquipmentItemHasARecipe(GameTestHelper helper) {
        final List<String> missing = RecipeCoverageSweep.findItemsMissingARecipe(
                helper, "betterend", RecipeCoverageGameTest::looksLikeEquipment
        );

        if (!missing.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betterend items with no recipe producing them:\n - " + String.join("\n - ", missing)
            ));
        }
        helper.succeed();
    }

    /** Tools, armor and weapons - identified by path convention, same as the existing datagen naming. */
    private static boolean looksLikeEquipment(Item item) {
        final var key = BuiltInRegistries.ITEM.getResourceKey(item).orElse(null);
        if (key == null) return false;
        final String path = key.identifier().getPath();
        return path.endsWith("_sword") || path.endsWith("_pickaxe") || path.endsWith("_axe")
                || path.endsWith("_shovel") || path.endsWith("_hoe") || path.endsWith("_hammer")
                || path.endsWith("_helmet") || path.endsWith("_chestplate") || path.endsWith("_leggings")
                || path.endsWith("_boots") || path.endsWith("_elytra");
    }
}
