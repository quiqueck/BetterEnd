package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.EndSlimeEntity;
import org.betterx.betterend.entity.model.EndSlimeEntityModel;
import org.betterx.betterend.entity.render.state.EndSlimeRenderState;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RendererEntityEndSlime extends MobRenderer<EndSlimeEntity, EndSlimeRenderState, EndSlimeEntityModel> {
    private static final Identifier[] TEXTURE = new Identifier[4];
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
            public void submit(
                    PoseStack poseStack,
                    SubmitNodeCollector submitNodeCollector,
                    int light,
                    EndSlimeRenderState state,
                    float yRot,
                    float xRot
            ) {
                RenderType glowType = GLOW[state.slimeType];
                // submitCustomGeometry (raw deferred buffer), NOT submitModel: a submitModel call
                // from a feature layer here routes the geometry to the outline pass (renders as a
                // white, depth-less silhouette on top of everything). The raw-buffer path renders
                // normally, same as the flower/crop below.
                submitNodeCollector.submitCustomGeometry(poseStack, glowType, (pose, buffer) -> {
                    PoseStack local = new PoseStack();
                    local.mulPose(pose.pose());
                    this.getParentModel().renderToBuffer(local, buffer, 15728640, OverlayTexture.NO_OVERLAY, 0xffffffff);
                });
                if (state.isLake) {
                    submitNodeCollector.submitCustomGeometry(poseStack, glowType, (pose, buffer) -> {
                        PoseStack local = new PoseStack();
                        local.mulPose(pose.pose());
                        this.getParentModel().renderFlower(local, buffer, 15728640, OverlayTexture.NO_OVERLAY);
                    });
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
    public Identifier getTextureLocation(EndSlimeRenderState state) {
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

        public void submit(
                PoseStack poseStack,
                SubmitNodeCollector submitNodeCollector,
                int light,
                EndSlimeRenderState state,
                float yRot,
                float xRot
        ) {
            if (!state.isInvisible) {
                int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
                if (state.isLake) {
                    // entityCutoutCull: the petals are flat 2-sided quads; 26.1's entityCutout
                    // no longer culls, so their coplanar faces z-fight without this.
                    RenderType cutout = RenderTypes.entityCutoutCull(this.getTextureLocation(state));
                    submitNodeCollector.submitCustomGeometry(poseStack, cutout, (pose, buffer) -> {
                        PoseStack local = new PoseStack();
                        local.mulPose(pose.pose());
                        this.getParentModel().renderFlower(local, buffer, light, overlay);
                    });
                } else if (state.isAmber || state.isChorus) {
                    RenderType cutout = RenderTypes.entityCutoutCull(this.getTextureLocation(state));
                    submitNodeCollector.submitCustomGeometry(poseStack, cutout, (pose, buffer) -> {
                        PoseStack local = new PoseStack();
                        local.mulPose(pose.pose());
                        this.getParentModel().renderCrop(local, buffer, light, overlay);
                    });
                }

                EndSlimeEntityModel model = state.slimeType == 1 ? modelLake : modelOrdinal;
                model.setupAnim(state);
                // submitCustomGeometry (raw deferred buffer), NOT submitModel: submitModel from a
                // feature layer routes the geometry to the outline pass (white, depth-less
                // silhouette drawn on top of everything). The raw-buffer path renders the
                // translucent shell correctly, same as the flower/crop above.
                RenderType translucent = RenderTypes.entityTranslucent(this.getTextureLocation(state));
                // Only the chorus variant (slimeType 0) reads as too solid (dark texture, no
                // emissive core), so soften its shell to ~63% vertex-color alpha (stacks on top of
                // the texture's own ~50%). Other variants keep full alpha. Tune the AA byte.
                int shellColor = state.slimeType == 0 ? 0xA0ffffff : 0xffffffff;
                submitNodeCollector.submitCustomGeometry(poseStack, translucent, (pose, buffer) -> {
                    PoseStack local = new PoseStack();
                    local.mulPose(pose.pose());
                    model.renderToBuffer(local, buffer, light, overlay, shellColor);
                });
            }
        }

        private Identifier getTextureLocation(EndSlimeRenderState state) {
            return RendererEntityEndSlime.this.getTextureLocation(state);
        }
    }

    static {
        TEXTURE[0] = BetterEnd.C.mk("textures/entity/end_slime/end_slime.png");
        TEXTURE[1] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_mossy.png");
        TEXTURE[2] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_lake.png");
        TEXTURE[3] = BetterEnd.C.mk("textures/entity/end_slime/end_slime_amber.png");
        GLOW[0] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_glow.png"));
        GLOW[1] = GLOW[0];
        GLOW[2] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_lake_glow.png"));
        GLOW[3] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/end_slime/end_slime_amber_glow.png"));
    }
}