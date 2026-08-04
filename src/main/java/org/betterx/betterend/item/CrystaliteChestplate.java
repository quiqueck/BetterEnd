package org.betterx.betterend.item;

import org.betterx.betterend.effects.EndStatusEffects;
import org.betterx.betterend.interfaces.MobEffectApplier;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class CrystaliteChestplate extends CrystaliteArmor implements MobEffectApplier {
    public static ItemDefinition<CrystaliteChestplate, ?> definition(ItemRegistry registry, String name) {
        return CrystaliteArmor.crystaliteArmorDefinition(
                registry, name, ArmorSlot.CHESTPLATE_SLOT,
                CrystaliteChestplate::new
        );
    }

    public CrystaliteChestplate(ItemDefinition<?, ?> definition) {
        super(definition);
    }

    @Override
    public void applyEffect(LivingEntity owner) {
        owner.addEffect(new MobEffectInstance(EndStatusEffects.CRYSTALITE_DIG_SPEED));
    }

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
        consumer.accept(CHEST_DESC);
    }
}
