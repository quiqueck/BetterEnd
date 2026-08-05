package org.betterx.betterend.mixin.client;

import org.betterx.betterend.client.effects.EternalHint;
import org.betterx.betterend.client.effects.InfusionHint;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    /**
     * Wobbles the view while an {@link InfusionHint} or {@link EternalHint} is running.
     * <p>
     * Rides on {@code bobHurt} rather than patching {@code renderLevel} directly: the pose stack it
     * fills is multiplied into the level projection matrix right afterwards, so contributing to it
     * needs nothing but the method's own arguments - no captured locals, and it is applied whether
     * or not the player has view bobbing enabled.
     */
    @Inject(method = "bobHurt", at = @At("RETURN"))
    private void be_wobbleForInfusionHint(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo info) {
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float[] wobble = InfusionHint.wobble(partialTick);
        if (wobble == null) wobble = EternalHint.wobble(partialTick);
        if (wobble == null) return;

        poseStack.mulPose(Axis.ZP.rotationDegrees(wobble[0]));
        poseStack.mulPose(Axis.XP.rotationDegrees(wobble[1]));
    }
}
