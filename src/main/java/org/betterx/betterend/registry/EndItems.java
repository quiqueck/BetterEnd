package org.betterx.betterend.registry;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.items.BaseDiscItem;
import org.betterx.bclib.trait.item.CompostableItemTrait;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.*;
import org.betterx.betterend.item.material.AeterniumSet;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.registry.item.EndFoodItems;
import org.betterx.betterend.registry.item.EndDiscItems;
import org.betterx.betterend.registry.item.EndEquipmentItems;
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

/**
 * Registry facade for BetterEnd's items. The actual field declarations live in the per-category
 * classes under {@code org.betterx.betterend.registry.item} (see {@link #ensureStaticallyLoaded()}
 * for the boot order); this class only keeps the shared registration forwarders, the
 * {@link ExternalItemModel} client helper, and the driver that loads every category class in the
 * correct order.
 */
public class EndItems {
    private static ItemRegistry ITEMS_REGISTRY;

    public static List<Item> getModItems() {
        return getItemRegistry().allItems().toList();
    }

    public static Item registerEndDisc(String name, ResourceKey<JukeboxSong> sound) {
        return getItemRegistry()
                .defineDefaultItem(name, def -> BaseDiscItem.create(sound, def.getProperties()))
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .addTags(CommonItemTags.MUSIC_DISCS)
                .buildAndRegister();
    }

    public static DefaultItemDefinition<Item> defineEndItem(String name) {
        return getItemRegistry()
                .defineDefaultItem(name, def -> new Item(def.getProperties()));
    }

    public static Item registerEndItem(String name) {
        return defineEndItem(name)
                .buildAndRegister();
    }

    public static Item registerEndEgg(String name, EntityType<? extends Mob> type, int background, int dots) {
        return getItemRegistry().defineSpawnEgg(name).entityType(type).colors(background, dots).buildAndRegister();
    }

    // Compost chance mirrors vanilla's own food-to-compost heuristic (nutrition * saturationModifier * 0.18,
    // see net.minecraft.world.level.block.ComposterBlock's vanilla table derivation) computed straight off
    // the values passed in here - see CompostableItemTrait. This used to be (re-)computed at datagen time in
    // ItemTagProvider by looping every item's built FoodProperties component and calling
    // ComposterAPI.allowCompost, which only ever ran in the datagen environment and never affected the
    // shipped game; attaching the trait here makes the same chance apply at real runtime.
    public static Item registerEndFood(String name, int hunger, float saturation, MobEffectInstance... effects) {
        return getItemRegistry()
                .defineFoodItem(name, def -> new Item(def.getProperties()))
                .nutrition(hunger)
                .saturationModifier(saturation)
                .setEffects(effects)
                .addTrait(CompostableItemTrait.withChance(hunger * saturation * 0.18F))
                .buildAndRegister();
    }

    public static Item registerEndFood(String name, FoodProperties foodComponent) {
        return getItemRegistry()
                .defineFoodItem(name, def -> new Item(def.getProperties()))
                .food(foodComponent)
                .addTrait(CompostableItemTrait.withChance(
                        foodComponent.nutrition() * foodComponent.saturation() * 0.18F
                ))
                .buildAndRegister();

    }

    public static Item registerEndDrink(String name, int hunger, float saturation) {
        return getItemRegistry()
                .defineDrinkItem(name, def -> new Item(def.getProperties()))
                .nutrition(hunger)
                .saturationModifier(saturation)
                .addTrait(CompostableItemTrait.withChance(hunger * saturation * 0.18F))
                .buildAndRegister();
    }

    public static Item.Properties makeEndItemSettings() {
        return new Item.Properties();
    }

    @NotNull
    public static ItemRegistry getItemRegistry() {
        if (ITEMS_REGISTRY == null) {
            ITEMS_REGISTRY = ItemRegistry.forMod(BetterEnd.C);
        }
        return ITEMS_REGISTRY;
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        EndResourceItems.ensureLoaded();
        EndFoodItems.ensureLoaded();
        EndDiscItems.ensureLoaded();
        EndEquipmentItems.ensureLoaded();   // after Resource (registry-split-map.md Resolution 2)
        GuideBookItem.ensureStaticallyLoaded();
        if (BCLib.isDevEnvironment() && !ModCore.isDatagen()) {
            DebugHelpers.generateDebugItems();
        }
    }

    public static Item.Properties defaultSettings() {
        return new Item.Properties();
    }

    /**
     * Kept in a separate class file (not just an @Environment(CLIENT) method) since EndItems is
     * always loaded on the server; a lambda body's synthetic method does not inherit the
     * annotation from its enclosing method, so leaving it here would strand vanilla client-only
     * type references in a class file the server actually has to verify.
     *
     * <p>Public (rather than the original package-private-by-file private) so the per-category
     * item registry classes in {@code org.betterx.betterend.registry.item}, which now hold the
     * fields that reference it, can still call it.
     */
    @Environment(EnvType.CLIENT)
    public static class ExternalItemModel {
        public static ItemModelTrait build() {
            return ClientItemTraits.MODEL.with((key, item, generator) -> generator.itemModelOutput.accept(
                    item,
                    ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item))
            ));
        }
    }
}
