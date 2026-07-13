package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnAmberMoss;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.HelixTreeFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class HelixTreeSaplingBlock extends PottableFeatureSapling<HelixTreeFeature, NoneFeatureConfiguration> implements SurvivesOnAmberMoss {
    public HelixTreeSaplingBlock(BlockBehaviour.Properties props) {
        super(props, (level, pos, state, rnd) -> EndConfiguredVegetation.HELIX_TREE.placeInWorld(level, pos, rnd));
    }
}
