package org.betterx.betterend.util;


import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBiomes;
import de.ambertation.wover.loot.api.LootTableManager;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * The {@link ResourceKey}s of every loot table BetterEnd ships.
 * <p>
 * The two additions BetterEnd makes to vanilla tables - the end-city treasure extras and the in-End fishing
 * override - used to be Java appenders registered from here against {@code LootTableEvents.MODIFY}. They are
 * data now, emitted by {@code EndLootAdditionProvider} to {@code data/betterend/wover/loot_addition/}, so this
 * class only holds keys.
 */
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
    public static final ResourceKey<LootTable> END_CITY_EXTRA = LootTableManager.createLootTableKey(BetterEnd.C, "chests/end_city_extra");
    public static final ResourceKey<LootTable> FISHING_FISH = LootTableManager.createLootTableKey(BetterEnd.C, "gameplay/fishing/fish");
    public static final ResourceKey<LootTable> FISHING_TREASURE = LootTableManager.createLootTableKey(BetterEnd.C, "gameplay/fishing/treasure");
    public static final ResourceKey<LootTable> FISHING_JUNK = LootTableManager.createLootTableKey(BetterEnd.C, "gameplay/fishing/junk");

    public static ResourceKey<LootTable> getTable(Holder<Biome> biome) {
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
