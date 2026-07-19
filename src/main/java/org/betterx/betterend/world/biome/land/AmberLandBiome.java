package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AmberLandBiome extends EndBiome.Config {
    public AmberLandBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(255, 184, 71)
                .fogDensity(2.0F)
                .plantsColor(219, 115, 38)
                .waterAndFogColor(145, 108, 72)
                .music(EndSounds.MUSIC_FOREST)
                .loop(EndSounds.AMBIENT_AMBER_LAND)
                .particles(EndParticles.AMBER_SPHERE, 0.001F)
                .feature(EndOreFeatures.AMBER_ORE)
                .feature(EndLakeFeatures.END_LAKE_RARE)
                .feature(EndVegetationFeatures.HELIX_TREE)
                .feature(EndVegetationFeatures.LANCELEAF)
                .feature(EndVegetationFeatures.GLOW_PILLAR)
                .feature(EndVegetationFeatures.AMBER_GRASS)
                .feature(EndVegetationFeatures.AMBER_ROOT)
                .feature(EndVegetationFeatures.BULB_MOSS)
                .feature(EndVegetationFeatures.BULB_MOSS_WOOD)
                .feature(EndVegetationFeatures.CHARNIA_ORANGE)
                .feature(EndVegetationFeatures.CHARNIA_RED)
                .structure(BiomeTags.HAS_END_CITY)
                .spawn(EntityType.ENDERMAN, 3, 1, 4)
                .spawn(EndEntities.END_SLIME.type(), 30, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.AMBER_MOSS.defaultBlockState();
            }
        };
    }
}
