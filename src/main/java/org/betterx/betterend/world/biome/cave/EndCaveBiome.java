package org.betterx.betterend.world.biome.cave;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndCarvers;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;
import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeData;

import com.mojang.datafixers.util.Function11;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndCaveBiome extends EndBiome {
    public static final MapCodec<EndCaveBiome> CODEC = simpleCaveBiomeCodec(EndCaveBiome::new);


    public static <T extends EndCaveBiome> MapCodec<T> simpleCaveBiomeCodec(final Function11<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, Boolean, SurfaceMaterialProvider, T> factory) {
        return codec(
                Codec.BOOL.fieldOf("has_caves").orElse(true).forGetter(EndBiome::hasCaves),
                SurfaceMaterialProvider.CODEC.fieldOf("surface")
                                             .orElse(new DefaultSurfaceMaterialProvider())
                                             .forGetter(o -> o.surfMatProv),
                factory
        );
    }

    public static final KeyDispatchDataCodec<EndCaveBiome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    @Override
    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
    }


    protected EndCaveBiome(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull BiomeGenerationDataContainer generatorData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent,
            boolean hasCaves,
            SurfaceMaterialProvider surface
    ) {
        super(
                fogDensity, biome, generatorData, terrainHeight,
                genChance, edgeSize, vertical,
                edge, parent,
                hasCaves,
                surface
        );
    }

    public static abstract class Config<C extends Config<?>> extends EndBiome.Config {
        protected Config(EndBiomeKey<C, ?> key) {
            super();
        }


        @Override
        public void addCustomBuildData(EndBiomeBuilder builder) {
            builder.carver(EndCarvers.ROUND_CAVE)
                   .carver(EndCarvers.TUNNEL_CAVE)
                   .music(EndSounds.MUSIC_CAVES)
                   .loop(EndSounds.AMBIENT_CAVES);
        }

        @Override
        public boolean hasCaves() {
            return false;
        }

        @Override
        public boolean hasReturnGateway() {
            return false;
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
                @Nullable ResourceKey<Biome> edge,
                @Nullable ResourceKey<Biome> parent,
                boolean hasCave,
                SurfaceMaterialProvider surface
        ) {
            return new EndCaveBiome(
                    fogDensity, key.key, generatorData,
                    terrainHeight, genChance, edgeSize, vertical, edge, parent,
                    hasCave, surface
            );
        }

    }

    // Runtime hooks used by CaveSurfaceCoatFeature to coat exposed End-stone surfaces per cave biome.
    public BlockState getCeil(BlockPos pos) {
        return null;
    }

    public BlockState getWall(BlockPos pos) {
        return null;
    }
}
