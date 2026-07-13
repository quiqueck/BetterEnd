package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.ShadowWalkerEntity;
import org.betterx.betterend.entity.render.state.ShadowWalkerRenderState;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RendererEntityShadowWalker
        extends HumanoidMobRenderer<ShadowWalkerEntity, ShadowWalkerRenderState, HumanoidModel<ShadowWalkerRenderState>> {
    private static final ResourceLocation TEXTURE = BetterEnd.C.mk("textures/entity/shadow_walker.png");

    public RendererEntityShadowWalker(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ShadowWalkerRenderState createRenderState() {
        return new ShadowWalkerRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(ShadowWalkerRenderState state) {
        return TEXTURE;
    }
}
