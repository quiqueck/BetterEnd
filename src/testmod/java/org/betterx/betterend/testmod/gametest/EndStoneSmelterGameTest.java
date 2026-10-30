package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.client.gui.EndStoneSmelterMenu;
import org.betterx.betterend.registry.EndItems;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Shift-click behaviour of the End Stone Smelter menu.
 */
public class EndStoneSmelterGameTest implements FabricGameTest {
    /**
     * Shift-clicking out of the ingredient and fuel slots used to target only the hotbar row, so a full
     * hotbar made the click do nothing even with an empty main inventory. Like a vanilla furnace, every
     * direction has to reach the whole player inventory. Alloy ingredients and blasting inputs have to reach
     * the ingredient slots.
     */
    @GameTest(template = EMPTY_STRUCTURE)
    public void shiftClickMovesStacksThroughTheWholeInventory(GameTestHelper helper) {
        final Player player = helper.makeMockPlayer();
        final List<String> failures = new ArrayList<>();
        final int hotbarStart = EndStoneSmelterMenu.SLOT_COUNT + 27;

        // From A/B/fuel with a full hotbar: stack must land in the main inventory.
        final int[] sources = {
                EndStoneSmelterMenu.INGREDIENT_SLOT_A,
                EndStoneSmelterMenu.INGREDIENT_SLOT_B,
                EndStoneSmelterMenu.FUEL_SLOT
        };
        for (int source : sources) {
            player.getInventory().clearContent();
            for (int i = 0; i < 9; i++) player.getInventory().setItem(i, new ItemStack(Items.DIRT, 64));
            final EndStoneSmelterMenu menu = newMenu(player);
            menu.getSlot(source).set(new ItemStack(Items.IRON_INGOT, 5));
            menu.quickMoveStack(player, source);
            if (!menu.getSlot(source).getItem().isEmpty()) {
                failures.add("slot " + source + " kept " + menu.getSlot(source).getItem() + " with a full hotbar");
            }
            int inMain = 0;
            for (int i = 9; i < 36; i++) {
                if (player.getInventory().getItem(i).is(Items.IRON_INGOT)) {
                    inMain += player.getInventory().getItem(i).getCount();
                }
            }
            if (inMain != 5) failures.add("slot " + source + " moved " + inMain + " of 5 items into the main inventory");
        }

        // Result slot -> inventory.
        {
            player.getInventory().clearContent();
            final EndStoneSmelterMenu menu = newMenu(player);
            menu.getSlot(EndStoneSmelterMenu.RESULT_SLOT).set(new ItemStack(Items.IRON_INGOT, 3));
            menu.quickMoveStack(player, EndStoneSmelterMenu.RESULT_SLOT);
            if (!menu.getSlot(EndStoneSmelterMenu.RESULT_SLOT).getItem().isEmpty()
                    || !player.getInventory().getItem(8).is(Items.IRON_INGOT)) {
                failures.add("result slot stack did not move into the inventory (hotbar slot 8: "
                        + player.getInventory().getItem(8) + ")");
            }
        }

        // Player inventory -> ingredient / fuel slots; otherwise between main inventory and hotbar.
        {
            player.getInventory().clearContent();
            final EndStoneSmelterMenu menu = newMenu(player);
            player.getInventory().setItem(9, new ItemStack(Items.IRON_INGOT, 2));
            player.getInventory().setItem(10, new ItemStack(Items.COAL_BLOCK, 2));
            player.getInventory().setItem(11, new ItemStack(Items.DIRT, 2));
            player.getInventory().setItem(12, new ItemStack(EndItems.ENDER_DUST, 2));
            player.getInventory().setItem(0, new ItemStack(Items.STONE, 2));
            menu.quickMoveStack(player, EndStoneSmelterMenu.SLOT_COUNT);
            menu.quickMoveStack(player, EndStoneSmelterMenu.SLOT_COUNT + 1);
            menu.quickMoveStack(player, EndStoneSmelterMenu.SLOT_COUNT + 2);
            menu.quickMoveStack(player, EndStoneSmelterMenu.SLOT_COUNT + 3);
            menu.quickMoveStack(player, hotbarStart);
            if (!menu.getSlot(EndStoneSmelterMenu.INGREDIENT_SLOT_A).getItem().is(Items.IRON_INGOT)) {
                failures.add("iron ingot did not shift-click into an ingredient slot");
            }
            if (!menu.getSlot(EndStoneSmelterMenu.INGREDIENT_SLOT_B).getItem().is(EndItems.ENDER_DUST)) {
                failures.add("ender dust did not shift-click into the second ingredient slot");
            }
            if (!menu.getSlot(EndStoneSmelterMenu.FUEL_SLOT).getItem().is(Items.COAL_BLOCK)) {
                failures.add("coal did not shift-click into the fuel slot");
            }
            if (!player.getInventory().getItem(11).isEmpty() || !hotbarHas(player, Items.DIRT)) {
                failures.add("dirt did not move from the main inventory to the hotbar");
            }
            if (!player.getInventory().getItem(0).isEmpty() || !player.getInventory().getItem(9).is(Items.STONE)) {
                failures.add("stone did not move from the hotbar to the main inventory");
            }
        }

        // A blasting input (raw iron) reaches the ingredient slots as well.
        {
            player.getInventory().clearContent();
            final EndStoneSmelterMenu menu = newMenu(player);
            player.getInventory().setItem(9, new ItemStack(Items.RAW_IRON, 2));
            menu.quickMoveStack(player, EndStoneSmelterMenu.SLOT_COUNT);
            if (!menu.getSlot(EndStoneSmelterMenu.INGREDIENT_SLOT_A).getItem().is(Items.RAW_IRON)) {
                failures.add("raw iron did not shift-click into an ingredient slot");
            }
        }

        helper.assertTrue(
                failures.isEmpty(),
                "End Stone Smelter shift-click regression: " + String.join("; ", failures)
        );
        helper.succeed();
    }

    private static EndStoneSmelterMenu newMenu(Player player) {
        return new EndStoneSmelterMenu(
                1,
                player.getInventory(),
                new SimpleContainer(EndStoneSmelterMenu.SLOT_COUNT),
                new SimpleContainerData(4)
        );
    }

    private static boolean hotbarHas(Player player, Item item) {
        for (int i = 0; i < 9; i++) if (player.getInventory().getItem(i).is(item)) return true;
        return false;
    }
}
