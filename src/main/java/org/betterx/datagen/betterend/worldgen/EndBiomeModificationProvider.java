package org.betterx.datagen.betterend.worldgen;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import de.ambertation.wover.biome.api.modification.BiomeModification;
import de.ambertation.wover.biome.api.modification.BiomeModificationRegistry;
import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.data.worldgen.BootstrapContext;

public class EndBiomeModificationProvider extends WoverRegistryContentProvider<BiomeModification> {
    public EndBiomeModificationProvider(
            ModCore modCore
    ) {
        super(modCore, "BetterEnd - Biome Modifications", BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstrapContext<BiomeModification> context) {
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
                .addFeature(EndOreFeatures.FLAVOLITE_LAYER)
                .addFeature(EndOreFeatures.THALLASIUM_ORE)
                .addFeature(EndOreFeatures.ENDER_ORE)
                .addFeature(EndTerrainFeatures.CRASHED_SHIP)
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
