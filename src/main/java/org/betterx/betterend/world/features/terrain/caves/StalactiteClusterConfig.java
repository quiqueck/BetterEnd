package org.betterx.betterend.world.features.terrain.caves;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Configuration for {@link StalactiteClusterFeature}, modelled on vanilla
 * {@code DripstoneClusterConfiguration}: a radial disc of columns with an edge falloff, each column
 * optionally producing a floor and (dripstone-style paired) ceiling sub-feature.
 * <p>
 * Sub-features are referenced as {@link ConfiguredFeature} holders via {@link ConfiguredFeature#CODEC}
 * (the inline-or-reference configured-feature codec). This keeps placement out of the cluster config
 * so the cluster itself owns the per-column placement, while the sub-features remain plain
 * single-stalactite configured features.
 */
public record StalactiteClusterConfig(
        IntProvider radius,
        FloatProvider density,
        float pairChance,
        Holder<ConfiguredFeature<?, ?>> floorFeature,
        Holder<ConfiguredFeature<?, ?>> ceilFeature
) implements FeatureConfiguration {
    public static final Codec<StalactiteClusterConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    IntProvider.codec(0, 16).fieldOf("radius").forGetter(o -> o.radius),
                    FloatProvider.codec(0.0F, 1.0F).fieldOf("density").forGetter(o -> o.density),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("pair_chance").forGetter(o -> o.pairChance),
                    ConfiguredFeature.CODEC.fieldOf("floor_feature").forGetter(o -> o.floorFeature),
                    ConfiguredFeature.CODEC.fieldOf("ceil_feature").forGetter(o -> o.ceilFeature)
            )
            .apply(instance, StalactiteClusterConfig::new));
}
