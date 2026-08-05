package org.betterx.betterend.registry;

import org.betterx.bclib.particles.BCLParticleType;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.particle.InfusionParticleType;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

public class EndParticles {
    public static final SimpleParticleType GLOWING_SPHERE = register("glowing_sphere");
    public static final SimpleParticleType PORTAL_SPHERE = register("portal_sphere");
    public static final ParticleType<InfusionParticleType> INFUSION = BCLParticleType.register(BetterEnd.C.mk("infusion"), InfusionParticleType.CODEC, InfusionParticleType.STREAM_CODEC);

    public static final SimpleParticleType SULPHUR_PARTICLE = register("sulphur_particle");
    public static final SimpleParticleType GEYSER_PARTICLE = registerFar("geyser_particle");
    public static final SimpleParticleType SNOWFLAKE = register("snowflake");
    public static final SimpleParticleType AMBER_SPHERE = register("amber_sphere");
    public static final SimpleParticleType BLACK_SPORE = register("black_spore");
    public static final SimpleParticleType TENANEA_PETAL = register("tenanea_petal");
    public static final SimpleParticleType JUNGLE_SPORE = register("jungle_spore");
    public static final SimpleParticleType FIREFLY = register("firefly");
    public static final SimpleParticleType SMARAGDANT = register("smaragdant_particle");
    public static final SimpleParticleType INFUSION_MIST = register("infusion_mist");
    public static final SimpleParticleType INFUSION_MIST_NORTH = register("infusion_mist_north");

    private static SimpleParticleType register(String name) {
        return BCLParticleType.register(BetterEnd.C.mk(name));
    }

    private static SimpleParticleType registerFar(String name) {
        return BCLParticleType.register(BetterEnd.C.mk(name), true);
    }

    public static void ensureStaticallyLoadedServerside() {
    }
}
