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

public class EndOreBlocks {
    // ENDER_ORE / AMBER_ORE were BaseOreBlock + STONE_BLOCK: they took their 2/6 strength from
    // STONE_BLOCK, not from BaseOreBlock's 3/9 ctor defaults. Reparent to a plain DropExperienceBlock +
    // ORE_BLOCK (which supplies c:ores, the pickaxe tag and the ore loot trait) and chain strength(2,6)
    // AFTER the trait so the original 2/6 wins (WorldWeaver 5dcd992 call-order rule). END_STONES and
    // (for ender_ore) DRAGON_IMMUNE are added by BetterEnd's own BlockTagProvider, so they are unaffected.
    public static final Block ENDER_ORE = EndBlocks.defineBlock(
            "ender_ore",
            p -> new DropExperienceBlock(UniformInt.of(1, 5), p)
    )
            .addTrait(BlockTraits.ORE_BLOCK.dropping(() -> EndResourceItems.ENDER_SHARD, 1, 3))
            .addTags(BlockTags.NEEDS_STONE_TOOL)
            .addTrait(ModelTraitLibrary.cube())
            .strength(2, 6)
            .buildAndRegister();

    public static final Block AMBER_ORE = EndBlocks.defineBlock(
            "amber_ore",
            p -> new DropExperienceBlock(UniformInt.of(1, 4), p)
    )
            .addTrait(BlockTraits.ORE_BLOCK.dropping(() -> EndResourceItems.RAW_AMBER, 1, 2))
            .addTags(BlockTags.NEEDS_STONE_TOOL)
            .addTrait(ModelTraitLibrary.cube())
            .strength(2, 6)
            .buildAndRegister();

    public static void ensureLoaded() {}
}
