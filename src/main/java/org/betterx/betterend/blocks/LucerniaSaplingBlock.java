package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnRutiscus;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.LucerniaFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LucerniaSaplingBlock extends PottableFeatureSapling<LucerniaFeature, NoneFeatureConfiguration> implements SurvivesOnRutiscus {
    public LucerniaSaplingBlock(BlockBehaviour.Properties props) {
        super(props, (level, pos, state, rnd) -> EndConfiguredVegetation.LUCERNIA.placeInWorld(level, pos, rnd));
    }
}
