package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.block.EndTerrainBlocks;
import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Regression test for "bone-mealing pallidium tiny results in pallidium full".
 * <p>
 * The four pallidium variants form a thickness chain: bone meal on one turns it into the next thicker
 * one, and only the full variant (which has no next level) falls through to {@code BonemealAPI} to grow
 * vegetation. {@code PallidiumBlock} stores that next block, handed to it at registration.
 * <p>
 * The registry-split migration (613651221) rewired {@code thin} and {@code tiny} to point at
 * {@code PALLIDIUM_FULL} instead of the next step, so a single bone meal on tiny skipped straight to
 * full - two growth stages at once. The blocks are deliberately declared thickest-first so each can name
 * the next one, which is what made the wrong wiring compile silently.
 * <p>
 * Bone meal is applied through {@code useItemOn} with a real (mock) player, because that is where the
 * upgrade lives - the console-driven checks used elsewhere cannot reach it.
 */
public class PallidiumGrowthGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void boneMealWalksTheThicknessChainOneStepAtATime(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        expectStep(helper, EndTerrainBlocks.PALLIDIUM_TINY, EndTerrainBlocks.PALLIDIUM_THIN, "tiny", "thin", failures);
        expectStep(helper, EndTerrainBlocks.PALLIDIUM_THIN, EndTerrainBlocks.PALLIDIUM_HEAVY, "thin", "heavy", failures);
        expectStep(helper, EndTerrainBlocks.PALLIDIUM_HEAVY, EndTerrainBlocks.PALLIDIUM_FULL, "heavy", "full", failures);

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Pallidium bone meal skipped a growth stage:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    /**
     * The full variant must NOT turn into anything else - it is the end of the chain, and its bone meal
     * is handed on to BonemealAPI to grow vegetation instead.
     */
    @GameTest
    public void fullPallidiumStaysFull(GameTestHelper helper) {
        helper.setBlock(POS, EndTerrainBlocks.PALLIDIUM_FULL);
        boneMeal(helper, POS);

        final Block after = helper.getBlockState(POS).getBlock();
        if (after != EndTerrainBlocks.PALLIDIUM_FULL) {
            throw helper.assertionException(Component.literal(
                    "bone meal on pallidium_full replaced it with " + name(after)
                            + " - full is the last stage and should keep its block"
            ));
        }
        helper.succeed();
    }

    private static void expectStep(
            GameTestHelper helper,
            Block from,
            Block expected,
            String fromName,
            String expectedName,
            List<String> failures
    ) {
        helper.setBlock(POS, from);
        boneMeal(helper, POS);

        final Block after = helper.getBlockState(POS).getBlock();
        if (after != expected) {
            failures.add("bone meal on pallidium_" + fromName + " produced " + name(after)
                    + ", expected pallidium_" + expectedName);
        }
    }

    private static void boneMeal(GameTestHelper helper, BlockPos pos) {
        final ServerPlayer player = MockPlayers.survival(helper, pos.east());
        final ItemStack boneMeal = new ItemStack(Items.BONE_MEAL, 8);
        helper.getBlockState(pos).useItemOn(
                boneMeal,
                helper.getLevel(),
                player,
                InteractionHand.MAIN_HAND,
                hit(helper, pos)
        );
    }

    private static String name(Block block) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    private static BlockHitResult hit(GameTestHelper helper, BlockPos pos) {
        final BlockPos abs = helper.absolutePos(pos);
        return new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
    }
}
