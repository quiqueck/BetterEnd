package org.betterx.betterend.registry;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.BaseAnvilBlock;
import org.betterx.bclib.blocks.BaseOreBlock;
import org.betterx.bclib.blocks.BaseTerrainBlock;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.blocks.SimpleLeavesBlock;
import org.betterx.bclib.blocks.StalactiteBlock;
import org.betterx.bclib.trait.block.*;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.*;
import org.betterx.betterend.blocks.EndPortalBlock;
import org.betterx.betterend.blocks.basis.*;
import org.betterx.betterend.client.models.EndModelTraits;
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
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.pottable.api.trait.PottablePlantBlockTrait;
import org.betterx.wover.pottable.api.trait.PottableSoilBlockTrait;
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
import net.minecraft.world.level.storage.loot.LootTable;
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
            .addTrait(ModelTraitLibrary.externalModel())
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
            "amber_moss", MapColor.COLOR_ORANGE, EndTerrainBlock::new, EndModelTraits.amberMoss(),
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
            "rutiscus", MapColor.COLOR_ORANGE, EndTerrainBlock::new, ModelTraitLibrary.externalModel(),
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
    public static final Block AMBER_MOSS_PATH = registerPath(
            "amber_moss_path", AMBER_MOSS, EndModelTraits.amberMossPath(() -> AMBER_MOSS));
    public static final Block JUNGLE_MOSS_PATH = registerPath("jungle_moss_path", JUNGLE_MOSS);
    public static final Block SANGNUM_PATH = registerPath("sangnum_path", SANGNUM);
    public static final Block RUTISCUS_PATH = registerPath("rutiscus_path", RUTISCUS);

    public static final Block MOSSY_OBSIDIAN = defineBlock("mossy_obsidian", MossyObsidian::new)
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch(Blocks.OBSIDIAN))
            .addTrait(BlockTraits.OBSIDIAN_BLOCK)
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
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
            .addTrait(ModelTraitLibrary.externalModel())
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
            .addTrait(ModelTraitLibrary.externalModel())
            .mapColor(MapColor.COLOR_BROWN)
            .randomTicks()
            .buildAndRegister();

    public static final Block SULPHUR_CRYSTAL = defineBlock("sulphur_crystal", SulphurCrystalBlock::new)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> SulphurCrystalBlock.buildLoot(block, provider)
            ))
            .addTrait(ModelTraitLibrary.externalModel())
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
            .noCollission()
            .buildAndRegister();

    public static final Block MISSING_TILE = defineBlock("missing_tile", Block::new)
            .replacePropertiesWithCopy(Blocks.END_STONE)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
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
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block FLAVOLITE_RUNED_ETERNAL = defineBlock("flavolite_runed_eternal", RunedFlavolite::new)
            .replacePropertiesWithCopy(EndBlocks.FLAVOLITE.getBlock(SlotType.POLISHED))
            .strength(-11, Blocks.BEDROCK.getExplosionResistance())
            .lightLevel(state -> state.getValue(RunedFlavolite.ACTIVATED) ? 8 : 0)
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block HYDROTHERMAL_VENT = defineBlock("hydrothermal_vent", HydrothermalVentBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
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
            .addTrait(ModelTraitLibrary.cube())
            .randomTicks()
            .buildAndRegister();

    public static final Block DENSE_EMERALD_ICE = defineBlock("dense_emerald_ice", Block::new)
            .addTrait(IceBlockTrait.withBase(Blocks.PACKED_ICE))
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final Block ANCIENT_EMERALD_ICE = defineBlock("ancient_emerald_ice", AncientEmeraldIceBlock::new)
            .addTrait(IceBlockTrait.withBase(Blocks.BLUE_ICE))
            .addTrait(ModelTraitLibrary.cube())
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
     .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
     .buildAndRegister();

    public static final Block MOSSY_GLOWSHROOM_CAP = defineBlock("mossy_glowshroom_cap", MossyGlowshroomCapBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block MOSSY_GLOWSHROOM_HYMENOPHORE = defineBlock(
            "mossy_glowshroom_hymenophore",
            GlowingHymenophoreBlock::new
    ).addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
     .addTrait(ModelTraitLibrary.externalModel())
     .addTrait(BlockTraits.LOOT_TABLE)
     .lightLevel((_s) -> 15)
     .sound(SoundType.WART_BLOCK)
     .buildAndRegister();

    public static final Block MOSSY_GLOWSHROOM_FUR = defineBlock("mossy_glowshroom_fur", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_LIGHT_BLUE, 15, true, 16, MOSSY_GLOWSHROOM_SAPLING, false))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial MOSSY_GLOWSHROOM = new EndWoodenComplexMaterial(
            "mossy_glowshroom",
            MapColor.COLOR_GRAY,
            MapColor.WOOD
    ).buildAndRegister();

    public static final Block PYTHADENDRON_SAPLING = defineBlock("pythadendron_sapling", PythadendronSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_PURPLE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .buildAndRegister();

    public static final Block PYTHADENDRON_LEAVES = defineBlock("pythadendron_leaves", PottableLeavesBlock::new)
            // generateModel=false: LeavesBlockTrait's own model factory would give the item a flat
            // 2D icon and, since block model traits don't dedupe, collide with the one below - supply
            // our own cube model + properly item-delegated model instead.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, -1, PYTHADENDRON_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial PYTHADENDRON = new EndWoodenComplexMaterial(
            "pythadendron",
            MapColor.COLOR_MAGENTA,
            MapColor.COLOR_PURPLE
    ).buildAndRegister();

    public static final Block END_LOTUS_SEED = defineBlock("end_lotus_seed", EndLotusSeedBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.COLOR_CYAN, 0, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block END_LOTUS_STEM = defineBlock("end_lotus_stem", EndLotusStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
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
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block END_LOTUS_FLOWER = defineBlockOnly("end_lotus_flower", EndLotusFlowerBlock::new)
            .addTrait(PlantBlockTrait.withColor(MapColor.COLOR_PINK, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_LOTUS_STEM))
            .addTrait(CompostableBlockTrait.withDefault())
            .addTrait(BlockTraits.FLAMMABLE.withDefault())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            // No block item exists, so it drops lotus seeds (1-2) via an explicit loot table rather than
            // dropping itself.
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> EndLotusFlowerBlock.buildLoot(block, provider)))
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel((bs) -> 15)
            .buildAndRegister();

    public static final EndWoodenComplexMaterial END_LOTUS = new EndWoodenComplexMaterial(
            "end_lotus",
            MapColor.COLOR_LIGHT_BLUE,
            MapColor.COLOR_CYAN
    ).buildAndRegister();

    public static final Block LACUGROVE_SAPLING = defineBlock("lacugrove_sapling", LacugroveSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_CYAN))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_DUST))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_MOSS_OR_DUST))
            .buildAndRegister();

    public static final Block LACUGROVE_LEAVES = defineBlock("lacugrove_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_CYAN, 0, false, -1, LACUGROVE_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial LACUGROVE = new EndWoodenComplexMaterial(
            "lacugrove",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_YELLOW
    ).buildAndRegister();

    public static final Block DRAGON_TREE_SAPLING = defineBlock("dragon_tree_sapling", DragonTreeSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_MAGENTA))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .buildAndRegister();

    public static final Block DRAGON_TREE_LEAVES = defineBlock("dragon_tree_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, -1, DRAGON_TREE_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial DRAGON_TREE = new EndWoodenComplexMaterial(
            "dragon_tree",
            MapColor.COLOR_BLACK,
            MapColor.COLOR_MAGENTA
    ).buildAndRegister();

    public static final Block TENANEA_SAPLING = defineBlock("tenanea_sapling", TenaneaSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_PINK))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_PINK_MOSS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_PINK_MOSS))
            .buildAndRegister();

    public static final Block TENANEA_LEAVES = defineBlock("tenanea_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_PINK, 0, false, -1, TENANEA_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final Block TENANEA_FLOWERS = defineBlock("tenanea_flowers", TenaneaFlowersBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.COLOR_PINK, 15, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block TENANEA_OUTER_LEAVES = defineBlockOnly("tenanea_outer_leaves", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_PINK, 0, false, 32, TENANEA_SAPLING, false))
            .addTrait(noLootTableTrait())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial TENANEA = new EndWoodenComplexMaterial(
            "tenanea",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_PINK
    ).buildAndRegister();

    public static final Block HELIX_TREE_SAPLING = defineBlock("helix_tree_sapling", HelixTreeSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_ORANGE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_AMBER_MOSS))
            .buildAndRegister();

    public static final Block HELIX_TREE_LEAVES = defineBlock("helix_tree_leaves", HelixTreeLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 0, false, 8, HELIX_TREE_SAPLING, false))
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
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
     .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_JUNGLE_MOSS))
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
     .addTrait(ModelTraitLibrary.cube())
     .mapColor(MapColor.COLOR_PURPLE)
     .lightLevel((bs) -> 15)
     .buildAndRegister();

    public static final Block UMBRELLA_TREE_CLUSTER_EMPTY = defineBlock(
            "umbrella_tree_cluster_empty",
            UmbrellaTreeClusterEmptyBlock::new
    ).replacePropertiesWithCopy(Blocks.NETHER_WART_BLOCK)
     .addTrait(BlockTraits.WOOD_BLOCK)
     .addTrait(BlockTraits.LOOT_TABLE)
     .addTrait(ModelTraitLibrary.cube())
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
     .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
             (key, block, generator) -> JellyshroomCapBlock.provideBlockModel(generator, block)
     ) : null)
     .buildAndRegister();

    public static final EndWoodenComplexMaterial JELLYSHROOM = new EndWoodenComplexMaterial(
            "jellyshroom",
            MapColor.COLOR_PURPLE,
            MapColor.COLOR_LIGHT_BLUE
    ).buildAndRegister();

    public static final Block LUCERNIA_SAPLING = defineBlock("lucernia_sapling", LucerniaSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_ORANGE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_RUTISCUS))
            .buildAndRegister();

    public static final Block LUCERNIA_LEAVES = defineBlock("lucernia_leaves", PottableLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 0, false, -1, LUCERNIA_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/lucernia_leaves_1")))
            .buildAndRegister();

    public static final Block LUCERNIA_OUTER_LEAVES = defineBlockOnly("lucernia_outer_leaves", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_RED, 0, false, 32, LUCERNIA_SAPLING, false))
            .addTrait(noLootTableTrait())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/lucernia_outer_leaves_1")))
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
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(() -> BetterEnd.C.mk("item/umbrella_moss_small")))
            .lightLevel(state -> 11)
            .buildAndRegister();

    public static final Block UMBRELLA_MOSS_TALL = defineBlock("umbrella_moss_tall", UmbrellaMossTallBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, true))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(() -> BetterEnd.C.mk("item/umbrella_moss_large")))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block CREEPING_MOSS = defineBlock("creeping_moss", GlowingMossBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, true))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(state -> 11)
            .buildAndRegister();
    public static final Block CHORUS_GRASS = defineBlock("chorus_grass", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block CAVE_GRASS = defineBlock("cave_grass", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(CAVE_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block CRYSTAL_GRASS = defineBlock("crystal_grass", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(CRYSTAL_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block SHADOW_PLANT = defineBlock("shadow_plant", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(SHADOW_GRASS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block BUSHY_GRASS = defineBlock("bushy_grass", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(PINK_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block AMBER_GRASS = defineBlock("amber_grass", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(AMBER_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();
    public static final Block TWISTED_UMBRELLA_MOSS = defineBlock(
            "twisted_umbrella_moss",
            TwistedUmbrellaMossBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(() -> BetterEnd.C.mk("item/twisted_umbrella_moss_small")))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block TWISTED_UMBRELLA_MOSS_TALL = defineBlock(
            "twisted_umbrella_moss_tall",
            TwistedUmbrellaMossTallBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            // Blockstate/models are hand-authored (top/rotation variants) - the generic cube-model
            // generator doesn't know about them and was overwriting them with a broken single-texture
            // cube using the (non-existent) plain block texture. Exclude from validation and only
            // wire the flat item icon.
            .addTrait(ModelTraitLibrary.externalModelFlatItem(() -> BetterEnd.C.mk("item/twisted_umbrella_moss_large")))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block JUNGLE_GRASS = defineBlock("jungle_grass", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(JUNGLE_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block BLOOMING_COOKSONIA = defineBlock("blooming_cooksonia", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block SALTEAGO = defineBlock("salteago", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block VAIOLUSH_FERN = defineBlock("vaiolush_fern", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block FRACTURN = defineBlock("fracturn", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(END_MOSS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();
    public static final Block CLAWFERN = defineBlock("clawfern", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(SANGNUM, MOSSY_OBSIDIAN, MOSSY_DRAGON_BONE))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block GLOBULAGUS = defineBlock("globulagus", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(SANGNUM, MOSSY_OBSIDIAN, MOSSY_DRAGON_BONE))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block ORANGO = defineBlock("orango", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block AERIDIUM = defineBlock("aeridium", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block LUTEBUS = defineBlock("lutebus", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block LAMELLARIUM = defineBlock("lamellarium", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(RUTISCUS))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    public static final Block INFLEXIA = defineBlock("inflexia", EndPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PLANT, false, true))
            .addTrait(SurvivesOnBlockTrait.withBlocks(PALLIDIUM_FULL, PALLIDIUM_HEAVY, PALLIDIUM_THIN, PALLIDIUM_TINY))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.crossPlant())
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();
    public static final Block FLAMMALIX = defineBlock("flammalix", FlammalixBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_PALLIDIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_PALLIDIUM))
            // No dedicated item icon exists - reuse one of the block's own random-rotation variants
            // as the item's model instead of a flat texture icon.
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/flammalix_1")))
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
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(GlowLichenBlock.emission(7))
            .buildAndRegister();

    public static final Block BLUE_VINE_SEED = defineBlock("blue_vine_seed", BlueVineSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, true))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block BLUE_VINE = defineBlockOnly("blue_vine", BlueVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.COLOR_BLUE, 15, false, false))
            .addTrait(noLootTableTrait())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block BLUE_VINE_LANTERN = defineBlock("blue_vine_lantern", BlueVineLanternBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK.withDefault())
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> BlueVineLanternBlock.provideBlockModel(generator, block)
            ) : null)
            .lightLevel(state -> 15)
            .sound(SoundType.WART_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block BLUE_VINE_FUR = defineBlock("blue_vine_fur", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_BLUE, 0, false, 15, BLUE_VINE_SEED, false))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block LANCELEAF_SEED = defineBlock("lanceleaf_seed", LanceleafSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block LANCELEAF = defineBlock("lanceleaf", LanceleafBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_AMBER_MOSS))
            // Drops lanceleaf seeds (guaranteed from the bottom segment, 50% from the others) instead of
            // itself; overrides the dropSelf() bundled by compostableWithColor above.
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> LanceleafBlock.buildLoot(block, provider)))
            // No dedicated item icon exists (multi-segment plant, block texture is per-segment, not a
            // single "lanceleaf" sprite) - reuse the top segment's own model as the item's, like other
            // multi-part plants in this file (e.g. large_amaranita_mushroom's cap).
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/lanceleaf_leaf_top")))
            .offsetType(OffsetType.XZ)
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_SEED = defineBlock("glowing_pillar_seed", GlowingPillarSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(state -> state.getValue(EndPlantWithAgeBlock.AGE) * 3 + 3)
            .sound(SoundType.GRASS)
            .offsetType(OffsetType.XZ)
            .randomTicks()
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_ROOTS = defineBlockOnly(
            "glowing_pillar_roots",
            GlowingPillarRootsBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(noLootTableTrait())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .lightLevel(state -> 15)
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_LUMINOPHOR = defineBlock(
            "glowing_pillar_luminophor",
            GlowingPillarLuminophorBlock::new
    )
            .addTrait(BlockTraits.METAL_BLOCK)
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) ->
                            GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
            ) : null)
            .mapColor(MapColor.COLOR_ORANGE)
            .strength(0.2F)
            .lightLevel(state -> 15)
            .sound(SoundType.GRASS)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_LEAVES = defineBlock("glowing_pillar_leaves", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 0, false, 15, GLOWING_PILLAR_SEED, false))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block SMALL_JELLYSHROOM = defineBlock("small_jellyshroom", SmallJellyshroomBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch())
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(s -> 13)
            .sound(SoundType.NETHER_WART)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .buildAndRegister();
    public static final Block BOLUX_MUSHROOM = defineBlock("bolux_mushroom", BoluxMushroomBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .lightLevel(bs -> 10)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block LUMECORN_SEED = defineBlock("lumecorn_seed", LumecornSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_MOSS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block LUMECORN = defineBlockOnly("lumecorn", LumecornBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .strength(0.5F, 0.5F)
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block SMALL_AMARANITA_MUSHROOM = defineBlock(
            "small_amaranita_mushroom",
            SmallAmaranitaBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_BONE))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_END_BONE))
            .buildAndRegister();
    public static final Block LARGE_AMARANITA_MUSHROOM = defineBlock(
            "large_amaranita_mushroom",
            LargeAmaranitaBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, true, false))
            // No dedicated item icon exists (multi-segment plant, block texture is per-segment) -
            // reuse the top segment's own cap model as the item's, matching small_amaranita_mushroom's
            // single-texture convention as closely as this multi-block plant allows.
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/large_amaranita_cap")))
            .lightLevel(state -> state.getValue(BlockProperties.TRIPLE_SHAPE) == BlockProperties.TripleShape.TOP
                    ? 15
                    : 0)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_BONE))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_END_BONE))
            .buildAndRegister();
    public static final Block AMARANITA_STEM = defineBlock("amaranita_stem", AmaranitaStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.externalModel())
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .buildAndRegister();
    public static final Block AMARANITA_HYPHAE = defineBlock("amaranita_hyphae", AmaranitaStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.pillar())
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .buildAndRegister();
    public static final Block AMARANITA_HYMENOPHORE = defineBlock(
            "amaranita_hymenophore",
            AmaranitaHymenophoreBlock::new
    )
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(ModelTraitLibrary.cube())
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block AMARANITA_LANTERN = defineBlock("amaranita_lantern", GlowingHymenophoreBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) ->
                            GlowingHymenophoreBlock.provideUnshadedCubeModel(generator, block)
            ) : null)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block AMARANITA_FUR = defineBlock("amaranita_fur", FurBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_CYAN, 0, false, 15, SMALL_AMARANITA_MUSHROOM, false))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();
    public static final Block AMARANITA_CAP = defineBlock("amaranita_cap", AmaranitaCapBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block NEON_CACTUS = defineBlock("neon_cactus", NeonCactusPlantBlock::new)
            .lightLevel(bs -> 15)
            .randomTicks()
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/neon_cactus_small")))
            .buildAndRegister();
    public static final Block NEON_CACTUS_BLOCK = defineBlock("neon_cactus_block", NeonCactusBlock::new)
            .lightLevel(bs -> 15)
            .addTrait(ModelTraitLibrary.pillar())
            .buildAndRegister();
    public static final Block NEON_CACTUS_BLOCK_STAIRS = defineBlock(
            "neon_cactus_stairs",
            props -> new StairBlock(NEON_CACTUS_BLOCK.defaultBlockState(), props)
    )
            .addTrait(BlockTraits.STAIR_BLOCK)
            .addTrait(EndModelTraits.litStairs(() -> BetterEnd.C.mk("block/neon_cactus_block")))
            .addTrait(RecipeTraitLibrary.stairs(RecipeMaterial.of(NEON_CACTUS_BLOCK)))
            .buildAndRegister();
    public static final Block NEON_CACTUS_BLOCK_SLAB = defineBlock("neon_cactus_slab", SlabBlock::new)
            .addTrait(BlockTraits.SLAB_BLOCK)
            .addTrait(EndModelTraits.slabFrom(() -> NEON_CACTUS_BLOCK, () -> BetterEnd.C.mk("block/neon_cactus_block")))
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
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block BLOSSOM_BERRY = defineBlock(
            "blossom_berry_seed",
            p -> new PottableCropBlock(p, EndItems.BLOSSOM_BERRY, PINK_MOSS)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_PINK_MOSS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block AMBER_ROOT = defineBlock(
            "amber_root_seed",
            p -> new PottableCropBlock(p, EndItems.AMBER_ROOT_RAW, AMBER_MOSS)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block CHORUS_MUSHROOM = defineBlock(
            "chorus_mushroom_seed",
            p -> new PottableCropBlock(p, EndItems.CHORUS_MUSHROOM_RAW, CHORUS_NYLIUM)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    //public static final Block PEARLBERRY = registerBlock("pearlberry_seed", new PottableCropBlock(EndItems.BLOSSOM_BERRY, END_MOSS, END_MYCELIUM));
    public static final Block CAVE_PUMPKIN_SEED = defineBlock("cave_pumpkin_seed", CavePumpkinVineBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_ORANGE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block CAVE_PUMPKIN = defineBlock("cave_pumpkin", CavePumpkinBlock::new)
            .lightLevel(state -> state.getValue(BlockProperties.SMALL) ? 10 : 15)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .buildAndRegister();

    // Water plants
    public static final Block BUBBLE_CORAL = defineBlock("bubble_coral", BubbleCoralBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .sound(SoundType.CORAL_BLOCK)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block MENGER_SPONGE = defineBlock("menger_sponge", MengerSpongeBlock::new)
            .noOcclusion()
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .buildAndRegister();
    public static final Block MENGER_SPONGE_WET = defineBlock("menger_sponge_wet", MengerSpongeWetBlock::new)
            .noOcclusion()
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .buildAndRegister();
    public static final Block CHARNIA_RED = defineBlock("charnia_red", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block CHARNIA_PURPLE = defineBlock("charnia_purple", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block CHARNIA_ORANGE = defineBlock("charnia_orange", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block CHARNIA_LIGHT_BLUE = defineBlock("charnia_light_blue", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block CHARNIA_CYAN = defineBlock("charnia_cyan", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_CYAN, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block CHARNIA_GREEN = defineBlock("charnia_green", CharniaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_GREEN, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block END_LILY = defineBlockOnly("end_lily", EndLilyBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.WATER, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            // No block item; the top segment drops lily leaves + seeds via this table.
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> EndLilyBlock.buildLoot(block, provider)))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();
    public static final Block END_LILY_SEED = defineBlock("end_lily_seed", EndLilySeedBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.WATER, 0, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block HYDRALUX_SAPLING = defineBlock("hydralux_sapling", HydraluxSaplingBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.COLOR_LIGHT_BLUE, 0, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SULPHURIC_ROCK))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block HYDRALUX = defineBlockOnly("hydralux", HydraluxBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            // No block item; the flower/roots segments drop petals/saplings via this table.
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> HydraluxBlock.buildLoot(block, provider)))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();
    public static final Block HYDRALUX_PETAL_BLOCK = defineBlock("hydralux_petal_block", HydraluxPetalBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PODZOL, true, false))
            .addTrait(ModelTraitLibrary.cube())
            .strength(1.0f, 1.0f)
            .sound(SoundType.WART_BLOCK)
            .buildAndRegister();
    public static final ColoredMaterial HYDRALUX_PETAL_BLOCK_COLORED = new ColoredMaterial(
            HydraluxPetalColoredBlock::new,
            HYDRALUX_PETAL_BLOCK,
            true,
            (def) -> def.addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> HydraluxPetalColoredBlock.provideBlockModel(generator, block)
            ) : null)
    );

    public static final Block POND_ANEMONE = defineBlock("pond_anemone", PondAnemoneBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_MAGENTA, false, false))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ModelTraitLibrary.externalModel())
            .sound(SoundType.CORAL_BLOCK)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .lightLevel(state -> 13)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();

    public static final Block FLAMAEA = defineBlock("flamaea", FlamaeaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(SurvivesOnBlockTrait.withBlocks(Blocks.WATER))
            .addTrait(ModelTraitLibrary.externalModel())
            .sound(SoundType.WET_GRASS)
            .buildAndRegister();

    public static final Block CAVE_BUSH = defineBlock("cave_bush", SimpleLeavesBlock::new)
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, 0.0F, null, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block MURKWEED = defineBlock("murkweed", MurkweedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block NEEDLEGRASS = defineBlock("needlegrass", NeedlegrassBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> NeedlegrassBlock.buildLoot(block, provider)
            ))
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .buildAndRegister();

    // Wall Plants //
    public static final Block PURPLE_POLYPORE = defineBlock("purple_polypore", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(s -> 13)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block AURANT_POLYPORE = defineBlock("aurant_polypore", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(s -> 13)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();
    public static final Block TAIL_MOSS = defineBlock("tail_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();
    public static final Block CYAN_MOSS = defineBlock("cyan_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_CYAN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block TWISTED_MOSS = defineBlock("twisted_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();
    public static final Block TUBE_WORM = defineBlock("tube_worm", EndUnderwaterWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block BULB_MOSS = defineBlock("bulb_moss", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_ORANGE, false, false))
            .lightLevel(bs -> 12)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block JUNGLE_FERN = defineBlock("jungle_fern", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_GREEN, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block RUSCUS = defineBlock("ruscus", EndWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    // Vines //
    public static final Block DENSE_VINE = defineBlock("dense_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block TWISTED_VINE = defineBlock("twisted_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(EndModelTraits.twistedVine())
            .buildAndRegister();

    public static final Block BULB_VINE_SEED = defineBlock("bulb_vine_seed", BulbVineSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE_OR_TREES))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block BULB_VINE = defineBlock("bulb_vine", BulbVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> BulbVineBlock.buildLoot(block, provider)
            ))
            .buildAndRegister();

    public static final Block JUNGLE_VINE = defineBlock("jungle_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block RUBINEA = defineBlock("rubinea", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block MAGNULA = defineBlock("magnula", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block FILALUX = defineBlock("filalux", FilaluxBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(OffsetType.NONE)
            .buildAndRegister();

    public static final Block FILALUX_WINGS = defineBlock("filalux_wings", FilaluxWingsBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(ModelTraitLibrary.externalModel())
            .sound(SoundType.WET_GRASS)
            .buildAndRegister();

    public static final Block FILALUX_LANTERN = defineBlock("filalux_lantern", FilaluxLanternBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
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
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block SILK_MOTH_HIVE = defineBlock("silk_moth_hive", SilkMothHiveBlock::new)
            .strength(0.5F, 0.1F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .randomTicks()
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Ores //
    public static final Block ENDER_ORE = defineBlock(
            "ender_ore",
            p -> new BaseOreBlock(p, () -> EndItems.ENDER_SHARD, 1, 3, 5)
    )
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();
    public static final Block AMBER_ORE = defineBlock(
            "amber_ore",
            p -> new BaseOreBlock(p, () -> EndItems.RAW_AMBER, 1, 2, 4)
    )
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
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
            .addTrait(ModelTraitLibrary.cube())
            .mapColor(MapColor.COLOR_GRAY)
            .strength(65F, 1200F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.NETHERITE_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block CHARCOAL_BLOCK = defineBlock("charcoal_block", CharcoalBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block ENDER_BLOCK = defineBlock("ender_block", EnderBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
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
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) ->
                            provider.dropOre(block, EndItems.CRYSTAL_SHARDS, UniformGenerator.between(1, 4))
            ))
            .buildAndRegister();
    public static final Block AMBER_BLOCK = defineBlock("amber_block", AmberBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
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
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block SMARAGDANT_CRYSTAL = defineBlock("smaragdant_crystal", SmaragdantCrystalBlock::new)
            .lightLevel(bs -> 15)
            .strength(1F)
            .noOcclusion()
            .sound(SoundType.AMETHYST)
            .addTrait(EndModelTraits.rotatedPillar(() -> BetterEnd.C.mk("block/smaragdant_crystal")))
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
            .addTrait(EndModelTraits.rotatedPillar(() -> BetterEnd.C.mk("block/budding_smaragdant_crystal")))
            .buildAndRegister();

    public static final Block RESPAWN_OBELISK = defineBlock("respawn_obelisk", RespawnObeliskBlock::new)
            .lightLevel(
                    state -> state.getValue(BlockProperties.TRIPLE_SHAPE) == BlockProperties.TripleShape.BOTTOM ? 0 : 15
            )
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Lanterns
    public static final ColoredMaterial IRON_BULB_LANTERN_COLORED = new ColoredMaterial(
            BulbVineLanternColoredBlock::new,
            IRON_SET.getBlock(MetalMaterial.BULB_LANTERN),
            false,
            def -> def.addTrait(ModCore.isDatagen() ? BulbVineLanternBlock.buildModel(null, null) : null)
    );


    // Blocks With Entity //
    public static final Block END_STONE_SMELTER = defineBlock("end_stone_smelter", EndStoneSmelter::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();
    public static final Block ETERNAL_PEDESTAL = getBlockRegistry()
            .defineDefaultBlock("eternal_pedestal", def -> new EternalPedestal(def.blockKey))
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> ((EternalPedestal) block).provideBlockModelsInstance(generator)
            ) : null)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block INFUSION_PEDESTAL = getBlockRegistry()
            .defineDefaultBlock("infusion_pedestal", def -> new InfusionPedestal(def.blockKey))
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> ((InfusionPedestal) block).provideBlockModelsInstance(generator)
            ) : null)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();
    public static final Block AETERNIUM_ANVIL = defineBlock("aeternium_anvil", AeterniumAnvil::new)
            .addTrait(BlockTraits.METAL_BLOCK)
            .addTrait(ModCore.isDatagen() ? BaseAnvilBlock.buildModel(null, null) : null)
            .buildAndRegister();

    // Technical
    public static final Block END_PORTAL_BLOCK = defineBlockOnly("end_portal_block", EndPortalBlock::new)
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
                .addTrait(PottableSoilBlockTrait.DEFAULT)
                .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                        (key, block, generator) -> {
                            final var terrain = (BaseTerrainBlock) block;
                            generator.createBlockTopSideBottom(terrain.getBaseBlock(), terrain, true);
                        }
                ) : null)
                .addTags(tags)
                .buildAndRegister();
    }

    /**
     * A terrain block whose top/side/bottom model would not fit its textures, so it supplies its own
     * {@code modelTrait} (e.g. {@link EndModelTraits#amberMoss()} or {@link ModelTraitLibrary#externalModel()})
     * instead of the default {@code createBlockTopSideBottom} model.
     */
    @SafeVarargs
    public static <T extends Block> T registerEndTerrain(
            String name,
            MapColor color,
            Function<BlockBehaviour.Properties, T> blockF,
            @org.jetbrains.annotations.Nullable BlockModelTrait modelTrait,
            TagKey<Block>... tags
    ) {
        return (T) defineBlock(name, blockF)
                .mapColor(color)
                .addTrait(TerrainBlockTrait.DEFAULT)
                .addTrait(PottableSoilBlockTrait.DEFAULT)
                .addTrait(modelTrait)
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

    /**
     * A path block that supplies its own {@code modelTrait} instead of the standard single path model (used
     * by amber-moss-style paths whose sides come from multi-variant {@code _side_N} textures).
     */
    public static Block registerPath(
            String name,
            Block source,
            @org.jetbrains.annotations.Nullable BlockModelTrait modelTrait
    ) {
        return defineBlock(name, DirtPathBlock::new)
                .replacePropertiesWithCopy(source)
                .addTrait(PathBlockTrait.withSource(source, false))
                .addTrait(modelTrait)
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

    /**
     * Overrides whatever loot table a preceding trait (e.g. {@code PlantBlockTrait.compostableWithColor}
     * or {@code VineBlockTrait.withColor}, both of which always attach a self-drop trait) would otherwise
     * generate, with an empty one - for block-only blocks (no item, via {@link #defineBlockOnly}) where
     * that assumed self-drop would fail datagen with "Item must not be air". Traits are matched by key with
     * only the latest instance kept, so adding this after the other trait replaces its loot behavior.
     */
    public static BlockTrait<?, ?> noLootTableTrait() {
        return BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable.lootTable());
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
