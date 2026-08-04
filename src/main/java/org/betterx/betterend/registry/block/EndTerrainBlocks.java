package org.betterx.betterend.registry.block;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.LeveledAnvilBlock;
import org.betterx.bclib.items.BaseAnvilItem;
import net.minecraft.core.Direction;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import org.betterx.bclib.blocks.StalactiteBlock;
import org.betterx.bclib.blocks.BasePlantBlock;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.blocks.UnderwaterPlantBlock;
import org.betterx.bclib.blocks.BaseTerrainBlock;
import org.betterx.bclib.blocks.BasePlantWithAgeBlock;
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
import org.betterx.bclib.trait.block.StalactiteBlockTrait;
import org.betterx.bclib.trait.block.TerrainTraits;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.DefaultBlockDefinition;
import de.ambertation.wover.block.api.VanillaBlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.item.api.BlockItemDefinition;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.recipe.api.RecipeMaterial;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
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

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.registry.EndTemplates;

public class EndTerrainBlocks {
    public static final Block ENDSTONE_DUST = EndBlocks.defineBlock("endstone_dust", EndstoneDustBlock::new)
            .replacePropertiesWithCopy(Blocks.SAND)
            .mapColor(Blocks.END_STONE.defaultMapColor())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShovel())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block END_MYCELIUM = EndBlocks.defineBlock("end_mycelium", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .addTrait(TerrainTraits.full())
            .buildAndRegister();

    public static final Block END_MOSS = EndBlocks.defineBlock("end_moss", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_CYAN)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block CHORUS_NYLIUM = EndBlocks.defineBlock("chorus_nylium", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_MAGENTA)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block CAVE_MOSS = EndBlocks.defineBlock("cave_moss", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_PURPLE)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block CRYSTAL_MOSS = EndBlocks.defineBlock("crystal_moss", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_CYAN)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block SHADOW_GRASS = EndBlocks.defineBlock("shadow_grass", ShadowGrassBlock::new)
            .mapColor(MapColor.COLOR_BLACK)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block PINK_MOSS = EndBlocks.defineBlock("pink_moss", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_PINK)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block AMBER_MOSS = EndBlocks.defineBlock("amber_moss", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_ORANGE)
            .addTrait(TerrainTraits.full(EndModelTraits.amberMoss()))
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block JUNGLE_MOSS = EndBlocks.defineBlock("jungle_moss", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_GREEN)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block SANGNUM = EndBlocks.defineBlock("sangnum", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_RED)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block RUTISCUS = EndBlocks.defineBlock("rutiscus", p -> new BaseTerrainBlock(p, Blocks.END_STONE))
            .mapColor(MapColor.COLOR_ORANGE)
            .addTrait(TerrainTraits.full(ModelTraitLibrary.externalModel()))
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block PALLIDIUM_FULL = EndBlocks.defineBlock("pallidium_full", p -> new PallidiumBlock(p, "full", null))
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block PALLIDIUM_HEAVY = EndBlocks.defineBlock(
            "pallidium_heavy", p -> new PallidiumBlock(p, "heavy", PALLIDIUM_FULL)
    )
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block PALLIDIUM_THIN = EndBlocks.defineBlock(
            "pallidium_thin", p -> new PallidiumBlock(p, "thin", PALLIDIUM_FULL)
    )
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block PALLIDIUM_TINY = EndBlocks.defineBlock(
            "pallidium_tiny", p -> new PallidiumBlock(p, "tiny", PALLIDIUM_FULL)
    )
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .addTrait(TerrainTraits.full())
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_END_STONE, BlockTags.NYLIUM)
            .buildAndRegister();

    public static final Block END_MYCELIUM_PATH = EndBlocks.defineBlock("end_mycelium_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(END_MYCELIUM)
            .addTrait(PathBlockTrait.withSource(END_MYCELIUM))
            .buildAndRegister();

    public static final Block END_MOSS_PATH = EndBlocks.defineBlock("end_moss_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(END_MOSS)
            .addTrait(PathBlockTrait.withSource(END_MOSS))
            .buildAndRegister();

    public static final Block CHORUS_NYLIUM_PATH = EndBlocks.defineBlock("chorus_nylium_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(CHORUS_NYLIUM)
            .addTrait(PathBlockTrait.withSource(CHORUS_NYLIUM))
            .buildAndRegister();

    public static final Block CAVE_MOSS_PATH = EndBlocks.defineBlock("cave_moss_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(CAVE_MOSS)
            .addTrait(PathBlockTrait.withSource(CAVE_MOSS))
            .buildAndRegister();

    public static final Block CRYSTAL_MOSS_PATH = EndBlocks.defineBlock("crystal_moss_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(CRYSTAL_MOSS)
            .addTrait(PathBlockTrait.withSource(CRYSTAL_MOSS))
            .buildAndRegister();

    public static final Block SHADOW_GRASS_PATH = EndBlocks.defineBlock("shadow_grass_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(SHADOW_GRASS)
            .addTrait(PathBlockTrait.withSource(SHADOW_GRASS))
            .buildAndRegister();

    public static final Block PINK_MOSS_PATH = EndBlocks.defineBlock("pink_moss_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(PINK_MOSS)
            .addTrait(PathBlockTrait.withSource(PINK_MOSS))
            .buildAndRegister();

    public static final Block AMBER_MOSS_PATH = EndBlocks.defineBlock("amber_moss_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(AMBER_MOSS)
            .addTrait(PathBlockTrait.withSource(AMBER_MOSS, false))
            .addTrait(EndModelTraits.amberMossPath(() -> AMBER_MOSS))
            .buildAndRegister();

    public static final Block JUNGLE_MOSS_PATH = EndBlocks.defineBlock("jungle_moss_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(JUNGLE_MOSS)
            .addTrait(PathBlockTrait.withSource(JUNGLE_MOSS))
            .buildAndRegister();

    public static final Block SANGNUM_PATH = EndBlocks.defineBlock("sangnum_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(SANGNUM)
            .addTrait(PathBlockTrait.withSource(SANGNUM))
            .buildAndRegister();

    public static final Block RUTISCUS_PATH = EndBlocks.defineBlock("rutiscus_path", DirtPathBlock::new)
            .replacePropertiesWithCopy(RUTISCUS)
            .addTrait(PathBlockTrait.withSource(RUTISCUS))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
