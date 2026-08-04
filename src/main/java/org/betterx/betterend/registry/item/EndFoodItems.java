package org.betterx.betterend.registry.item;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.items.BaseDiscItem;
import org.betterx.bclib.trait.item.CompostableItemTrait;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.*;
import org.betterx.betterend.item.material.AeterniumSet;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.trait.item.EndArmorItemTraitBuilder;
import org.betterx.betterend.trait.item.HammerTraitBuilder;
import org.betterx.betterend.util.DebugHelpers;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.item.api.DefaultItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.client.trait.ClientItemTraits;
import de.ambertation.wover.item.api.client.trait.ItemModelTrait;
import de.ambertation.wover.item.api.trait.ItemTraits;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.EndSounds;

public class EndFoodItems {
    // Models are hand-authored (reusing the "shadow_berry"/"end_fish" base texture, not the "_raw"
    // item-name convention the generic flat-item fallback assumes) - point the item definition at the
    // existing hand-authored model instead of letting the fallback regenerate (and overwrite) it.
    public final static Item SHADOW_BERRY_RAW = EndItems.getItemRegistry()
            .defineFoodItem("shadow_berry_raw", def -> new Item(def.getProperties()))
            .nutrition(4)
            .saturationModifier(0.5F)
            .addTrait(CompostableItemTrait.withChance(4 * 0.5F * 0.18F))
            .addTrait(ModCore.isDatagen() ? EndItems.ExternalItemModel.build() : null)
            .buildAndRegister();

    public final static Item SHADOW_BERRY_COOKED = EndItems.registerEndFood("shadow_berry_cooked", 6, 0.7F);

    public final static Item END_FISH_RAW = EndItems.getItemRegistry()
            .defineFoodItem("end_fish_raw", def -> new Item(def.getProperties()))
            .food(Foods.SALMON)
            .addTrait(CompostableItemTrait.withChance(Foods.SALMON.nutrition() * Foods.SALMON.saturation() * 0.18F))
            .addTrait(ModCore.isDatagen() ? EndItems.ExternalItemModel.build() : null)
            .buildAndRegister();

    public final static Item END_FISH_COOKED = EndItems.registerEndFood("end_fish_cooked", Foods.COOKED_SALMON);

    public final static Item BUCKET_END_FISH = EndItems.getItemRegistry()
            .defineToolItem(
                    "bucket_end_fish",
                    def -> new EndBucketItem(EndEntities.END_FISH.type(), def.getProperties())
            ).stacksTo(1)
            .buildAndRegister();

    public final static Item BUCKET_CUBOZOA = EndItems.getItemRegistry()
            .defineToolItem(
                    "bucket_cubozoa",
                    def -> new EndBucketItem(EndEntities.CUBOZOA.type(), def.getProperties())
            ).stacksTo(1)
            .buildAndRegister();

    public final static Item SWEET_BERRY_JELLY = EndItems.registerEndFood("sweet_berry_jelly", 8, 0.7F);

    public final static Item SHADOW_BERRY_JELLY = EndItems.registerEndFood(
            "shadow_berry_jelly",
            6,
            0.8F,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 400)
    );

    public final static Item BLOSSOM_BERRY_JELLY = EndItems.registerEndFood("blossom_berry_jelly", 8, 0.7F);

    public final static Item BLOSSOM_BERRY = EndItems.registerEndFood("blossom_berry", Foods.APPLE);

    public final static Item AMBER_ROOT_RAW = EndItems.registerEndFood("amber_root_raw", 2, 0.8F);

    public final static Item CHORUS_MUSHROOM_RAW = EndItems.registerEndFood("chorus_mushroom_raw", 3, 0.5F);

    public final static Item CHORUS_MUSHROOM_COOKED = EndItems.registerEndFood("chorus_mushroom_cooked", Foods.MUSHROOM_STEW);

    public final static Item BOLUX_MUSHROOM_COOKED = EndItems.registerEndFood("bolux_mushroom_cooked", Foods.MUSHROOM_STEW);

    public final static Item CAVE_PUMPKIN_PIE = EndItems.registerEndFood("cave_pumpkin_pie", Foods.PUMPKIN_PIE);

    public final static Item UMBRELLA_CLUSTER_JUICE = EndItems.registerEndDrink("umbrella_cluster_juice", 5, 0.7F);

    public static void ensureLoaded() {}
}
