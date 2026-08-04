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
 * Noise source that returns a value in [0, 3]
 */
public class SulphuricSurfaceNoiseCondition implements NumericProvider {
    public static final SulphuricSurfaceNoiseCondition DEFAULT = new SulphuricSurfaceNoiseCondition();
    public static final MapCodec<SulphuricSurfaceNoiseCondition> CODEC = Codec.BYTE.fieldOf("sulphuric_surf")
                                                                                   .xmap((obj) -> DEFAULT, obj -> (byte) 0);

    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(5123);

    /**
     * An arbitrary but fixed base seed, so this provider does not walk the same sequence as the other End
     * surface providers over the same positions.
     */
    private static final int SEED = 0x73756C66;

    @Override
    public int getNumber(SurfaceRulesContext context) {
        final int x = context.getBlockX();
        final int z = context.getBlockZ();
        // The jitter below used to be drawn from MHelper.RANDOM_SOURCE, which is a ThreadLocalRandomSource:
        // it has no seed, cannot be given one, and hands a different stream to every thread and every JVM
        // start, so the surface it selected could never be rebuilt from the same world seed. Seeding from
        // the full block position keeps the draw per-sample exactly as it was - only its source changes.
        final RandomSource random = RandomSource.create(MathHelper.getSeed(
                SEED,
                x,
                context.getBlockY(),
                z
        ));
        final double value = NOISE.eval(x * 0.03, z * 0.03) + NOISE.eval(
                x * 0.1,
                z * 0.1
        ) * 0.3 + MHelper.randRange(
                -0.1,
                0.1,
                random
        );
        if (value < -0.6) return 0;
        if (value < -0.3) return 1;
        if (value < 0.5) return 2;
        return 3;
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

}
