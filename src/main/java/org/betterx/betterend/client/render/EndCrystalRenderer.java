package org.betterx.betterend.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Constants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import org.joml.Quaternionf;

public class EndCrystalRenderer {
    private static final Identifier CRYSTAL_TEXTURE = Identifier.withDefaultNamespace(
            "textures/entity/end_crystal/end_crystal.png");
    private static final RenderType END_CRYSTAL;
    private static final ModelPart CORE;
    private static final ModelPart FRAME;
    private static final int AGE_CYCLE = 240;
    private static final float SINE_45_DEGREES;
    private static final Quaternionf ROTATOR;

    public static void render(
            int age,
            int maxAge,
            float tickDelta,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            int light
    ) {
        float k = (float) AGE_CYCLE / maxAge;
        float rotation = (age * k + tickDelta) * 3.0F;
        submitNodeCollector.submitCustomGeometry(matrices, END_CRYSTAL, (pose, buffer) -> {
            PoseStack local = new PoseStack();
            local.mulPose(pose.pose());
            local.scale(0.8F, 0.8F, 0.8F);
            local.translate(0.0D, -0.5D, 0.0D);
            local.mulPose(Axis.YP.rotationDegrees(rotation));
            local.translate(0.0D, 0.8F, 0.0D);
            local.mulPose(ROTATOR);
            FRAME.render(local, buffer, light, OverlayTexture.NO_OVERLAY);
            local.scale(0.875F, 0.875F, 0.875F);
            local.mulPose(ROTATOR);
            local.mulPose(Axis.YP.rotationDegrees(rotation));
            FRAME.render(local, buffer, light, OverlayTexture.NO_OVERLAY);
            local.scale(0.875F, 0.875F, 0.875F);
            local.mulPose(ROTATOR);
            local.mulPose(Axis.YP.rotationDegrees(rotation));
            CORE.render(local, buffer, light, OverlayTexture.NO_OVERLAY);
        });
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(
                "FRAME",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                PartPose.ZERO
        );

        modelPartData.addOrReplaceChild(
                "CORE",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                PartPose.ZERO
        );

        return LayerDefinition.create(modelData, 64, 32);
    }

    static {
        END_CRYSTAL = RenderTypes.entityCutout(CRYSTAL_TEXTURE);
        SINE_45_DEGREES = (float) Math.sin(0.7853981633974483D);

        ModelPart root = getTexturedModelData().bakeRoot();
        FRAME = root.getChild("FRAME");
        CORE = root.getChild("CORE");

        ROTATOR = new Quaternionf().setAngleAxis(
                60.0f * Constants.DEG_TO_RAD,
                SINE_45_DEGREES,
                0.0F,
                SINE_45_DEGREES
        );
    }
}
