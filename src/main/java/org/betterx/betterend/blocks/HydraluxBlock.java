package org.betterx.betterend.blocks;

import org.betterx.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.behaviours.interfaces.BehaviourWaterPlant;
import org.betterx.bclib.blocks.UnderwaterPlantBlock;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.EndBlockProperties.HydraluxShape;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class HydraluxBlock extends UnderwaterPlantBlock implements BehaviourWaterPlant {

    public static final EnumProperty<HydraluxShape> SHAPE = EndBlockProperties.HYDRALUX_SHAPE;

    public HydraluxBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.below());
        HydraluxShape shape = state.getValue(SHAPE);
        if (shape == HydraluxShape.FLOWER_BIG_TOP || shape == HydraluxShape.FLOWER_SMALL_TOP) {
            return down.is(this);
        } else if (shape == HydraluxShape.ROOTS) {
            return down.is(EndBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE)) && world.getBlockState(pos.above()).is(this);
        } else {
            return down.is(this) && world.getBlockState(pos.above()).is(this);
        }
    }

    @Override
    protected boolean isTerrain(BlockState state) {
        return state.is(CommonBlockTags.END_STONES);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        return new ItemStack(EndBlocks.HYDRALUX_SAPLING);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        HydraluxShape shape = state.getValue(SHAPE);
        if (shape == HydraluxShape.FLOWER_BIG_BOTTOM || shape == HydraluxShape.FLOWER_SMALL_BOTTOM) {
            return Lists.newArrayList(new ItemStack(
                    EndItems.HYDRALUX_PETAL,
                    MHelper.randRange(1, 4, MHelper.RANDOM_SOURCE)
            ));
        } else if (shape == HydraluxShape.ROOTS) {
            return Lists.newArrayList(new ItemStack(
                    EndBlocks.HYDRALUX_SAPLING,
                    MHelper.randRange(1, 2, MHelper.RANDOM_SOURCE)
            ));
        }
        return Collections.emptyList();
    }

    @Override
    public LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    ) {
        // Has no item (block-only) and drops are already fully handled by the getDrops()
        // override above - the base class's dropWithSilkTouch(this) assumes an item exists,
        // which no longer holds here.
        return LootTable.lootTable();
    }
}
