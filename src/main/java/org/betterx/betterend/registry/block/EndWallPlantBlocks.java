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

public class EndWallPlantBlocks {
    public static final Block PURPLE_POLYPORE = EndBlocks.defineBlock("purple_polypore", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_PURPLE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(s -> 13)
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .buildAndRegister();

    public static final Block AURANT_POLYPORE = EndBlocks.defineBlock("aurant_polypore", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_ORANGE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ModelTraitLibrary.externalModel())
            .lightLevel(s -> 13)
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .buildAndRegister();

    public static final Block TAIL_MOSS = EndBlocks.defineBlock("tail_moss", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_BLACK, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final Block CYAN_MOSS = EndBlocks.defineBlock("cyan_moss", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_CYAN, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block TWISTED_MOSS = EndBlocks.defineBlock("twisted_moss", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_LIGHT_BLUE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(EndModelTraits.twistedMoss())
            .buildAndRegister();

    public static final Block TUBE_WORM = EndBlocks.defineBlock("tube_worm", EndUnderwaterWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_BROWN, false, false))
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block BULB_MOSS = EndBlocks.defineBlock("bulb_moss", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.TERRACOTTA_ORANGE, false, false))
            .addTrait(VegetationTagTrait.plant())
            .lightLevel(bs -> 12)
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(EndModelTraits.bulbMoss())
            .buildAndRegister();

    public static final Block JUNGLE_FERN = EndBlocks.defineBlock("jungle_fern", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_GREEN, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(EndModelTraits.jungleFern())
            .buildAndRegister();

    public static final Block RUSCUS = EndBlocks.defineBlock("ruscus", BaseWallPlantBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.COLOR_RED, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(SurvivesOnSolidTrait.DEFAULT)
            .addTrait(EndModelTraits.ruscus())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
