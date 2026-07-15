package org.betterx.betterend.blocks.basis;

import org.betterx.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Age-tracking underwater seed: bonemeal advances {@link #AGE} up to 3, then calls {@link #grow} to spawn
 * the adult plant. Reproduces the former BCLib {@code UnderwaterPlantWithAgeBlock} on the vanilla-based
 * {@link EndUnderwaterPlantBlock}.
 */
public abstract class EndUnderwaterPlantWithAgeBlock extends EndUnderwaterPlantBlock {
    public static final IntegerProperty AGE = BlockProperties.AGE;

    public EndUnderwaterPlantWithAgeBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(AGE);
    }

    public abstract void grow(WorldGenLevel world, RandomSource random, BlockPos pos);

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        if (random.nextInt(4) == 0) {
            int age = state.getValue(AGE);
            if (age < 3) {
                world.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
            } else {
                grow(world, random, pos);
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (isBonemealSuccess(world, random, pos, state)) {
            performBonemeal(world, random, pos, state);
        }
    }
}
