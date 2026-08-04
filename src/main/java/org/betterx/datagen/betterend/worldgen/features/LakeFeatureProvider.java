package org.betterx.datagen.betterend.worldgen.features;

import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.features.EndConfiguredLakeFeature;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class LakeFeatureProvider extends WoverFeatureProvider {
    public LakeFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("lakes"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        EndConfiguredLakeFeature.SULPHURIC_LAKE.bootstrap(context).register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        // END_LAKE / END_LAKE_NORMAL / END_LAKE_RARE are now structures (EndStructures.END_LAKE*),
        // registered in StructureDataProvider, so they are no longer placed as features here.
        registerLake(context, EndLakeFeatures.DESERT_LAKE, EndFeatures.DESERT_LAKE_FEATURE, 8);

        EndLakeFeatures
                .SULPHURIC_LAKE
                .place(context)
                .onceEvery(8)
                .squarePlacement()
                .onlyInBiome()
                .register();
    }

    private static <F extends Feature<NoneFeatureConfiguration>> void registerLake(
            BootstrapContext<PlacedFeature> context, PlacedFeatureKey key,
            F feature, int chance
    ) {
        key.inlineConfiguration(context)
           .withFeature(feature)
           .inlinePlace()
           .onceEvery(chance)
           .squarePlacement()
           .onlyInBiome()
           .register();
    }
}
