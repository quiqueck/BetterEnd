package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * Global {@link PlacedFeatureKey}s for the modernized End cave decoration (WP4.3-4.5). Each key is a
 * single, biome-independent placed feature added at {@link GenerationStep.Decoration#UNDERGROUND_DECORATION}.
 * Cave biomes reference a subset of these keys (in a fixed canonical order) instead of the removed
 * per-biome {@code *_cave_populator} feature.
 */
public class EndPlacedCaveFeatures {
    public static final PlacedFeatureKey STALACTITE_CLUSTER_PLAIN = key("stalactite_cluster_plain");
    public static final PlacedFeatureKey STALACTITE_CLUSTER_CAVEMOSS = key("stalactite_cluster_cavemoss");
    public static final PlacedFeatureKey STALAGMITE_SCATTER = key("stalagmite_scatter");
    public static final PlacedFeatureKey STALACTITE_SCATTER = key("stalactite_scatter");
    public static final PlacedFeatureKey BIG_AURORA_CRYSTAL = key("big_aurora_crystal_placed");
    public static final PlacedFeatureKey SMARAGDANT_CRYSTAL = key("smaragdant_crystal_placed");
    public static final PlacedFeatureKey SMARAGDANT_SHARD_SCATTER = key("smaragdant_shard_scatter");
    public static final PlacedFeatureKey CAVE_LUSH_FLOOR_PATCH = key("cave_lush_floor_patch");
    public static final PlacedFeatureKey CAVE_LUSH_CEILING_PATCH = key("cave_lush_ceiling_patch");
    public static final PlacedFeatureKey CAVE_GRASS_SCATTER = key("cave_grass_scatter");
    public static final PlacedFeatureKey CAVE_BUSH_SCATTER = key("cave_bush_scatter");
    public static final PlacedFeatureKey CAVE_PUMPKIN_PLACED = key("cave_pumpkin_placed");

    private static PlacedFeatureKey key(String name) {
        return PlacedFeatureManager
                .createKey(BetterEnd.C.mk(name))
                .setDecoration(GenerationStep.Decoration.UNDERGROUND_DECORATION);
    }
}
