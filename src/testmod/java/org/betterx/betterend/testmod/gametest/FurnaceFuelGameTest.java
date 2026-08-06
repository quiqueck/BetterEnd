package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.block.EndMetalBlocks;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Regression coverage for {@code charcoal_block} actually burning at its declared fuel time.
 * <p>
 * Per {@code org.betterx.bclib.api.v2.FuelValueRegistration}'s own file comment: the hook that turns a
 * block's {@code Fuel} interface into a real, usable furnace
 * burn time used to be wired from a datagen-only provider, so nothing in BCLib, BetterEnd or
 * BetterNether ever actually got the burn time it declared - a silent, total regression that only
 * datagen output (never read at runtime) would have shown as "working". This asserts the number the
 * *game* will actually use, {@code level.fuelValues().burnDuration(...)}, not the block's own declared
 * {@code getFuelTime()} - the two only agree if the registration hook is wired correctly.
 */
public class FurnaceFuelGameTest {
    @GameTest
    public void charcoalBlockBurnsAtItsDeclaredFuelTime(GameTestHelper helper) {
        final ItemStack stack = new ItemStack(EndMetalBlocks.CHARCOAL_BLOCK);
        final int burnDuration = helper.getLevel().fuelValues().burnDuration(stack);

        // 16000 ticks - vanilla coal_block's burn time, which CharcoalBlock.getFuelTime() matches.
        if (burnDuration < 16000) {
            throw helper.assertionException(Component.literal(
                    "level.fuelValues().burnDuration(charcoal_block) returned " + burnDuration
                            + ", expected at least 16000 - the block's declared fuel time is not reaching"
                            + " the actual furnace fuel-value registry"
            ));
        }
        helper.succeed();
    }
}
