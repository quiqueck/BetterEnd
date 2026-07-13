package org.betterx.betterend.item.model;

import org.betterx.betterend.registry.EndEntitiesRenders;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CrystaliteHelmetModel extends HumanoidModel<HumanoidRenderState> {
    final ModelPart myHat;

    public static LayerDefinition getTexturedModelData() {
        final float scale = 1.0f;
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        // TODO: see if we need to subclass HumanoidModel
        // Humanoid model tries to retrieve all parts in it's constructor,
        // so we need to add empty Nodes
        PartDefinition head = modelPartData.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        modelPartData.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation deformation_hat = new CubeDeformation(scale + 0.5f);
        PartDefinition hat = head.addOrReplaceChild(
                PartNames.HAT,
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, deformation_hat),
                PartPose.ZERO
        );

        return LayerDefinition.create(modelData, 64, 48);
    }

    public static CrystaliteHelmetModel createModel(EntityModelSet entityModelSet) {
        return new CrystaliteHelmetModel(entityModelSet == null
                ? getTexturedModelData().bakeRoot()
                : entityModelSet.bakeLayer(
                        EndEntitiesRenders.CRYSTALITE_HELMET));
    }


    public CrystaliteHelmetModel(ModelPart modelPart) {
        super(modelPart, RenderType::entityTranslucent);

        myHat = this.head.getChild(PartNames.HAT);
    }
}
