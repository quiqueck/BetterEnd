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

public class UmbrellaJungleBiome extends EndBiome.Config {
    public UmbrellaJungleBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(87, 223, 221)
                .waterAndFogColor(119, 198, 253)
                .foliageColorOverride(27, 183, 194)
                .fogDensity(2.3F)
                .particles(EndParticles.JUNGLE_SPORE, 0.001F)
                .music(EndSounds.MUSIC_FOREST)
                .loop(EndSounds.AMBIENT_UMBRELLA_JUNGLE)
                .feature(EndLakeFeatures.END_LAKE)
                .feature(EndVegetationFeatures.UMBRELLA_TREE)
                .feature(EndVegetationFeatures.JELLYSHROOM)
                .feature(EndVegetationFeatures.TWISTED_UMBRELLA_MOSS)
                .feature(EndVegetationFeatures.SMALL_JELLYSHROOM_FLOOR)
                .feature(EndVegetationFeatures.JUNGLE_GRASS)
                .feature(EndVegetationFeatures.CYAN_MOSS)
                .feature(EndVegetationFeatures.CYAN_MOSS_WOOD)
                .feature(EndVegetationFeatures.JUNGLE_FERN_WOOD)
                .feature(EndVegetationFeatures.SMALL_JELLYSHROOM_WALL)
                .feature(EndVegetationFeatures.SMALL_JELLYSHROOM_WOOD)
                .feature(EndVegetationFeatures.SMALL_JELLYSHROOM_CEIL)
                .feature(EndVegetationFeatures.JUNGLE_VINE)
                .feature(EndVegetationFeatures.CHARNIA_CYAN)
                .feature(EndVegetationFeatures.CHARNIA_GREEN)
                .feature(EndVegetationFeatures.CHARNIA_LIGHT_BLUE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .structure(BiomeTags.HAS_END_CITY)
                .structure(EndStructures.ETERNAL_PORTAL)
                .spawn(EntityType.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.JUNGLE_MOSS.defaultBlockState();
            }
        };
    }
}