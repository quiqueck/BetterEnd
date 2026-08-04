package org.betterx.betterend.world.biome.land;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.BaseSurfaceRuleBuilder;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class DustWastelandsBiome extends EndBiome.Config {
    public DustWastelandsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(226, 239, 168)
                .fogDensity(2)
                .waterAndFogColor(192, 180, 131)
                .terrainHeight(1.5F)
                .particles(ParticleTypes.WHITE_ASH, 0.01F)
                .loop(EndSounds.AMBIENT_DUST_WASTELANDS)
                .music(EndSounds.MUSIC_OPENSPACE)
                .structure(EndStructures.END_VILLAGE)
                .structure(EndStructures.ETERNAL_PORTAL)
                .feature(EndOreFeatures.FLAVOLITE_LAYER)
                .spawn(EntityType.ENDERMAN, 50, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState();
            }

            @Override
            public SurfaceRuleBuilder surface() {
                return super
                        .surface()
                        // Underside stays end stone so the (falling) dust never hangs off a ceiling.
                        .ceil(Blocks.END_STONE.defaultBlockState())
                        // Dust layer of randomized depth: addSurfaceDepth=true varies the thickness
                        // per column instead of a flat 5. It sits on the END_STONE filler below, so
                        // the falling dust always has solid ground under it. Priority must beat the
                        // FILLER (900) - a small number like the old 4 sorts after it and never runs.
                        .rule(SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(2, true, CaveSurface.FLOOR),
                                SurfaceRules.state(EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState())
                        ), BaseSurfaceRuleBuilder.SUB_SURFACE_PRIORITY);
            }
        };
    }
}
