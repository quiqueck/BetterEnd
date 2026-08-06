package org.betterx.betterend.world.biome.land;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.registry.features.EndLakeFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.surface.SulphuricSurfaceNoiseCondition;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.BaseSurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.world.entity.EntityTypes;
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
                .feature(EndTerrainFeatures.SULPHUR_SPIKE)
                .feature(EndTerrainFeatures.SULPHUR_SPIKE_HANGING)
                .feature(EndLakeFeatures.SULPHURIC_LAKE)
                .feature(EndVegetationFeatures.HYDRALUX)
                .feature(EndVegetationFeatures.CHARNIA_GREEN)
                .feature(EndVegetationFeatures.CHARNIA_ORANGE)
                .feature(EndVegetationFeatures.CHARNIA_RED_RARE)
                .structure(EndStructures.ETERNAL_PORTAL)
                .structure(EndStructures.SULPHURIC_CAVE)
                .spawn(EndEntities.END_FISH.type(), 50, 3, 8)
                .spawn(EndEntities.CUBOZOA.type(), 50, 3, 8)
                .spawn(EntityTypes.ENDERMAN, 1, 1, 4);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndStoneBlocks.BRIMSTONE.defaultBlockState();
            }

            @Override
            public BlockState getAltTopMaterial() {
                // The surface switch's "default" band (index 0). Brimstone rather than plain end
                // stone, so the biome floor matches the brimstone rims of its sulphuric pools.
                return EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE).defaultBlockState();
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
                // The switch must out-prioritise super.surface()'s unconditional END_STONE filler
                // (FILLER_PRIORITY = 900). PriorityLinkedList orders highest-priority-first and the
                // sequence is first-match-wins, so a small number like 2 sorts AFTER the filler and
                // never runs - which is why the surface was coming out as plain end stone.
                return super
                        .surface()
                        .rule(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, surfaceBlockRule),
                                BaseSurfaceRuleBuilder.TOP_SURFACE_PRIORITY
                        )
                        .rule(
                                SurfaceRules.ifTrue(
                                        SurfaceRules.stoneDepthCheck(5, false, CaveSurface.FLOOR),
                                        surfaceBlockRule
                                ), BaseSurfaceRuleBuilder.SUB_SURFACE_PRIORITY
                        );
            }
        };
    }
}
