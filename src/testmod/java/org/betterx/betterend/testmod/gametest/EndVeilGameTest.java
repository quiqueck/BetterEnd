package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.effects.EndPotions;
import org.betterx.betterend.effects.EndStatusEffects;
import org.betterx.betterend.registry.EndEnchantments;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.testmod.mixin.EnderManInvoker;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code betterend:end_veil} end to end: the enchantment, the potion effect, and the brewing
 * recipes that are the only way to obtain the potion.
 * <p>
 * {@code EnderManMixin} short-circuits {@code EnderMan#isBeingStaredBy} on three independent conditions
 * - creative mode, the End Veil mob effect, and the End Veil enchantment on the head slot. The
 * enchantment and the effect are asserted separately, and
 * {@link #bareHeadedPlayerStillAngersEnderman} is the control that makes them mean anything: a mixin
 * that returned {@code false} unconditionally would satisfy every positive test on its own.
 * <p>
 * {@code isBeingStaredBy} is {@code PLAYER_NOT_WEARING_DISGUISE_ITEM && player.isLookingAtMe(this,
 * 0.025, ...)}, so the player has to genuinely face the enderman with line of sight - hence the
 * {@code lookAt} call in {@link #starer}. Aiming is what the control proves.
 */
public class EndVeilGameTest {
    private static final BlockPos PLAYER_POS = new BlockPos(1, 2, 1);
    private static final BlockPos ENDERMAN_POS = new BlockPos(1, 2, 4);
    /** Long enough for the spawned enderman to land before the player takes aim. */
    private static final int SETTLE_TICKS = 10;

    private static boolean isStaredAt(EnderMan enderman, ServerPlayer player) {
        return ((EnderManInvoker) enderman).betterend_testmod$isBeingStaredBy(player);
    }

    private static EnderMan enderman(GameTestHelper helper) {
        // spawnWithNoFreeWill keeps the enderman from wandering out of the structure mid-test.
        return helper.spawnWithNoFreeWill(EntityTypes.ENDERMAN, ENDERMAN_POS);
    }

    /** A survival player inside the structure, bare-headed. Aim it with {@link #aimAt} once settled. */
    private static ServerPlayer starer(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER_POS);
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        return player;
    }

    /**
     * Points the player at the enderman's eyes. Must run after both entities have settled: the enderman
     * spawns slightly above the floor and falls, and an aim taken before it lands is off by enough
     * (~15 degrees at this range) to miss the 0.025 stare tolerance entirely.
     */
    private static void aimAt(ServerPlayer player, EnderMan target) {
        MockPlayers.lookAt(player, target.getEyePosition());
    }

    /**
     * The control. A plain survival player looking straight at an enderman must count as staring,
     * otherwise every other assertion in this class is vacuous.
     */
    @GameTest(maxTicks = 100)
    public void bareHeadedPlayerStillAngersEnderman(GameTestHelper helper) {
        final EnderMan enderman = enderman(helper);
        final ServerPlayer player = starer(helper);

        helper.startSequence()
              .thenIdle(SETTLE_TICKS)
              .thenExecute(() -> {
                  aimAt(player, enderman);
                  if (!isStaredAt(enderman, player)) {
                      throw helper.assertionException(Component.literal(
                              "Control failed: a bare-headed survival player looking straight at the "
                                      + "enderman was not treated as staring, so the End Veil tests "
                                      + "cannot tell working from broken"
                      ));
                  }
              })
              .thenSucceed();
    }

    @GameTest(maxTicks = 100)
    public void endVeilEnchantmentHidesTheStare(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final EnderMan enderman = enderman(helper);
        final ServerPlayer player = starer(helper);

        final ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
        final boolean enchanted = EnchantmentUtils.enchantInWorld(
                helmet,
                EndEnchantments.END_VEIL.key(),
                1,
                helper.getLevel().registryAccess()
        );
        if (!enchanted) {
            failures.add("could not apply betterend:end_veil - the enchantment is not registered");
        }
        // The mixin does not look at the enchantment directly, it asks whether any enchantment on the
        // stack contributes the END_VEIL_STATE effect component. Assert that link explicitly, so a
        // datagen change that drops `.withEffect(END_VEIL_STATE)` is caught here and not in the wild.
        if (!EnchantmentHelper.has(helmet, EndEnchantments.END_VEIL_STATE)) {
            failures.add("the enchanted helmet does not carry the END_VEIL_STATE effect component");
        }

        player.setItemSlot(EquipmentSlot.HEAD, helmet);

        helper.startSequence()
              .thenIdle(SETTLE_TICKS)
              .thenExecute(() -> {
                  aimAt(player, enderman);
                  if (isStaredAt(enderman, player)) {
                      failures.add("an enderman still reacted to a player wearing an End Veil helmet");
                  }
                  failIfAny(helper, "End Veil enchantment regression", failures);
              })
              .thenSucceed();
    }

    @GameTest(maxTicks = 100)
    public void endVeilEffectHidesTheStare(GameTestHelper helper) {
        final EnderMan enderman = enderman(helper);
        final ServerPlayer player = starer(helper);
        player.addEffect(new MobEffectInstance(EndStatusEffects.END_VEIL, 400));

        helper.startSequence()
              .thenIdle(SETTLE_TICKS)
              .thenExecute(() -> {
                  aimAt(player, enderman);
                  if (isStaredAt(enderman, player)) {
                      throw helper.assertionException(Component.literal(
                              "an enderman still reacted to a player under the End Veil effect"
                      ));
                  }
              })
              .thenSucceed();
    }

    /**
     * The End Veil potion is only reachable by brewing, so a missing mix silently makes the whole
     * mechanic unobtainable in survival. Asserts the mixes directly against {@code PotionBrewing}
     * rather than driving a brewing stand for 400 ticks.
     */
    @GameTest
    public void endVeilPotionsAreBrewable(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        requireMix(helper, Potions.AWKWARD, new ItemStack(EndResourceItems.ENDER_DUST),
                EndPotions.END_VEIL, "awkward + ender dust", failures);
        requireMix(helper, EndPotions.END_VEIL, new ItemStack(Items.REDSTONE),
                EndPotions.LONG_END_VEIL, "end veil + redstone", failures);

        failIfAny(helper, "End Veil brewing regression", failures);
        helper.succeed();
    }

    private static void requireMix(
            GameTestHelper helper,
            Holder<Potion> from,
            ItemStack reagent,
            Holder<Potion> expected,
            String what,
            List<String> failures
    ) {
        final ItemStack input = potion(from);
        if (!helper.getLevel().potionBrewing().hasMix(input, reagent)) {
            failures.add(what + ": no brewing mix is registered");
            return;
        }

        final ItemStack result = helper.getLevel().potionBrewing().mix(reagent, input);
        final PotionContents contents = result.get(DataComponents.POTION_CONTENTS);
        final Holder<Potion> got = contents == null ? null : contents.potion().orElse(null);
        if (got == null || !got.value().equals(expected.value())) {
            failures.add(what + ": brewed into " + (got == null ? "nothing" : got.getRegisteredName())
                    + " instead of " + expected.getRegisteredName());
        }
    }

    private static ItemStack potion(Holder<Potion> potion) {
        final ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
