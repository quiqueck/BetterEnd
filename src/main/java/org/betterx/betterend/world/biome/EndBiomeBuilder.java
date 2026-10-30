package org.betterx.betterend.world.biome;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.world.biome.cave.EndCaveBiome;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.EndSounds;
import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.EndPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndBiomeBuilder extends WoverBiomeBuilder.AbstractWoverBiomeBuilder<EndBiomeBuilder> {
    /**
     * Every BetterEnd Biome competes with a slightly higher weight than vanilla Biomes and Biomes of other mods,
     * which use WoVer's default {@code genChance} of {@code 1.0}. The factor is applied to every
     * {@code genChance} (top-level, sub- and edge-biomes alike), so the weights between BetterEnd Biomes stay
     * as configured.
     */
    private static final float GEN_CHANCE_FACTOR = 1.25f;

    protected boolean hasCave = false;
    protected SurfaceMaterialProvider surface;
    protected BiomeFactory biomeFactory;

    protected EndBiomeBuilder(
            BiomeBootstrapContext context,
            BiomeKey<EndBiomeBuilder> key,
            TagKey<Biome>... biomeTag
    ) {
        super(context, key);
        this.surface = EndBiome.Config.DEFAULT_MATERIAL;
        this
                .music(SoundEvents.MUSIC_END)
                .waterColor(BiomeBuilder.DEFAULT_END_WATER_COLOR)
                .waterFogColor(BiomeBuilder.DEFAULT_END_WATER_FOG_COLOR)
                .fogColor(BiomeBuilder.DEFAULT_END_FOG_COLOR)
                .skyColor(BiomeBuilder.DEFAULT_END_SKY_COLOR)
                .mood(EndSounds.AMBIENT_DUST_WASTELANDS)
                .temperature(BiomeBuilder.DEFAULT_END_TEMPERATURE)
                .downfall(BiomeBuilder.DEFAULT_END_WETNESS)
                .hasPrecipitation(false)
                .intendedPlacement(biomeTag.length > 0 ? biomeTag[0] : null)
                .tag(biomeTag);
    }

    EndBiomeBuilder configure(EndBiome.Config biomeConfig) {
        // 26.2: SurfaceRules.isBiome resolves its keys into a HolderSet eagerly, so
        // SurfaceRuleBuilder.build() gained a HolderGetter<Biome> parameter. The lookup is only
        // consulted when a biome filter was set on the builder - which none of BetterEnd's
        // SurfaceMaterialProviders do - but the bootstrap context we were constructed with provides a
        // real one anyway, so no null is handed to WoVer.
        // Cave biomes own the whole column below the cave-biome top, and carving runs after the surface pass,
        // so a cave biome's surface rule never reaches a cave face (CaveSurfaceCoatFeature paints those) - it
        // only ever painted the top of a low island in that column, e.g. with lush caves' cave moss (#615).
        if (!(biomeConfig instanceof EndCaveBiome.Config<?>)) {
            this.startSurface()
                .rule(biomeConfig.surfaceMaterial().surface().build(bootstrapContext.lookup(Registries.BIOME)))
                .finishSurface();
        }

        this.surface = biomeConfig.surfaceMaterial();

        this.hasCave = biomeConfig.hasCaves();
        if (biomeConfig.hasReturnGateway()) {
            this.feature(GenerationStep.Decoration.SURFACE_STRUCTURES, EndPlacements.END_GATEWAY_RETURN);
        }
        biomeConfig.addCustomBuildData(this);

        EndFeatures.addDefaultFeatures(this, biomeConfig.hasCaves());
        this.biomeFactory = biomeConfig;

        return this;
    }

    public static <C extends EndBiome.Config> EndBiomeKey<C, ?> createKey(
            @NotNull String name
    ) {
        return new EndBiomeKey<>(
                BetterEnd.C.id(name.replace(' ', '_').toLowerCase(Locale.ROOT)),
                null
        );
    }


    public static <C extends EndBiome.Config, PC extends EndBiome.Config> EndBiomeKey<C, PC> createKey(
            @NotNull String name,
            @Nullable EndBiomeKey<PC, ?> parentOrNull
    ) {
        return new EndBiomeKey<>(
                BetterEnd.C.id(name.replace(' ', '_').toLowerCase(Locale.ROOT)),
                parentOrNull
        );
    }

    @Override
    public void registerBiomeData(BootstrapContext<BiomeData> dataContext) {
        final EndBiome biome = biomeFactory.instantiateBiome(fogDensity, key, new BiomeGenerationDataContainer(parameters, intendedPlacement), terrainHeight, genChance * GEN_CHANCE_FACTOR, edgeSize, vertical, edge, parent, hasCave, surface);
        biome.datagenSetup(dataContext);
        dataContext.register(key.dataKey, biome);
    }

    @FunctionalInterface
    public interface BiomeFactory {
        @NotNull
        EndBiome instantiateBiome(
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
        );
    }
}


