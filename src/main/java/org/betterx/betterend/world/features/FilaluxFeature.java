package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndLightBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FilaluxFeature extends SkyScatterFeature {
    @Override
    public void generate(ScatterFeatureConfig cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos) {
        BlockState vine = EndVineBlocks.FILALUX.defaultBlockState();
        BlockState wings = EndVineBlocks.FILALUX_WINGS.defaultBlockState();
        BlocksHelper.setWithoutUpdate(world, blockPos, EndLightBlocks.FILALUX_LANTERN);
        BlocksHelper.setWithoutUpdate(
                world,
                blockPos.above(),
                wings.setValue(BlockStateProperties.FACING, Direction.UP)
        );
        for (Direction dir : BlocksHelper.HORIZONTAL) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    blockPos.relative(dir),
                    wings.setValue(BlockStateProperties.FACING, dir)
            );
        }
        int length = MHelper.randRange(1, 3, random);
        for (int i = 1; i <= length; i++) {
            TripleShape shape = length > 1 ? TripleShape.TOP : TripleShape.BOTTOM;
            if (i > 1) {
                shape = i == length ? TripleShape.BOTTOM : TripleShape.MIDDLE;
            }
            BlocksHelper.setWithoutUpdate(world, blockPos.below(i), vine.setValue(BlockProperties.TRIPLE_SHAPE, shape));
        }
    }
}
