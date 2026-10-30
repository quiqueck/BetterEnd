package org.betterx.betterend;

import org.betterx.bclib.api.v2.dataexchange.DataExchangeAPI;
import org.betterx.bclib.api.v2.generator.BiomeDecider;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.api.BetterEndPlugin;
import org.betterx.betterend.commands.CommandRegistry;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.effects.EndPotions;
import org.betterx.betterend.integration.Integrations;
import org.betterx.betterend.integration.trinkets.Elytra;
import org.betterx.betterend.network.RitualUpdate;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.tab.CreativeTabs;
import org.betterx.betterend.util.BonemealPlants;
import org.betterx.betterend.util.LootTableUtil;
import org.betterx.betterend.world.generator.EndLandBiomeDecider;
import org.betterx.betterend.world.generator.GeneratorOptions;
import org.betterx.betterend.world.generator.VoidRing;
import org.betterx.worlds.together.util.Logger;
import org.betterx.worlds.together.world.WorldConfig;
import org.betterx.worlds.together.world.event.WorldEvents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class BetterEnd implements ModInitializer {
    public static final String MOD_ID = "betterend";
    public static final Logger LOGGER = new Logger(MOD_ID);
    public static final boolean RUNS_TRINKETS = FabricLoader.getInstance()
                                                            .getModContainer("trinkets")
                                                            .isPresent();

    @Override
    public void onInitialize() {
        WorldConfig.registerModCache(MOD_ID);
        EndNumericProviders.register();
        EndPortals.loadPortals();
        EndSounds.register();
        EndMenuTypes.ensureStaticallyLoaded();
        EndBlockEntities.register();
        EndPoiTypes.register();
        EndFeatures.register();
        EndEntities.register();
        EndBiomes.register();
        EndTags.register();
        EndBlocks.ensureStaticallyLoaded();
        EndItems.register();
        EndTemplates.ensureStaticallyLoaded();
        EndEnchantments.register();
        EndPotions.register();
        InfusionRecipe.register();
        EndStructures.register();
        EndBiomeBuildings.register();
        BonemealPlants.init();
        GeneratorOptions.init();
        LootTableUtil.init();
        CommandRegistry.register();
        EndParticles.ensureStaticallyLoadedServerside();
        BECriteria.register();
        FabricLoader.getInstance()
                    .getEntrypoints("betterend", BetterEndPlugin.class)
                    .forEach(BetterEndPlugin::register);
        Integrations.init();
        Configs.saveConfigs();
        CreativeTabs.register();

        WorldEvents.ADAPT_WORLD_PRESET.on(VoidRing::adaptWorldPreset);

        if (GeneratorOptions.useNewGenerator()) {
            BiomeDecider.registerHighPriorityDecider(makeID("end_land"), new EndLandBiomeDecider());
        }

        BiomeAPI.registerEndBiomeModification((biomeID, biome) -> {
            if (!biomeID.equals(Biomes.THE_VOID.location())) {
                EndFeatures.addBiomeFeatures(biomeID, biome);
            }
        });

        BiomeAPI.onFinishingEndBiomeTags((biomeID, biome) -> {
            if (!biomeID.equals(Biomes.THE_VOID.location())) {
                EndStructures.addBiomeStructures(biomeID, biome);
            }
        });

        DataExchangeAPI.registerDescriptors(List.of(
                RitualUpdate.DESCRIPTOR
        ));

        if (RUNS_TRINKETS) {
            Elytra.register();
        }
    }

    public static ResourceLocation makeID(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
