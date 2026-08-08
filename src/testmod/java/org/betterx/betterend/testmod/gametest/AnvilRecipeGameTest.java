package org.betterx.betterend.testmod.gametest;

import org.betterx.bclib.blocks.LeveledAnvilBlock;
import org.betterx.betterend.blocks.AeterniumAnvil;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers anvil tier gating and per-anvil durability, at the level that actually drives recipe/repair
 * eligibility ({@code LeveledAnvilBlock.getAnvilCraftingLevel}/{@code canHandle}), rather than
 * simulating a full anvil-use/fall-damage sequence to observe the same numbers indirectly.
 * <p>
 * The vanilla-anvil-is-tier-1-not-tier-0 offset is easy to get backwards: {@code getAnvilCraftingLevel}
 * treats {@code Blocks.ANVIL}/{@code CHIPPED_ANVIL}/{@code DAMAGED_ANVIL} as
 * {@code LegacyTiers.IRON.level - 1} (2 - 1 = 1), not 0.
 */
public class AnvilRecipeGameTest {
    @GameTest
    public void vanillaAnvilIsTierOneNotTierZero(GameTestHelper helper) {
        final int level = LeveledAnvilBlock.getAnvilCraftingLevel(Blocks.ANVIL);
        if (level != 1) {
            throw helper.assertionException(Component.literal(
                    "Blocks.ANVIL's crafting level is " + level + ", expected 1 (LegacyTiers.IRON.level - 1)"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void aeterniumAnvilOutranksVanillaAndHasTwelveDurability(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final int aeterniumLevel = LeveledAnvilBlock.getAnvilCraftingLevel(EndFunctionalBlocks.AETERNIUM_ANVIL);
        if (aeterniumLevel != 5) {
            failures.add("aeternium_anvil's crafting level is " + aeterniumLevel + ", expected 5");
        }
        if (!LeveledAnvilBlock.canHandle(EndFunctionalBlocks.AETERNIUM_ANVIL, LeveledAnvilBlock.getAnvilCraftingLevel(Blocks.ANVIL))) {
            failures.add("aeternium_anvil cannot handle recipes a vanilla anvil can - tier gating is inverted");
        }

        final int durability = ((AeterniumAnvil) EndFunctionalBlocks.AETERNIUM_ANVIL).getMaxDurability();
        if (durability != 12) {
            failures.add("aeternium_anvil's max durability is " + durability + ", expected 12 (BCLib default is 5)");
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Aeternium anvil regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
