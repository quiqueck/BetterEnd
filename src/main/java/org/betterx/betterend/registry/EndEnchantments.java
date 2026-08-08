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

    /**
     * Area-mining enchantment for hammers. Level 1 mines a 3x3x3 cube, level 2 a 5x5x5 cube, both
     * centered on the targeted block. The actual mining logic lives in
     * {@link org.betterx.betterend.mixin.common.ServerPlayerGameModeMixin}, since there is no vanilla
     * effect component for "break more than one block".
     */
    public final static EnchantmentKey RESONANCE = EnchantmentManager.createKey(BetterEnd.C.mk("resonance"));

    public static final DataComponentType<Unit> END_VEIL_STATE = EnchantmentManager.registerEffectComponent(
            BetterEnd.C.mk("end_veil"),
            (builder) -> builder.persistent(Unit.CODEC)
    );

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        EndStatusEffects.ensureStaticallyLoaded();
    }
}
