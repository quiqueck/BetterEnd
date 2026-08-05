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

public class EndDecorBlocks {
    public static final Block DENSE_SNOW = EndBlocks.defineBlock("dense_snow", Block::new)
            .addTrait(SnowBlockTrait.withDefault())
            .buildAndRegister();

    public static final Block EMERALD_ICE = EndBlocks.defineBlock("emerald_ice", EmeraldIceBlock::new)
            .addTrait(IceBlockTrait.withBase(Blocks.ICE))
            .randomTicks()
            .buildAndRegister();

    public static final Block DENSE_EMERALD_ICE = EndBlocks.defineBlock("dense_emerald_ice", Block::new)
            .addTrait(IceBlockTrait.withBase(Blocks.PACKED_ICE))
            .buildAndRegister();

    public static final Block ANCIENT_EMERALD_ICE = EndBlocks.defineBlock("ancient_emerald_ice", AncientEmeraldIceBlock::new)
            .addTrait(IceBlockTrait.withBase(Blocks.BLUE_ICE))
            .randomTicks()
            .buildAndRegister();

    public static final Block NEON_CACTUS_BLOCK = EndBlocks.defineBlock("neon_cactus_block", RotatedPillarBlock::new)
            .lightLevel(bs -> 15)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.pillar())
            .buildAndRegister();

    public static final Block NEON_CACTUS_BLOCK_STAIRS = EndBlocks.defineBlock(
            "neon_cactus_stairs",
            props -> new StairBlock(NEON_CACTUS_BLOCK.defaultBlockState(), props)
    )
            .addTrait(BlockTraits.STAIR_BLOCK)
            .addTrait(EndModelTraits.litStairs(() -> BetterEnd.C.mk("block/neon_cactus_block")))
            .addTrait(RecipeTraitLibrary.stairs(RecipeMaterial.of(NEON_CACTUS_BLOCK)))
            .buildAndRegister();

    public static final Block NEON_CACTUS_BLOCK_SLAB = EndBlocks.defineBlock("neon_cactus_slab", SlabBlock::new)
            .addTrait(BlockTraits.SLAB_BLOCK)
            .addTrait(EndModelTraits.slabFrom(() -> NEON_CACTUS_BLOCK, () -> BetterEnd.C.mk("block/neon_cactus_block")))
            .addTrait(RecipeTraitLibrary.slab(RecipeMaterial.of(NEON_CACTUS_BLOCK)))
            .buildAndRegister();

    public static final Block MENGER_SPONGE = EndBlocks.defineBlock("menger_sponge", MengerSpongeBlock::new)
            .noOcclusion()
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .buildAndRegister();

    public static final Block MENGER_SPONGE_WET = EndBlocks.defineBlock("menger_sponge_wet", MengerSpongeWetBlock::new)
            .noOcclusion()
            .addTrait(BlockTraits.MINEABLE_WITH.needsHoe())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            // A pure texture swap of the bespoke menger_sponge fractal mesh: generate its child model
            // (parent=menger_sponge, particle+texture from this block's texture), single-variant blockstate and
            // delegated item from the kept template parent instead of hand-authoring them.
            .addTrait(TemplateModelTrait.cube(BetterEnd.C.mk("block/menger_sponge"), true))
            .buildAndRegister();

    public static final Block HYDRALUX_PETAL_BLOCK = EndBlocks.defineBlock("hydralux_petal_block", HydraluxPetalBlock::new)
            .addTrait(PlantBlockTrait.compostableWithColor(MapColor.PODZOL, true, false, OffsetType.NONE))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .addTrait(ModelTraitLibrary.cube())
            .strength(1.0f, 1.0f)
            .sound(SoundType.WART_BLOCK)
            .buildAndRegister();

    public static final ColoredMaterial HYDRALUX_PETAL_BLOCK_COLORED = new ColoredMaterial(
            HydraluxPetalColoredBlock::new,
            HYDRALUX_PETAL_BLOCK,
            true,
             (def) -> def.addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> HydraluxPetalColoredBlock.provideBlockModel(generator, block)
            ) : null).addTrait(PlantLikeBlockTrait.withDefault()).addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
    );

    public static void ensureLoaded() {}
}
