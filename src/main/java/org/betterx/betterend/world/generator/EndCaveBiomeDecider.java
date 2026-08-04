package org.betterx.betterend.world.generator;

import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.world.biome.EndBiome;
import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeSource;
import de.ambertation.wover.generator.api.biomesource.end.BiomeDecider;
import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import org.jetbrains.annotations.Nullable;

/**
 * Turns BetterEnd's six cave biomes (everything tagged {@link EndTags#IS_END_CAVE}) into real, vertical
 * biomes placed directly by the {@link WoverEndBiomeSource}, replacing the now-removed legacy 2D
 * cave-biome map (the old {@code EndBiomes} {@code CAVE_BIOMES} picker / {@code caveBiomeMap}).
 *
 * <h2>The vertical band model</h2>
 * The End biome source classifies a column into a "ring" (center / highland / midland / small-island /
 * barrens) in a Y-independent way. This decider adds a horizontal band <em>below</em> a configurable top
 * height to the columns that carry a cave-bearing land biome:
 * <ul>
 *     <li>For any block whose {@code blockY <= caveBiomesTopY} (± a per-column noise jitter of
 *     {@code caveBiomesTopJitter} blocks) that sits under a highland/midland land biome whose
 *     {@link EndBiome#hasCaves()} is {@code true} (third-party, non-{@link EndBiome} land biomes are treated
 *     as cave-bearing, matching the legacy {@code biomeMissingCaves} semantics), the column's biome switches
 *     to one of the {@link EndTags#IS_END_CAVE} biomes, picked from this decider's own map.</li>
 *     <li>Center, small-island (void) and barrens columns keep their biome at every Y, so no cave biome —
 *     and therefore no carver bound to a cave biome — runs there.</li>
 * </ul>
 * The band is a genuine biome region: it is visible in F3, and where the cave top rises above an island's
 * underside it can peek out at the bottom of floating islands. This is deliberate and vanilla-like (the End
 * has no "surface" that hides such a band). The band's top, its noise jitter and the cave-biome size are all
 * per-world configurable via {@link WoverEndConfig#caveBiomesTopY}, {@link WoverEndConfig#caveBiomesTopJitter}
 * and {@link WoverEndConfig#caveBiomesSize}; setting {@code caveBiomesTopY} to {@code 0} disables cave biomes
 * entirely (this decider then returns the incoming suggestion unchanged for every column).
 *
 * <h2>Interim behavior (accepted, resolved in a later WP)</h2>
 * While this decider is active the legacy {@code RoundCave}/{@code TunelCave} carve features largely
 * self-disable: their {@code biomeMissingCaves()} probe samples the biome at low Y, now finds a cave biome
 * (whose {@link EndBiome#hasCaves()} is {@code false}) and bails out. That is expected — carving moves onto
 * the cave biomes in the next work package. The legacy {@code CAVE_BIOMES} picker / {@code caveBiomeMap}
 * that this decider replaced has since been removed.
 */
public class EndCaveBiomeDecider extends BiomeDecider {
    /**
     * Horizontal frequency of the coarse "cave region" noise. At {@code 0.004} one noise period spans
     * ~250 blocks, so a contiguous cave region (and the gap between regions) covers a small cluster of
     * islands rather than a single column: caves appear in broad patches instead of under every island.
     */
    private static final double CAVE_REGION_FREQ = 0.004;
    /**
     * Threshold the coarse region noise must exceed for a column to be eligible for cave biomes.
     * {@link OpenSimplexNoise} outputs roughly {@code [-1, 1]} (concentrated near {@code 0}); a threshold of
     * {@code 0.0} yields ~50% coverage and higher values yield progressively less. {@code 0.1} lands in the
     * intended ~40-50% band, so clearly not every cave-bearing island ends up riddled with caves. Raise this
     * to make caves rarer, lower it (toward negative values) to make them more common.
     */
    private static final double CAVE_REGION_THRESHOLD = 0.1;

    /**
     * The typed biome source this decider instance belongs to. {@code null} for the prototype instance that
     * is registered globally and only used to spawn per-source instances via {@link #createInstance}.
     */
    private final @Nullable WoverEndBiomeSource endSource;
    /**
     * Per-column noise used to jitter the top of the cave band; (re)created per world seed in
     * {@link #createMap(BiomeMapBuilderFunction, long)}.
     */
    private OpenSimplexNoise jitterNoise;
    /**
     * Low-frequency, column-coherent noise gating which regions may bear caves at all; (re)created per world
     * seed in {@link #createMap(BiomeMapBuilderFunction, long)} with a salt distinct from {@link #jitterNoise}
     * so the two fields are statistically independent.
     */
    private OpenSimplexNoise caveRegionNoise;

    /**
     * Prototype constructor used for global registration. The resulting instance carries no source, picker or
     * map; the biome source calls {@link #createInstance(WoverBiomeSource)} to build the instances it
     * actually uses.
     */
    public EndCaveBiomeDecider() {
        this(null);
    }

    private EndCaveBiomeDecider(@Nullable WoverEndBiomeSource endSource) {
        // The predicate is only used by the base addToPicker(BiomeData); our biomes are routed via
        // pickerTag()/picker() instead (see WoverEndBiomeSource.createFreshPickerMap), so it is never
        // consulted. Mirrors EndLandBiomeDecider's dummy predicate.
        super((biome) -> false);
        this.endSource = endSource;
        if (endSource != null) {
            // Same picker construction the WoverEndBiomeSource uses for its own rings: the ResourceKey
            // constructor resolves the biome registry from the current WorldState. Fallback is the always
            // present empty_end_cave biome.
            this.picker = new WoverBiomePicker(EndBiomes.EMPTY_END_CAVE.key);
        }
    }

