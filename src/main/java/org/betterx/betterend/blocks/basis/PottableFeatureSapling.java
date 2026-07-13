package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.behaviours.interfaces.BehaviourSapling;
import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.bclib.interfaces.SurvivesOn;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public abstract class PottableFeatureSapling<F extends Feature<FC>, FC extends FeatureConfiguration> extends FeatureSaplingBlock<F, FC> implements BehaviourSapling, SurvivesOn {

    public PottableFeatureSapling(FeatureSupplier<F, FC> featureSupplier) {
        super(featureSupplier);
    }

    public PottableFeatureSapling(
            BlockBehaviour.Properties properties,
            FeatureSupplier<F, FC> featureSupplier
    ) {
        super(properties, featureSupplier);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return isSurvivable(blockState);
    }
}
