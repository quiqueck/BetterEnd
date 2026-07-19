package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FoggyMushroomlandBiome extends EndBiome.Config {
    public FoggyMushroomlandBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .structure(EndStructures.GIANT_MOSSY_GLOWSHROOM)
                .plantsColor(73, 210, 209)
                .fogColor(41, 122, 173)
                .fogDensity(3)
                .waterAndFogColor(119, 227, 250)
                .particles(EndParticles.GLOWING_SPHERE, 0.001F)
                .loop(EndSounds.AMBIENT_FOGGY_MUSHROOMLAND)
                .music(EndSounds.MUSIC_FOREST)
                .feature(EndLakeFeatures.END_LAKE)
                .feature(EndVegetationFeatures.MOSSY_GLOWSHROOM)
                .feature(EndVegetationFeatures.BLUE_VINE)
                .feature(EndVegetationFeatures.UMBRELLA_MOSS)
                .feature(EndVegetationFeatures.CREEPING_MOSS)
                .feature(EndVegetationFeatures.DENSE_VINE)
                //.featur(EndVegetationFeatures.PEARLBERRY)
                .feature(EndVegetationFeatures.CYAN_MOSS)
                .feature(EndVegetationFeatures.CYAN_MOSS_WOOD)
                .feature(EndVegetationFeatures.END_LILY)
                .feature(EndVegetationFeatures.BUBBLE_CORAL)
                .feature(EndVegetationFeatures.CHARNIA_CYAN)
                .feature(EndVegetationFeatures.CHARNIA_LIGHT_BLUE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .structure(BiomeTags.HAS_END_CITY)
                .structure(EndStructures.ETERNAL_PORTAL)
                .spawn(EndEntities.DRAGONFLY.type(), 80, 2, 5)
                .spawn(EndEntities.END_FISH.type(), 20, 2, 5)
                .spawn(EndEntities.CUBOZOA.type(), 10, 3, 8)
                .spawn(EndEntities.END_SLIME.type(), 10, 1, 2)
                .spawn(EntityType.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.END_MOSS.defaultBlockState();
            }

            @Override
            public BlockState getAltTopMaterial() {
                return EndBlocks.END_MYCELIUM.defaultBlockState();
            }
        };


    }
}
