package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.SilkMothEntity;
import org.betterx.betterend.entity.model.SilkMothEntityModel;
import org.betterx.betterend.entity.render.state.SilkMothRenderState;
import org.betterx.betterend.registry.EndEntitiesRenders;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SilkMothEntityRenderer extends MobRenderer<SilkMothEntity, SilkMothRenderState, SilkMothEntityModel> {
    private static final ResourceLocation TEXTURE = BetterEnd.C.mk("textures/entity/silk_moth.png");

    public SilkMothEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SilkMothEntityModel(ctx.bakeLayer(EndEntitiesRenders.SILK_MOTH_MODEL)), 0.5f);
    }

    @Override
    public SilkMothRenderState createRenderState() {
        return new SilkMothRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(SilkMothRenderState state) {
        return TEXTURE;
    }
}