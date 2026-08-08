package org.betterx.betterend.testmod.gametest;

import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.betterend.registry.block.EndTerrainBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Regression test for "bone meal on Shadow Grass only grows a plant on the block that was clicked".
 * <p>
 * The terrain blocks registered through {@code BonemealAPI.addSpreadableFeatures} carry a patch feature
 * ({@code wover:random_patch}, 9 tries over a 7x7 area - the same shape vanilla nylium uses). The bug was
 * that {@code BonemealNyliumLike#performBonemeal} asked {@code FeatureUtils} to place that feature with
 * {@code unchanged=false}, which unwraps a random patch down to the single block feature inside it (an
 * unwrap that only exists so a sapling's {@code GrowableFeature} can be found) and then placed that one
 * block at {@code pos.above()} - the patch's tries and spread were discarded entirely.
 * <p>
 * The test bone-meals the centre of a 7x7 platform many times over, clearing the layer above between
 * applications, and records every position that ever received a plant. A working patch covers most of
 * the platform; the bug pins every single plant to the clicked column.
 */
public class BonemealSpreadGameTest {
    /** Platform spans relative x/z 0..6 at y=1, so the clicked block is the centre of a 7x7 area. */
    private static final int PLATFORM_MIN = 0;
    private static final int PLATFORM_MAX = 6;
    private static final int GROUND_Y = 1;
    private static final int PLANT_Y = GROUND_Y + 1;
    private static final BlockPos CENTER = new BlockPos(3, GROUND_Y, 3);

    // Each application is 9 tries scattered over 7x7x3, and roughly half of those land on the wrong
    // y level, so a single application only grows a handful of plants. 40 applications make the
    // "did anything land away from the click" question statistically decisive without being slow.
    private static final int APPLICATIONS = 40;
    // With the patch intact, ~180 successful placements spread over 49 columns; requiring 12 distinct
    // columns and a plant at least 2 blocks away leaves an enormous margin, while the bug scores
    // exactly 1 distinct column and 0 distant ones.
    private static final int MIN_DISTINCT_COLUMNS = 12;

    private static final Map<String, Block> SPREADING_TERRAIN = new LinkedHashMap<>();

    static {
        SPREADING_TERRAIN.put("shadow_grass", EndTerrainBlocks.SHADOW_GRASS);
        SPREADING_TERRAIN.put("end_mycelium", EndTerrainBlocks.END_MYCELIUM);
        SPREADING_TERRAIN.put("jungle_moss", EndTerrainBlocks.JUNGLE_MOSS);
        SPREADING_TERRAIN.put("sangnum", EndTerrainBlocks.SANGNUM);
        SPREADING_TERRAIN.put("cave_moss", EndTerrainBlocks.CAVE_MOSS);
        SPREADING_TERRAIN.put("chorus_nylium", EndTerrainBlocks.CHORUS_NYLIUM);
        SPREADING_TERRAIN.put("crystal_moss", EndTerrainBlocks.CRYSTAL_MOSS);
        SPREADING_TERRAIN.put("pink_moss", EndTerrainBlocks.PINK_MOSS);
        SPREADING_TERRAIN.put("amber_moss", EndTerrainBlocks.AMBER_MOSS);
    }

    @GameTest(maxTicks = 200)
    public void shadowGrassBonemealSpreadsAcrossTheSurface(GameTestHelper helper) {
        final Set<BlockPos> covered = bonemealCoverage(helper, EndTerrainBlocks.SHADOW_GRASS);
        final List<String> failures = describeFailures("shadow_grass", covered);

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Shadow Grass bone meal did not spread:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest(maxTicks = 400)
    public void everySpreadingTerrainBlockScattersVegetation(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        SPREADING_TERRAIN.forEach((name, block) -> failures.addAll(
                describeFailures(name, bonemealCoverage(helper, block))
        ));

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Bone meal spreading regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static List<String> describeFailures(String name, Set<BlockPos> covered) {
        final List<String> failures = new ArrayList<>();
        if (covered.isEmpty()) {
            failures.add(name + " grew nothing at all in " + APPLICATIONS + " bone meal applications");
            return failures;
        }

        final boolean grewAwayFromClick = covered
                .stream()
                .anyMatch(p -> Math.max(Math.abs(p.getX() - CENTER.getX()), Math.abs(p.getZ() - CENTER.getZ())) >= 2);

        if (!grewAwayFromClick) {
            failures.add(name + " only grew on/next to the clicked column (" + covered.size()
                    + " distinct positions, none 2+ blocks away)");
        }
        if (covered.size() < MIN_DISTINCT_COLUMNS) {
            failures.add(name + " covered only " + covered.size() + " of 49 columns, expected at least "
                    + MIN_DISTINCT_COLUMNS);
        }
        return failures;
    }

    /**
     * Lays down a 7x7 platform of {@code terrain}, bone-meals its centre repeatedly and returns every
     * relative position that grew a plant. The layer above is wiped between applications because
     * {@code isValidBonemealTarget} requires air over the clicked block - without the wipe the first
     * plant to land on the centre would block every later application.
     */
    private static Set<BlockPos> bonemealCoverage(GameTestHelper helper, Block terrain) {
        final ServerLevel level = helper.getLevel();
        final Set<BlockPos> covered = new HashSet<>();

        for (int x = PLATFORM_MIN; x <= PLATFORM_MAX; x++) {
            for (int z = PLATFORM_MIN; z <= PLATFORM_MAX; z++) {
                helper.setBlock(new BlockPos(x, GROUND_Y, z), terrain);
            }
        }

        for (int attempt = 0; attempt < APPLICATIONS; attempt++) {
            clearPlantLayer(helper);
            BonemealAPI.INSTANCE.runSpreaders(
                    new ItemStack(Items.BONE_MEAL, 64),
                    level,
                    helper.absolutePos(CENTER),
                    false
            );

            for (int x = PLATFORM_MIN; x <= PLATFORM_MAX; x++) {
                for (int z = PLATFORM_MIN; z <= PLATFORM_MAX; z++) {
                    final BlockPos rel = new BlockPos(x, PLANT_Y, z);
                    if (!level.getBlockState(helper.absolutePos(rel)).isAir()) {
                        covered.add(rel);
                    }
                }
            }
        }

        clearPlantLayer(helper);
        return covered;
    }

    private static void clearPlantLayer(GameTestHelper helper) {
        for (int x = PLATFORM_MIN; x <= PLATFORM_MAX; x++) {
            for (int z = PLATFORM_MIN; z <= PLATFORM_MAX; z++) {
                helper.setBlock(new BlockPos(x, PLANT_Y, z), Blocks.AIR);
                // y_spread lets the patch aim one block higher than the surface; a plant can never
                // survive up there, but clear it anyway so a stray placement cannot skew a later pass.
                helper.setBlock(new BlockPos(x, PLANT_Y + 1, z), Blocks.AIR);
            }
        }
    }
}
