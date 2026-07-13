package org.betterx.betterend.util;

import org.betterx.bclib.items.DebugDataItem;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.world.structures.village.VillagePools;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class DebugHelpers {
    private static boolean didCreateDebugItems = false;

    public static void generateDebugItems() {
        if (didCreateDebugItems) return;
        didCreateDebugItems = true;
        BetterEnd.LOGGER.warn("Generating Debug Helpers");

        EndItems.getItemRegistry().register(
                "debug/village_loot",
                DebugDataItem.forLootTable(LootTableUtil.VILLAGE_LOOT, Items.IRON_INGOT)
        );

        EndItems.getItemRegistry().register(
                "debug/village_bonus",
                DebugDataItem.forLootTable(LootTableUtil.VILLAGE_BONUS_LOOT, Items.DIAMOND)
        );

        EndItems.getItemRegistry().register(
                "debug/village_template",
                DebugDataItem.forLootTable(LootTableUtil.VILLAGE_TEMPLATE_LOOT, Items.GOLD_INGOT)
        );

        EndItems.getItemRegistry().register(
                "debug/biome_loot",
                DebugDataItem.forLootTable(LootTableUtil.BIOME_CHEST, Items.OAK_LEAVES)
        );

        EndItems.getItemRegistry().register(
                "debug/jigsaw_entrance",
                DebugDataItem.forHouseEntranceJigSaw(BetterEnd.MOD_ID, null, Items.OAK_DOOR)
        );

        EndItems.getItemRegistry().register(
                "debug/jigsaw_street_entrance",
                DebugDataItem.forHouseEntranceJigSaw(BetterEnd.MOD_ID, VillagePools.HOUSES_KEY.key, Items.IRON_DOOR)
        );

        EndItems.getItemRegistry().register(
                "debug/jigsaw_street",
                DebugDataItem.forSteetJigSaw(
                        BetterEnd.MOD_ID,
                        VillagePools.STREET_KEY.key,
                        Items.ENDER_PEARL
                )
        );

        EndItems.getItemRegistry().register(
                "debug/jigsaw_street_deco",
                DebugDataItem.forStreetDecorationJigSaw(
                        BetterEnd.MOD_ID,
                        VillagePools.STREET_DECO_KEY.key,
                        Items.ENDER_EYE
                )
        );
        EndItems.getItemRegistry().register(
                "debug/jigsaw_street_big_deco",
                DebugDataItem.forDecorationJigSaw(
                        BetterEnd.MOD_ID,
                        VillagePools.DECORATIONS_KEY.key,
                        Items.SLIME_BALL
                )
        );

        EndItems.getItemRegistry().register(
                "debug/jigsaw_big_deco",
                DebugDataItem.forDecorationJigSaw(
                        BetterEnd.MOD_ID,
                        null,
                        Items.TURTLE_HELMET
                )
        );

        EndItems.getItemRegistry().register(
                "debug/jigsaw_deco",
                DebugDataItem.forStreetDecorationJigSaw(
                        BetterEnd.MOD_ID,
                        null,
                        Items.LANTERN
                )
        );

        EndItems.getItemRegistry().register(
                "debug/fill_base_void",
                new DebugDataItem((player, entity, useOnContext) -> DebugDataItem.fillStructureEntityBounds(
                        useOnContext, entity,
                        state -> state.is(Blocks.END_STONE),
                        Blocks.STRUCTURE_VOID.defaultBlockState(),
                        false
                ), false, BuiltInRegistries.ITEM.getKey(Items.WATER_BUCKET))
        );

        Item item = EndItems.getItemRegistry().register(
                "debug/fill_air",
                new DebugDataItem((player, entity, useOnContext) -> DebugDataItem.fillStructureEntityBounds(
                        useOnContext, entity,
                        state -> state.isAir() || state.is(Blocks.STRUCTURE_VOID),
                        Blocks.STRUCTURE_VOID.defaultBlockState(),
                        true
                ), false, BuiltInRegistries.ITEM.getKey(Items.BUCKET))
        );
    }

}
