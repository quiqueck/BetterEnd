package org.betterx.betterend.mixin.client;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {

    public PlayerRendererMixin(
            EntityRendererProvider.Context context,
            PlayerModel entityModel,
            float f
    ) {
        super(context, entityModel, f);
    }
}
