package org.betterx.betterend.registry;

import org.betterx.betterend.particle.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;

@Environment(EnvType.CLIENT)
public class EndParticleProviders {
    public static void register() {
        ParticleProviderRegistry.getInstance().register(EndParticles.GLOWING_SPHERE, ParticleGlowingSphere.FactoryGlowingSphere::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.PORTAL_SPHERE, PaticlePortalSphere.FactoryPortalSphere::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.INFUSION, InfusionParticle.InfusionFactory::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.SULPHUR_PARTICLE, ParticleSulphur.FactorySulphur::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.GEYSER_PARTICLE, ParticleGeyser.FactoryGeyser::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.SNOWFLAKE, ParticleSnowflake.FactorySnowflake::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.AMBER_SPHERE, ParticleGlowingSphere.FactoryGlowingSphere::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.BLACK_SPORE, ParticleBlackSpore.FactoryBlackSpore::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.TENANEA_PETAL, ParticleTenaneaPetal.FactoryTenaneaPetal::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.JUNGLE_SPORE, ParticleJungleSpore.FactoryJungleSpore::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.FIREFLY, FireflyParticle.FireflyParticleFactory::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.SMARAGDANT, SmaragdantParticle.SmaragdantParticleFactory::new);
        ParticleProviderRegistry.getInstance().register(EndParticles.INFUSION_MIST, InfusionMistParticle.FactoryInfusionMist::new);
        ParticleProviderRegistry.getInstance().register(
                EndParticles.INFUSION_MIST_NORTH,
                sprites -> new InfusionMistParticle.FactoryInfusionMist(sprites, InfusionMistParticle.NORTH_RGB)
        );
    }
}
