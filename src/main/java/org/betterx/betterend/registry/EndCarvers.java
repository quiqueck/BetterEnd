package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.carvers.EndCaveCarver;
import org.betterx.betterend.world.carvers.EndCaveCarverConfiguration;
import org.betterx.betterend.world.carvers.EndTunnelCarver;
import org.betterx.betterend.world.carvers.EndTunnelCarverConfiguration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

/**
 * Registers the two BetterEnd {@link WorldCarver}s into {@link BuiltInRegistries#CARVER} and
 * exposes the {@link ResourceKey}s of their configured variants (which live in the
 * {@link Registries#CONFIGURED_CARVER} datapack registry, populated by {@code CarverProvider}).
 */
public class EndCarvers {
    public static final EndCaveCarver END_ROUND_CAVE = register(
            "end_round_cave",
            new EndCaveCarver(EndCaveCarverConfiguration.CODEC)
    );
    public static final EndTunnelCarver END_TUNNEL_CAVE = register(
            "end_tunnel_cave",
            new EndTunnelCarver(EndTunnelCarverConfiguration.CODEC)
    );

    public static final ResourceKey<ConfiguredWorldCarver<?>> ROUND_CAVE = configuredKey("round_cave");
    public static final ResourceKey<ConfiguredWorldCarver<?>> TUNNEL_CAVE = configuredKey("tunnel_cave");

    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(
            String name,
            F carver
    ) {
        return Registry.register(BuiltInRegistries.CARVER, BetterEnd.C.mk(name), carver);
    }

    private static ResourceKey<ConfiguredWorldCarver<?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, BetterEnd.C.mk(name));
    }

    /**
     * Referencing this class triggers static initialization, which performs the
     * {@link BuiltInRegistries#CARVER} registrations above. Called from
     * {@code BetterEnd.onInitialize}.
     */
    public static void ensureStaticallyLoaded() {
    }
}
