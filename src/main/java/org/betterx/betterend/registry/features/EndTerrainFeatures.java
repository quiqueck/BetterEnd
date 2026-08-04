package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

public class EndTerrainFeatures {
    public static final PlacedFeatureKey SURFACE_VENT = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("surface_vent"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey SULPHUR_HILL = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("sulphur_hill"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey OBSIDIAN_PILLAR_BASEMENT = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("obsidian_pillar_basement"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey OBSIDIAN_BOULDER = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("obsidian_boulder"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey FALLEN_PILLAR = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("fallen_pillar"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey UMBRALITH_ARCH = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("umbralith_arch"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey THIN_UMBRALITH_ARCH = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("thin_umbralith_arch"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey CRASHED_SHIP = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("crashed_ship"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final PlacedFeatureKey SILK_MOTH_NEST = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("silk_moth_nest"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);


    public static final PlacedFeatureKey SPIRE = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("spire"))
            .setDecoration(GenerationStep.Decoration.RAW_GENERATION);

    public static final PlacedFeatureKey FLOATING_SPIRE = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("floating_spire"))
            .setDecoration(GenerationStep.Decoration.RAW_GENERATION);

    // Moved from RAW_GENERATION to LAKES so it's guaranteed to run after EndStructures.SULPHURIC_CAVE
    // (also RAW_GENERATION, in the same biome) rather than depend on same-step registration order.
    public static final PlacedFeatureKey GEYSER = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("geyser"))
            .setDecoration(GenerationStep.Decoration.LAKES);

    public static final PlacedFeatureKey ICE_STAR = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("ice_star"))
            .setDecoration(GenerationStep.Decoration.RAW_GENERATION);

    public static final PlacedFeatureKey ICE_STAR_SMALL = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("ice_star_small"))
            .setDecoration(GenerationStep.Decoration.RAW_GENERATION);

    public static final PlacedFeatureKey POND_WITH_WATERFALL = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("pond_with_waterfall"))
            .setDecoration(GenerationStep.Decoration.LAKES);

    public static final PlacedFeatureKey BIOME_ISLAND = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("overworld_island"))
            .setDecoration(GenerationStep.Decoration.RAW_GENERATION);

}
