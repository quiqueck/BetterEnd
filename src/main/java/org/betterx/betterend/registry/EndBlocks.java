package org.betterx.betterend.registry;

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
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.block.EndOreBlocks;
import org.betterx.betterend.registry.block.EndSaplingBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.registry.block.EndWallPlantBlocks;
import org.betterx.betterend.registry.block.EndCropBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.betterend.registry.block.EndLightBlocks;
import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndDecorBlocks;
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

/**
 * Registry facade for BetterEnd's blocks. The actual field declarations live in the per-category
 * classes under {@code org.betterx.betterend.registry.block} (see {@link #ensureStaticallyLoaded()}
 * for the boot order); this class only keeps the shared registration forwarders and the driver that
 * loads every category class in the correct order.
 */
public class EndBlocks {
    private static BlockRegistry BLOCKS_REGISTRY;

    public static List<Block> getModBlocks() {
        return getBlockRegistry().allBlocks().toList();
    }

    @SafeVarargs
    public static <T extends Block> T registerBlock(
            String name,
            Function<BlockBehaviour.Properties, T> blockF,
            TagKey<Block>... tags
    ) {
        return defineBlock(name, blockF)
                .addTags(tags)
                .buildAndRegister();
    }

    public static <T extends Block> DefaultBlockDefinition<T> defineBlock(
            String name,
            Function<BlockBehaviour.Properties, T> blockF
    ) {
        return getBlockRegistry()
                .defineDefaultBlock(name, def -> blockF.apply(def.getProperties()));
    }

    public static VanillaBlockDefinition defineEndBlock(String name) {
        return getBlockRegistry()
                .defineDefaultBlock(name);
    }

    public static <T extends Block> DefaultBlockDefinition<T> defineBlockOnly(
            String name,
            Function<BlockBehaviour.Properties, T> blockF
    ) {
        return defineBlock(name, blockF).withBlockItem((def, block) -> null);
    }

    public static Block registerEndBlockOnly(String name, Function<BlockBehaviour.Properties, Block> blockF) {
        return defineBlockOnly(name, blockF)
                .buildAndRegister();
    }

    @NotNull
    public static BlockRegistry getBlockRegistry() {
        if (BLOCKS_REGISTRY == null) {
            BLOCKS_REGISTRY = BlockRegistry.forMod(BetterEnd.C);
        }
        return BLOCKS_REGISTRY;
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        EndTerrainBlocks.ensureLoaded();
        EndStoneBlocks.ensureLoaded();
        EndMetalBlocks.ensureLoaded();
        EndOreBlocks.ensureLoaded();
        EndSaplingBlocks.ensureLoaded();   // before Wood (registry-split-map.md Resolution 1)
        EndWoodBlocks.ensureLoaded();
        EndPlantBlocks.ensureLoaded();
        EndWaterPlantBlocks.ensureLoaded();
        EndWallPlantBlocks.ensureLoaded();
        EndCropBlocks.ensureLoaded();
        EndVineBlocks.ensureLoaded();
        EndMushroomBlocks.ensureLoaded();
        EndLightBlocks.ensureLoaded();
        EndCrystalBlocks.ensureLoaded();
        EndFunctionalBlocks.ensureLoaded();
        EndDecorBlocks.ensureLoaded();
    }
}
