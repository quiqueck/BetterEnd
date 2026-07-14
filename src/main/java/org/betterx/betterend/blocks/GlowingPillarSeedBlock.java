package org.betterx.betterend.blocks;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GlowingPillarSeedBlock extends EndPlantWithAgeBlock {
    public GlowingPillarSeedBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void growAdult(WorldGenLevel world, RandomSource random, BlockPos pos) {
        int height = MHelper.randRange(1, 2, random);
        int h = BlocksHelper.upRay(world, pos, height + 2);
        if (h < height) {
            return;
        }

        MutableBlockPos mut = new MutableBlockPos().set(pos);
        BlockState roots = EndBlocks.GLOWING_PILLAR_ROOTS.defaultBlockState();
        if (height < 2) {
            BlocksHelper.setWithUpdate(world, mut, roots.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE));
        } else {
            BlocksHelper.setWithUpdate(world, mut, roots.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM));
            mut.move(Direction.UP);
            BlocksHelper.setWithUpdate(world, mut, roots.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP));
        }
        mut.move(Direction.UP);
        BlocksHelper.setWithUpdate(
                world,
                mut,
                EndBlocks.GLOWING_PILLAR_LUMINOPHOR.defaultBlockState().setValue(BlueVineLanternBlock.NATURAL, true)
        );
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            pos = mut.relative(dir);
            if (world.isEmptyBlock(pos)) {
                BlocksHelper.setWithUpdate(
                        world,
                        pos,
                        EndBlocks.GLOWING_PILLAR_LEAVES.defaultBlockState().setValue(BlockStateProperties.FACING, dir)
                );
            }
        }
        mut.move(Direction.UP);
        if (world.isEmptyBlock(mut)) {
            BlocksHelper.setWithUpdate(
                    world,
                    mut,
                    EndBlocks.GLOWING_PILLAR_LEAVES.defaultBlockState()
                                                   .setValue(BlockStateProperties.FACING, Direction.UP)
            );
        }
    }

}
