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

public class EndSaplingBlocks {
    public static final Block MOSSY_GLOWSHROOM_SAPLING = EndBlocks.defineBlock(
            "mossy_glowshroom_sapling",
            MossyGlowshroomSaplingBlock::new
    ).addTrait(SaplingBlockTrait.withLight(7))
     .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
     .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM))
     .buildAndRegister();

    public static final Block PYTHADENDRON_SAPLING = EndBlocks.defineBlock("pythadendron_sapling", PythadendronSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_PURPLE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_CHORUS_NYLIUM))
            .buildAndRegister();

    public static final Block LACUGROVE_SAPLING = EndBlocks.defineBlock("lacugrove_sapling", LacugroveSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_CYAN))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_MOSS_OR_DUST))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_MOSS_OR_DUST))
            .buildAndRegister();

    public static final Block DRAGON_TREE_SAPLING = EndBlocks.defineBlock("dragon_tree_sapling", DragonTreeSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_MAGENTA))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_SHADOW_GRASS))
            .buildAndRegister();

    public static final Block TENANEA_SAPLING = EndBlocks.defineBlock("tenanea_sapling", TenaneaSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_PINK))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_PINK_MOSS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_PINK_MOSS))
            .buildAndRegister();

    public static final Block HELIX_TREE_SAPLING = EndBlocks.defineBlock("helix_tree_sapling", HelixTreeSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_ORANGE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_AMBER_MOSS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_AMBER_MOSS))
            .buildAndRegister();

    public static final Block UMBRELLA_TREE_SAPLING = EndBlocks.defineBlock(
            "umbrella_tree_sapling",
            UmbrellaTreeSaplingBlock::new
    ).addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_BLUE))
     .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_JUNGLE_MOSS))
     .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_JUNGLE_MOSS))
     .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
     .buildAndRegister();

    public static final Block LUCERNIA_SAPLING = EndBlocks.defineBlock("lucernia_sapling", LucerniaSaplingBlock::new)
            .addTrait(SaplingBlockTrait.withColor(MapColor.COLOR_ORANGE))
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_RUTISCUS))
            .addTrait(PottablePlantBlockTrait.withSoils(EndTags.SURVIVES_ON_RUTISCUS))
            .buildAndRegister();

    public static final Block HYDRALUX_SAPLING = EndBlocks.defineBlock("hydralux_sapling", HydraluxSaplingBlock::new)
            .addTrait(WaterSeedBlockTrait.withColor(MapColor.COLOR_LIGHT_BLUE, 0, false))
            .addTrait(VegetationTagTrait.waterPlant())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_SULPHURIC_ROCK))
            .addTrait(WeightedCrossModelTrait.propertyDispatch(HydraluxSaplingBlock.AGE, List.of(
                            WeightedCrossModelTrait.Case.of(0, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/hydralux_sapling_1"))
                    )),
                            WeightedCrossModelTrait.Case.of(1, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/hydralux_sapling_2"))
                    )),
                            WeightedCrossModelTrait.Case.of(2, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/hydralux_sapling_3"))
                    )),
                            WeightedCrossModelTrait.Case.of(3, List.of(
                            WeightedCrossModelTrait.cropParent(BetterEnd.C.mk("block/cross_no_distortion"), BetterEnd.C.mk("block/hydralux_sapling_4"))
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("item/hydralux_spore"))))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
