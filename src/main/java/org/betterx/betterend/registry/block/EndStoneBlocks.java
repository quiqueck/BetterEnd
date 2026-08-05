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

public class EndStoneBlocks {
    public static final Block MOSSY_OBSIDIAN = EndBlocks.defineBlock("mossy_obsidian", MossyObsidian::new)
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTags(BCLBlockTags.BONEMEAL_SOURCE_OBSIDIAN)
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch(Blocks.OBSIDIAN))
            .addTrait(BlockTraits.OBSIDIAN_BLOCK)
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .randomTicks()
            .destroyTime(3)
            .buildAndRegister();

    public static final Block DRAGON_BONE_BLOCK = EndBlocks.defineBlock("dragon_bone_block", RotatedPillarBlock::new)
            .replacePropertiesWithCopy(Blocks.BONE_BLOCK)
            .addTags(EndTags.BONEMEAL_TARGET_DRAGON_BONE)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(ModelTraitLibrary.pillar())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block DRAGON_BONE_STAIRS = EndBlocks.defineBlock(
            "dragon_bone_stairs",
            props -> new StairBlock(DRAGON_BONE_BLOCK.defaultBlockState(), props)
    )
            .replacePropertiesWithCopy(DRAGON_BONE_BLOCK)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.STAIR_BLOCK)
            .addTrait(ModelTraitLibrary.stairs(() -> DRAGON_BONE_BLOCK))
            .addTrait(RecipeTraitLibrary.stairs(RecipeMaterial.of(DRAGON_BONE_BLOCK)))
            .buildAndRegister();

    public static final Block DRAGON_BONE_SLAB = EndBlocks.defineBlock("dragon_bone_slab", SlabBlock::new)
            .replacePropertiesWithCopy(DRAGON_BONE_BLOCK)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.SLAB_BLOCK)
            .addTrait(ModelTraitLibrary.slab(() -> DRAGON_BONE_BLOCK))
            .addTrait(RecipeTraitLibrary.slab(RecipeMaterial.of(DRAGON_BONE_BLOCK)))
            .buildAndRegister();

    public static final Block MOSSY_DRAGON_BONE = EndBlocks.defineBlock("mossy_dragon_bone", MossyDragonBoneBlock::new)
            .replacePropertiesWithCopy(DRAGON_BONE_BLOCK)
            .addTags(EndTags.BONEMEAL_SOURCE_DRAGON_BONE)
            .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch(DRAGON_BONE_BLOCK))
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.externalModel())
            .randomTicks()
            .destroyTime(0.5f)
            .buildAndRegister();

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

    public static final Block BRIMSTONE = EndBlocks.defineBlock("brimstone", BrimstoneBlock::new)
            .replacePropertiesWithCopy(Blocks.END_STONE)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.externalModel())
            .mapColor(MapColor.COLOR_BROWN)
            .randomTicks()
            .buildAndRegister();

    public static final Block SULPHUR_CRYSTAL = EndBlocks.defineBlock("sulphur_crystal", SulphurCrystalBlock::new)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(SurvivesOnBlockTrait.withTag(EndTags.SURVIVES_ON_BRIMSTONE))
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.with(
                    (tableKey, blockKey, block, provider) -> SulphurCrystalBlock.buildLoot(block, provider)
            ))
            .addTrait(WeightedCrossModelTrait.property2Dispatch(SulphurCrystalBlock.AGE, SulphurCrystalBlock.FACING, List.of(
                            WeightedCrossModelTrait.Case2.of(0, Direction.UP, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_0"))
                    )),
                            WeightedCrossModelTrait.Case2.of(0, Direction.DOWN, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_0")).rotated(180, 0)
                    )),
                            WeightedCrossModelTrait.Case2.of(0, Direction.NORTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_0")).rotated(90, 0)
                    )),
                            WeightedCrossModelTrait.Case2.of(0, Direction.SOUTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_0")).rotated(90, 180)
                    )),
                            WeightedCrossModelTrait.Case2.of(0, Direction.EAST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_0")).rotated(90, 90)
                    )),
                            WeightedCrossModelTrait.Case2.of(0, Direction.WEST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_0")).rotated(90, 270)
                    )),
                            WeightedCrossModelTrait.Case2.of(1, Direction.UP, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_1"))
                    )),
                            WeightedCrossModelTrait.Case2.of(1, Direction.DOWN, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_1")).rotated(180, 0)
                    )),
                            WeightedCrossModelTrait.Case2.of(1, Direction.NORTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_1")).rotated(90, 0)
                    )),
                            WeightedCrossModelTrait.Case2.of(1, Direction.SOUTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_1")).rotated(90, 180)
                    )),
                            WeightedCrossModelTrait.Case2.of(1, Direction.EAST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_1")).rotated(90, 90)
                    )),
                            WeightedCrossModelTrait.Case2.of(1, Direction.WEST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_1")).rotated(90, 270)
                    )),
                            WeightedCrossModelTrait.Case2.of(2, Direction.UP, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_2"))
                    )),
                            WeightedCrossModelTrait.Case2.of(2, Direction.DOWN, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_2")).rotated(180, 0)
                    )),
                            WeightedCrossModelTrait.Case2.of(2, Direction.NORTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_2")).rotated(90, 0)
                    )),
                            WeightedCrossModelTrait.Case2.of(2, Direction.SOUTH, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_2")).rotated(90, 180)
                    )),
                            WeightedCrossModelTrait.Case2.of(2, Direction.EAST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_2")).rotated(90, 90)
                    )),
                            WeightedCrossModelTrait.Case2.of(2, Direction.WEST, List.of(
                            WeightedCrossModelTrait.cross(BetterEnd.C.mk("block/sulphur_crystal_2")).rotated(90, 270)
                    ))
                    ),
                    WeightedCrossModelTrait.Item.flat(BetterEnd.C.mk("block/sulphur_crystal_2"))))
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
            .noCollission()
            .buildAndRegister();

    public static final Block MISSING_TILE = EndBlocks.defineBlock("missing_tile", Block::new)
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

    public static final Block FLAVOLITE_RUNED = EndBlocks.defineBlock("flavolite_runed", RunedFlavolite::new)
            .replacePropertiesWithCopy(EndStoneBlocks.FLAVOLITE.getBlock(SlotType.POLISHED))
            .strength(1, Blocks.OBSIDIAN.getExplosionResistance())
            .lightLevel(state -> state.getValue(RunedFlavolite.ACTIVATED) ? 8 : 0)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(EndModelTraits.flavoliteRuned())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block FLAVOLITE_RUNED_ETERNAL = EndBlocks.defineBlock("flavolite_runed_eternal", RunedFlavolite::new)
            .replacePropertiesWithCopy(EndStoneBlocks.FLAVOLITE.getBlock(SlotType.POLISHED))
            .strength(-11, Blocks.BEDROCK.getExplosionResistance())
            .lightLevel(state -> state.getValue(RunedFlavolite.ACTIVATED) ? 8 : 0)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(EndModelTraits.flavoliteRuned())
            // No LOOT_TABLE trait: the negative strength above makes this variant unbreakable, so it is not
            // obtainable and a block with no table drops nothing. RunedFlavolite used to say that with a
            // getDrops override testing its own hardness - but the two variants are separate registrations,
            // so the distinction belongs here rather than in code both of them share.
            .buildAndRegister();

    public static final Block HYDROTHERMAL_VENT = EndBlocks.defineBlock("hydrothermal_vent", HydrothermalVentBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .sound(SoundType.STONE)
            .noCollission()
            .requiresCorrectToolForDrops()
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block VENT_BUBBLE_COLUMN = EndBlocks.defineBlockOnly("vent_bubble_column", VentBubbleColumnBlock::new)
            .replacePropertiesWithCopy(Blocks.BUBBLE_COLUMN)
            .noOcclusion().noCollission().noLootTable()
            .buildAndRegister();

    public static final Block END_STONE_STALACTITE = EndBlocks.defineBlock("end_stone_stalactite", StalactiteBlock::new)
            .addTrait(StalactiteBlockTrait.withSource(Blocks.END_STONE))
            .buildAndRegister();

    public static final Block END_STONE_STALACTITE_CAVEMOSS = EndBlocks.defineBlock(
            "end_stone_stalactite_cavemoss",
            StalactiteBlock::new
    ).addTrait(StalactiteBlockTrait.withSource(EndTerrainBlocks.CAVE_MOSS))
     .buildAndRegister();

    // Variations
    public static final VanillaVariantStoneMaterial END_STONE_BRICK_VARIATIONS =
            new VanillaVariantStoneMaterial(
                    "end_stone_brick",
                    Blocks.END_STONE_BRICKS,
                    MapColor.SAND
            );

    public static final Block END_STONE_SLAB = EndBlocks.defineBlock("end_stone_slab", SlabBlock::new)
            .replacePropertiesWithCopy(Blocks.END_STONE)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.SLAB_BLOCK)
            .addTrait(ModelTraitLibrary.slab(() -> Blocks.END_STONE))
            .addTrait(RecipeTraitLibrary.slab(RecipeMaterial.of(Blocks.END_STONE)))
            .buildAndRegister();

    public static final Block END_STONE_STAIR = EndBlocks.defineBlock(
            "end_stone_stairs",
            props -> new StairBlock(Blocks.END_STONE.defaultBlockState(), props)
    )
            .replacePropertiesWithCopy(Blocks.END_STONE)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.STAIR_BLOCK)
            .addTrait(ModelTraitLibrary.stairs(() -> Blocks.END_STONE))
            .addTrait(RecipeTraitLibrary.stairs(RecipeMaterial.of(Blocks.END_STONE)))
            .buildAndRegister();

    public static final Block END_STONE_WALLS = EndBlocks.defineBlock("end_stone_wall", WallBlock::new)
            .replacePropertiesWithCopy(Blocks.END_STONE)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTrait(BlockTraits.WALL_BLOCK)
            .addTrait(ModelTraitLibrary.wall(() -> Blocks.END_STONE))
            .addTrait(RecipeTraitLibrary.wall(RecipeMaterial.of(Blocks.END_STONE)))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
