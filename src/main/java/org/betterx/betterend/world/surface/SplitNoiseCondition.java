package org.betterx.betterend.world.surface;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import de.ambertation.wover.math.api.MathHelper;
import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.api.noise.NumericProvider;

import net.minecraft.util.RandomSource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

/**
 * Noise source that returns a value in [0, 1]
 */
public class SplitNoiseCondition implements NumericProvider {
    public static final SplitNoiseCondition DEFAULT = new SplitNoiseCondition();
    public static final MapCodec<SplitNoiseCondition> CODEC = Codec.BYTE.fieldOf("split_noise")
                                                                        .xmap((obj) -> DEFAULT, obj -> (byte) 0);

    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(4141);

    /**
     * An arbitrary but fixed base seed, so this provider does not walk the same sequence as the other End
     * surface providers over the same positions.
     */
    private static final int SEED = 0x73706C74;

    @Override
    public int getNumber(SurfaceRulesContext context) {
        final int x = context.getBlockX();
        final int z = context.getBlockZ();
        // The jitter below used to be drawn from MHelper.RANDOM_SOURCE, which is a ThreadLocalRandomSource:
        // it has no seed, cannot be given one, and hands a different stream to every thread and every JVM
        // start, so the split it selected could never be rebuilt from the same world seed. Seeding from the
        // full block position keeps the draw per-sample exactly as it was - only its source changes.
        final RandomSource random = RandomSource.create(MathHelper.getSeed(
                SEED,
                x,
                context.getBlockY(),
                z
        ));
        float noise = (float) NOISE.eval(x * 0.1, z * 0.1) + MHelper.randRange(-0.4F, 0.4F, random);
        return noise > 0 ? 1 : 0;
    }

    /**
     * @deprecated a column carries no Y, so this has to jitter per column where {@link #getNumber} jitters
     * per sample. Prefer {@link #getNoise(int, int, int)} wherever a Y is available; this overload is kept
     * for callers that genuinely only know a column.
     */
    @Deprecated
    public double getNoise(int x, int z) {
        return getNoise(x, 0, z);
    }

    public double getNoise(int x, int y, int z) {
        final RandomSource random = RandomSource.create(MathHelper.getSeed(SEED, x, y, z));
        float noise = (float) NOISE.eval(x * 0.1, z * 0.1) + MHelper.randRange(
                -0.2F,
                0.2F,
                random
        );
        return noise;
    }


    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }


}
