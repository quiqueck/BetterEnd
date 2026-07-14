package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.FeatureSaplingBlock;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.world.features.trees.MossyGlowshroomFeature;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MossyGlowshroomSaplingBlock extends FeatureSaplingBlock<MossyGlowshroomFeature, NoneFeatureConfiguration> {
    public MossyGlowshroomSaplingBlock(BlockBehaviour.Properties properties) {
        super(
                properties,
                (level, pos, state, rnd) -> EndConfiguredVegetation.MOSSY_GLOWSHROOM.placeInWorld(level, pos, rnd)
        );
    }
}
