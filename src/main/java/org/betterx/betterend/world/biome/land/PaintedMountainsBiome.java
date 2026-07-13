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
import net.minecraft.world.level.block.Blocks;
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
                SurfaceRules.RuleSource surfaceBlockRule = new SwitchRuleSource(
                        VerticalBandNoiseCondition.DEFAULT,
                        List.of(
                                SurfaceRules.state(Blocks.END_STONE.defaultBlockState()),
                                SurfaceRules.state(EndBlocks.FLAVOLITE.getBlock(SlotType.SOURCE).defaultBlockState()),
                                SurfaceRules.state(EndBlocks.VIOLECITE.getBlock(SlotType.SOURCE).defaultBlockState())
                        )
                );
                return SurfaceRuleBuilder.start().rule(surfaceBlockRule, 9);
            }
        };
    }
}
