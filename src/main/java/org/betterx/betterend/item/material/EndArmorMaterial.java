package org.betterx.betterend.item.material;

import org.betterx.betterend.BetterEnd;
import org.betterx.wover.item.api.armor.CustomArmorMaterial;

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
            .buildAndRegister();
}
