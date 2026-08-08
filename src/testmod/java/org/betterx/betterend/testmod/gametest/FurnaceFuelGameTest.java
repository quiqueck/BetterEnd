package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.complexmaterials.EndWoodenComplexMaterial;
import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;

import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Regression coverage for {@code charcoal_block} actually burning at its declared fuel time.
 * <p>
 * Per {@code org.betterx.bclib.api.v2.FuelValueRegistration}'s own file comment: the hook that turns a
 * block's {@code Fuel} interface into a real, usable furnace
 * burn time used to be wired from a datagen-only provider, so nothing in BCLib, BetterEnd or
 * BetterNether ever actually got the burn time it declared - a silent, total regression that only
 * datagen output (never read at runtime) would have shown as "working". This asserts the number the
 * *game* will actually use, {@code level.fuelValues().burnDuration(...)}, not the block's own declared
 * {@code getFuelTime()} - the two only agree if the registration hook is wired correctly.
 */
public class FurnaceFuelGameTest {
    @GameTest
    public void charcoalBlockBurnsAtItsDeclaredFuelTime(GameTestHelper helper) {
        final ItemStack stack = new ItemStack(EndMetalBlocks.CHARCOAL_BLOCK);
        final int burnDuration = helper.getLevel().fuelValues().burnDuration(stack);

        // 16000 ticks - vanilla coal_block's burn time, which CharcoalBlock.getFuelTime() matches.
        if (burnDuration < 16000) {
            throw helper.assertionException(Component.literal(
                    "level.fuelValues().burnDuration(charcoal_block) returned " + burnDuration
                            + ", expected at least 16000 - the block's declared fuel time is not reaching"
                            + " the actual furnace fuel-value registry"
            ));
        }
        helper.succeed();
    }

    /**
     * The wooden slots {@code WoodenBlockSet.FUEL_TICKS} declares, with the burn time that map's
     * {@code DefaultFuelTicks} tier resolves to ({@code DEFAULT_TICKS = 200} scaled by the tier's
     * multiplier/divisor). These are exact equality checks, not lower bounds: a wooden chair is not fuel
     * through any vanilla item tag, so the only thing that can produce a non-zero burn duration for it is
     * {@code FuelBlockTrait} having been applied by {@code WoodenBlockSet#fuelTrait} and having reached
     * Fabric's {@code FuelRegistryEvents.BUILD}. Before that trait existed on this branch every one of
     * these read {@code 0}.
     */
    private static final Map<SlotType, Integer> EXPECTED_TICKS = Map.ofEntries(
            Map.entry(SlotType.TABURET, 300),
            Map.entry(SlotType.CHAIR, 300),
            Map.entry(SlotType.BAR_STOOL, 300),
            Map.entry(SlotType.PLANKS, 300),
            Map.entry(SlotType.SLAB, 150),
            Map.entry(SlotType.BUTTON, 100),
            Map.entry(SlotType.DOOR, 200),
            Map.entry(SlotType.HANGING_SIGN, 800),
            // BetterEnd does not override WoodenBlockSet#fuelTrait, so - unlike BetterNether's nether
            // woods - its logs and bark stay in the map and burn at the log tier.
            Map.entry(SlotType.LOG, 300),
            Map.entry(SlotType.BARK, 300)
    );

    private static final Map<String, EndWoodenComplexMaterial> WOOD_MATERIALS = Map.ofEntries(
            Map.entry("mossy_glowshroom", EndWoodBlocks.MOSSY_GLOWSHROOM),
            Map.entry("pythadendron", EndWoodBlocks.PYTHADENDRON),
            Map.entry("end_lotus", EndWoodBlocks.END_LOTUS),
            Map.entry("lacugrove", EndWoodBlocks.LACUGROVE),
            Map.entry("dragon_tree", EndWoodBlocks.DRAGON_TREE),
            Map.entry("tenanea", EndWoodBlocks.TENANEA),
            Map.entry("helix_tree", EndWoodBlocks.HELIX_TREE),
            Map.entry("umbrella_tree", EndWoodBlocks.UMBRELLA_TREE),
            Map.entry("jellyshroom", EndWoodBlocks.JELLYSHROOM),
            Map.entry("lucernia", EndWoodBlocks.LUCERNIA)
    );

    /**
     * Wooden furniture and plank-derived building blocks are furnace fuel, at the tick counts
     * {@code WoodenBlockSet.FUEL_TICKS} declares.
     * <p>
     * This is the runtime half of the {@code FuelBlockTrait} port: the generated
     * {@code block_registrations.txt} only records the tick count the trait <em>carries</em>, while this
     * asserts the number a furnace will actually read out of {@code level.fuelValues()}, which only agrees
     * if {@code afterBlockRegistration} really registered the block with Fabric.
     */
    @GameTest
    public void woodenFurnitureAndPlankDerivedBlocksAreFurnaceFuel(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        WOOD_MATERIALS.forEach((name, material) -> EXPECTED_TICKS.forEach((slot, expected) -> {
            final Block block = material.getBlock(slot);
            // Not every End wood registers every slot; a missing slot is FurnitureParityGameTest's
            // business, not this test's.
            if (block == null) return;

            final int actual = helper.getLevel().fuelValues().burnDuration(new ItemStack(block));
            if (actual != expected) {
                failures.add(name + " " + slot.suffix() + ": burnDuration=" + actual
                        + ", expected " + expected);
            }
        }));

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "level.fuelValues().burnDuration disagreed with WoodenBlockSet.FUEL_TICKS for "
                            + failures.size() + " block(s): " + String.join("; ", failures)
            ));
        }
        helper.succeed();
    }

    /**
     * Leaves carry no {@code FuelBlockTrait} and are in no vanilla burn tag, so they must read 0.
     * <p>
     * The negative side of the pair: {@code WoodenBlockSet#fuelTrait} is now non-null by default, so a
     * regression that widened it into a blanket "every block the wood set touches burns" would still pass
     * the assertion above. These blocks sit in the same wood materials but outside
     * {@code WoodenBlockSet.FUEL_TICKS}, and pin that boundary.
     */
    @GameTest
    public void endLeavesAreNotFurnaceFuel(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final Map<String, Block> leaves = Map.of(
                "pythadendron_leaves", EndWoodBlocks.PYTHADENDRON_LEAVES,
                "lacugrove_leaves", EndWoodBlocks.LACUGROVE_LEAVES,
                "dragon_tree_leaves", EndWoodBlocks.DRAGON_TREE_LEAVES,
                "tenanea_leaves", EndWoodBlocks.TENANEA_LEAVES,
                "helix_tree_leaves", EndWoodBlocks.HELIX_TREE_LEAVES,
                "lucernia_leaves", EndWoodBlocks.LUCERNIA_LEAVES
        );

        leaves.forEach((name, block) -> {
            final int actual = helper.getLevel().fuelValues().burnDuration(new ItemStack(block));
            if (actual != 0) {
                failures.add(name + ": burnDuration=" + actual + ", expected 0");
            }
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "unexpected furnace fuel on " + failures.size() + " leaves block(s): "
                            + String.join("; ", failures)
            ));
        }
        helper.succeed();
    }
}
