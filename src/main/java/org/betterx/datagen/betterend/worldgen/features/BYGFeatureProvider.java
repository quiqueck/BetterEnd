package org.betterx.datagen.betterend.worldgen.features;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class BYGFeatureProvider extends WoverFeatureProvider {
    public BYGFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("byg_features"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {

    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
//        registerVegetation(context, BYGFeatures.OLD_BULBIS_TREE, BYGFeatures.OLD_BULBIS_TREE_FEATURE, 1);
//        registerVegetation(context, BYGFeatures.NIGHTSHADE_REDWOOD_TREE, BYGFeatures.NIGHTSHADE_REDWOOD_TREE_FEATURE, 1);
//        registerVegetation(context, BYGFeatures.BIG_ETHER_TREE, BYGFeatures.BIG_ETHER_TREE_FEATURE, 1);
//
//        registerVegetation(context, BYGFeatures.IVIS_SPROUT, EndFeatures.SINGLE_PLANT_FEATURE,
//                new SinglePlantFeatureConfig(Integrations.BYG.getBlock("ivis_sprout"), 6, 2),
//                6
//        );
//        registerVegetation(context, BYGFeatures.IVIS_VINE, EndFeatures.VINE_FEATURE,
//                new VineFeatureConfig(BYGBlocks.IVIS_VINE, 24),
//                5
//        );
//        registerVegetation(context, BYGFeatures.IVIS_MOSS, EndFeatures.WALL_PLANT_FEATURE,
//                new WallPlantFeatureConfig(BYGBlocks.IVIS_MOSS, 6),
//                1
//        );
//        registerVegetation(context, BYGFeatures.IVIS_MOSS_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE,
//                new WallPlantFeatureConfig(BYGBlocks.IVIS_MOSS, 6),
//                15
//        );
//        registerVegetation(context, BYGFeatures.NIGHTSHADE_MOSS, EndFeatures.WALL_PLANT_FEATURE,
//                new WallPlantFeatureConfig(BYGBlocks.NIGHTSHADE_MOSS, 5),
//                2
//        );
//        registerVegetation(context, BYGFeatures.NIGHTSHADE_MOSS_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE,
//                new WallPlantFeatureConfig(BYGBlocks.NIGHTSHADE_MOSS, 5),
//                8
//        );
    }

    private static <F extends Feature<NoneFeatureConfiguration>> void registerVegetation(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            F feature,
            int density
    ) {
        registerVegetation(context, key, feature, NoneFeatureConfiguration.NONE, density);
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> void registerVegetation(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            F feature,
            FC config,
            int density
    ) {
        key.inlineConfiguration(context)
           .withFeature(feature)
           .configuration(config)
           .inlinePlace()
           .countMax(density)
           .squarePlacement()
           .heightmap()
           .onlyInBiome()
           .register();
    }
}
