package org.betterx.betterend.entity.render;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.CubozoaEntity;
import org.betterx.betterend.entity.model.CubozoaEntityModel;
import org.betterx.betterend.entity.render.state.CubozoaRenderState;
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

public class RendererEntityCubozoa extends MobRenderer<CubozoaEntity, CubozoaRenderState, CubozoaEntityModel> {
    private static final Identifier[] TEXTURE = new Identifier[2];
    private static final RenderType[] GLOW = new RenderType[2];

    public RendererEntityCubozoa(EntityRendererProvider.Context ctx) {
        super(ctx, new CubozoaEntityModel(ctx.bakeLayer(EndEntitiesRenders.CUBOZOA_MODEL)), 0.5f);
        this.addLayer(new EyesLayer<CubozoaRenderState, CubozoaEntityModel>(this) {
            @Override
            public RenderType renderType() {
                return GLOW[0];
            }

            @Override
            public void submit(
                    PoseStack poseStack,
                    SubmitNodeCollector submitNodeCollector,
                    int light,
                    CubozoaRenderState state,
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
    public CubozoaRenderState createRenderState() {
        return new CubozoaRenderState();
    }

    @Override
    public void extractRenderState(CubozoaEntity entity, CubozoaRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.variant = entity.getVariant();
    }

    @Override
    public Identifier getTextureLocation(CubozoaRenderState state) {
        return TEXTURE[state.variant];
    }

    static {
        TEXTURE[0] = BetterEnd.C.mk("textures/entity/cubozoa/cubozoa.png");
        TEXTURE[1] = BetterEnd.C.mk("textures/entity/cubozoa/cubozoa_sulphur.png");

        GLOW[0] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/cubozoa/cubozoa_glow.png"));
        GLOW[1] = RenderTypes.eyes(BetterEnd.C.mk("textures/entity/cubozoa/cubozoa_sulphur_glow.png"));
    }
}