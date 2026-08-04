package org.betterx.datagen.betterend.worldgen.features;


import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.betterend.registry.features.EndConfiguredCaveFeatures;
import org.betterx.betterend.registry.features.EndPlacedCaveFeatures;
import org.betterx.betterend.world.features.VineFeatureConfig;
import org.betterx.betterend.world.features.bushes.BushFeatureConfig;
import org.betterx.betterend.world.features.terrain.StalactiteFeatureConfig;
import org.betterx.betterend.world.features.terrain.caves.StalactiteClusterConfig;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class CaveFeatureProvider extends WoverFeatureProvider {
    public CaveFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("caves"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        //EndConfiguredCaveFeatures.CAVE_BUSH.bootstrap(context).register();

        EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL.bootstrap(context).register();

        EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL_SHARD
                .bootstrap(context)
                .configuration(new SimpleBlockConfiguration(SimpleStateProvider.simple(EndCrystalBlocks.SMARAGDANT_CRYSTAL_SHARD)))
                .register();

        EndConfiguredCaveFeatures.BIG_AURORA_CRYSTAL.bootstrap(context).register();

        EndConfiguredCaveFeatures.CAVE_BUSH
                .bootstrap(context)
                .configuration(new BushFeatureConfig(EndPlantBlocks.CAVE_BUSH, EndPlantBlocks.CAVE_BUSH))
                .register();

        EndConfiguredCaveFeatures.CAVE_GRASS
                .bootstrap(context)
                .configuration(new SimpleBlockConfiguration(SimpleStateProvider.simple(EndPlantBlocks.CAVE_GRASS)))
                .register();

        EndConfiguredCaveFeatures.RUBINEA
                .bootstrap(context)
                .configuration(new VineFeatureConfig(EndVineBlocks.RUBINEA, 8))
                .register();

        EndConfiguredCaveFeatures.MAGNULA
                .bootstrap(context)
                .configuration(new VineFeatureConfig(EndVineBlocks.MAGNULA, 8))
                .register();


        EndConfiguredCaveFeatures.END_STONE_STALACTITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(true, EndStoneBlocks.END_STONE_STALACTITE, Blocks.END_STONE))
                .register();
        EndConfiguredCaveFeatures.END_STONE_STALAGMITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(false, EndStoneBlocks.END_STONE_STALACTITE, Blocks.END_STONE))
                .register();
        EndConfiguredCaveFeatures.END_STONE_STALACTITE_CAVEMOSS
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(true, EndStoneBlocks.END_STONE_STALACTITE_CAVEMOSS, Blocks.END_STONE, EndTerrainBlocks.CAVE_MOSS))
                .register();
        EndConfiguredCaveFeatures.END_STONE_STALAGMITE_CAVEMOSS
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(false, EndStoneBlocks.END_STONE_STALACTITE_CAVEMOSS, EndTerrainBlocks.CAVE_MOSS))
                .register();
        EndConfiguredCaveFeatures.END_STONE_WITH_CAVEMOSS_STALACTITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(true, EndStoneBlocks.END_STONE_STALACTITE, 5, EndStoneBlocks.END_STONE_STALACTITE_CAVEMOSS, 2, Blocks.END_STONE))
                .register();
        EndConfiguredCaveFeatures.END_STONE_WITH_CAVEMOSS_STALAGMITE
                .bootstrap(context)
                .configuration(new StalactiteFeatureConfig(false, EndStoneBlocks.END_STONE_STALACTITE, 5, EndStoneBlocks.END_STONE_STALACTITE_CAVEMOSS, 1, Blocks.END_STONE))
                .register();

        EndConfiguredCaveFeatures.CAVE_PUMPKIN.bootstrap(context).register();

        // WP4.3: dripstone-style clusters. The cluster owns its per-column floor/ceiling placement, so the
        // floor/ceiling sub-features are referenced as plain configured-feature holders.
        EndConfiguredCaveFeatures.STALACTITE_CLUSTER_PLAIN
                .bootstrap(context)
                .configuration(new StalactiteClusterConfig(
                        UniformInt.of(3, 6),
                        ConstantFloat.of(0.35F),
                        0.6F,
                        EndConfiguredCaveFeatures.END_STONE_STALAGMITE.getHolder(context),
                        EndConfiguredCaveFeatures.END_STONE_STALACTITE.getHolder(context)
                ))
                .register();
        EndConfiguredCaveFeatures.STALACTITE_CLUSTER_CAVEMOSS
                .bootstrap(context)
                .configuration(new StalactiteClusterConfig(
                        UniformInt.of(3, 6),
                        ConstantFloat.of(0.35F),
                        0.6F,
                        EndConfiguredCaveFeatures.END_STONE_WITH_CAVEMOSS_STALAGMITE.getHolder(context),
                        EndConfiguredCaveFeatures.END_STONE_WITH_CAVEMOSS_STALACTITE.getHolder(context)
                ))
                .register();

        // WP4.4: lush vegetation patches (vanilla VegetationPatchFeature, purely data-driven).
        EndConfiguredCaveFeatures.CAVE_LUSH_FLOOR_PATCH
                .bootstrap(context)
                .configuration(lushPatch(context, CaveSurface.FLOOR, floorVegetation(context)))
                .register();
        EndConfiguredCaveFeatures.CAVE_LUSH_CEILING_PATCH
                .bootstrap(context)
                .configuration(lushPatch(context, CaveSurface.CEILING, ceilingVegetation(context)))
                .register();
    }

    private static VegetationPatchConfiguration lushPatch(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            CaveSurface surface,
            Holder<PlacedFeature> vegetation
    ) {
        return new VegetationPatchConfiguration(
                CommonBlockTags.END_STONES,
                BlockStateProvider.simple(EndTerrainBlocks.CAVE_MOSS),
                vegetation,
                surface,
                ConstantInt.of(1),
                0.1F,
                5,
                0.7F,
                UniformInt.of(4, 7),
                0.3F
        );
    }

    // Weighted vegetation via RandomSelectorFeature: because vanilla WeightedPlacedFeature stores a 0..1
    // chance (not an integer weight), the design weights [11,4,3,1] are converted to the conditional chances
    // that reproduce that distribution, with the lowest-weight entry as the default fallback.
    private static Holder<PlacedFeature> floorVegetation(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        return ConfiguredFeatureManager.INLINE_BUILDER
                .randomFeature()
                .add(inlinePlaced(context, EndConfiguredCaveFeatures.CAVE_GRASS), 11.0F / 19.0F)
                .add(inlinePlaced(context, EndConfiguredCaveFeatures.CAVE_BUSH), 4.0F / 8.0F)
                .add(inlinePlaced(context, EndConfiguredCaveFeatures.END_STONE_STALAGMITE_CAVEMOSS), 3.0F / 4.0F)
                .defaultFeature(inlinePlaced(context, EndConfiguredCaveFeatures.BIG_AURORA_CRYSTAL))
                .inlinePlace()
                .directHolder();
    }

    private static Holder<PlacedFeature> ceilingVegetation(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        return ConfiguredFeatureManager.INLINE_BUILDER
                .randomFeature()
                .add(inlinePlaced(context, EndConfiguredCaveFeatures.RUBINEA), 10.0F / 20.0F)
                .add(inlinePlaced(context, EndConfiguredCaveFeatures.MAGNULA), 4.0F / 10.0F)
                .add(inlinePlaced(context, EndConfiguredCaveFeatures.END_STONE_STALACTITE_CAVEMOSS), 5.0F / 6.0F)
                .defaultFeature(inlinePlaced(context, EndConfiguredCaveFeatures.CAVE_PUMPKIN))
                .inlinePlace()
                .directHolder();
    }

    private static Holder<PlacedFeature> inlinePlaced(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ConfiguredFeatureKey<?> key
    ) {
        return PlacementUtils.inlinePlaced(key.getHolder(context));
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        // Surface coating (floor/ceiling material + wall shell) is done at carve time by CaveSurfaceCoater;
        // there is no longer a decoration feature for it.

        // Clusters self-scan for floor/ceiling columns; no findSolid needed.
        cluster(context, EndPlacedCaveFeatures.STALACTITE_CLUSTER_PLAIN, EndConfiguredCaveFeatures.STALACTITE_CLUSTER_PLAIN, 2);
        cluster(context, EndPlacedCaveFeatures.STALACTITE_CLUSTER_CAVEMOSS, EndConfiguredCaveFeatures.STALACTITE_CLUSTER_CAVEMOSS, 3);

        // Floor scatters: the sub-feature requires solid End stone directly below the origin, which is exactly
        // where findSolidFloor lands (the air block resting on the floor) - no extra offset.
        floorScatter(context, EndPlacedCaveFeatures.STALAGMITE_SCATTER, EndConfiguredCaveFeatures.END_STONE_STALAGMITE, 6);
        floorScatter(context, EndPlacedCaveFeatures.BIG_AURORA_CRYSTAL, EndConfiguredCaveFeatures.BIG_AURORA_CRYSTAL, 1);
        floorScatter(context, EndPlacedCaveFeatures.SMARAGDANT_SHARD_SCATTER, EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL_SHARD, 24);
        floorScatter(context, EndPlacedCaveFeatures.CAVE_GRASS_SCATTER, EndConfiguredCaveFeatures.CAVE_GRASS, 10);
        floorScatter(context, EndPlacedCaveFeatures.CAVE_BUSH_SCATTER, EndConfiguredCaveFeatures.CAVE_BUSH, 3);

        // Each call places a 15-30 block cluster, but every attempt still needs its own findSolidFloor hit
        // inside the cave band, so count is the actual coverage knob - not a per-call multiplier.
        EndPlacedCaveFeatures.SMARAGDANT_CRYSTAL
                .place(context, EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL)
                .count(24)
                .squarePlacement()
                // Column-biome semantics: filter before the height/findSolid modifiers move the position up
                // out of the vertical cave band (see CaveSurfaceCoater for the same column-biome rationale).
                .onlyInBiome()
                .modifier(caveHeight())
                .findSolidFloor(12)
                .register();

        // Ceiling scatters: the sub-feature requires solid End stone directly above the origin, which is where
        // findSolidCeil lands (the air block hanging under the ceiling) - no extra offset.
        ceilScatter(context, EndPlacedCaveFeatures.STALACTITE_SCATTER, EndConfiguredCaveFeatures.END_STONE_STALACTITE, 6);
        ceilScatter(context, EndPlacedCaveFeatures.CAVE_PUMPKIN_PLACED, EndConfiguredCaveFeatures.CAVE_PUMPKIN, 2);

        // Vegetation patches find their own surface within vertical_range.
        patch(context, EndPlacedCaveFeatures.CAVE_LUSH_FLOOR_PATCH, EndConfiguredCaveFeatures.CAVE_LUSH_FLOOR_PATCH, 3);
        patch(context, EndPlacedCaveFeatures.CAVE_LUSH_CEILING_PATCH, EndConfiguredCaveFeatures.CAVE_LUSH_CEILING_PATCH, 3);
    }

    private static void cluster(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            ConfiguredFeatureKey<?> configured,
            int count
    ) {
        key.place(context, configured)
           .count(count)
           .squarePlacement()
           .onlyInBiome()
           .modifier(caveHeight())
           .register();
    }

    private static void patch(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            ConfiguredFeatureKey<?> configured,
            int count
    ) {
        key.place(context, configured)
           .count(count)
           .squarePlacement()
           .onlyInBiome()
           .modifier(caveHeight())
           .register();
    }

    private static void floorScatter(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            ConfiguredFeatureKey<?> configured,
            int count
    ) {
        key.place(context, configured)
           .count(count)
           .squarePlacement()
           .onlyInBiome()
           .modifier(caveHeight())
           .findSolidFloor(12)
           .register();
    }

    private static void ceilScatter(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            ConfiguredFeatureKey<?> configured,
            int count
    ) {
        key.place(context, configured)
           .count(count)
           .squarePlacement()
           .onlyInBiome()
           .modifier(caveHeight())
           .findSolidCeil(12)
           .register();
    }

    // The wover placement builder has no bounded absolute-range helper, so the design's "uniform absolute
    // 4..56" band is expressed with the raw vanilla HeightRangePlacement modifier.
    private static HeightRangePlacement caveHeight() {
        return HeightRangePlacement.uniform(VerticalAnchor.absolute(4), VerticalAnchor.absolute(56));
    }
}
