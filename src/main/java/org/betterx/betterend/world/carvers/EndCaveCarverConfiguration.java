package org.betterx.betterend.world.carvers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

/**
 * Configuration for {@link EndCaveCarver}. Extends the vanilla {@link CarverConfiguration}
 * (which already provides {@code probability}, {@code y}, {@code yScale}, {@code lava_level},
 * {@code debug_settings} and {@code replaceable}) with the two fields the legacy
 * {@code RoundCaveFeature} needed:
 * <ul>
 *     <li>{@code radius} &ndash; the cavern radius (legacy used a uniform 10..30 roll).</li>
 *     <li>{@code vertical_squash} &ndash; the vertical distance multiplier (legacy hard-coded 1.6).</li>
 * </ul>
 * The codec composition mirrors {@code CaveCarverConfiguration}: it embeds the base
 * {@link CarverConfiguration#CODEC} via {@code forGetter(cfg -> cfg)} and appends the extra fields.
 */
public class EndCaveCarverConfiguration extends CarverConfiguration {
    public static final Codec<EndCaveCarverConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    CarverConfiguration.CODEC.forGetter(cfg -> cfg),
                    IntProviders.CODEC.fieldOf("radius").forGetter(cfg -> cfg.radius),
                    Codec.FLOAT.fieldOf("vertical_squash").forGetter(cfg -> cfg.verticalSquash)
            ).apply(instance, EndCaveCarverConfiguration::new)
    );

    public final IntProvider radius;
    public final float verticalSquash;

    public EndCaveCarverConfiguration(
            CarverConfiguration base,
            IntProvider radius,
            float verticalSquash
    ) {
        super(base.probability, base.y, base.yScale, base.lavaLevel, base.debugSettings, base.replaceable);
        this.radius = radius;
        this.verticalSquash = verticalSquash;
    }

    public EndCaveCarverConfiguration(
            float probability,
            HeightProvider y,
            FloatProvider yScale,
            VerticalAnchor lavaLevel,
            HolderSet<Block> replaceable,
            IntProvider radius,
            float verticalSquash
    ) {
        super(probability, y, yScale, lavaLevel, CarverDebugSettings.DEFAULT, replaceable);
        this.radius = radius;
        this.verticalSquash = verticalSquash;
    }
}
