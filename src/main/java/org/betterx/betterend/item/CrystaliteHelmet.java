package org.betterx.betterend.item;

import org.betterx.betterend.registry.EndAttributes;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

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

    /**
     * The helmet's bonus is the {@code betterend:generic.blindness_resistance} attribute, which on its
     * own renders as a bare number the player has no way to interpret. Spell it out the same way the
     * boots and the chestplate do.
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
        consumer.accept(HELMET_DESC);
    }
}
