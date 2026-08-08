package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.blocks.entities.EternalPedestalEntity;
import org.betterx.betterend.blocks.RunedFlavolite;
import org.betterx.betterend.portal.PortalBuilder;
import org.betterx.betterend.registry.EndPortals;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.rituals.EternalRitual;

import de.ambertation.wover.test.api.gametest.MockPlayers;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers the eternal portal's pedestal geometry, its data-driven dimension linking, and - per direct
 * feedback that a test controlling a player should just build the real structure - a real physical
 * activation: 6 pedestals in the exact {@code EternalRitual.pedestalOffsets()} ring, a
 * {@code PortalBuilder.FRAME_POSITIONS} frame of {@code flavolite_runed_eternal} below them, and
 * {@code betterend:eternal_crystal} (the default portal key item, read from {@code portals.json}'s own
 * default) inserted into each pedestal via the same {@code state.useItemOn(...)} a player's right-click
 * drives.
 * <p>
 * Two direction conventions have to be kept straight, confirmed by reading {@code EternalRitual}
 * directly rather than assumed: {@code checkStructure} walks the pedestal ring with
 * {@code moveX=EAST, moveY=NORTH} for the X axis, but {@code checkFrame}/{@code PortalBuilder} walk the
 * frame with a single {@code moveDir=NORTH} for the X axis - different axes for different structures,
 * both anchored on the same {@code center}/{@code center.below()}.
 */
public class EternalPortalGameTest {
    private static final BlockPos CENTER = new BlockPos(8, 5, 8);

