package org.betterx.datagen.betterend.worldgen;

import org.betterx.betterend.registry.EndCarvers;
import org.betterx.betterend.world.carvers.EndCaveCarverConfiguration;
import org.betterx.betterend.world.carvers.EndTunnelCarverConfiguration;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

/**
 * Datapack registry provider for the two BetterEnd {@link ConfiguredWorldCarver}s
 * ({@code betterend:round_cave} and {@code betterend:tunnel_cave}).
 */
public class CarverProvider extends WoverRegistryContentProvider<ConfiguredWorldCarver<?>> {
    public CarverProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Carvers", Registries.CONFIGURED_CARVER);
    }

    @Override
    protected void bootstrap(BootstrapContext<ConfiguredWorldCarver<?>> context) {
        final HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
        final HolderSet<Block> endStones = blocks.getOrThrow(CommonBlockTags.END_STONES);

        // Round cavern: legacy RoundCaveFeature -> uniform radius 10..30, vertical squash 1.6.
        // Top tracks WoverEndConfig#DEFAULT_CAVE_BIOMES_TOP_Y (32 -> 56 -> 48): carving is hard-capped at
        // the cave-biome band ceiling anyway (see EndCaveCarver#caveBandCeiling), so a cavern-center range
        // that stopped short of the ceiling left the band's upper reaches biome territory but rarely
        // actually carved. 56 (matching the band's first raise) fixed missing floors but let caves breach
        // island surfaces with only a block or two of roof in many places, since small islands can center
        // as low as Y40; 56 pulled back to match the band's own correction to 48. Bottom kept at 8 - this
        // widens the band upward rather than shifting it, so existing low-elevation caves are unaffected.
        context.register(
                EndCarvers.ROUND_CAVE,
                EndCarvers.END_ROUND_CAVE.configured(new EndCaveCarverConfiguration(
                        0.4F,
                        UniformHeight.of(VerticalAnchor.absolute(8), VerticalAnchor.absolute(56)),
                        ConstantFloat.of(1.0F),
                        VerticalAnchor.aboveBottom(8),
                        endStones,
                        UniformInt.of(10, 30),
                        1.6F
                ))
        );

        // Continuous tunnel network: legacy TunelCaveFeature -> threshold 0.15, probability 1.0.
        // y/yScale/lava_level are codec-required but unused by the tunnel carve logic (it never calls
        // getCarveState and sets CAVE_AIR directly), so sane placeholders are used.
        context.register(
                EndCarvers.TUNNEL_CAVE,
                EndCarvers.END_TUNNEL_CAVE.configured(new EndTunnelCarverConfiguration(
                        1.0F,
                        UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.absolute(0)),
                        ConstantFloat.of(1.0F),
                        VerticalAnchor.aboveBottom(0),
                        endStones,
                        0.15F
                ))
        );
    }
}
