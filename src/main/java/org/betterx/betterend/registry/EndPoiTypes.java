package org.betterx.betterend.registry;


import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.RunedFlavolite;
import de.ambertation.wover.poi.api.PoiManager;
import de.ambertation.wover.poi.api.WoverPoiType;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class EndPoiTypes {
    public static final WoverPoiType ETERNAL_PORTAL = PoiManager.register(
            BetterEnd.C.mk("eternal_portal"),
            ImmutableSet.copyOf(EndFunctionalBlocks.END_PORTAL_BLOCK.getStateDefinition().getPossibleStates()),
            0, 1
    );

    public static final WoverPoiType ETERNAL_PORTAL_FRAME = PoiManager.register(
            BetterEnd.C.mk("eternal_portal_frame"),
            Set.of(
                    EndStoneBlocks.FLAVOLITE_RUNED_ETERNAL.defaultBlockState().setValue(RunedFlavolite.ACTIVATED, false)
            ),
            0, 1
    );

    public static void register() {

    }
}
