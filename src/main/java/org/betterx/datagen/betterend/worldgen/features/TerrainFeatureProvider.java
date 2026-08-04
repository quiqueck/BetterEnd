package org.betterx.datagen.betterend.worldgen.features;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.features.BuildingListFeatureConfig;
import org.betterx.betterend.world.features.NBTFeatureConfig;
import org.betterx.betterend.world.features.terrain.ArchFeatureConfig;
import org.betterx.betterend.world.features.terrain.IceStarFeatureConfig;
import org.betterx.betterend.world.features.terrain.ThinArchFeatureConfig;
import org.betterx.betterend.world.structures.village.VillagePools;
import org.betterx.datagen.betterend.worldgen.EndBiomesProvider;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class TerrainFeatureProvider extends WoverFeatureProvider {
    public TerrainFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("terrain"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // Randomly place nbt features from folder
        EndBiomesProvider
                .BIOMES
                .values()
                .stream()
                .filter(i -> i.configuredFeatureKey() != null)
                .forEach(info -> {
                    info.configuredFeatureKey()
                        .bootstrap(context)
                        .configuration(new BuildingListFeatureConfig(info.structures(), info
                                .config()
                                .surfaceMaterial()
                                .getTopMaterial()))
                        .register();
                });
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        registerChanced(context, EndTerrainFeatures.SURFACE_VENT, EndFeatures.SURFACE_VENT_FEATURE, FeatureConfiguration.NONE, 4);
        registerChanced(context, EndTerrainFeatures.SULPHUR_HILL, EndFeatures.SULPHUR_HILL_FEATURE, FeatureConfiguration.NONE, 8);
        registerChanced(context, EndTerrainFeatures.OBSIDIAN_PILLAR_BASEMENT, EndFeatures.OBSIDIAN_PILLAR_FEATURE, FeatureConfiguration.NONE, 8);
        registerChanced(context, EndTerrainFeatures.OBSIDIAN_BOULDER, EndFeatures.OBSIDIAN_BOULDER_FEATURE, FeatureConfiguration.NONE, 10);
        registerChanced(context, EndTerrainFeatures.FALLEN_PILLAR, EndFeatures.FALLEN_PILLAR_FEATURE, FeatureConfiguration.NONE, 20);
        // Arches: back to onceEvery, NOT count. ArchFeature ignores the placement XZ and always snaps
        // to the chunk centre (see its getPosOnSurfaceWG at (x&~15)|7), so count(N) just stacks N arches
        // at the same centre (looks like "too many"), while a chunk whose centre is over a void gap
        // rejects entirely ("none"). onceEvery(N) gives one arch per ~N chunks, evenly, no stacking.
        registerChanced(context, EndTerrainFeatures.UMBRALITH_ARCH, EndFeatures.ARCH_FEATURE, new ArchFeatureConfig(EndStoneBlocks.UMBRALITH.getBlock(SlotType.SOURCE), ArchFeatureConfig.SurfaceFunction.UMBRA_VALLEY), 3);
        registerChanced(context, EndTerrainFeatures.THIN_UMBRALITH_ARCH, EndFeatures.THIN_ARCH_FEATURE, new ThinArchFeatureConfig(EndStoneBlocks.UMBRALITH.getBlock(SlotType.SOURCE)), 4);
        registerChanced(context, EndTerrainFeatures.CRASHED_SHIP, EndFeatures.CRASHED_SHIP_FEATURE, new NBTFeatureConfig(EndBiome.Config.DEFAULT_MATERIAL.getTopMaterial()), 500);
        registerChanced(context, EndTerrainFeatures.SILK_MOTH_NEST, EndFeatures.SILK_MOTH_NEST_FEATURE, FeatureConfiguration.NONE, 2);

        registerChanced(context, EndTerrainFeatures.SPIRE, EndFeatures.SPIRE_FEATURE, FeatureConfiguration.NONE, 4);
        registerChanced(context, EndTerrainFeatures.FLOATING_SPIRE, EndFeatures.FLOATING_SPIRE_FEATURE, FeatureConfiguration.NONE, 8);
        registerChanced(context, EndTerrainFeatures.GEYSER, EndFeatures.GEYSER_FEATURE, FeatureConfiguration.NONE, 8);
        registerChanced(context, EndTerrainFeatures.ICE_STAR, EndFeatures.ICE_STAR_FEATURE, new IceStarFeatureConfig(5, 15, 10, 25), 15);
        registerChanced(context, EndTerrainFeatures.ICE_STAR_SMALL, EndFeatures.ICE_STAR_FEATURE, new IceStarFeatureConfig(3, 5, 7, 12), 8);


        // Rim pond + spill-over waterfall for the waterfall_ponds biome. No squarePlacement so the
        // origin stays at the chunk corner and PondWithWaterfallFeature can centre on the chunk
        // (origin + 8,8), keeping the whole bowl inside the origin chunk. Runs once EVERY chunk (no
        // rarity_filter): the SDF islands are now much rarer, and the feature self-rejects on void
        // chunks (WORLD_SURFACE_WG floor probe), so an every-chunk attempt just ensures that (almost)
        // every island that IS present actually gets its pond instead of being thinned out again.
        EndTerrainFeatures.POND_WITH_WATERFALL
                .inlineConfiguration(context)
                .withFeature(EndFeatures.POND_WITH_WATERFALL_FEATURE)
                .configuration(FeatureConfiguration.NONE)
                .inlinePlace()
                .count(1)
                .onlyInBiome()
                .register();

        EndTerrainFeatures.BIOME_ISLAND
                .inlineConfiguration(context)
                .withFeature(EndFeatures.OVERWORLD_ISLAND)
                .inlinePlace()
                .register();

        // Place chorus village
        VillagePools.CHORUS_VILLAGE
                .place(context, net.minecraft.data.worldgen.features.EndFeatures.CHORUS_PLANT)
                .modifier(PlacementUtils.filteredByBlockSurvival(Blocks.CHORUS_PLANT))
                .register();


        // Randomly place nbt features from folder
        EndBiomesProvider
                .BIOMES
                .values()
                .stream()
                .filter(i -> i.placed() != null)
                .forEach(info -> {
                    info.placed()
                        .place(context)
                        .onceEvery(10)
                        .squarePlacement()
                        .onlyInBiome()
                        .register();
                });
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> void registerChanced(
            BootstrapContext<PlacedFeature> context, PlacedFeatureKey key,
            F feature, FC config, int chance
    ) {
        key.inlineConfiguration(context)
           .withFeature(feature)
           .configuration(config)
           .inlinePlace()
           .onceEvery(chance)
           .squarePlacement()
           .onlyInBiome()
           .register();
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> void registerCounted(
            BootstrapContext<PlacedFeature> context, PlacedFeatureKey key,
            F feature, FC config, int count
    ) {
        key.inlineConfiguration(context)
           .withFeature(feature)
           .configuration(config)
           .inlinePlace()
           .count(count)
           .squarePlacement()
           .onlyInBiome()
           .register();
    }
}
