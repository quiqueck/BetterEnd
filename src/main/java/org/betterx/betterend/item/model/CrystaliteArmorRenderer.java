package org.betterx.betterend.item.model;


import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.BetterEnd;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.FabricModel;

@Environment(EnvType.CLIENT)
public class CrystaliteArmorRenderer implements ArmorRenderer {
    private final static Identifier FIRST_LAYER = BetterEnd.C.mk(
            "textures/models/armor/crystalite_layer_1.png");
    private final static Identifier SECOND_LAYER = BetterEnd.C.mk(
            "textures/models/armor/crystalite_layer_2.png");
    private final static CrystaliteHelmetModel HELMET_MODEL = CrystaliteHelmetModel.createModel(null);
    private final static CrystaliteChestplateModel CHEST_MODEL = CrystaliteChestplateModel.createRegularModel(null);
    private final static CrystaliteChestplateModel CHEST_MODEL_SLIM = CrystaliteChestplateModel.createThinModel(null);
    private final static CrystaliteLeggingsModel LEGGINGS_MODEL = CrystaliteLeggingsModel.createModel(null);
    private final static CrystaliteBootsModel BOOTS_MODEL = CrystaliteBootsModel.createModel(null);
    private static CrystaliteArmorRenderer INSTANCE = null;

    public static void register() {
        if (INSTANCE == null) {
            INSTANCE = new CrystaliteArmorRenderer();
            // CRYSTALITE_ELYTRA deliberately excluded: it now has its own equipment asset
            // (assets/betterend/equipment/elytra_crystalite.json, a "wings" layer only) and
            // renders through vanilla's WingsLayer instead of this custom chestplate-shaped model.
            ArmorRenderer.register(
                    INSTANCE,
                    EndEquipmentItems.CRYSTALITE_HELMET,
                    EndEquipmentItems.CRYSTALITE_CHESTPLATE,
                    EndEquipmentItems.CRYSTALITE_LEGGINGS,
                    EndEquipmentItems.CRYSTALITE_BOOTS
            );
        }
    }

    private Identifier getTextureForSlot(boolean innerLayer) {
        return innerLayer ? SECOND_LAYER : FIRST_LAYER;
    }

    private HumanoidModel<HumanoidRenderState> getModelForSlot(HumanoidRenderState renderState, EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD) return HELMET_MODEL;
        if (slot == EquipmentSlot.LEGS) return LEGGINGS_MODEL;
        if (slot == EquipmentSlot.FEET) return BOOTS_MODEL;
        if (slot == EquipmentSlot.CHEST) {
            if (renderState instanceof AvatarRenderState playerRenderState
                    && playerRenderState.skin.model() == PlayerModelType.SLIM) {
                return CHEST_MODEL_SLIM;
            } else {
                return CHEST_MODEL;
            }
        }
        return null;
    }

    private void setPartVisibility(HumanoidModel<HumanoidRenderState> model, EquipmentSlot slot) {
        // Model.allParts() == root.getAllParts(), which includes the root ModelPart itself
        // (getAllParts() does `allParts.add(this)` before recursing into children) - so this
        // blanket sweep also hides the root container, and it must be turned back on explicitly:
        // ModelPart.render() gates its ENTIRE recursion on `this.visible`, so an invisible root
        // means none of its children ever draw regardless of their own flag.
        model.allParts().forEach(part -> part.visible = false);
        model.root().visible = true;
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                // leftShoulder/rightShoulder carry the chestplate's actual arm-covering geometry
                // (right_arm/left_arm are empty placeholder nodes, see the class comment).
                if (model instanceof CrystaliteChestplateModel chest) {
                    chest.leftShoulder.visible = true;
                    chest.rightShoulder.visible = true;
                }
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                // Same story as the shoulders: leftBoot/rightBoot hold the actual geometry.
                if (model instanceof CrystaliteBootsModel boots) {
                    boots.leftBoot.visible = true;
                    boots.rightBoot.visible = true;
                }
            }
            default -> {
            }
        }
    }

    @Override
    public void render(
            PoseStack matrices,
            SubmitNodeCollector collector,
            ItemStack stack,
            HumanoidRenderState renderState,
            EquipmentSlot slot,
            int light,
            HumanoidModel<HumanoidRenderState> contextModel
    ) {
        HumanoidModel<HumanoidRenderState> model = getModelForSlot(renderState, slot);
        if (model == null) return;

        // Copy the wearer's current pose onto the (shared, static) crystal model.
        model.resetPose();
        contextModel.setupAnim(renderState);
        ((FabricModel<HumanoidRenderState>) model).copyTransforms(contextModel);

        // Must run AFTER copyTransforms: it reads the just-copied arm/leg pose to place the
        // chestplate's shoulder pads / boots' cuffs, which have no same-named source part and
        // are therefore never touched by copyTransforms itself.
        if (model instanceof CopyExtraState copyExtraState) {
            copyExtraState.copyExtraState();
        }
        setPartVisibility(model, slot);

        // The crystal models are shared static instances (one per armor type, reused by every
        // wearer). submitCustomGeometry only defers the actual draw call, not the pose copy above,
        // so if two entities wear the same piece in one frame, whichever renders last overwrites
        // this shared model's pose before either deferred callback runs - both would then draw
        // with the same (wrong, frozen) pose. Snapshot every part's transform+visibility now and
        // reapply it right before drawing, so each submission is immune to later ones.
        Map<ModelPart, PartSnapshot> snapshot = new HashMap<>();
        for (ModelPart part : model.allParts()) {
            snapshot.put(part, PartSnapshot.of(part));
        }

        submitLayer(matrices, collector, model, snapshot, light, getTextureForSlot(false));
        submitLayer(matrices, collector, model, snapshot, light, getTextureForSlot(true));
    }

    private static void submitLayer(
            PoseStack matrices,
            SubmitNodeCollector collector,
            HumanoidModel<HumanoidRenderState> model,
            Map<ModelPart, PartSnapshot> snapshot,
            int light,
            Identifier texture
    ) {
        collector.submitCustomGeometry(matrices, RenderTypes.entityTranslucent(texture), (pose, buffer) -> {
            for (Map.Entry<ModelPart, PartSnapshot> entry : snapshot.entrySet()) {
                entry.getValue().applyTo(entry.getKey());
            }
            PoseStack local = new PoseStack();
            local.mulPose(pose.pose());
            model.renderToBuffer(local, buffer, light, OverlayTexture.NO_OVERLAY, -1);
        });
    }

    private record PartSnapshot(
            float x, float y, float z,
            float xRot, float yRot, float zRot,
            float xScale, float yScale, float zScale,
            boolean visible
    ) {
        static PartSnapshot of(ModelPart part) {
            return new PartSnapshot(
                    part.x, part.y, part.z,
                    part.xRot, part.yRot, part.zRot,
                    part.xScale, part.yScale, part.zScale,
                    part.visible
            );
        }

        void applyTo(ModelPart part) {
            part.x = x;
            part.y = y;
            part.z = z;
            part.xRot = xRot;
            part.yRot = yRot;
            part.zRot = zRot;
            part.xScale = xScale;
            part.yScale = yScale;
            part.zScale = zScale;
            part.visible = visible;
        }
    }

    public interface CopyExtraState {
        void copyExtraState();
    }
}
