package org.betterx.betterend.blocks.basis;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import org.jetbrains.annotations.Nullable;

/**
 * Underwater variant of {@link EndWallPlantBlock}: always water-logged and only survives while submerged.
 * Reproduces the former BCLib {@code BaseUnderwaterWallPlantBlock} on the vanilla-based wall plant.
 */
public class EndUnderwaterWallPlantBlock extends EndWallPlantBlock implements LiquidBlockContainer {
    public EndUnderwaterWallPlantBlock(BlockBehaviour.Properties props) {
        super(props);
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
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getSource(false);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getFluidState(pos).getType() == Fluids.WATER && super.canSurvive(state, level, pos);
    }
}
