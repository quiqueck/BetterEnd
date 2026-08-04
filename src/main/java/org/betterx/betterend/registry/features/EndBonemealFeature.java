package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

public class EndBonemealFeature {
    public static final PlacedFeatureKey BONEMEAL_END_MOSS_NOT_GLOWING_GRASSLANDS = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("bonemeal_end_moss_not_glowing_grasslands"));

    public static final PlacedFeatureKey BONEMEAL_END_MOSS_GLOWING_GRASSLANDS = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("bonemeal_end_moss_glowing_grasslands"));

    public static final PlacedFeatureKey BONEMEAL_RUTISCUS_NOT_LANTERN_WOODS = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("bonemeal_rutiscus_not_lantern_woods"));

    public static final PlacedFeatureKey BONEMEAL_RUTISCUS_LANTERN_WOODS = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("bonemeal_rutiscus_lantern_woods"));
}
