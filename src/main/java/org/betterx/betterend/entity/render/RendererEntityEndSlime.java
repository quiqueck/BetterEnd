package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.EndSlimeEntity;
import org.betterx.betterend.entity.model.EndSlimeEntityModel;
import org.betterx.betterend.entity.render.state.EndSlimeRenderState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RendererEntityEndSlime extends MobRenderer<EndSlimeEntity, EndSlimeRenderState, EndSlimeEntityModel> {
    private static final ResourceLocation[] TEXTURE = new ResourceLocation[4];
    private static final RenderType[] GLOW = new RenderType[4];

    public RendererEntityEndSlime(EntityRendererProvider.Context ctx) {
        super(ctx, new EndSlimeEntityModel(ctx.getModelSet(), false), 0.25f);
        this.addLayer(new OverlayFeatureRenderer(this, ctx));
        this.addLayer(new EyesLayer<EndSlimeRenderState, EndSlimeEntityModel>(this) {
            @Override
            public RenderType renderType() {
                return GLOW[0];
            }

            @Override
            public void render(
                    PoseStack matrices,
                    MultiBufferSource vertexConsumers,
                    int light,
                    EndSlimeRenderState state,
                    float yRot,
                    float xRot
            ) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(GLOW[state.slimeType]);
                this.getParentModel()
                    .renderToBuffer(
                            matrices,
                            vertexConsumer,
                            15728640,
                            OverlayTexture.NO_OVERLAY,
                            0xffffffff
                    );
                if (state.isLake) {
                    this.getParentModel().renderFlower(matrices, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY);
                }
            }
        });
    }

    @Override
    public EndSlimeRenderState createRenderState() {
        return new EndSlimeRenderState();
    }

    @Override
    public void extractRenderState(EndSlimeEntity entity, EndSlimeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.slimeType = entity.getSlimeType();
        state.isLake = entity.isLake();
        state.isAmber = entity.isAmber();
        state.isChorus = entity.isChorus();
        state.squish = Mth.lerp(partialTick, entity.oSquish, entity.squish);
        state.size = entity.getSize();
    }

    @Override
    public ResourceLocation getTextureLocation(EndSlimeRenderState state) {
        return TEXTURE[state.slimeType];
    }

    @Override
    protected float getShadowRadius(EndSlimeRenderState state) {
        return 0.25F * state.size;
    }

    @Override
    protected void scale(EndSlimeRenderState state, PoseStack matrixStack) {
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0D, 0.0010000000474974513D, 0.0D);
        float h = state.size;
        float i = state.squish / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        matrixStack.scale(j * h, 1.0F / j * h, j * h);
    }

    private final class OverlayFeatureRenderer extends RenderLayer<EndSlimeRenderState, EndSlimeEntityModel> {
        private final EndSlimeEntityModel modelOrdinal;
        private final EndSlimeEntityModel modelLake;

        public OverlayFeatureRenderer(
                RenderLayerParent<EndSlimeRenderState, EndSlimeEntityModel> featureRendererContext,
                EntityRendererProvider.Context ctx
        ) {
            super(featureRendererContext);
            modelOrdinal = new EndSlimeEntityModel(ctx.getModelSet(), true);
            modelLake = new EndSlimeEntityModel(ctx.getModelSet(), true);
        }

        public void render(
                PoseStack matrixStack,
                MultiBufferSource vertexConsumerProvider,
                int i,
                EndSlimeRenderState state,
                float yRot,
                float xRot
        ) {
            if (!state.isInvisible) {
                if (state.isLake) {
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityCutout(this.getTextureLocation(
                            state)));
                    this.getParentModel()
                        .renderFlower(
                                matrixStack,
                                vertexConsumer,
                                i,
                                LivingEntityRenderer.getOverlayCoords(state, 0.0F)
                        );
                } else if (state.isAmber || state.isChorus) {
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityCutout(this.getTextureLocation(
                            state)));
                    this.getParentModel()
                        .renderCrop(
                                matrixStack,
                                vertexConsumer,
                                i,
                                LivingEntityRenderer.getOverlayCoords(state, 0.0F)
                        );
                }

                EndSlimeEntityModel model = state.slimeType == 1 ? modelLake : modelOrdinal;
                model.setupAnim(state);
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(
                        state)));
                model.renderToBuffer(
                        matrixStack,
                        vertexConsumer,
                        i,
                        LivingEntityRenderer.getOverlayCoords(state, 0.0F),
                        0xffffffff
                );
            }
        }

        private ResourceLocation getTextureLocation(EndSlimeRenderState state) {
            return RendererEntityEndSlime.this.getTextureLocation(state);
        }
    }

    static {
        TEXTURE[0] = BetterEnd.C.mk("textures/entity/end_slime/end_slime.png");
        TEXTURE[1] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_mossy.png");
        TEXTURE[2] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_lake.png");
        TEXTURE[3] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_amber.png");
        GLOW[0] = RenderType.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_glow.png"));
        GLOW[1] = GLOW[0];
        GLOW[2] = RenderType.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_lake_glow.png"));
        GLOW[3] = RenderType.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_amber_glow.png"));
    }
}