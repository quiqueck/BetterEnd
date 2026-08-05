package org.betterx.betterend.registry.block;


import org.betterx.betterend.registry.item.EndResourceItems;
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

public class EndCrystalBlocks {
    public static final Block AURORA_CRYSTAL = EndBlocks.defineBlock("aurora_crystal", AuroraCrystalBlock::new)
            .strength(1F)
            .noOcclusion()
            .lightLevel(bs -> 15)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.MINEABLE_WITH.needsHammer())
            .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) ->
                            provider.dropOre(block, EndResourceItems.CRYSTAL_SHARDS, UniformGenerator.between(1, 4))
            ))
            .buildAndRegister();

    public static final Block SMARAGDANT_CRYSTAL_SHARD = EndBlocks.defineBlock(
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
            .addTrait(WeightedCrossModelTrait.propertyDispatch(SmaragdantCrystalShardBlock.FACING, List.of(
                            WeightedCrossModelTrait.Case.of(Direction.UP, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/smaragdant_crystal_shard"))
                    )),
                            WeightedCrossModelTrait.Case.of(Direction.DOWN, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/smaragdant_crystal_shard")).rotated(180, 0)
                    )),
                            WeightedCrossModelTrait.Case.of(Direction.NORTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/smaragdant_crystal_shard")).rotated(90, 0)
                    )),
                            WeightedCrossModelTrait.Case.of(Direction.SOUTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/smaragdant_crystal_shard")).rotated(90, 180)
                    )),
                            WeightedCrossModelTrait.Case.of(Direction.EAST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/smaragdant_crystal_shard")).rotated(90, 90)
                    )),
                            WeightedCrossModelTrait.Case.of(Direction.WEST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/smaragdant_crystal_shard")).rotated(90, 270)
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/smaragdant_crystal_shard"))))
            .buildAndRegister();

    public static final Block SMARAGDANT_CRYSTAL = EndBlocks.defineBlock("smaragdant_crystal", RotatedPillarBlock::new)
            .lightLevel(bs -> 15)
            .strength(1F)
            .noOcclusion()
            .sound(SoundType.AMETHYST)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(EndModelTraits.rotatedPillar(() -> BetterEnd.C.mk("block/smaragdant_crystal")))
            .buildAndRegister();

    public static final CrystalSubblocksMaterial SMARAGDANT_SUBBLOCKS = new CrystalSubblocksMaterial(
            "smaragdant_crystal",
            SMARAGDANT_CRYSTAL
    );

    public static final Block BUDDING_SMARAGDANT_CRYSTAL = EndBlocks.defineBlock(
            "budding_smaragdant_crystal",
            BuddingSmaragdantCrystalBlock::new
    )
            .lightLevel(bs -> 15)
            .strength(1F, 1F)
            .noOcclusion()
            .sound(SoundType.AMETHYST)
            .randomTicks()
            .pushReaction(PushReaction.DESTROY)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            // No LOOT_TABLE trait on purpose: a budding block is not obtainable, and a block with no table
            // drops nothing. It used to carry dropSelf() alongside a getDrops override returning an empty
            // list - the override won, so the generated dropSelf table was both dead and wrong.
            .addTags(CommonBlockTags.BUDDING_BLOCKS)
            .addTrait(EndModelTraits.rotatedPillar(() -> BetterEnd.C.mk("block/budding_smaragdant_crystal")))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
