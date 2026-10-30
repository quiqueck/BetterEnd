package org.betterx.betterend.tab;



import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.complexmaterials.types.FlowerPot;
import org.betterx.betterend.complexmaterials.types.Furnace;
import org.betterx.betterend.complexmaterials.types.StoneLantern;
import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndLightBlocks;
import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.block.EndOreBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.sets.api.blocks.slots.StoneSlots;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class CreativeTabs {

    public static void register() {
        de.ambertation.wover.tabs.api.CreativeTabs
                .start(BetterEnd.C)
                .createTab("nature")
                .setPredicate(item -> PlantLikeBlockTrait.TAB_PREDICATE.contains(item)
                        || item == EndResourceItems.END_LILY_LEAF
                        || item == EndResourceItems.END_LILY_LEAF_DRIED
                )
                .setIcon(EndWoodBlocks.TENANEA_FLOWERS)
                .buildAndAdd()
                .createBlockOnlyTab(EndTerrainBlocks.END_MYCELIUM)
                .buildAndAdd()
                .createItemOnlyTab(EndResourceItems.ETERNAL_CRYSTAL)
                .buildAndAdd()
                .process(inTabOrder(EndItems.getModItems()))
                .registerAllTabs();
    }

    /**
     * The tabs list our items in registration order, and that order follows class loading rather than
     * the registry classes: a field referenced from an earlier registry (features, block entities, ...)
     * pulls its whole class in ahead of the others. That left some related entries far apart, so each
     * group below is listed together, in the given order, where its first registered member was. Only
     * the order in the tabs changes - no ID and nothing in a world.
     */
    private static Stream<Item> inTabOrder(List<Item> registered) {
        final List<Item> items = new ArrayList<>(registered);

        // Blocks
        final List<ItemLike> ironLanterns = new ArrayList<>();
        ironLanterns.add(EndMetalBlocks.IRON_SET.getBlock(MetalMaterial.BULB_LANTERN));
        ironLanterns.addAll(EndLightBlocks.IRON_BULB_LANTERN_COLORED.getBlocks());
        keepTogether(items, ironLanterns);
        keepTogether(items, List.of(EndMetalBlocks.AETERNIUM_BLOCK, EndFunctionalBlocks.AETERNIUM_ANVIL));
        keepTogether(items, List.of(EndOreBlocks.ENDER_ORE, EndMetalBlocks.ENDER_BLOCK));
        keepTogether(items, List.of(EndOreBlocks.AMBER_ORE, EndMetalBlocks.AMBER_BLOCK));
        keepTogether(items, List.of(
                EndPlantBlocks.NEON_CACTUS,
                EndDecorBlocks.NEON_CACTUS_BLOCK,
                EndDecorBlocks.NEON_CACTUS_BLOCK_STAIRS,
                EndDecorBlocks.NEON_CACTUS_BLOCK_SLAB
        ));
        keepTogether(items, List.of(EndStoneBlocks.END_STONE_STALACTITE, EndStoneBlocks.END_STONE_STALACTITE_CAVEMOSS));

        final List<ItemLike> endStone = new ArrayList<>(List.of(
                EndStoneBlocks.END_STONE_SLAB,
                EndStoneBlocks.END_STONE_STAIR,
                EndStoneBlocks.END_STONE_WALLS
        ));
        endStone.addAll(range(
                items,
                EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED_SOURCE),
                EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED_WALL)
        ));
        endStone.add(EndStoneBlocks.END_STONE_SET.getBlock(FlowerPot.SLOT));
        endStone.add(EndStoneBlocks.END_STONE_SET.getBlock(StoneLantern.SLOT));
        endStone.add(EndStoneBlocks.END_STONE_SET.getBlock(Furnace.SLOT));
        keepTogether(items, endStone);

        // Items
        keepTogether(items, List.of(EndMetalBlocks.THALLASIUM.rawOre, EndMetalBlocks.THALLASIUM.equipment.nugget));
        // The Aeternium set and the rest of the gear right after the Thallasium set, which ends with its boots
        final List<ItemLike> gear = new ArrayList<>();
        gear.add(EndMetalBlocks.THALLASIUM.equipment.get(ArmorSlot.BOOTS_SLOT));
        gear.addAll(range(items, EndEquipmentItems.AETERNIUM_SET.ingot, EndEquipmentItems.NETHERITE_HAMMER));
        keepTogether(items, gear);

        // Plants
        keepTogether(items, List.of(
                EndWaterPlantBlocks.END_LILY_SEED,
                EndResourceItems.END_LILY_LEAF,
                EndResourceItems.END_LILY_LEAF_DRIED
        ));

        return items.stream();
    }

    /**
     * Moves the given entries next to each other, in the given order, to where the first of them was
     * registered. Entries that are missing or not in {@code items} (e.g. blocks without an item) are skipped.
     */
    private static void keepTogether(List<Item> items, List<? extends ItemLike> group) {
        final List<Item> members = group.stream().filter(Objects::nonNull).map(ItemLike::asItem).filter(items::contains).distinct().toList();
        if (members.isEmpty()) return;

        final int first = members.stream().mapToInt(items::indexOf).min().getAsInt();
        items.removeAll(members);
        items.addAll(first, members);
    }

    /**
     * @return the entries from {@code first} to {@code last} (both included), in their current order
     */
    private static List<Item> range(List<Item> items, ItemLike first, ItemLike last) {
        if (first == null || last == null) return List.of();
        final int from = items.indexOf(first.asItem());
        final int to = items.indexOf(last.asItem());
        if (from < 0 || to < from) return List.of();
        return List.copyOf(items.subList(from, to + 1));
    }
}
