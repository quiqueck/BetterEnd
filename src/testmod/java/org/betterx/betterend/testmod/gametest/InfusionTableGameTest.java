package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.blocks.entities.InfusionPedestalEntity;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.rituals.InfusionRitual;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the infusion table end to end: octagon geometry, recipe presence, and a real physical
 * {@code runed_flavolite} craft (8 pedestals placed, catalysts and primary input inserted via the same
 * {@code PedestalBlock#useItemOn}/{@code state.useItemOn(...)} a player's right-click drives, ritual
 * left to tick on its own).
 * <p>
 * Geometry, confirmed by reading the source rather than assumed: {@code PedestalBlockEntity#setLevel}
 * calls {@code linkRitual}, which runs {@code InfusionRitual#configure()} immediately - it scans all 8
 * neighbour positions for an existing {@code PedestalBlockEntity} <em>once</em>, at that moment. Placing
 * the outer ring of pedestals before the central {@code INFUSION_PEDESTAL} (not after) is what makes
 * that first scan actually find them; {@code InfusionRitual#isValid()} requires all 8 slots non-null
 * regardless of which ones the recipe itself declares a catalyst for.
 */
public class InfusionTableGameTest {
    private static final BlockPos CENTER = new BlockPos(4, 2, 4);

    @GameTest
    public void pedestalOctagonMatchesCatalystSlotIndices(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final var map = InfusionRitual.getMap();

        if (map.length != 8) {
            failures.add("PEDESTALS_MAP has " + map.length + " points, expected 8");
        } else {
            for (InfusionRecipe.CatalystSlot slot : InfusionRecipe.CatalystSlot.values()) {
                if (slot.index < 0 || slot.index >= map.length) {
                    failures.add(slot.key + ": index " + slot.index + " is out of range for an 8-point octagon");
                }
            }
            // NORTH (index 0) must be due north: zero east-offset, positive north-offset.
            final var north = map[InfusionRecipe.CatalystSlot.NORTH.index];
            if (north.x != 0) {
                failures.add("NORTH pedestal has a nonzero east/west offset (" + north.x + "), expected 0");
            }
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Infusion pedestal geometry regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void runedFlavoliteRecipeIsRegisteredAndProducesTheBlock(GameTestHelper helper) {
        final var holder = helper.getLevel()
                                  .recipeAccess()
                                  .byKey(net.minecraft.resources.ResourceKey.create(
                                          net.minecraft.core.registries.Registries.RECIPE,
                                          ResourceLocation.parse("betterend:runed_flavolite")
                                  ));
        if (holder.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "betterend:runed_flavolite infusion recipe is not registered at all"
            ));
        }

        final ItemStack result;
        try {
            @SuppressWarnings({"unchecked", "rawtypes"})
            final Recipe raw = holder.get().value();
            //noinspection unchecked
            result = (ItemStack) raw.assemble(null, helper.getLevel().registryAccess());
        } catch (Exception e) {
            throw helper.assertionException(Component.literal(
                    "betterend:runed_flavolite threw while assembling with a null input: " + e
            ));
        }

        if (!result.is(EndStoneBlocks.FLAVOLITE_RUNED.asItem())) {
            throw helper.assertionException(Component.literal(
                    "betterend:runed_flavolite assembles to " + result + " instead of flavolite_runed"
            ));
        }
        helper.succeed();
    }

    /**
     * The real thing: build the ring, insert catalysts, insert the primary input, let the ritual tick
     * for its full 100-tick infusion time, and confirm both the crafted block and
     * {@code BECriteria.INFUSION_FINISHED} land.
     */
    @GameTest(maxTicks = 200)
    public void buildingAndFillingTheRitualCraftsRunedFlavolite(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.inLevel(helper, CENTER.east(4));

        // Outer ring first - see the class javadoc on why the order matters.
        for (var point : InfusionRitual.getMap()) {
            final BlockPos pos = CENTER.relative(Direction.EAST, point.x).relative(Direction.NORTH, point.y);
            helper.setBlock(pos, EndStoneBlocks.FLAVOLITE.getBlock(StoneMaterial.PEDESTAL));
        }
        helper.setBlock(CENTER, EndFunctionalBlocks.INFUSION_PEDESTAL);

        final List<String> failures = new ArrayList<>();

        // Catalysts: only the 4 the recipe declares (NORTH/EAST/SOUTH/WEST).
        for (InfusionRecipe.CatalystSlot slot : List.of(
                InfusionRecipe.CatalystSlot.NORTH, InfusionRecipe.CatalystSlot.EAST,
                InfusionRecipe.CatalystSlot.SOUTH, InfusionRecipe.CatalystSlot.WEST
        )) {
            final var point = InfusionRitual.getMap()[slot.index];
            final BlockPos pos = CENTER.relative(Direction.EAST, point.x).relative(Direction.NORTH, point.y);
            useOn(helper, player, pos, new ItemStack(EndResourceItems.CRYSTAL_SHARDS));
        }

        // Primary input, last - this is what actually calls checkRitual() with every catalyst already
        // in place and read by the initial configure() scan.
        useOn(helper, player, CENTER, new ItemStack(EndStoneBlocks.FLAVOLITE.getBlock(
                de.ambertation.wover.sets.api.blocks.SlotType.POLISHED
        )));

        final BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(CENTER));
        if (!(be instanceof InfusionPedestalEntity pedestal)) {
            throw helper.assertionException(Component.literal(
                    "infusion_pedestal did not create an InfusionPedestalEntity"
            ));
        }
        if (!pedestal.hasRitual() || !pedestal.getRitual().isValid()) {
            failIfAny(helper, "Infusion ritual regression", List.of(
                    "the ritual never became valid after building the full ring - isValid() requires all"
                            + " 8 pedestal blocks to exist, check the outer ring placement"
            ));
        }
        if (!pedestal.getRitual().hasRecipe()) {
            failIfAny(helper, "Infusion ritual regression", List.of(
                    "no recipe matched after inserting catalysts and the primary input - expected"
                            + " runed_flavolite to match"
            ));
        }

        helper.startSequence()
              .thenIdle(110) // recipe time is 100 ticks; a small margin for tick-order slack
              .thenExecute(() -> {
                  final ItemStack resultItem = pedestal.getItem(0);
                  if (!resultItem.is(EndStoneBlocks.FLAVOLITE_RUNED.asItem())) {
                      failures.add("central pedestal holds " + resultItem + " after 110 ticks, expected"
                              + " flavolite_runed");
                  }
                  requireDone(helper, player, "infusion_finished", failures);
                  failIfAny(helper, "Infusion ritual regression", failures);
              })
              .thenSucceed();
    }

    private static void useOn(GameTestHelper helper, ServerPlayer player, BlockPos relativePos, ItemStack stack) {
        final BlockPos abs = helper.absolutePos(relativePos);
        final BlockState state = helper.getLevel().getBlockState(abs);
        final BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
        state.useItemOn(stack, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    }

    private static void requireDone(
            GameTestHelper helper,
            ServerPlayer player,
            String path,
            List<String> failures
    ) {
        final var holder = helper.getLevel().getServer().getAdvancements().get(ResourceLocation.parse("betterend:" + path));
        if (holder == null) {
            failures.add("betterend:" + path + " is not loaded at all");
            return;
        }
        final var progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            final List<String> remaining = new ArrayList<>();
            progress.getRemainingCriteria().forEach(remaining::add);
            failures.add("betterend:" + path + " was not awarded - still missing " + remaining);
        }
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
