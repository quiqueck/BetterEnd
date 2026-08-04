package org.betterx.betterend.world.biome;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeData;
import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndBiome extends WoverBiomeData implements SurfaceMaterialProvider {
    public static final MapCodec<EndBiome> CODEC = codec(
            Codec.BOOL.fieldOf("has_caves").orElse(true).forGetter(o -> o.hasCaves),
            SurfaceMaterialProvider.CODEC.fieldOf("surface")
                                         .orElse(Config.DEFAULT_MATERIAL)
                                         .forGetter(o -> o.surfMatProv),
            EndBiome::new
    );
    public static final KeyDispatchDataCodec<EndBiome> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    public EndBiome(
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
                genChance, edgeSize, vertical, edge, parent
        );
        this.hasCaves = hasCaves;
        this.surfMatProv = surface;
    }

    public void datagenSetup(BootstrapContext<BiomeData> dataContext) {

    }

    @Override
    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
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

    public abstract static class Config implements EndBiomeBuilder.BiomeFactory {
        public static final SurfaceMaterialProvider DEFAULT_MATERIAL = new DefaultSurfaceMaterialProvider();

        protected static final SurfaceRules.RuleSource END_STONE = SurfaceRules.state(DefaultSurfaceMaterialProvider.END_STONE);
        protected static final SurfaceRules.RuleSource END_MOSS = SurfaceRules.state(EndTerrainBlocks.END_MOSS.defaultBlockState());
        protected static final SurfaceRules.RuleSource ENDSTONE_DUST = SurfaceRules.state(EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState());
        protected static final SurfaceRules.RuleSource END_MYCELIUM = SurfaceRules.state(EndTerrainBlocks.END_MYCELIUM.defaultBlockState());
        protected static final SurfaceRules.RuleSource FLAVOLITE = SurfaceRules.state(EndStoneBlocks.FLAVOLITE.getBlock(SlotType.SOURCE).defaultBlockState());
        protected static final SurfaceRules.RuleSource SULPHURIC_ROCK = SurfaceRules.state(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE).defaultBlockState());
        protected static final SurfaceRules.RuleSource BRIMSTONE = SurfaceRules.state(EndStoneBlocks.BRIMSTONE.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_FULL = SurfaceRules.state(EndTerrainBlocks.PALLIDIUM_FULL.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_HEAVY = SurfaceRules.state(EndTerrainBlocks.PALLIDIUM_HEAVY.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_THIN = SurfaceRules.state(EndTerrainBlocks.PALLIDIUM_THIN.defaultBlockState());
        protected static final SurfaceRules.RuleSource PALLIDIUM_TINY = SurfaceRules.state(EndTerrainBlocks.PALLIDIUM_TINY.defaultBlockState());
        protected static final SurfaceRules.RuleSource UMBRALITH = SurfaceRules.state(EndStoneBlocks.UMBRALITH.getBlock(SlotType.SOURCE).defaultBlockState());

        protected Config() {
        }

        public abstract void addCustomBuildData(EndBiomeBuilder builder);

        public boolean hasCaves() {
            return true;
        }

        public boolean hasReturnGateway() {
            return true;
        }

        public SurfaceMaterialProvider surfaceMaterial() {
            return DEFAULT_MATERIAL;
        }

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
            return new EndBiome(
                    fogDensity, key.key, generatorData,
                    terrainHeight, genChance, edgeSize, vertical, edge, parent,
                    hasCave, surface
            );
        }
    }

    protected SurfaceMaterialProvider surfMatProv = Config.DEFAULT_MATERIAL;

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

    public static BlockState findTopMaterial(Holder<Biome> biome) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(biome)
                .map(SurfaceMaterialProvider::getTopMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getTopMaterial());
    }

    /**
     * WARNING: this position-based lookup is unreliable during world generation and will often fall
     * back to {@link EndBiome.Config#DEFAULT_MATERIAL} (plain end stone) instead of the biome's real
     * surface block. It uses {@link WoverBiomePicker#getBiomeAt(WorldGenLevel, BlockPos)}, which only
     * reads already-baked chunk biome data and returns {@code null} whenever the containing chunk
     * hasn't reached {@link net.minecraft.world.level.chunk.status.ChunkStatus#BIOMES} yet or lies
     * outside the region currently being generated (e.g. the far edge of a large lake) — in which
     * case the {@code DEFAULT_MATERIAL} fallback silently kicks in.
     * <p>
     * Do NOT "fix" this by routing through {@link SurfaceMaterialProvider#findSurfaceMaterialProvider(WorldGenLevel, BlockPos)}
     * / {@link WorldGenLevel#getBiome(BlockPos)}: that path runs a Voronoi sample across neighbouring
     * columns and calls {@code getChunk} on chunks that may be unavailable, throwing
     * {@code IllegalStateException: Requested chunk unavailable during world generation} and crashing
     * chunk gen (observed via {@code EndLakeFeature}).
     * <p>
     * When you already hold a {@link Holder}{@code <}{@link Biome}{@code >} (e.g. a structure piece
     * that knows its biome, or a feature that picked one), prefer {@link #findTopMaterial(Holder)}.
     * When you need the true surface block at a position, sample the block already placed there
     * (tagged {@link de.ambertation.wover.tag.api.predefined.CommonBlockTags#TERRAIN}) — see
     * {@code LakePiece} for the pattern.
     */
    public static BlockState findTopMaterial(WorldGenLevel world, BlockPos pos) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(WoverBiomePicker.getBiomeAt(world, pos))
                .map(SurfaceMaterialProvider::getTopMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getTopMaterial());
    }

    /**
     * Preferred over {@link #findTopMaterial(WorldGenLevel, BlockPos)} for features that decorate a
     * lake shore or similar spot: it samples the real surface block the surface rules already placed
     * at {@code topSolidPos}, so noise-driven surfaces (e.g. the Sulphur Springs' sulphuric rock)
     * are reflected instead of only a biome's single {@code getTopMaterial()}. Reading an
     * already-placed block is always crash-safe — no biome/chunk-status dependency. Only falls back
     * to the (often {@code DEFAULT_MATERIAL}) biome lookup when the sampled block is not a genuine
     * surface material, i.e. plain {@link Blocks#END_STONE} or a block not tagged
     * {@link CommonBlockTags#TERRAIN}.
     *
     * @param topSolidPos the top <em>solid</em> block of a column (the biome surface block)
     */
    public static BlockState sampleTopMaterial(WorldGenLevel world, BlockPos topSolidPos) {
        final BlockState top = world.getBlockState(topSolidPos);
        if (top.is(CommonBlockTags.TERRAIN) && !top.is(Blocks.END_STONE)) {
            return top;
        }
        return findTopMaterial(world, topSolidPos);
    }

    public static BlockState findUnderMaterial(Holder<Biome> biome) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(biome)
                .map(SurfaceMaterialProvider::getUnderMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getUnderMaterial());
    }

    /**
     * WARNING: same caveats as {@link #findTopMaterial(WorldGenLevel, BlockPos)} — unreliable during
     * world generation (frequently falls back to {@link EndBiome.Config#DEFAULT_MATERIAL}), and it
     * must use the null-safe {@link WoverBiomePicker#getBiomeAt(WorldGenLevel, BlockPos)} rather than
     * {@link WorldGenLevel#getBiome(BlockPos)}, which throws
     * {@code IllegalStateException: Requested chunk unavailable during world generation}. Prefer
     * {@link #findUnderMaterial(Holder)} when a biome is already known.
     */
    public static BlockState findUnderMaterial(WorldGenLevel world, BlockPos pos) {
        return SurfaceMaterialProvider
                .findSurfaceMaterialProvider(WoverBiomePicker.getBiomeAt(world, pos))
                .map(SurfaceMaterialProvider::getUnderMaterial)
                .orElse(EndBiome.Config.DEFAULT_MATERIAL.getUnderMaterial());
    }
}