    @GameTest
    public void pedestalOffsetsFormASixPointRing(GameTestHelper helper) {
        final var offsets = EternalRitual.pedestalOffsets();
        if (offsets.size() != 6) {
            throw helper.assertionException(Component.literal(
                    "EternalRitual.pedestalOffsets() has " + offsets.size() + " points, expected 6"
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void portalConfigLinksToARealDimensionByDefault(GameTestHelper helper) {
        EndPortals.loadPortals();

        final List<String> failures = new ArrayList<>();
        if (EndPortals.getCount() == 0) {
            failures.add("no portals are configured at all - portals.json produced zero entries");
        } else {
            final ResourceLocation worldId = EndPortals.getWorldId(0);
            if (worldId == null) {
                failures.add("portal 0's target world id is null");
            }
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Eternal portal config regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    /**
     * Builds the real structure and inserts the real key item into every pedestal. Asserts the
     * source-side activation state ({@code EternalRitual#isActive}, the frame's obsidian-portal-style
     * {@code ACTIVE} property, and {@code PORTAL_ON}) rather than the far side of the link - finding or
     * generating a landing spot in the target Overworld searches real, normally-generated terrain
     * around wherever the GameTest structure happens to sit, which this test does not control and
     * should not need to in order to prove the ritual itself fires.
     */
    @GameTest(maxTicks = 200)
    public void buildingAndFillingAllSixPedestalsActivatesThePortal(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.inLevel(helper, CENTER.south(6));

        // Pedestal ring - checkStructure's own convention (X axis: EAST/NORTH).
        for (var point : EternalRitual.pedestalOffsets()) {
            final BlockPos pos = CENTER.relative(Direction.EAST, point.x).relative(Direction.NORTH, point.y);
            helper.setBlock(pos, EndFunctionalBlocks.ETERNAL_PEDESTAL);
        }

        // Frame - checkFrame/PortalBuilder's convention (X axis: NORTH only, mirrored both ways, one
        // level below the pedestal ring's center).
        final BlockPos framePos = CENTER.below();
        for (var point : PortalBuilder.FRAME_POSITIONS) {
            helper.setBlock(framePos.relative(Direction.NORTH, point.x).relative(Direction.UP, point.y),
                    PortalBuilder.FRAME);
            helper.setBlock(framePos.relative(Direction.NORTH, -point.x).relative(Direction.UP, point.y),
                    PortalBuilder.FRAME);
        }

        // Fill every pedestal with the key item - the last one placed triggers checkRitual ->
        // checkStructure -> activatePortal, exactly like a player's final right-click would.
        for (var point : EternalRitual.pedestalOffsets()) {
            final BlockPos pos = CENTER.relative(Direction.EAST, point.x).relative(Direction.NORTH, point.y);
            useOn(helper, player, pos, new ItemStack(EndResourceItems.ETERNAL_CRYSTAL));
        }

        final List<String> failures = new ArrayList<>();

        // Any of the 6 pedestals independently discovers the same shared ritual once it has configured
        // itself, so check them all rather than assuming a specific one (pedestalOffsets() is an
        // unordered Set, so "the last one placed" is not a fixed position).
        EternalPedestalEntity pedestal = null;
        for (var point : EternalRitual.pedestalOffsets()) {
            final BlockPos pos = CENTER.relative(Direction.EAST, point.x).relative(Direction.NORTH, point.y);
            final BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(pos));
            if (be instanceof EternalPedestalEntity ep && ep.hasRitual()) {
                pedestal = ep;
                break;
            }
        }
        if (pedestal == null) {
            throw helper.assertionException(Component.literal(
                    "none of the 6 pedestals ever got a linked EternalRitual - pedestal ring geometry is"
                            + " probably wrong, check EAST/NORTH offsets against pedestalOffsets()"
            ));
        }

        if (!pedestal.getRitual().isActive()) {
            failures.add("EternalRitual never became active after filling all 6 pedestals with"
                    + " eternal_crystal - checkStructure's frame or pedestal-item check failed");
        }

        // The frame blocks themselves flip to ACTIVE=true on real activation (PortalBuilder/
        // EternalRitual#activatePortal both set it), independent of whether a far-side portal was found.
        final BlockState frameState = helper.getLevel().getBlockState(
                helper.absolutePos(framePos.relative(Direction.NORTH, 0))
        );
        if (frameState.hasProperty(EternalRitual.ACTIVE) && !frameState.getValue(EternalRitual.ACTIVE)) {
            failures.add("the frame block did not flip to ACTIVE=true on activation");
        }

        requireDone(helper, player, "portal_on", failures);

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Eternal portal activation regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    /**
     * {@code PORTAL_TRAVEL} fires from {@code EndPortalBlock} when an entity actually rides a portal
     * across dimensions, not from ritual activation - simulating that would mean a player entity
     * genuinely completing a dimension change, a different (and separately risky) thing to drive in a
     * GameTest from generating the portal itself. Covered directly instead, the same way
     * {@code EndPortalBlock.java:158}'s own call is made.
     */
    @GameTest
    public void portalTravelCanBeAwarded(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.inLevel(helper, CENTER.south(6));
        BECriteria.PORTAL_TRAVEL.trigger(player);

        final List<String> failures = new ArrayList<>();
        requireDone(helper, player, "portal_travel", failures);
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Eternal portal advancement regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static void useOn(GameTestHelper helper, ServerPlayer player, BlockPos relativePos, ItemStack stack) {
        final BlockPos abs = helper.absolutePos(relativePos);
        final BlockState state = helper.getLevel().getBlockState(abs);
        final BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
        state.useItemOn(stack, helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    }

    private static void requireDone(GameTestHelper helper, ServerPlayer player, String path, List<String> failures) {
        final AdvancementHolder holder = helper.getLevel()
                                                .getServer()
                                                .getAdvancements()
                                                .get(ResourceLocation.parse("betterend:" + path));
        if (holder == null) {
            failures.add("betterend:" + path + " is not loaded at all");
            return;
        }
        final AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            final List<String> remaining = new ArrayList<>();
            progress.getRemainingCriteria().forEach(remaining::add);
            failures.add("betterend:" + path + " was not awarded - still missing " + remaining);
        }
    }
}
