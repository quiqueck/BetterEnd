package org.betterx.datagen.betterend.recipes;

import org.betterx.bclib.api.v3.datagen.BlockLootTableProvider;
import org.betterx.betterend.BetterEnd;

import net.minecraft.core.HolderLookup;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EndBlockLootTableProvider extends BlockLootTableProvider {

    public EndBlockLootTableProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup, List.of(BetterEnd.MOD_ID));
    }
}
