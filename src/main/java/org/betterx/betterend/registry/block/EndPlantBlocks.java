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

public class EndPlantBlocks {
    public static final Block UMBRELLA_MOSS = EndBlocks.defineBlock("umbrella_moss", UmbrellaMossBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, true))
            .addTrait(VegetationTagTrait.plant())
            // Less restrictive on purpose: grows on any soil (see CommonBlockTags.SOIL) so it can be
            // used freely in decorations and on lake shores, not just jungle moss/mycelium.
            .addTrait(SurvivesOnBlockTrait.withTag(CommonBlockTags.SOIL))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(EndModelTraits.umbrellaMoss())
            .lightLevel(state -> 11)
            .buildAndRegister();

    public static final Block UMBRELLA_MOSS_TALL = EndBlocks.defineBlock("umbrella_moss_tall", UmbrellaMossTallBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, true))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> BaseDoublePlantBlock.buildLoot(block, provider)))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(() -> BetterEnd.C.mk("item/umbrella_moss_large")))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block CREEPING_MOSS = EndBlocks.defineBlock("creeping_moss", GlowingMossBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, true))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .addTrait(EndModelTraits.creepingMoss())
            .lightLevel(state -> 11)
            .buildAndRegister();

    public static final Block CHORUS_GRASS = EndBlocks.defineBlock("chorus_grass", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/chorus_grass_01")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/chorus_grass_02")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/chorus_grass_03")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/chorus_grass_04")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_grass_01")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_grass_02")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_grass_03")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_grass_04"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/chorus_grass_01"))))
            .offsetType(OffsetType.XZ)
            .replaceable()
            .buildAndRegister();

    // Ground cross-plant micro-fold (category-traits Batch 4): compostableWithColor(PLANT,false,true) +
    // VegetationTagTrait.plant() + PottablePlantBlockTrait.any() collapse into
    // PlantBlockTrait.groundCrossPlant(). The chained offsetType(XZ) is dropped (redundant - the trait
    // already forces XZ); .replaceable() is a genuine setter and stays chained.
    public static final Block CAVE_GRASS = EndBlocks.defineBlock("cave_grass", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withBlocks(EndTerrainBlocks.CAVE_MOSS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/cave_grass_1")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/cave_grass_2")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/cave_grass_1")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/cave_grass_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/cave_grass_1")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/cave_grass_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/cave_grass_1")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/cave_grass_2"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/cave_grass_1"))))
            .replaceable()
            .buildAndRegister();

    public static final Block CRYSTAL_GRASS = EndBlocks.defineBlock("crystal_grass", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withBlocks(EndTerrainBlocks.CRYSTAL_MOSS))
            .addTrait(ModelTraitLibrary.externalModel())
            .replaceable()
            .buildAndRegister();

    public static final Block SHADOW_PLANT = EndBlocks.defineBlock("shadow_plant", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/shadow_plant")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/shadow_plant")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/shadow_plant")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/shadow_plant"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block BUSHY_GRASS = EndBlocks.defineBlock("bushy_grass", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withBlocks(EndTerrainBlocks.PINK_MOSS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/bushy_grass_1")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/bushy_grass_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/bushy_grass_1")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/bushy_grass_2")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/bushy_grass_1")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/bushy_grass_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/bushy_grass_1")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/bushy_grass_2"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/bushy_grass_2"))))
            .replaceable()
            .buildAndRegister();

    public static final Block AMBER_GRASS = EndBlocks.defineBlock("amber_grass", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/amber_grass")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/amber_grass")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/amber_grass")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/amber_grass"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block TWISTED_UMBRELLA_MOSS = EndBlocks.defineBlock(
            "twisted_umbrella_moss",
            TwistedUmbrellaMossBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            .addTrait(EndModelTraits.twistedUmbrellaMoss())
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block TWISTED_UMBRELLA_MOSS_TALL = EndBlocks.defineBlock(
            "twisted_umbrella_moss_tall",
            TwistedUmbrellaMossTallBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> BaseDoublePlantBlock.buildLoot(block, provider)))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM))
            // Blockstate/models are hand-authored (top/rotation variants) - the generic cube-model
            // generator doesn't know about them and was overwriting them with a broken single-texture
            // cube using the (non-existent) plain block texture. Exclude from validation and only
            // wire the flat item icon.
            .addTrait(ModelTraitLibrary.externalModelFlatItem(() -> BetterEnd.C.mk("item/twisted_umbrella_moss_large")))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final Block JUNGLE_GRASS = EndBlocks.defineBlock("jungle_grass", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            // Less restrictive on purpose: grows on any soil (see CommonBlockTags.SOIL) so it can be
            // used freely in decorations and on lake shores, not just jungle moss.
            .addTrait(SurvivesOnBlockTrait.withTag(CommonBlockTags.SOIL))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/jungle_grass")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/jungle_grass")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/jungle_grass")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/jungle_grass")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/jungle_grass_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/jungle_grass_2")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/jungle_grass_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/jungle_grass_2")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/twisted_umbrella_moss_small")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/twisted_umbrella_moss_small")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/twisted_umbrella_moss_small")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/twisted_umbrella_moss_small"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block BLOOMING_COOKSONIA = EndBlocks.defineBlock("blooming_cooksonia", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_MOSS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/blooming_cooksonia")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/blooming_cooksonia")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/blooming_cooksonia")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/blooming_cooksonia"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block SALTEAGO = EndBlocks.defineBlock("salteago", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_MOSS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/salteago")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/salteago")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/salteago")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/salteago"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block VAIOLUSH_FERN = EndBlocks.defineBlock("vaiolush_fern", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_MOSS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/vaiolush_fern")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/vaiolush_fern")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/vaiolush_fern")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/vaiolush_fern"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block FRACTURN = EndBlocks.defineBlock("fracturn", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_MOSS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/fracturn")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/fracturn")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/fracturn")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/fracturn"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block CLAWFERN = EndBlocks.defineBlock("clawfern", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withBlocks(EndTerrainBlocks.SANGNUM, EndStoneBlocks.MOSSY_OBSIDIAN, EndStoneBlocks.MOSSY_DRAGON_BONE))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/clawfern")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/clawfern"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block GLOBULAGUS = EndBlocks.defineBlock("globulagus", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withBlocks(EndTerrainBlocks.SANGNUM, EndStoneBlocks.MOSSY_OBSIDIAN, EndStoneBlocks.MOSSY_DRAGON_BONE))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/globulagus")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/globulagus")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/globulagus")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/globulagus"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block ORANGO = EndBlocks.defineBlock("orango", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/orango")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/orango")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/orango")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/orango"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block AERIDIUM = EndBlocks.defineBlock("aeridium", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/aeridium")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/aeridium"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block LUTEBUS = EndBlocks.defineBlock("lutebus", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/lutebus")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/lutebus"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block LAMELLARIUM = EndBlocks.defineBlock("lamellarium", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/lamellarium")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/lamellarium")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/lamellarium")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/lamellarium"))
                    ),
                    WeightedCrossModelTrait.Item.flat(null)))
            .replaceable()
            .buildAndRegister();

    public static final Block INFLEXIA = EndBlocks.defineBlock("inflexia", BasePlantBlock::new)
            .addTrait(PlantBlockTrait.groundCrossPlant())
            .addTrait(SurvivesOnBlockTrait.withBlocks(EndTerrainBlocks.PALLIDIUM_FULL, EndTerrainBlocks.PALLIDIUM_HEAVY, EndTerrainBlocks.PALLIDIUM_THIN, EndTerrainBlocks.PALLIDIUM_TINY))
            .addTrait(ModelTraitLibrary.crossPlant())
            .replaceable()
            .buildAndRegister();

    public static final Block FLAMMALIX = EndBlocks.defineBlock("flammalix", FlammalixBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_PALLIDIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_PALLIDIUM))
            // No dedicated item icon exists - reuse one of the block's own random-rotation variants
            // as the item's model instead of a flat texture icon.
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/flammalix_1")))
            .lightLevel(state -> 12)
            .buildAndRegister();

    public static final MultifaceSpreadeableBlock CRYSTAL_MOSS_COVER = (MultifaceSpreadeableBlock) EndBlocks.defineBlock(
            "crystal_moss_cover",
            CrystalMossCoverBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(GlowLichenBlock.emission(7))
            // A flat multiface block cover: it must stay grid-aligned. Override the plant trait's
            // default random offset so the cover doesn't wobble off the faces it clings to.
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .buildAndRegister();

    public static final Block LANCELEAF_SEED = EndBlocks.defineBlock("lanceleaf_seed", LanceleafSeedBlock::new)
            // walkable=true: the seed blocks movement (players should not walk through it).
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, true, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(LanceleafSeedBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lanceleaf_seed_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lanceleaf_seed_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lanceleaf_seed_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lanceleaf_seed_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/lanceleaf_seed"))))
            .buildAndRegister();

    public static final Block LANCELEAF = EndBlocks.defineBlock("lanceleaf", LanceleafBlock::new)
            // walkable=true: lanceleaf blocks movement (players should not walk through it).
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, true, false))
            .addTrait(VegetationTagTrait.plant())
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

    public static final Block NEON_CACTUS = EndBlocks.defineBlock("neon_cactus", NeonCactusPlantBlock::new)
            .lightLevel(bs -> 15)
            .randomTicks()
            .strength(1.0F)
            .sound(SoundType.BAMBOO)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/neon_cactus_small")))
            .buildAndRegister();

    public static final Block CAVE_BUSH = EndBlocks.defineBlock("cave_bush", Block::new)
            // Non-full bush: solid (walkable=true) so it blocks movement like a real bush.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, 0.0F, null, false, true))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block MURKWEED = EndBlocks.defineBlock("murkweed", MurkweedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block NEEDLEGRASS = EndBlocks.defineBlock("needlegrass", NeedlegrassBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> NeedlegrassBlock.buildLoot(block, provider)
            ))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/needlegrass_01")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/needlegrass_02")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/needlegrass_03")),
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/needlegrass_04")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/needlegrass_01")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/needlegrass_02")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/needlegrass_03")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/needlegrass_04"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/needlegrass_01"))))
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
