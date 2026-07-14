package org.betterx.betterend.registry;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.items.BaseDiscItem;
import org.betterx.bclib.items.ModelProviderItem;
import org.betterx.bclib.models.RecordItemModelProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.*;
import org.betterx.betterend.item.material.AeterniumSet;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.trait.item.EndArmorItemTraitBuilder;
import org.betterx.betterend.trait.item.HammerTraitBuilder;
import org.betterx.betterend.util.DebugHelpers;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.complex.api.equipment.ToolTiers;
import org.betterx.wover.item.api.DefaultItemDefinition;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.client.trait.ClientItemTraits;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;
import org.betterx.wover.item.api.trait.ItemTraits;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

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

public class EndItems {
    private static ItemRegistry ITEMS_REGISTRY;

    // Materials //
    public final static Item ENDER_DUST = registerEndItem("ender_dust");
    public final static Item ENDER_SHARD = registerEndItem("ender_shard");

    // Model is hand-authored (uses the "_small" leaf texture, not the plain item-name convention the
    // generic flat-item fallback assumes) - point the item definition at the existing hand-authored
    // model instead of letting the fallback regenerate (and overwrite) it.
    public final static Item END_LILY_LEAF = defineEndItem("end_lily_leaf")
            .addTrait(ModCore.isDatagen() ? ExternalItemModel.build() : null)
            .buildAndRegister();
    public final static Item END_LILY_LEAF_DRIED = registerEndItem("end_lily_leaf_dried");
    public final static Item CRYSTAL_SHARDS = registerEndItem("crystal_shards");
    public final static Item RAW_AMBER = registerEndItem("raw_amber");
    public final static Item AMBER_GEM = registerEndItem("amber_gem");
    public final static Item GLOWING_BULB = registerEndItem("glowing_bulb");
    public final static Item CRYSTALLINE_SULPHUR = registerEndItem("crystalline_sulphur");
    public final static Item HYDRALUX_PETAL = registerEndItem("hydralux_petal");
    public final static Item GELATINE = registerEndItem("gelatine");
    public static final Item ETERNAL_CRYSTAL = defineEndItem("eternal_crystal")
            .stacksTo(16)
            .rarity(Rarity.EPIC)
            .buildAndRegister();

    public final static Item ENCHANTED_PETAL = defineEndItem("enchanted_petal")
            .stacksTo(16)
            .rarity(Rarity.RARE)
            .addTrait(ModelTraitLibrary.itemModel(() -> HYDRALUX_PETAL))
            .buildAndRegister();

    public final static Item LEATHER_STRIPE = registerEndItem("leather_stripe");
    public final static Item LEATHER_WRAPPED_STICK = registerEndItem("leather_wrapped_stick");
    public final static Item SILK_FIBER = registerEndItem("silk_fiber");
    public final static Item LUMECORN_ROD = registerEndItem("lumecorn_rod");
    public final static Item SILK_MOTH_MATRIX = registerEndItem("silk_moth_matrix");
    public final static Item ENCHANTED_MEMBRANE = defineEndItem("enchanted_membrane")
            .stacksTo(16)
            .rarity(Rarity.RARE)
            .addTrait(ModelTraitLibrary.itemModel(() -> Items.PHANTOM_MEMBRANE))
            .buildAndRegister();

    // Music Discs
    public final static Item MUSIC_DISC_STRANGE_AND_ALIEN = registerEndDisc(
            "music_disc_strange_and_alien",
            EndSounds.RECORD_STRANGE_AND_ALIEN
    );
    public final static Item MUSIC_DISC_GRASPING_AT_STARS = registerEndDisc(
            "music_disc_grasping_at_stars",
            EndSounds.RECORD_GRASPING_AT_STARS
    );
    public final static Item MUSIC_DISC_ENDSEEKER = registerEndDisc(
            "music_disc_endseeker",
            EndSounds.RECORD_ENDSEEKER
    );
    public final static Item MUSIC_DISC_EO_DRACONA = registerEndDisc(
            "music_disc_eo_dracona",
            EndSounds.RECORD_EO_DRACONA
    );

    // Equipment Sets //
    public static final AeterniumSet AETERNIUM_SET = new AeterniumSet();

