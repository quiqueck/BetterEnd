package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class MengerSpongeFeature extends UnderwaterPlantScatter<ScatterFeatureConfig> {
    private static final Function<BlockState, Boolean> REPLACE;

    public MengerSpongeFeature() {
        super(ScatterFeatureConfig.CODEC);
    }

    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        BlocksHelper.setWithoutUpdate(world, blockPos, EndDecorBlocks.MENGER_SPONGE_WET);
        if (random.nextBoolean()) {
            for (Direction dir : BlocksHelper.DIRECTIONS) {
                BlockPos pos = blockPos.relative(dir);
                if (REPLACE.apply(world.getBlockState(pos))) {
                    BlocksHelper.setWithoutUpdate(world, pos, EndDecorBlocks.MENGER_SPONGE_WET);
                }
            }
        }
    }

    static {
        REPLACE = (state) -> {
            if (state.is(EndWoodBlocks.END_LOTUS_STEM)) {
                return false;
            }
            return !state.getFluidState().isEmpty() || state.canBeReplaced();
        };
    }
}
