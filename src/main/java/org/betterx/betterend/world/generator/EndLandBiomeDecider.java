package org.betterx.betterend.world.generator;


import org.betterx.betterend.registry.EndTags;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeSource;
import de.ambertation.wover.generator.api.biomesource.end.BiomeDecider;
import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

public class EndLandBiomeDecider extends BiomeDecider {
    public EndLandBiomeDecider() {
        super((biome) -> false);
    }

    @Override
    public boolean canProvideFor(BiomeSource source) {
        if (source instanceof WoverEndBiomeSource endSource) {
            return endSource.getBiomeSourceConfig().generatorVersion == WoverEndConfig.EndBiomeGeneratorType.PAULEVS;
        }
        return false;
    }

    @Override
    public BiomeDecider createInstance(WoverBiomeSource biomeSource) {
        //TODO: 1.19.4: This ok?
        return new EndLandBiomeDecider(/*biomeSource.getBiomeRegistry()*/);
    }

    @Override
    public TagKey<Biome> suggestType(
            TagKey<Biome> originalType,
            TagKey<Biome> suggestedType,
            double density,
            int maxHeight,
            int blockX,
            int blockY,
            int blockZ,
            int quarterX,
            int quarterY,
            int quarterZ
    ) {
        if (TerrainGenerator.isLand(quarterX, quarterZ, maxHeight)) {
            return suggestedType.equals(CommonBiomeTags.IS_END_CENTER)
                    ? suggestedType
                    : EndTags.IS_END_HIGH_OR_MIDLAND;
        } else {
            return suggestedType.equals(CommonBiomeTags.IS_END_CENTER)
                    ? CommonBiomeTags.IS_END_BARRENS
                    : CommonBiomeTags.IS_SMALL_END_ISLAND;
        }
    }

    @Override
    public boolean canProvideBiome(TagKey<Biome> suggestedType) {
        return false;
    }
}
