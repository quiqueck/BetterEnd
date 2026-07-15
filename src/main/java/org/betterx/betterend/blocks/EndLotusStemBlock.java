package org.betterx.betterend.blocks;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.Maps;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class EndLotusStemBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LEAF = BooleanProperty.create("leaf");
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
    private static final Map<Axis, VoxelShape> SHAPES = Maps.newEnumMap(Axis.class);

    public EndLotusStemBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false)
                                                     .setValue(SHAPE, TripleShape.MIDDLE)
                                                     .setValue(LEAF, false)
                                                     .setValue(FACING, Direction.UP));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(LEAF) ? SHAPES.get(Axis.Y) : SHAPES.get(state.getValue(FACING).getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, SHAPE, LEAF);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelAccessor worldAccess = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        return this.defaultBlockState()
                   .setValue(WATERLOGGED, worldAccess.getFluidState(blockPos).getType() == Fluids.WATER)
                   .setValue(FACING, ctx.getClickedFace());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return BlocksHelper.rotateHorizontal(state, rotation, FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return BlocksHelper.mirrorHorizontal(state, mirror, FACING);
    }

    @Override
    protected @NotNull BlockState updateShape(
            BlockState state,
            @NotNull LevelReader levelReader,
            @NotNull ScheduledTickAccess scheduledTickAccess,
            @NotNull BlockPos pos,
            @NotNull Direction direction,
            @NotNull BlockPos blockPos2,
            @NotNull BlockState blockState2,
            @NotNull RandomSource randomSource
    ) {

        if (state.getValue(WATERLOGGED) && levelReader instanceof ServerLevelAccessor sla) {
            sla.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        return state;
    }

    static {
        SHAPES.put(Axis.X, Block.box(0, 6, 6, 16, 10, 10));
        SHAPES.put(Axis.Y, Block.box(6, 0, 6, 10, 16, 10));
        SHAPES.put(Axis.Z, Block.box(6, 6, 0, 10, 10, 16));
    }
}
