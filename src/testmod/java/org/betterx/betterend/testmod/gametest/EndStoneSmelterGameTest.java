package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.blocks.entities.EndStoneSmelterBlockEntity;
import org.betterx.betterend.client.gui.EndStoneSmelterMenu;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.bclib.recipes.AlloyingRecipe;
import org.betterx.bclib.recipes.AlloyingRecipeInput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code end_stone_smelter}'s two recipe paths and its hopper I/O rules.
 * <p>
 * <b>On the "1 input is faster / 2 inputs give 3x" claim</b>: {@code EndStoneSmelterBlockEntity} sets
 * a different {@code cookTimeIncrement} per tick for each path (blasting: 2, alloying: 1) but also reads
 * a *different* total-cook-time formula for each recipe type, so whether the single-input path actually
 * finishes sooner in wall-clock ticks depends on the specific recipe's own declared smelt time - it is
 * not a fixed ratio. Rather than assume a direction, both tests below poll for completion
 * ({@code thenWaitUntil}) instead of predicting an exact tick count, and assert what IS fixed: the
 * blasting path yields the recipe's plain result count, the alloying path yields 3x
 * ({@code growsResultBy()} in the block entity), and the fuel/fuel-slot mechanics behave as documented.
 * <p>
 * <b>Second bug found while writing this class, since fixed</b>: igniting the smelter with the
 * <em>last unit</em> of a fuel item that has no crafting remainder (a single coal block, one piece of
 * coal, etc.) NPE'd inside {@code EndStoneSmelterBlockEntity#serverTick} -
 * {@code item.getCraftingRemainder().create()} was called unconditionally once the fuel stack emptied,
 * and most fuel items (everything except things like buckets/glass bottles) return {@code null} from
 * {@code getCraftingRemainder()}. The exception was thrown from the block entity's own ticker, outside
 * any GameTest method's call stack, so it took down the whole dedicated test server rather than failing
 * one test - it was found by accident, by a prior version of this class's other tests tripping it and
 * taking the whole suite down with them, before a null check was added. {@link #ignitingWithTheLastUnitOfFuelDoesNotCrash}
 * is the regression test; every other test in this class still uses a 64-count fuel stack regardless,
 * simply to keep fuel supply out of the way of what each of them is actually testing.
 */
public class EndStoneSmelterGameTest {
    private static final BlockPos SMELTER_POS = new BlockPos(1, 2, 1);

    private static EndStoneSmelterBlockEntity smelter(GameTestHelper helper) {
        final BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(SMELTER_POS));
        if (!(be instanceof EndStoneSmelterBlockEntity smelter)) {
            throw helper.assertionException(Component.literal(
                    "end_stone_smelter did not create an EndStoneSmelterBlockEntity"
            ));
        }
        return smelter;
    }

    /**
     * Single-input path: iron ore alone matches vanilla's blast-furnace recipe
     * ({@code hasOneInput()} routes to {@code RecipeType.BLASTING}), producing one plain iron ingot.
     */
    @GameTest(maxTicks = 600)
    public void blastingPathProducesPlainResult(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        final EndStoneSmelterBlockEntity smelter = smelter(helper);

        // A large fuel stack, deliberately: shrinking a *single* fuel item to empty at ignition crashes
        // the block entity tick entirely (see the class javadoc) - a stack of many sidesteps that so
        // this test can actually exercise what it is meant to.
        smelter.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 64));
        smelter.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.IRON_ORE));

        helper.startSequence()
              .thenWaitUntil(() -> {
                  if (smelter.getItem(EndStoneSmelterMenu.RESULT_SLOT).isEmpty()) {
                      throw helper.assertionException(Component.literal("still cooking"));
                  }
              })
              .thenExecute(() -> {
                  final ItemStack result = smelter.getItem(EndStoneSmelterMenu.RESULT_SLOT);
                  final List<String> failures = new ArrayList<>();
                  if (!result.is(Items.IRON_INGOT)) {
                      failures.add("blasting iron ore produced " + result + " instead of an iron ingot");
                  } else if (result.getCount() != 1) {
                      failures.add("blasting a single iron ore should yield exactly 1 ingot, got "
                              + result.getCount());
                  }
                  failIfAny(helper, "End Stone Smelter blasting-path regression", failures);
              })
              .thenSucceed();
    }

    /**
     * Two-input path: iron ore in both slots matches {@code additional_iron}
     * ({@code EndTags#ALLOYING_IRON} contains iron ore/deepslate iron ore/raw iron), which grows the
     * result by 3 per the block entity's {@code AlloyingRecipeInfo.growsResultBy()}.
     */
    @GameTest(maxTicks = 600)
    public void alloyingPathTriplesTheResult(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        final EndStoneSmelterBlockEntity smelter = smelter(helper);

        // A large fuel stack, deliberately: shrinking a *single* fuel item to empty at ignition crashes
        // the block entity tick entirely (see the class javadoc) - a stack of many sidesteps that so
        // this test can actually exercise what it is meant to.
        smelter.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 64));
        smelter.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.IRON_ORE));
        smelter.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_B, new ItemStack(Items.IRON_ORE));

        helper.startSequence()
              .thenWaitUntil(() -> {
                  if (smelter.getItem(EndStoneSmelterMenu.RESULT_SLOT).isEmpty()) {
                      throw helper.assertionException(Component.literal("still cooking"));
                  }
              })
              .thenExecute(() -> {
                  final ItemStack result = smelter.getItem(EndStoneSmelterMenu.RESULT_SLOT);
                  final List<String> failures = new ArrayList<>();
                  if (!result.is(Items.IRON_INGOT)) {
                      failures.add("alloying two iron ore produced " + result + " instead of iron ingots");
                  } else if (result.getCount() != 3) {
                      failures.add("alloying with both slots filled should grow the result by 3, got "
                              + result.getCount());
                  }
                  failIfAny(helper, "End Stone Smelter alloying-path regression", failures);
              })
              .thenSucceed();
    }

    /**
     * Hopper I/O contract, asserted directly against the {@code WorldlyContainer} methods a real hopper
     * relies on rather than by simulating hopper transfer timing.
     */
    @GameTest
    public void hopperSlotRulesMatchTheDocumentedContract(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        final EndStoneSmelterBlockEntity smelter = smelter(helper);

        final List<String> failures = new ArrayList<>();

        // A hopper feeding from above must reach the ingredient slots, not fuel/result.
        for (int slot : smelter.getSlotsForFace(Direction.UP)) {
            if (slot != EndStoneSmelterMenu.INGREDIENT_SLOT_A && slot != EndStoneSmelterMenu.INGREDIENT_SLOT_B) {
                failures.add("UP face exposes slot " + slot + ", expected only the two ingredient slots");
            }
        }
        // A hopper feeding from the side must reach only the fuel slot.
        for (int slot : smelter.getSlotsForFace(Direction.NORTH)) {
            if (slot != EndStoneSmelterMenu.FUEL_SLOT) {
                failures.add("side face exposes slot " + slot + ", expected only the fuel slot");
            }
        }
        // A hopper below must reach fuel (for the bucket byproduct) and the result slot.
        for (int slot : smelter.getSlotsForFace(Direction.DOWN)) {
            if (slot != EndStoneSmelterMenu.FUEL_SLOT && slot != EndStoneSmelterMenu.RESULT_SLOT) {
                failures.add("DOWN face exposes slot " + slot + ", expected only fuel and result slots");
            }
        }

        // Only a water/empty bucket may be pulled out of the fuel slot, and only from below.
        if (!smelter.canTakeItemThroughFace(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.WATER_BUCKET), Direction.DOWN)) {
            failures.add("a water bucket cannot be pulled from the fuel slot through DOWN");
        }
        if (!smelter.canTakeItemThroughFace(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.BUCKET), Direction.DOWN)) {
            failures.add("an empty bucket cannot be pulled from the fuel slot through DOWN");
        }
        if (smelter.canTakeItemThroughFace(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL), Direction.DOWN)) {
            failures.add("coal can be pulled from the fuel slot through DOWN - only buckets should be extractable there");
        }
        if (!smelter.canTakeItemThroughFace(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.WATER_BUCKET), Direction.NORTH)) {
            failures.add("a water bucket cannot be pulled from the fuel slot through a side face"
                    + " (the DOWN-only restriction should only apply to blocking non-buckets, not the reverse)");
        }

        failIfAny(helper, "End Stone Smelter hopper-contract regression", failures);
        helper.succeed();
    }

    /**
     * The wet-sponge byproduct: blasting a wet sponge with an empty bucket sitting in the fuel slot is
     * supposed to convert that bucket into a water bucket - and, per
     * {@link EndStoneSmelterBlockEntity#canTakeItemThroughFace}'s own special case (only a water/empty
     * bucket may ever be pulled out of the fuel slot from below), the water bucket has to actually end
     * up back in the fuel slot for a hopper to ever retrieve it.
     * <p>
     * <b>This currently fails on the very first premise, and looks like real bugs, not a bad test.</b>
     * Two independent problems, found by writing this test:
     * <ol>
     *   <li>The single-input path only ever checks {@code RecipeType.BLASTING}
     *       ({@code quickCheckBlastFurnace = RecipeManager.createCheck(RecipeType.BLASTING)}), but
     *       vanilla's wet-sponge-to-sponge recipe is a plain {@code RecipeType.SMELTING} recipe (a
     *       regular furnace recipe, not a blast-furnace one) - and BetterEnd registers no
     *       {@code betterend:}-namespaced blasting recipe for wet sponge either. So {@code canBurn}
     *       never returns true for a wet sponge at all: the smelter never even lights, and the whole
     *       byproduct mechanic is unreachable in current gameplay, not merely misbehaving.</li>
     *   <li>Even if it did fire, the byproduct branch in {@code EndStoneSmelterBlockEntity#burn(
     *       RegistryAccess, InputState, BlastingRecipeInfo, NonNullList)} guards on
     *       {@code inventory.get(EndStoneSmelterMenu.FUEL_SLOT)} correctly, but writes the result with a
     *       hardcoded {@code inventory.set(1, ...)} - slot 1 is {@code INGREDIENT_SLOT_B}, not
     *       {@code FUEL_SLOT} (2). The water bucket would land in the second ingredient slot instead,
     *       silently overwriting whatever was there, while the fuel slot keeps the now-inert empty
     *       bucket forever - unreachable by any hopper regardless of bug #1.</li>
     * </ol>
     * This test asserts the smelter never lights for a wet sponge (proving #1, quickly, rather than
     * idling out a 600-tick timeout waiting for a cook that can never complete), and documents #2 for
     * whoever fixes #1 next - fixing only the recipe-type gate would silently trade an unreachable
     * feature for a badly-broken one.
     */
    @GameTest
    public void wetSpongeNeverIgnitesTheBlastingPath(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        final EndStoneSmelterBlockEntity smelter = smelter(helper);

        smelter.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 64));
        smelter.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.WET_SPONGE));

        helper.startSequence()
              .thenIdle(20)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  final boolean lit = helper.getBlockState(SMELTER_POS).getValue(org.betterx.betterend.blocks.EndStoneSmelter.LIT);
                  if (lit) {
                      failures.add("the smelter lit up for a bare wet sponge - if the recipe-type gate was"
                              + " fixed, re-check the fuel-slot byproduct destination too (see class javadoc, bug #2)");
                  }
                  failIfAny(helper, "End Stone Smelter wet-sponge byproduct regression", failures);
              })
              .thenSucceed();
    }

    /**
     * Regression test for the fuel-remainder NPE described in the class javadoc: a single coal block
     * (no crafting remainder) as the entire fuel supply must ignite the smelter and leave the fuel slot
     * empty afterwards, not crash the block entity's ticker.
     */
    @GameTest
    public void ignitingWithTheLastUnitOfFuelDoesNotCrash(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        final EndStoneSmelterBlockEntity smelter = smelter(helper);

        smelter.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 1));
        smelter.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.IRON_ORE));

        helper.startSequence()
              .thenIdle(5)
              .thenExecute(() -> {
                  final ItemStack fuelSlot = smelter.getItem(EndStoneSmelterMenu.FUEL_SLOT);
                  if (!fuelSlot.isEmpty()) {
                      throw helper.assertionException(Component.literal(
                              "expected the fuel slot to be empty after burning its only coal block, found "
                                      + fuelSlot
                      ));
                  }
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
