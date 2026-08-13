package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.registry.item.EndEquipmentItems;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
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

    /**
     * The shipped contents of {@link EndTags#CRYSTALITE_SET}. {@code hasFullSet} reads nothing else, so
     * if the generated tag file drifts from the {@code addTags} calls on the item definitions - the
     * usual cause being datagen not re-run - the set bonus silently stops working, and every
     * end-to-end test of it fails with a far less obvious message than this one.
     */
    @GameTest
    public void crystaliteSetTagContainsEveryPiece(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        for (Piece piece : crystalite()) {
            if (!new ItemStack(piece.item()).is(EndTags.CRYSTALITE_SET)) {
                failures.add(piece.name() + " is missing from " + EndTags.CRYSTALITE_SET.location());
            }
        }
        if (!new ItemStack(EndEquipmentItems.CRYSTALITE_ELYTRA).is(EndTags.CRYSTALITE_SET)) {
            failures.add("elytra_crystalite is missing from " + EndTags.CRYSTALITE_SET.location());
        }
        // The Aeternium elytra is a different material and must never complete a Crystalite set.
        if (new ItemStack(EndEquipmentItems.ARMORED_ELYTRA).is(EndTags.CRYSTALITE_SET)) {
            failures.add("elytra_armored is in " + EndTags.CRYSTALITE_SET.location()
                    + " - the Aeternium elytra is not a Crystalite piece");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "The Crystalite set tag no longer matches the items:\n - " + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    /**
     * Fire resistance is set-wide, not part of the elytra's protection trade: all four humanoid pieces
     * get it from {@code EndArmorItemTraitBuilder}'s fireproof flag, and the elytra passes the same flag
     * explicitly. The diamond control pins that this is BetterEnd's doing and not a vanilla default.
     */
    @GameTest
    public void everyCrystalitePieceIsFireproof(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final List<Piece> pieces = new ArrayList<>(crystalite());
        pieces.add(new Piece("elytra_crystalite", EndEquipmentItems.CRYSTALITE_ELYTRA, EquipmentSlot.CHEST));

        for (Piece piece : pieces) {
            if (new ItemStack(piece.item()).get(DataComponents.DAMAGE_RESISTANT) == null) {
                failures.add(piece.name() + " is not fireproof");
            }
        }
        if (new ItemStack(Items.DIAMOND_CHESTPLATE).get(DataComponents.DAMAGE_RESISTANT) != null) {
            failures.add("control: a diamond chestplate is fireproof - the assertion itself is wrong");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Crystalite fire resistance regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    /**
     * Both elytras carry knockback resistance, and it must apply in the chest slot they are worn in -
     * it was bound to {@code EquipmentSlotGroup.MAINHAND} and therefore only ever applied while the
     * elytra was *held*, which is to say never.
     * <p>
     * The upper bound is the second half of the fix: while the modifier was inert the declared value
     * drifted to 0.5, which live would have beaten a full netherite set (four pieces at 0.1) off a
     * single item. Anchoring to the vanilla control rather than to a literal keeps that honest.
     */
    @GameTest
    public void elytraKnockbackResistanceAppliesInTheChestSlot(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final double netheriteSet = 4 * knockbackResistance(Items.NETHERITE_CHESTPLATE, EquipmentSlot.CHEST);

        if (netheriteSet <= 0.0) {
            failures.add("control: vanilla netherite measured no knockback resistance - the test is broken");
        }

        for (Piece piece : List.of(
                new Piece("elytra_crystalite", EndEquipmentItems.CRYSTALITE_ELYTRA, EquipmentSlot.CHEST),
                new Piece("elytra_armored", EndEquipmentItems.ARMORED_ELYTRA, EquipmentSlot.CHEST)
        )) {
            final double worn = knockbackResistance(piece.item(), piece.slot());
            if (worn <= 0.0) {
                failures.add(piece.name() + ": knockback resistance does not apply while worn in "
                        + piece.slot() + " - it is bound to a slot the elytra can never occupy");
            } else if (worn >= netheriteSet) {
                failures.add(piece.name() + ": grants " + worn
                        + " knockback resistance, at or above a full netherite set's " + netheriteSet
                        + " - one chest piece must not out-resist four");
            }
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Elytra knockback resistance regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    /**
     * The protection an elytra trades away is read straight off its tooltip, so it has to be a number a
     * player recognises. Both values used to be derived by dividing the chestplate's - 8 / 1.2 and
     * 1.2 / 1.25 - which put 6.666666 armor and 0.96 toughness in front of the player. They are authored
     * outright now, and this pins them exactly so a divider cannot quietly come back.
     */
    @GameTest
    public void elytraProtectionValuesAreTooltipClean(GameTestHelper helper) {
        record Expected(String name, Item item, double armor, double toughness) {}

        final List<String> failures = new ArrayList<>();

        for (Expected e : List.of(
                new Expected("elytra_crystalite", EndEquipmentItems.CRYSTALITE_ELYTRA, 6.5, 1.0),
                new Expected("elytra_armored", EndEquipmentItems.ARMORED_ELYTRA, 7.5, 3.0)
        )) {
            requireExactly(failures, e.name() + " armor",
                    modifierValue(e.item(), Attributes.ARMOR, EquipmentSlot.CHEST), e.armor());
            requireExactly(failures, e.name() + " toughness",
                    modifierValue(e.item(), Attributes.ARMOR_TOUGHNESS, EquipmentSlot.CHEST), e.toughness());
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Elytra protection values are not tooltip-clean:\n - " + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    /** Every expected value here is exactly representable, so an exact comparison is the honest one. */
    private static void requireExactly(List<String> failures, String what, double actual, double expected) {
        if (actual != expected) {
            failures.add(what + " is " + actual + ", expected exactly " + expected
                    + " - trailing decimals read as noise on the tooltip");
        }
    }

    /**
     * Sums one attribute's modifiers for the given slot. Spelled out rather than using
     * {@code ItemAttributeModifiers#compute} so it reads the same on every branch this is ported to.
     */
    private static double modifierValue(Item item, Holder<Attribute> attribute, EquipmentSlot slot) {
        final ItemAttributeModifiers modifiers = new ItemStack(item).get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return 0.0;
        double total = 0.0;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().is(attribute) && entry.slot().test(slot)) {
                total += entry.modifier().amount();
            }
        }
        return total;
    }

    /** What {@code Attributes.KNOCKBACK_RESISTANCE} the item contributes while worn in {@code slot}. */
    private static double knockbackResistance(Item item, EquipmentSlot slot) {
        final ItemAttributeModifiers modifiers = new ItemStack(item).get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return 0.0;
        return modifiers.compute(Attributes.KNOCKBACK_RESISTANCE, 0.0, slot);
    }

    /** Sums what each piece contributes while worn in its own slot, exactly as the attribute map would. */
    private static double computeArmor(List<Piece> pieces) {
        double total = 0.0;
        for (Piece piece : pieces) {
            final ItemStack stack = new ItemStack(piece.item());
            final ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (modifiers == null) continue;
            total += modifiers.compute(Attributes.ARMOR, 0.0, piece.slot());
        }
        return total;
    }
}
