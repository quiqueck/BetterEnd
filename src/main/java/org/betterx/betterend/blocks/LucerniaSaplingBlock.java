package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.LucerniaFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LucerniaSaplingBlock extends FeatureSaplingBlock<LucerniaFeature, NoneFeatureConfiguration> {
    public LucerniaSaplingBlock(BlockBehaviour.Properties props) {
        super(props, (level, pos, state, rnd) -> EndConfiguredVegetation.LUCERNIA.placeInWorld(level, pos, rnd));
    }
}
