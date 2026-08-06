package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.Lists;

import java.util.Queue;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class MengerSpongeBlock extends Block {
    public static final VoxelShape SHAPE;

    public MengerSpongeBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (absorbWater(world, pos)) {
            world.setBlockAndUpdate(pos, EndDecorBlocks.MENGER_SPONGE_WET.defaultBlockState());
        }

    }

    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (world instanceof LevelAccessor levelAccessor && absorbWater(levelAccessor, pos)) {
            return EndDecorBlocks.MENGER_SPONGE_WET.defaultBlockState();
        }
        return state;
    }

    private boolean absorbWater(LevelAccessor world, BlockPos pos) {
        // 26.2 deleted net.minecraft.util.Tuple; DFU's Pair is the same two-slot carrier.
        Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(Pair.of(pos, 0));
        int i = 0;

        while (!queue.isEmpty()) {
            Pair<BlockPos, Integer> pair = queue.poll();
            BlockPos blockPos = pair.getFirst();
            int j = pair.getSecond();

            for (Direction direction : Direction.values()) {
                BlockPos blockPos2 = blockPos.relative(direction);
                BlockState blockState = world.getBlockState(blockPos2);
                FluidState fluidState = world.getFluidState(blockPos2);
                if (fluidState.is(FluidTags.WATER)) {
                    if (blockState.getBlock() instanceof BucketPickup
                            && !((BucketPickup) blockState.getBlock())
                            .pickupBlock(null, world, blockPos2, blockState)
                            .isEmpty()
                    ) {
                        ++i;
                        if (j < 6) {
                            queue.add(Pair.of(blockPos2, j + 1));
                        }
                    } else if (blockState.getBlock() instanceof LiquidBlock) {
                        world.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(Pair.of(blockPos2, j + 1));
                        }
                    } else if (blockState.is(CommonBlockTags.WATER_PLANT)) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
                        dropResources(blockState, world, blockPos2, blockEntity);
                        world.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(Pair.of(blockPos2, j + 1));
                        }
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }

    @Override
    public @NotNull VoxelShape getShape(
            BlockState blockState,
            BlockGetter blockGetter,
            BlockPos blockPos,
            CollisionContext collisionContext
    ) {
        return SHAPE;
    }

    static {
        SHAPE = Shapes.or(
                Shapes.or(box(0, 0, 0, 16, 6, 6), box(0, 0, 10, 16, 6, 16),
                        Shapes.or(box(0, 10, 0, 16, 16, 6), box(0, 10, 10, 16, 16, 16)),

                        Shapes.or(box(0, 0, 0, 6, 6, 16), box(10, 0, 0, 16, 6, 16)),
                        Shapes.or(box(0, 10, 0, 6, 16, 16), box(10, 10, 0, 16, 16, 16)),

                        Shapes.or(box(0, 0, 0, 6, 16, 6), box(10, 0, 0, 16, 16, 6)),
                        Shapes.or(box(0, 0, 10, 6, 16, 16), box(10, 0, 10, 16, 16, 16))
                ));
    }
}
