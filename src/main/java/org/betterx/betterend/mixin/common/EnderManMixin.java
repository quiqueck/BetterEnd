package org.betterx.betterend.mixin.common;

import org.betterx.betterend.effects.EndStatusEffects;
import org.betterx.betterend.registry.EndEnchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EnderManMixin {
    @Inject(method = "isBeingStaredBy", at = @At("HEAD"), cancellable = true)
    private void be_isLookingAtMe(Player player, CallbackInfoReturnable<Boolean> info) {
        final ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);

        if (player.isCreative() || player.hasEffect(EndStatusEffects.END_VEIL) || EnchantmentHelper.has(headItem, EndEnchantments.END_VEIL_STATE)) {
            info.setReturnValue(false);
        }
    }
}
