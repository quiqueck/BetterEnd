package org.betterx.betterend.mixin.common;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.interfaces.MobEffectApplier;
import org.betterx.betterend.item.CrystaliteArmor;
import org.betterx.betterend.registry.EndAttributes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 200)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }


    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot equipmentSlot);

    @Unique
    private Entity be_lastAttacker;

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void be_addLivingAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        EndAttributes.addLivingEntityAttributes(info.getReturnValue());
    }

    @Inject(method = "tickEffects", at = @At("HEAD"))
    protected void be_applyEffects(CallbackInfo info) {
        if (!level().isClientSide()) {
            LivingEntity owner = LivingEntity.class.cast(this);
            if (CrystaliteArmor.hasFullSet(owner)) {
                CrystaliteArmor.applySetEffect(owner);
            }
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor()) continue;
                ItemStack itemStack = getItemBySlot(slot);
                if (itemStack.getItem() instanceof MobEffectApplier) {
                    ((MobEffectApplier) itemStack.getItem()).applyEffect(owner);
                }
            }
        }
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void be_canBeAffected(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> info) {
        try {
            if (mobEffectInstance.getEffect() == MobEffects.BLINDNESS && getAttributes().getValue(EndAttributes.BLINDNESS_RESISTANCE) > 0.0) {
                info.setReturnValue(false);
            }
        } catch (Exception ex) {
            BetterEnd.LOGGER.warn("Blindness resistance attribute haven't been registered.");
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"))
    public void be_hurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        this.be_lastAttacker = source.getEntity();
    }

    @ModifyArg(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"), index = 0)
    private double be_increaseKnockback(double value, double x, double z) {
        if (be_lastAttacker != null && be_lastAttacker instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) be_lastAttacker;
            value += this.be_getKnockback(attacker.getMainHandItem());
        }
        return value;
    }

    @Unique
    private double be_getKnockback(ItemStack tool) {
        if (tool == null) return 0.0;
        double[] res = {0.0};
        tool.forEachModifier(EquipmentSlot.MAINHAND, (holder, attributeModifier) -> {
            if (holder.is(Attributes.ATTACK_KNOCKBACK)) {
                res[0] = attributeModifier.amount();
            }

        });
        return res[0];
    }
}
