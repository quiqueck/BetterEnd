package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.item.EndEquipmentItems;

import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Guards the slot targeting and the actual defensive value of the Crystalite armor set.
 * <p>
 * Crystalite is registered through {@code EndArmorItemTraitBuilder}, which - unlike every other
 * BetterEnd tier - adds {@link Attributes#ARMOR} and {@link Attributes#ARMOR_TOUGHNESS} to a fixed
 * {@code EquipmentSlotGroup.CHEST} rather than to the slot the piece is actually worn in. Two things
 * follow from that, and this class pins both:
 * <ul>
 *   <li>the helmet, leggings and boots contribute nothing at all, because their modifiers are keyed to
 *       a slot they can never occupy;</li>
 *   <li>their tooltips render the armor lines under "When on Body" instead of the piece's own slot.</li>
 * </ul>
 * {@link #netheriteControlGrantsArmorInEverySlot} is the harness control: it runs the identical
 * assertions against vanilla netherite, so a failure there means the test is wrong, not the mod.
 */
public class CrystaliteArmorGameTest {
    private record Piece(String name, Item item, EquipmentSlot slot) {}

    private static List<Piece> crystalite() {
        return List.of(
                new Piece("crystalite_helmet", EndEquipmentItems.CRYSTALITE_HELMET, EquipmentSlot.HEAD),
                new Piece("crystalite_chestplate", EndEquipmentItems.CRYSTALITE_CHESTPLATE, EquipmentSlot.CHEST),
                new Piece("crystalite_leggings", EndEquipmentItems.CRYSTALITE_LEGGINGS, EquipmentSlot.LEGS),
                new Piece("crystalite_boots", EndEquipmentItems.CRYSTALITE_BOOTS, EquipmentSlot.FEET)
        );
    }

    private static List<Piece> netherite() {
        return List.of(
                new Piece("netherite_helmet", Items.NETHERITE_HELMET, EquipmentSlot.HEAD),
                new Piece("netherite_chestplate", Items.NETHERITE_CHESTPLATE, EquipmentSlot.CHEST),
                new Piece("netherite_leggings", Items.NETHERITE_LEGGINGS, EquipmentSlot.LEGS),
                new Piece("netherite_boots", Items.NETHERITE_BOOTS, EquipmentSlot.FEET)
        );
    }

    /**
     * Collects, for one armor piece, every reason its ARMOR/ARMOR_TOUGHNESS modifiers would not apply
     * while the piece is worn in its own slot.
     */
    private static void checkOwnSlot(Piece piece, List<String> failures) {
        final ItemStack stack = new ItemStack(piece.item());
        final ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);

        if (modifiers == null || modifiers.modifiers().isEmpty()) {
            failures.add(piece.name() + ": has no attribute modifiers at all");
            return;
        }

        boolean sawArmor = false;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.attribute().is(Attributes.ARMOR) && !entry.attribute().is(Attributes.ARMOR_TOUGHNESS)) {
                continue;
            }
            sawArmor = true;
            if (!entry.slot().test(piece.slot())) {
                failures.add(piece.name() + ": "
                        + entry.attribute().getRegisteredName()
                        + " is bound to EquipmentSlotGroup." + entry.slot()
                        + ", which does not include " + piece.slot()
                        + " - the piece can never grant it, and the tooltip lists it under the wrong slot");
            }
        }

        if (!sawArmor) {
            failures.add(piece.name() + ": carries neither ARMOR nor ARMOR_TOUGHNESS");
        }
    }

    /**
     * The real assertion. Reads the item's own {@code ATTRIBUTE_MODIFIERS} component rather than
     * equipping anything, so it is deterministic and needs no ticks.
     */
    @GameTest
    public void crystaliteModifiersTargetTheirOwnSlot(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        crystalite().forEach(piece -> checkOwnSlot(piece, failures));

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Crystalite armor modifiers are bound to the wrong equipment slot:\n - "
                            + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    /**
     * Harness control: the identical check against vanilla netherite. If this ever fails, the test is
     * broken - not BetterEnd.
     */
    @GameTest
    public void netheriteControlGrantsArmorInEverySlot(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        netherite().forEach(piece -> checkOwnSlot(piece, failures));

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Control failed - the assertion itself is wrong, vanilla netherite must pass it:\n - "
                            + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    /**
     * End-to-end version of the same defect, measured the way a player experiences it: the computed
     * armor value of a full Crystalite set must be strictly greater than that of the chestplate on its
     * own. Today they are identical, because only the chestplate contributes.
     */
    @GameTest
    public void fullCrystaliteSetBeatsChestplateAlone(GameTestHelper helper) {
        final double chestOnly = computeArmor(List.of(
                new Piece("crystalite_chestplate", EndEquipmentItems.CRYSTALITE_CHESTPLATE, EquipmentSlot.CHEST)
        ));
        final double fullSet = computeArmor(crystalite());

        if (fullSet <= chestOnly) {
            helper.fail(Component.literal(
                    "A full Crystalite set computes to " + fullSet + " armor, the chestplate alone to "
                            + chestOnly + " - the other three pieces contribute nothing"
            ));
            return;
        }
        helper.succeed();
    }

    /** Sums what each piece contributes while worn in its own slot, exactly as the attribute map would. */
    private static double computeArmor(List<Piece> pieces) {
        double total = 0.0;
        for (Piece piece : pieces) {
            final ItemStack stack = new ItemStack(piece.item());
            final ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (modifiers == null) continue;
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                if (entry.attribute().is(Attributes.ARMOR) && entry.slot().test(piece.slot())) {
                    total += entry.modifier().amount();
                }
            }
        }
        return total;
    }
}
