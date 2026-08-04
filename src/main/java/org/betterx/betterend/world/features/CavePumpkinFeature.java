package org.betterx.betterend.world.features;


import org.betterx.betterend.registry.block.EndCropBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.EndBlockProperties;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CavePumpkinFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.above())
                  .is(CommonBlockTags.END_STONES) || !world.isEmptyBlock(pos) || !world.isEmptyBlock(
                pos.below())) {
            return false;
        }

        int age = random.nextInt(4);
        BlocksHelper.setWithoutUpdate(
                world,
                pos,
                EndCropBlocks.CAVE_PUMPKIN_SEED.defaultBlockState().setValue(EndBlockProperties.AGE, age)
        );
        if (age > 1) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    pos.below(),
                    EndCropBlocks.CAVE_PUMPKIN.defaultBlockState().setValue(EndBlockProperties.SMALL, age < 3)
            );
        }

        return true;
    }
}
