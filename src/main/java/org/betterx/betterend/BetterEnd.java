package org.betterx.betterend;

import org.betterx.bclib.api.v2.datafixer.MigrationProfile;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.api.BetterEndPlugin;
import org.betterx.betterend.commands.CommandRegistry;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.effects.EndPotions;
import org.betterx.betterend.integration.Integrations;
import org.betterx.betterend.network.RitualUpdate;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.tab.CreativeTabs;
import org.betterx.betterend.util.BonemealPlants;
import org.betterx.betterend.world.generator.EndCaveBiomeDecider;
import org.betterx.betterend.world.generator.EndLandBiomeDecider;
import org.betterx.betterend.world.generator.GeneratorOptions;
import de.ambertation.wover.core.api.Logger;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.generator.api.biomesource.end.BiomeDecider;
import de.ambertation.wover.state.api.WorldConfig;

import net.minecraft.resources.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class BetterEnd implements ModInitializer {
    public static final ModCore C = ModCore.create("betterend");
    public static final ModCore TRINKETS_CORE = ModCore.create("trinkets");
    public static final String MOD_ID = C.namespace;
    public static final Logger LOGGER = C.LOG;

    public static final ModCore BYG = ModCore.create("byg");
    public static final ModCore NOURISH = ModCore.create("nourish");
    public static final ModCore FLAMBOYANT = ModCore.create("flamboyant");
    public static final ModCore PATCHOULI = ModCore.create("patchouli");
    public static final ModCore HYDROGEN = ModCore.create("hydrogen");
    public static final Identifier BYG_ADDITIONS_PACK = C.addDatapack(BYG);
    public static final Identifier NOURISH_ADDITIONS_PACK = C.addDatapack(NOURISH);
    public static final Identifier FLAMBOYANT_ADDITIONS_PACK = C.addDatapack(FLAMBOYANT);
    public static final Identifier PATCHOULI_ADDITIONS_PACK = C.addDatapack(PATCHOULI);

    @Override
    public void onInitialize() {
        WorldConfig.registerMod(C);
        Patcher.register();

        // Dev-only: rewrites the IDs inside our shipped .nbt templates using the registered patches.
        // Run with -Dbetterend.fixStructures=<abs path to src/main/resources/data/betterend/structure>.
        // Registered on SERVER_STARTING because every mod has to have registered its patches before
        // the folder is walked, and BCLib's own initializer runs after this one.
        final String fixStructures = System.getProperty("betterend.fixStructures");
        if (fixStructures != null) {
            ServerLifecycleEvents.SERVER_STARTING.register(server ->
                    MigrationProfile.fixCustomFolder(new File(fixStructures))
            );
        }

        EndNumericProviders.register();
        EndPortals.loadPortals();
        EndSounds.register();
        EndMenuTypes.ensureStaticallyLoaded();
        EndBlockEntities.register();
        EndPoiTypes.register();
        EndFeatures.register();
        EndCarvers.ensureStaticallyLoaded();
        EndEntities.register();
        EndBiomes.register();
        EndTags.register();
        EndBlocks.ensureStaticallyLoaded();
        EndItems.ensureStaticallyLoaded();
        EndTemplates.ensureStaticallyLoaded();
        EndEnchantments.ensureStaticallyLoaded();
        EndPotions.register();
        InfusionRecipe.register();
        EndStructures.register();
        BonemealPlants.init();
        GeneratorOptions.init();
        CommandRegistry.register();
        EndParticles.ensureStaticallyLoadedServerside();
        BECriteria.register();
        FabricLoader.getInstance()
                    .getEntrypoints("betterend", BetterEndPlugin.class)
                    .forEach(BetterEndPlugin::register);
        Integrations.init();
        Configs.saveConfigs();
        CreativeTabs.register();

        if (GeneratorOptions.useNewGenerator()) {
            BiomeDecider.registerHighPriorityDecider(C.mk("end_land"), new EndLandBiomeDecider());
        }
        // The cave decider is generator-agnostic (its canProvideFor accepts every WoverEndBiomeSource,
        // vanilla and Paulevs alike), so it must register unconditionally - caves exist under both
        // terrain options.
        BiomeDecider.registerDecider(C.mk("cave_biome_decider"), new EndCaveBiomeDecider());

        // NOTE: flower_islets / waterfall_ponds are placed as ordinary void small-island patches (via
        // the IS_SMALL_END_ISLAND tag) and grow their own terrain through EndStructures.SMALL_ISLAND, so
        // the old terrain-coupled small-island biome decider was removed.

        // RitualUpdate registers itself with NetworkRegistry from its own KEY's static initializer;
        // this just forces the class to load.
        var ignored = RitualUpdate.KEY;

        //TODO: Trinkets integration disabled (dependency commented out in build.gradle)
//        if (TRINKETS_CORE.isLoaded()) {
//            Elytra.register();
//        }
    }
}
