package org.betterx.betterend.world.biome.land;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.surface.SplitNoiseCondition;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.BaseSurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import java.util.List;

public class CrystalMountainsBiome extends EndBiome.Config {
    public CrystalMountainsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .structure(EndStructures.MOUNTAIN)
                .plantsColor(255, 133, 211)
                .music(EndSounds.MUSIC_OPENSPACE)
                .feature(EndVegetationFeatures.CRYSTAL_GRASS)
                .feature(EndVegetationFeatures.CRYSTAL_MOSS_COVER)
                .spawn(EntityType.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndTerrainBlocks.CRYSTAL_MOSS.defaultBlockState();
            }

            @Override
            public SurfaceRuleBuilder surface() {
                // A crystal-moss / end-moss split - no end stone, so nothing bare shows on the surface.
                SurfaceRules.RuleSource surfaceBlockRule = new SwitchRuleSource(
                        new SplitNoiseCondition(),
                        List.of(
                                SurfaceRules.state(EndTerrainBlocks.END_MOSS.defaultBlockState()),
                                SurfaceRules.state(EndTerrainBlocks.CRYSTAL_MOSS.defaultBlockState())
                        )
                );
                return super
                        .surface()
                        // ONE top layer only: crystal/end moss are grass-like blocks and must not stack
                        // (multiple moss layers on top of each other look wrong). ON_FLOOR paints just the
                        // top floor block; end stone stays below. Priority must beat the FILLER (900) or
                        // the rule is dead - that was the old bug (1).
                        .rule(SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                surfaceBlockRule
                        ), BaseSurfaceRuleBuilder.SUB_SURFACE_PRIORITY)
                        // Near-vertical faces aren't ON_FLOOR, so cover them explicitly - one block deep
                        // so the moss stays a single layer here too.
                        .steep(EndTerrainBlocks.CRYSTAL_MOSS.defaultBlockState(), 1);
            }
        };
    }
}
