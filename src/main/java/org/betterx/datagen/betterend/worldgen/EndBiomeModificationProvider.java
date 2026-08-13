package org.betterx.datagen.betterend.worldgen;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndPlacedCaveFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import de.ambertation.wover.biome.api.modification.BiomeModification;
import de.ambertation.wover.biome.api.modification.BiomeModificationRegistry;
import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BiomeTags;

public class EndBiomeModificationProvider extends WoverRegistryContentProvider<BiomeModification> {
    public EndBiomeModificationProvider(
            ModCore modCore
    ) {
        super(modCore, "BetterEnd - Biome Modifications", BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstrapContext<BiomeModification> context) {
        // Split off the ores, and PREPENDED, because the two halves need opposite insert positions - see
        // FeaturePosition, and BetterEnd#596 for the crash the wrong one causes.
        //
        // UNDERGROUND_ORES is empty in every vanilla End biome, so in BetterEnd's own biomes these three are
        // the whole step and anything a third-party mod adds to it lands behind them. TechReborn adds four
        // End ores there through Fabric's biome modification API, which runs before ours: appending here put
        // them in front of our ores in vanilla's End biomes while our own biomes still had ours in front,
        // and vanilla's FeatureSorter rejects that contradiction by refusing to generate the whole
        // dimension. Prepending keeps our three leading in every End biome, foreign ones included.
        BiomeModification
                .build(context, BetterEnd.C.id("default_ores"))
                .allOf(
                        BiomePredicate.not(BiomePredicate.inNamespace(BetterEnd.C)),
                        BiomePredicate.anyOf(
                                BiomePredicate.hasTag(CommonBiomeTags.IS_END_BARRENS),
                                BiomePredicate.hasTag(CommonBiomeTags.IS_END_MIDLAND),
                                BiomePredicate.hasTag(CommonBiomeTags.IS_END_HIGHLAND)
                        )
                )
                .prependFeatures()
                .addFeature(EndOreFeatures.FLAVOLITE_LAYER)
                .addFeature(EndOreFeatures.THALLASIUM_ORE)
                .addFeature(EndOreFeatures.ENDER_ORE)
                .register();

        // The crashed ship stays APPENDED, for the same reason the ores must not be: it sits behind
        // minecraft:end_gateway_return in BetterEnd's own SURFACE_STRUCTURES step, so it has to stay behind
        // it in the foreign biomes that have that feature too.
        BiomeModification
                .build(context, BetterEnd.C.id("defaults"))
                .allOf(
                        BiomePredicate.not(BiomePredicate.inNamespace(BetterEnd.C)),
                        BiomePredicate.anyOf(
                                BiomePredicate.hasTag(CommonBiomeTags.IS_END_BARRENS),
                                BiomePredicate.hasTag(CommonBiomeTags.IS_END_MIDLAND),
                                BiomePredicate.hasTag(CommonBiomeTags.IS_END_HIGHLAND)
                        )
                )
                .addFeature(EndTerrainFeatures.CRASHED_SHIP)
                .register();

        // The cave surface coat has to be declared by EVERY biome that can own a column in the End, foreign
        // ones included, or it does not run at all in some chunks that hold part of the cave band: a placed
        // feature only runs where the biome at the rolled position declared it, and the roll is one random
        // column of the chunk. EndFeatures#addDefaultFeatures covers BetterEnd's own biomes; this covers the
        // rest, above all vanilla's minecraft:the_end, which owns the void columns between the islands and
        // made up more than half of some cave chunks. The pass itself paints cave columns only, so declaring
        // it here changes nothing about where the coat appears; it only decides where the sweep gets to look.
        BiomeModification
                .build(context, BetterEnd.C.id("cave_surface_coat"))
                .allOf(
                        BiomePredicate.not(BiomePredicate.inNamespace(BetterEnd.C)),
                        BiomePredicate.hasTag(BiomeTags.IS_END)
                )
                // Prepended for the same reason as the ores above: UNDERGROUND_DECORATION is empty in vanilla's
                // End biomes, so the coat is the whole step in BetterEnd's own biomes and has to lead in the
                // foreign ones too.
                .prependFeatures()
                .addFeature(EndPlacedCaveFeatures.CAVE_SURFACE_COAT)
                .register();

        BiomeModification
                .build(context, BetterEnd.C.id("eternal_portals"))
                .not(
                        BiomePredicate.or(
                                BiomePredicate.inNamespace("minecraft"),
                                BiomePredicate.inNamespace(BetterEnd.C),
                                BiomePredicate.pathContains("mountain"),
                                BiomePredicate.pathContains("lake")
                        )
                )
                .addStructureSet(EndStructures.ETERNAL_PORTAL)
                .register();


    }
}
