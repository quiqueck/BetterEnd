package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.EndFishEntity;
import org.betterx.betterend.entity.model.EndFishEntityModel;
import org.betterx.betterend.entity.render.state.EndFishRenderState;
import org.betterx.betterend.registry.EndEntitiesRenders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RendererEntityEndFish extends MobRenderer<EndFishEntity, EndFishRenderState, EndFishEntityModel> {
    private static final Identifier[] TEXTURE = new Identifier[EndFishEntity.VARIANTS];
    private static final RenderType[] GLOW = new RenderType[EndFishEntity.VARIANTS];

    public RendererEntityEndFish(EntityRendererProvider.Context ctx) {
        super(ctx, new EndFishEntityModel(ctx.bakeLayer(EndEntitiesRenders.END_FISH_MODEL)), 0.5f);
        this.addLayer(new EyesLayer<EndFishRenderState, EndFishEntityModel>(this) {
            @Override
            public RenderType renderType() {
                return GLOW[0];
            }

            @Override
            public void submit(
                    PoseStack poseStack,
                    SubmitNodeCollector submitNodeCollector,
                    int light,
                    EndFishRenderState state,
                    float yRot,
                    float xRot
            ) {
                submitNodeCollector.submitModel(
                        this.getParentModel(),
                        state,
                        poseStack,
                        GLOW[state.variant],
                        15728640,
                        OverlayTexture.NO_OVERLAY,
                        0xffffffff,
                        null
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
    public Identifier getTextureLocation(EndFishRenderState state) {
        return TEXTURE[state.variant];
    }

    static {
        for (int i = 0; i < EndFishEntity.VARIANTS; i++) {
            TEXTURE[i] = BetterEnd.C.mk("textures/entity/end_fish/end_fish_" + i + ".png");
            GLOW[i] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/end_fish/end_fish_" + i + "_glow.png"));
        }
    }
}