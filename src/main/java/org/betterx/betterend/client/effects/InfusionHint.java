package org.betterx.betterend.client.effects;

import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.rituals.InfusionRitual;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.Nullable;

/**
 * Client-side state of the "here is where the catalyst pedestals go" hint: a short screen wobble
 * plus ghostly outlines drawn at the 8 catalyst sockets of one infusion pedestal.
 * <p>
 * Only one hint is ever active. It is purely presentational, so it lives entirely on the client and
 * is triggered locally from {@code InfusionPedestal} (both {@code setPlacedBy} and {@code useItemOn}
 * run on the client as well) - no packet involved.
 */
@Environment(EnvType.CLIENT)
public final class InfusionHint {
    /** How long the socket outlines stay up. */
    private static final int DURATION = 160;
    /** Ramp of the outlines at either end of {@link #DURATION}. */
    private static final int FADE_IN = 8;
    private static final int FADE_OUT = 30;
    /** Beyond this the hint is dropped - it belongs to a pedestal the player walked away from. */
    private static final double MAX_DISTANCE_SQ = 48 * 48;
    /** Mist is released at the top of the ghost outline so it visibly falls down the whole shape. */
    private static final double MIST_SPAWN_HEIGHT = 1.0;
    private static final double MIST_SPAWN_JITTER = 0.35;
    private static final double MIST_SPREAD = 0.008;
    /** The placement puff starts on the floor, so it rolls outwards instead of falling first. */
    private static final int BURST_COUNT = 14;
    private static final double BURST_RADIUS = 0.34;
    private static final double BURST_HEIGHT = 0.08;

    /**
     * How much of the outline alpha a subtle hint keeps - present, but clearly not a fresh summons.
     * <p>
     * This was 0.14 while the outlines were being drawn through a line type that discarded alpha on
     * 26.3; the value was picked to fight a renderer that was ignoring it. With blending actually
     * working it only had to come down a little from full.
     */
    private static final float SUBTLE_ALPHA = 0.45F;
    /** Runes sent down each spoke per pulse, and how many pulses the confirmation runs for. */
    private static final int FLASH_PER_SPOKE = 3;
    private static final int FLASH_PULSES = 4;
    private static final int FLASH_PULSE_INTERVAL = 10;
    /**
     * Launch height. Enchant runes dip about 1.2 blocks as they arrive, so this is set high enough that
     * they land around pedestal-top rather than sinking into the floor.
     */
    private static final double FLASH_HEIGHT = 1.6;

    private static BlockPos pedestalPos = null;
    private static long startTime = 0;
    private static boolean subtle = false;
    private static BlockPos flashPos = null;
    private static long flashStart = 0;

    private InfusionHint() {
    }

    /**
     * (Re)starts the full hint for the pedestal at {@code pos} - wobble, bright outlines and mist.
     * Re-triggering while a hint is running restarts it, so repeatedly clicking a pedestal keeps the
     * outlines up.
     */
    public static void trigger(Level level, BlockPos pos) {
        start(level, pos, false);
    }

    /**
     * Brings the vision back faintly: outlines only, no wobble and no mist.
     * <p>
     * For the acknowledgement after a pedestal lands on a socket. Showing the layout again there is
     * useful - it says which sockets are still open - but a second full summons for every one of eight
     * placements would be its own kind of noise.
     * <p>
     * Always replaces whatever was showing, including a full hint - "placing one drops the vision to
     * faint" is a rule a player can learn, where "unless you asked for it recently" is not.
     */
    public static void triggerSubtle(Level level, BlockPos pos) {
        start(level, pos, true);
    }

    private static void start(Level level, BlockPos pos, boolean quiet) {
        pedestalPos = pos.immutable();
        startTime = level.getGameTime();
        subtle = quiet;
    }

    public static void clear() {
        pedestalPos = null;
    }

