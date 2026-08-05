package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

/**
 * Drops nothing, like vanilla's budding amethyst: the block is the shard source, not a resource. That is
 * expressed by registering no {@code LOOT_TABLE} trait at all - a block with no table drops nothing - rather
 * than by the {@code getDrops} override this used to carry, which bypassed the loot table entirely and
 * silently contradicted the {@code dropSelf} table that was being generated alongside it.
 */
public class BuddingSmaragdantCrystalBlock extends RotatedPillarBlock {
    public BuddingSmaragdantCrystalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState blockState, ServerLevel world, BlockPos pos, RandomSource random) {
        Direction dir = BlocksHelper.randomDirection(random);
        BlockPos side = pos.relative(dir);
        BlockState sideState = world.getBlockState(side);
        if (random.nextInt(20) == 0) {
            if (canShardGrowAtState(sideState)) {
                BlockState shard = EndCrystalBlocks.SMARAGDANT_CRYSTAL_SHARD.defaultBlockState()
                                                                     .setValue(
                                                                             SmaragdantCrystalShardBlock.WATERLOGGED,
                                                                             sideState.getFluidState()
                                                                                      .getType() == Fluids.WATER
                                                                     )
                                                                     .setValue(SmaragdantCrystalShardBlock.FACING, dir);
                world.setBlockAndUpdate(side, shard);
            }
        }
    }

    public static boolean canShardGrowAtState(BlockState blockState) {
        return blockState.isAir() || blockState.is(Blocks.WATER) && blockState.getFluidState().getAmount() == 8;
    }
}
