package org.betterx.betterend.mixin.common;

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChorusPlantBlock.class, priority = 100)
public abstract class ChorusPlantBlockMixin extends Block {
    public ChorusPlantBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("RETURN"), cancellable = true)
    private void be_getStateForPlacement(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> info) {
        BlockPos pos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        BlockState plant = info.getReturnValue();
        if (ctx.canPlace() && plant.is(Blocks.CHORUS_PLANT) && world.getBlockState(pos.below())
                                                                    .is(CommonBlockTags.END_STONES)) {
            info.setReturnValue(plant.setValue(BlockStateProperties.DOWN, true));
        }
    }

    @Inject(method = "getStateWithConnections", at = @At("RETURN"), cancellable = true)
    private static void be_getStateForPlacement(
            BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<BlockState> info
    ) {
        BlockState plant = info.getReturnValue();
        if (plant.is(Blocks.CHORUS_PLANT) && blockGetter.getBlockState(blockPos.below())
                                                        .is(CommonBlockTags.END_STONES)) {
            info.setReturnValue(plant.setValue(BlockStateProperties.DOWN, true));
        }
    }

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void be_canSurvive(
            BlockState state,
            LevelReader world,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> info
    ) {
        BlockState down = world.getBlockState(pos.below());
        if (down.is(EndBlocks.CHORUS_NYLIUM) || down.is(Blocks.END_STONE)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void be_updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos posFrom,
            BlockState newState,
            RandomSource randomSource,
            CallbackInfoReturnable<BlockState> info
    ) {
        BlockState plant = info.getReturnValue();
        if (plant.is(Blocks.CHORUS_PLANT) && world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) {
            plant = plant.setValue(BlockStateProperties.DOWN, true);
            info.setReturnValue(plant);
        }
    }
}