    // Armor //
    public static final CrystaliteHelmet CRYSTALITE_HELMET = CrystaliteHelmet.definition(
            ITEMS_REGISTRY, "crystalite_helmet"
    ).buildAndRegister();
    public static final CrystaliteChestplate CRYSTALITE_CHESTPLATE = CrystaliteChestplate.definition(
            ITEMS_REGISTRY, "crystalite_chestplate"
    ).buildAndRegister();
    public static final Item CRYSTALITE_LEGGINGS = CrystaliteLeggings.definition(
            ITEMS_REGISTRY, "crystalite_leggings"
    ).buildAndRegister();
    public static final Item CRYSTALITE_BOOTS = CrystaliteBoots.definition(
            ITEMS_REGISTRY, "crystalite_boots"
    ).buildAndRegister();

    public static final Item ARMORED_ELYTRA = ITEMS_REGISTRY
            .defineArmorItem(
                    "elytra_armored",
                    (def) -> new ArmoredElytra(0.97, def)
            )
            .addTrait(ItemTraits.ELYTRA_ITEM)
            .addTrait(EndArmorItemTraitBuilder.BUILDER.with(
                    ArmorSlot.CHESTPLATE_SLOT,
                    EndArmorTier.AETERNIUM,
                    1.15f, 1.15f, 0.5f,
                    true
            ))
            .durability(900)
            .rarity(Rarity.EPIC)
            .buildAndRegister();


    public static final Item CRYSTALITE_ELYTRA = ITEMS_REGISTRY
            .defineArmorItem(
                    "elytra_crystalite",
                    (def) -> new ArmoredElytra(1.0, def)
            )
            .addTrait(ItemTraits.ELYTRA_ITEM.with(ENCHANTED_MEMBRANE))
            .addTrait(EndArmorItemTraitBuilder.BUILDER.with(
                    ArmorSlot.CHESTPLATE_SLOT,
                    EndArmorTier.CRYSTALITE,
                    1.2f, 1.25f, 0.5f,
                    false
            ))
            .durability(650)
            .rarity(Rarity.EPIC)
            .buildAndRegister();


