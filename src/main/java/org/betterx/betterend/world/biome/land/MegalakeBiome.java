package org.betterx.betterend.world.biome.land;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import de.ambertation.wover.surface.api.Conditions;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;

import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MegalakeBiome extends EndBiome.Config {
    public MegalakeBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .structure(EndStructures.MEGALAKE)
                .plantsColor(73, 210, 209)
                .fogColor(178, 209, 248)
                .waterAndFogColor(96, 163, 255)
                .fogDensity(1.75F)
                .music(EndSounds.MUSIC_WATER)
                .loop(EndSounds.AMBIENT_MEGALAKE)
                .terrainHeight(0F)
                .feature(EndVegetationFeatures.END_LOTUS)
                .feature(EndVegetationFeatures.END_LOTUS_LEAF)
                .feature(EndVegetationFeatures.BUBBLE_CORAL_RARE)
                .feature(EndVegetationFeatures.END_LILY_RARE)
                .feature(EndVegetationFeatures.UMBRELLA_MOSS)
                .feature(EndVegetationFeatures.CREEPING_MOSS)
                //.feature(EndVegetationFeatures.PEARLBERRY)
                .feature(EndVegetationFeatures.CHARNIA_CYAN)
                .feature(EndVegetationFeatures.CHARNIA_LIGHT_BLUE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .feature(EndVegetationFeatures.MENGER_SPONGE)
                .spawn(EndEntities.DRAGONFLY.type(), 50, 1, 3)
                .spawn(EndEntities.END_FISH.type(), 50, 3, 8)
                .spawn(EndEntities.CUBOZOA.type(), 50, 3, 8)
                .spawn(EndEntities.END_SLIME.type(), 5, 1, 2)
                .spawn(EntityType.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndTerrainBlocks.END_MOSS.defaultBlockState();
            }

            @Override
            public BlockState getAltTopMaterial() {
                return EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState();
            }

            @Override
            public SurfaceRuleBuilder surface() {
                // Mostly end moss: the threshold sits at the ~10th percentile of the noise (plus
                // roughness), so only about 10% of the surface is end stone dust.
                return SurfaceRuleBuilder
                        .start()
                        .chancedFloor(
                                getTopMaterial(),
                                getAltTopMaterial(),
                                Conditions.threshold(4141, -0.58, UniformFloat.of(-0.4f, 0.4f), 0.1, 0.1)
                        )
                        .filler(getUnderMaterial());
            }
        };
    }
}
