package org.betterx.betterend.item;

import org.betterx.betterend.effects.EndStatusEffects;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.trait.item.EndArmorItemTraitBuilder;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.item.api.ArmorItemDefinition;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class CrystaliteArmor extends Item {
    protected static <I extends CrystaliteArmor> ItemDefinition<I, ?> crystaliteArmorDefinition(
            ItemRegistry registry,
            String name,
            ArmorSlot slot,
            ArmorItemDefinition.ItemFactory<I> factory
    ) {
        return registry.defineArmorItem(name, factory)
                       .addTrait(EndArmorItemTraitBuilder.BUILDER.with(slot, EndArmorTier.CRYSTALITE))
                       .addTags(EndTags.CRYSTALITE_SET);
    }

    public final static MutableComponent CHEST_DESC;
    public final static MutableComponent ELYTRA_DESC;
    public final static MutableComponent BOOTS_DESC;
    public final static MutableComponent HELMET_DESC;
    public final static MutableComponent LEGGINGS_DESC;

    public CrystaliteArmor(ItemDefinition<?, ?> definition) {
        super(definition.getProperties());
    }

    private static final EquipmentSlot[] SET_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    /**
     * Set membership is decided by {@link EndTags#CRYSTALITE_SET}, not by this class. The elytra is a
     * chest-slot item that cannot extend {@code CrystaliteArmor} (both are {@link Item} subclasses), and
     * a marker interface would have kept the answer locked in code; the tag lets packs both add pieces
     * and remove ours.
     */
    public static boolean hasFullSet(LivingEntity owner) {
        for (EquipmentSlot slot : SET_SLOTS) {
            if (!owner.getItemBySlot(slot).is(EndTags.CRYSTALITE_SET)) {
                return false;
            }
        }
        return true;
    }

    public static void applySetEffect(LivingEntity owner) {
        if ((owner.tickCount & 63) == 0) {
            owner.addEffect(new MobEffectInstance(EndStatusEffects.CRYSTALITE_HEALTH_REGEN));
        }
    }

    static {
        Style descStyle = Style.EMPTY.applyFormats(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC);
        CHEST_DESC = Component.translatable("tooltip.armor.crystalite_chest");
        CHEST_DESC.setStyle(descStyle);
        ELYTRA_DESC = Component.translatable("tooltip.armor.crystalite_elytra");
        ELYTRA_DESC.setStyle(descStyle);
        BOOTS_DESC = Component.translatable("tooltip.armor.crystalite_boots");
        BOOTS_DESC.setStyle(descStyle);
        HELMET_DESC = Component.translatable("tooltip.armor.crystalite_helmet");
        HELMET_DESC.setStyle(descStyle);
        LEGGINGS_DESC = Component.translatable("tooltip.armor.crystalite_leggings");
        LEGGINGS_DESC.setStyle(descStyle);
    }
}
