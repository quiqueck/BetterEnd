package org.betterx.betterend.entity.model;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.entity.render.state.EndSlimeRenderState;
import org.betterx.betterend.registry.EndEntitiesRenders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EndSlimeEntityModel extends EntityModel<EndSlimeRenderState> {
    private final ModelPart innerCube;
    private final ModelPart rightEye;
    private final ModelPart leftEye;
    private final ModelPart mouth;
    private final ModelPart flower;
    private final ModelPart crop;

    public static LayerDefinition getShellOnlyTexturedModelData() {
        return getTexturedModelData(true);
    }

    public static LayerDefinition getCompleteTexturedModelData() {
        return getTexturedModelData(false);
    }

    private static LayerDefinition getTexturedModelData(boolean onlyShell) {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        if (onlyShell) {
            modelPartData.addOrReplaceChild(
                    PartNames.BODY,
                    CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                    PartPose.ZERO
            );
        } else {
            modelPartData.addOrReplaceChild(
                    PartNames.BODY,
                    CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                    PartPose.ZERO
            );

            modelPartData.addOrReplaceChild(
                    PartNames.RIGHT_EYE,
                    CubeListBuilder.create().texOffs(32, 0).addBox(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F),
                    PartPose.ZERO
            );

            modelPartData.addOrReplaceChild(
                    PartNames.LEFT_EYE,
                    CubeListBuilder.create().texOffs(32, 4).addBox(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F),
                    PartPose.ZERO
            );

            modelPartData.addOrReplaceChild(
                    PartNames.MOUTH,
                    CubeListBuilder.create().texOffs(32, 8).addBox(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F),
                    PartPose.ZERO
            );

            PartDefinition flowerPart = modelPartData.addOrReplaceChild(
                    "flower",
                    CubeListBuilder.create(),
                    PartPose.ZERO
            );
            PartDefinition cropPart = modelPartData.addOrReplaceChild("crop", CubeListBuilder.create(), PartPose.ZERO);

            for (int i = 0; i < 6; i++) {
                final PartDefinition parent = i < 4 ? flowerPart : cropPart;
                final float rot = MHelper.degreesToRadians(i < 4 ? (i * 45F) : ((i - 4) * 90F + 45F));

                PartDefinition petalRotPart = parent.addOrReplaceChild(
                        "petalRot_" + i,
                        CubeListBuilder.create(),
                        PartPose.offsetAndRotation(0, 0, 0, 0, rot, 0)
                );


                petalRotPart.addOrReplaceChild(
                        "petal_" + i,
                        CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 0.0F),
                        PartPose.offset(-4, 8, 0)
                );
            }
        }

        return LayerDefinition.create(modelData, 64, 32);
    }

    public EndSlimeEntityModel(EntityModelSet modelSet, boolean onlyShell) {
        this(
                modelSet.bakeLayer(onlyShell
                        ? EndEntitiesRenders.END_SLIME_SHELL_MODEL
                        : EndEntitiesRenders.END_SLIME_MODEL),
                onlyShell
        );
    }

    private EndSlimeEntityModel(ModelPart modelPart, boolean onlyShell) {
        super(buildRenderRoot(modelPart, onlyShell), RenderTypes::entityCutoutCull);

        innerCube = modelPart.getChild(PartNames.BODY);
        if (!onlyShell) {
            rightEye = modelPart.getChild(PartNames.RIGHT_EYE);
            leftEye = modelPart.getChild(PartNames.LEFT_EYE);
            mouth = modelPart.getChild(PartNames.MOUTH);
            flower = modelPart.getChild("flower");
            crop = modelPart.getChild("crop");
        } else {
            rightEye = null;
            leftEye = null;
            mouth = null;
            flower = null;
            crop = null;
        }
    }

    // Builds a synthetic root containing only the parts that should be part of the
    // regular render pass; "flower" and "crop" are rendered separately on demand.
    private static ModelPart buildRenderRoot(ModelPart modelPart, boolean onlyShell) {
        Map<String, ModelPart> children = new LinkedHashMap<>();
        children.put(PartNames.BODY, modelPart.getChild(PartNames.BODY));
        if (!onlyShell) {
            children.put(PartNames.RIGHT_EYE, modelPart.getChild(PartNames.RIGHT_EYE));
            children.put(PartNames.LEFT_EYE, modelPart.getChild(PartNames.LEFT_EYE));
            children.put(PartNames.MOUTH, modelPart.getChild(PartNames.MOUTH));
        }
        return new ModelPart(List.of(), children);
    }

    @Override
    public void setupAnim(EndSlimeRenderState state) {
    }

    public void renderFlower(PoseStack matrices, VertexConsumer vertices, int light, int overlay) {
        flower.render(matrices, vertices, light, overlay);
    }

    public void renderCrop(PoseStack matrices, VertexConsumer vertices, int light, int overlay) {
        crop.render(matrices, vertices, light, overlay);
    }
}
