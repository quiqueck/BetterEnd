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

public class EndMetalBlocks {
    // Vanilla Metal Sets
    public static final VanillaMetalSet IRON_SET = new VanillaMetalSet(
            "iron", Blocks.IRON_BLOCK, Items.IRON_INGOT,
            SlotMap.of(Chandelier.SLOT, BulbLantern.SLOT)
    );

    public static final VanillaMetalSet GOLD_SET = new VanillaMetalSet(
            "gold", Blocks.GOLD_BLOCK, Items.GOLD_INGOT,
            SlotMap.of(Chandelier.SLOT)
    );

    // TERMINITE needs to be defined before any block whose constructor references EndItems
    // (e.g. EndCropBlocks.SHADOW_BERRY below), since that would trigger EndItems.<clinit> -> AeterniumSet,
    // which reads EndMetalBlocks.TERMINITE - and it must already be assigned by then.
    public static final MetalMaterial TERMINITE = MetalMaterial.makeOreless(
            "terminite",
            MapColor.WARPED_WART_BLOCK,
            7F,
            9F,
            EndToolTier.TERMINITE,
            EndArmorTier.TERMINITE,
            EndTags.ANVIL_DIAMOND_TOOL,
            () -> EndTemplates.TERMINITE_UPGRADE,
            BlockTags.NEEDS_IRON_TOOL
    );

    public static final MetalMaterial THALLASIUM = MetalMaterial.makeNormal(
            "thallasium",
            MapColor.COLOR_BLUE,
            EndToolTier.THALLASIUM,
            EndArmorTier.THALLASIUM,
            EndTags.ANVIL_IRON_TOOL,
            () -> EndTemplates.THALLASIUM_UPGRADE,
            BlockTags.NEEDS_STONE_TOOL
    );

    public static final Block AETERNIUM_BLOCK = EndBlocks.defineBlock("aeternium_block", AeterniumBlock::new)
            .addTrait(BlockTraits.METAL_BLOCK)
            .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
            .addTags(BlockTags.NEEDS_DIAMOND_TOOL)
            .addTrait(ModelTraitLibrary.cube())
            .mapColor(MapColor.COLOR_GRAY)
            .strength(65F, 1200F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.NETHERITE_BLOCK)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .withBlockItem((def, block) -> new BlockItemDefinition<>(def, id -> new BlockItem(block, id.getProperties().fireResistant())))
            .buildAndRegister();

    public static final Block CHARCOAL_BLOCK = EndBlocks.defineBlock("charcoal_block", CharcoalBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block ENDER_BLOCK = EndBlocks.defineBlock("ender_block", EnderBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .mapColor(MapColor.WARPED_WART_BLOCK)
            .strength(5F, 6F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final Block AMBER_BLOCK = EndBlocks.defineBlock("amber_block", AmberBlock::new)
            .addTrait(BlockTraits.STONE_BLOCK)
            .addTrait(ModelTraitLibrary.cube())
            .mapColor(MapColor.COLOR_YELLOW)
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static void ensureLoaded() {}
}
