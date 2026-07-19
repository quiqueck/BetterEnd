package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlossomingSpiresBiome extends EndBiome.Config {
    public BlossomingSpiresBiome() {
        super();
    }

    @Override
    public boolean hasCaves() {
        return false;
    }

    @Override
    public boolean hasReturnGateway() {
        return false;
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(241, 146, 229)
                .fogDensity(1.7F)
                .plantsColor(122, 45, 122)
                .music(EndSounds.MUSIC_FOREST)
                .loop(EndSounds.AMBIENT_BLOSSOMING_SPIRES)
                .feature(EndTerrainFeatures.SPIRE)
                .feature(EndTerrainFeatures.FLOATING_SPIRE)
                .feature(EndVegetationFeatures.TENANEA)
                .feature(EndVegetationFeatures.TENANEA_BUSH)
                .feature(EndVegetationFeatures.BULB_VINE)
                .feature(EndVegetationFeatures.BUSHY_GRASS)
                .feature(EndVegetationFeatures.BUSHY_GRASS_WG)
                .feature(EndVegetationFeatures.BLOSSOM_BERRY)
                .feature(EndVegetationFeatures.TWISTED_MOSS)
                .feature(EndVegetationFeatures.TWISTED_MOSS_WOOD)
                .feature(EndTerrainFeatures.SILK_MOTH_NEST)
                .spawn(EntityType.ENDERMAN, 3, 1, 4)
                .spawn(EndEntities.SILK_MOTH.type(), 5, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.PINK_MOSS.defaultBlockState();
            }

            @Override
            public BlockState getAltTopMaterial() {
                return getTopMaterial();
            }
        };
    }
}
