package org.betterx.betterend.client.effects;

import org.betterx.bclib.interfaces.ClientLevelAccess;
import org.betterx.betterend.blocks.RunedFlavolite;
import org.betterx.betterend.client.render.EternalCrystalRenderer;
import org.betterx.betterend.client.render.PedestalItemRenderer;
import org.betterx.betterend.portal.PortalBuilder;
import org.betterx.betterend.rituals.EternalRitual;
import de.ambertation.wunderlib.math.Float3;
import de.ambertation.wunderlib.ui.ColorHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side vision of the eternal portal ritual: crystals turning above every pedestal the ritual
 * wants, streaks running in towards the portal centre, and outlines where frame blocks are missing.
 * <p>
 * The counterpart to {@link InfusionHint}, and the same shape - one vision at a time, held entirely
 * on the client, triggered from a block interaction that runs on both sides but only acts on one.
 * <p>
 * The look deliberately borrows from what a finished ritual already does (see
 * {@code EternalPedestal#dispatchParticles}): the same crystal-coloured spell particles running the
 * same pedestal-to-centre path. The vision is meant to read as a preview of that, not as its own
 * separate effect.
 */
@Environment(EnvType.CLIENT)
public final class EternalHint {
    private static final int DURATION = 220;
    private static final int FADE_IN = 8;
    private static final int FADE_OUT = 40;
    private static final double MAX_DISTANCE_SQ = 64 * 64;

    /** Height the ghost crystals hover at above their pedestals, matching a held eternal crystal. */
    public static final double CRYSTAL_HEIGHT = 1.35;
    /** Streaks are fast - they are pointing somewhere, not drifting. */
    private static final double STREAK_SPEED = 0.55;
    private static final int STREAKS_PER_TICK = 2;
    /** Weeping-obsidian drips, but quicker: the gaps should nag. */
    private static final float DRIP_CHANCE = 0.35F;

    private static BlockPos center = null;
    private static BlockPos source = null;
    private static Direction.Axis axis = null;
    private static long startTime = 0;

    private EternalHint() {
    }

    /**
     * Starts the vision for the ritual the pedestal at {@code pos} belongs to.
     * <p>
     * The centre and axis come straight from {@link EternalRitual#configure(BlockPos)}, which infers
     * them by looking for neighbouring pedestals and falls back to a fixed guess when it finds none.
     * Whatever it reports is what the ritual itself would use, so the vision always shows the layout
     * that would actually form - including from a lone pedestal.
     */
    public static void trigger(Level level, BlockPos pos) {
        EternalRitual ritual = new EternalRitual(level, pos);
        if (ritual.getCenter() == null || ritual.getAxis() == null) return;

        center = ritual.getCenter().immutable();
        source = pos.immutable();
        axis = ritual.getAxis();
        startTime = level.getGameTime();
    }

    public static void clear() {
        center = null;
        source = null;
    }

    /**
     * The block entity the vision is drawn from - the pedestal that was clicked.
     * <p>
     * It has to hang off exactly one of them: drawn from every pedestal in the ring it would be drawn
     * six times over. The clicked one is stable for the life of the vision and is certain to exist,
     * which a position derived from the layout would not be.
     */
    public static @Nullable BlockPos anchorPedestal(Level level) {
        return intensity(0) > 0 ? source : null;
    }

    public static @Nullable BlockPos center() {
        return center;
    }

    /**
     * Age in ticks, or negative when nothing is showing. Expiry and the distance check live here so
     * every caller drops the vision at the same moment.
     */
    private static float age(float partialTick) {
        if (center == null) return -1;

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null || minecraft.player == null) {
            clear();
            return -1;
        }
        if (minecraft.player.distanceToSqr(Vec3.atCenterOf(center)) > MAX_DISTANCE_SQ) {
            clear();
            return -1;
        }

        float elapsed = level.getGameTime() - startTime + partialTick;
        if (elapsed < 0 || elapsed > DURATION) {
            clear();
            return -1;
        }
        return elapsed;
    }

    /**
     * Fade envelope of the vision, {@code 0..1}, or {@code 0} when nothing is showing.
     */
    public static float intensity(float partialTick) {
        float elapsed = age(partialTick);
        if (elapsed < 0) return 0;
        if (elapsed < FADE_IN) return elapsed / FADE_IN;
        if (elapsed > DURATION - FADE_OUT) return (DURATION - elapsed) / FADE_OUT;
        return 1.0F;
    }

    /**
     * Screen wobble for this frame, or {@code null} while no vision is running. The eternal vision
     * arrives with the same jolt the infusion one does - it is the same kind of event.
     */
    public static float @Nullable [] wobble(float partialTick) {
        return ScreenWobble.at(age(partialTick));
    }

    /**
     * Where the ritual wants its six pedestals, in world space.
     */
    public static List<BlockPos> pedestalPositions() {
        List<BlockPos> out = new ArrayList<>();
        if (center == null) return out;

        Direction moveX = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction moveY = axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        for (Point p : EternalRitual.pedestalOffsets()) {
            out.add(center.relative(moveX, p.x).relative(moveY, p.y));
        }
        return out;
    }

    /**
     * Frame positions that still need a runed flavolite block. Mirrored on both sides exactly the way
     * {@code EternalRitual#checkFrame} walks them, so the vision cannot disagree with the check.
     */
    public static List<BlockPos> missingFrame(Level level) {
        List<BlockPos> out = new ArrayList<>();
        if (center == null) return out;

        BlockPos framePos = center.below();
        Direction moveDir = axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST;
        for (Point point : PortalBuilder.FRAME_POSITIONS) {
            for (int sign = -1; sign <= 1; sign += 2) {
                // The frame set has entries on the centre line, and mirroring those lands on the same
                // block twice. checkFrame does not care that it tests one twice; a vision would draw it
                // twice and drip at double rate.
                if (sign > 0 && point.x == 0) continue;

                BlockPos pos = framePos.relative(moveDir, sign * point.x).above(point.y);
                if (!(level.getBlockState(pos).getBlock() instanceof RunedFlavolite)) {
                    out.add(pos);
                }
            }
        }
        return out;
    }

    /**
     * One tick of the vision's particles, driven from the pedestal's {@code animateTick}.
     * <p>
     * Streaks run pedestal-to-centre like a live ritual's do, and every gap in the frame drips - at a
     * higher rate than weeping obsidian, because a missing block is the thing being complained about.
     */
    public static void tickParticles(Level level, BlockPos pedestalPos, RandomSource random) {
        if (intensity(0) <= 0) return;

        ClientLevelAccess clientLevel = level instanceof ClientLevelAccess access ? access : null;
        if (clientLevel == null) return;

        float[] color = ColorHelper.toFloatArrayRGBA(EternalCrystalRenderer.colors(PedestalItemRenderer.getGemAge()));
        ParticleOptions tinted = SpellParticleOption.create(ParticleTypes.EFFECT, color[0], color[1], color[2], 1.0F);

        streakToCentre(clientLevel, pedestalPos, random, tinted);
        dripFromGaps(clientLevel, level, random, tinted);
    }

    private static void streakToCentre(
            ClientLevelAccess clientLevel,
            BlockPos pedestalPos,
            RandomSource random,
            ParticleOptions tinted
    ) {
        Float3 start = Float3.of(pedestalPos);
        Float3 towards = Float3.of(center).sub(start).normalized().mul(STREAK_SPEED);

        for (int i = 0; i < STREAKS_PER_TICK; i++) {
            var particle = clientLevel.bcl_addParticle(
                    tinted,
                    start.x + 0.3 + random.nextFloat() * 0.4,
                    start.y + CRYSTAL_HEIGHT,
                    start.z + 0.3 + random.nextFloat() * 0.4,
                    0,
                    0,
                    0
            );
            if (particle == null) continue;
            particle.setParticleSpeed(towards.x, towards.y, towards.z);
            // Short-lived on purpose: a streak that outlives its flight turns into a drifting blob.
            particle.setLifetime(5 + random.nextInt(3));
        }
    }

    private static void dripFromGaps(
            ClientLevelAccess clientLevel,
            Level level,
            RandomSource random,
            ParticleOptions tinted
    ) {
        for (BlockPos gap : missingFrame(level)) {
            if (random.nextFloat() > DRIP_CHANCE) continue;

            var particle = clientLevel.bcl_addParticle(
                    tinted,
                    gap.getX() + random.nextDouble(),
                    gap.getY() + 0.05,
                    gap.getZ() + random.nextDouble(),
                    0,
                    0,
                    0
            );
            if (particle == null) continue;
            particle.setParticleSpeed(0, -0.08 - random.nextDouble() * 0.05, 0);
        }
    }
}
