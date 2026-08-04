package org.betterx.betterend.testmod.mixin;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Test-only accessor for {@link EnderMan#isBeingStaredBy(Player)}, which is private.
 * <p>
 * That method is the single decision point BetterEnd's {@code EnderManMixin} overrides, and the only
 * public way into it is {@code EndermanLookForPlayerGoal#canUse} on a private inner class. Rather than
 * drive the whole AI goal (which would also need line of sight, a real look vector and a tick or two),
 * the tests call the predicate directly through this invoker.
 */
@Mixin(EnderMan.class)
public interface EnderManInvoker {
    @Invoker("isBeingStaredBy")
    boolean betterend_testmod$isBeingStaredBy(Player player);
}
