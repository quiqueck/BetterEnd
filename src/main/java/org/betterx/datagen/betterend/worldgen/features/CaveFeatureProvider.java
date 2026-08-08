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
import org.betterx.betterend.world.features.terrain.caves.CaveSurfaceCoatConfig;
import org.betterx.betterend.world.features.terrain.caves.StalactiteClusterConfig;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
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
        // Cave surface coat. Geometry only - the floor/ceiling/wall materials are read from the
        // EndCaveBiome at placement time, so this single instance serves every cave biome and every face.
        //
        // The band is deliberately wider than the carved cave band: the feature recognises sky and void by
        // an air run reaching an end of the band, so the band has to extend past the caves in both
        // directions for that test to mean anything. 0 is the End's world floor; 72 clears the top of the
        // carve band with room to spare. shell_depth 5 is the thickness of the jadestone wall lining.
        //
        // `replaceable` is bare End stone, NOT CommonBlockTags.END_STONES - that tag also carries the land
        // biomes' surface blocks and the ores, and a cave passing close to an island's skin then repainted
        // it. `protected` is redundant while replaceable is bare End stone, but it encodes the invariant a
        // datapack could otherwise break by widening replaceable: this runs after UNDERGROUND_ORES, unlike
        // the carve-time coater it replaces, so a wide shell would eat ore veins near a cave face.
        EndConfiguredCaveFeatures.CAVE_SURFACE_COAT
                .bootstrap(context)
                .configuration(new CaveSurfaceCoatConfig(
                        HolderSet.direct(Blocks.END_STONE.builtInRegistryHolder()),
                        context.lookup(Registries.BLOCK).getOrThrow(CommonBlockTags.ORES),
                        0,
                        72,
                        5
                ))
                .register();

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
        // Cave surface coat: the decoration-time replacement for the carve-time CaveSurfaceCoater, ported
        // back from 26.3 so a world that migrates forward keeps generating the same caves. See
        // CaveSurfaceCoatFeature for why the sweep shape is what it is.
        surfaceCoat(context, EndPlacedCaveFeatures.CAVE_SURFACE_COAT, EndConfiguredCaveFeatures.CAVE_SURFACE_COAT);

        // Clusters self-scan for floor/ceiling columns; no findSolid needed.
        cluster(context, EndPlacedCaveFeatures.STALACTITE_CLUSTER_PLAIN, EndConfiguredCaveFeatures.STALACTITE_CLUSTER_PLAIN, 2);
        cluster(context, EndPlacedCaveFeatures.STALACTITE_CLUSTER_CAVEMOSS, EndConfiguredCaveFeatures.STALACTITE_CLUSTER_CAVEMOSS, 3);

        // Floor scatters: the sub-feature requires solid End stone directly below the origin, which is exactly
        // where findSolidFloor lands (the air block resting on the floor) - no extra offset.
        floorScatter(context, EndPlacedCaveFeatures.STALAGMITE_SCATTER, EndConfiguredCaveFeatures.END_STONE_STALAGMITE, 6);
        floorScatter(context, EndPlacedCaveFeatures.BIG_AURORA_CRYSTAL, EndConfiguredCaveFeatures.BIG_AURORA_CRYSTAL, 1);
        // 8 -> 24: a headless fill-census in a fresh world (fresh chunks, current code) found essentially no
        // smaragdant material at all - see the SMARAGDANT_CRYSTAL comment below, the same findSolidFloor-into-
        // the-cave-band hit-rate problem applies here too.
        floorScatter(context, EndPlacedCaveFeatures.SMARAGDANT_SHARD_SCATTER, EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL_SHARD, 24);
        floorScatter(context, EndPlacedCaveFeatures.CAVE_GRASS_SCATTER, EndConfiguredCaveFeatures.CAVE_GRASS, 10);
        floorScatter(context, EndPlacedCaveFeatures.CAVE_BUSH_SCATTER, EndConfiguredCaveFeatures.CAVE_BUSH, 3);

        // Each call places a 15-30 block cluster, but every attempt still needs its own findSolidFloor hit
        // inside the cave band, so count is the actual coverage knob - not a per-call multiplier. count(1)
        // with onceEvery(2) (~0.5 attempts/chunk) left Smaragdant caves looking like plain end-stone caves
        // with the odd shard, since it was 12-16x sparser than every other floor/ceiling scatter below.
        // Raising it to 8 (the first fix here) turned out to still be nowhere near enough: a headless
        // fill-census in freshly generated chunks (not reused stale ones) found ~6 crystal-related blocks
        // across roughly 15 chunks of a located lush_smaragdant_cave, out of ~71,000 solid-rock blocks
        // sampled - i.e. still visually indistinguishable from a plain end-stone cave. findSolidFloor only
        // searches ~12 blocks either way from a uniformly random (x, z, y) pick across the whole Y4-64 band,
        // so most attempts likely land inside solid rock or open void with nothing to find in range; count
        // is the only lever available to compensate without changing the search mechanism itself. Pushed to
        // 24 - since this is the biome's signature feature. (The cave surface coat solved the same
        // "make sure this is actually visible" problem by dropping the scatter model altogether and
        // sweeping the chunk once; these scatters place individual objects, so they still need the count.)
        EndPlacedCaveFeatures.SMARAGDANT_CRYSTAL
                .place(context, EndConfiguredCaveFeatures.SMARAGDANT_CRYSTAL)
                .count(24)
                .squarePlacement()
                // Column-biome semantics: filter before the height/findSolid modifiers move the position up
                // out of the vertical cave band (the cave biomes are vertical: a column either is a cave column
                // at every Y or it is not).
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

    /**
     * Places the {@code CaveSurfaceCoatFeature} exactly once per chunk.
     * <p>
     * The feature sweeps the whole chunk it is decorating, so unlike every other helper in this file the
     * placement is not a scatter: {@code count(1)} plus the constant height picks one column, and the only
     * thing that column decides is <em>whether</em> the sweep runs. There is deliberately no surface search
     * - an {@code environment_scan} would tie the pass to one cave face again, which is the coverage problem
     * the sweep exists to solve.
     * <p>
     * {@code onlyInBiome()} is kept - vanilla logs an error for any biome-placed feature without it - but it
     * is not what selects cave columns here; the sweep's own per-column check is. It passes everywhere
     * because {@code EndFeatures#addDefaultFeatures} declares this feature on every End biome, land ones
     * included, exactly so that the roll cannot skip a chunk that holds part of the cave band.
     * <p>
     * The height is a constant deep inside the vertical cave band: {@code EndCaveBiomeDecider} switches a
     * column to a cave biome only BELOW {@code WoverEndConfig#caveBiomesTopY} (plus jitter), and the feature
     * reuses the origin's Y to resolve every column's biome, so that Y wants to be a depth the cave biomes
     * actually own rather than the {@link #caveHeight()} roll the scatters use.
     */
    private static void surfaceCoat(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            ConfiguredFeatureKey<?> configured
    ) {
        key.place(context, configured)
           .count(1)
           .modifier(HeightRangePlacement.of(ConstantHeight.of(VerticalAnchor.absolute(16))))
           .onlyInBiome()
           .register();
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
