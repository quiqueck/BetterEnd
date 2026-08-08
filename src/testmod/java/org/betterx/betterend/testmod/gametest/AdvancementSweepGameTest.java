package org.betterx.betterend.testmod.gametest;

import de.ambertation.wover.test.api.gametest.AdvancementSweep;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Sweeps every currently-loaded {@code betterend:} advancement, via {@code wover-test-api}'s
 * {@link AdvancementSweep}, and asserts each has at least one criterion. BetterEnd ships ~890 generated
 * advancement JSONs (most of them "unlocked by crafting X" entries datagen derives automatically), so
 * this deliberately re-derives its target list from the server's own loaded advancements each run
 * rather than hand-listing them - a hand list would immediately rot as datagen adds/removes entries.
 * <p>
 * The only *custom* trigger types ({@code PORTAL_ON}/{@code PORTAL_TRAVEL}/{@code INFUSION_FINISHED})
 * get their own dedicated award-path coverage in {@link EternalPortalGameTest} and
 * {@link InfusionTableGameTest}; this sweep only checks that every advancement is well-formed enough to
 * ever be awardable at all, regardless of trigger type.
 */
public class AdvancementSweepGameTest {
    @GameTest
    public void everyAdvancementHasAtLeastOneCriterion(GameTestHelper helper) {
        final List<String> problems = AdvancementSweep.findMalformedAdvancements(helper, "betterend");

        if (!problems.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betterend advancement regression:\n - " + String.join("\n - ", problems)
            ));
        }
        helper.succeed();
    }
}
