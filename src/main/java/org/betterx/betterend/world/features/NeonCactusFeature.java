package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.betterend.blocks.NeonCactusPlantBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NeonCactusFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        BlockState ground = world.getBlockState(pos.below());
        if (!ground.is(EndTerrainBlocks.ENDSTONE_DUST) && !ground.is(EndTerrainBlocks.END_MOSS)) {
            return false;
        }

        NeonCactusPlantBlock cactus = ((NeonCactusPlantBlock) EndPlantBlocks.NEON_CACTUS);
        cactus.growPlant(world, pos, random);

        return true;
    }
}
