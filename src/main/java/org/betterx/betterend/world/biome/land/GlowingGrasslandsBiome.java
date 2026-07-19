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

public class GlowingGrasslandsBiome extends EndBiome.Config {
    public GlowingGrasslandsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(99, 228, 247)
                .fogDensity(1.3F)
                .particles(EndParticles.FIREFLY, 0.001F)
                .music(EndSounds.MUSIC_OPENSPACE)
                .loop(EndSounds.AMBIENT_GLOWING_GRASSLANDS)
                .waterAndFogColor(92, 250, 230)
                .plantsColor(73, 210, 209)
                .feature(EndLakeFeatures.END_LAKE_RARE)
                .feature(EndVegetationFeatures.LUMECORN)
                .feature(EndVegetationFeatures.BLOOMING_COOKSONIA)
                .feature(EndVegetationFeatures.SALTEAGO)
                .feature(EndVegetationFeatures.VAIOLUSH_FERN)
                .feature(EndVegetationFeatures.FRACTURN)
                .feature(EndVegetationFeatures.UMBRELLA_MOSS_RARE)
                .feature(EndVegetationFeatures.CREEPING_MOSS_RARE)
                .feature(EndVegetationFeatures.TWISTED_UMBRELLA_MOSS_RARE)
                .feature(EndVegetationFeatures.CHARNIA_CYAN)
                .feature(EndVegetationFeatures.CHARNIA_GREEN)
                .feature(EndVegetationFeatures.CHARNIA_LIGHT_BLUE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .structure(BiomeTags.HAS_END_CITY)
                .structure(EndStructures.END_VILLAGE)
                .spawn(EntityType.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.END_MOSS.defaultBlockState();
            }
        };
    }
}
