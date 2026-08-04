package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VentBubbleColumnBlock extends Block implements BucketPickup, LiquidBlockContainer {
    public VentBubbleColumnBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public @NotNull ItemStack pickupBlock(
            @Nullable LivingEntity player,
            LevelAccessor world,
            BlockPos pos,
            BlockState state
    ) {
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        return new ItemStack(Items.WATER_BUCKET);
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.below());
        return blockState.is(this) || blockState.is(EndStoneBlocks.HYDROTHERMAL_VENT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos posFrom,
            BlockState newState,
            RandomSource randomSource
    ) {
        if (!state.canSurvive(world, pos)) {
            return Blocks.WATER.defaultBlockState();
        } else {
            BlockPos up = pos.above();
            if (world.getBlockState(up).is(Blocks.WATER) && world instanceof LevelAccessor levelAccessor) {
                BlocksHelper.setWithoutUpdate(levelAccessor, up, this);
                scheduledTickAccess.scheduleTick(up, this, 5);
            }
        }
        return state;
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            double px = pos.getX() + random.nextDouble();
            double py = pos.getY() + random.nextDouble();
            double pz = pos.getZ() + random.nextDouble();
            world.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, px, py, pz, 0, 0.04, 0);
        }
        if (random.nextInt(200) == 0) {
            world.playLocalSound(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundSource.BLOCKS,
                    0.2F + random.nextFloat() * 0.2F,
                    0.9F + random.nextFloat() * 0.15F,
                    false
            );
        }
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier insideBlockEffectApplier) {
        BlockState blockState = world.getBlockState(pos.above());
        if (blockState.isAir()) {
            entity.onAboveBubbleColumn(false, pos);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel) world;

                for (int i = 0; i < 2; ++i) {
                    serverWorld.sendParticles(
                            ParticleTypes.SPLASH,
                            (double) pos.getX() + world.random.nextDouble(),
                            pos.getY() + 1,
                            (double) pos.getZ() + world.random.nextDouble(),
                            1,
                            0.0D,
                            0.0D,
                            0.0D,
                            1.0D
                    );
                    serverWorld.sendParticles(
                            ParticleTypes.BUBBLE,
                            (double) pos.getX() + world.random.nextDouble(),
                            pos.getY() + 1,
                            (double) pos.getZ() + world.random.nextDouble(),
                            1,
                            0.0D,
                            0.01D,
                            0.0D,
                            0.2D
                    );
                }
            }
        } else {
            entity.onInsideBubbleColumn(false);
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
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getSource(false);
    }


    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}
