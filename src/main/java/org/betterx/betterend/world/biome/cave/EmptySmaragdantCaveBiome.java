package org.betterx.betterend.world.biome.cave;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.features.EndPlacedCaveFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;
import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptySmaragdantCaveBiome extends EndCaveBiome.Config<EmptySmaragdantCaveBiome> {
    public static final MapCodec<Biome> CODEC = EndCaveBiome.simpleCaveBiomeCodec(
            EmptySmaragdantCaveBiome.Biome::new);
    public static final KeyDispatchDataCodec<EmptySmaragdantCaveBiome.Biome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    public static class Biome extends EndCaveBiome {
        @Override
        public KeyDispatchDataCodec<? extends EndCaveBiome> codec() {
            return EmptySmaragdantCaveBiome.KEY_CODEC;
        }

        protected Biome(
                float fogDensity,
                @NotNull ResourceKey<net.minecraft.world.level.biome.Biome> biome,
                @NotNull BiomeGenerationDataContainer generatorData,
                float terrainHeight,
                float genChance,
                int edgeSize,
                boolean vertical,
                @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> edge,
                @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> parent,
                boolean hasCaves,
                SurfaceMaterialProvider surface
        ) {
            super(
                    fogDensity, biome, generatorData, terrainHeight,
                    genChance, edgeSize, vertical,
                    edge, parent,
                    hasCaves, surface
            );
        }
    }

    public EmptySmaragdantCaveBiome(EndBiomeKey<EmptySmaragdantCaveBiome, ?> key) {
        super(key);
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        super.addCustomBuildData(builder);
        builder.feature(EndPlacedCaveFeatures.STALACTITE_CLUSTER_PLAIN)
               .feature(EndPlacedCaveFeatures.STALAGMITE_SCATTER)
               .feature(EndPlacedCaveFeatures.STALACTITE_SCATTER)
               .feature(EndPlacedCaveFeatures.SMARAGDANT_CRYSTAL)
               .feature(EndPlacedCaveFeatures.SMARAGDANT_SHARD_SCATTER);
        builder.fogColor(0, 253, 182)
               .fogDensity(2.0F)
               .plantsColor(0, 131, 145)
               .waterAndFogColor(31, 167, 212)
               .particles(EndParticles.SMARAGDANT, 0.001F);
    }

    @Override
    public @NotNull EndBiome instantiateBiome(
            float fogDensity,
            BiomeKey<?> key,
            @NotNull BiomeGenerationDataContainer generatorData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> edge,
            @Nullable ResourceKey<net.minecraft.world.level.biome.Biome> parent,
            boolean hasCave,
            SurfaceMaterialProvider surface
    ) {
        return new EmptySmaragdantCaveBiome.Biome(fogDensity, key.key, generatorData, terrainHeight, genChance, edgeSize, vertical, edge, parent, hasCave, surface);
    }
}
