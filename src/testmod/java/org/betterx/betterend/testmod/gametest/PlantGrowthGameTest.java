package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.registry.block.EndSaplingBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers bonemeal-ability and randomTick-driven growth for BetterEnd's saplings, generically over the
 * full set rather than one test per species.
 * <p>
 * Growth is driven by calling {@code BlockState#randomTick} directly instead of waiting for the game's
 * own passive random-tick scheduler: whether a merely-placed GameTest structure's chunks actually
 * receive random ticks (as opposed to only scheduled block ticks) is exactly the kind of thing the
 * project's own console-testing notes warn is easy to get wrong for a forceloaded region, and a direct
 * method call sidesteps needing to prove GameTest's own chunk-loading ticket type either way -
 * {@code FeatureSaplingBlock#randomTick} is the same method a real random tick invokes, whichever
 * chunk-ticking path reaches it. Growth is probabilistic per call (bonemeal-style staged growth), so
 * each sapling gets several attempts, not just one.
 * <p>
 * {@link #vanillaOakSaplingGrowsAsAControl} is the harness control: if it fails, the harness itself is
 * broken and a "BetterEnd saplings don't grow" result would mean nothing.
 */
public class PlantGrowthGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    // isBonemealSuccess is a flat 1/16 roll per call (FeatureSaplingBlock), so this needs real margin -
    // 40 attempts flaked in testing (~8% chance of zero successes at 1/16 odds); 200 leaves a ~1 in
    // 700,000 chance of a false failure.
    private static final int ATTEMPTS = 200;

    private static final Map<String, Block> SAPLINGS = new LinkedHashMap<>();

    static {
        SAPLINGS.put("mossy_glowshroom", EndSaplingBlocks.MOSSY_GLOWSHROOM_SAPLING);
        SAPLINGS.put("pythadendron", EndSaplingBlocks.PYTHADENDRON_SAPLING);
        SAPLINGS.put("lacugrove", EndSaplingBlocks.LACUGROVE_SAPLING);
        SAPLINGS.put("dragon_tree", EndSaplingBlocks.DRAGON_TREE_SAPLING);
        SAPLINGS.put("tenanea", EndSaplingBlocks.TENANEA_SAPLING);
        SAPLINGS.put("helix_tree", EndSaplingBlocks.HELIX_TREE_SAPLING);
        SAPLINGS.put("umbrella_tree", EndSaplingBlocks.UMBRELLA_TREE_SAPLING);
        SAPLINGS.put("lucernia", EndSaplingBlocks.LUCERNIA_SAPLING);
        SAPLINGS.put("hydralux", EndSaplingBlocks.HYDRALUX_SAPLING);
    }

    @GameTest
    public void everySaplingIsBonemealable(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        SAPLINGS.forEach((name, block) -> {
            if (!(block instanceof BonemealableBlock)) {
                failures.add(name + "_sapling does not implement BonemealableBlock at all");
                return;
            }
            helper.setBlock(POS, block);
            final BlockPos abs = helper.absolutePos(POS);
            final BonemealableBlock bonemealable = (BonemealableBlock) block;
            final BlockState state = helper.getLevel().getBlockState(abs);
            if (!bonemealable.isValidBonemealTarget(helper.getLevel(), abs, state)) {
                failures.add(name + "_sapling is not a valid bonemeal target when freshly placed");
            }
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Sapling bonemeal-target regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void vanillaOakSaplingGrowsAsAControl(GameTestHelper helper) {
        if (!growsWithinAttempts(helper, Blocks.OAK_SAPLING)) {
            throw helper.assertionException(Component.literal(
                    "Control failed: a vanilla oak sapling did not grow/advance within " + ATTEMPTS
                            + " randomTick calls - the harness itself is broken, not BetterEnd"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void everySaplingAdvancesUnderRandomTick(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        SAPLINGS.forEach((name, block) -> {
            if (!growsWithinAttempts(helper, block)) {
                failures.add(name + "_sapling did not advance/grow within " + ATTEMPTS + " randomTick calls");
            }
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Sapling growth regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    /** Places {@code sapling}, calls randomTick repeatedly, and reports whether anything changed. */
    private static boolean growsWithinAttempts(GameTestHelper helper, Block sapling) {
        helper.setBlock(POS, sapling);
        final BlockPos abs = helper.absolutePos(POS);
        final RandomSource random = helper.getLevel().getRandom();

        for (int i = 0; i < ATTEMPTS; i++) {
            final BlockState before = helper.getLevel().getBlockState(abs);
            before.randomTick(helper.getLevel(), abs, random);
            final BlockState after = helper.getLevel().getBlockState(abs);
            if (!after.equals(before)) {
                return true;
            }
        }
        return false;
    }
}
