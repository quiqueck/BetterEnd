package org.betterx.betterend.item;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.complex.api.equipment.ArmorTier;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

public class EndArmorItem extends Item {
    public static final ResourceLocation BASE_BLINDNESS_RESISTANCE = BetterEnd.C.mk("base_blindness_resistance");
    public static final ResourceLocation BASE_KNOCKBACK_RESISTANCE = BetterEnd.C.mk("base_knockback_resistance");
    public static final ResourceLocation MAX_HEALTH_BOOST = BetterEnd.C.mk("max_health_boost");
    public static final ResourceLocation TOUGHNESS_BOOST = BetterEnd.C.mk("toughness_boost");
    public static final ResourceLocation ARMOR_BOOST = BetterEnd.C.mk("armor_boost");

    public static Properties createDefaultEndArmorSettings(ArmorSlot slot, ArmorTier tier) {
        var values = tier.getValues(slot);
        if (values == null) {
            throw new IllegalArgumentException("Values for " + slot + " are not defined for " + tier);
        }

        return EndItems.defaultSettings().durability(slot.armorType.getDurability(values.durability()));
    }

    public static ItemAttributeModifiers.Builder startAttributeBuilder(
            ArmorSlot slot,
            ArmorTier tier
    ) {
        return startAttributeBuilder(
                slot, tier,
                EndArmorTier.CRYSTALITE.armorMaterial
                        .defense()
                        .getOrDefault(slot.armorType, 0),
                EndArmorTier.CRYSTALITE.armorMaterial
                        .toughness(),
                0.0f
        );
    }

    public static ItemAttributeModifiers.Builder startAttributeBuilder(
            ArmorSlot slot,
            ArmorTier tier,
            int defenseDivider,
            float toughness,
            float knockbackResistance
    ) {
        final ItemAttributeModifiers.Builder builder = ItemAttributeModifiers
                .builder()
                .add(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                ARMOR_BOOST,
                                tier.armorMaterial
                                        .defense()
                                        .getOrDefault(ArmorType.CHESTPLATE, 0) / 1.25f,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.CHEST
                )
                .add(
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                TOUGHNESS_BOOST,
                                EndArmorTier.CRYSTALITE.armorMaterial
                                        .toughness() / 1.25f,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.CHEST
                );

        if (knockbackResistance > 0.0f) {
            builder.add(
                    Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(
                            BASE_KNOCKBACK_RESISTANCE,
                            knockbackResistance,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.MAINHAND
            );
        }

        return builder;
    }

    public static Properties createDefaultEndArmorSettings(
            ArmorSlot slot,
            ArmorTier tier,
            ItemAttributeModifiers attributes
    ) {
        final var props = createDefaultEndArmorSettings(slot, tier)
                .rarity(Rarity.RARE);
        if (attributes != null) {
            props.attributes(attributes);
        }
        return props;

    }

    public EndArmorItem(ArmorTier tier, ArmorSlot slot, Properties settings) {
        super(settings.component(
                DataComponents.EQUIPPABLE,
                Equippable.builder(slot.armorType.getSlot())
                          .setEquipSound(tier.armorMaterial.equipSound())
                          .setAsset(tier.armorMaterial.assetId())
                          .build()
        ));
    }
}
