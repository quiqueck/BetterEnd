package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndPlantWithAgeBlock;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LumecornSeedBlock extends EndPlantWithAgeBlock {

    public LumecornSeedBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void growAdult(WorldGenLevel world, RandomSource random, BlockPos pos) {
        EndConfiguredVegetation.LUMECORN.placeInWorld(world, pos, random);
    }

}
