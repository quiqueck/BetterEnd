package org.betterx.betterend.particle;

import org.betterx.bclib.util.MHelper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ParticleSulphur extends SingleQuadParticle {
    private int ticks;
    private double preVX;
    private double preVY;
    private double preVZ;
    private double nextVX;
    private double nextVY;
    private double nextVZ;

    protected ParticleSulphur(
            ClientLevel world,
            double x,
            double y,
            double z,
            double r,
            double g,
            double b,
            SpriteSet sprites
    ) {
        super(world, x, y, z, r, g, b, sprites.get(world.getRandom()));

        this.lifetime = MHelper.randRange(150, 300, random);
        this.quadSize = MHelper.randRange(0.05F, 0.15F, random);
        this.setColor(1, 1, 1);
        this.setAlpha(0);

        preVX = random.nextGaussian() * 0.015;
        preVY = random.nextGaussian() * 0.015;
        preVZ = random.nextGaussian() * 0.015;

        nextVX = random.nextGaussian() * 0.015;
        nextVY = random.nextGaussian() * 0.015;
        nextVZ = random.nextGaussian() * 0.015;
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks > 200) {
            preVX = nextVX;
            preVY = nextVY;
            preVZ = nextVZ;
            nextVX = random.nextGaussian() * 0.015;
            nextVY = random.nextGaussian() * 0.015;
            nextVZ = random.nextGaussian() * 0.015;
            if (random.nextInt(4) == 0) {
                nextVY = Math.abs(nextVY);
            }
            ticks = 0;
        }
        double delta = (double) ticks / 200.0;

        if (this.age <= 40) {
            this.setAlpha(this.age / 40F);
        } else if (this.age >= this.lifetime - 40) {
            this.setAlpha((this.lifetime - this.age) / 40F);
        }

        if (this.age >= this.lifetime) {
            this.remove();
        }

        this.xd = Mth.lerp(delta, preVX, nextVX);
        this.yd = Mth.lerp(delta, preVY, nextVY);
        this.zd = Mth.lerp(delta, preVZ, nextVZ);

        super.tick();
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class FactorySulphur implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public FactorySulphur(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel world,
                double x,
                double y,
                double z,
                double vX,
                double vY,
                double vZ,
                RandomSource randomSource
        ) {
            return new ParticleSulphur(world, x, y, z, 1, 1, 1, sprites);
        }
    }
}