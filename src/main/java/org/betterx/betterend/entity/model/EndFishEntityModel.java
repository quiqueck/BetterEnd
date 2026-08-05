package org.betterx.betterend.entity.model;

import org.betterx.betterend.entity.render.state.EndFishRenderState;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class EndFishEntityModel extends EntityModel<EndFishRenderState> {
    private final ModelPart model;
    private final ModelPart fin_top;
    private final ModelPart fin_bottom;
    private final ModelPart flipper;
    private final ModelPart fin_right;
    private final ModelPart fin_left;

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition bodyPart = modelPartData.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 4.0F, 8.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F)
        );

        bodyPart.addOrReplaceChild(
                PartNames.TOP_FIN,
                CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, -8.0F, 0.0F, 0.0F, 8.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, -4.0F, -0.6981F, 0.0F, 0.0F)
        );

        bodyPart.addOrReplaceChild(
                PartNames.BOTTOM_FIN,
                CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, -4.0F, 0.6981F, 0.0F, 0.0F)
        );

        bodyPart.addOrReplaceChild(
                PartNames.TAIL_FIN,
                CubeListBuilder.create().texOffs(0, 15).addBox(0.0F, -5.0F, 0.0F, 0.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.7854F, 0.0F, 0.0F)
        );

        bodyPart.addOrReplaceChild(
                PartNames.RIGHT_FIN,
                CubeListBuilder.create().texOffs(0, 25).addBox(-3.7071F, 0.7071F, -1.5F, 3.0F, 0.0F, 3.0F),
                PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, 1.5708F, 0.7854F, 0.0F)
        );

        bodyPart.addOrReplaceChild(
                PartNames.LEFT_FIN,
                CubeListBuilder.create().mirror().texOffs(0, 25).addBox(0.7071F, 0.7071F, -1.5F, 3.0F, 0.0F, 3.0F),
                PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, 1.5708F, -0.7854F, 0.0F)
        );

        return LayerDefinition.create(modelData, 32, 32);
    }

    public EndFishEntityModel(ModelPart modelPart) {
        super(modelPart, RenderTypes::entityCutoutCull);

        model = modelPart.getChild(PartNames.BODY);
        fin_top = model.getChild(PartNames.TOP_FIN);
        fin_bottom = model.getChild(PartNames.BOTTOM_FIN);
        flipper = model.getChild(PartNames.TAIL_FIN);
        fin_right = model.getChild(PartNames.RIGHT_FIN);
        fin_left = model.getChild(PartNames.LEFT_FIN);
    }

    @Override
    public void setupAnim(EndFishRenderState state) {
        float s1 = (float) Math.sin(state.ageInTicks * 0.1);
        float s2 = (float) Math.sin(state.ageInTicks * 0.05);
        flipper.yRot = s1 * 0.3F;
        fin_top.xRot = s2 * 0.02F - 0.6981F;
        fin_bottom.xRot = 0.6981F - s2 * 0.02F;
        fin_left.yRot = s1 * 0.3F - 0.7854F;
        fin_right.yRot = 0.7854F - s1 * 0.3F;
    }
}
