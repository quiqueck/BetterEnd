package org.betterx.betterend.blocks.basis;

import org.betterx.wover.block.api.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Age-tracking variant of {@link EndPlantBlock}: bonemeal advances {@link #AGE} up to 3, then calls
 * {@link #growAdult} to spawn the adult plant. Reproduces the former BCLib {@code BasePlantWithAgeBlock}
 * on top of the vanilla-based {@link EndPlantBlock}.
 */
public abstract class EndPlantWithAgeBlock extends EndPlantBlock {
    public static final IntegerProperty AGE = BlockProperties.AGE;

    protected EndPlantWithAgeBlock(Properties settings) {
        super(settings.randomTicks());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(AGE);
    }

    public abstract void growAdult(WorldGenLevel world, RandomSource random, BlockPos pos);

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < 3) {
            level.setBlockAndUpdate(pos, state.setValue(AGE, age + 1));
        } else {
            growAdult(level, random, pos);
        }
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (random.nextInt(8) == 0) {
            performBonemeal(world, random, pos, state);
        }
    }
}
