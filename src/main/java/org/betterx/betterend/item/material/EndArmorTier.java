package org.betterx.betterend.item.material;

import org.betterx.betterend.registry.EndTemplates;
import org.betterx.wover.complex.api.equipment.ArmorTier;
import org.betterx.wover.complex.api.equipment.ArmorTiers;

public class EndArmorTier {
    public static ArmorTier THALLASIUM = ArmorTier
            .builder("thallasium")
            .armorMaterial(EndArmorMaterial.THALLASIUM)
            .armorValuesWithOffset(ArmorTiers.IRON_ARMOR, new ArmorTier.ArmorValues(0))
            .build();

    public static ArmorTier TERMINITE = ArmorTier
            .builder("terminite")
            .armorMaterial(EndArmorMaterial.TERMINITE)
            .armorValuesWithOffset(ArmorTiers.DIAMOND_ARMOR, new ArmorTier.ArmorValues(0))
            .build();

    public static ArmorTier CRYSTALITE = ArmorTier
            .builder("crystalite")
            .armorMaterial(EndArmorMaterial.CRYSTALITE)
            .armorValuesWithOffset(ArmorTiers.NETHERITE_ARMOR, new ArmorTier.ArmorValues(1))
            .build();


    public static ArmorTier AETERNIUM = ArmorTier
            .builder("aeternium")
            .armorMaterial(EndArmorMaterial.AETERNIUM)
            .armorValuesWithOffset(
                    ArmorTiers.NETHERITE_ARMOR,
                    new ArmorTier.ArmorValues(100, () -> EndTemplates.PLATE_UPGRADE)
            )
            .build();
}
