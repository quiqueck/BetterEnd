package org.betterx.betterend.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.material.Fluids;

public class EndBucketItem extends MobBucketItem {
    public EndBucketItem(EntityType<? extends Mob> type, Item.Properties properties) {
        super(type, Fluids.WATER, SoundEvents.BUCKET_EMPTY, properties);
    }
}
