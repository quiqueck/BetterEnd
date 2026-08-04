package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.surface.SplitNoiseCondition;
import org.betterx.betterend.world.surface.SulphuricSurfaceNoiseCondition;
import org.betterx.betterend.world.surface.UmbraSurfaceNoiseCondition;
import org.betterx.betterend.world.surface.VerticalBandNoiseCondition;
import de.ambertation.wover.surface.api.noise.NumericProvider;
import de.ambertation.wover.surface.api.noise.NumericProviderRegistry;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;

public class EndNumericProviders {
    public static final ResourceKey<MapCodec<? extends NumericProvider>> SULPHURIC_SURF
            = NumericProviderRegistry.createKey(BetterEnd.C.id("sulphuric_surf"));

    public static final ResourceKey<MapCodec<? extends NumericProvider>> VERTICAL_BAND
            = NumericProviderRegistry.createKey(BetterEnd.C.id("vertical_band"));

    public static final ResourceKey<MapCodec<? extends NumericProvider>> SPLIT_NOISE
            = NumericProviderRegistry.createKey(BetterEnd.C.id("split_noise"));

    public static final ResourceKey<MapCodec<? extends NumericProvider>> UMBRA_SRF
            = NumericProviderRegistry.createKey(BetterEnd.C.id("umbra_srf"));

    public static void register() {
        NumericProviderRegistry.register(SULPHURIC_SURF, (MapCodec<? extends NumericProvider>) SulphuricSurfaceNoiseCondition.CODEC);
        NumericProviderRegistry.register(VERTICAL_BAND, VerticalBandNoiseCondition.CODEC);
        NumericProviderRegistry.register(SPLIT_NOISE, SplitNoiseCondition.CODEC);
        NumericProviderRegistry.register(UMBRA_SRF, UmbraSurfaceNoiseCondition.CODEC);
    }
}
