package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class EndTags {
    // Table with common (c) tags:
    // https://fabricmc.net/wiki/tutorial:tags

    public static final TagKey<Biome> IS_END_CAVE = TagManager.BIOMES.makeTag(BetterEnd.C, "is_end_cave");
    public static final TagKey<Biome> IS_END_HIGH_OR_MIDLAND = TagManager.BIOMES.makeTag(
            BetterEnd.C,
            "is_end/high_or_midland"
    );

    // Block Tags
    public static final TagKey<Block> PEDESTALS = TagManager.BLOCKS.makeTag(BetterEnd.C, "pedestal");

    // Item Tags
    public static final TagKey<Item> ALLOYING_IRON = TagManager.ITEMS.makeTag(BetterEnd.C, "alloying_iron");
    public static final TagKey<Item> ALLOYING_GOLD = TagManager.ITEMS.makeTag(BetterEnd.C, "alloying_gold");
    public static final TagKey<Item> ALLOYING_COPPER = TagManager.ITEMS.makeTag(BetterEnd.C, "alloying_copper");

    public static final TagKey<Item> ANVIL_AETERNIUM_TOOL = TagManager.ITEMS.makeTag(
            BetterEnd.C,
            "anvil_tool/aeternium"
    );
    public static final TagKey<Item> ANVIL_IRON_TOOL = TagManager.ITEMS.makeTag(BetterEnd.C, "anvil_tool/iron");
    public static final TagKey<Item> ANVIL_NETHERITE_TOOL = TagManager.ITEMS.makeTag(
            BetterEnd.C,
            "anvil_tool/netherite"
    );
    public static final TagKey<Item> ANVIL_DIAMOND_TOOL = TagManager.ITEMS.makeTag(BetterEnd.C, "anvil_tool/diamond");


    public static final TagKey<Block> BONEMEAL_SOURCE_DRAGON_BONE = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "bonemeal/source/dragon_bone"
    );
    public static final TagKey<Block> BONEMEAL_TARGET_DRAGON_BONE = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "bonemeal/target/dragon_bone"
    );

    public static final TagKey<Block> BONEMEAL_SOURCE_WATER_GRASS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "bonemeal/source/water_grass"
    );

    public static final TagKey<Block> BONEMEAL_TARGET_WATER_GRASS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "bonemeal/target/water_grass"
    );

    public static final TagKey<Block> INCORRECT_FOR_AETERNIUM_TOOL = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "incorrect_for_aeternium_tool"
    );


    /* Block Survival Tags */
    public static final TagKey<Block> SURVIVES_ON_PALLIDIUM = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/pallidium"
    );
    public static final TagKey<Block> SURVIVES_ON_AMBER_MOSS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/amber_moss"
    );
    public static final TagKey<Block> SURVIVES_ON_BRIMSTONE = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/brimstone"
    );
    public static final TagKey<Block> SURVIVES_ON_CHORUS_NYLIUM = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/chorus_nylium"
    );
    public static final TagKey<Block> SURVIVES_ON_END_BONE = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/end_bone"
    );
    public static final TagKey<Block> SURVIVES_ON_END_MOSS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/end_moss"
    );
    public static final TagKey<Block> SURVIVES_ON_END_STONE = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/end_stone"
    );
    public static final TagKey<Block> SURVIVES_ON_END_STONE_OR_TREES = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/end_stone_or_trees"
    );
    public static final TagKey<Block> SURVIVES_ON_JUNGLE_MOSS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/jungle_moss"
    );
    public static final TagKey<Block> SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/jungle_moss_or_mycelium"
    );
    public static final TagKey<Block> SURVIVES_ON_MOSS_OR_DUST = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/moss_or_dust"
    );
    public static final TagKey<Block> SURVIVES_ON_MOSS_OR_MYCELIUM = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/moss_or_mycelium"
    );
    public static final TagKey<Block> SURVIVES_ON_PINK_MOSS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/pink_moss"
    );
    public static final TagKey<Block> SURVIVES_ON_RUTISCUS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/rutiscus"
    );
    public static final TagKey<Block> SURVIVES_ON_SHADOW_GRASS = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/shadow_grass"
    );
    public static final TagKey<Block> SURVIVES_ON_SULPHURIC_ROCK = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/sulphuric_rock"
    );

    /**
     * Vanilla dirt-like soils that a potted vanilla plant may be planted on inside a
     * BetterEnd flower pot. Used as the {@code valid_soils} tag for the vanilla flower-pot
     * plant set (see {@code EndPottablePlantProvider}).
     */
    public static final TagKey<Block> SURVIVES_ON_DIRT = TagManager.BLOCKS.makeTag(
            BetterEnd.C,
            "survives_on/dirt"
    );

    public static void register() {


    }
}
