package org.betterx.betterend.world.carvers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

/**
 * Configuration for {@link EndTunnelCarver}. Extends the vanilla {@link CarverConfiguration}
 * with the single tuning value the legacy {@code TunelCaveFeature} used: {@code threshold}
 * (the combined-noise cut-off below which a block is carved; legacy hard-coded {@code 0.15}).
 */
public class EndTunnelCarverConfiguration extends CarverConfiguration {
    public static final Codec<EndTunnelCarverConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    CarverConfiguration.CODEC.forGetter(cfg -> cfg),
                    Codec.FLOAT.fieldOf("threshold").forGetter(cfg -> cfg.threshold)
            ).apply(instance, EndTunnelCarverConfiguration::new)
    );

    public final float threshold;

    public EndTunnelCarverConfiguration(CarverConfiguration base, float threshold) {
        super(base.probability, base.y, base.yScale, base.lavaLevel, base.debugSettings, base.replaceable);
        this.threshold = threshold;
    }

    public EndTunnelCarverConfiguration(
            float probability,
            HeightProvider y,
            FloatProvider yScale,
            VerticalAnchor lavaLevel,
            HolderSet<Block> replaceable,
            float threshold
    ) {
        super(probability, y, yScale, lavaLevel, CarverDebugSettings.DEFAULT, replaceable);
        this.threshold = threshold;
    }
}
