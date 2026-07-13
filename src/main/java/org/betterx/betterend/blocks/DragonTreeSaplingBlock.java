package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PottableFeatureSapling;
import org.betterx.betterend.interfaces.survives.SurvivesOnShadowGrass;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.DragonTreeFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DragonTreeSaplingBlock extends PottableFeatureSapling<DragonTreeFeature, NoneFeatureConfiguration> implements SurvivesOnShadowGrass {
    public DragonTreeSaplingBlock(BlockBehaviour.Properties props) {
        super(props, (level, pos, state, rnd) -> EndConfiguredVegetation.DRAGON_TREE.placeInWorld(level, pos, rnd));
    }
}
