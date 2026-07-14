package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.LacugroveFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LacugroveSaplingBlock extends FeatureSaplingBlock<LacugroveFeature, NoneFeatureConfiguration> {
    public LacugroveSaplingBlock(BlockBehaviour.Properties props) {
        super(
                props,
                (level, pos, state, rnd)
                        -> EndConfiguredVegetation.LACUGROVE.placeInWorld(level, pos, rnd)
        );
    }
}
