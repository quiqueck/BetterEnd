package org.betterx.betterend.world.biome.air;

import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.world.entity.EntityTypes;

public class BiomeIceStarfield extends EndBiome.Config {
    public BiomeIceStarfield() {
        super();
    }

    @Override
    public boolean hasCaves() {
        return false;
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder.structure(EndStructures.GIANT_ICE_STAR)
               .fogColor(224, 245, 254)
               .temperature(0F)
               .fogDensity(2.2F)
               .foliageColorOverride(193, 244, 244)
               .genChance(0.01F)
               .particles(EndParticles.SNOWFLAKE, 0.002F)
               .feature(EndTerrainFeatures.ICE_STAR)
               .feature(EndTerrainFeatures.ICE_STAR_SMALL)
               .spawn(EntityTypes.ENDERMAN, 3, 1, 4);
    }
}
