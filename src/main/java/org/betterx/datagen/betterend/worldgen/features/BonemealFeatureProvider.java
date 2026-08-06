package org.betterx.datagen.betterend.worldgen.features;



import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.features.EndBonemealFeature;
import org.betterx.betterend.registry.features.EndConfiguredBonemealFeature;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.features.config.ConditionFeatureConfig;
import de.ambertation.wover.feature.api.placed.modifiers.InBiome;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class BonemealFeatureProvider extends WoverFeatureProvider {
    public BonemealFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("bonemeal"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        EndConfiguredBonemealFeature.BONEMEAL_END_MOSS
                .bootstrap(context)
                .configuration(new ConditionFeatureConfig(
                        InBiome.matchingID(EndBiomes.GLOWING_GRASSLANDS.key.identifier()),
                        EndBonemealFeature.BONEMEAL_END_MOSS_GLOWING_GRASSLANDS.getHolder(context),
                        EndBonemealFeature.BONEMEAL_END_MOSS_NOT_GLOWING_GRASSLANDS.getHolder(context)
                ))
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_RUTISCUS
                .bootstrap(context)
                .configuration(new ConditionFeatureConfig(
                        InBiome.matchingID(EndBiomes.LANTERN_WOODS.key.identifier()),
                        EndBonemealFeature.BONEMEAL_RUTISCUS_LANTERN_WOODS.getHolder(context),
                        EndBonemealFeature.BONEMEAL_RUTISCUS_NOT_LANTERN_WOODS.getHolder(context)
                ))
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_END_MYCELIUM
                .bootstrap(context).add(EndPlantBlocks.CREEPING_MOSS, 100)
                .add(EndPlantBlocks.UMBRELLA_MOSS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_JUNGLE_MOSS
                .bootstrap(context).add(EndPlantBlocks.JUNGLE_GRASS, 100)
                .add(EndPlantBlocks.TWISTED_UMBRELLA_MOSS, 100)
                .add(EndMushroomBlocks.SMALL_JELLYSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_SANGNUM
                .bootstrap(context).add(EndPlantBlocks.CLAWFERN, 100)
                .add(EndPlantBlocks.GLOBULAGUS, 100)
                .add(EndMushroomBlocks.SMALL_AMARANITA_MUSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_MOSSY_DRAGON_BONE
                .bootstrap(context).add(EndPlantBlocks.CLAWFERN, 100)
                .add(EndPlantBlocks.GLOBULAGUS, 100)
                .add(EndMushroomBlocks.SMALL_AMARANITA_MUSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_MOSSY_OBSIDIAN
                .bootstrap(context).add(EndPlantBlocks.CLAWFERN, 100)
                .add(EndPlantBlocks.GLOBULAGUS, 100)
                .add(EndMushroomBlocks.SMALL_AMARANITA_MUSHROOM, 10)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_CAVE_MOSS
                .bootstrap(context).add(EndPlantBlocks.CAVE_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_CHORUS_NYLIUM
                .bootstrap(context).add(EndPlantBlocks.CHORUS_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_CRYSTAL_MOSS
                .bootstrap(context).add(EndPlantBlocks.CRYSTAL_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_SHADOW_GRASS
                .bootstrap(context).add(EndPlantBlocks.SHADOW_PLANT, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_PINK_MOSS
                .bootstrap(context).add(EndPlantBlocks.BUSHY_GRASS, 100)
                .register();

        EndConfiguredBonemealFeature.BONEMEAL_AMBER_MOSS
                .bootstrap(context).add(EndPlantBlocks.AMBER_GRASS, 100)
                .register();

        // Umbra Valley is the only biome that plants on pallidium, and it plants exactly these two
        // (VegetationFeaturesProvider: INFLEXIA at density 16, FLAMMALIX at 5). The weights keep that
        // ~3:1 ratio so bonemealed patches look like the generated surface.
        EndConfiguredBonemealFeature.BONEMEAL_PALLIDIUM
                .bootstrap(context).add(EndPlantBlocks.INFLEXIA, 100)
                .add(EndPlantBlocks.FLAMMALIX, 30)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        EndBonemealFeature.BONEMEAL_END_MOSS_NOT_GLOWING_GRASSLANDS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndPlantBlocks.CREEPING_MOSS, 10)
                .add(EndPlantBlocks.UMBRELLA_MOSS, 10)
                .inlinePlace()
                .register();

        EndBonemealFeature.BONEMEAL_END_MOSS_GLOWING_GRASSLANDS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndPlantBlocks.CREEPING_MOSS, 10)
                .add(EndPlantBlocks.UMBRELLA_MOSS, 10)
                .add(EndPlantBlocks.BLOOMING_COOKSONIA, 100)
                .add(EndPlantBlocks.VAIOLUSH_FERN, 100)
                .add(EndPlantBlocks.FRACTURN, 100)
                .add(EndPlantBlocks.SALTEAGO, 100)
                .add(EndPlantBlocks.TWISTED_UMBRELLA_MOSS, 10)
                .inlinePlace()
                .register();

        EndBonemealFeature.BONEMEAL_RUTISCUS_NOT_LANTERN_WOODS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndPlantBlocks.ORANGO, 100)
                .add(EndPlantBlocks.AERIDIUM, 20)
                .add(EndPlantBlocks.LUTEBUS, 20)
                .add(EndPlantBlocks.LAMELLARIUM, 100)
                .inlinePlace()
                .register();

        EndBonemealFeature.BONEMEAL_RUTISCUS_LANTERN_WOODS
                .inlineConfiguration(context).bonemealPatch()
                .add(EndPlantBlocks.AERIDIUM, 20)
                .add(EndMushroomBlocks.BOLUX_MUSHROOM, 5)
                .add(EndPlantBlocks.LAMELLARIUM, 100)
                .inlinePlace()
                .register();
    }
}
