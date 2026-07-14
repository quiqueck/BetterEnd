package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourWaterPlant;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.basis.EndUnderwaterPlantBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnEndStone;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EndLilyBlock extends EndUnderwaterPlantBlock implements BehaviourWaterPlant, SurvivesOnEndStone {
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
    private static final VoxelShape SHAPE_BOTTOM = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_TOP = Block.box(2, 0, 2, 14, 6, 14);

    public EndLilyBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected @NotNull BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction neighborDirection,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (!canSurvive(state, level, pos)) {
            return state.getValue(SHAPE) == TripleShape.TOP
                    ? Blocks.AIR.defaultBlockState()
                    : Blocks.WATER.defaultBlockState();
        } else {
            return state;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(pos);
        VoxelShape shape = state.getValue(SHAPE) == TripleShape.TOP ? SHAPE_TOP : SHAPE_BOTTOM;
        return shape.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(SHAPE) == TripleShape.TOP ? Fluids.EMPTY.defaultFluidState() : Fluids.WATER.getSource(
                false);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(SHAPE) == TripleShape.TOP) {
            return world.getBlockState(pos.below()).getBlock() == this;
        } else if (state.getValue(SHAPE) == TripleShape.BOTTOM) {
            return isTerrain(world.getBlockState(pos.below()));
        } else {
            BlockState up = world.getBlockState(pos.above());
            BlockState down = world.getBlockState(pos.below());
            return up.getBlock() == this && down.getBlock() == this;
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(SHAPE) == TripleShape.TOP) {
            return Lists.newArrayList(
                    new ItemStack(EndItems.END_LILY_LEAF, MHelper.randRange(1, 2, MHelper.RANDOM_SOURCE)),
                    new ItemStack(EndBlocks.END_LILY_SEED, MHelper.randRange(1, 2, MHelper.RANDOM_SOURCE))
            );
        }
        return Collections.emptyList();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        return new ItemStack(EndBlocks.END_LILY_SEED);
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

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnEndStone.super.isTerrain(state);
    }
}
