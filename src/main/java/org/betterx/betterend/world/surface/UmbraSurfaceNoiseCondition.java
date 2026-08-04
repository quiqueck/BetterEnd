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
 * Noise source that returns a value in [0, 4]
 */
public class UmbraSurfaceNoiseCondition implements NumericProvider {
    public static final UmbraSurfaceNoiseCondition DEFAULT = new UmbraSurfaceNoiseCondition();
    public static final MapCodec<UmbraSurfaceNoiseCondition> CODEC = Codec.BYTE.fieldOf("umbra_srf")
                                                                               .xmap((obj) -> DEFAULT, obj -> (byte) 0);

    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(1512);

    /**
     * An arbitrary but fixed base seed, so this provider does not walk the same sequence as the other End
     * surface providers over the same positions.
     */
    private static final int SEED = 0x756D6272;

    @Override
    public int getNumber(SurfaceRulesContext context) {
        return getDepth(context.getBlockX(), context.getBlockY(), context.getBlockZ());
    }

    /**
     * @deprecated a column carries no Y, so this has to jitter per column where the surface rule jitters
     * per sample. Prefer {@link #getDepth(int, int, int)} wherever a Y is available; this overload is kept
     * for callers that genuinely only know a column.
     */
    @Deprecated
    public static int getDepth(int x, int z) {
        return getDepth(x, 0, z);
    }

    public static int getDepth(int x, int y, int z) {
        // The jitter below used to be drawn from MHelper.RANDOM_SOURCE, which is a ThreadLocalRandomSource:
        // it has no seed, cannot be given one, and hands a different stream to every thread and every JVM
        // start, so the surface it selected could never be rebuilt from the same world seed. Seeding from
        // the block position keeps the draw per-sample exactly as it was - only its source changes.
        final RandomSource random = RandomSource.create(MathHelper.getSeed(SEED, x, y, z));
        final double value = NOISE.eval(x * 0.03, z * 0.03) + NOISE.eval(
                x * 0.1,
                z * 0.1
        ) * 0.3 + MHelper.randRange(
                -0.1,
                0.1,
                random
        );
        if (value > 0.4) return 0;
        if (value > 0.15) return 1;
        if (value > -0.15) return 2;
        if (value > -0.4) return 3;
        return 4;
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }
}
