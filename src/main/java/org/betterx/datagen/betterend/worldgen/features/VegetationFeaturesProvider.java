package org.betterx.datagen.betterend.worldgen.features;


import org.betterx.betterend.registry.block.EndCropBlocks;
import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.betterend.registry.block.EndWallPlantBlocks;
import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.features.*;
import org.betterx.betterend.world.features.bushes.BushFeatureConfig;
import org.betterx.betterend.world.features.bushes.BushWithOuterFeatureConfig;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class VegetationFeaturesProvider extends WoverFeatureProvider {
    public VegetationFeaturesProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("vegetation"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        EndConfiguredVegetation.DRAGON_TREE.bootstrap(context).register();
        EndConfiguredVegetation.GIGANTIC_AMARANITA.bootstrap(context).register();
        EndConfiguredVegetation.HELIX_TREE.bootstrap(context).register();
        EndConfiguredVegetation.DRAGON_HELIX_TREE.bootstrap(context).register();
        EndConfiguredVegetation.JELLYSHROOM.bootstrap(context).register();
        EndConfiguredVegetation.LACUGROVE.bootstrap(context).register();
        EndConfiguredVegetation.LUCERNIA.bootstrap(context).register();
        EndConfiguredVegetation.MOSSY_GLOWSHROOM.bootstrap(context).register();
        EndConfiguredVegetation.PYTHADENDRON_TREE.bootstrap(context).register();
        EndConfiguredVegetation.TENANEA.bootstrap(context).register();
        EndConfiguredVegetation.UMBRELLA_TREE.bootstrap(context).register();

        EndConfiguredVegetation.LARGE_AMARANITA.bootstrap(context).register();
        EndConfiguredVegetation.LUMECORN.bootstrap(context).register();
        EndConfiguredVegetation.TENANEA_BUSH.bootstrap(context).register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        //Trees
        registerVegetation(context, EndVegetationFeatures.MOSSY_GLOWSHROOM, 2);
        registerVegetation(context, EndVegetationFeatures.PYTHADENDRON_TREE, 1);
        registerVegetation(context, EndVegetationFeatures.LACUGROVE, 4);
        registerVegetation(context, EndVegetationFeatures.DRAGON_TREE, 2);
        registerVegetation(context, EndVegetationFeatures.TENANEA, 3);
        registerVegetation(context, EndVegetationFeatures.HELIX_TREE, 1);
        registerVegetation(context, EndVegetationFeatures.DRAGON_HELIX_TREE, 1);
        registerVegetation(context, EndVegetationFeatures.UMBRELLA_TREE, 2);
        registerVegetation(context, EndVegetationFeatures.JELLYSHROOM, 2);
        registerVegetation(context, EndVegetationFeatures.GIGANTIC_AMARANITA, 1);
        registerVegetation(context, EndVegetationFeatures.LUCERNIA, 3);

        //Bushes
        registerVegetation(context, EndVegetationFeatures.TENANEA_BUSH, 12);
        registerVegetation(context, EndVegetationFeatures.LUMECORN, 5);
        registerVegetation(context, EndVegetationFeatures.LARGE_AMARANITA, 5);
        registerVegetation(context, EndVegetationFeatures.AMARANITA_PATCH, EndFeatures.AMARANITA_PATCH_FEATURE, 1);
        registerVegetation(context, EndVegetationFeatures.NEON_CACTUS, EndFeatures.NEON_CACTUS_FEATURE, 2);
        registerVegetation(context, EndVegetationFeatures.PYTHADENDRON_BUSH, EndFeatures.BUSH_FEATURE, new BushFeatureConfig(EndWoodBlocks.PYTHADENDRON_LEAVES, EndWoodBlocks.PYTHADENDRON.getBark()), 3);
        registerVegetation(context, EndVegetationFeatures.DRAGON_TREE_BUSH, EndFeatures.BUSH_FEATURE, new BushFeatureConfig(EndWoodBlocks.DRAGON_TREE_LEAVES, EndWoodBlocks.DRAGON_TREE.getBark()), 5);
        registerVegetation(context, EndVegetationFeatures.LUCERNIA_BUSH, EndFeatures.BUSH_WITH_OUTER_FEATURE, new BushWithOuterFeatureConfig(EndWoodBlocks.LUCERNIA_LEAVES, EndWoodBlocks.LUCERNIA_OUTER_LEAVES, EndWoodBlocks.LUCERNIA.getBark()), 10);
        registerVegetation(context, EndVegetationFeatures.LUCERNIA_BUSH_RARE, EndFeatures.BUSH_WITH_OUTER_FEATURE, new BushWithOuterFeatureConfig(EndWoodBlocks.LUCERNIA_LEAVES, EndWoodBlocks.LUCERNIA_OUTER_LEAVES, EndWoodBlocks.LUCERNIA.getBark()), 1);

        //Vines
        registerVegetation(context, EndVegetationFeatures.DENSE_VINE, EndFeatures.VINE_FEATURE, new VineFeatureConfig(EndVineBlocks.DENSE_VINE, 24), 3);
        registerVegetation(context, EndVegetationFeatures.TWISTED_VINE, EndFeatures.VINE_FEATURE, new VineFeatureConfig(EndVineBlocks.TWISTED_VINE, 24), 1);
        registerVegetation(context, EndVegetationFeatures.BULB_VINE, EndFeatures.VINE_FEATURE, new VineFeatureConfig(EndVineBlocks.BULB_VINE, 24), 5);
        registerVegetation(context, EndVegetationFeatures.JUNGLE_VINE, EndFeatures.VINE_FEATURE, new VineFeatureConfig(EndVineBlocks.JUNGLE_VINE, 24), 5);
        registerVegetation(context, EndVegetationFeatures.BLUE_VINE, EndFeatures.BLUE_VINE_FEATURE, ScatterFeatureConfig.blueVine(), 1);

        // Ceil plants
        registerVegetation(context, EndVegetationFeatures.SMALL_JELLYSHROOM_CEIL, EndFeatures.SINGLE_INVERTED_SCATTER_FEATURE, new SinglePlantFeatureConfig(EndMushroomBlocks.SMALL_JELLYSHROOM, 8), 8);

        // Wall Plants
        registerVegetation(context, EndVegetationFeatures.PURPLE_POLYPORE, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.PURPLE_POLYPORE, 3), 5);
        registerVegetation(context, EndVegetationFeatures.AURANT_POLYPORE, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.AURANT_POLYPORE, 3), 5);
        registerVegetation(context, EndVegetationFeatures.TAIL_MOSS, EndFeatures.WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.TAIL_MOSS, 3), 15);
        registerVegetation(context, EndVegetationFeatures.CYAN_MOSS, EndFeatures.WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.CYAN_MOSS, 3), 15);
        registerVegetation(context, EndVegetationFeatures.TAIL_MOSS_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.TAIL_MOSS, 4), 25);
        registerVegetation(context, EndVegetationFeatures.CYAN_MOSS_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.CYAN_MOSS, 4), 25);
        registerVegetation(context, EndVegetationFeatures.TWISTED_MOSS, EndFeatures.WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.TWISTED_MOSS, 6), 15);
        registerVegetation(context, EndVegetationFeatures.TWISTED_MOSS_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.TWISTED_MOSS, 6), 25);
        registerVegetation(context, EndVegetationFeatures.BULB_MOSS, EndFeatures.WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.BULB_MOSS, 6), 1);
        registerVegetation(context, EndVegetationFeatures.BULB_MOSS_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.BULB_MOSS, 6), 15);
        registerVegetation(context, EndVegetationFeatures.SMALL_JELLYSHROOM_WALL, EndFeatures.WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndMushroomBlocks.SMALL_JELLYSHROOM, 4), 4);
        registerVegetation(context, EndVegetationFeatures.SMALL_JELLYSHROOM_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndMushroomBlocks.SMALL_JELLYSHROOM, 4), 8);
        registerVegetation(context, EndVegetationFeatures.JUNGLE_FERN_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.JUNGLE_FERN, 3), 12);
        registerVegetation(context, EndVegetationFeatures.RUSCUS, EndFeatures.WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.RUSCUS, 6), 10);
        registerVegetation(context, EndVegetationFeatures.RUSCUS_WOOD, EndFeatures.WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndWallPlantBlocks.RUSCUS, 6), 10);

        //Sky plants
        registerVegetation(context, EndVegetationFeatures.FILALUX, EndFeatures.FILALUX_FEATURE, ScatterFeatureConfig.filalux(), 1);

        // Water
        registerVegetation(context, EndVegetationFeatures.BUBBLE_CORAL, EndFeatures.UNDERWATER_PLANT_FEATURE, new SinglePlantFeatureConfig(EndWaterPlantBlocks.BUBBLE_CORAL, 6), 10);
        registerVegetation(context, EndVegetationFeatures.BUBBLE_CORAL_RARE, EndFeatures.UNDERWATER_PLANT_FEATURE, new SinglePlantFeatureConfig(EndWaterPlantBlocks.BUBBLE_CORAL, 3), 4);
        registerVegetation(context, EndVegetationFeatures.END_LILY, EndFeatures.END_LILY_FEATURE, new ScatterFeatureConfig(6), 10);
        registerVegetation(context, EndVegetationFeatures.END_LILY_RARE, EndFeatures.END_LILY_FEATURE, new ScatterFeatureConfig(3), 4);
        registerVegetation(context, EndVegetationFeatures.END_LOTUS, EndFeatures.END_LOTUS_FEATURE, new ScatterFeatureConfig(7), 6);
        registerVegetation(context, EndVegetationFeatures.END_LOTUS_LEAF, EndFeatures.END_LOTUS_LEAF_FEATURE, new ScatterFeatureConfig(20), 4);
        registerVegetation(context, EndVegetationFeatures.HYDRALUX, EndFeatures.HYDRALUX_FEATURE, new ScatterFeatureConfig(5), 5);
        registerVegetation(context, EndVegetationFeatures.POND_ANEMONE, EndFeatures.UNDERWATER_PLANT_FEATURE, new SinglePlantFeatureConfig(EndWaterPlantBlocks.POND_ANEMONE, 6), 10);
        registerVegetation(context, EndVegetationFeatures.MENGER_SPONGE, EndFeatures.MENGER_SPONGE_FEATURE, new ScatterFeatureConfig(5), 1);
        registerVegetation(context, EndVegetationFeatures.FLAMAEA, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndWaterPlantBlocks.FLAMAEA, 12, false, 5), 20);
        registerVegetation(context, EndVegetationFeatures.CHARNIA_RED, EndFeatures.CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndWaterPlantBlocks.CHARNIA_RED), 10);
        registerVegetation(context, EndVegetationFeatures.CHARNIA_PURPLE, EndFeatures.CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndWaterPlantBlocks.CHARNIA_PURPLE), 10);
        registerVegetation(context, EndVegetationFeatures.CHARNIA_CYAN, EndFeatures.CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndWaterPlantBlocks.CHARNIA_CYAN), 10);
        registerVegetation(context, EndVegetationFeatures.CHARNIA_LIGHT_BLUE, EndFeatures.CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndWaterPlantBlocks.CHARNIA_LIGHT_BLUE), 10);
        registerVegetation(context, EndVegetationFeatures.CHARNIA_ORANGE, EndFeatures.CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndWaterPlantBlocks.CHARNIA_ORANGE), 10);
        registerVegetation(context, EndVegetationFeatures.CHARNIA_GREEN, EndFeatures.CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndWaterPlantBlocks.CHARNIA_GREEN), 10);
        registerVegetation(context, EndVegetationFeatures.CHARNIA_RED_RARE, EndFeatures.CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndWaterPlantBlocks.CHARNIA_RED), 2);

        // Plants
        registerVegetation(context, EndVegetationFeatures.UMBRELLA_MOSS, EndFeatures.DOUBLE_PLANT_FEATURE, new DoublePlantFeatureConfig(EndPlantBlocks.UMBRELLA_MOSS, EndPlantBlocks.UMBRELLA_MOSS_TALL, 5), 3);
        registerVegetation(context, EndVegetationFeatures.TWISTED_UMBRELLA_MOSS, EndFeatures.DOUBLE_PLANT_FEATURE, new DoublePlantFeatureConfig(EndPlantBlocks.TWISTED_UMBRELLA_MOSS, EndPlantBlocks.TWISTED_UMBRELLA_MOSS_TALL, 6), 3);

        registerVegetation(context, EndVegetationFeatures.CREEPING_MOSS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.CREEPING_MOSS, 5), 3);
        registerVegetation(context, EndVegetationFeatures.CHORUS_GRASS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.CHORUS_GRASS, 4), 3);
        registerVegetation(context, EndVegetationFeatures.CRYSTAL_GRASS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.CRYSTAL_GRASS, 8, false), 20);
        registerVegetation(context, EndVegetationFeatures.SHADOW_PLANT, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.SHADOW_PLANT, 6), 5);
        registerVegetation(context, EndVegetationFeatures.MURKWEED, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.MURKWEED, 3), 2);
        registerVegetation(context, EndVegetationFeatures.NEEDLEGRASS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.NEEDLEGRASS, 3), 1);
        registerVegetation(context, EndVegetationFeatures.SHADOW_BERRY, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndCropBlocks.SHADOW_BERRY, 2), 1);
        registerVegetation(context, EndVegetationFeatures.BUSHY_GRASS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.BUSHY_GRASS, 8, false), 10);
        registerVegetation(context, EndVegetationFeatures.BUSHY_GRASS_WG, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.BUSHY_GRASS, 5), 8);
        registerVegetation(context, EndVegetationFeatures.AMBER_GRASS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.AMBER_GRASS, 6), 7);
        registerVegetation(context, EndVegetationFeatures.JUNGLE_GRASS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.JUNGLE_GRASS, 7, 3), 6);
        registerVegetation(context, EndVegetationFeatures.SMALL_JELLYSHROOM_FLOOR, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndMushroomBlocks.SMALL_JELLYSHROOM, 5, 5), 2);
        registerVegetation(context, EndVegetationFeatures.BLOSSOM_BERRY, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndCropBlocks.BLOSSOM_BERRY, 4, 4), 3);
        registerVegetation(context, EndVegetationFeatures.BLOOMING_COOKSONIA, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.BLOOMING_COOKSONIA, 5), 5);
        registerVegetation(context, EndVegetationFeatures.SALTEAGO, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.SALTEAGO, 5), 5);
        registerVegetation(context, EndVegetationFeatures.VAIOLUSH_FERN, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.VAIOLUSH_FERN, 5), 5);
        registerVegetation(context, EndVegetationFeatures.FRACTURN, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.FRACTURN, 5), 5);
        registerVegetation(context, EndVegetationFeatures.UMBRELLA_MOSS_RARE, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.UMBRELLA_MOSS, 3), 2);
        registerVegetation(context, EndVegetationFeatures.CREEPING_MOSS_RARE, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.CREEPING_MOSS, 3), 2);
        registerVegetation(context, EndVegetationFeatures.TWISTED_UMBRELLA_MOSS_RARE, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.TWISTED_UMBRELLA_MOSS, 3), 2);
        registerVegetation(context, EndVegetationFeatures.ORANGO, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.ORANGO, 5), 6);
        registerVegetation(context, EndVegetationFeatures.AERIDIUM, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.AERIDIUM, 5, 4), 5);
        registerVegetation(context, EndVegetationFeatures.LUTEBUS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.LUTEBUS, 5, 2), 5);
        registerVegetation(context, EndVegetationFeatures.LAMELLARIUM, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.LAMELLARIUM, 5), 6);
        registerVegetation(context, EndVegetationFeatures.SMALL_AMARANITA, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndMushroomBlocks.SMALL_AMARANITA_MUSHROOM, 5, 5), 4);
        registerVegetation(context, EndVegetationFeatures.GLOBULAGUS, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.GLOBULAGUS, 5, 3), 6);
        registerVegetation(context, EndVegetationFeatures.CLAWFERN, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.CLAWFERN, 5, 4), 5);
        registerVegetation(context, EndVegetationFeatures.BOLUX_MUSHROOM, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndMushroomBlocks.BOLUX_MUSHROOM, 5, 5), 2);
        registerVegetation(context, EndVegetationFeatures.CHORUS_MUSHROOM, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndCropBlocks.CHORUS_MUSHROOM, 3, 5), 1);
        registerVegetation(context, EndVegetationFeatures.AMBER_ROOT, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndCropBlocks.AMBER_ROOT, 5, 5), 1);
        registerVegetation(context, EndVegetationFeatures.INFLEXIA, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.INFLEXIA, 7, false, 3), 16);
        registerVegetation(context, EndVegetationFeatures.FLAMMALIX, EndFeatures.SINGLE_PLANT_FEATURE, new SinglePlantFeatureConfig(EndPlantBlocks.FLAMMALIX, 3, false, 7), 5);
        registerVegetation(context, EndVegetationFeatures.LANCELEAF, EndFeatures.LANCELEAF_FEATURE, ScatterFeatureConfig.lanceleaf(), 2);
        registerVegetation(context, EndVegetationFeatures.GLOW_PILLAR, EndFeatures.GLOW_PILLAR_FEATURE, ScatterFeatureConfig.glowPillar(), 1);

        // Cover patches: the original countRange(16,256) x onEveryLayer(2) with spread chance 1.0
        // and search range 20 was up to ~512 aggressive placements per chunk - it plastered every
        // crystal-moss surface and occupied the block spaces the crystal grass (which runs BEFORE
        // this, order was never the problem) needed. That got scaled all the way down to
        // countRange(2,6), which then read as too sparse. Settling on a middle ground.
        EndVegetationFeatures.CRYSTAL_MOSS_COVER.inlineConfiguration(context)
                                                .withFeature(Feature.MULTIFACE_GROWTH)
                                                .configuration(new MultifaceGrowthConfiguration(EndPlantBlocks.CRYSTAL_MOSS_COVER, 8, true, true, true, 0.75f, HolderSet.direct(Block::builtInRegistryHolder, EndTerrainBlocks.CRYSTAL_MOSS, Blocks.END_STONE)))
                                                .inlinePlace()
                                                .countRange(16, 32)
                                                .onEveryLayer(1)
                                                .onlyInBiome()
                                                .register();
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
           .onEveryLayerMax(density)
           .onlyInBiome()
           .register();
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> void registerVegetation(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            F feature,
            int density
    ) {
        key
                .inlineConfiguration(context)
                .withFeature(feature)
                .inlinePlace()
                .onEveryLayerMax(density)
                .onlyInBiome()
                .register();
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> void registerVegetation(
            BootstrapContext<PlacedFeature> context,
            PlacedConfiguredFeatureKey key,
            int density
    ) {
        key
                .place(context)
                .onEveryLayerMax(density)
                .onlyInBiome()
                .register();
    }
}