    // ITEM_HAMMERS //
    public static final Item IRON_HAMMER = ITEMS_REGISTRY
            .defineToolItem("iron_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.IRON_TOOL, 0.2f))
            .addTags(CommonItemTags.HAMMERS)
            .buildAndRegister();

    public static final Item GOLDEN_HAMMER = ITEMS_REGISTRY
            .defineToolItem("golden_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.GOLD_TOOL, 0.3f))
            .addTags(CommonItemTags.HAMMERS)
            .buildAndRegister();


    public static final Item DIAMOND_HAMMER = ITEMS_REGISTRY
            .defineToolItem("diamond_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.DIAMOND_TOOL, 0.2f))
            .addTags(CommonItemTags.HAMMERS)
            .buildAndRegister();

    public static final Item NETHERITE_HAMMER = ITEMS_REGISTRY
            .defineToolItem("netherite_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.NETHERITE_TOOL, 0.2f))
            .addTags(CommonItemTags.HAMMERS)
            .addTrait(ItemTraits.IS_FIREPROOF)
            .buildAndRegister();


    // Food //
    // Models are hand-authored (reusing the "shadow_berry"/"end_fish" base texture, not the "_raw"
    // item-name convention the generic flat-item fallback assumes) - point the item definition at the
    // existing hand-authored model instead of letting the fallback regenerate (and overwrite) it.
    public final static Item SHADOW_BERRY_RAW = getItemRegistry()
            .defineFoodItem("shadow_berry_raw", def -> new ModelProviderItem(def.getProperties()))
            .nutrition(4)
            .saturationModifier(0.5F)
            .addTrait(ModCore.isDatagen() ? ExternalItemModel.build() : null)
            .buildAndRegister();
    public final static Item SHADOW_BERRY_COOKED = registerEndFood("shadow_berry_cooked", 6, 0.7F);
    public final static Item END_FISH_RAW = getItemRegistry()
            .defineFoodItem("end_fish_raw", def -> new ModelProviderItem(def.getProperties()))
            .food(Foods.SALMON)
            .addTrait(ModCore.isDatagen() ? ExternalItemModel.build() : null)
            .buildAndRegister();
    public final static Item END_FISH_COOKED = registerEndFood("end_fish_cooked", Foods.COOKED_SALMON);
    public final static Item BUCKET_END_FISH = ITEMS_REGISTRY
            .defineToolItem(
                    "bucket_end_fish",
                    def -> new EndBucketItem(EndEntities.END_FISH.type(), def.getProperties())
            ).stacksTo(1)
            .buildAndRegister();

    public final static Item BUCKET_CUBOZOA = ITEMS_REGISTRY
            .defineToolItem(
                    "bucket_cubozoa",
                    def -> new EndBucketItem(EndEntities.CUBOZOA.type(), def.getProperties())
            ).stacksTo(1)
            .buildAndRegister();

    public final static Item SWEET_BERRY_JELLY = registerEndFood("sweet_berry_jelly", 8, 0.7F);
    public final static Item SHADOW_BERRY_JELLY = registerEndFood(
            "shadow_berry_jelly",
            6,
            0.8F,
            new MobEffectInstance(MobEffects.NIGHT_VISION, 400)
    );
    public final static Item BLOSSOM_BERRY_JELLY = registerEndFood("blossom_berry_jelly", 8, 0.7F);
    public final static Item BLOSSOM_BERRY = registerEndFood("blossom_berry", Foods.APPLE);
    public final static Item AMBER_ROOT_RAW = registerEndFood("amber_root_raw", 2, 0.8F);
    public final static Item CHORUS_MUSHROOM_RAW = registerEndFood("chorus_mushroom_raw", 3, 0.5F);
    public final static Item CHORUS_MUSHROOM_COOKED = registerEndFood("chorus_mushroom_cooked", Foods.MUSHROOM_STEW);
    public final static Item BOLUX_MUSHROOM_COOKED = registerEndFood("bolux_mushroom_cooked", Foods.MUSHROOM_STEW);
    public final static Item CAVE_PUMPKIN_PIE = registerEndFood("cave_pumpkin_pie", Foods.PUMPKIN_PIE);

    // Drinks //
    public final static Item UMBRELLA_CLUSTER_JUICE = registerEndDrink("umbrella_cluster_juice", 5, 0.7F);

    public static List<Item> getModItems() {
        return getItemRegistry().allItems().toList();
    }

    public static Item registerEndDisc(String name, ResourceKey<JukeboxSong> sound) {
        Item item = BaseDiscItem.create(
                sound,
                BehaviourBuilders.createDisc().setId(getItemRegistry().key(name))
        );
        RecordItemModelProvider.add(item);
        getItemRegistry().register(name, item, CommonItemTags.MUSIC_DISCS);
        return item;
    }

    public static DefaultItemDefinition<Item> defineEndItem(String name) {
        return getItemRegistry()
                .defineDefaultItem(name, def -> new ModelProviderItem(def.getProperties()));
    }

    public static Item registerEndItem(String name) {
        return defineEndItem(name)
                .buildAndRegister();
    }

    public static Item registerEndEgg(String name, EntityType<? extends Mob> type, int background, int dots) {
        return getItemRegistry().defineSpawnEgg(name).entityType(type).colors(background, dots).buildAndRegister();
    }

    public static Item registerEndFood(String name, int hunger, float saturation, MobEffectInstance... effects) {
        return getItemRegistry()
                .defineFoodItem(name, def -> new ModelProviderItem(def.getProperties()))
                .nutrition(hunger)
                .saturationModifier(saturation)
                .setEffects(effects)
                .buildAndRegister();
    }

    public static Item registerEndFood(String name, FoodProperties foodComponent) {
        return getItemRegistry()
                .defineFoodItem(name, def -> new ModelProviderItem(def.getProperties()))
                .food(foodComponent)
                .buildAndRegister();

    }

    public static Item registerEndDrink(String name, int hunger, float saturation) {
        return getItemRegistry()
                .defineDrinkItem(name, def -> new ModelProviderItem(def.getProperties()))
                .nutrition(hunger)
                .saturationModifier(saturation)
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
     */
    @Environment(EnvType.CLIENT)
    private static class ExternalItemModel {
        private static ItemModelTrait build() {
            return ClientItemTraits.MODEL.with((key, item, generator) -> generator.itemModelOutput.accept(
                    item,
                    ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item))
            ));
        }
    }
}
