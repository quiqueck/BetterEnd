package org.betterx.betterend.item.material;

import org.betterx.betterend.registry.EndTemplates;
import de.ambertation.wover.complex.api.equipment.ArmorTier;
import de.ambertation.wover.complex.api.equipment.ArmorTiers;

public class EndArmorTier {
    public static ArmorTier THALLASIUM = ArmorTier
            .builder("thallasium")
            .armorMaterial(EndArmorMaterial.THALLASIUM.value())
            .armorValuesWithOffset(ArmorTiers.IRON_ARMOR, new ArmorTier.ArmorValues(0))
            .build();

    public static ArmorTier TERMINITE = ArmorTier
            .builder("terminite")
            .armorMaterial(EndArmorMaterial.TERMINITE.value())
            .armorValuesWithOffset(ArmorTiers.DIAMOND_ARMOR, new ArmorTier.ArmorValues(0))
            .build();

    public static ArmorTier CRYSTALITE = ArmorTier
            .builder("crystalite")
            .armorMaterial(EndArmorMaterial.CRYSTALITE.value())
            .armorValuesWithOffset(ArmorTiers.NETHERITE_ARMOR, new ArmorTier.ArmorValues(1))
            .build();


    public static ArmorTier AETERNIUM = ArmorTier
            .builder("aeternium")
            .armorMaterial(EndArmorMaterial.AETERNIUM.value())
            .armorValuesWithOffset(
                    ArmorTiers.NETHERITE_ARMOR,
                    new ArmorTier.ArmorValues(100, () -> EndTemplates.PLATE_UPGRADE)
            )
            .build();

    // Same per-slot values as AETERNIUM/CRYSTALITE, just wrapping the elytra-specific render
    // material (see EndArmorMaterial.AETERNIUM_ELYTRA/CRYSTALITE_ELYTRA).
    public static ArmorTier AETERNIUM_ELYTRA = AETERNIUM.copyWithOffset(
            "aeternium_elytra",
            EndArmorMaterial.AETERNIUM_ELYTRA.value(),
            new ArmorTier.ArmorValues(0)
    );

    public static ArmorTier CRYSTALITE_ELYTRA = CRYSTALITE.copyWithOffset(
            "crystalite_elytra",
            EndArmorMaterial.CRYSTALITE_ELYTRA.value(),
            new ArmorTier.ArmorValues(0)
    );
}
