package org.betterx.betterend.registry;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.BaseOreBlock;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.blocks.SimpleLeavesBlock;
import org.betterx.bclib.blocks.StalactiteBlock;
import org.betterx.bclib.trait.block.*;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.*;
import org.betterx.betterend.blocks.EndPortalBlock;
import org.betterx.betterend.blocks.basis.*;
import org.betterx.betterend.complexmaterials.*;
import org.betterx.betterend.complexmaterials.types.*;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.item.material.EndToolTier;
import org.betterx.betterend.trait.block.IceBlockTrait;
import org.betterx.betterend.trait.block.SnowBlockTrait;
import org.betterx.betterend.trait.block.StalactiteBlockTrait;
import org.betterx.betterend.trait.block.TerrainBlockTrait;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.DefaultBlockDefinition;
import org.betterx.wover.block.api.VanillaBlockDefinition;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeMaterial;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class EndBlocks {
    private static BlockRegistry BLOCKS_REGISTRY;

    // Terrain //
    public static final Block ENDSTONE_DUST = defineBlock("endstone_dust", EndstoneDustBlock::new)
            .replacePropertiesWithCopy(Blocks.SAND)
            .mapColor(Blocks.END_STONE.defaultMapColor())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block END_MYCELIUM = registerEndTerrain("end_mycelium", MapColor.COLOR_LIGHT_BLUE);


    public static final Block END_MOSS = registerEndTerrain(
            "end_moss", MapColor.COLOR_CYAN,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block CHORUS_NYLIUM = registerEndTerrain(
            "chorus_nylium", MapColor.COLOR_MAGENTA,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block CAVE_MOSS = registerEndTerrain(
            "cave_moss", MapColor.COLOR_PURPLE,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block CRYSTAL_MOSS = registerEndTerrain(
            "crystal_moss", MapColor.COLOR_CYAN,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block SHADOW_GRASS = registerEndTerrain(
            "shadow_grass", MapColor.COLOR_BLACK,
            ShadowGrassBlock::new,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block PINK_MOSS = registerEndTerrain(
            "pink_moss", MapColor.COLOR_PINK,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block AMBER_MOSS = registerEndTerrain(
            "amber_moss", MapColor.COLOR_ORANGE,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block JUNGLE_MOSS = registerEndTerrain(
            "jungle_moss", MapColor.COLOR_GREEN,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block SANGNUM = registerEndTerrain(
            "sangnum", MapColor.COLOR_RED,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block RUTISCUS = registerEndTerrain(
            "rutiscus", MapColor.COLOR_ORANGE,
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    public static final Block PALLIDIUM_FULL = registerEndTerrain(
            "pallidium_full", MapColor.COLOR_LIGHT_GRAY,
            p -> new PallidiumBlock(p, "full", null),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );
    public static final Block PALLIDIUM_HEAVY = registerEndTerrain(
            "pallidium_heavy", MapColor.COLOR_LIGHT_GRAY,
            p -> new PallidiumBlock(p, "heavy", PALLIDIUM_FULL),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );
    public static final Block PALLIDIUM_THIN = registerEndTerrain(
            "pallidium_thin", MapColor.COLOR_LIGHT_GRAY,
            p -> new PallidiumBlock(p, "thin", PALLIDIUM_FULL),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );
    public static final Block PALLIDIUM_TINY = registerEndTerrain(
            "pallidium_tiny", MapColor.COLOR_LIGHT_GRAY,
            p -> new PallidiumBlock(p, "tiny", PALLIDIUM_FULL),
            BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM
    );

    // Paths //
    public static final Block END_MYCELIUM_PATH = registerPath("end_mycelium_path", END_MYCELIUM);
    public static final Block END_MOSS_PATH = registerPath("end_moss_path", END_MOSS);
    public static final Block CHORUS_NYLIUM_PATH = registerPath("chorus_nylium_path", CHORUS_NYLIUM);
    public static final Block CAVE_MOSS_PATH = registerPath("cave_moss_path", CAVE_MOSS);
    public static final Block CRYSTAL_MOSS_PATH = registerPath("crystal_moss_path", CRYSTAL_MOSS);
    public static final Block SHADOW_GRASS_PATH = registerPath("shadow_grass_path", SHADOW_GRASS);
    public static final Block PINK_MOSS_PATH = registerPath("pink_moss_path", PINK_MOSS);
    public static final Block AMBER_MOSS_PATH = registerPath("amber_moss_path", AMBER_MOSS);
    public static final Block JUNGLE_MOSS_PATH = registerPath("jungle_moss_path", JUNGLE_MOSS);
    public static final Block SANGNUM_PATH = registerPath("sangnum_path", SANGNUM);
    public static final Block RUTISCUS_PATH = registerPath("rutiscus_path", RUTISCUS);

    public static final Block MOSSY_OBSIDIAN = defineBlock("mossy_obsidian", MossyObsidian::new)
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch(Blocks.OBSIDIAN))
            .addTrait(BlockTraits.OBSIDIAN_BLOCK)
            .randomTicks()
            .destroyTime(3)
            .buildAndRegister();

    public static final Block DRAGON_BONE_BLOCK = defineBlock("dragon_bone_block", RotatedPillarBlock::new)
            .replacePropertiesWithCopy(Blocks.BONE_BLOCK)
            .addTags(EndTags.BONEMEAL_TARGET_DRAGON_BONE)
            .addTrait(ModelTraitLibrary.pillar())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block DRAGON_BONE_STAIRS = defineBlock(
            "dragon_bone_stairs",
            props -> new StairBlock(DRAGON_BONE_BLOCK.defaultBlockState(), props)
    )
            .addTrait(BlockTraits.STAIR_BLOCK)
            .addTrait(ModelTraitLibrary.stairs(() -> DRAGON_BONE_BLOCK))
            .addTrait(RecipeTraitLibrary.stairs(RecipeMaterial.of(DRAGON_BONE_BLOCK)))
            .buildAndRegister();

    public static final Block DRAGON_BONE_SLAB = defineBlock("dragon_bone_slab", SlabBlock::new)
            .addTrait(BlockTraits.SLAB_BLOCK)
            .addTrait(ModelTraitLibrary.slab(() -> DRAGON_BONE_BLOCK))
            .addTrait(RecipeTraitLibrary.slab(RecipeMaterial.of(DRAGON_BONE_BLOCK)))
            .buildAndRegister();

    public static final Block MOSSY_DRAGON_BONE = defineBlock("mossy_dragon_bone", MossyDragonBoneBlock::new)
            .replacePropertiesWithCopy(DRAGON_BONE_BLOCK)
            .addTags(EndTags.BONEMEAL_SOURCE_DRAGON_BONE)
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch(DRAGON_BONE_BLOCK))
            .addTrait(BlockTraits.STONE_BLOCK)
            .randomTicks()
            .destroyTime(0.5f)
            .buildAndRegister();


    // Rocks //
    public static final StoneMaterial FLAVOLITE = new StoneMaterial("flavolite", MapColor.SAND);
    public static final StoneMaterial VIOLECITE = new StoneMaterial("violecite", MapColor.COLOR_PURPLE);
    public static final StoneMaterial SULPHURIC_ROCK = new StoneMaterial("sulphuric_rock", MapColor.COLOR_BROWN);
    public static final StoneMaterial VIRID_JADESTONE = new StoneMaterial("virid_jadestone", MapColor.COLOR_GREEN);
    public static final StoneMaterial AZURE_JADESTONE = new StoneMaterial(
            "azure_jadestone",
            MapColor.COLOR_LIGHT_BLUE
    );
    public static final StoneMaterial SANDY_JADESTONE = new StoneMaterial(
            "sandy_jadestone",
            MapColor.COLOR_YELLOW
    );
    public static final StoneMaterial UMBRALITH = new StoneMaterial("umbralith", MapColor.DEEPSLATE);
    public static final Block BRIMSTONE = defineBlock("brimstone", BrimstoneBlock::new)
            .replacePropertiesWithCopy(Blocks.END_STONE)
            .addTrait(BlockTraits.STONE_BLOCK)
            .mapColor(MapColor.COLOR_BROWN)
            .randomTicks()
            .buildAndRegister();

    public static final Block SULPHUR_CRYSTAL = defineBlock("sulphur_crystal", SulphurCrystalBlock::new)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> SulphurCrystalBlock.buildLoot(block, provider)
            ))
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
            .noCollission()
            .buildAndRegister();

    public static final Block MISSING_TILE = defineBlock("missing_tile", Block::new)
            .replacePropertiesWithCopy(Blocks.END_STONE)
            .addTrait(BlockTraits.STONE_BLOCK)
            .buildAndRegister();

    // Vanilla Stone Sets
    public static final VanillaStoneSet END_STONE_SET = new VanillaStoneSet(
            "end_stone", Blocks.END_STONE,
            SlotMap.of(FlowerPot.SLOT, StoneLantern.SLOT, Furnace.SLOT)
    );

    public static final VanillaStoneSet ANDESITE_SET = new VanillaStoneSet(
            "andesite", Blocks.ANDESITE,
            SlotMap.of(Pedestal.SLOT, StoneLantern.SLOT)
    );

    public static final VanillaStoneSet DIORITE_SET = new VanillaStoneSet(
            "diorite", Blocks.DIORITE,
            SlotMap.of(Pedestal.SLOT, StoneLantern.SLOT)
    );

    public static final VanillaStoneSet GRANITE_SET = new VanillaStoneSet(
            "granite", Blocks.GRANITE,
            SlotMap.of(Pedestal.SLOT, StoneLantern.SLOT)
    );

    public static final VanillaStoneSet QUARTZ_SET = new VanillaStoneSet(
            "quartz", Blocks.QUARTZ_BLOCK,
            SlotMap.of(Pedestal.SLOT, StoneLantern.SLOT)
    );

    public static final VanillaStoneSet PURPUR_SET = new VanillaStoneSet(
            "purple", Blocks.PURPUR_BLOCK,
            SlotMap.of(Pedestal.SLOT, StoneLantern.SLOT)
    );

    public static final VanillaStoneSet BLACKSTONE_SET = new VanillaStoneSet(
            "blackstone", Blocks.BLACKSTONE,
            SlotMap.of(StoneLantern.SLOT)
    );

    // Vanilla Metal Sets
    public static final VanillaMetalSet IRON_SET = new VanillaMetalSet(
            "iron", Blocks.IRON_BLOCK, Items.IRON_INGOT,
            SlotMap.of(Chandelier.SLOT, BulbLantern.SLOT)
    );
    public static final VanillaMetalSet GOLD_SET = new VanillaMetalSet(
            "gold", Blocks.GOLD_BLOCK, Items.GOLD_INGOT,
            SlotMap.of(Chandelier.SLOT)
    );

    public static final Block FLAVOLITE_RUNED = defineBlock("flavolite_runed", RunedFlavolite::new)
            .replacePropertiesWithCopy(EndBlocks.FLAVOLITE.getBlock(SlotType.POLISHED))
            .strength(1, Blocks.OBSIDIAN.getExplosionResistance())
            .lightLevel(state -> state.getValue(RunedFlavolite.ACTIVATED) ? 8 : 0)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block FLAVOLITE_RUNED_ETERNAL = defineBlock("flavolite_runed_eternal", RunedFlavolite::new)
            .replacePropertiesWithCopy(EndBlocks.FLAVOLITE.getBlock(SlotType.POLISHED))
            .strength(-11, Blocks.BEDROCK.getExplosionResistance())
            .lightLevel(state -> state.getValue(RunedFlavolite.ACTIVATED) ? 8 : 0)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block HYDROTHERMAL_VENT = defineBlock("hydrothermal_vent", HydrothermalVentBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .sound(SoundType.STONE)
            .noCollission()
            .requiresCorrectToolForDrops()
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block VENT_BUBBLE_COLUMN = defineBlockOnly("vent_bubble_column", VentBubbleColumnBlock::new)
            .replacePropertiesWithCopy(Blocks.BUBBLE_COLUMN)
            .noOcclusion().noCollission().noLootTable()
            .buildAndRegister();

    public static final Block DENSE_SNOW = defineBlock("dense_snow", Block::new)
            .addTrait(SnowBlockTrait.withDefault())
            .buildAndRegister();

    public static final Block EMERALD_ICE = defineBlock("emerald_ice", EmeraldIceBlock::new)
            .addTrait(IceBlockTrait.withBase(Blocks.ICE))
            .randomTicks()
            .buildAndRegister();

    public static final Block DENSE_EMERALD_ICE = defineBlock("dense_emerald_ice", Block::new)
            .addTrait(IceBlockTrait.withBase(Blocks.PACKED_ICE))
            .buildAndRegister();

    public static final Block ANCIENT_EMERALD_ICE = defineBlock("ancient_emerald_ice", AncientEmeraldIceBlock::new)
            .addTrait(IceBlockTrait.withBase(Blocks.BLUE_ICE))
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch())
            .randomTicks()
            .buildAndRegister();
    ;

    public static final Block END_STONE_STALACTITE = defineBlock("end_stone_stalactite", StalactiteBlock::new)
            .addTrait(StalactiteBlockTrait.withSource(Blocks.END_STONE))
            .buildAndRegister();

    public static final Block END_STONE_STALACTITE_CAVEMOSS = defineBlock(
            "end_stone_stalactite_cavemoss",
            StalactiteBlock::new
    ).addTrait(StalactiteBlockTrait.withSource(CAVE_MOSS))
     .buildAndRegister();

    // Wooden Materials And Trees //
    public static final Block MOSSY_GLOWSHROOM_SAPLING = defineBlock(
            "mossy_glowshroom_sapling",
            MossyGlowshroomSaplingBlock::new
    ).addTrait(SaplingBlockTrait.withLight(7))
     .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
     .buildAndRegister();

    public static final Block MOSSY_GLOWSHROOM_CAP = defineBlock("mossy_glowshroom_cap", MossyGlowshroomCapBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block MOSSY_GLOWSHROOM_HYMENOPHORE = defineBlock(
            "mossy_glowshroom_hymenophore",
            GlowingHymenophoreBlock::new
    ).addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
     .addTrait(ClientBlockTraits.MODEL.with(
             (key, block, generator) ->
                     GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
     ))
     .addTrait(BlockTraits.LOOT_TABLE)
     .lightLevel((_s) -> 15)
     .sound(SoundType.WART_BLOCK)
     .buildAndRegister();

    public static final Block MOSSY_GLOWSHROOM_FUR = defineBlock("mossy_glowshroom_fur", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_LIGHT_BLUE, 15, true, 16, MOSSY_GLOWSHROOM_SAPLING))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial MOSSY_GLOWSHROOM = new EndWoodenComplexMaterial(
            "mossy_glowshroom",
            MapColor.COLOR_GRAY,
            MapColor.WOOD
    ).buildAndRegister();

    public static final Block PYTHADENDRON_SAPLING = defineBlock("pythadendron_sapling", PythadendronSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_PURPLE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .buildAndRegister();

    public static final Block PYTHADENDRON_LEAVES = defineBlock("pythadendron_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, PYTHADENDRON_SAPLING))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial PYTHADENDRON = new EndWoodenComplexMaterial(
            "pythadendron",
            MapColor.COLOR_MAGENTA,
            MapColor.COLOR_PURPLE
    ).buildAndRegister();

    public static final Block END_LOTUS_SEED = defineBlock("end_lotus_seed", EndLotusSeedBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.COLOR_CYAN))
            .buildAndRegister();

    public static final Block END_LOTUS_STEM = defineBlock("end_lotus_stem", EndLotusStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // These blocks have no item (defineBlockOnly), so they can't use compostableWithColor's
    // default dropSelf() loot table (block.asItem() would be air) - build the same trait bundle
    // without a loot table trait instead, so they simply drop nothing when broken.
    public static final Block END_LOTUS_LEAF = defineBlockOnly("end_lotus_leaf", EndLotusLeafBlock::new)
            .addTrait(PlantBlockTrait.withColor(MapColor.COLOR_PINK, true))
            .addTrait(CompostableBlockTrait.withDefault())
            .addTrait(BlockTraits.FLAMMABLE.withDefault())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .buildAndRegister();

    public static final Block END_LOTUS_FLOWER = defineBlockOnly("end_lotus_flower", EndLotusFlowerBlock::new)
            .addTrait(PlantBlockTrait.withColor(MapColor.COLOR_PINK, true))
            .addTrait(CompostableBlockTrait.withDefault())
            .addTrait(BlockTraits.FLAMMABLE.withDefault())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .lightLevel((bs) -> 15)
            .buildAndRegister();

    public static final EndWoodenComplexMaterial END_LOTUS = new EndWoodenComplexMaterial(
            "end_lotus",
            MapColor.COLOR_LIGHT_BLUE,
            MapColor.COLOR_CYAN
    ).buildAndRegister();

    public static final Block LACUGROVE_SAPLING = defineBlock("lacugrove_sapling", LacugroveSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_CYAN))
            .buildAndRegister();

    public static final Block LACUGROVE_LEAVES = defineBlock("lacugrove_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_CYAN, 0, false, LACUGROVE_SAPLING))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial LACUGROVE = new EndWoodenComplexMaterial(
            "lacugrove",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_YELLOW
    ).buildAndRegister();

    public static final Block DRAGON_TREE_SAPLING = defineBlock("dragon_tree_sapling", DragonTreeSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_MAGENTA))
            .buildAndRegister();

    public static final Block DRAGON_TREE_LEAVES = defineBlock("dragon_tree_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, DRAGON_TREE_SAPLING))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial DRAGON_TREE = new EndWoodenComplexMaterial(
            "dragon_tree",
            MapColor.COLOR_BLACK,
            MapColor.COLOR_MAGENTA
    ).buildAndRegister();

    public static final Block TENANEA_SAPLING = defineBlock("tenanea_sapling", TenaneaSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_PINK))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_PINK_MOSS))
            .buildAndRegister();

    public static final Block TENANEA_LEAVES = defineBlock("tenanea_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_PINK, 0, false, TENANEA_SAPLING))
            .buildAndRegister();

    public static final Block TENANEA_FLOWERS = defineBlock("tenanea_flowers", TenaneaFlowersBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.COLOR_PINK, 15))
            .buildAndRegister();

    public static final Block TENANEA_OUTER_LEAVES = defineBlock("tenanea_outer_leaves", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_PINK, 32, TENANEA_SAPLING))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial TENANEA = new EndWoodenComplexMaterial(
            "tenanea",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_PINK
    ).buildAndRegister();

    public static final Block HELIX_TREE_SAPLING = defineBlock("helix_tree_sapling", HelixTreeSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_ORANGE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .buildAndRegister();

    public static final Block HELIX_TREE_LEAVES = defineBlock("helix_tree_leaves", HelixTreeLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 8, HELIX_TREE_SAPLING))
            .sound(SoundType.WART_BLOCK)
            .buildAndRegister();

    public static final EndWoodenComplexMaterial HELIX_TREE = new EndWoodenComplexMaterial(
            "helix_tree",
            MapColor.COLOR_GRAY,
            MapColor.COLOR_ORANGE
    ).buildAndRegister();

    public static final Block UMBRELLA_TREE_SAPLING = defineBlock(
            "umbrella_tree_sapling",
            UmbrellaTreeSaplingBlock::new
    ).addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_BLUE))
     .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS))
     .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
     .buildAndRegister();

    public static final Block UMBRELLA_TREE_MEMBRANE = defineBlock(
            "umbrella_tree_membrane",
            UmbrellaTreeMembraneBlock::new
    ).replacePropertiesWithCopy(Blocks.SLIME_BLOCK)
     .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_BLUE, 8, UMBRELLA_TREE_SAPLING))
     .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
     .buildAndRegister();

    public static final Block UMBRELLA_TREE_CLUSTER = defineBlock(
            "umbrella_tree_cluster",
            UmbrellaTreeClusterBlock::new
    ).replacePropertiesWithCopy(Blocks.NETHER_WART_BLOCK)
     .addTrait(BlockTraits.WOOD_BLOCK)
     .addTrait(BlockTraits.LOOT_TABLE)
     .mapColor(MapColor.COLOR_PURPLE)
     .lightLevel((bs) -> 15)
     .buildAndRegister();

    public static final Block UMBRELLA_TREE_CLUSTER_EMPTY = defineBlock(
            "umbrella_tree_cluster_empty",
            UmbrellaTreeClusterEmptyBlock::new
    ).replacePropertiesWithCopy(Blocks.NETHER_WART_BLOCK)
     .addTrait(BlockTraits.WOOD_BLOCK)
     .addTrait(BlockTraits.LOOT_TABLE)
     .mapColor(MapColor.COLOR_PURPLE)
     .randomTicks()
     .buildAndRegister();

    public static final EndWoodenComplexMaterial UMBRELLA_TREE = new EndWoodenComplexMaterial(
            "umbrella_tree",
            MapColor.COLOR_BLUE,
            MapColor.COLOR_GREEN
    ).buildAndRegister();

    public static final Block JELLYSHROOM_CAP_PURPLE = defineBlock(
            "jellyshroom_cap_purple",
            p -> new JellyshroomCapBlock(
                    p,
                    217, 142, 255,
                    164, 0, 255
            )
    ).replacePropertiesWithCopy(Blocks.SLIME_BLOCK)
     .mapColor(MapColor.COLOR_PURPLE)
     .addTrait(BlockTraits.LOOT_TABLE)
     .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
     .addTrait(ClientBlockTraits.MODEL.with(
             (key, block, generator) -> JellyshroomCapBlock.provideBlockModel(generator, block)
     ))
     .buildAndRegister();

    public static final EndWoodenComplexMaterial JELLYSHROOM = new EndWoodenComplexMaterial(
            "jellyshroom",
            MapColor.COLOR_PURPLE,
            MapColor.COLOR_LIGHT_BLUE
    ).buildAndRegister();

    public static final Block LUCERNIA_SAPLING = defineBlock("lucernia_sapling", LucerniaSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_ORANGE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .buildAndRegister();

    public static final Block LUCERNIA_LEAVES = defineBlock("lucernia_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 0, false, LUCERNIA_SAPLING))
            .buildAndRegister();

    public static final Block LUCERNIA_OUTER_LEAVES = defineBlock("lucernia_outer_leaves", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_RED, 32, LUCERNIA_SAPLING))
            .buildAndRegister();
    public static final EndWoodenComplexMaterial LUCERNIA = new EndWoodenComplexMaterial(
            "lucernia",
            MapColor.COLOR_ORANGE,
            MapColor.COLOR_ORANGE
    ).buildAndRegister();

    public static final EndWoodenComplexMaterial LUCERNIA_JELLY = new JellyLucerniaWoodMaterial().buildAndRegister();

    // Small Plants //
    public static final Block UMBRELLA_MOSS = defineBlock("umbrella_moss", UmbrellaMossBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, true))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .lightLevel(state -> 11)
            .buildAndRegister();

    public static final Block UMBRELLA_MOSS_TALL = defineBlock("umbrella_moss_tall", UmbrellaMossTallBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, true))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block CREEPING_MOSS = defineBlock("creeping_moss", GlowingMossBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, true))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .lightLevel(state -> 11)
            .buildAndRegister();
    public static final Block CHORUS_GRASS = defineBlock("chorus_grass", ChorusGrassBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block CAVE_GRASS = defineBlock("cave_grass", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(CAVE_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block CRYSTAL_GRASS = defineBlock("crystal_grass", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(CRYSTAL_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block SHADOW_PLANT = defineBlock("shadow_plant", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(SHADOW_GRASS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block BUSHY_GRASS = defineBlock("bushy_grass", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(PINK_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block AMBER_GRASS = defineBlock("amber_grass", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(AMBER_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();
    public static final Block TWISTED_UMBRELLA_MOSS = defineBlock(
            "twisted_umbrella_moss",
            TwistedUmbrellaMossBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block TWISTED_UMBRELLA_MOSS_TALL = defineBlock(
            "twisted_umbrella_moss_tall",
            TwistedUmbrellaMossTallBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> generator.createCubeModelWithFlatItem(
                            block, BetterEnd.C.mk("item/twisted_umbrella_moss_large")
                    )
            ))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block JUNGLE_GRASS = defineBlock("jungle_grass", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(JUNGLE_MOSS))
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block BLOOMING_COOKSONIA = defineBlock("blooming_cooksonia", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block SALTEAGO = defineBlock("salteago", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block VAIOLUSH_FERN = defineBlock("vaiolush_fern", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block FRACTURN = defineBlock("fracturn", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();
    public static final Block CLAWFERN = defineBlock("clawfern", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(SANGNUM, MOSSY_OBSIDIAN, MOSSY_DRAGON_BONE))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block GLOBULAGUS = defineBlock("globulagus", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(SANGNUM, MOSSY_OBSIDIAN, MOSSY_DRAGON_BONE))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block ORANGO = defineBlock("orango", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block AERIDIUM = defineBlock("aeridium", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block LUTEBUS = defineBlock("lutebus", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block LAMELLARIUM = defineBlock("lamellarium", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block INFLEXIA = defineBlock("inflexia", TerrainPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(PALLIDIUM_FULL, PALLIDIUM_HEAVY, PALLIDIUM_THIN, PALLIDIUM_TINY))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();
    public static final Block FLAMMALIX = defineBlock("flammalix", FlammalixBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_PALLIDIUM))
            .lightLevel(state -> 12)
            .buildAndRegister();


    public static final MultifaceSpreadeableBlock CRYSTAL_MOSS_COVER = (MultifaceSpreadeableBlock) defineBlock(
            "crystal_moss_cover",
            CrystalMossCoverBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .lightLevel(GlowLichenBlock.emission(7))
            .buildAndRegister();

    public static final Block BLUE_VINE_SEED = defineBlock("blue_vine_seed", BlueVineSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, true))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .buildAndRegister();

    public static final Block BLUE_VINE = defineBlock("blue_vine", BlueVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.COLOR_BLUE, 15))
            .buildAndRegister();

    public static final Block BLUE_VINE_LANTERN = defineBlock("blue_vine_lantern", BlueVineLanternBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK.withDefault())
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> BlueVineLanternBlock.provideBlockModel(generator, block)
            ))
            .lightLevel(state -> 15)
            .sound(SoundType.WART_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block BLUE_VINE_FUR = defineBlock("blue_vine_fur", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_BLUE, 15, BLUE_VINE_SEED))
            .buildAndRegister();

    public static final Block LANCELEAF_SEED = defineBlock("lanceleaf_seed", LanceleafSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .buildAndRegister();

    public static final Block LANCELEAF = defineBlock("lanceleaf", LanceleafBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, false, false))
            .offsetType(OffsetType.XZ)
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_SEED = defineBlock("glowing_pillar_seed", GlowingPillarSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .lightLevel(state -> state.getValue(EndPlantWithAgeBlock.AGE) * 3 + 3)
            .sound(SoundType.GRASS)
            .offsetType(OffsetType.XZ)
            .randomTicks()
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_ROOTS = defineBlock(
            "glowing_pillar_roots",
            GlowingPillarRootsBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .lightLevel(state -> 15)
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_LUMINOPHOR = defineBlock(
            "glowing_pillar_luminophor",
            GlowingPillarLuminophorBlock::new
    )
            .addTrait(BlockTraits.METAL_BLOCK)
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) ->
                            GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
            ))
            .mapColor(MapColor.COLOR_ORANGE)
            .strength(0.2F)
            .lightLevel(state -> 15)
            .sound(SoundType.GRASS)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_LEAVES = defineBlock("glowing_pillar_leaves", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 15, GLOWING_PILLAR_SEED))
            .buildAndRegister();

    public static final Block SMALL_JELLYSHROOM = defineBlock("small_jellyshroom", SmallJellyshroomBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch())
            .lightLevel(s -> 13)
            .sound(SoundType.NETHER_WART)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .buildAndRegister();
    public static final Block BOLUX_MUSHROOM = defineBlock("bolux_mushroom", BoluxMushroomBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .lightLevel(bs -> 10)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .buildAndRegister();

    public static final Block LUMECORN_SEED = defineBlock("lumecorn_seed", LumecornSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_MOSS))
            .buildAndRegister();

    public static final Block LUMECORN = defineBlock("lumecorn", LumecornBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .strength(0.5F, 0.5F)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block SMALL_AMARANITA_MUSHROOM = defineBlock(
            "small_amaranita_mushroom",
            SmallAmaranitaBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_BONE))
            .buildAndRegister();
    public static final Block LARGE_AMARANITA_MUSHROOM = defineBlock(
            "large_amaranita_mushroom",
            LargeAmaranitaBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, true, false))
            .lightLevel(state -> state.getValue(BlockProperties.TRIPLE_SHAPE) == BlockProperties.TripleShape.TOP
                    ? 15
                    : 0)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_BONE))
            .buildAndRegister();
    public static final Block AMARANITA_STEM = defineBlock("amaranita_stem", AmaranitaStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .buildAndRegister();
    public static final Block AMARANITA_HYPHAE = defineBlock("amaranita_hyphae", AmaranitaStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .buildAndRegister();
    public static final Block AMARANITA_HYMENOPHORE = defineBlock(
            "amaranita_hymenophore",
            AmaranitaHymenophoreBlock::new
    )
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block AMARANITA_LANTERN = defineBlock("amaranita_lantern", GlowingHymenophoreBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) ->
                            GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
            ))
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block AMARANITA_FUR = defineBlock("amaranita_fur", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_CYAN, 15, SMALL_AMARANITA_MUSHROOM))
            .buildAndRegister();
    public static final Block AMARANITA_CAP = defineBlock("amaranita_cap", AmaranitaCapBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block NEON_CACTUS = defineBlock("neon_cactus", NeonCactusPlantBlock::new)
            .lightLevel(bs -> 15)
            .randomTicks()
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block NEON_CACTUS_BLOCK = defineBlock("neon_cactus_block", NeonCactusBlock::new)
            .lightLevel(bs -> 15)
            .buildAndRegister();
    public static final Block NEON_CACTUS_BLOCK_STAIRS = defineBlock(
            "neon_cactus_stairs",
            props -> new StairBlock(NEON_CACTUS_BLOCK.defaultBlockState(), props)
    )
            .addTrait(BlockTraits.STAIR_BLOCK)
            .addTrait(ModelTraitLibrary.stairs(() -> NEON_CACTUS_BLOCK))
            .addTrait(RecipeTraitLibrary.stairs(RecipeMaterial.of(NEON_CACTUS_BLOCK)))
            .buildAndRegister();
    public static final Block NEON_CACTUS_BLOCK_SLAB = defineBlock("neon_cactus_slab", SlabBlock::new)
            .addTrait(BlockTraits.SLAB_BLOCK)
            .addTrait(ModelTraitLibrary.slab(() -> NEON_CACTUS_BLOCK))
            .addTrait(RecipeTraitLibrary.slab(RecipeMaterial.of(NEON_CACTUS_BLOCK)))
            .buildAndRegister();
    ;

    // TERMINITE needs to be defined before any block whose constructor references EndItems
    // (e.g. SHADOW_BERRY below), since that would trigger EndItems.<clinit> -> AeterniumSet,
    // which reads EndBlocks.TERMINITE - and it must already be assigned by then.
    public static final MetalMaterial TERMINITE = MetalMaterial.makeOreless(
            "terminite",
            MapColor.WARPED_WART_BLOCK,
            7F,
            9F,
            EndToolTier.TERMINITE,
            EndArmorTier.TERMINITE,
            EndTags.ANVIL_DIAMOND_TOOL,
            () -> EndTemplates.TERMINITE_UPGRADE
    );

    // Crops
    public static final Block SHADOW_BERRY = defineBlock("shadow_berry", ShadowBerryBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .buildAndRegister();
    public static final Block BLOSSOM_BERRY = defineBlock(
            "blossom_berry_seed",
            p -> new PottableCropBlock(p, EndItems.BLOSSOM_BERRY, PINK_MOSS)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .buildAndRegister();
    public static final Block AMBER_ROOT = defineBlock(
            "amber_root_seed",
            p -> new PottableCropBlock(p, EndItems.AMBER_ROOT_RAW, AMBER_MOSS)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .buildAndRegister();
    public static final Block CHORUS_MUSHROOM = defineBlock(
            "chorus_mushroom_seed",
            p -> new PottableCropBlock(p, EndItems.CHORUS_MUSHROOM_RAW, CHORUS_NYLIUM)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .buildAndRegister();
    //public static final Block PEARLBERRY = registerBlock("pearlberry_seed", new PottableCropBlock(EndItems.BLOSSOM_BERRY, END_MOSS, END_MYCELIUM));
    public static final Block CAVE_PUMPKIN_SEED = defineBlock("cave_pumpkin_seed", CavePumpkinVineBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_ORANGE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block CAVE_PUMPKIN = defineBlock("cave_pumpkin", CavePumpkinBlock::new)
            .lightLevel(state -> state.getValue(BlockProperties.SMALL) ? 10 : 15)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Water plants
    public static final Block BUBBLE_CORAL = defineBlock("bubble_coral", BubbleCoralBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .sound(SoundType.CORAL_BLOCK)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block MENGER_SPONGE = defineBlock("menger_sponge", MengerSpongeBlock::new)
            .noOcclusion()
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block MENGER_SPONGE_WET = defineBlock("menger_sponge_wet", MengerSpongeWetBlock::new)
            .noOcclusion()
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block CHARNIA_RED = defineBlock("charnia_red", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .buildAndRegister();
    public static final Block CHARNIA_PURPLE = defineBlock("charnia_purple", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .buildAndRegister();
    public static final Block CHARNIA_ORANGE = defineBlock("charnia_orange", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .buildAndRegister();
    public static final Block CHARNIA_LIGHT_BLUE = defineBlock("charnia_light_blue", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .buildAndRegister();
    public static final Block CHARNIA_CYAN = defineBlock("charnia_cyan", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_CYAN, false, false))
            .buildAndRegister();
    public static final Block CHARNIA_GREEN = defineBlock("charnia_green", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_GREEN, false, false))
            .buildAndRegister();

    public static final Block END_LILY = defineBlock("end_lily", EndLilyBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.WATER, false, false))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .buildAndRegister();
    public static final Block END_LILY_SEED = defineBlock("end_lily_seed", EndLilySeedBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.WATER))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();

    public static final Block HYDRALUX_SAPLING = defineBlock("hydralux_sapling", HydraluxSaplingBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.COLOR_LIGHT_BLUE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SULPHURIC_ROCK))
            .buildAndRegister();
    public static final Block HYDRALUX = defineBlock("hydralux", HydraluxBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .buildAndRegister();
    public static final Block HYDRALUX_PETAL_BLOCK = defineBlock("hydralux_petal_block", HydraluxPetalBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PODZOL, true, false))
            .strength(1.0f, 1.0f)
            .sound(SoundType.WART_BLOCK)
            .buildAndRegister();
    public static final ColoredMaterial HYDRALUX_PETAL_BLOCK_COLORED = new ColoredMaterial(
            HydraluxPetalColoredBlock::new,
            HYDRALUX_PETAL_BLOCK,
            true,
            (def) -> def.addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> HydraluxPetalColoredBlock.provideBlockModel(generator, block)
            ))
    );

    public static final Block POND_ANEMONE = defineBlock("pond_anemone", PondAnemoneBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_MAGENTA, false, false))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> generator.createCubeModelWithFlatItem(block)
            ))
            .sound(SoundType.CORAL_BLOCK)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .lightLevel(state -> 13)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();

    public static final Block FLAMAEA = defineBlock("flamaea", FlamaeaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .sound(SoundType.WET_GRASS)
            .buildAndRegister();

    public static final Block CAVE_BUSH = defineBlock("cave_bush", SimpleLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, 0.0F, null))
            .buildAndRegister();

    public static final Block MURKWEED = defineBlock("murkweed", MurkweedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .buildAndRegister();
    public static final Block NEEDLEGRASS = defineBlock("needlegrass", NeedlegrassBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> NeedlegrassBlock.buildLoot(block, provider)
            ))
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .buildAndRegister();

    // Wall Plants //
    public static final Block PURPLE_POLYPORE = defineBlock("purple_polypore", EndWallMushroom::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> generator.createCubeModelWithFlatItem(block)
            ))
            .lightLevel(s -> 13)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block AURANT_POLYPORE = defineBlock("aurant_polypore", EndWallMushroom::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> generator.createCubeModelWithFlatItem(block)
            ))
            .lightLevel(s -> 13)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block TAIL_MOSS = defineBlock("tail_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block CYAN_MOSS = defineBlock("cyan_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_CYAN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block TWISTED_MOSS = defineBlock("twisted_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block TUBE_WORM = defineBlock("tube_worm", EndUnderwaterWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block BULB_MOSS = defineBlock("bulb_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_ORANGE, false, false))
            .lightLevel(bs -> 12)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block JUNGLE_FERN = defineBlock("jungle_fern", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_GREEN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block RUSCUS = defineBlock("ruscus", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();

    // Vines //
    public static final Block DENSE_VINE = defineBlock("dense_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15))
            .buildAndRegister();

    public static final Block TWISTED_VINE = defineBlock("twisted_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT))
            .buildAndRegister();

    public static final Block BULB_VINE_SEED = defineBlock("bulb_vine_seed", BulbVineSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE_OR_TREES))
            .buildAndRegister();

    public static final Block BULB_VINE = defineBlock("bulb_vine", BulbVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15))
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> BulbVineBlock.buildLoot(block, provider)
            ))
            .buildAndRegister();

    public static final Block JUNGLE_VINE = defineBlock("jungle_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT))
            .buildAndRegister();

    public static final Block RUBINEA = defineBlock("rubinea", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT))
            .buildAndRegister();

    public static final Block MAGNULA = defineBlock("magnula", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT))
            .buildAndRegister();

    public static final Block FILALUX = defineBlock("filalux", FilaluxBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15))
            .offsetType(OffsetType.NONE)
            .buildAndRegister();

    public static final Block FILALUX_WINGS = defineBlock("filalux_wings", FilaluxWingsBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .sound(SoundType.WET_GRASS)
            .buildAndRegister();

    public static final Block FILALUX_LANTERN = defineBlock("filalux_lantern", FilaluxLanternBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .lightLevel(state -> 15)
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Mob-Related
    public static final Block SILK_MOTH_NEST = defineBlock("silk_moth_nest", SilkMothNestBlock::new)
            .strength(0.5F, 0.1F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .randomTicks()
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block SILK_MOTH_HIVE = defineBlock("silk_moth_hive", SilkMothHiveBlock::new)
            .strength(0.5F, 0.1F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .randomTicks()
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Ores //
    public static final Block ENDER_ORE = defineBlock(
            "ender_ore",
            p -> new BaseOreBlock(p, () -> EndItems.ENDER_SHARD, 1, 3, 5)
    )
            .addTrait(BlockTraits.STONE_BLOCK)
            .buildAndRegister();
    public static final Block AMBER_ORE = defineBlock(
            "amber_ore",
            p -> new BaseOreBlock(p, () -> EndItems.RAW_AMBER, 1, 2, 4)
    )
            .addTrait(BlockTraits.STONE_BLOCK)
            .buildAndRegister();

    // Materials //
    public static final MetalMaterial THALLASIUM = MetalMaterial.makeNormal(
            "thallasium",
            MapColor.COLOR_BLUE,
            EndToolTier.THALLASIUM,
            EndArmorTier.THALLASIUM,
            EndTags.ANVIL_IRON_TOOL,
            () -> EndTemplates.THALLASIUM_UPGRADE
    );

    public static final Block AETERNIUM_BLOCK = defineBlock("aeternium_block", AeterniumBlock::new)
            .addTrait(BlockTraits.METAL_BLOCK)
            .mapColor(MapColor.COLOR_GRAY)
            .strength(65F, 1200F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.NETHERITE_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block CHARCOAL_BLOCK = defineBlock("charcoal_block", CharcoalBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block ENDER_BLOCK = defineBlock("ender_block", EnderBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .mapColor(MapColor.WARPED_WART_BLOCK)
            .strength(5F, 6F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block AURORA_CRYSTAL = defineBlock("aurora_crystal", AuroraCrystalBlock::new)
            .strength(1F)
            .lightLevel(bs -> 15)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHammer())
            .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) ->
                            provider.dropOre(block, EndItems.CRYSTAL_SHARDS, UniformGenerator.between(1, 4))
            ))
            .buildAndRegister();
    public static final Block AMBER_BLOCK = defineBlock("amber_block", AmberBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block SMARAGDANT_CRYSTAL_SHARD = defineBlock(
            "smaragdant_crystal_shard",
            SmaragdantCrystalShardBlock::new
    )
            .mapColor(MapColor.COLOR_GREEN)
            .lightLevel(bs -> 15)
            .sound(SoundType.AMETHYST_CLUSTER)
            .requiresCorrectToolForDrops()
            .noCollission()
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block SMARAGDANT_CRYSTAL = defineBlock("smaragdant_crystal", SmaragdantCrystalBlock::new)
            .lightLevel(bs -> 15)
            .strength(1F)
            .noOcclusion()
            .sound(SoundType.AMETHYST)
            .buildAndRegister();
    public static final CrystalSubblocksMaterial SMARAGDANT_SUBBLOCKS = new CrystalSubblocksMaterial(
            "smaragdant_crystal",
            SMARAGDANT_CRYSTAL
    );
    public static final Block BUDDING_SMARAGDANT_CRYSTAL = defineBlock(
            "budding_smaragdant_crystal",
            BuddingSmaragdantCrystalBlock::new
    )
            .lightLevel(bs -> 15)
            .strength(1F, 1F)
            .noOcclusion()
            .sound(SoundType.AMETHYST)
            .randomTicks()
            .pushReaction(PushReaction.DESTROY)
            .addTags(CommonBlockTags.BUDDING_BLOCKS)
            .buildAndRegister();

    public static final Block RESPAWN_OBELISK = defineBlock("respawn_obelisk", RespawnObeliskBlock::new)
            .lightLevel(
                    state -> state.getValue(BlockProperties.TRIPLE_SHAPE) == BlockProperties.TripleShape.BOTTOM ? 0 : 15
            )
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Lanterns
    public static final ColoredMaterial IRON_BULB_LANTERN_COLORED = new ColoredMaterial(
            BulbVineLanternColoredBlock::new,
            IRON_SET.getBlock(MetalMaterial.BULB_LANTERN),
            false
    );


    // Blocks With Entity //
    public static final Block END_STONE_SMELTER = defineBlock("end_stone_smelter", EndStoneSmelter::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .buildAndRegister();
    public static final Block ETERNAL_PEDESTAL = getBlockRegistry()
            .defineDefaultBlock("eternal_pedestal", def -> new EternalPedestal(def.blockKey))
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> ((EternalPedestal) block).provideBlockModelsInstance(generator)
            ))
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block INFUSION_PEDESTAL = getBlockRegistry()
            .defineDefaultBlock("infusion_pedestal", def -> new InfusionPedestal(def.blockKey))
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> ((InfusionPedestal) block).provideBlockModelsInstance(generator)
            ))
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block AETERNIUM_ANVIL = defineBlock("aeternium_anvil", AeterniumAnvil::new)
            .addTrait(BlockTraits.METAL_BLOCK)
            .buildAndRegister();

    // Technical
    public static final Block END_PORTAL_BLOCK = defineBlock("end_portal_block", EndPortalBlock::new)
            .lightLevel(bs -> 15)
            .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
            .buildAndRegister();

    // Variations
    public static final VanillaVariantStoneMaterial END_STONE_BRICK_VARIATIONS =
            new VanillaVariantStoneMaterial(
                    "end_stone_brick",
                    Blocks.END_STONE_BRICKS,
                    MapColor.SAND
            );

    public static final Block END_STONE_SLAB = defineBlock("end_stone_slab", SlabBlock::new)
            .addTrait(BlockTraits.SLAB_BLOCK)
            .addTrait(ModelTraitLibrary.slab(() -> Blocks.END_STONE))
            .addTrait(RecipeTraitLibrary.slab(RecipeMaterial.of(Blocks.END_STONE)))
            .buildAndRegister();

    public static final Block END_STONE_STAIR = defineBlock(
            "end_stone_stairs",
            props -> new StairBlock(Blocks.END_STONE.defaultBlockState(), props)
    )
            .addTrait(BlockTraits.STAIR_BLOCK)
            .addTrait(ModelTraitLibrary.stairs(() -> Blocks.END_STONE))
            .addTrait(RecipeTraitLibrary.stairs(RecipeMaterial.of(Blocks.END_STONE)))
            .buildAndRegister();

    public static final Block END_STONE_WALLS = defineBlock("end_stone_wall", WallBlock::new)
            .addTrait(BlockTraits.WALL_BLOCK)
            .addTrait(ModelTraitLibrary.wall(() -> Blocks.END_STONE))
            .addTrait(RecipeTraitLibrary.wall(RecipeMaterial.of(Blocks.END_STONE)))
            .buildAndRegister();


    public static List<Block> getModBlocks() {
        return getBlockRegistry().allBlocks().toList();
    }

    @SafeVarargs
    public static <T extends Block> T registerBlock(
            String name,
            Function<BlockBehaviour.Properties, T> blockF,
            TagKey<Block>... tags
    ) {
        return defineBlock(name, blockF)
                .addTags(tags)
                .buildAndRegister();
    }

    @SafeVarargs
    public static EndTerrainBlock registerEndTerrain(
            String name,
            MapColor color,
            TagKey<Block>... tags
    ) {
        return registerEndTerrain(name, color, EndTerrainBlock::new, tags);
    }

    @SafeVarargs
    public static <T extends Block> T registerEndTerrain(
            String name,
            MapColor color,
            Function<BlockBehaviour.Properties, T> blockF,
            TagKey<Block>... tags
    ) {
        return (T) defineBlock(name, blockF)
                .mapColor(color)
                .addTrait(TerrainBlockTrait.DEFAULT)
                .addTags(tags)
                .buildAndRegister();
    }

    public static Block registerPath(
            String name,
            Block source
    ) {
        return defineBlock(name, DirtPathBlock::new)
                .replacePropertiesWithCopy(source)
                .addTrait(PathBlockTrait.withSource(source))
                .buildAndRegister();
    }


    public static <T extends Block> DefaultBlockDefinition<T> defineBlock(
            String name,
            Function<BlockBehaviour.Properties, T> blockF
    ) {
        return getBlockRegistry()
                .defineDefaultBlock(name, def -> blockF.apply(def.getProperties()));
    }

    public static VanillaBlockDefinition defineEndBlock(String name) {
        return getBlockRegistry()
                .defineDefaultBlock(name);
    }

    public static <T extends Block> DefaultBlockDefinition<T> defineBlockOnly(
            String name,
            Function<BlockBehaviour.Properties, T> blockF
    ) {
        return defineBlock(name, blockF).withBlockItem((def, block) -> null);
    }

    public static Block registerEndBlockOnly(String name, Function<BlockBehaviour.Properties, Block> blockF) {
        return defineBlockOnly(name, blockF)
                .buildAndRegister();
    }

    @NotNull
    public static BlockRegistry getBlockRegistry() {
        if (BLOCKS_REGISTRY == null) {
            BLOCKS_REGISTRY = BlockRegistry.forMod(BetterEnd.C);
        }
        return BLOCKS_REGISTRY;
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {

    }
}
