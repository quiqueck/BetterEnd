package org.betterx.betterend.mixin.client;

import org.betterx.betterend.item.ArmoredElytra;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    public void be_checkCustomElytra(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int i,
            AvatarRenderState avatarRenderState,
            float f,
            float g,
            CallbackInfo info
    ) {
        ItemStack itemStack = avatarRenderState.chestEquipment;
        if (itemStack.getItem() instanceof ArmoredElytra) {
            info.cancel();
        }
    }
}
