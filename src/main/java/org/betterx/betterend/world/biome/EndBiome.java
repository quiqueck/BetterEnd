package org.betterx.betterend.world.biome;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeBuilder;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeBuilder.BiomeSupplier;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeSettings;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.bclib.api.v2.levelgen.surface.SurfaceRuleBuilder;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.bclib.util.GenerationArea;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndBiomeBuildings;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndTags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.placement.EndPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class EndBiome extends BCLBiome implements SurfaceMaterialProvider {
    public static final Codec<EndBiome> CODEC = RecordCodecBuilder.create(instance ->
            codecWithSettings(
                    instance,
                    Codec.BOOL.fieldOf("has_caves").orElse(true).forGetter(o -> o.hasCaves),
                    SurfaceMaterialProvider.CODEC.fieldOf("surface")
                                                 .orElse(Config.DEFAULT_MATERIAL)
                                                 .forGetter(o -> o.surfMatProv)
            ).apply(instance, EndBiome::new)
    );
    public static final KeyDispatchDataCodec<EndBiome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    @Override
    public KeyDispatchDataCodec<? extends BCLBiome> codec() {
        return KEY_CODEC;
    }

    protected EndBiome(
            float terrainHeight,
            float fogDensity,
            float genChance,
            int edgeSize,
            boolean vertical,
            Optional<ResourceLocation> edge,
            ResourceLocation biomeID,
            Optional<List<Climate.ParameterPoint>> parameterPoints,
            Optional<ResourceLocation> biomeParent,
            Optional<String> intendedType,
            boolean hasCaves,
            SurfaceMaterialProvider surface
    ) {
        super(
                terrainHeight,
                fogDensity,
                genChance,
                edgeSize,
                vertical,
                edge,
                biomeID,
                parameterPoints,
                biomeParent,
                intendedType
        );
        this.hasCaves = hasCaves;
        this.surfMatProv = surface;
    }

    private boolean hasCaves = true;

    void setHasCaves(boolean v) {
        this.hasCaves = v;
    }

    public boolean hasCaves() {
        return hasCaves;
    }

    public static class DefaultSurfaceMaterialProvider implements SurfaceMaterialProvider {
        public static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();

        @Override
        public BlockState getTopMaterial() {
            return getUnderMaterial();
        }

        @Override
        public BlockState getAltTopMaterial() {
            return getTopMaterial();
        }

        @Override
        public BlockState getUnderMaterial() {
            return END_STONE;
        }

        @Override
        public boolean generateFloorRule() {
            return true;
        }

        @Override
        public SurfaceRuleBuilder surface() {
            SurfaceRuleBuilder builder = SurfaceRuleBuilder.start();

            if (generateFloorRule() && getTopMaterial() != getUnderMaterial()) {
                if (getTopMaterial() != getAltTopMaterial()) {
                    builder.floor(getTopMaterial());
                } else {
                    builder.chancedFloor(getTopMaterial(), getAltTopMaterial());
                }
            }
            return builder.filler(getUnderMaterial());
        }
    }

    public abstract static class Config {
        public static final SurfaceMaterialProvider DEFAULT_MATERIAL = new DefaultSurfaceMaterialProvider();

        protected static final SurfaceRules.RuleSource END_STONE = SurfaceRules.state(DefaultSurfaceMaterialProvider.END_STONE);
        protected static final SurfaceRules.RuleSource END_MOSS = SurfaceRules.state(EndBlocks.END_MOSS.defaultBlockState());
        protected static final SurfaceRules.RuleSource ENDSTONE_DUST = SurfaceRules.state(EndBlocks.ENDSTONE_DUST.defaultBlockState());
        protected static final SurfaceRules.RuleSource END_MYCELIUM = SurfaceRules.state(EndBlocks.END_MYCELIUM.defaultBlockState());
        protected static final SurfaceRules.RuleSource FLAVOLITE = SurfaceRules.state(EndBlocks.FLAVOLITE.stone.defaultBlockState());
        protected static final SurfaceRules.RuleSource SULPHURIC_ROCK = SurfaceRules.state(EndBlocks.SULPHURIC_ROCK.stone.defaultBlockState());
        protected static final SurfaceRules.RuleSource BRIMSTONE = SurfaceRules.state(EndBlocks.BRIMSTONE.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_FULL = SurfaceRules.state(EndBlocks.PALLIDIUM_FULL.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_HEAVY = SurfaceRules.state(EndBlocks.PALLIDIUM_HEAVY.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_THIN = SurfaceRules.state(EndBlocks.PALLIDIUM_THIN.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_TINY = SurfaceRules.state(EndBlocks.PALLIDIUM_TINY.defaultBlockState());
        protected static final SurfaceRules.RuleSource UMBRALITH = SurfaceRules.state(EndBlocks.UMBRALITH.stone.defaultBlockState());

        public final ResourceLocation ID;

        protected Config(String name) {
            this.ID = BetterEnd.makeID(name);
        }

        protected Config(ResourceLocation ID) {
            this.ID = ID;
        }

        protected abstract void addCustomBuildData(BCLBiomeBuilder builder);

        public BiomeSupplier<EndBiome> getSupplier() {
            return EndBiome::new;
        }

        protected boolean hasCaves() {
            return true;
        }

        protected boolean hasReturnGateway() {
            return true;
        }

        protected SurfaceMaterialProvider surfaceMaterial() {
            return DEFAULT_MATERIAL;
        }
    }


    public EndBiome(ResourceKey<Biome> biomeID, BCLBiomeSettings settings) {
        super(biomeID, settings);
    }

    public static EndBiome create(Config biomeConfig, BiomeAPI.BiomeType type) {
        return create(biomeConfig, type, null);
    }

    public static EndBiome createSubBiome(Config data, @NotNull BCLBiome parentBiome) {
        return create(data, parentBiome.getIntendedType(), parentBiome);
    }

    private static EndBiome create(Config biomeConfig, BiomeAPI.BiomeType type, BCLBiome parentBiome) {
        BCLBiomeBuilder builder = BCLBiomeBuilder
                .start(biomeConfig.ID)
                .music(SoundEvents.MUSIC_END)
                .waterColor(BCLBiomeBuilder.DEFAULT_END_WATER_COLOR)
                .waterFogColor(BCLBiomeBuilder.DEFAULT_END_WATER_FOG_COLOR)
                .fogColor(BCLBiomeBuilder.DEFAULT_END_FOG_COLOR)
                .skyColor(BCLBiomeBuilder.DEFAULT_END_SKY_COLOR)
                .mood(EndSounds.AMBIENT_DUST_WASTELANDS)
                .temperature(BCLBiomeBuilder.DEFAULT_END_TEMPERATURE)
                .wetness(BCLBiomeBuilder.DEFAULT_END_WETNESS)
                .parentBiome(parentBiome)
                .precipitation(Biome.Precipitation.NONE)
                .surface(biomeConfig.surfaceMaterial().surface().build())
                .type(type);

        biomeConfig.addCustomBuildData(builder);
        // the biome's buildings and ruins, each a structure of its own (see EndBiomeBuildings)
        for (EndBiomeBuildings.Building building : EndBiomeBuildings.of(biomeConfig.ID)) {
            builder.structure(building.structure());
        }
        EndFeatures.addDefaultFeatures(biomeConfig.ID, builder, biomeConfig.hasCaves());

        if (biomeConfig.hasReturnGateway()) {
            builder.feature(GenerationStep.Decoration.SURFACE_STRUCTURES, EndPlacements.END_GATEWAY_RETURN);
        }

        EndBiome biome = builder.build(biomeConfig.getSupplier()).biome();
        biome.setHasCaves(biomeConfig.hasCaves());
        biome.setSurfaceMaterial(biomeConfig.surfaceMaterial());

        EndTags.addBiomeSurfaceToEndGroup(biome);
        return biome;
    }


    protected SurfaceMaterialProvider surfMatProv = Config.DEFAULT_MATERIAL;

    protected void setSurfaceMaterial(SurfaceMaterialProvider prov) {
        surfMatProv = prov;
    }

    @Override
    public BlockState getTopMaterial() {
        return surfMatProv.getTopMaterial();
    }

    @Override
    public BlockState getUnderMaterial() {
        return surfMatProv.getUnderMaterial();
    }

    @Override
    public BlockState getAltTopMaterial() {
        return surfMatProv.getAltTopMaterial();
    }

    @Override
    public boolean generateFloorRule() {
        return surfMatProv.generateFloorRule();
    }

    @Override
    public SurfaceRuleBuilder surface() {
        return surfMatProv.surface();
    }

    public static BlockState findTopMaterial(BCLBiome biome) {
        return BiomeAPI.findTopMaterial(biome).orElse(EndBiome.Config.DEFAULT_MATERIAL.getTopMaterial());
    }

    public static BlockState findTopMaterial(Biome biome) {
        return findTopMaterial(BiomeAPI.getBiome(biome));
    }

    public static BlockState findTopMaterial(WorldGenLevel world, BlockPos pos) {
        return findTopMaterial(BiomeAPI.getBiome(GenerationArea.getBiome(world, pos)));
    }

    public static BlockState findUnderMaterial(BCLBiome biome) {
        return BiomeAPI.findUnderMaterial(biome).orElse(EndBiome.Config.DEFAULT_MATERIAL.getUnderMaterial());
    }

    public static BlockState findUnderMaterial(WorldGenLevel world, BlockPos pos) {
        return findUnderMaterial(BiomeAPI.getBiome(GenerationArea.getBiome(world, pos)));
    }

    public static List<BCLBiome> getAllBeBiomes() {
        return BiomeAPI.getAllBiomes(BiomeAPI.BiomeType.END);
    }
}
