package org.betterx.betterend.registry.block;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.trait.TraitLists;
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
import de.ambertation.wover.block.api.trait.BlockTrait;
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

public class EndWaterPlantBlocks {
    // charnia_* micro-fold (category-traits Batch 4): the six charnia_* blocks are byte-identical but for
    // mapColor - compostableWithColor(color, false, false) + VegetationTagTrait.waterPlant() + the shared
    // randomly-Y-rotated template model. BE-local (not BCLib) because the model reference is
    // BetterEnd-specific (BetterEnd.C.mk("block/charnia")).
    private static List<BlockTrait<?, ?>> charnia(MapColor color) {
        return TraitLists.and(
                PlantBlockTrait.compostableWithColor(color, false, false),
                VegetationTagTrait.waterPlant(),
                TemplateModelTrait.randomYRotation(BetterEnd.C.mk("block/charnia"))
        );
    }

    public static final Block END_LOTUS_SEED = EndBlocks.defineBlock("end_lotus_seed", EndLotusSeedBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.COLOR_CYAN, 0, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(WeightedCrossModelTrait.propertyDispatch(EndLotusSeedBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lotus_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lotus_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lotus_3"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lotus_4"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/end_lotus_seed"))))
            .buildAndRegister();

    // These blocks have no item (defineBlockOnly), so they can't use compostableWithColor's
    // default dropSelf() loot table (block.asItem() would be air) - build the same trait bundle
    // without a loot table trait instead, so they simply drop nothing when broken.
    public static final Block END_LOTUS_LEAF = EndBlocks.defineBlockOnly("end_lotus_leaf", EndLotusLeafBlock::new)
            // Lily-pad leaves tile edge-to-edge (BOTTOM/MIDDLE/TOP segments must line up), so they must
            // NOT get the random X-Z plant offset - force OffsetType.NONE via the composition overload.
            .addTrait(PlantBlockTrait.withColor(MapColor.COLOR_PINK, true, OffsetType.NONE))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(CompostableBlockTrait.withDefault())
            .addTrait(BlockTraits.FLAMMABLE.withDefault())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(WeightedCrossModelTrait.property2Dispatch(EndLotusLeafBlock.SHAPE, EndLotusLeafBlock.HORIZONTAL_FACING, List.of(
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.BOTTOM, Direction.NORTH, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_center"))
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.BOTTOM, Direction.SOUTH, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_center"))
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.BOTTOM, Direction.EAST, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_center"))
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.BOTTOM, Direction.WEST, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_center"))
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.MIDDLE, Direction.NORTH, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_side"))
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.MIDDLE, Direction.SOUTH, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_side")).rotated(0, 180)
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.MIDDLE, Direction.EAST, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_side")).rotated(0, 90)
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.MIDDLE, Direction.WEST, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_side")).rotated(0, 270)
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.TOP, Direction.NORTH, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_corner"))
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.TOP, Direction.SOUTH, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_corner")).rotated(0, 180)
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.TOP, Direction.EAST, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_corner")).rotated(0, 90)
                    )),
                            WeightedCrossModelTrait.Case2.of(BlockProperties.TripleShape.TOP, Direction.WEST, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/end_lotus_leaf_corner")).rotated(0, 270)
                    ))
                    ),
                    WeightedCrossModelTrait.Item.delegated()))
            .buildAndRegister();

    public static final Block END_LOTUS_FLOWER = EndBlocks.defineBlockOnly("end_lotus_flower", EndLotusFlowerBlock::new)
            // Sits centered on the END_LOTUS_STEM column beneath it, so it must not get the random
            // X-Z plant offset (that would visibly detach it from the stem) - force OffsetType.NONE.
            .addTrait(PlantBlockTrait.withColor(MapColor.COLOR_PINK, true, OffsetType.NONE))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(SurvivesOnBlockTrait.withBlocks(EndWoodBlocks.END_LOTUS_STEM))
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

    // Water plants
    public static final Block BUBBLE_CORAL = EndBlocks.defineBlock("bubble_coral", BubbleCoralBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .sound(SoundType.CORAL_BLOCK)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    // A texture-swap child of the shared betterend:block/charnia mesh, placed with a random 0/90/180/270
    // Y rotation; item stays each color's hand-authored static item/charnia_* icon.
    public static final Block CHARNIA_RED = EndBlocks.defineBlock("charnia_red", CharniaBlock::new)
            .addTrait(charnia(MapColor.COLOR_RED))
            .buildAndRegister();

    public static final Block CHARNIA_PURPLE = EndBlocks.defineBlock("charnia_purple", CharniaBlock::new)
            .addTrait(charnia(MapColor.COLOR_PURPLE))
            .buildAndRegister();

    public static final Block CHARNIA_ORANGE = EndBlocks.defineBlock("charnia_orange", CharniaBlock::new)
            .addTrait(charnia(MapColor.COLOR_ORANGE))
            .buildAndRegister();

    public static final Block CHARNIA_LIGHT_BLUE = EndBlocks.defineBlock("charnia_light_blue", CharniaBlock::new)
            .addTrait(charnia(MapColor.COLOR_LIGHT_BLUE))
            .buildAndRegister();

    public static final Block CHARNIA_CYAN = EndBlocks.defineBlock("charnia_cyan", CharniaBlock::new)
            .addTrait(charnia(MapColor.COLOR_CYAN))
            .buildAndRegister();

    public static final Block CHARNIA_GREEN = EndBlocks.defineBlock("charnia_green", CharniaBlock::new)
            .addTrait(charnia(MapColor.COLOR_GREEN))
            .buildAndRegister();

    public static final Block END_LILY = EndBlocks.defineBlockOnly("end_lily", EndLilyBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.WATER, false, false))
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            // No block item; the top segment drops lily leaves + seeds via this table.
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> EndLilyBlock.buildLoot(block, provider)))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block END_LILY_SEED = EndBlocks.defineBlock("end_lily_seed", EndLilySeedBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.WATER, 0, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(EndLilySeedBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lily_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lily_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lily_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/end_lily_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/end_lily_seed"))))
            .buildAndRegister();

    public static final Block HYDRALUX = EndBlocks.defineBlockOnly("hydralux", HydraluxBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            // No block item; the flower/roots segments drop petals/saplings via this table.
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> HydraluxBlock.buildLoot(block, provider)))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block POND_ANEMONE = EndBlocks.defineBlock("pond_anemone", PondAnemoneBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_MAGENTA, false, false))
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ModelTraitLibrary.externalModel())
            .sound(SoundType.CORAL_BLOCK)
            .offsetType(BlockBehaviour.OffsetType.NONE)
            .lightLevel(state -> 13)
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_END_STONE))
            .buildAndRegister();

    public static final Block FLAMAEA = EndBlocks.defineBlock("flamaea", FlamaeaBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(SurvivesOnBlockTrait.withBlocks(Blocks.WATER))
            .addTrait(WeightedCrossModelTrait.simple(List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/flamaea_1")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/flamaea_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/flamaea_3")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/flamaea_4")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/plane_bottom"), BetterEnd.C.mk("block/flamaea_5"))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/flamaea_1"))))
            .sound(SoundType.WET_GRASS)
            .withBlockItem((def, block) -> new BlockItemDefinition<>(def, id -> new PlaceOnWaterBlockItem(block, id.getProperties())))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
