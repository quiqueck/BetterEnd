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
import org.betterx.betterend.client.render.EndTinterKeys;
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
import de.ambertation.wover.block.api.render.TinterKeys;
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

public class EndFunctionalBlocks {
    // Mob-Related
    public static final Block SILK_MOTH_NEST = EndBlocks.defineBlock("silk_moth_nest", SilkMothNestBlock::new)
            .strength(0.5F, 0.1F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .randomTicks()
            .addTrait(BlockTraits.MINEABLE_WITH.needsShears())
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block SILK_MOTH_HIVE = EndBlocks.defineBlock("silk_moth_hive", SilkMothHiveBlock::new)
            .strength(0.5F, 0.1F)
            .sound(SoundType.WOOL)
            .noOcclusion()
            .randomTicks()
            .addTrait(BlockTraits.MINEABLE_WITH.needsAxe())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block RESPAWN_OBELISK = EndBlocks.defineBlock("respawn_obelisk", RespawnObeliskBlock::new)
            .lightLevel(
                    state -> state.getValue(BlockProperties.TRIPLE_SHAPE) == BlockProperties.TripleShape.BOTTOM ? 0 : 15
            )
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
            // Shares AURORA_CRYSTAL's palette instance - this block used to express that by delegating to its
            // provider. World only: the obelisk's item has its own coloured texture.
            .addTrait(ClientBlockTraits.TINT.world(TinterKeys.PALETTE_CYCLE, () -> EndCrystalBlocks.AURORA_PALETTE))
            .addTrait(ModelTraitLibrary.externalModel())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block END_STONE_SMELTER = EndBlocks.defineBlock("end_stone_smelter", EndStoneSmelter::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            // Chained after STONE_BLOCK on purpose: chain setters and trait-configured properties
            // interleave by call order (BlockDefinition.build()), so strength() here must come after the
            // trait to win over its strength(2, 6) default.
            .mapColor(MapColor.COLOR_YELLOW)
            .strength(4F, 100F)
            .lightLevel(state -> state.getValue(EndStoneSmelter.LIT) ? 15 : 0)
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    public static final Block ETERNAL_PEDESTAL = EndBlocks.getBlockRegistry()
            .defineDefaultBlock("eternal_pedestal", def -> new EternalPedestal(def.blockKey))
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> ((EternalPedestal) block).provideBlockModelsInstance(generator)
            ) : null)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTags(EndTags.PEDESTALS)
            .buildAndRegister();

    public static final Block INFUSION_PEDESTAL = EndBlocks.getBlockRegistry()
            .defineDefaultBlock("infusion_pedestal", def -> new InfusionPedestal(def.blockKey))
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> ((InfusionPedestal) block).provideBlockModelsInstance(generator)
            ) : null)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .addTags(EndTags.PEDESTALS)
            .buildAndRegister();

    public static final Block AETERNIUM_ANVIL = EndBlocks.defineBlock("aeternium_anvil", AeterniumAnvil::new)
            .addTrait(BlockTraits.METAL_BLOCK)
            .addTags(BlockTags.NEEDS_DIAMOND_TOOL)
            .mapColor(EndMetalBlocks.AETERNIUM_BLOCK.defaultMapColor())
            .strength(5.0F, 1200.0F)
            .sound(SoundType.ANVIL)
            .pushReaction(PushReaction.BLOCK)
            .addTrait(ModCore.isDatagen() ? LeveledAnvilBlock.buildModel(null, null) : null)
            .withBlockItem((def, block) -> new BlockItemDefinition<>(def, id -> new BaseAnvilItem(block, id.getProperties().fireResistant())))
            .buildAndRegister();

    // Technical
    public static final Block END_PORTAL_BLOCK = EndBlocks.defineBlockOnly("end_portal_block", EndPortalBlock::new)
            .lightLevel(bs -> 15)
            .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
            // World only - defineBlockOnly, so there is no item to tint. The colour comes from the runtime
            // EndPortals config, which is why this uses BetterEnd's own key rather than a wover built-in.
            .addTrait(ClientBlockTraits.TINT.world(EndTinterKeys.END_PORTAL, () -> null))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
