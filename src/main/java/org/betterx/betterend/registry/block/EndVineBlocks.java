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

public class EndVineBlocks {
    public static final Block BLUE_VINE_SEED = EndBlocks.defineBlock("blue_vine_seed", BlueVineSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLUE, false, true))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(BlueVineSeedBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/blue_vine_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/blue_vine_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/blue_vine_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/blue_vine_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/blue_vine_seed"))))
            .buildAndRegister();

    public static final Block BLUE_VINE = EndBlocks.defineBlockOnly("blue_vine", BlueVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.COLOR_BLUE, 15, false, false))
            // Overrides the self-drop trait always bundled by the preceding PlantBlockTrait/VineBlockTrait/
            // LeavesBlockTrait with an empty loot table - this block has no item (defineBlockOnly), so the
            // assumed self-drop would fail datagen with "Item must not be air". Traits are matched by key with
            // only the latest instance kept, so adding this after the other trait replaces its loot behavior.
            .addTrait(BlockTraits.LOOT_TABLE.with((tableKey, blockKey, block, provider) -> LootTable.lootTable()))
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            // Replaces the c:plant tag the retired BehaviourPlant marker (via UpDownPlantBlock) used to
            // contribute through the BCLAutoBlockTagProvider instanceof scan. The marker/mineable-hoe/
            // compostable parts BehaviourPlant also implied are already covered by VineBlockTrait above, so
            // PlantLikeBlockTrait.plant() is not used here to avoid re-adding those.
            .addTrait(VegetationTagTrait.plant())
            .buildAndRegister();

    public static final Block BLUE_VINE_FUR = EndBlocks.defineBlock("blue_vine_fur", FurBlock::new)
            // FurBlock decorative variant: keep pass-through (walkable=false), unlike genuine cube leaves.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_BLUE, 0, false, 1F / 15F, BLUE_VINE_SEED, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(EndModelTraits.upLeaf(BetterEnd.C.mk("block/blue_vine_fur"),
                    WeightedTemplateModelTrait.Item.flat(null)))
            .buildAndRegister();

    public static final Block DENSE_VINE = EndBlocks.defineBlock("dense_vine", BaseVineBlock::new)
            // light=0: BaseVineBlock's now-removed constructor mutation always overrode this trait's
            // lightLevel with its own hardcoded 0 (the default of the (Properties)-only ctor used here),
            // silently ignoring any light value declared at the trait. Kept at 0 to match the frozen golden.
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block TWISTED_VINE = EndBlocks.defineBlock("twisted_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(EndModelTraits.twistedVine())
            .buildAndRegister();

    public static final Block BULB_VINE_SEED = EndBlocks.defineBlock("bulb_vine_seed", BulbVineSeedBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(VegetationTagTrait.seed())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(WeightedCrossModelTrait.propertyDispatch(BulbVineSeedBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/bulb_vine_seed_0"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/bulb_vine_seed_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/bulb_vine_seed_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/bulb_vine_seed_3"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/bulb_vine_seed"))))
            .buildAndRegister();

    public static final Block BULB_VINE = EndBlocks.defineBlock("bulb_vine", BulbVineBlock::new)
            // onlyBottomIsLit=true: BulbVineBlock's now-removed constructor mutation hardcoded
            // onlyBottomIsLit=true regardless of this trait's onlyBottomIsLit argument, silently
            // overriding it. Kept true to match the frozen golden (lightEmission=0..15#2).
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15, true, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> BulbVineBlock.buildLoot(block, provider)
            ))
            .buildAndRegister();

    public static final Block JUNGLE_VINE = EndBlocks.defineBlock("jungle_vine", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(BaseVineBlock.SHAPE, List.of(
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.TOP, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/jungle_vine"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.MIDDLE, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/jungle_vine"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.BOTTOM, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/jungle_vine_bottom"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/jungle_vine_bottom"))))
            .buildAndRegister();

    public static final Block RUBINEA = EndBlocks.defineBlock("rubinea", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(BaseVineBlock.SHAPE, List.of(
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.TOP, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/rubinea"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.MIDDLE, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/rubinea"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.BOTTOM, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/rubinea_bottom"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/rubinea_bottom"))))
            .buildAndRegister();

    public static final Block MAGNULA = EndBlocks.defineBlock("magnula", BaseVineBlock::new)
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 0, false, false))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(BaseVineBlock.SHAPE, List.of(
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.TOP, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/magnula"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.MIDDLE, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/magnula"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.BOTTOM, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/magnula_bottom"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/magnula_bottom"))))
            .buildAndRegister();

    public static final Block FILALUX = EndBlocks.defineBlock("filalux", FilaluxBlock::new)
            // onlyBottomIsLit=true: FilaluxBlock's now-removed constructor mutation hardcoded
            // onlyBottomIsLit=true regardless of this trait's onlyBottomIsLit argument, silently
            // overriding it. Kept true to match the frozen golden (lightEmission=0..15#2). The
            // .offsetType(OffsetType.NONE) below (previously also re-applied by the constructor's
            // propMod) already matches the golden and needs no change.
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15, true, false))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(BaseVineBlock.SHAPE, List.of(
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.TOP, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/filalux_middle"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.MIDDLE, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/filalux_middle"))
                    )),
                            WeightedCrossModelTrait.Case.of(BlockProperties.TripleShape.BOTTOM, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/filalux_bottom"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/filalux_bottom"))))
            .offsetType(OffsetType.NONE)
            .buildAndRegister();

    public static final Block FILALUX_WINGS = EndBlocks.defineBlock("filalux_wings", FilaluxWingsBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(ModelTraitLibrary.externalModel())
            .sound(SoundType.WET_GRASS)
            .buildAndRegister();

    public static void ensureLoaded() {}
}
