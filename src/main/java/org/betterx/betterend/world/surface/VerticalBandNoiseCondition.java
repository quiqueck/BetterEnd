package org.betterx.betterend.world.surface;

import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class VerticalBandNoiseCondition implements NumericProvider {
    public static final VerticalBandNoiseCondition DEFAULT = new VerticalBandNoiseCondition(
            Noises.CLAY_BANDS_OFFSET,
            1.5,  // offsetScale: keep the bands mostly horizontal (was 4.0, which shuffled them into a speckle)
            5.0,  // bandScale: chunkier ~5-block stripes (was 4.0)
            6.0,
            1.3
    );
    public static final MapCodec<VerticalBandNoiseCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(o -> o.noise),
                    Codec.DOUBLE.fieldOf("offset_scale").orElse(4.0).forGetter(o -> o.offsetScale),
                    Codec.DOUBLE.fieldOf("band_scale").orElse(4.0).forGetter(o -> o.bandScale),
                    Codec.DOUBLE.fieldOf("xz_scale").orElse(6.0).forGetter(o -> o.xzScale),
                    Codec.DOUBLE.fieldOf("y_scale").orElse(1.3).forGetter(o -> o.yScale)
            )
            .apply(instance, VerticalBandNoiseCondition::new));

    private final ResourceKey<NormalNoise.NoiseParameters> noise;
    private final double offsetScale;
    private final double bandScale;
    private final double xzScale;
    private final double yScale;

    public VerticalBandNoiseCondition(
            ResourceKey<NormalNoise.NoiseParameters> noise,
            double offsetScale,
            double bandScale,
            double xzScale,
            double yScale
    ) {
        this.noise = noise;
        this.offsetScale = offsetScale;
        this.bandScale = bandScale;
        this.xzScale = xzScale;
        this.yScale = yScale;
    }

    @Override
    public int getNumber(SurfaceRulesContext context) {
        final NormalNoise normalNoise = context.getRandomState().getOrCreateNoise(this.noise);
        double offset = normalNoise.getValue(
                (double) context.getBlockX() * xzScale,
                context.getBlockY() * yScale * 10,
                (double) context.getBlockZ() * xzScale
        ) * offsetScale;


        return (int) (context.getBlockY() / bandScale + offset);
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }
}
