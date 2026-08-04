package org.betterx.betterend.world.structures.village;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;
import de.ambertation.wover.structure.api.StructureKeys;
import de.ambertation.wover.structure.api.pools.StructurePoolKey;

public class VillagePools {
    public static final PlacedFeatureKey CHORUS_VILLAGE = PlacedFeatureManager.createKey(BetterEnd.C.mk("village_chorus"));

    public static StructurePoolKey TERMINATORS_KEY = StructureKeys.pool(BetterEnd.C.mk("village/terminators"));
    public static StructurePoolKey START = StructureKeys.pool(BetterEnd.C.mk("village/center_piece"));
    public static StructurePoolKey HOUSES_KEY = StructureKeys.pool(BetterEnd.C.mk("village/houses"));
    public static StructurePoolKey STREET_KEY = StructureKeys.pool(BetterEnd.C.mk("village/streets"));
    public static StructurePoolKey STREET_DECO_KEY = StructureKeys.pool(BetterEnd.C.mk("village/street_decorations"));
    public static StructurePoolKey DECORATIONS_KEY = StructureKeys.pool(BetterEnd.C.mk("village/decorations"));
}
