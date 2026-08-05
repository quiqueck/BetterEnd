package org.betterx.betterend.particle;

import org.betterx.bclib.util.MHelper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Heavy mist pouring off a ghost pedestal socket: sinks slowly, and on reaching the floor stops
 * falling and creeps outwards, the way cold smoke pools and rolls instead of dispersing.
 * <p>
 * Written as its own particle instead of reusing {@link ParticleGlowingSphere} because that one
 * overwrites {@code xd/yd/zd} with fresh gaussians on every tick, so a spawn velocity handed to
 * {@code addParticle} never survives its first tick.
 */
@Environment(EnvType.CLIENT)
public class InfusionMistParticle extends SingleQuadParticle {
    /**
     * Re-applied every tick rather than left to drag or gravity: a decaying fall speed runs out
     * after a fixed distance and leaves the mist hanging in mid-air, and gravity accelerates it into
     * a raindrop. A constant sink keeps the slow, heavy pace and still always reaches the floor,
     * whatever the drop height.
     */
    private static final double SINK_SPEED_MIN = 0.022;
    private static final double SINK_SPEED_MAX = 0.048;
    /** Sideways sway on the way down, so wisps wander instead of dropping on rails. */
    private static final double SWAY_STRENGTH = 0.0015;
    /**
     * Fast enough that the sine turns over inside the ~30 ticks of a fall. Slower than that and each
     * wisp only ever sees half a cycle, so the sway stops cancelling and integrates into a straight
     * sideways drift - which is a fan, not a wander.
     */
    private static final float SWAY_SPEED_MIN = 0.12F;
    private static final float SWAY_SPEED_MAX = 0.28F;
    /** Hard ceiling on lateral speed during the fall; the sway accumulates, so it needs a stop. */
    private static final double MAX_FALL_DRIFT = 0.012;
    /** The outward roll after landing eases in rather than snapping to full speed. */
    private static final double GROUND_ACCELERATION = 0.0035;
    private static final double MAX_GROUND_SPEED = 0.045;
    /** Spreading mist also thins and widens; the growth sells the billow more than the motion does. */
    private static final float GROUND_GROWTH = 1.025F;
    private static final float MAX_QUAD_SIZE = 0.42F;
    private static final float MAX_ALPHA = 0.85F;
    private static final int FADE_TICKS = 10;

    /**
     * Warm gold dust, matching the socket outlines it pours from. The sprites are deliberately neutral
     * white ({@code infusion_mote_*}) - particle colour multiplies the texture, so tinting the cyan
     * {@code glowing_sphere} sprites could never produce a warm result no matter what was asked for.
     */
    public static final int DEFAULT_RGB = 0xFFD98A;
    /**
     * The north socket is the one the recipe layouts are oriented from, so its dust goes purple - the
     * ring stops being eight interchangeable positions and gains a readable "this way up".
     */
    public static final int NORTH_RGB = 0xC79BFF;

    /** Unit vector along the floor, pointing away from the socket the mist fell from. */
    private final double driftX;
    private final double driftZ;
    private final double sinkSpeed;
    private final float swaySpeed;
    private final float swayPhase;
    private final float swayAxis;
    private double groundSpeed;
    private boolean landed;

    protected InfusionMistParticle(
            ClientLevel world,
            double x,
            double y,
            double z,
            double vX,
            double vY,
            double vZ,
            SpriteSet sprites,
            int rgb
    ) {
        super(world, x, y, z, sprites.get(world.getRandom()));

        this.lifetime = MHelper.randRange(50, 80, random);
        this.quadSize = MHelper.randRange(0.10F, 0.20F, random);
        // Every wisp falls at its own pace and sways on its own schedule; a shared rate makes eight
        // sockets look like eight identical sprinklers.
        this.sinkSpeed = MHelper.randRange(SINK_SPEED_MIN, SINK_SPEED_MAX, random);
        this.swaySpeed = MHelper.randRange(SWAY_SPEED_MIN, SWAY_SPEED_MAX, random);
        this.swayPhase = random.nextFloat() * Mth.TWO_PI;
        this.swayAxis = random.nextFloat() * Mth.TWO_PI;
        this.xd = vX;
        this.yd = vY;
        this.zd = vZ;
        this.rCol = ARGB.redFloat(rgb);
        this.gCol = ARGB.greenFloat(rgb);
        this.bCol = ARGB.blueFloat(rgb);
        this.setAlpha(0.0F);
        this.gravity = 0.0F;

        // Roll outwards from wherever inside the socket footprint this wisp started, so the mist
        // opens away from the pedestal instead of every wisp wandering off independently.
        double offsetX = x - Math.floor(x) - 0.5;
        double offsetZ = z - Math.floor(z) - 0.5;
        double length = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
        if (length < 1.0E-4) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            this.driftX = Mth.cos(angle);
            this.driftZ = Mth.sin(angle);
        } else {
            this.driftX = offsetX / length;
            this.driftZ = offsetZ / length;
        }
    }

    @Override
    public void tick() {
        if (!this.landed) {
            this.yd = -this.sinkSpeed;
            // Sway is added to the spawn drift rather than replacing it, so the wisp keeps curling
            // around its own path instead of being snapped onto a sine wave.
            float sway = Mth.sin(this.swayPhase + this.age * this.swaySpeed) * (float) SWAY_STRENGTH;
            this.xd = Mth.clamp(this.xd + Mth.cos(this.swayAxis) * sway, -MAX_FALL_DRIFT, MAX_FALL_DRIFT);
            this.zd = Mth.clamp(this.zd + Mth.sin(this.swayAxis) * sway, -MAX_FALL_DRIFT, MAX_FALL_DRIFT);
        }

        if (this.age < FADE_TICKS) {
            this.setAlpha(MAX_ALPHA * this.age / FADE_TICKS);
        } else if (this.age >= this.lifetime - FADE_TICKS) {
            this.setAlpha(MAX_ALPHA * (this.lifetime - this.age) / FADE_TICKS);
        } else {
            this.setAlpha(MAX_ALPHA);
        }

        super.tick();

        if (this.onGround) {
            this.landed = true;
        }
        if (this.landed) {
            this.groundSpeed = Math.min(MAX_GROUND_SPEED, this.groundSpeed + GROUND_ACCELERATION);
            this.quadSize = Math.min(MAX_QUAD_SIZE, this.quadSize * GROUND_GROWTH);
            // Moved by hand, not through xd/zd: the blocked fall sets Particle#stoppedByCollision,
            // after which move() returns immediately and no velocity has any effect ever again.
            // Doing it after super.tick() also leaves xo/zo on last tick's position, so the slide
            // still interpolates between frames.
            setPos(this.x + this.driftX * this.groundSpeed, this.y, this.z + this.driftZ * this.groundSpeed);
        }
    }

    /**
     * Lit as if it glows. The sockets being pointed at are usually in End twilight or in the shadow
     * of whatever the player is building on, and ambient lighting drags the mist down to almost
     * invisible there - which defeats the point of a hint.
     */
    @Override
    protected int getLightCoords(float a) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class FactoryInfusionMist implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final int rgb;

        public FactoryInfusionMist(SpriteSet sprites) {
            this(sprites, DEFAULT_RGB);
        }

        public FactoryInfusionMist(SpriteSet sprites, int rgb) {
            this.sprites = sprites;
            this.rgb = rgb;
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
            return new InfusionMistParticle(world, x, y, z, vX, vY, vZ, sprites, rgb);
        }
    }
}
