package org.betterx.betterend.world.biome.land;

import org.betterx.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.surface.VerticalBandNoiseCondition;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class PaintedMountainsBiome extends EndBiome.Config {
    public PaintedMountainsBiome() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .structure(EndStructures.PAINTED_MOUNTAIN)
                .fogColor(226, 239, 168)
                .fogDensity(2)
                .waterAndFogColor(192, 180, 131)
                .music(EndSounds.MUSIC_OPENSPACE)
                .loop(EndSounds.AMBIENT_DUST_WASTELANDS)
                .particles(ParticleTypes.WHITE_ASH, 0.01F)
                .spawn(EntityType.ENDERMAN, 50, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndBlocks.ENDSTONE_DUST.defaultBlockState();
            }

            public SurfaceRuleBuilder surface() {
                // A full rainbow of coloured end-stone, no plain end stone - so the painted bands
                // actually read as colour instead of washing back into the default terrain.
                SurfaceRules.RuleSource surfaceBlockRule = new SwitchRuleSource(
                        VerticalBandNoiseCondition.DEFAULT,
                        List.of(
                                SurfaceRules.state(EndBlocks.FLAVOLITE.getBlock(SlotType.SOURCE).defaultBlockState()),
                                SurfaceRules.state(EndBlocks.VIOLECITE.getBlock(SlotType.SOURCE).defaultBlockState()),
                                SurfaceRules.state(EndBlocks.VIRID_JADESTONE.getBlock(SlotType.SOURCE).defaultBlockState()),
                                SurfaceRules.state(EndBlocks.AZURE_JADESTONE.getBlock(SlotType.SOURCE).defaultBlockState()),
                                SurfaceRules.state(EndBlocks.SANDY_JADESTONE.getBlock(SlotType.SOURCE).defaultBlockState())
                        )
                );
                return SurfaceRuleBuilder.start().rule(surfaceBlockRule, 9);
            }
        };
    }
}
