package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.DragonflyEntity;
import org.betterx.betterend.entity.model.DragonflyEntityModel;
import org.betterx.betterend.entity.render.state.DragonflyRenderState;
import org.betterx.betterend.registry.EndEntitiesRenders;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class RendererEntityDragonfly extends MobRenderer<DragonflyEntity, DragonflyRenderState, DragonflyEntityModel> {
    private static final ResourceLocation TEXTURE = BetterEnd.C.mk("textures/entity/dragonfly.png");
    private static final RenderType GLOW = RenderType.eyes(BetterEnd.C.mk("textures/entity/dragonfly_glow.png"));

    public RendererEntityDragonfly(EntityRendererProvider.Context ctx) {
        super(ctx, new DragonflyEntityModel(ctx.bakeLayer(EndEntitiesRenders.DRAGONFLY_MODEL)), 0.5f);
        this.addLayer(new EyesLayer<DragonflyRenderState, DragonflyEntityModel>(this) {
            @Override
            public RenderType renderType() {
                return GLOW;
            }
        });
    }

    @Override
    public DragonflyRenderState createRenderState() {
        return new DragonflyRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(DragonflyRenderState state) {
        return TEXTURE;
    }
}