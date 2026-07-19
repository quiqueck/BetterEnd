package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DragonGraveyardsBiome extends EndBiome.Config {
    public DragonGraveyardsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .genChance(0.1f)
                .fogColor(244, 46, 79)
                .fogDensity(1.3F)
                .particles(EndParticles.FIREFLY, 0.0007F)
                .music(EndSounds.MUSIC_OPENSPACE)
                .loop(EndSounds.AMBIENT_GLOWING_GRASSLANDS)
                .waterAndFogColor(203, 59, 167)
                .plantsColor(244, 46, 79)
                .feature(EndTerrainFeatures.OBSIDIAN_PILLAR_BASEMENT)
                .feature(EndOreFeatures.DRAGON_BONE_BLOCK_ORE)
                .feature(EndTerrainFeatures.FALLEN_PILLAR)
                .feature(EndTerrainFeatures.OBSIDIAN_BOULDER)
                .feature(EndVegetationFeatures.GIGANTIC_AMARANITA)
                .feature(EndVegetationFeatures.LARGE_AMARANITA)
                .feature(EndVegetationFeatures.SMALL_AMARANITA)
                .feature(EndVegetationFeatures.GLOBULAGUS)
                .feature(EndVegetationFeatures.CLAWFERN)
                .structure(EndStructures.ETERNAL_PORTAL)
                .spawn(EntityType.ENDERMAN, 1, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.SANGNUM.defaultBlockState();
            }
        };
    }
}
