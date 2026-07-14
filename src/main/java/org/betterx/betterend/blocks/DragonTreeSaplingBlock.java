package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.DragonTreeFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DragonTreeSaplingBlock extends FeatureSaplingBlock<DragonTreeFeature, NoneFeatureConfiguration> {
    public DragonTreeSaplingBlock(BlockBehaviour.Properties props) {
        super(props, (level, pos, state, rnd) -> EndConfiguredVegetation.DRAGON_TREE.placeInWorld(level, pos, rnd));
    }
}
