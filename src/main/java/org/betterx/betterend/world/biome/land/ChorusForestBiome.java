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
import net.minecraft.data.worldgen.placement.EndPlacements;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class ChorusForestBiome extends EndBiome.Config {
    public ChorusForestBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(87, 26, 87)
                .fogDensity(1.5F)
                .plantsColor(122, 45, 122)
                .waterAndFogColor(73, 30, 73)
                .particles(ParticleTypes.PORTAL, 0.01F)
                .loop(EndSounds.AMBIENT_CHORUS_FOREST)
                .music(EndSounds.MUSIC_DARK)
                .feature(EndOreFeatures.VIOLECITE_LAYER)
                .feature(EndLakeFeatures.END_LAKE_RARE)
                .feature(EndVegetationFeatures.PYTHADENDRON_TREE)
                .feature(EndVegetationFeatures.PYTHADENDRON_BUSH)
                .feature(Decoration.VEGETAL_DECORATION, EndPlacements.CHORUS_PLANT)
                .feature(EndVegetationFeatures.PURPLE_POLYPORE)
                .feature(EndVegetationFeatures.CHORUS_GRASS)
                .feature(EndVegetationFeatures.CHORUS_MUSHROOM)
                .feature(EndVegetationFeatures.TAIL_MOSS)
                .feature(EndVegetationFeatures.TAIL_MOSS_WOOD)
                .feature(EndVegetationFeatures.CHARNIA_PURPLE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .structure(BiomeTags.HAS_END_CITY)
                .structure(EndStructures.ETERNAL_PORTAL)
                .spawn(EndEntities.END_SLIME.type(), 5, 1, 2)
                .spawn(EntityType.ENDERMAN, 3, 1, 4);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.CHORUS_NYLIUM.defaultBlockState();
            }
        };
    }
}
