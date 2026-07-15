package org.betterx.betterend.blocks.basis;

import org.betterx.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import org.jetbrains.annotations.Nullable;

public abstract class EndLanternBlock extends EndBlockNotFull implements SimpleWaterloggedBlock, LiquidBlockContainer {
    public static final BooleanProperty IS_FLOOR = BlockProperties.IS_FLOOR;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public EndLanternBlock(Block source) {
        this(BlockBehaviour.Properties.ofFullCopy(source).lightLevel((bs) -> 15).noOcclusion());
    }

    public EndLanternBlock(Properties settings) {
        super(settings.noOcclusion());
        this.registerDefaultState(getStateDefinition()
                .any()
                .setValue(IS_FLOOR, true)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(IS_FLOOR, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelReader worldView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        Direction dir = ctx.getClickedFace();
        boolean water = worldView.getFluidState(blockPos).getType() == Fluids.WATER;
        if (dir != Direction.DOWN && dir != Direction.UP) {
            if (canSupportCenter(worldView, blockPos.above(), Direction.DOWN)) {
                return defaultBlockState().setValue(IS_FLOOR, false).setValue(WATERLOGGED, water);
            } else if (canSupportCenter(worldView, blockPos.below(), Direction.UP)) {
                return defaultBlockState().setValue(IS_FLOOR, true).setValue(WATERLOGGED, water);
            } else {
                return null;
            }
        } else if (dir == Direction.DOWN) {
            if (canSupportCenter(worldView, blockPos.above(), Direction.DOWN)) {
                return defaultBlockState().setValue(IS_FLOOR, false).setValue(WATERLOGGED, water);
            } else if (canSupportCenter(worldView, blockPos.below(), Direction.UP)) {
                return defaultBlockState().setValue(IS_FLOOR, true).setValue(WATERLOGGED, water);
            } else {
                return null;
            }
        } else {
            if (canSupportCenter(worldView, blockPos.below(), Direction.UP)) {
                return defaultBlockState().setValue(IS_FLOOR, true).setValue(WATERLOGGED, water);
            } else if (canSupportCenter(worldView, blockPos.above(), Direction.DOWN)) {
                return defaultBlockState().setValue(IS_FLOOR, false).setValue(WATERLOGGED, water);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(IS_FLOOR)) {
            return canSupportCenter(world, pos.below(), Direction.UP);
        } else {
            return canSupportCenter(world, pos.above(), Direction.DOWN);
        }
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos blockPos2,
            BlockState blockState2,
            RandomSource randomSource
    ) {
        Boolean water = state.getValue(WATERLOGGED);
        if (water && level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (!canSurvive(state, level, pos)) {
            return water ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        } else {
            return state;
        }
    }

    @Override
    public boolean canPlaceLiquid(
            @Nullable LivingEntity livingEntity,
            BlockGetter blockGetter,
            BlockPos blockPos,
            BlockState blockState,
            Fluid fluid
    ) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

}
