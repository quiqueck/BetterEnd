package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class EndLakeFeatures {
    // END_LAKE, END_LAKE_NORMAL and END_LAKE_RARE are no longer features: they are now per-chunk
    // structures (see EndStructures.END_LAKE*). A decoration feature carves neighbouring chunks and
    // could flood the ground under trees a neighbour had already grown, stranding them over the water;
    // a structure carves during the chunk's own LAKES step, before that chunk grows its trees.
    public static final PlacedFeatureKey DESERT_LAKE = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("desert_lake"))
            .setDecoration(Decoration.LAKES);

    public static final PlacedConfiguredFeatureKey SULPHURIC_LAKE = PlacedFeatureManager
            .createKey(EndConfiguredLakeFeature.SULPHURIC_LAKE)
            .setDecoration(Decoration.LAKES);
}
