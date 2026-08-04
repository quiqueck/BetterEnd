package org.betterx.datagen.betterend.tags;

import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.EndTags;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeTagProvider extends WoverTagProvider.ForBiomes {
    public BiomeTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Biome> context) {
        context.add(
                EndStructures.ETERNAL_PORTAL.biomeTag(),
                Biomes.END_BARRENS,
                Biomes.END_MIDLANDS,
                Biomes.END_HIGHLANDS
        );

        context.add(
                EndTags.IS_END_HIGH_OR_MIDLAND,
                CommonBiomeTags.IS_END_HIGHLAND,
                CommonBiomeTags.IS_END_MIDLAND
        );
    }
}
