package org.betterx.betterend.client.effects;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.Nullable;

/**
 * The jolt a vision arrives with, shared by every hint that has one.
 * <p>
 * Two detuned sines rather than one, so the motion does not read as a clean pendulum, damped
 * exponentially from the trigger - the jolt is over long before the vision it announces fades.
 */
@Environment(EnvType.CLIENT)
final class ScreenWobble {
    private static final float DECAY = 14.0F;
    private static final float MAX_ROLL = 4.5F;
    private static final float MAX_PITCH = 2.5F;

    private ScreenWobble() {
    }

    /**
     * Roll and pitch in degrees for a vision {@code elapsed} ticks old, or {@code null} once the jolt
     * has decayed to nothing. Scaled by the Distortion Effects accessibility slider, which is what
     * that setting is for.
     */
    static float @Nullable [] at(float elapsed) {
        if (elapsed < 0) return null;

        float amplitude = Mth.clamp((float) Math.exp(-elapsed / DECAY), 0.0F, 1.0F)
                * Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        if (amplitude <= 0.001F) return null;

        return new float[]{
                Mth.sin(elapsed * 0.55F) * amplitude * MAX_ROLL,
                Mth.sin(elapsed * 0.37F + 1.1F) * amplitude * MAX_PITCH
        };
    }
}
