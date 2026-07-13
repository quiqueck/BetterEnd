package org.betterx.betterend.mixin.client;

import org.betterx.betterend.item.ArmoredElytra;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    public void be_checkCustomElytra(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            PlayerRenderState playerRenderState,
            float f,
            float g,
            CallbackInfo info
    ) {
        ItemStack itemStack = playerRenderState.chestEquipment;
        if (itemStack.getItem() instanceof ArmoredElytra) {
            info.cancel();
        }
    }
}
