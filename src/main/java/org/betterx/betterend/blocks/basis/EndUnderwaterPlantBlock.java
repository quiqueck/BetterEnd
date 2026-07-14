package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Submerged ground plant: survives on valid ground while water-logged, and schedules its own removal when
 * the water drains. Reproduces the former BCLib {@code UnderwaterPlantBlock} on vanilla {@link Block}.
 * <p>
 * The ground check defaults to the block's {@code SurvivesOnBlockTrait} ({@link #isValidGround}); subclasses
 * with a non-tag-expressible rule (e.g. "any solid") override {@link #isValidGround}. Bonemeal drops a copy.
 */
public class EndUnderwaterPlantBlock extends Block implements BonemealableBlock, LiquidBlockContainer {
    private static final VoxelShape SHAPE = box(4, 0, 4, 12, 14, 12);

    public EndUnderwaterPlantBlock(Properties settings) {
        super(settings);
    }

    /**
     * @return whether {@code state} (the block below) is valid ground for this plant. Defaults to the
     * block's {@link SurvivesOnBlockTrait}.
     */
    protected boolean isValidGround(BlockState state) {
        return SurvivesOnBlockTrait.survivesOn(this, state);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(pos);
        return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.below());
        return isValidGround(down)
                && world.getBlockState(pos).getFluidState().getType().equals(Fluids.WATER.getSource());
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource randomSource) {
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, true);
        }
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
            scheduledTickAccess.scheduleTick(pos, this, 1);
        }
        return state;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        ItemEntity item = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                new ItemStack(this)
        );
        level.addFreshEntity(item);
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
    protected @NotNull FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getSource(false);
    }
}
