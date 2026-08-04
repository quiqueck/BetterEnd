package org.betterx.betterend.tab;



import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;

public class CreativeTabs {

    public static void register() {
        de.ambertation.wover.tabs.api.CreativeTabs
                .start(BetterEnd.C)
                .createTab("nature")
                .setPredicate(item -> PlantLikeBlockTrait.TAB_PREDICATE.contains(item)
                        || item == EndResourceItems.END_LILY_LEAF
                        || item == EndResourceItems.END_LILY_LEAF_DRIED
                )
                .setIcon(EndWoodBlocks.TENANEA_FLOWERS)
                .buildAndAdd()
                .createBlockOnlyTab(EndTerrainBlocks.END_MYCELIUM)
                .buildAndAdd()
                .createItemOnlyTab(EndResourceItems.ETERNAL_CRYSTAL)
                .buildAndAdd()
                .processRegistries()
                .registerAllTabs();
    }
}
