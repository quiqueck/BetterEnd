package org.betterx.betterend.blocks;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.BasePlantBlock;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.NotNull;

public class LargeAmaranitaBlock extends BasePlantBlock {
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
    private static final VoxelShape SHAPE_BOTTOM = Block.box(4, 0, 4, 12, 14, 12);
    private static final VoxelShape SHAPE_TOP = Shapes.or(Block.box(1, 3, 1, 15, 16, 15), SHAPE_BOTTOM);

    public LargeAmaranitaBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, TripleShape.TOP));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == TripleShape.TOP ? SHAPE_TOP : SHAPE_BOTTOM;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.below());
        if (below.is(this)) {
            return true;
        }
        return SurvivesOnBlockTrait.survivesOn(this, below);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelReader level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        boolean hasThisBelow = level.getBlockState(pos.below()).is(this);
        boolean hasThisAbove = level.getBlockState(pos.above()).is(this);

        TripleShape shape;
        if (hasThisBelow && hasThisAbove) {
            shape = TripleShape.MIDDLE;
        } else if (hasThisBelow) {
            shape = TripleShape.TOP;
        } else if (hasThisAbove) {
            shape = TripleShape.BOTTOM;
        } else {
            shape = TripleShape.TOP;
        }
        return this.defaultBlockState().setValue(SHAPE, shape);
    }

    @Override
    protected @NotNull BlockState updateShape(
            BlockState state,
            LevelReader level,
            net.minecraft.world.level.ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            net.minecraft.core.Direction neighborDirection,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (!canSurvive(state, level, pos)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }

        boolean hasThisBelow = level.getBlockState(pos.below()).is(this);
        boolean hasThisAbove = level.getBlockState(pos.above()).is(this);

        TripleShape shape;
        if (hasThisBelow && hasThisAbove) {
            shape = TripleShape.MIDDLE;
        } else if (hasThisBelow) {
            shape = TripleShape.TOP;
        } else if (hasThisAbove) {
            shape = TripleShape.BOTTOM;
        } else {
            shape = TripleShape.TOP;
        }

        return state.setValue(SHAPE, shape);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }
}
