package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import de.ambertation.wover.block.api.BlockProperties;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.util.GlobalState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SilkMothNestFeature extends DefaultFeature {
    private boolean canGenerate(WorldGenLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos.above());
        // Cube leaves or logs only: a tenanea_outer_leaves above fills just the half of its block nearest
        // its own support, so a nest hung under one dangles half a block clear of it.
        if (BlocksHelper.isCubeLeaves(world, pos.above(), state) || state.is(BlockTags.LOGS)) {
            state = world.getBlockState(pos);
            if ((state.isAir() || state.is(EndWoodBlocks.TENANEA_OUTER_LEAVES)) && world.isEmptyBlock(pos.below())) {
                for (Direction dir : BlocksHelper.HORIZONTAL) {
                    return !world.getBlockState(pos.below().relative(dir)).blocksMotion();
                }
            }
        }
        return false;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final MutableBlockPos POS = GlobalState.stateForThread().POS;
        final RandomSource random = featureConfig.random();
        final BlockPos center = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        int maxY = world.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX(), center.getZ());
        int minY = BlocksHelper.upRay(world, new BlockPos(center.getX(), 0, center.getZ()), maxY);
        POS.set(center);
        for (int y = maxY; y > minY; y--) {
            POS.setY(y);
            if (canGenerate(world, POS)) {
                Direction dir = BlocksHelper.randomHorizontal(random);
                BlocksHelper.setWithoutUpdate(
                        world,
                        POS,
                        EndFunctionalBlocks.SILK_MOTH_NEST.defaultBlockState()
                                                .setValue(BlockStateProperties.HORIZONTAL_FACING, dir)
                                                .setValue(BlockProperties.ACTIVE, false)
                );
                POS.setY(y - 1);
                BlocksHelper.setWithoutUpdate(
                        world,
                        POS,
                        EndFunctionalBlocks.SILK_MOTH_NEST.defaultBlockState()
                                                .setValue(BlockStateProperties.HORIZONTAL_FACING, dir)
                );
                return true;
            }
        }
        return false;
    }
}
