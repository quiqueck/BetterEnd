package org.betterx.betterend.world.biome.land;

import org.betterx.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.surface.SulphuricSurfaceNoiseCondition;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import java.util.List;

public class SulphurSpringsBiome extends EndBiome.Config {

    public SulphurSpringsBiome() {
        super();
    }

    @Override
    public boolean hasCaves() {
        return false;
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .music(EndSounds.MUSIC_OPENSPACE)
                .loop(EndSounds.AMBIENT_SULPHUR_SPRINGS)
                .waterColor(25, 90, 157)
                .waterFogColor(30, 65, 61)
                .fogColor(207, 194, 62)
                .fogDensity(1.5F)
                .terrainHeight(0F)
                .particles(EndParticles.SULPHUR_PARTICLE, 0.001F)
                .feature(EndTerrainFeatures.GEYSER)
                .feature(EndTerrainFeatures.SURFACE_VENT)
                .feature(EndLakeFeatures.SULPHURIC_LAKE)
                .feature(EndTerrainFeatures.SULPHURIC_CAVE)
                .feature(EndVegetationFeatures.HYDRALUX)
                .feature(EndVegetationFeatures.CHARNIA_GREEN)
                .feature(EndVegetationFeatures.CHARNIA_ORANGE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .structure(EndStructures.ETERNAL_PORTAL)
                .spawn(EndEntities.END_FISH.type(), 50, 3, 8)
                .spawn(EndEntities.CUBOZOA.type(), 50, 3, 8)
                .spawn(EntityType.ENDERMAN, 50, 1, 4);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.FLAVOLITE.getBlock(SlotType.SOURCE).defaultBlockState();
            }

            @Override
            public BlockState getAltTopMaterial() {
                // The surface switch's "default" band (index 0). Brimstone rather than plain end
                // stone, so the biome floor matches the brimstone rims of its sulphuric pools.
                return EndBlocks.BRIMSTONE.defaultBlockState();
            }

            @Override
            public boolean generateFloorRule() {
                return false;
            }

            @Override
            public SurfaceRuleBuilder surface() {
                RuleSource surfaceBlockRule = new SwitchRuleSource(
                        new SulphuricSurfaceNoiseCondition(),
                        List.of(
                                SurfaceRules.state(surfaceMaterial().getAltTopMaterial()),
                                SurfaceRules.state(surfaceMaterial().getTopMaterial()),
                                SULPHURIC_ROCK,
                                BRIMSTONE
                        )
                );
                return super
                        .surface()
                        .rule(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, surfaceBlockRule), 2)
                        .rule(
                                SurfaceRules.ifTrue(
                                        SurfaceRules.stoneDepthCheck(5, false, CaveSurface.FLOOR),
                                        surfaceBlockRule
                                ), 2
                        );
            }
        };
    }
}
