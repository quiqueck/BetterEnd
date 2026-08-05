package org.betterx.betterend.world.features.terrain.caves;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Configuration for {@link CaveSurfaceCoatFeature}: pure geometry for the shell pass, no materials.
 * <p>
 * The feature sweeps its own chunk between {@code minY} and {@code maxY}, finds every enclosed pocket of
 * cave air in it, and paints the rock bounding that air {@code shellDepth} blocks deep.
 * <p>
 * There is no ground/vegetation {@code BlockStateProvider} here on purpose. The materials are not part of
 * the configuration at all - they come from the {@code EndCaveBiome} resolved at placement time (see
 * {@link CaveSurfaceCoatFeature}), which is the whole reason this is a BetterEnd feature and not just
 * another {@code VegetationPatchFeature} instance in datagen.
 *
 * @param replaceable blocks this feature is allowed to coat. Bare End stone only, deliberately: this used to
 *                    be the whole {@code END_STONES} tag, which also holds every surface block the land
 *                    biomes put on top of their islands (the mosses, {@code endstone_dust}, {@code sangnum},
 *                    the pallidiums, ...) plus the ores. A cave passing within {@code shellDepth} of an
 *                    island's skin then repainted that skin, which is not the coat's business. Narrowing the
 *                    set is the direct fix, and it also makes the pass idempotent: jadestone is not End
 *                    stone, so a second run over the same chunk finds nothing left to do.
 * @param protect     blocks the coat must leave alone even though {@code replaceable} matches them, and
 *                    which the shell wraps around rather than growing through. Redundant while
 *                    {@code replaceable} is bare End stone - it is kept because it encodes an invariant a
 *                    datapack could otherwise break by widening {@code replaceable} again: the coat runs in
 *                    {@code UNDERGROUND_DECORATION}, i.e. <em>after</em> {@code UNDERGROUND_ORES}, while the
 *                    carve-time coater it replaces ran before any feature at all, so a wide shell would
 *                    quietly eat every ore vein that generated near a cave face.
 * @param minY        bottom of the swept band, inclusive.
 * @param maxY        top of the swept band, inclusive. The band has to be <em>wider</em> than the carved
 *                    cave band, because reaching an end of it is exactly how the sweep recognises air that
 *                    is not a cave - see {@link CaveSurfaceCoatFeature}.
 * @param shellDepth  how many blocks deep the wall material is painted into the rock; {@code 0} paints no
 *                    shell at all.
 */
public record CaveSurfaceCoatConfig(
        HolderSet<Block> replaceable,
        HolderSet<Block> protect,
        int minY,
        int maxY,
        int shellDepth
) implements FeatureConfiguration {
    public static final Codec<CaveSurfaceCoatConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK)
                                  .fieldOf("replaceable")
                                  .forGetter(o -> o.replaceable),
                    RegistryCodecs.homogeneousList(Registries.BLOCK)
                                  .fieldOf("protected")
                                  .forGetter(o -> o.protect),
                    Codec.intRange(-64, 320).fieldOf("min_y").forGetter(o -> o.minY),
                    Codec.intRange(-64, 320).fieldOf("max_y").forGetter(o -> o.maxY),
                    Codec.intRange(0, 16).fieldOf("shell_depth").forGetter(o -> o.shellDepth)
            )
            .apply(instance, CaveSurfaceCoatConfig::new));
}
