package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LanternWoodsBiome extends EndBiome.Config {
    public LanternWoodsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(189, 82, 70)
                .fogDensity(1.1F)
                .waterAndFogColor(171, 234, 226)
                .plantsColor(254, 85, 57)
                .music(EndSounds.MUSIC_FOREST)
                .particles(EndParticles.GLOWING_SPHERE, 0.001F)
                .feature(EndLakeFeatures.END_LAKE_NORMAL)
                .feature(EndVegetationFeatures.FLAMAEA)
                .feature(EndVegetationFeatures.LUCERNIA)
                .feature(EndVegetationFeatures.LUCERNIA_BUSH)
                .feature(EndVegetationFeatures.FILALUX)
                .feature(EndVegetationFeatures.AERIDIUM)
                .feature(EndVegetationFeatures.LAMELLARIUM)
                .feature(EndVegetationFeatures.BOLUX_MUSHROOM)
                .feature(EndVegetationFeatures.AURANT_POLYPORE)
                .feature(EndVegetationFeatures.POND_ANEMONE)
                .feature(EndVegetationFeatures.CHARNIA_ORANGE)
                .feature(EndVegetationFeatures.CHARNIA_RED)
                .feature(EndVegetationFeatures.RUSCUS)
                .feature(EndVegetationFeatures.RUSCUS_WOOD)
                .structure(BiomeTags.HAS_END_CITY)
                .structure(EndStructures.ETERNAL_PORTAL)
                .spawn(EntityType.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.RUTISCUS.defaultBlockState();
            }
        };
    }
}
