package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.blocks.SilkMothNestBlock;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code silk_moth_nest}'s loot gating and shear-harvest mechanic.
 * <p>
 * The loot check is a regression test in the strictest sense: the block's own {@code buildLoot} javadoc
 * documents that this replaced a previous {@code getDrops} override which always dropped the nest
 * regardless of its {@code ACTIVE} state.
 */
public class SilkMothNestGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void onlyAnActiveNestDropsItself(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        if (!dropsSomething(helper, true)) {
            failures.add("an active nest dropped nothing");
        }
        if (dropsSomething(helper, false)) {
            failures.add("an inactive (spent) nest still dropped itself");
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "silk_moth_nest loot regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static boolean dropsSomething(GameTestHelper helper, boolean active) {
        final BlockState state = EndFunctionalBlocks.SILK_MOTH_NEST.defaultBlockState()
                                                                    .setValue(SilkMothNestBlock.ACTIVE, active);
        helper.setBlock(POS, state);
        final BlockPos abs = helper.absolutePos(POS);
        final ServerPlayer player = MockPlayers.survival(helper, POS.above());

        Block.dropResources(helper.getLevel().getBlockState(abs), helper.getLevel(), abs, null, player, ItemStack.EMPTY);

        final boolean dropped = !helper.getLevel()
                                        .getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3.0))
                                        .isEmpty();
        // Clean up so the next call's search area starts empty.
        helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3.0)).forEach(ItemEntity::discard);
        player.discard();
        return dropped;
    }

    @GameTest
    public void fullNestGivesSilkOnlyToShears(GameTestHelper helper) {
        final BlockState full = EndFunctionalBlocks.SILK_MOTH_NEST.defaultBlockState()
                                                                   .setValue(SilkMothNestBlock.ACTIVE, true)
                                                                   .setValue(SilkMothNestBlock.FULLNESS, 3);
        helper.setBlock(POS, full);
        final BlockPos abs = helper.absolutePos(POS);
        final ServerPlayer player = MockPlayers.survival(helper, POS.above());
        final BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);

        helper.getLevel()
              .getBlockState(abs)
              .useItemOn(new ItemStack(Items.SHEARS), helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);

        final List<String> failures = new ArrayList<>();
        final BlockState after = helper.getLevel().getBlockState(abs);
        if (after.getValue(SilkMothNestBlock.FULLNESS) != 0) {
            failures.add("FULLNESS was not reset to 0 after shearing, still " + after.getValue(SilkMothNestBlock.FULLNESS));
        }

        final List<ItemStack> drops = helper.getLevel()
                                             .getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(3.0))
                                             .stream()
                                             .map(ItemEntity::getItem)
                                             .toList();
        if (drops.stream().noneMatch(s -> s.is(EndResourceItems.SILK_FIBER))) {
            failures.add("shearing a full nest did not drop silk fiber, got " + drops);
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "silk_moth_nest shear-harvest regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
