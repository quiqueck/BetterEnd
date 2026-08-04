package org.betterx.betterend.world.biome.cave;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.features.EndPlacedCaveFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;
import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LushAuroraCaveBiome extends EndCaveBiome.Config<LushAuroraCaveBiome> {
    public static final MapCodec<Biome> CODEC = EndCaveBiome.simpleCaveBiomeCodec(LushAuroraCaveBiome.Biome::new);
    public static final KeyDispatchDataCodec<LushAuroraCaveBiome.Biome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    public static class Biome extends EndCaveBiome {
        @Override
        public KeyDispatchDataCodec<? extends EndCaveBiome> codec() {
            return LushAuroraCaveBiome.KEY_CODEC;
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

        @Override
        public BlockState getCeil(BlockPos pos) {
            return EndTerrainBlocks.CAVE_MOSS.defaultBlockState();
        }
    }

    public LushAuroraCaveBiome(EndBiomeKey<LushAuroraCaveBiome, ?> key) {
        super(key);
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        super.addCustomBuildData(builder);
        builder.feature(EndPlacedCaveFeatures.STALACTITE_CLUSTER_CAVEMOSS)
               .feature(EndPlacedCaveFeatures.BIG_AURORA_CRYSTAL)
               .feature(EndPlacedCaveFeatures.CAVE_LUSH_FLOOR_PATCH)
               .feature(EndPlacedCaveFeatures.CAVE_LUSH_CEILING_PATCH)
               .feature(EndPlacedCaveFeatures.CAVE_GRASS_SCATTER)
               .feature(EndPlacedCaveFeatures.CAVE_BUSH_SCATTER)
               .feature(EndPlacedCaveFeatures.CAVE_PUMPKIN_PLACED);
        builder.fogColor(150, 30, 68)
               .fogDensity(2.0F)
               .plantsColor(108, 25, 46)
               .waterAndFogColor(186, 77, 237)
               .particles(EndParticles.GLOWING_SPHERE, 0.001F)
        ;
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndTerrainBlocks.CAVE_MOSS.defaultBlockState();
            }
        };
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
        return new LushAuroraCaveBiome.Biome(fogDensity, key.key, generatorData, terrainHeight, genChance, edgeSize, vertical, edge, parent, hasCave, surface);
    }
}
