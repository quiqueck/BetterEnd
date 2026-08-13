package org.betterx.betterend.testmod.gametest;

import org.betterx.bclib.trait.Compostables;
import org.betterx.betterend.registry.block.EndVineBlocks;

import de.ambertation.wover.test.api.gametest.MockPlayers;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers composting of BetterEnd's compostables, mirroring BetterNether's suite of the same name.
 * <p>
 * Compostability has two halves that are easy to get out of step, because one is datagen and the other is
 * runtime. {@code CompostableBlockTrait.configure()} emits the {@code c:compostable} item tag, while an
 * actual composter reads the trait off the block at use time through {@code chanceFor(item)}. An item can
 * therefore sit in the tag - looking entirely compostable to anything that inspects data - while a
 * composter refuses it, which is precisely what the retired {@code BehaviourCompostable} marker used to do
 * to every block in both mods. Nothing short of putting the item in a composter tells the two apart, so
 * that is what these do.
 * <p>
 * {@link #everyItemInTheCompostableTagActuallyComposts} is the check that would catch a recurrence across
 * the whole mod: it starts from the <em>tag</em> rather than from the trait, so a block that has drifted
 * into the tag-only state is a failure rather than an item the sweep never looks at.
 */
public class ComposterGameTest {
    private static final BlockPos COMPOSTER = new BlockPos(1, 1, 1);
    private static final BlockPos PLAYER = new BlockPos(2, 1, 1);
    private static final int STACK = 16;

    /**
     * A survival player composts a blue vine seed by hand.
     * <p>
     * Named explicitly rather than left to the sweep because it is the case that was reported: the seed is
     * in {@code c:compostable} and its registration asks for {@code PlantBlockTrait.compostableWithColor},
     * so anything short of a real composter says it works.
     */
    @GameTest
    public void aBlueVineSeedComposts(GameTestHelper helper) {
        helper.setBlock(COMPOSTER, Blocks.COMPOSTER);
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(EndVineBlocks.BLUE_VINE_SEED, STACK));

        helper.useBlock(COMPOSTER, player);

        final List<String> failures = new ArrayList<>();
        final int level = helper.getBlockState(COMPOSTER).getValue(ComposterBlock.LEVEL);
        if (level != 1) {
            // An empty composter always accepts the first item with a positive chance, so this is
            // deterministic - see the level-0 rule below.
            failures.add("clicking a blue vine seed into an empty composter left it at level " + level
                    + ", expected 1 - the composter refuses it");
        }
        final int left = player.getItemInHand(InteractionHand.MAIN_HAND).getCount();
        if (level == 1 && left != STACK - 1) {
            failures.add("the composter filled but " + left + " seeds are left of the " + STACK
                    + " held, expected one to be spent");
        }

        failIfAny(helper, "Blue vine seed composting regression", failures);
        helper.succeed();
    }

    /**
     * Every item in {@code c:compostable} is actually accepted by a composter.
     * <p>
     * Driven from the tag on purpose. Sweeping the trait instead would only ever confirm that items
     * carrying the trait work - it could never see an item that has the tag and lost the trait, which is
     * the exact shape this bug takes.
     * <p>
     * The composter is reset to empty between items, which is what makes each assertion deterministic:
     * {@code ComposterBlock.addItem} raises the level on a chance roll except from level 0, where any item
     * with a positive chance is guaranteed to take it to 1.
     */
    @GameTest
    public void everyItemInTheCompostableTagActuallyComposts(GameTestHelper helper) {
        final ServerPlayer player = MockPlayers.survival(helper, PLAYER);
        final List<Item> tagged = taggedCompostable();
        final List<String> failures = new ArrayList<>();

        for (Item item : tagged) {
            helper.setBlock(COMPOSTER, Blocks.COMPOSTER);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item, 2));

            helper.useBlock(COMPOSTER, player);

            final int level = helper.getBlockState(COMPOSTER).getValue(ComposterBlock.LEVEL);
            if (level != 1) {
                failures.add(BuiltInRegistries.ITEM.getKey(item) + " is in " + CommonItemTags.COMPOSTABLE.location()
                        + " but an empty composter refused it (still level " + level + ")"
                        + (Compostables.isCompostable(item)
                                ? " - it carries a compostable trait, so the composter path itself is broken"
                                : " - it carries no compostable trait, so only the tag was ever emitted"));
            }
        }

        failIfAny(helper, "Composting regression", failures);
        helper.succeed();
    }

    /**
     * The hopper route for the same set, asked of the composter's own input container directly.
     * <p>
     * A hopper does not go through {@code ComposterBlock.useItemOn} at all - it inserts through the
     * container handed out by {@code getContainer}, whose {@code canPlaceItemThroughFace} does its own
     * compostability lookup. Only one of the two was patched at one point in BetterNether, and the symptom
     * was an item that composted by hand and sat in the hopper forever. A hopper per item would take
     * thousands of ticks, and the container is exactly what a hopper consults, so this covers the family
     * for the cost of one composter.
     */
    @GameTest
    public void everyItemInTheCompostableTagIsAcceptedThroughTheComposterFace(GameTestHelper helper) {
        helper.setBlock(COMPOSTER, Blocks.COMPOSTER);

        final BlockPos abs = helper.absolutePos(COMPOSTER);
        final BlockState state = helper.getLevel().getBlockState(abs);
        final WorldlyContainer container = ((WorldlyContainerHolder) state.getBlock())
                .getContainer(state, helper.getLevel(), abs);

        final List<String> failures = new ArrayList<>();
        for (Item item : taggedCompostable()) {
            if (!container.canPlaceItemThroughFace(0, new ItemStack(item), Direction.UP)) {
                failures.add(BuiltInRegistries.ITEM.getKey(item) + " is in "
                        + CommonItemTags.COMPOSTABLE.location() + " but the composter rejects it through the"
                        + " top face - a hopper can never insert it");
            }
        }

        failIfAny(helper, "Automated composting regression", failures);
        helper.succeed();
    }

    /**
     * Every registered item in {@code c:compostable}. Throws rather than returning an empty list: a sweep
     * over nothing passes silently, which would make both sweeps above prove nothing at all.
     */
    private static List<Item> taggedCompostable() {
        final List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.builtInRegistryHolder().is(CommonItemTags.COMPOSTABLE)) items.add(item);
        }
        if (items.isEmpty()) {
            throw new AssertionError("no registered item is in " + CommonItemTags.COMPOSTABLE.location()
                    + " - nothing to sweep");
        }
        return items;
    }

    private static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
