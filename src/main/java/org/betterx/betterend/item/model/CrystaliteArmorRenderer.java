package org.betterx.betterend.item.model;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndItems;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;

@Environment(EnvType.CLIENT)
public class CrystaliteArmorRenderer implements ArmorRenderer {
    private final static ResourceLocation FIRST_LAYER = BetterEnd.C.mk(
            "textures/models/armor/crystalite_layer_1.png");
    private final static ResourceLocation SECOND_LAYER = BetterEnd.C.mk(
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
            ArmorRenderer.register(
                    INSTANCE,
                    EndItems.CRYSTALITE_HELMET,
                    EndItems.CRYSTALITE_CHESTPLATE,
                    EndItems.CRYSTALITE_ELYTRA,
                    EndItems.CRYSTALITE_LEGGINGS,
                    EndItems.CRYSTALITE_BOOTS
            );
        }
    }

    private ResourceLocation getTextureForSlot(boolean innerLayer) {
        return innerLayer ? SECOND_LAYER : FIRST_LAYER;
    }

    private HumanoidModel<HumanoidRenderState> getModelForSlot(HumanoidRenderState renderState, EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD) return HELMET_MODEL;
        if (slot == EquipmentSlot.LEGS) return LEGGINGS_MODEL;
        if (slot == EquipmentSlot.FEET) return BOOTS_MODEL;
        if (slot == EquipmentSlot.CHEST) {
            if (renderState instanceof PlayerRenderState playerRenderState
                    && playerRenderState.skin.model() == PlayerSkin.Model.SLIM) {
                return CHEST_MODEL_SLIM;
            } else {
                return CHEST_MODEL;
            }
        }
        return null;
    }

    private void setPartVisibility(HumanoidModel<HumanoidRenderState> model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }

    @Override
    public void render(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            ItemStack stack,
            HumanoidRenderState renderState,
            EquipmentSlot slot,
            int light,
            HumanoidModel<HumanoidRenderState> contextModel
    ) {
        HumanoidModel<HumanoidRenderState> model = getModelForSlot(renderState, slot);
        if (model == null) return;

        contextModel.copyPropertiesTo(model);
        if (model instanceof CopyExtraState copyExtraState) {
            copyExtraState.copyExtraState();
        }
        setPartVisibility(model, slot);

        ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, getTextureForSlot(false));
        ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, getTextureForSlot(true));
    }

    public interface CopyExtraState {
        void copyExtraState();
    }
}
