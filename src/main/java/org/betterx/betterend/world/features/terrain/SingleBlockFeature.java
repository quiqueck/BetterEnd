package org.betterx.betterend.world.features.terrain;

import org.betterx.bclib.util.BlocksHelper;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SingleBlockFeature extends Feature<SimpleBlockConfiguration> {
    public SingleBlockFeature() {
        super(SimpleBlockConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        final SimpleBlockConfiguration cfg = featureConfig.config();
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) {
            return false;
        }

        BlockState state = cfg.toPlace().getState(world, random, pos);
        if (state.getBlock().getStateDefinition().getProperty("waterlogged") != null) {
            boolean waterlogged = !world.getFluidState(pos).isEmpty();
            state = state.setValue(BlockStateProperties.WATERLOGGED, waterlogged);
        }
        BlocksHelper.setWithoutUpdate(world, pos, state);

        return true;
    }
}
