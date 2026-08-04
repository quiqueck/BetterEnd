package org.betterx.betterend.util;


import org.betterx.betterend.registry.item.EndDiscItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndEnchantments;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import de.ambertation.wover.loot.api.LootTableManager;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class LootTableUtil {
    public static final ResourceKey<LootTable> VILLAGE_LOOT = LootTableManager.createLootTableKey(BetterEnd.C, "chests/end_village_loot");
    public static final ResourceKey<LootTable> VILLAGE_TEMPLATE_LOOT = LootTableManager.createLootTableKey(BetterEnd.C, "chests/end_village_template_loot");
    public static final ResourceKey<LootTable> VILLAGE_BONUS_LOOT = LootTableManager.createLootTableKey(BetterEnd.C, "chests/end_village_bonus_loot");
    public static final ResourceKey<LootTable> COMMON = LootTableManager.createLootTableKey(BetterEnd.C, "chests/common");
    public static final ResourceKey<LootTable> FOGGY_MUSHROOMLAND = LootTableManager.createLootTableKey(BetterEnd.C, "chests/foggy_mushroomland");
    public static final ResourceKey<LootTable> CHORUS_FOREST = LootTableManager.createLootTableKey(BetterEnd.C, "chests/chorus_forest");
    public static final ResourceKey<LootTable> SHADOW_FOREST = LootTableManager.createLootTableKey(BetterEnd.C, "chests/shadow_forest");
    public static final ResourceKey<LootTable> LANTERN_WOODS = LootTableManager.createLootTableKey(BetterEnd.C, "chests/lantern_woods");
    public static final ResourceKey<LootTable> UMBRELLA_JUNGLE = LootTableManager.createLootTableKey(BetterEnd.C, "chests/umbrella_jungle");
    public static final ResourceKey<LootTable> BIOME_CHEST = LootTableManager.createLootTableKey(BetterEnd.C, "chests/biome");
    public static final ResourceKey<LootTable> FISHING_FISH = LootTableManager.createLootTableKey(BetterEnd.C, "gameplay/fishing/fish");
    public static final ResourceKey<LootTable> FISHING_TREASURE = LootTableManager.createLootTableKey(BetterEnd.C, "gameplay/fishing/treasure");
    public static final ResourceKey<LootTable> FISHING_JUNK = LootTableManager.createLootTableKey(BetterEnd.C, "gameplay/fishing/junk");


    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Compare keys to keys. BuiltInLootTables exposes ResourceKey<LootTable>, so testing it
            // against the ResourceLocation from key.location() is an Object#equals between two
            // different types: it compiles, never matches, and silently disables every modification
            // below.
            final LootItemCondition.Builder IN_END = LocationCheck.checkLocation(LocationPredicate.Builder
                    .location()
                    .setDimension(Level.END));

            if (BuiltInLootTables.END_CITY_TREASURE.equals(key)) {
                LootPool.Builder builder = LootPool.lootPool();
                builder.setRolls(ConstantValue.exactly(1));
                builder.when(LootItemRandomChanceCondition.randomChance(0.2f));
                builder.add(LootItem.lootTableItem(Items.GHAST_TEAR));
                tableBuilder.withPool(builder);

                builder = LootPool.lootPool();
                builder.setRolls(UniformGenerator.between(0, 3));
                builder.add(LootItem.lootTableItem(EndDiscItems.MUSIC_DISC_STRANGE_AND_ALIEN));
                builder.add(LootItem.lootTableItem(EndDiscItems.MUSIC_DISC_GRASPING_AT_STARS));
                builder.add(LootItem.lootTableItem(EndDiscItems.MUSIC_DISC_ENDSEEKER));
                builder.add(LootItem.lootTableItem(EndDiscItems.MUSIC_DISC_EO_DRACONA));
                builder.add(LootItem.lootTableItem(EndDiscItems.MUSIC_DISC_ENDER_HOLLOW));
                builder.add(LootItem.lootTableItem(EndDiscItems.MUSIC_DISC_MOONLIT_UNDERCURRENTS));
                tableBuilder.withPool(builder);

                tableBuilder.withPool(LootPool
                        .lootPool()
                        .setRolls(UniformGenerator.between(2, 4))
                        .add(EmptyLootItem.emptyItem().setWeight(12))
                        .add(LootItem.lootTableItem(EndTemplates.NETHERITE_UPGRADE).setWeight(3))
                        .add(LootItem.lootTableItem(EndTemplates.HANDLE_ATTACHMENT).setWeight(2))
                        .add(LootItem.lootTableItem(EndTemplates.LEATHER_HANDLE_ATTACHMENT).setWeight(1))
                        .add(LootItem.lootTableItem(EndTemplates.TOOL_ASSEMBLY).setWeight(1))
                        .add(LootItem.lootTableItem(EndTemplates.AETERNIUM_UPGRADE).setWeight(1))
                        .add(LootItem.lootTableItem(EndTemplates.THALLASIUM_UPGRADE).setWeight(2))
                        .add(LootItem.lootTableItem(EndTemplates.TERMINITE_UPGRADE).setWeight(2))
                );

                // End Veil is a head-armor enchantment with no other route into a survival world:
                // BetterEnd emits no enchantment tags, so it is not in
                // #minecraft:in_enchanting_table and an enchanting table can never roll it. This pool
                // and the end_veil_book infusion recipe are the two ways to get it.
                //
                // EnchantRandomlyFunction rather than a hand-built component: it is the vanilla path
                // for "enchant this book with exactly this", and it knows to write stored_enchantments
                // on a book instead of enchantments, which is the difference between a usable book and
                // an inert one.
                // End Veil is a head-armor enchantment with no other route into a survival world:
                // BetterEnd emits no enchantment tags, so it is not in #minecraft:in_enchanting_table
                // and an enchanting table can never roll it. This pool and the end_veil_book infusion
                // recipe are the two ways to get it.
                //
                // EnchantRandomlyFunction rather than a hand-built component: it is the vanilla path
                // for "enchant this book with exactly this", and it knows to write stored_enchantments
                // on a book instead of enchantments, which is the difference between a usable book and
                // an inert one.
                //
                // The holder has to come from the registries this event hands out. Resolving it from
                // the world state instead yields a holder from an earlier registry instance: the book
                // still generates, but every attempt to encode it fails with "is not valid in current
                // registry set", so it can neither be saved nor sent to a client.
                tableBuilder.withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.25f))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                     .apply(EnchantRandomlyFunction
                                             .randomEnchantment()
                                             .withEnchantment(registries
                                                     .lookupOrThrow(Registries.ENCHANTMENT)
                                                     .getOrThrow(EndEnchantments.END_VEIL.key()))))
                );
            } else if (BuiltInLootTables.FISHING.equals(key)) {
                tableBuilder.modifyPools((modifier) -> modifier.when(IN_END.invert()));
                tableBuilder.withPool(LootPool.lootPool().when(IN_END).setRolls(ConstantValue.exactly(1.0F))
                                              .add(NestedLootTable.lootTableReference(FISHING_FISH)
                                                                  .setWeight(85)
                                                                  .setQuality(-1))
                                              .add(NestedLootTable.lootTableReference(FISHING_TREASURE)
                                                                  .setWeight(5)
                                                                  .setQuality(2))
                                              .add(NestedLootTable.lootTableReference(FISHING_JUNK)
                                                                  .setWeight(10)
                                                                  .setQuality(-2)));
            }
        });
    }

    public static ResourceKey<LootTable> getTable(Holder<Biome> biome) {
        ;
        if (biome.unwrapKey().isPresent()) {
            if (biome.is(EndBiomes.FOGGY_MUSHROOMLAND.key)) {
                return FOGGY_MUSHROOMLAND;
            } else if (biome.is(EndBiomes.CHORUS_FOREST.key)) {
                return CHORUS_FOREST;
            } else if (biome.is(EndBiomes.SHADOW_FOREST.key)) {
                return SHADOW_FOREST;
            } else if (biome.is(EndBiomes.LANTERN_WOODS.key)) {
                return LANTERN_WOODS;
            } else if (biome.is(EndBiomes.UMBRELLA_JUNGLE.key)) {
                return UMBRELLA_JUNGLE;
            }
        }
        return COMMON;
    }

}
