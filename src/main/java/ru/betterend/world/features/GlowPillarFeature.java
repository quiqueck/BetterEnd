package ru.betterend.world.features;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import ru.betterend.blocks.basis.EndPlantWithAgeBlock;
import ru.betterend.registry.EndBlocks;

public class GlowPillarFeature extends ScatterFeature {
	public GlowPillarFeature() {
		super(9);
	}

	@Override
	public boolean canGenerate(StructureWorldAccess world, Random random, BlockPos center, BlockPos blockPos, float radius) {
		return EndBlocks.GLOWING_PILLAR_SEED.canPlaceAt(AIR, world, blockPos);
	}

	@Override
	public void generate(StructureWorldAccess world, Random random, BlockPos blockPos) {
		EndPlantWithAgeBlock seed = ((EndPlantWithAgeBlock) EndBlocks.GLOWING_PILLAR_SEED);
		seed.growAdult(world, random, blockPos);
	}
	
	@Override
	protected int getChance() {
		return 10;
	}
}
