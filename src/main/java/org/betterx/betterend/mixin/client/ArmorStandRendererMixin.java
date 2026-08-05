package org.betterx.betterend.mixin.client;

import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.entity.decoration.ArmorStand;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin
        extends LivingEntityRenderer<ArmorStand, ArmorStandRenderState, ArmorStandArmorModel> {

    public ArmorStandRendererMixin(EntityRendererProvider.Context context, ArmorStandArmorModel entityModel, float f) {
        super(context, entityModel, f);
    }
}
