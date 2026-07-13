package org.betterx.betterend.item;

import org.betterx.betterend.registry.EndAttributes;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.ItemRegistry;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class CrystaliteHelmet extends CrystaliteArmor {
    public static ItemDefinition<CrystaliteHelmet, ?> definition(ItemRegistry registry, String name) {
        return CrystaliteArmor.crystaliteArmorDefinition(
                registry, name, ArmorSlot.HELMET_SLOT,
                CrystaliteHelmet::new
        ).addAttribute(
                EndAttributes.BLINDNESS_RESISTANCE,
                new AttributeModifier(
                        EndArmorItem.BASE_BLINDNESS_RESISTANCE,
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.HEAD
        );
    }

    public CrystaliteHelmet(ItemDefinition<?, ?> definition) {
        super(definition);
    }
}