    @Override
    public boolean canProvideFor(BiomeSource source) {
        // Deliberately NOT gated on generatorVersion (unlike EndLandBiomeDecider, which is PAULEVS-only):
        // cave biomes should exist under both the VANILLA and PAULEVS End placement algorithms.
        return source instanceof WoverEndBiomeSource;
    }

    @Override
    public BiomeDecider createInstance(WoverBiomeSource biomeSource) {
        return new EndCaveBiomeDecider((WoverEndBiomeSource) biomeSource);
    }

    @Override
    public @Nullable TagKey<Biome> pickerTag() {
        return EndTags.IS_END_CAVE;
    }

    /**
     * The biome source always invokes this seeded overload (see {@code WoverEndBiomeSource.onInitMap}); the
     * non-seeded {@link #createMap(BiomeMapBuilderFunction)} delegates here with seed {@code 0} for safety.
     */
    @Override
    public void createMap(BiomeMapBuilderFunction mapBuilder, long seed) {
        final WoverEndConfig config = endSource.getBiomeSourceConfig();
        this.map = mapBuilder.create(picker, config.caveBiomesSize);
        this.jitterNoise = new OpenSimplexNoise(seed ^ 0xCA7E15L);
        // Distinct salt from the band jitter above: seeded purely from the world seed (no shared RNG is
        // consumed), so the same world seed always reproduces the same cave regions.
        this.caveRegionNoise = new OpenSimplexNoise(seed ^ 0x0CA5E9A7EL);
    }

    @Override
    public void createMap(BiomeMapBuilderFunction mapBuilder) {
        createMap(mapBuilder, 0);
    }

    @Override
    public TagKey<Biome> suggestType(
            TagKey<Biome> originalType,
            TagKey<Biome> suggestedType,
            double density,
            int maxHeight,
            int blockX,
            int blockY,
            int blockZ,
            int quarterX,
            int quarterY,
            int quarterZ
    ) {
        final WoverEndConfig config = endSource.getBiomeSourceConfig();
        if (config.caveBiomesTopY <= 0) return suggestedType;
        if (!isLandSuggestion(suggestedType)) return suggestedType;
        if (blockY > caveTopY(blockX, blockZ)) return suggestedType;
        if (!columnHasCaves(blockX, blockZ)) return suggestedType;
        if (!inCaveRegion(blockX, blockZ)) return suggestedType;
        return EndTags.IS_END_CAVE;
    }

    /**
     * Whether the coarse cave-region noise admits caves for this column. Sampled from {@code blockX}/
     * {@code blockZ} only (never {@code blockY}), so a whole island's cave-ness is decided horizontally and
     * stays constant up the column — caves never fragment vertically. Because {@link OpenSimplexNoise} is
     * smooth, the noise crosses {@link #CAVE_REGION_THRESHOLD} gradually over hundreds of blocks, so
     * cave/non-cave borders are soft; the tunnel carver's per-corner biome lerp then still tapers tunnels to
     * nothing across a region edge rather than shearing them off at a hard wall.
     */
    private boolean inCaveRegion(int blockX, int blockZ) {
        return caveRegionNoise.eval(blockX * CAVE_REGION_FREQ, blockZ * CAVE_REGION_FREQ)
                > CAVE_REGION_THRESHOLD;
    }

    /**
     * Whether {@code suggestedType} identifies a highland/midland (cave-bearing) land column. Matches both the
     * raw tags the {@link WoverEndBiomeSource} emits for land (under the VANILLA algorithm) and the merged
     * {@link EndTags#IS_END_HIGH_OR_MIDLAND} tag that {@link EndLandBiomeDecider} substitutes upstream (under
     * the PAULEVS algorithm), plus the generic {@link BiomeTags#IS_END} fallback tag.
     */
    private static boolean isLandSuggestion(TagKey<Biome> suggestedType) {
        return suggestedType.equals(CommonBiomeTags.IS_END_HIGHLAND)
                || suggestedType.equals(CommonBiomeTags.IS_END_MIDLAND)
                || suggestedType.equals(EndTags.IS_END_HIGH_OR_MIDLAND)
                || suggestedType.equals(BiomeTags.IS_END);
    }

    private double caveTopY(int blockX, int blockZ) {
        final WoverEndConfig config = endSource.getBiomeSourceConfig();
        return config.caveBiomesTopY
                + jitterNoise.eval(blockX * 0.03, blockZ * 0.03) * config.caveBiomesTopJitter;
    }

    /**
     * Whether the land biome prospectively placed for this column bears caves. Follows the legacy
     * {@code biomeMissingCaves} rule: an {@link EndBiome} contributes caves only if
     * {@link EndBiome#hasCaves()}; any non-{@link EndBiome} (third-party) land biome is assumed to.
     */
    private boolean columnHasCaves(int blockX, int blockZ) {
        final WoverBiomePicker.PickableBiome land = endSource.landBiomeAt(blockX, blockZ);
        if (land == null) return false; // map not initialized yet
        if (land.biomeData instanceof EndBiome endBiome) return endBiome.hasCaves();
        return true;
    }

    @Override
    public boolean canProvideBiome(TagKey<Biome> suggestedType) {
        return suggestedType.equals(EndTags.IS_END_CAVE);
    }

    @Override
    public WoverBiomePicker.PickableBiome provideBiome(TagKey<Biome> suggestedType, int posX, int posY, int posZ) {
        if (map == null) return null; // pre-init guard; the source falls through to the next decider/map
        return map.getBiome(posX, posY, posZ);
    }
}
