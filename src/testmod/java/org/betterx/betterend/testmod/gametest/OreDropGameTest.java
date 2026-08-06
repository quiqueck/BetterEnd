package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.block.EndOreBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers tool-tier gating and silk touch for BetterEnd's ores.
 * <p>
 * <b>{@code Block.dropResources} does not enforce tool-tier gating at all</b> - confirmed by reading its
 * bytecode: it runs straight through to {@code Block.getDrops}, which only ever sets the
 * {@code LootContextParams.TOOL} parameter and runs the loot table. The actual "is this even the right
 * tool" check ({@code state.requiresCorrectToolForDrops() && !tool.isCorrectToolForDrops(state)}) lives
 * one level up, in the real player-mining code path, before {@code getDrops}/{@code dropResources} is
 * ever reached - so calling {@code dropResources} directly with a wooden pickaxe (as an earlier version
 * of this test did) drops the item regardless, which is a property of the API, not a BetterEnd bug.
 * {@link #woodenPickaxeIsNotAValidToolForAmberOre} instead asserts the actual gate condition
 * ({@code requiresCorrectToolForDrops()}/{@code isCorrectToolForDrops()}) directly, which is what real
 * mining consults; {@code stoneToolDropsSomethingFromAmberOre} and {@code silkTouchDropsTheOreBlockItself}
 * still use {@code dropResources} for the drop-*content* checks, the same way {@code RubyFireGameTest}
 * does in BetterNether, since content is exactly what that API does compute correctly.
 * <p>
 * Fortune is deliberately not covered: it only changes the drop *count* probabilistically, which would
 * need many trials to assert reliably without flaking, and count-scaling loot functions are shared
 * vanilla machinery rather than anything BetterEnd-specific to regress.
 */
public class OreDropGameTest {
    private static final BlockPos ORE_POS = new BlockPos(1, 2, 1);

    private static List<ItemEntity> breakAndCollectDrops(
            GameTestHelper helper,
            Block ore,
            ServerPlayer player,
            net.minecraft.world.item.ItemStack tool
    ) {
        helper.setBlock(ORE_POS, ore);
        final BlockPos abs = helper.absolutePos(ORE_POS);
        final BlockState state = helper.getLevel().getBlockState(abs);

        Block.dropResources(state, helper.getLevel(), abs, null, player, tool);

        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3.0));
    }

    @GameTest
    public void woodenPickaxeIsNotAValidToolForAmberOre(GameTestHelper helper) {
        helper.setBlock(ORE_POS, EndOreBlocks.AMBER_ORE);
        final BlockState state = helper.getBlockState(ORE_POS);
        final net.minecraft.world.item.ItemStack wooden =
                new net.minecraft.world.item.ItemStack(Items.WOODEN_PICKAXE);

        final List<String> failures = new ArrayList<>();
        if (!state.requiresCorrectToolForDrops()) {
            failures.add("amber_ore does not require a correct tool for drops at all"
                    + " (NEEDS_STONE_TOOL would then have no effect on drops)");
        }
        if (wooden.isCorrectToolForDrops(state)) {
            failures.add("a wooden pickaxe is considered a correct tool for amber_ore,"
                    + " despite the NEEDS_STONE_TOOL tag");
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "amber_ore tool-tier gating regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void stoneToolDropsSomethingFromAmberOre(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, ORE_POS.above());
        final List<ItemEntity> drops = breakAndCollectDrops(
                helper, EndOreBlocks.AMBER_ORE, player, new net.minecraft.world.item.ItemStack(Items.STONE_PICKAXE)
        );

        if (drops.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "a stone pickaxe (meets amber_ore's NEEDS_STONE_TOOL requirement) dropped nothing"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void silkTouchDropsTheOreBlockItself(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, ORE_POS.above());

        final net.minecraft.world.item.ItemStack pickaxe = new net.minecraft.world.item.ItemStack(Items.IRON_PICKAXE);
        final Holder<Enchantment> silkTouch = helper.getLevel()
                                                     .registryAccess()
                                                     .lookupOrThrow(Registries.ENCHANTMENT)
                                                     .getOrThrow(Enchantments.SILK_TOUCH);
        pickaxe.enchant(silkTouch, 1);

        final List<ItemEntity> drops = breakAndCollectDrops(helper, EndOreBlocks.AMBER_ORE, player, pickaxe);

        final List<String> failures = new ArrayList<>();
        if (drops.isEmpty()) {
            failures.add("a silk-touch pickaxe dropped nothing");
        } else if (drops.stream().noneMatch(e -> e.getItem().is(EndOreBlocks.AMBER_ORE.asItem()))) {
            failures.add("a silk-touch pickaxe did not drop the ore block itself, got "
                    + drops.stream().map(e -> e.getItem().toString()).toList());
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Silk touch ore-drop regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
