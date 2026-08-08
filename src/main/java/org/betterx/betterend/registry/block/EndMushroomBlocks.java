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

public class EndMushroomBlocks {
    public static final Block GLOWING_PILLAR_SEED = EndBlocks.defineBlock("glowing_pillar_seed", GlowingPillarSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(RandomTicksTrait.withDefault())
            .addTrait(WeightedCrossModelTrait.propertyDispatch(GlowingPillarSeedBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/glowing_pillar_seed_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/glowing_pillar_seed_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/glowing_pillar_seed_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/glowing_pillar_seed_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/glowing_pillar_seed"))))
            .lightLevel(state -> state.getValue(BasePlantWithAgeBlock.AGE) * 3 + 3)
            .sound(SoundType.GRASS)
            .offsetType(OffsetType.XZ)
            .randomTicks()
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_ROOTS = EndBlocks.defineBlockOnly(
            "glowing_pillar_roots",
            GlowingPillarRootsBlock::new
    )
            // walkable=true: the roots block movement (players should not walk through them).
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, true, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            // Overrides the self-drop trait always bundled by the preceding PlantBlockTrait/VineBlockTrait/
            // LeavesBlockTrait with an empty loot table - this block has no item (defineBlockOnly), so the
            // assumed self-drop would fail datagen with "Item must not be air". Traits are matched by key with
            // only the latest instance kept, so adding this after the other trait replaces its loot behavior.
            .addTrait(BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable.lootTable()))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(GlowingPillarRootsBlock.SHAPE, List.of(
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.TOP, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/glowing_pillar_roots_top"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.MIDDLE, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/glowing_pillar_roots_both"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.BOTTOM, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/glowing_pillar_roots_bottom"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.delegated()))
            .lightLevel(state -> 15)
            .buildAndRegister();

    public static final Block GLOWING_PILLAR_LEAVES = EndBlocks.defineBlock("glowing_pillar_leaves", FurBlock::new)
            // FurBlock decorative variant: keep pass-through (walkable=false), unlike genuine cube leaves.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 0, false, 1F / 15F, GLOWING_PILLAR_SEED, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(EndModelTraits.glowingPillarLeaves())
            .buildAndRegister();

    public static final Block SMALL_JELLYSHROOM = EndBlocks.defineBlock("small_jellyshroom", SmallJellyshroomBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch())
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(s -> 13)
            .sound(SoundType.NETHER_WART)
            // A small ground plant: give it the vanilla-style random offset so it doesn't render
            // perfectly grid-aligned (matches the other End plants, e.g. creeping moss). Safe without
            // dynamicShape because the block has no collision shape (hasCollision=false).
            .offsetType(BlockBehaviour.OffsetType.XYZ)
            .buildAndRegister();

    public static final Block BOLUX_MUSHROOM = EndBlocks.defineBlock("bolux_mushroom", BoluxMushroomBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .lightLevel(bs -> 10)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block LUMECORN_SEED = EndBlocks.defineBlock("lumecorn_seed", LumecornSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_MOSS))
            .addTrait(RandomTicksTrait.withDefault())
            .addTrait(WeightedCrossModelTrait.propertyDispatch(LumecornSeedBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lumecorn_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lumecorn_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lumecorn_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/lumecorn_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/lumecorn_seed"))))
            .buildAndRegister();

    public static final Block LUMECORN = EndBlocks.defineBlockOnly("lumecorn", LumecornBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .strength(0.5F, 0.5F)
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block SMALL_AMARANITA_MUSHROOM = EndBlocks.defineBlock(
            "small_amaranita_mushroom",
            SmallAmaranitaBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ModelTraitLibrary.externalModel())
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_BONE))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_END_BONE))
            .buildAndRegister();

    public static final Block LARGE_AMARANITA_MUSHROOM = EndBlocks.defineBlock(
            "large_amaranita_mushroom",
            LargeAmaranitaBlock::new
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, true, false))
            .addTrait(VegetationTagTrait.plant())
            // Hand-authored block models (bottom, middle, top) and item model (defined in models/item/large_amaranita_mushroom.json).
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(state -> state.getValue(BlockProperties.TRIPLE_SHAPE) == BlockProperties.TripleShape.TOP
                    ? 15
                    : 0)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_BONE))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_END_BONE))
            .buildAndRegister();

    public static final Block AMARANITA_STEM = EndBlocks.defineBlock("amaranita_stem", AmaranitaStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.externalModel())
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .buildAndRegister();

    public static final Block AMARANITA_HYPHAE = EndBlocks.defineBlock("amaranita_hyphae", AmaranitaStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.pillar())
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
            .buildAndRegister();

    public static final Block AMARANITA_HYMENOPHORE = EndBlocks.defineBlock(
            "amaranita_hymenophore",
            AmaranitaHymenophoreBlock::new
    )
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(ModelTraitLibrary.cube())
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block AMARANITA_FUR = EndBlocks.defineBlock("amaranita_fur", FurBlock::new)
            // FurBlock decorative variant: keep pass-through (walkable=false), unlike genuine cube leaves.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_CYAN, 0, false, 1F / 15F, SMALL_AMARANITA_MUSHROOM, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block AMARANITA_CAP = EndBlocks.defineBlock("amaranita_cap", AmaranitaCapBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .sound(SoundType.WOOD)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
