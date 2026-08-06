package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.entity.EndFishEntity;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.item.EndFoodItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers {@code betterend:bucket_end_fish}'s capture round-trip.
 * <p>
 * Vanilla's real capture flow ({@code Bucketable.bucketMobPickup}) calls {@code getBucketItemStack()}
 * to get the base stack, then calls {@code saveToBucketTag(stack)} on it separately - both are exercised
 * here in that order, not just one or the other, since {@code EndFishEntity#getBucketItemStack()}'s own
 * body no longer writes variant/scale itself (that write is commented out - see the source) and relies
 * entirely on the later {@code saveToBucketTag} call to do it.
 * <p>
 * <b>Flagging a likely release-side bug this class does not mechanically reproduce</b>:
 * {@code EndFishEntity} overrides {@code saveToBucketTag} to write {@code variant}/{@code scale} into
 * {@code DataComponents.BUCKET_ENTITY_DATA}, but never overrides the counterpart
 * {@code Bucketable#loadFromBucketTag} to read them back when a fish is released. Vanilla's own
 * {@code TropicalFish} needs exactly this same pair of overrides for the identical reason (its variant
 * is not part of the default {@code Bucketable} contract), which is strong precedent that the omission
 * here is a real bug, not a deliberate simplification. Reproducing the release side mechanically would
 * need a real bucket-empty interaction (a player raycast + {@code MobBucketItem#use}), which is fragile
 * to set up reliably in a GameTest; this class instead documents the finding precisely enough to verify
 * by inspection, and fully covers the capture side, which is where the actual data currently survives.
 */
public class BucketEndFishGameTest {
    private static final BlockPos FISH_POS = new BlockPos(1, 2, 1);

    @GameTest
    public void captureRoundTripPreservesVariant(GameTestHelper helper) {
        final EndFishEntity fish = helper.spawn(EndEntities.END_FISH.type(), FISH_POS);
        final int variant = fish.getVariant();

        // Mirrors Bucketable.bucketMobPickup's real order: get the base stack, then let the entity
        // stamp its own data onto it.
        final ItemStack bucket = fish.getBucketItemStack();
        fish.saveToBucketTag(bucket);

        final List<String> failures = new ArrayList<>();
        if (!bucket.is(EndFoodItems.BUCKET_END_FISH)) {
            failures.add("getBucketItemStack() returned " + bucket + " instead of betterend:bucket_end_fish");
        }

        final CustomData data = bucket.get(DataComponents.BUCKET_ENTITY_DATA);
        if (data == null) {
            failures.add("no BUCKET_ENTITY_DATA component was written at all");
        } else {
            final var tag = data.copyTag();
            if (!tag.contains("variant") || tag.getIntOr("variant", -1) != variant) {
                failures.add("expected variant " + variant + " in the bucket's data, found "
                        + (tag.contains("variant") ? tag.getIntOr("variant", -1) : "nothing"));
            }
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "bucket_end_fish capture regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
