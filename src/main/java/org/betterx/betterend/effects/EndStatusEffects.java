package org.betterx.betterend.effects;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.effects.status.EndVeilEffect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import org.jetbrains.annotations.ApiStatus;

public class EndStatusEffects {
    public final static MobEffectInstance CRYSTALITE_HEALTH_REGEN = new MobEffectInstance(
            MobEffects.REGENERATION,
            80,
            0,
            true,
            false,
            true
    );
    public final static MobEffectInstance CRYSTALITE_DIG_SPEED = new MobEffectInstance(
            MobEffects.HASTE,
            80,
            0,
            true,
            false,
            true
    );
    public final static MobEffectInstance CRYSTALITE_MOVE_SPEED = new MobEffectInstance(
            MobEffects.SPEED,
            80,
            0,
            true,
            false,
            true
    );

    public final static Holder<MobEffect> END_VEIL = registerEffect("end_veil", new EndVeilEffect());

    public static <E extends MobEffect> Holder<MobEffect> registerEffect(String name, E effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, BetterEnd.C.mk(name), effect);
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }
}
