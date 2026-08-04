package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.effects.EndStatusEffects;
import de.ambertation.wover.enchantment.api.EnchantmentKey;
import de.ambertation.wover.enchantment.api.EnchantmentManager;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;

import org.jetbrains.annotations.ApiStatus;

public class EndEnchantments {
    public final static EnchantmentKey END_VEIL = EnchantmentManager.createKey(BetterEnd.C.mk("end_veil"));

    public static final DataComponentType<Unit> END_VEIL_STATE = EnchantmentManager.registerEffectComponent(
            BetterEnd.C.mk("end_veil"),
            (builder) -> builder.persistent(Unit.CODEC)
    );

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        EndStatusEffects.ensureStaticallyLoaded();
    }
}
