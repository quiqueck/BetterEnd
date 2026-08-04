package org.betterx.betterend.integration.byg.features;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndFeatures;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

public class BYGFeatures {
    public static final NightshadeRedwoodTreeFeature NIGHTSHADE_REDWOOD_TREE_FEATURE = EndFeatures.inlineBuild("nightshade_redwood_tree", new NightshadeRedwoodTreeFeature());
    public static final BigEtherTreeFeature BIG_ETHER_TREE_FEATURE = EndFeatures.inlineBuild("big_ether_tree", new BigEtherTreeFeature());
    public static final OldBulbisTreeFeature OLD_BULBIS_TREE_FEATURE = EndFeatures.inlineBuild("old_bulbis_tree_feature", new OldBulbisTreeFeature());

    public static final PlacedFeatureKey OLD_BULBIS_TREE = PlacedFeatureManager.createKey(BetterEnd.C.mk("old_bulbis_tree"));
    public static final PlacedFeatureKey IVIS_SPROUT = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_sprout"));
    public static final PlacedFeatureKey IVIS_VINE = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_vine"));
    public static final PlacedFeatureKey IVIS_MOSS = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_moss"));
    public static final PlacedFeatureKey IVIS_MOSS_WOOD = PlacedFeatureManager.createKey(BetterEnd.C.mk("ivis_moss_wood"));
    public static final PlacedFeatureKey NIGHTSHADE_MOSS = PlacedFeatureManager.createKey(BetterEnd.C.mk("nightshade_moss"));
    public static final PlacedFeatureKey NIGHTSHADE_MOSS_WOOD = PlacedFeatureManager.createKey(BetterEnd.C.mk("nightshade_moss_wood"));
    public static final PlacedFeatureKey NIGHTSHADE_REDWOOD_TREE = PlacedFeatureManager.createKey(BetterEnd.C.mk("nightshade_redwood_tree"));
    public static final PlacedFeatureKey BIG_ETHER_TREE = PlacedFeatureManager.createKey(BetterEnd.C.mk("big_ether_tree"));

    public static void register() {
    }
}
