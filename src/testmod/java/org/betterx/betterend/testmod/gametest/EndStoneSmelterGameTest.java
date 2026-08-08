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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code end_stone_smelter}'s two recipe paths and its hopper I/O rules.
 * <p>
 * <b>On the "1 input is faster / 2 inputs give more" design claim</b>: both halves are asserted, but as
 * comparisons rather than as hardcoded tick counts, because neither number is a fixed ratio -
 * {@code EndStoneSmelterBlockEntity} sets a different {@code cookTimeIncrement} per tick for each path
 * (blasting: 2, alloying: 1) <em>and</em> reads a different total-cook-time formula per recipe type, so
 * the absolute figures move with whatever smelt time a given recipe declares. See
 * {@link #singleInputSmeltsFasterThanAVanillaBlastFurnace} (measured against a real Blast Furnace on the
 * identical recipe - iron ore finishes at tick 33 against the furnace's 100 as of writing) and
 * {@link #twoInputsYieldMoreThanTheSameOreBlastedOneAtATime} (the same two ore are worth 3 ingots split
 * across both slots against 2 fed one at a time). The plain per-path outcomes are pinned separately by
 * {@link #blastingPathProducesPlainResult} and {@link #alloyingPathTriplesTheResult}.
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
    /** Second block for the side-by-side comparisons: either a vanilla control or a second smelter. */
    private static final BlockPos COMPANION_POS = new BlockPos(4, 2, 1);

    // Vanilla's three-slot furnace layout, for the Blast Furnace control below.
    private static final int VANILLA_INGREDIENT_SLOT = 0;
    private static final int VANILLA_FUEL_SLOT = 1;
    private static final int VANILLA_RESULT_SLOT = 2;

    private static EndStoneSmelterBlockEntity smelter(GameTestHelper helper) {
        return smelter(helper, SMELTER_POS);
    }

    private static EndStoneSmelterBlockEntity smelter(GameTestHelper helper, BlockPos pos) {
        final BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(pos));
        if (!(be instanceof EndStoneSmelterBlockEntity smelter)) {
            throw helper.assertionException(Component.literal(
                    "end_stone_smelter did not create an EndStoneSmelterBlockEntity at " + pos
            ));
        }
        return smelter;
    }

    private static AbstractFurnaceBlockEntity vanillaFurnace(GameTestHelper helper, BlockPos pos) {
        final BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(pos));
        if (!(be instanceof AbstractFurnaceBlockEntity furnace)) {
            throw helper.assertionException(Component.literal(
                    "expected a vanilla furnace block entity at " + pos
            ));
        }
        return furnace;
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
     * Wet sponge is deliberately <em>not</em> smeltable here, and this asserts that as the intended
     * contract rather than as a known defect. The End Stone Smelter is a blast furnace (plus alloying),
     * and vanilla's wet-sponge-to-sponge recipe is a plain {@code RecipeType.SMELTING} recipe - a regular
     * furnace recipe. A vanilla Blast Furnace therefore cannot dry a sponge either, so neither does this
     * one: {@code quickCheckBlastFurnace} only ever consults {@code RecipeType.BLASTING} and never matches.
     * <p>
     * The control block makes that the actual assertion: a real Blast Furnace is run alongside with the
     * identical contents, and the two have to agree. Pinning it to vanilla's behaviour rather than to a
     * bare "does nothing" means a datapack that <em>does</em> add a blasting recipe for wet sponge moves
     * both blocks together instead of tripping this test.
     * <p>
     * That parity extends to the wet-sponge byproduct - the empty bucket in the fuel slot that vanilla
     * turns into a water bucket ({@code AbstractFurnaceBlockEntity.burn}, inherited by
     * {@code BlastFurnaceBlockEntity}). BetterEnd carries the same branch, and it is equally unreachable
     * here for the same reason it is unreachable in a vanilla Blast Furnace. It was still worth repairing
     * the destination slot it writes to - it wrote to a hardcoded {@code inventory.set(1, ...)}, correct
     * for vanilla's three-slot layout but {@code INGREDIENT_SLOT_B} in this block's four-slot one - so
     * that a datapack enabling the recipe gets vanilla behaviour instead of a clobbered ingredient slot.
     */
    @GameTest(maxTicks = 100)
    public void wetSpongeIsNoMoreSmeltableThanInAVanillaBlastFurnace(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        helper.setBlock(COMPANION_POS, Blocks.BLAST_FURNACE);
        final EndStoneSmelterBlockEntity smelter = smelter(helper);
        final AbstractFurnaceBlockEntity control = vanillaFurnace(helper, COMPANION_POS);

        // Identical contents on both sides, including the empty bucket the byproduct would convert.
        smelter.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.WET_SPONGE));
        smelter.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.BUCKET));
        control.setItem(VANILLA_INGREDIENT_SLOT, new ItemStack(Items.WET_SPONGE));
        control.setItem(VANILLA_FUEL_SLOT, new ItemStack(Items.BUCKET));

        helper.startSequence()
              .thenIdle(40)
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();

                  final boolean controlProduced = !control.getItem(VANILLA_RESULT_SLOT).isEmpty();
                  final boolean smelterProduced = !smelter.getItem(EndStoneSmelterMenu.RESULT_SLOT).isEmpty();
                  if (controlProduced != smelterProduced) {
                      failures.add("the End Stone Smelter and a vanilla Blast Furnace disagree on whether a wet"
                              + " sponge is smeltable (smelter produced=" + smelterProduced
                              + ", blast furnace produced=" + controlProduced + ")");
                  }
                  if (smelterProduced) {
                      failures.add("a wet sponge was smelted into "
                              + smelter.getItem(EndStoneSmelterMenu.RESULT_SLOT)
                              + " - wet sponge is a SMELTING recipe, so neither a blast furnace nor this"
                              + " block should accept it");
                  }

                  // The byproduct must not fire on either side, and must never touch ingredient slot B.
                  final ItemStack fuelSlot = smelter.getItem(EndStoneSmelterMenu.FUEL_SLOT);
                  if (!fuelSlot.is(Items.BUCKET)) {
                      failures.add("the empty bucket in the fuel slot became " + fuelSlot
                              + " without the sponge ever being smelted");
                  }
                  if (!smelter.getItem(EndStoneSmelterMenu.INGREDIENT_SLOT_B).isEmpty()) {
                      failures.add("ingredient slot B was written to ("
                              + smelter.getItem(EndStoneSmelterMenu.INGREDIENT_SLOT_B)
                              + ") - this is the vanilla-slot-index bug the byproduct branch used to have");
                  }

                  failIfAny(helper, "End Stone Smelter wet-sponge parity regression", failures);
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

    /**
     * The "one input is faster" half of the smelter's design claim, measured against a vanilla Blast
     * Furnace running the identical recipe rather than against a hardcoded tick count: both get one iron
     * ore and ample fuel, and the smelter has to finish strictly sooner.
     * <p>
     * The speed-up comes from two independent multipliers - {@code BlastingRecipeInfo.cookTimeIncrement()}
     * advances the timer by 2 per tick instead of 1, and {@code getTotalCookTime} divides the blasting
     * recipe's own {@code cookingTime} by 1.5 - so this asserts the observable outcome and reports both
     * measured tick counts on failure, rather than pinning the exact ratio those two produce.
     */
    @GameTest(maxTicks = 400)
    public void singleInputSmeltsFasterThanAVanillaBlastFurnace(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        helper.setBlock(COMPANION_POS, Blocks.BLAST_FURNACE);
        final EndStoneSmelterBlockEntity smelter = smelter(helper);
        final AbstractFurnaceBlockEntity furnace = vanillaFurnace(helper, COMPANION_POS);

        smelter.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 64));
        smelter.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.IRON_ORE));
        // Vanilla furnace slot layout: 0 = ingredient, 1 = fuel, 2 = result.
        furnace.setItem(VANILLA_INGREDIENT_SLOT, new ItemStack(Items.IRON_ORE));
        furnace.setItem(VANILLA_FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 64));

        final long[] smelterDoneAt = {-1};
        final long[] furnaceDoneAt = {-1};

        helper.startSequence()
              .thenWaitUntil(() -> {
                  if (smelterDoneAt[0] < 0 && !smelter.getItem(EndStoneSmelterMenu.RESULT_SLOT).isEmpty()) {
                      smelterDoneAt[0] = helper.getTick();
                  }
                  if (furnaceDoneAt[0] < 0 && !furnace.getItem(VANILLA_RESULT_SLOT).isEmpty()) {
                      furnaceDoneAt[0] = helper.getTick();
                  }
                  if (smelterDoneAt[0] < 0 || furnaceDoneAt[0] < 0) {
                      throw helper.assertionException(Component.literal("still cooking"));
                  }
              })
              .thenExecute(() -> {
                  final List<String> failures = new ArrayList<>();
                  if (smelterDoneAt[0] >= furnaceDoneAt[0]) {
                      failures.add("the End Stone Smelter is supposed to beat a vanilla Blast Furnace on the"
                              + " same recipe, but finished at tick " + smelterDoneAt[0]
                              + " against the furnace's " + furnaceDoneAt[0]);
                  }
                  failIfAny(helper, "End Stone Smelter single-input speed regression", failures);
              })
              .thenSucceed();
    }

    /**
     * The "two inputs give more" half of the design claim, asserted as a like-for-like comparison rather
     * than as two separate absolute counts: the same two iron ore either go through the single-input
     * blasting path one at a time (2 x 1 ingot) or fill both slots at once and hit {@code additional_iron}
     * (one alloy of 3 ingots, {@code outputCount(3)} in {@link org.betterx.datagen.betterend.recipes.AlloyingRecipesProvider}).
     * Splitting the ore across both slots has to be worth strictly more than feeding it one at a time,
     * which is the whole reason the alloying path exists.
     */
    @GameTest(maxTicks = 600)
    public void twoInputsYieldMoreThanTheSameOreBlastedOneAtATime(GameTestHelper helper) {
        helper.setBlock(SMELTER_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        helper.setBlock(COMPANION_POS, EndFunctionalBlocks.END_STONE_SMELTER);
        final EndStoneSmelterBlockEntity blasting = smelter(helper);
        final EndStoneSmelterBlockEntity alloying = smelter(helper, COMPANION_POS);

        // Same two iron ore either way - stacked in one slot, or one in each slot.
        blasting.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 64));
        blasting.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.IRON_ORE, 2));

        alloying.setItem(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.COAL_BLOCK, 64));
        alloying.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A, new ItemStack(Items.IRON_ORE));
        alloying.setItem(EndStoneSmelterMenu.INGREDIENT_SLOT_B, new ItemStack(Items.IRON_ORE));

        helper.startSequence()
              .thenWaitUntil(() -> {
                  final boolean blastingDone = blasting.getItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A).isEmpty()
                          && !blasting.getItem(EndStoneSmelterMenu.RESULT_SLOT).isEmpty();
                  final boolean alloyingDone = alloying.getItem(EndStoneSmelterMenu.INGREDIENT_SLOT_A).isEmpty()
                          && !alloying.getItem(EndStoneSmelterMenu.RESULT_SLOT).isEmpty();
                  if (!blastingDone || !alloyingDone) {
                      throw helper.assertionException(Component.literal("still cooking"));
                  }
              })
              .thenExecute(() -> {
                  final int blasted = blasting.getItem(EndStoneSmelterMenu.RESULT_SLOT).getCount();
                  final int alloyed = alloying.getItem(EndStoneSmelterMenu.RESULT_SLOT).getCount();
                  final List<String> failures = new ArrayList<>();
                  if (blasted != 2) {
                      failures.add("blasting two iron ore one at a time should yield 2 ingots, got " + blasted);
                  }
                  if (alloyed != 3) {
                      failures.add("alloying two iron ore should yield 3 ingots, got " + alloyed);
                  }
                  if (alloyed <= blasted) {
                      failures.add("filling both slots (" + alloyed + " ingots) is supposed to beat feeding the"
                              + " same two ore one at a time (" + blasted + " ingots)");
                  }
                  failIfAny(helper, "End Stone Smelter two-input yield regression", failures);
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
