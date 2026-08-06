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

import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpeleothemConfiguration;
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

        // Sulphur spikes for sulphur_springs. The rarity filter picks the occasional column, and count +
        // spreadHorizontal then turn that one column into a patch of spikes rather than a single spike -
        // the same way vanilla's own sulfur_spike placement builds its clusters.
        EndTerrainFeatures.SULPHUR_SPIKE
                .inlineConfiguration(context)
                .withFeature(Feature.SPELEOTHEM)
                .configuration(surfaceSpike())
                .inlinePlace()
                .onceEvery(4)
                .squarePlacement()
                .count(28)
                .spreadHorizontal(UniformInt.of(-5, 5))
                // onEveryLayer emits the free block resting on top of each solid layer - exactly the origin
                // SpeleothemFeature expects - and isEmptyAndOn then keeps only the layers whose top is the
                // biome's own crust, which is what confines the patches to brimstone / sulphuric rock.
                // isEmpty means AIR, not air-or-water: spikes stay out of the sulphuric pools. Beyond
                // leaving the pools to the vents and crystal shards, a submerged spike is the one position
                // that can end up floating - a neighbouring chunk's lake writes its water at LAKES, which
                // for that chunk runs after this chunk has already finished UNDERGROUND_DECORATION, and it
                // can wash out the rock a spike was standing on.
                .onEveryLayer()
                .isEmptyAndOn(BlockPredicate.matchesBlocks(
                        EndStoneBlocks.BRIMSTONE,
                        EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE)
                ))
                .onlyInBiome()
                .register();

        // The mirror image, hanging off the island's underside. Sparser than the surface patches: these are
        // only visible from below/outside, so they read as an accent rather than as ground cover.
        EndTerrainFeatures.SULPHUR_SPIKE_HANGING
                .inlineConfiguration(context)
                .withFeature(Feature.SPELEOTHEM)
                .configuration(hangingSpike())
                .inlinePlace()
                .onceEvery(3)
                .squarePlacement()
                .count(20)
                .spreadHorizontal(UniformInt.of(-6, 6))
                // underEveryLayer emits the free block hanging under each solid layer; isEmptyAndUnder keeps
                // only the ones hanging in air below bare End stone, i.e. the island's bottom rather than its
                // sulphuric top or the ceiling of anything flooded.
                .underEveryLayer()
                .isEmptyAndUnder(BlockPredicate.matchesBlocks(Blocks.END_STONE))
                .onlyInBiome()
                .register();
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

    /**
     * The upward-growing sulphur spikes that stand on the sulphur springs crust.
     * <p>
     * Vanilla's own {@code minecraft:sulfur_spike} is the same {@link Feature#SPELEOTHEM} pointed at
     * {@code minecraft:sulfur} and gated on {@code #minecraft:sulfur_spike_replaceable_blocks} - neither of
     * which exists in the End. Rather than widening that vanilla tag (which would let spikes grow on End
     * stone in every dimension) the configuration is rebuilt here against the biome's own materials.
     */
    private static SpeleothemConfiguration surfaceSpike() {
        return new SpeleothemConfiguration(
                EndStoneBlocks.BRIMSTONE.defaultBlockState(),
                Blocks.SULFUR_SPIKE.defaultBlockState(),
                // replaceableBlocks does double duty in SpeleothemFeature: it is both "what counts as a base
                // to grow out of" and "what the base patch is allowed to overwrite". Listing the biome's two
                // surface materials therefore keeps the spikes on the crust and keeps the small brimstone
                // patch under them from eating into anything else.
                HolderSet.direct(
                        Block::builtInRegistryHolder,
                        EndStoneBlocks.BRIMSTONE,
                        EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE)
                ),
                0.2F, 0.7F, 0.5F, 0.5F
        );
    }

    /**
     * The downward-growing spikes that hang off the island's underside.
     * <p>
     * Base and replaceable block are both plain End stone: the biome's surface rules only define a
     * {@code floor} band, so the bottom of the island is bare End stone, and making the base block the same
     * block turns {@code createPatchOfBaseBlocks} into a no-op - the island keeps its own skin and only
     * gains the spikes.
     */
    private static SpeleothemConfiguration hangingSpike() {
        return new SpeleothemConfiguration(
                Blocks.END_STONE.defaultBlockState(),
                Blocks.SULFUR_SPIKE.defaultBlockState(),
                HolderSet.direct(Blocks.END_STONE.builtInRegistryHolder()),
                0.2F, 0.7F, 0.5F, 0.5F
        );
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
