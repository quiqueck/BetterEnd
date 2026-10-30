package org.betterx.betterend.registry;

import org.betterx.bclib.api.v2.ComposterAPI;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.tool.EndHammerItem;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.worlds.together.tag.v3.CommonBlockTags;
import org.betterx.worlds.together.tag.v3.CommonItemTags;
import org.betterx.worlds.together.tag.v3.TagManager;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import com.google.common.collect.Lists;

import java.util.List;

public class EndTags {
    // Table with common (c) tags:
    // https://fabricmc.net/wiki/tutorial:tags

    // Block Tags
    public static final TagKey<Block> PEDESTALS = TagManager.BLOCKS.makeTag(BetterEnd.MOD_ID, "pedestal");
    /**
     * The hull blocks of the crashed ship that may rot away (its block_rot processor); chests, TNT and the
     * dragon head always stay.
     */
    public static final TagKey<Block> CRASHED_SHIP_ROTS = TagManager.BLOCKS.makeTag(BetterEnd.MOD_ID, "crashed_ship_rots");

    // Item Tags
    public static final TagKey<Item> ALLOYING_IRON = TagManager.ITEMS.makeTag(BetterEnd.MOD_ID, "alloying_iron");
    public static final TagKey<Item> ALLOYING_GOLD = TagManager.ITEMS.makeTag(BetterEnd.MOD_ID, "alloying_gold");
    public static final TagKey<Item> ALLOYING_COPPER = TagManager.ITEMS.makeTag(BetterEnd.MOD_ID, "alloying_copper");

    public static final TagKey<Block> BONEMEAL_SOURCE_DRAGON_BONE = TagManager.BLOCKS.makeTogetherTag(
            "bonemeal/source/dragon_bone"
    );
    public static final TagKey<Block> BONEMEAL_TARGET_DRAGON_BONE = TagManager.BLOCKS.makeTogetherTag(
            "bonemeal/target/dragon_bone"
    );

    public static final TagKey<Block> BONEMEAL_SOURCE_WATER_GRASS = TagManager.BLOCKS.makeTag(
            BetterEnd.MOD_ID,
            "bonemeal/source/water_grass"
    );

    public static final TagKey<Block> BONEMEAL_TARGET_WATER_GRASS = TagManager.BLOCKS.makeTag(
            BetterEnd.MOD_ID,
            "bonemeal/target/water_grass"
    );

    public static void register() {
        addEndGround(EndBlocks.THALLASIUM.ore);
        addEndGround(EndBlocks.ENDSTONE_DUST);
        addEndGround(EndBlocks.AMBER_ORE);
        addEndGround(EndBlocks.CAVE_MOSS);

        List<Item> ITEM_HAMMERS = Lists.newArrayList();
        EndItems.getModItems().forEach(item -> {
            if (item.isEdible()) {
                FoodProperties food = item.getFoodProperties();
                if (food != null) {
                    float compost = food.getNutrition() * food.getSaturationModifier() * 0.18F;
                    ComposterAPI.allowCompost(compost, item);
                }
            }
            if (item instanceof EndHammerItem) {
                ITEM_HAMMERS.add(item);
            }
        });

        TagManager.BLOCKS.add(
                CommonBlockTags.END_STONES,
                EndBlocks.ENDER_ORE,
                EndBlocks.BRIMSTONE
        );
        TagManager.BLOCKS.add(CommonBlockTags.END_STONES, EndBlocks.BRIMSTONE);
        TagManager.BLOCKS.add(
                CRASHED_SHIP_ROTS,
                Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR, Blocks.PURPUR_STAIRS, Blocks.PURPUR_SLAB,
                Blocks.END_STONE_BRICKS, Blocks.OBSIDIAN, Blocks.MAGENTA_STAINED_GLASS, Blocks.END_ROD, Blocks.LADDER
        );
        TagManager.BLOCKS.add(BlockTags.ANVIL, EndBlocks.AETERNIUM_ANVIL);
        TagManager.BLOCKS.add(BlockTags.BEACON_BASE_BLOCKS, EndBlocks.AETERNIUM_BLOCK);
        TagManager.ITEMS.add(ItemTags.BEACON_PAYMENT_ITEMS, EndItems.AETERNIUM_INGOT);
        TagManager.BLOCKS.add(
                BlockTags.DRAGON_IMMUNE,
                EndBlocks.ENDER_ORE,
                EndBlocks.ETERNAL_PEDESTAL,
                EndBlocks.FLAVOLITE_RUNED_ETERNAL,
                EndBlocks.FLAVOLITE_RUNED
        );
        TagManager.ITEMS.add(CommonItemTags.IRON_INGOTS, EndBlocks.THALLASIUM.ingot);

        TagManager.ITEMS.add(ALLOYING_IRON, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
        TagManager.ITEMS.add(ALLOYING_GOLD, Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.RAW_GOLD);
        TagManager.ITEMS.add(ALLOYING_COPPER, Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);

        TagManager.ITEMS.add(ItemTags.FISHES, EndItems.END_FISH_RAW, EndItems.END_FISH_COOKED);

        TagManager.BLOCKS.addOtherTags(BONEMEAL_TARGET_WATER_GRASS, CommonBlockTags.END_STONES);
    }

    public static void addEndGround(Block bl) {
        TagManager.BLOCKS.add(CommonBlockTags.END_STONES, bl);
    }

    public static void addBiomeSurfaceToEndGroup(EndBiome b) {
        addEndGround(b.getTopMaterial().getBlock());
        addEndGround(b.getAltTopMaterial().getBlock());
        addEndGround(b.getUnderMaterial().getBlock());
    }
}
