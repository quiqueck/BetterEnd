package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DryShrublandBiome extends EndBiome.Config {
    public DryShrublandBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(132, 35, 13)
                .fogDensity(1.2F)
                .waterAndFogColor(113, 88, 53)
                .plantsColor(237, 122, 66)
                .music(EndSounds.MUSIC_OPENSPACE)
                .feature(EndVegetationFeatures.LUCERNIA_BUSH_RARE)
                .feature(EndVegetationFeatures.ORANGO)
                .feature(EndVegetationFeatures.AERIDIUM)
                .feature(EndVegetationFeatures.LUTEBUS)
                .feature(EndVegetationFeatures.LAMELLARIUM)
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
