package org.betterx.betterend.tab;

import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;

public class CreativeTabs {

    public static void register() {
        org.betterx.wover.tabs.api.CreativeTabs
                .start(BetterEnd.C)
                .createTab("nature")
                .setPredicate(item -> PlantLikeBlockTrait.TAB_PREDICATE.contains(item)
                        || item == EndItems.END_LILY_LEAF
                        || item == EndItems.END_LILY_LEAF_DRIED
                )
                .setIcon(EndBlocks.TENANEA_FLOWERS)
                .buildAndAdd()
                .createBlockOnlyTab(EndBlocks.END_MYCELIUM)
                .buildAndAdd()
                .createItemOnlyTab(EndItems.ETERNAL_CRYSTAL)
                .buildAndAdd()
                .processRegistries()
                .registerAllTabs();
    }
}
