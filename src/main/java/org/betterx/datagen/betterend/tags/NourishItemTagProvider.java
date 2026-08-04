package org.betterx.datagen.betterend.tags;


import org.betterx.betterend.registry.item.EndFoodItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.TagManager;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class NourishItemTagProvider extends WoverTagProvider.ForItems {
    public NourishItemTagProvider(ModCore modCore) {
        super(BetterEnd.NOURISH);
    }

    @Override
    public void prepareTags(ItemTagBootstrapContext context) {
        TagKey<Item> fats = TagManager.ITEMS.makeTag(BetterEnd.NOURISH, "fats");
        TagKey<Item> fruit = TagManager.ITEMS.makeTag(BetterEnd.NOURISH, "fruit");
        TagKey<Item> protein = TagManager.ITEMS.makeTag(BetterEnd.NOURISH, "protein");
        TagKey<Item> sweets = TagManager.ITEMS.makeTag(BetterEnd.NOURISH, "sweets");


        context.add(fats, EndFoodItems.END_FISH_RAW, EndFoodItems.END_FISH_COOKED);
        context.add(
                fruit,
                EndFoodItems.SHADOW_BERRY_RAW,
                EndFoodItems.SHADOW_BERRY_COOKED,
                EndFoodItems.BLOSSOM_BERRY,
                EndFoodItems.SHADOW_BERRY_JELLY,
                EndFoodItems.SWEET_BERRY_JELLY,
                EndFoodItems.BLOSSOM_BERRY_JELLY,
                EndFoodItems.AMBER_ROOT_RAW,
                EndFoodItems.CHORUS_MUSHROOM_RAW,
                EndFoodItems.CHORUS_MUSHROOM_COOKED,
                EndFoodItems.BOLUX_MUSHROOM_COOKED
        );
        context.add(
                protein,
                EndFoodItems.END_FISH_RAW,
                EndFoodItems.END_FISH_COOKED,
                EndFoodItems.CHORUS_MUSHROOM_COOKED,
                EndFoodItems.BOLUX_MUSHROOM_COOKED,
                EndFoodItems.CAVE_PUMPKIN_PIE
        );
        context.add(
                sweets,
                EndFoodItems.SHADOW_BERRY_JELLY,
                EndFoodItems.SWEET_BERRY_JELLY,
                EndFoodItems.BLOSSOM_BERRY_JELLY,
                EndFoodItems.CAVE_PUMPKIN_PIE
        );
    }
}
