package ru.betterend.blocks;

import java.util.Random;

import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import ru.betterend.blocks.BlockProperties.TripleShape;
import ru.betterend.blocks.basis.UnderwaterPlantWithAgeBlock;
import ru.betterend.registry.EndBlocks;
import ru.betterend.util.BlocksHelper;

public class EndLilySeedBlock extends UnderwaterPlantWithAgeBlock {
	@Override
	public void grow(WorldGenLevel world, Random random, BlockPos pos) {
		if (canGrow(world, pos)) {
			BlocksHelper.setWithoutUpdate(world, pos,
					EndBlocks.END_LILY.defaultBlockState().with(EndLilyBlock.SHAPE, TripleShape.BOTTOM));
			BlockPos up = pos.up();
			while (world.getFluidState(up).isStill()) {
				BlocksHelper.setWithoutUpdate(world, up,
						EndBlocks.END_LILY.defaultBlockState().with(EndLilyBlock.SHAPE, TripleShape.MIDDLE));
				up = up.up();
			}
			BlocksHelper.setWithoutUpdate(world, up,
					EndBlocks.END_LILY.defaultBlockState().with(EndLilyBlock.SHAPE, TripleShape.TOP));
		}
	}

	private boolean canGrow(WorldGenLevel world, BlockPos pos) {
		BlockPos up = pos.up();
		while (world.getBlockState(up).getFluidState().getFluid().equals(Fluids.WATER.getStill())) {
			up = up.up();
		}
		return world.isAir(up);
	}
}
