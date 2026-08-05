package org.betterx.datagen.betterend;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;
import de.ambertation.wover.pottable.api.datagen.WoverPottableSoilRegistryProvider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Serializes the {@link PottableSoil} entries for BetterEnd.
 * <p>
 * In addition to every BetterEnd block carrying a
 * {@link de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait} (handled by the
 * super class), this provider explicitly registers a set of VANILLA dirt-like blocks as
 * pottable soil. Those vanilla blocks have no BetterEnd trait, so they need to be added by
 * hand here.
 * <p>
 * The generated entries are keyed in the {@code betterend} namespace (e.g.
 * {@code betterend:dirt}) so all pottable-soil data ships in one place, even though the
 * referenced block lives in the {@code minecraft} namespace.
 */
public class EndPottableSoilProvider extends WoverPottableSoilRegistryProvider {
    /**
     * Vanilla dirt-like blocks that should be accepted as flower-pot soil. Keep in sync
     * with the {@code betterend:survives_on/dirt} tag populated in {@code BlockTagProvider}.
     */
    public static final List<Block> VANILLA_SOILS = List.of(
            Blocks.DIRT,
            Blocks.GRASS_BLOCK,
            Blocks.COARSE_DIRT,
            Blocks.PODZOL,
            Blocks.ROOTED_DIRT,
            Blocks.MUD,
            Blocks.MOSS_BLOCK,
            Blocks.MYCELIUM
    );

    public EndPottableSoilProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrap(BootstrapContext<PottableSoil> context) {
        // Register every BetterEnd block carrying the PottableSoilBlockTrait.
        super.bootstrap(context);

        // Register the vanilla dirt-like soils explicitly (they have no trait).
        for (Block soil : VANILLA_SOILS) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(soil);
            PottableSoilRegistry.register(
                    context,
                    PottableSoilRegistry.createKey(BetterEnd.C.id(blockId.getPath())),
                    soil
            );
        }
    }
}
