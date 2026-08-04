package org.betterx.betterend.item;

import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class CrystaliteLeggings extends CrystaliteArmor {
    public static ItemDefinition<CrystaliteLeggings, ?> definition(ItemRegistry registry, String name) {
        return CrystaliteArmor.crystaliteArmorDefinition(
                registry, name, ArmorSlot.LEGGINGS_SLOT,
                CrystaliteLeggings::new
        ).addAttribute(
                Attributes.MAX_HEALTH,
                new AttributeModifier(
                        EndArmorItem.MAX_HEALTH_BOOST,
                        4.0,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.LEGS
        );
    }

    public CrystaliteLeggings(ItemDefinition<?, ?> definition) {
        super(definition);
    }

    /**
     * The leggings' bonus is a Max Health modifier. It does show up in the generic attribute block,
     * but only there and only as a raw number; name it explicitly so every Crystalite piece reads the
     * same way.
     */
    @Override
    public void appendHoverText(
            @NotNull ItemStack itemStack,
            @NotNull TooltipContext tooltipContext,
            @NotNull TooltipDisplay tooltipDisplay,
            @NotNull Consumer<Component> consumer,
            @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);

        consumer.accept(Component.empty());
        consumer.accept(LEGGINGS_DESC);
    }
}
