package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.TenaneaFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TenaneaSaplingBlock extends FeatureSaplingBlock<TenaneaFeature, NoneFeatureConfiguration> {
    public TenaneaSaplingBlock(BlockBehaviour.Properties props) {
        super(props, (level, pos, state, rnd) -> EndConfiguredVegetation.TENANEA.placeInWorld(level, pos, rnd));
    }
}
