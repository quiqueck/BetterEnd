package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import org.betterx.betterend.util.LootTableUtil;
import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.complex.api.equipment.ToolSlot;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverLootTableProvider;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

public class EndChestLootTableProvider extends WoverLootTableProvider {
    public EndChestLootTableProvider(
            ModCore modCore
    ) {
        super(modCore, LootContextParamSets.CHEST);
    }

    private LootPool.Builder fishing() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .add(LootItem.lootTableItem(EndItems.END_FISH_RAW));
    }

    private LootPool.Builder fishingJunk() {
        final LootPool.Builder builder = LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .add(LootItem.lootTableItem(EndItems.END_LILY_LEAF))
                .add(LootItem.lootTableItem(Items.ENDER_PEARL))
                .add(LootItem.lootTableItem(Items.CHORUS_FRUIT))
                .add(LootItem.lootTableItem(EndItems.GELATINE))
                .add(LootItem.lootTableItem(EndItems.CRYSTAL_SHARDS))
                .add(LootItem.lootTableItem(EndItems.HYDRALUX_PETAL).when(IN_SULPHUR_SPRINGS));
        addCharnia(builder);
        return builder;
    }

    private LootPool.Builder fishingTreasure(HolderLookup.@NotNull Provider lookup) {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(EndBlocks.TERMINITE.equipment.swordBlade))
                .add(LootItem.lootTableItem(EndBlocks.TERMINITE.equipment.forgedPlate))
                .add(LootItem.lootTableItem(EndBlocks.MENGER_SPONGE))
                .add(LootItem.lootTableItem(Items.BOW)
                             .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.25F)))
                             .apply(EnchantWithLevelsFunction.enchantWithLevels(lookup, ConstantValue.exactly(30.0F))))
                .add(LootItem.lootTableItem(Items.FISHING_ROD)
                             .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.25F)))
                             .apply(EnchantWithLevelsFunction.enchantWithLevels(lookup, ConstantValue.exactly(30.0F))))
                .add(LootItem.lootTableItem(Items.BOOK)
                             .apply(EnchantWithLevelsFunction.enchantWithLevels(lookup, ConstantValue.exactly(30.0F))));
    }

    private LootPool.Builder foggyMushroomland() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(4, 8))
                .add(LootItem.lootTableItem(EndBlocks.MOSSY_GLOWSHROOM.getBlock(SlotType.PLANKS)))
                .add(LootItem.lootTableItem(EndBlocks.MOSSY_GLOWSHROOM_SAPLING))
                .add(LootItem.lootTableItem(EndBlocks.BLUE_VINE_SEED));
    }

    private LootPool.Builder chorusForest() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(4, 8))
                .add(LootItem.lootTableItem(EndBlocks.PYTHADENDRON.getBlock(SlotType.PLANKS)))
                .add(LootItem.lootTableItem(EndBlocks.PYTHADENDRON_SAPLING))
                .add(LootItem.lootTableItem(EndBlocks.CHORUS_MUSHROOM))
                .add(LootItem.lootTableItem(EndTemplates.HANDLE_ATTACHMENT));
    }

    private LootPool.Builder shadowForest() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(4, 8))
                .add(LootItem.lootTableItem(EndBlocks.DRAGON_TREE.getBlock(SlotType.PLANKS)))
                .add(LootItem.lootTableItem(EndBlocks.DRAGON_TREE_SAPLING))
                .add(LootItem.lootTableItem(EndBlocks.SHADOW_BERRY))
                .add(LootItem.lootTableItem(EndItems.SHADOW_BERRY_RAW));
    }

    private LootPool.Builder lanternWoods() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(4, 8))
                .add(LootItem.lootTableItem(EndBlocks.LUCERNIA.getBlock(SlotType.PLANKS)))
                .add(LootItem.lootTableItem(EndBlocks.LUCERNIA_SAPLING))
                .add(LootItem.lootTableItem(EndBlocks.BOLUX_MUSHROOM));
    }

    private LootPool.Builder umbrellaJungle() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(4, 8))
                .add(LootItem.lootTableItem(EndBlocks.UMBRELLA_TREE.getBlock(SlotType.PLANKS)))
                .add(LootItem.lootTableItem(EndBlocks.UMBRELLA_TREE_SAPLING))
                .add(LootItem.lootTableItem(EndBlocks.SMALL_JELLYSHROOM));
    }


    private LootPool.Builder upgradeLootPool() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .add(LootItem
                        .lootTableItem(EndTemplates.THALLASIUM_UPGRADE)
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(LootItem
                        .lootTableItem(EndTemplates.TERMINITE_UPGRADE)
                        .setWeight(4)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(LootItem
                        .lootTableItem(EndTemplates.AETERNIUM_UPGRADE)
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(LootItem
                        .lootTableItem(EndTemplates.NETHERITE_UPGRADE)
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                );
    }

    private LootPool.Builder plateUpgradeLootPool() {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                        .lootTableItem(EndTemplates.PLATE_UPGRADE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                        .when(LootItemRandomChanceCondition.randomChance(0.70F))
                );
    }

    private LootPool.Builder leatherHandleLootPool() {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                        .lootTableItem(EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .when(LootItemRandomChanceCondition.randomChance(0.75F))
                );
    }

    private LootPool.Builder handleLootPool() {
        return LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                        .lootTableItem(EndTemplates.HANDLE_ATTACHMENT)
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                )
                .add(LootItem
                        .lootTableItem(EndTemplates.TOOL_ASSEMBLY)
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                );
    }

    private LootPool.Builder villageBonusLoot() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .add(LootItem
                        .lootTableItem(EndItems.AETERNIUM_SET.ingot)
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                )
                .add(LootItem
                        .lootTableItem(EndBlocks.FLAVOLITE_RUNED_ETERNAL)
                        .setWeight(4)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(LootItem
                        .lootTableItem(EndItems.ETERNAL_CRYSTAL)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(LootItem
                        .lootTableItem(Items.SHULKER_SHELL)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
                );
    }

    private LootPool.Builder elytraLoot(float chance) {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(1, 3))
                .add(LootItem
                        .lootTableItem(Items.ELYTRA)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                ).add(LootItem
                        .lootTableItem(Items.FIREWORK_ROCKET)
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8)))
                )
                .when(LootItemRandomChanceCondition.randomChance(chance));
    }

    private LootPool.Builder simpleVillageLoot() {
        return LootPool
                .lootPool()
                .setRolls(UniformGenerator.between(1, 3))
                .add(LootItem
                        .lootTableItem(EndItems.LEATHER_WRAPPED_STICK)
                        .setWeight(10)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                )
                .add(LootItem
                        .lootTableItem(Items.ENDER_PEARL)
                        .setWeight(9)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5)))
                )
                .add(LootItem
                        .lootTableItem(EndItems.SHADOW_BERRY_COOKED)
                        .setWeight(8)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                )
                .add(LootItem
                        .lootTableItem(EndItems.BLOSSOM_BERRY_JELLY)
                        .setWeight(4)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))
                )
                .add(LootItem
                        .lootTableItem(EndBlocks.THALLASIUM.equipment.shovelHead)
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                )
                .add(LootItem
                        .lootTableItem(Blocks.ENDER_CHEST)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                );
    }

    private static void addCharnia(LootPool.Builder pool) {
        pool.add(LootItem.lootTableItem(EndBlocks.CHARNIA_CYAN)
                         .when(IN_GLOWING_GRASSLANDS.or(IN_MEGALAKE)
                                                    .or(IN_MEGALAKE_GROVE)
                                                    .or(
                                                            IN_NEON_OASIS)));
        pool.add(LootItem.lootTableItem(EndBlocks.CHARNIA_LIGHT_BLUE)
                         .when(IN_FOGGY_MUSHROOMLAND.or(IN_GLOWING_GRASSLANDS)
                                                    .or(IN_MEGALAKE)
                                                    .or(IN_MEGALAKE_GROVE)
                                                    .or(IN_UMBRELLA_JUNGLE)));
        pool.add(LootItem.lootTableItem(EndBlocks.CHARNIA_GREEN)
                         .when(IN_GLOWING_GRASSLANDS.or(IN_NEON_OASIS)
                                                    .or(IN_SULPHUR_SPRINGS)
                                                    .or(IN_UMBRELLA_JUNGLE)));
        pool.add(LootItem.lootTableItem(EndBlocks.CHARNIA_RED)
                         .when(IN_AMBER_LAND.or(IN_LANTERN_WOODS)
                                            .or(IN_NEON_OASIS)));
        pool.add(LootItem.lootTableItem(EndBlocks.CHARNIA_ORANGE)
                         .when(IN_AMBER_LAND.or(IN_LANTERN_WOODS)
                                            .or(IN_SULPHUR_SPRINGS)));
        pool.add(LootItem.lootTableItem(EndBlocks.CHARNIA_PURPLE)
                         .when(IN_CHORUS_FOREST.or(IN_SHADOW_FOREST)));
    }

    private static LootTable.Builder includeCommonItems(LootTable.Builder table) {
        LootPool.Builder builder = LootPool.lootPool();
        builder.setRolls(UniformGenerator.between(0, 2));
        builder.add(LootItem.lootTableItem(EndItems.MUSIC_DISC_STRANGE_AND_ALIEN));
        builder.add(LootItem.lootTableItem(EndItems.MUSIC_DISC_GRASPING_AT_STARS));
        builder.add(LootItem.lootTableItem(EndItems.MUSIC_DISC_ENDSEEKER));
        builder.add(LootItem.lootTableItem(EndItems.MUSIC_DISC_EO_DRACONA));
        table.withPool(builder);

        builder = LootPool.lootPool();
        builder.setRolls(UniformGenerator.between(4, 8));
        builder.add(LootItem.lootTableItem(EndBlocks.THALLASIUM.equipment.ingot));
        builder.add(LootItem.lootTableItem(EndBlocks.THALLASIUM.rawOre));
        builder.add(LootItem.lootTableItem(Items.ENDER_PEARL));
        table.withPool(builder);

        builder = LootPool.lootPool();
        builder.setRolls(UniformGenerator.between(2, 4));
        builder.add(LootItem.lootTableItem(EndBlocks.TERMINITE.equipment.ingot));
        builder.add(LootItem.lootTableItem(EndItems.ENDER_SHARD));
        builder.add(LootItem.lootTableItem(EndBlocks.AURORA_CRYSTAL));
        builder.add(LootItem.lootTableItem(EndBlocks.THALLASIUM.equipment.get(ToolSlot.AXE_SLOT)));
        builder.add(LootItem.lootTableItem(EndBlocks.THALLASIUM.equipment.get(ToolSlot.PICKAXE_SLOT)));
        builder.add(LootItem.lootTableItem(EndBlocks.THALLASIUM.equipment.get(ToolSlot.HOE_SLOT)));
        builder.add(LootItem.lootTableItem(EndBlocks.THALLASIUM.equipment.get(ToolSlot.SWORD_SLOT)));
        builder.add(LootItem.lootTableItem(EndBlocks.THALLASIUM.equipment.get(ToolSlot.SHOVEL_SLOT)));
        builder.add(LootItem.lootTableItem(Items.ENDER_EYE));
        builder.add(LootItem.lootTableItem(Blocks.OBSIDIAN));
        table.withPool(builder);

        builder = LootPool.lootPool();
        builder.setRolls(UniformGenerator.between(0, 4));
        builder.add(LootItem.lootTableItem(EndBlocks.FLAVOLITE_RUNED));
        builder.add(LootItem.lootTableItem(EndItems.AETERNIUM_SET.ingot));
        builder.add(LootItem.lootTableItem(EndItems.AMBER_GEM));
        builder.add(LootItem.lootTableItem(Items.END_CRYSTAL));
        builder.add(LootItem.lootTableItem(Items.GHAST_TEAR));
        table.withPool(builder);

        return table;
    }

    private static LootItemCondition.Builder biomePredicate(HolderLookup.@NotNull Provider lookup, BiomeKey<?> key) {
        final HolderLookup.RegistryLookup<Biome> biomeRegistry = lookup.lookupOrThrow(Registries.BIOME);
        Holder.Reference<Biome> holder = biomeRegistry.get(key.key).orElse(null);
        return LocationCheck.checkLocation(LocationPredicate.Builder
                .location()
                .setBiomes(HolderSet.direct(holder)));

    }

    private static LootItemCondition.Builder IN_FOGGY_MUSHROOMLAND;
    private static LootItemCondition.Builder IN_CHORUS_FOREST;
    private static LootItemCondition.Builder IN_AMBER_LAND;
    private static LootItemCondition.Builder IN_GLOWING_GRASSLANDS;
    private static LootItemCondition.Builder IN_LANTERN_WOODS;
    private static LootItemCondition.Builder IN_MEGALAKE;
    private static LootItemCondition.Builder IN_MEGALAKE_GROVE;
    private static LootItemCondition.Builder IN_NEON_OASIS;
    private static LootItemCondition.Builder IN_SHADOW_FOREST;
    private static LootItemCondition.Builder IN_SULPHUR_SPRINGS;
    private static LootItemCondition.Builder IN_UMBRELLA_JUNGLE;

    @Override
    protected void boostrap(
            HolderLookup.@NotNull Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {
        IN_FOGGY_MUSHROOMLAND = biomePredicate(lookup, EndBiomes.FOGGY_MUSHROOMLAND);
        IN_CHORUS_FOREST = biomePredicate(lookup, EndBiomes.CHORUS_FOREST);
        IN_AMBER_LAND = biomePredicate(lookup, EndBiomes.AMBER_LAND);
        IN_GLOWING_GRASSLANDS = biomePredicate(lookup, EndBiomes.GLOWING_GRASSLANDS);
        IN_LANTERN_WOODS = biomePredicate(lookup, EndBiomes.LANTERN_WOODS);
        IN_MEGALAKE = biomePredicate(lookup, EndBiomes.MEGALAKE);
        IN_MEGALAKE_GROVE = biomePredicate(lookup, EndBiomes.MEGALAKE_GROVE);
        IN_NEON_OASIS = biomePredicate(lookup, EndBiomes.NEON_OASIS);
        IN_SHADOW_FOREST = biomePredicate(lookup, EndBiomes.SHADOW_FOREST);
        IN_SULPHUR_SPRINGS = biomePredicate(lookup, EndBiomes.SULPHUR_SPRINGS);
        IN_UMBRELLA_JUNGLE = biomePredicate(lookup, EndBiomes.UMBRELLA_JUNGLE);

        biConsumer.accept(
                LootTableUtil.VILLAGE_LOOT,
                LootTable.lootTable()
                         .withPool(simpleVillageLoot())
                         .withPool(plateUpgradeLootPool())
        );

        biConsumer.accept(
                LootTableUtil.VILLAGE_TEMPLATE_LOOT,
                LootTable.lootTable()
                         .withPool(simpleVillageLoot())
                         .withPool(handleLootPool())
                         .withPool(leatherHandleLootPool())
                         .withPool(plateUpgradeLootPool())
                         .withPool(upgradeLootPool())
        );

        biConsumer.accept(
                LootTableUtil.VILLAGE_BONUS_LOOT,
                LootTable.lootTable()
                         .withPool(leatherHandleLootPool())
                         .withPool(plateUpgradeLootPool())
                         .withPool(villageBonusLoot())
                         .withPool(elytraLoot(0.2f))
        );

        biConsumer.accept(
                LootTableUtil.FISHING_FISH,
                LootTable.lootTable()
                         .withPool(fishing())
        );

        biConsumer.accept(
                LootTableUtil.FISHING_JUNK,
                LootTable.lootTable()
                         .withPool(fishingJunk())
        );

        biConsumer.accept(
                LootTableUtil.FISHING_TREASURE,
                LootTable.lootTable()
                         .withPool(fishingTreasure(lookup))
        );

        biConsumer.accept(
                LootTableUtil.FOGGY_MUSHROOMLAND,
                includeCommonItems(LootTable.lootTable())
                        .withPool(foggyMushroomland())
        );

        biConsumer.accept(
                LootTableUtil.CHORUS_FOREST,
                includeCommonItems(LootTable.lootTable())
                        .withPool(chorusForest())
        );

        biConsumer.accept(
                LootTableUtil.SHADOW_FOREST,
                includeCommonItems(LootTable.lootTable())
                        .withPool(shadowForest())
        );

        biConsumer.accept(
                LootTableUtil.LANTERN_WOODS,
                includeCommonItems(LootTable.lootTable())
                        .withPool(lanternWoods())
        );

        biConsumer.accept(
                LootTableUtil.UMBRELLA_JUNGLE,
                includeCommonItems(LootTable.lootTable())
                        .withPool(umbrellaJungle())
        );

        biConsumer.accept(
                LootTableUtil.BIOME_CHEST,
                includeCommonItems(LootTable.lootTable())
                        .withPool(umbrellaJungle().when(IN_UMBRELLA_JUNGLE))
                        .withPool(lanternWoods().when(IN_LANTERN_WOODS))
                        .withPool(shadowForest().when(IN_SHADOW_FOREST))
                        .withPool(chorusForest().when(IN_CHORUS_FOREST))
                        .withPool(foggyMushroomland().when(IN_FOGGY_MUSHROOMLAND))
        );

        biConsumer.accept(
                LootTableUtil.COMMON,
                includeCommonItems(LootTable.lootTable())
        );
    }
}