    /**
     * Runs the current hint out through its fade instead of cutting it. Used when the ring closes -
     * the outlines have nothing left to point at, but snapping them off mid-frame reads as a glitch
     * next to the flash that replaces them.
     */
    public static void dismiss(Level level) {
        if (pedestalPos == null) return;
        long elapsed = level.getGameTime() - startTime;
        long intoFade = DURATION - FADE_OUT;
        // Jumped to the start of the existing fade tail, so the alpha carries on from full rather than
        // stepping. Never rewound: a hint already deeper into its fade keeps fading.
        if (elapsed < intoFade) startTime = level.getGameTime() - intoFade;
    }

    public static BlockPos activePedestal() {
        return pedestalPos;
    }

    public static boolean isActiveAt(BlockPos pos) {
        return pedestalPos != null && pedestalPos.equals(pos);
    }

    /**
     * Age of the hint in ticks, or a negative value when no hint is running. Expiry and the
     * distance check are folded in here so every caller drops the hint at the same moment.
     */
    private static float age(float partialTick) {
        if (pedestalPos == null) return -1;

        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null || minecraft.player == null) {
            clear();
            return -1;
        }
        if (minecraft.player.distanceToSqr(Vec3.atCenterOf(pedestalPos)) > MAX_DISTANCE_SQ) {
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
     * Pours mist off the sockets that still need a pedestal. Called once per client tick from the
     * pedestal's block entity ticker, so the spawn rate is tied to ticks rather than frame rate.
     */
    public static void spawnMist(Level level, BlockPos pedestalPos) {
        // A subtle hint is outlines only; the falling columns are what make it a summons.
        if (subtle || !isActiveAt(pedestalPos)) return;

        float elapsed = age(0);
        if (elapsed < 0) return;

        RandomSource random = level.getRandom();
        boolean[] present = InfusionRitual.socketsPresent(level, pedestalPos);
        for (int i = 0; i < present.length; i++) {
            // Only the empty sockets smoke - the mist is pointing at what is still missing.
            if (present[i] || random.nextBoolean()) continue;

            BlockPos socket = InfusionRitual.socketPos(pedestalPos, i);
            level.addParticle(
                    mistFor(i),
                    socket.getX() + 0.25 + random.nextDouble() * 0.5,
                    // Jittered release height, so wisps from one socket do not descend in a rank.
                    socket.getY() + MIST_SPAWN_HEIGHT + (random.nextDouble() - 0.5) * MIST_SPAWN_JITTER,
                    socket.getZ() + 0.25 + random.nextDouble() * 0.5,
                    (random.nextDouble() - 0.5) * MIST_SPREAD,
                    0,
                    (random.nextDouble() - 0.5) * MIST_SPREAD
            );
        }
    }

    /**
     * Socket outline drawing values as {@code {fade, pulse}}, both {@code 0..1}, or {@code null}
     * while no hint is running. {@code fade} ramps the whole hint in and out; {@code pulse} is the
     * shimmer applied to sockets that still need a pedestal, driven off the hint's own age so it
     * never jumps mid-hint.
     * <p>
     * Returned in one call for the same reason as {@link #wobble(float)}: reading the hint can
     * expire it.
     */
    public static float @Nullable [] outline(float partialTick) {
        float elapsed = age(partialTick);
        if (elapsed < 0) return null;

        float fade;
        if (elapsed < FADE_IN) {
            fade = elapsed / FADE_IN;
        } else if (elapsed > DURATION - FADE_OUT) {
            fade = (DURATION - elapsed) / FADE_OUT;
        } else {
            fade = 1.0F;
        }

        if (subtle) fade *= SUBTLE_ALPHA;
        return new float[]{fade, 0.65F + 0.35F * Mth.sin(elapsed / 4.0F)};
    }

    /**
     * The mist type for catalyst socket {@code index} - north gets the warm one, so the ring reads
     * with an orientation instead of as eight identical columns.
     */
    private static SimpleParticleType mistFor(int index) {
        return index == InfusionRecipe.CatalystSlot.NORTH.index
                ? EndParticles.INFUSION_MIST_NORTH
                : EndParticles.INFUSION_MIST;
    }

    /**
     * A one-off puff at a socket a pedestal was just correctly placed on.
     * <p>
     * Released just above the floor rather than at the top of the ghost, so it lands within a tick or
     * two and spends its life doing the outward roll - the confirming half of the effect, without the
     * fall that means "still missing". Independent of {@link #trigger}: placing a pedestal in the
     * right spot should confirm itself whether or not a hint happens to be running.
     */
    public static void burstAtSocket(Level level, BlockPos socketPos, int index) {
        RandomSource random = level.getRandom();
        SimpleParticleType type = mistFor(index);
        for (int i = 0; i < BURST_COUNT; i++) {
            double angle = i * Mth.TWO_PI / BURST_COUNT + random.nextDouble() * 0.3;
            level.addParticle(
                    type,
                    socketPos.getX() + 0.5 + Math.cos(angle) * BURST_RADIUS,
                    socketPos.getY() + BURST_HEIGHT,
                    socketPos.getZ() + 0.5 + Math.sin(angle) * BURST_RADIUS,
                    0,
                    0,
                    0
            );
        }
    }

    /**
     * Starts the confirmation for a ring that just closed. The pulses themselves are emitted from the
     * pedestal's ticker, so they run on ticks rather than all landing in the single frame the last
     * pedestal was placed in.
     */
    public static void beginFlash(Level level, BlockPos pedestalPos) {
        flashPos = pedestalPos.immutable();
        flashStart = level.getGameTime();

        // playLocalSound, not playSound: the whole hint is a client-side affair, and routing this
        // through the level would ask the server to broadcast an effect only this player can see.
        level.playLocalSound(
                pedestalPos.getX() + 0.5,
                pedestalPos.getY() + 0.5,
                pedestalPos.getZ() + 0.5,
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.BLOCKS,
                0.8F,
                1.0F,
                false
        );
    }

    /**
     * Emits one pulse of the completion flash when a pulse is due.
     * <p>
     * Runes race from the infusion pedestal out along all eight spokes at once, repeated a few times so
     * the ring closing is something a player notices rather than a single frame they can miss. Each
     * rune outlives its pulse, so the waves overlap into one continuous surge.
     */
    public static void tickFlash(Level level, BlockPos pos) {
        if (flashPos == null || !flashPos.equals(pos)) return;

        long elapsed = level.getGameTime() - flashStart;
        if (elapsed < 0 || elapsed >= (long) FLASH_PULSES * FLASH_PULSE_INTERVAL) {
            flashPos = null;
            return;
        }
        if (elapsed % FLASH_PULSE_INTERVAL != 0) return;

        RandomSource random = level.getRandom();
        double originX = pos.getX() + 0.5;
        double originY = pos.getY() + FLASH_HEIGHT;
        double originZ = pos.getZ() + 0.5;

        for (int socket = 0; socket < InfusionRitual.getMap().length; socket++) {
            BlockPos target = InfusionRitual.socketPos(pos, socket);
            double targetX = target.getX() + 0.5;
            double targetY = target.getY() + FLASH_HEIGHT;
            double targetZ = target.getZ() + 0.5;

            for (int i = 0; i < FLASH_PER_SPOKE; i++) {
                // ENCHANT spawns at its destination and treats the velocity arguments as where to start
                // from, so the rune is placed on the socket and handed the offset back to the pedestal.
                double jitter = (random.nextDouble() - 0.5) * 0.4;
                level.addParticle(
                        ParticleTypes.ENCHANT,
                        targetX,
                        targetY,
                        targetZ,
                        originX - targetX + jitter,
                        originY - targetY,
                        originZ - targetZ + jitter
                );
            }
        }
    }

    /**
     * Screen wobble for this frame, or {@code null} while no full hint is running.
     * <p>
     * Returned in one call rather than as separate amplitude/phase getters because reading the hint
     * can expire it, and a single frame must not see it both alive and dead.
     */
    public static float @Nullable [] wobble(float partialTick) {
        if (subtle) return null;
        return ScreenWobble.at(age(partialTick));
    }
}
