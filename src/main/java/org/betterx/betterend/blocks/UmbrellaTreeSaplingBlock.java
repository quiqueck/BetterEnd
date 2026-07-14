package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.UmbrellaTreeFeature;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class UmbrellaTreeSaplingBlock extends FeatureSaplingBlock<UmbrellaTreeFeature, NoneFeatureConfiguration> {
    public UmbrellaTreeSaplingBlock(Properties props) {
        super(props, (level, pos, state, rnd) -> EndConfiguredVegetation.UMBRELLA_TREE.placeInWorld(level, pos, rnd));
    }
}
