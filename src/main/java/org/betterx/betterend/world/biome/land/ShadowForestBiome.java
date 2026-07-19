package org.betterx.betterend.world.biome.land;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ShadowForestBiome extends EndBiome.Config {
    public ShadowForestBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(0, 0, 0)
                .fogDensity(2.5F)
                .plantsColor(45, 45, 45)
                .waterAndFogColor(42, 45, 80)
                .particles(ParticleTypes.MYCELIUM, 0.01F)
                .loop(EndSounds.AMBIENT_CHORUS_FOREST)
                .music(EndSounds.MUSIC_DARK)
                .feature(EndOreFeatures.VIOLECITE_LAYER)
                .feature(EndLakeFeatures.END_LAKE_RARE)
                .feature(EndVegetationFeatures.DRAGON_TREE)
                .feature(EndVegetationFeatures.DRAGON_TREE_BUSH)
                .feature(EndVegetationFeatures.SHADOW_PLANT)
                .feature(EndVegetationFeatures.MURKWEED)
                .feature(EndVegetationFeatures.NEEDLEGRASS)
                .feature(EndVegetationFeatures.SHADOW_BERRY)
                .feature(EndVegetationFeatures.TWISTED_VINE)
                .feature(EndVegetationFeatures.PURPLE_POLYPORE)
                .feature(EndVegetationFeatures.TAIL_MOSS)
                .feature(EndVegetationFeatures.TAIL_MOSS_WOOD)
                .feature(EndVegetationFeatures.CHARNIA_PURPLE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .structure(BiomeTags.HAS_END_CITY)
                .structure(EndStructures.ETERNAL_PORTAL)
                .spawn(EndEntities.SHADOW_WALKER.type(), 80, 2, 4)
                .spawn(EntityType.ENDERMAN, 3, 1, 4)
                .spawn(EntityType.PHANTOM, 1, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.SHADOW_GRASS.defaultBlockState();
            }
        };
    }
}
