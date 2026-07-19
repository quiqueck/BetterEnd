package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MegalakeGroveBiome extends EndBiome.Config {
    public MegalakeGroveBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .structure(EndStructures.MEGALAKE_SMALL)
                .plantsColor(73, 210, 209)
                .fogColor(178, 209, 248)
                .waterAndFogColor(96, 163, 255)
                .fogDensity(2.0F)
                .particles(EndParticles.GLOWING_SPHERE, 0.001F)
                .music(EndSounds.MUSIC_WATER)
                .loop(EndSounds.AMBIENT_MEGALAKE_GROVE)
                .terrainHeight(0F)
                .feature(EndVegetationFeatures.LACUGROVE)
                .feature(EndVegetationFeatures.END_LOTUS)
                .feature(EndVegetationFeatures.END_LOTUS_LEAF)
                .feature(EndVegetationFeatures.BUBBLE_CORAL_RARE)
                .feature(EndVegetationFeatures.END_LILY_RARE)
                .feature(EndVegetationFeatures.UMBRELLA_MOSS)
                //.feature(EndVegetationFeatures.PEARLBERRY)
                .feature(EndVegetationFeatures.CREEPING_MOSS)
                .feature(EndVegetationFeatures.CHARNIA_CYAN)
                .feature(EndVegetationFeatures.CHARNIA_LIGHT_BLUE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .feature(EndVegetationFeatures.MENGER_SPONGE)
                .spawn(EndEntities.DRAGONFLY.type(), 20, 1, 3)
                .spawn(EndEntities.END_FISH.type(), 20, 3, 8)
                .spawn(EndEntities.CUBOZOA.type(), 50, 3, 8)
                .spawn(EndEntities.END_SLIME.type(), 5, 1, 2)
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
