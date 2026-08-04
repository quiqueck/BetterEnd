package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.EndAttributes;
import org.betterx.betterend.registry.item.EndEquipmentItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers everything the Crystalite set does through {@code LivingEntityMixin} - the per-piece effects,
 * the set bonus, and the blindness-resistance attribute - on real, ticking entities.
 * <p>
 * The subjects are villagers rather than mock players on purpose: all of this is
 * {@link LivingEntity}-level behaviour (the mixin targets {@code LivingEntity#tickEffects} and
 * {@code #canBeAffected}), and a GameTest mock player never processes equipment changes at all - see
 * {@link MockPlayers} for the details. A villager exercises exactly the code under test and actually
 * equips.
 * <p>
 * The boots effect and the set bonus are applied on {@code (tickCount & 63) == 0}, so every test idles
 * well past 64 ticks before asserting. The chestplate applies its effect every tick.
 */
public class CrystaliteEffectGameTest {
    /** Comfortably more than the 64-tick period the boots and the set bonus are gated on. */
    private static final int SETTLE_TICKS = 90;
    private static final BlockPos SUBJECT = new BlockPos(1, 2, 1);
    private static final BlockPos CONTROL = new BlockPos(4, 2, 4);

    private static Villager wearing(GameTestHelper helper, BlockPos at, Item... pieces) {
        final Villager villager = helper.spawnWithNoFreeWill(EntityType.VILLAGER, at);
        for (Item piece : pieces) {
            final ItemStack stack = new ItemStack(piece);
            villager.setItemSlot(villager.getEquipmentSlotForItem(stack), stack);
        }
        return villager;
    }

    private static void requireEffect(
            LivingEntity entity,
            Holder<MobEffect> effect,
            String what,
            List<String> failures
    ) {
        if (!entity.hasEffect(effect)) {
            failures.add(what + ": expected " + effect.getRegisteredName() + " but the wearer had none");
        }
    }

    @GameTest(maxTicks = 200)
    public void bootsGrantSwiftness(GameTestHelper helper) {
        final Villager subject = wearing(helper, SUBJECT, EndEquipmentItems.CRYSTALITE_BOOTS);
        final Villager control = wearing(helper, CONTROL, Items.NETHERITE_BOOTS);

        helper.startSequence()
              .thenIdle(SETTLE_TICKS)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  requireEffect(subject, MobEffects.SPEED, "crystalite_boots", failures);
                  if (control.hasEffect(MobEffects.SPEED)) {
                      failures.add("control: netherite boots also granted Speed");
                  }
                  failIfAny(helper, "Crystalite boots stopped granting Swiftness", failures);
              })
              .thenSucceed();
    }

    @GameTest(maxTicks = 200)
    public void chestplateGrantsDigSpeed(GameTestHelper helper) {
        final Villager subject = wearing(helper, SUBJECT, EndEquipmentItems.CRYSTALITE_CHESTPLATE);
        final Villager control = wearing(helper, CONTROL, Items.NETHERITE_CHESTPLATE);

        helper.startSequence()
              .thenIdle(SETTLE_TICKS)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  requireEffect(subject, MobEffects.HASTE, "crystalite_chestplate", failures);
                  if (control.hasEffect(MobEffects.HASTE)) {
                      failures.add("control: a netherite chestplate also granted Haste");
                  }
                  failIfAny(helper, "Crystalite chestplate stopped granting Dig Speed", failures);
              })
              .thenSucceed();
    }

    /**
     * The set bonus. Also asserts the negative case: three pieces must not be enough, otherwise
     * {@code CrystaliteArmor#hasFullSet} has silently stopped requiring all four.
     */
    @GameTest(maxTicks = 200)
    public void fullSetGrantsRegeneration(GameTestHelper helper) {
        final Villager full = wearing(
                helper, SUBJECT,
                EndEquipmentItems.CRYSTALITE_HELMET,
                EndEquipmentItems.CRYSTALITE_CHESTPLATE,
                EndEquipmentItems.CRYSTALITE_LEGGINGS,
                EndEquipmentItems.CRYSTALITE_BOOTS
        );
        final Villager partial = wearing(
                helper, CONTROL,
                EndEquipmentItems.CRYSTALITE_HELMET,
                EndEquipmentItems.CRYSTALITE_CHESTPLATE,
                EndEquipmentItems.CRYSTALITE_LEGGINGS
        );

        helper.startSequence()
              .thenIdle(SETTLE_TICKS)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  requireEffect(full, MobEffects.REGENERATION, "full crystalite set", failures);
                  if (partial.hasEffect(MobEffects.REGENERATION)) {
                      failures.add("three pieces granted the set bonus - hasFullSet no longer requires all four");
                  }
                  failIfAny(helper, "Crystalite set bonus regression", failures);
              })
              .thenSucceed();
    }

    /**
     * The Crystalite helmet's blindness resistance, asserted both as the attribute value and as the
     * behaviour that attribute is supposed to produce, against a bare-headed control.
     */
    @GameTest(maxTicks = 200)
    public void helmetGrantsBlindnessImmunity(GameTestHelper helper) {
        final Villager helmeted = wearing(helper, SUBJECT, EndEquipmentItems.CRYSTALITE_HELMET);
        final Villager bare = helper.spawnWithNoFreeWill(EntityType.VILLAGER, CONTROL);

        helper.startSequence()
              .thenIdle(SETTLE_TICKS)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();

                  final double resistance = helmeted.getAttributeValue(EndAttributes.BLINDNESS_RESISTANCE);
                  if (resistance <= 0.0) {
                      failures.add("crystalite_helmet: blindness_resistance was " + resistance + ", expected > 0");
                  }
                  if (helmeted.canBeAffected(new MobEffectInstance(MobEffects.BLINDNESS, 100))) {
                      failures.add("crystalite_helmet: the wearer can still be blinded");
                  }
                  // Control: without the helmet blindness must still land, otherwise the mixin is
                  // refusing it for everyone rather than for the attribute holder.
                  if (!bare.canBeAffected(new MobEffectInstance(MobEffects.BLINDNESS, 100))) {
                      failures.add("control: a bare-headed villager was also immune to blindness");
                  }

                  failIfAny(helper, "Crystalite blindness resistance regression", failures);
              })
              .thenSucceed();
    }

    /**
     * The equipped counterpart of {@link CrystaliteArmorGameTest}: what the wearer's armor attribute
     * actually adds up to. Anchored to a netherite-clad control rather than a magic constant -
     * Crystalite is the higher tier, so it must not come out below netherite.
     */
    @GameTest(maxTicks = 200)
    public void fullSetArmorBeatsNetherite(GameTestHelper helper) {
        final Villager crystalite = wearing(
                helper, SUBJECT,
                EndEquipmentItems.CRYSTALITE_HELMET,
                EndEquipmentItems.CRYSTALITE_CHESTPLATE,
                EndEquipmentItems.CRYSTALITE_LEGGINGS,
                EndEquipmentItems.CRYSTALITE_BOOTS
        );
        final Villager netherite = wearing(
                helper, CONTROL,
                Items.NETHERITE_HELMET,
                Items.NETHERITE_CHESTPLATE,
                Items.NETHERITE_LEGGINGS,
                Items.NETHERITE_BOOTS
        );

        helper.startSequence()
              .thenIdle(20)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  final double crystaliteArmor = crystalite.getAttributeValue(Attributes.ARMOR);
                  final double netheriteArmor = netherite.getAttributeValue(Attributes.ARMOR);

                  if (netheriteArmor <= 0.0) {
                      failures.add("control: a full netherite set measured " + netheriteArmor
                              + " armor - the test itself is broken");
                  } else if (crystaliteArmor < netheriteArmor) {
                      failures.add("a full Crystalite set measured " + crystaliteArmor
                              + " armor against netherite's " + netheriteArmor
                              + " - Crystalite is the higher tier and must not be weaker");
                  }

                  failIfAny(helper, "Crystalite armor value regression", failures);
              })
              .thenSucceed();
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
