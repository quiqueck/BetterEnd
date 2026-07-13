package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.EndFishEntity;
import org.betterx.betterend.entity.model.EndFishEntityModel;
import org.betterx.betterend.entity.render.state.EndFishRenderState;
import org.betterx.betterend.registry.EndEntitiesRenders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class RendererEntityEndFish extends MobRenderer<EndFishEntity, EndFishRenderState, EndFishEntityModel> {
    private static final ResourceLocation[] TEXTURE = new ResourceLocation[EndFishEntity.VARIANTS];
    private static final RenderType[] GLOW = new RenderType[EndFishEntity.VARIANTS];

    public RendererEntityEndFish(EntityRendererProvider.Context ctx) {
        super(ctx, new EndFishEntityModel(ctx.bakeLayer(EndEntitiesRenders.END_FISH_MODEL)), 0.5f);
        this.addLayer(new EyesLayer<EndFishRenderState, EndFishEntityModel>(this) {
            @Override
            public RenderType renderType() {
                return GLOW[0];
            }

            @Override
            public void render(
                    PoseStack matrices,
                    MultiBufferSource vertexConsumers,
                    int light,
                    EndFishRenderState state,
                    float yRot,
                    float xRot
            ) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(GLOW[state.variant]);
                this.getParentModel()
                    .renderToBuffer(
                            matrices,
                            vertexConsumer,
                            15728640,
                            OverlayTexture.NO_OVERLAY,
                            0xffffffff
                    );
            }
        });
    }

    @Override
    public EndFishRenderState createRenderState() {
        return new EndFishRenderState();
    }

    @Override
    public void extractRenderState(EndFishEntity entity, EndFishRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.variant = entity.getVariant();
    }

    @Override
    public ResourceLocation getTextureLocation(EndFishRenderState state) {
        return TEXTURE[state.variant];
    }

    static {
        for (int i = 0; i < EndFishEntity.VARIANTS; i++) {
            TEXTURE[i] = BetterEnd.C.mk("textures/entity/end_fish/end_fish_" + i + ".png");
            GLOW[i] = RenderType.eyes(BetterEnd.C.mk("textures/entity/end_fish/end_fish_" + i + "_glow.png"));
        }
    }
}