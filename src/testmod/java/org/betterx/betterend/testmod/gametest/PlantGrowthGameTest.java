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
 * broken and a "BetterEnd saplings don't grow" result would mean nothing. Unlike BetterEnd's saplings,
 * a vanilla one has a light requirement, so the control lights its own site rather than relying on the
 * gametest world happening to be in daylight - see {@link #lightGrowthSite}.
 */
public class PlantGrowthGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    // isBonemealSuccess is a flat 1/16 roll per call (FeatureSaplingBlock), so this needs real margin -
    // 40 attempts flaked in testing (~8% chance of zero successes at 1/16 odds); 200 leaves a ~1 in
    // 700,000 chance of a false failure.
    private static final int ATTEMPTS = 200;
    // SaplingBlock.BRIGHTNESS_FOR_SAPLING_GROWTH - inlined rather than referenced because the constant
    // is only public from 26.3 on and this file is kept identical across the version branches.
    private static final int MIN_BRIGHTNESS_FOR_SAPLING_GROWTH = 9;
    // Light updates are queued and flushed by the (threaded) light engine on a later level tick, so the
    // brightness at a freshly placed light source is not readable in the same tick.
    private static final int LIGHT_SETTLE_TICKS = 5;

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
        // Real soil, so the control site is one a vanilla sapling could actually occupy. randomTick
        // itself never consults canSurvive, but a control that stands on nothing proves less.
        helper.setBlock(POS.below(), Blocks.DIRT);
        lightGrowthSite(helper);

        // The light engine runs off the level tick, not inside setBlock, so the brightness the growth
        // check reads is only correct a tick or two after the light source was placed.
        helper.runAfterDelay(LIGHT_SETTLE_TICKS, () -> {
            final int brightness = helper.getLevel().getMaxLocalRawBrightness(helper.absolutePos(POS).above());
            if (brightness < MIN_BRIGHTNESS_FOR_SAPLING_GROWTH) {
                throw helper.assertionException(Component.literal(
                        "Harness precondition failed: the growth site is only lit to " + brightness
                                + " after " + LIGHT_SETTLE_TICKS + " ticks, vanilla saplings need "
                                + MIN_BRIGHTNESS_FOR_SAPLING_GROWTH
                                + " - the light source is missing or the light engine has not caught up"
                ));
            }

            if (!growsWithinAttempts(helper, Blocks.OAK_SAPLING)) {
                throw helper.assertionException(Component.literal(
                        "Control failed: a vanilla oak sapling did not grow/advance within " + ATTEMPTS
                                + " randomTick calls - the harness itself is broken, not BetterEnd"
                                + " (brightness above the sapling was "
                                + helper.getLevel().getMaxLocalRawBrightness(helper.absolutePos(POS).above())
                                + ", vanilla needs " + MIN_BRIGHTNESS_FOR_SAPLING_GROWTH + ")"
                ));
            }
            helper.succeed();
        });
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

    /**
     * Puts a glowstone block two above {@link #POS} so the position a sapling's growth check looks at
     * is lit to 14 no matter what the world is doing.
     * <p>
     * Vanilla's {@code SaplingBlock#randomTick} only advances when
     * {@code getMaxLocalRawBrightness(pos.above()) >= 9}. The GameTest structure is an 8x8x8 box of air
     * with open sky, so sky light there is 15 - but the effective brightness is
     * {@code skyLight - skyDarken}, and {@code skyDarken} is 11 at night. The gametest world under
     * {@code build/gametest} is a normal dedicated-server world that is never deleted between runs, so
     * its clock keeps accumulating: once the accumulated time lands in the night half of the day cycle
     * the control could never pass again, and every BetterEnd sapling result in this class stopped
     * meaning anything. Lighting the site ourselves makes the outcome depend on the block under test
     * rather than on how many times the suite has been run before.
     * <p>
     * BetterEnd's own saplings ({@code FeatureSaplingBlock}) ignore light entirely - they roll a flat
     * 1/16 per tick - so this only ever mattered for the vanilla control, which is exactly the point of
     * having a control, and {@link #everySaplingAdvancesUnderRandomTick} is deliberately left unlit.
     */
    private static void lightGrowthSite(GameTestHelper helper) {
        helper.setBlock(POS.above(2), Blocks.GLOWSTONE);
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
