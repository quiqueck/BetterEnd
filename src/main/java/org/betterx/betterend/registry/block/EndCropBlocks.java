package org.betterx.betterend.registry.block;


import org.betterx.betterend.registry.item.EndFoodItems;
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

public class EndCropBlocks {
    // Crops
    public static final Block SHADOW_BERRY = EndBlocks.defineBlock("shadow_berry", ShadowBerryBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> ((PottableCropBlock) block).buildLoot(provider)))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(RandomTicksTrait.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block BLOSSOM_BERRY = EndBlocks.defineBlock(
            "blossom_berry_seed",
            p -> new PottableCropBlock(p, EndFoodItems.BLOSSOM_BERRY, EndTerrainBlocks.PINK_MOSS)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PINK, false, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> ((PottableCropBlock) block).buildLoot(provider)))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_PINK_MOSS))
            .addTrait(RandomTicksTrait.withDefault())
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block AMBER_ROOT = EndBlocks.defineBlock(
            "amber_root_seed",
            p -> new PottableCropBlock(p, EndFoodItems.AMBER_ROOT_RAW, EndTerrainBlocks.AMBER_MOSS)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> ((PottableCropBlock) block).buildLoot(provider)))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(RandomTicksTrait.withDefault())
            .addTrait(WeightedCrossModelTrait.propertyDispatch(PottableCropBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/amber_root_0")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/amber_root_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/amber_root_1")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/amber_root_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/amber_root_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/amber_root_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/amber_root_3")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/amber_root_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/amber_root_seed"))))
            .buildAndRegister();

    public static final Block CHORUS_MUSHROOM = EndBlocks.defineBlock(
            "chorus_mushroom_seed",
            p -> new PottableCropBlock(p, EndFoodItems.CHORUS_MUSHROOM_RAW, EndTerrainBlocks.CHORUS_NYLIUM)
    )
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> ((PottableCropBlock) block).buildLoot(provider)))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(RandomTicksTrait.withDefault())
            .addTrait(WeightedCrossModelTrait.propertyDispatch(PottableCropBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_mushroom_0")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/chorus_mushroom_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_mushroom_1")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/chorus_mushroom_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_mushroom_2")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/chorus_mushroom_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block"), BetterEnd.C.mk("block/chorus_mushroom_3")),
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/crop_block_inverted"), BetterEnd.C.mk("block/chorus_mushroom_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/chorus_mushroom_seed"))))
            .buildAndRegister();

    //public static final Block PEARLBERRY = EndBlocks.registerBlock("pearlberry_seed", new PottableCropBlock(EndFoodItems.BLOSSOM_BERRY, EndTerrainBlocks.END_MOSS, EndTerrainBlocks.END_MYCELIUM));
    public static final Block CAVE_PUMPKIN_SEED = EndBlocks.defineBlock("cave_pumpkin_seed", CavePumpkinVineBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_ORANGE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(RandomTicksTrait.withDefault())
            .addTrait(WeightedCrossModelTrait.propertyDispatch(CavePumpkinVineBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/cave_pumpkin_stem_0")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/cave_pumpkin_stem_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/cave_pumpkin_stem_1")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/cave_pumpkin_stem_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/cave_pumpkin_stem_2")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/cave_pumpkin_stem_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/cave_pumpkin_stem_3")),
                            WeightedCrossModelTrait.crossParent(BetterEnd.C.mk("block/cross_inverted"), BetterEnd.C.mk("block/cave_pumpkin_stem_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/cave_pumpkin_seed"))))
            .buildAndRegister();

    public static final Block CAVE_PUMPKIN = EndBlocks.defineBlock("cave_pumpkin", CavePumpkinBlock::new)
            .lightLevel(state -> state.getValue(BlockProperties.SMALL) ? 10 : 15)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> CavePumpkinBlock.buildLoot(block, provider)
            ))
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
