package org.betterx.betterend.item.model;

import org.betterx.betterend.registry.EndEntitiesRenders;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class CrystaliteBootsModel extends HumanoidModel<HumanoidRenderState> implements CrystaliteArmorRenderer.CopyExtraState {

    public ModelPart leftBoot;
    public ModelPart rightBoot;

    public static LayerDefinition getTexturedModelData() {
        final float scale = 1.0f;
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        // Humanoid model tries to retrieve all parts in it's constructor,
        // so we need to add empty Nodes
        PartDefinition head = modelPartData.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        head.addOrReplaceChild(PartNames.HAT, CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation deformation = new CubeDeformation(scale + 0.25f);
        modelPartData.addOrReplaceChild(
                "leftBoot",
                CubeListBuilder.create().texOffs(0, 32).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                PartPose.offset(1.9f, 12.0f, 0.0f)
        );

        modelPartData.addOrReplaceChild(
                "rightBoot",
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, deformation),
                PartPose.offset(-1.9f, 12.0f, 0.0f)
        );

        return LayerDefinition.create(modelData, 64, 48);
    }

    public static CrystaliteBootsModel createModel(EntityModelSet entityModelSet) {
        return new CrystaliteBootsModel(entityModelSet == null
                ? getTexturedModelData().bakeRoot()
                : entityModelSet.bakeLayer(
                        EndEntitiesRenders.CRYSTALITE_BOOTS));
    }

    public CrystaliteBootsModel(ModelPart modelPart) {
        super(modelPart, RenderTypes::entityTranslucent);

        leftBoot = modelPart.getChild("leftBoot");
        rightBoot = modelPart.getChild("rightBoot");
    }

    @Override
    public void copyExtraState() {
        this.leftBoot.loadPose(leftLeg.storePose());
        this.rightBoot.loadPose(rightLeg.storePose());
    }
}
