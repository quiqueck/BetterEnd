package org.betterx.betterend.testmod.gametest;

import de.ambertation.wover.test.api.gametest.PottableSweep;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers flower-pot soil acceptance data, via {@code wover-test-api}'s {@link PottableSweep}, for every
 * plant BetterEnd registers as pottable rather than a hand-picked few.
 * <p>
 * This checks the registry data - every plant's declared soil tag actually matches at least one
 * registered soil - not the physical "place plant in my flower pot block" interaction, which is
 * necessarily BetterEnd-specific (WorldWeaver's {@code wover-pottable-api} is a data registry, not a pot
 * block implementation - see {@code PottableSweep}'s own class javadoc). A plant whose soil tag matches
 * nothing is a silent dead end no amount of clicking a real pot would ever catch, since the plant would
 * never be potable on anything at all.
 */
public class FlowerPotGameTest {
    @GameTest
    public void everyPlantsSoilTagIsSatisfiableBySomeRegisteredSoil(GameTestHelper helper) {
        final List<String> problems = PottableSweep.findPlantsWithUnsatisfiableSoilTag(helper, "betterend");

        if (!problems.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betterend pottable-plant regression:\n - " + String.join("\n - ", problems)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void atLeastOnePlantAndSoilAreRegistered(GameTestHelper helper) {
        if (PottableSweep.plantsIn(helper, "betterend").isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "no betterend plants are registered as pottable at all - either a real regression, or"
                            + " this sweep is looking in the wrong place"
            ));
        }
        helper.succeed();
    }
}
