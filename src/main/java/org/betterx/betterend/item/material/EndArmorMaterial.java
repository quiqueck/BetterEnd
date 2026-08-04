package org.betterx.betterend.item.material;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.item.api.armor.CustomArmorMaterial;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;

public class EndArmorMaterial {
    public static final Holder<ArmorMaterial> THALLASIUM = CustomArmorMaterial
            .start(BetterEnd.C.mk("thallasium"))
            .defense(1, 4, 5, 2, 12)
            .durability(16)
            .enchantmentValue(17)
            .equipSound(SoundEvents.ARMOR_EQUIP_IRON)
            .toughness(0.0f)
            .knockbackResistance(0.0f)
            .createRepairIngredient()
            .humanoidEquipmentAsset()
            .buildAndRegister();

    public static final Holder<ArmorMaterial> TERMINITE = CustomArmorMaterial
            .start(BetterEnd.C.mk("terminite"))
            .defense(3, 6, 7, 3, 14)
            .durability(25)
            .enchantmentValue(26)
            .equipSound(SoundEvents.ARMOR_EQUIP_IRON)
            .toughness(1.0f)
            .knockbackResistance(0.05f)
            .createRepairIngredient()
            .humanoidEquipmentAsset()
            .buildAndRegister();

    public static final Holder<ArmorMaterial> AETERNIUM = CustomArmorMaterial
            .start(BetterEnd.C.mk("aeternium"))
            .defense(4, 7, 9, 4, 18)
            .durability(42)
            .enchantmentValue(40)
            .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .toughness(3.5f)
            .knockbackResistance(0.2f)
            .createRepairIngredient()
            .humanoidEquipmentAsset()
            .buildAndRegister();

    public static final Holder<ArmorMaterial> CRYSTALITE = CustomArmorMaterial
            .start(BetterEnd.C.mk("crystalite"))
            .defense(3, 6, 8, 3, 24)
            .durability(33)
            .enchantmentValue(30)
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .toughness(1.2f)
            .knockbackResistance(0.1f)
            .createRepairIngredient()
            .humanoidEquipmentAsset()
            .buildAndRegister();

    // Same stats as AETERNIUM/CRYSTALITE, but a distinct assetId ("elytra_armored"/"elytra_crystalite")
    // so the elytra items can render their own equipment asset (armor shape + wings, or just wings)
    // instead of sharing the plain chestplate's asset - which would make regular chestplates sprout
    // wings too, since WingsLayer renders whatever assetId the CHEST-slot item carries.
    public static final Holder<ArmorMaterial> AETERNIUM_ELYTRA = CustomArmorMaterial
            .start(BetterEnd.C.mk("elytra_armored"))
            .defense(4, 7, 9, 4, 18)
            .durability(42)
            .enchantmentValue(40)
            .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .toughness(3.5f)
            .knockbackResistance(0.2f)
            .repairIngredient(AETERNIUM.value().repairIngredient())
            // Reuses AETERNIUM's own chestplate texture for the humanoid layers, and adds this
            // material's own texture for the wings layer.
            .equipmentAsset(spec -> spec
                    .addHumanoidLayers(BetterEnd.C.mk("aeternium"))
                    .addWingsLayer(BetterEnd.C.mk("elytra_armored")))
            .buildAndRegister();

    public static final Holder<ArmorMaterial> CRYSTALITE_ELYTRA = CustomArmorMaterial
            .start(BetterEnd.C.mk("elytra_crystalite"))
            .defense(3, 6, 8, 3, 24)
            .durability(33)
            .enchantmentValue(30)
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .toughness(1.2f)
            .knockbackResistance(0.1f)
            .repairIngredient(CRYSTALITE.value().repairIngredient())
            .wingsEquipmentAsset(BetterEnd.C.mk("elytra_crystalite"))
            .buildAndRegister();
}
