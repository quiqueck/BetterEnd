package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.interfaces.BetterEndElytra;
import org.betterx.betterend.registry.item.EndEquipmentItems;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the armored/crystalite elytra flight-speed multipliers.
 * <p>
 * The actual glide-speed effect is applied by BCLib's {@code LivingEntityMixin}, which
 * {@code @ModifyArg}s {@code LivingEntity#travelFallFlying} to multiply the horizontal velocity by
 * whatever {@code ((BCLElytraItem) chestplate).getMovementFactor()} returns - so the configured value on
 * each item IS the thing that determines glide distance; there is no additional logic in between. This
 * asserts that value directly (armored 0.97, crystalite 1.0, i.e. strictly faster than vanilla's
 * unmodified glide) rather than simulating full elytra-flight physics over many ticks, which would need
 * to reproduce vanilla's fall-flying trigger conditions and velocity/rotation setup with no added
 * value over reading the number the mixin itself reads.
 */
public class CrystaliteElytraGameTest {
    @GameTest
    public void movementFactorsAreConfiguredAsDocumented(GameTestHelper helper) {
        final double armored = ((BetterEndElytra) EndEquipmentItems.ARMORED_ELYTRA).getMovementFactor();
        final double crystalite = ((BetterEndElytra) EndEquipmentItems.CRYSTALITE_ELYTRA).getMovementFactor();

        final StringBuilder failures = new StringBuilder();
        if (armored != 0.97) {
            failures.append("\n - armored elytra movement factor is ").append(armored).append(", expected 0.97");
        }
        if (crystalite != 1.0) {
            failures.append("\n - crystalite elytra movement factor is ").append(crystalite).append(", expected 1.0");
        }
        if (crystalite <= armored) {
            failures.append("\n - crystalite (").append(crystalite)
                    .append(") does not glide faster than armored (").append(armored).append(")");
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Elytra movement-factor regression:" + failures
            ));
        }
        helper.succeed();
    }
}
