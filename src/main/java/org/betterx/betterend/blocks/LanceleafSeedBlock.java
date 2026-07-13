package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourSeed;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnAmberMoss;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.PentaShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LanceleafSeedBlock extends EndPlantWithAgeBlock implements SurvivesOnAmberMoss, BehaviourSeed {
    public LanceleafSeedBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void growAdult(WorldGenLevel world, RandomSource random, BlockPos pos) {
        int height = MHelper.randRange(4, 6, random);
        int h = BlocksHelper.upRay(world, pos, height + 2);
        if (h < height + 1) {
            return;
        }
        int rotation = random.nextInt(4);
        MutableBlockPos mut = new MutableBlockPos().set(pos);
        BlockState plant = EndBlocks.LANCELEAF.defaultBlockState().setValue(BlockProperties.ROTATION, rotation);
        BlocksHelper.setWithoutUpdate(world, mut, plant.setValue(BlockProperties.PENTA_SHAPE, PentaShape.BOTTOM));
        BlocksHelper.setWithoutUpdate(
                world,
                mut.move(Direction.UP),
                plant.setValue(BlockProperties.PENTA_SHAPE, PentaShape.PRE_BOTTOM)
        );
        for (int i = 2; i < height - 2; i++) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    mut.move(Direction.UP),
                    plant.setValue(BlockProperties.PENTA_SHAPE, PentaShape.MIDDLE)
            );
        }
        BlocksHelper.setWithoutUpdate(
                world,
                mut.move(Direction.UP),
                plant.setValue(BlockProperties.PENTA_SHAPE, PentaShape.PRE_TOP)
        );
        BlocksHelper.setWithoutUpdate(
                world,
                mut.move(Direction.UP),
                plant.setValue(BlockProperties.PENTA_SHAPE, PentaShape.TOP)
        );
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnAmberMoss.super.isTerrain(state);
    }
}
