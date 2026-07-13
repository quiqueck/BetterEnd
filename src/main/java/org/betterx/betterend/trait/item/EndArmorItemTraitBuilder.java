package org.betterx.betterend.trait.item;

import org.betterx.betterend.BetterEnd;
import static org.betterx.betterend.item.EndArmorItem.*;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.complex.api.equipment.ArmorTier;
import org.betterx.wover.item.api.ArmorItemDefinition;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.trait.AbstractItemTraitBuilder;
import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitKey;
import org.betterx.wover.item.api.trait.ItemTraits;
import org.betterx.wover.item.impl.trait.ItemTraitImpl;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class EndArmorItemTraitBuilder extends AbstractItemTraitBuilder.Generic {
    public static final EndArmorItemTraitBuilder BUILDER = new EndArmorItemTraitBuilder();

    private EndArmorItemTraitBuilder() {
        super(ItemTraitKey.ofUnique(BetterEnd.C, "armor"));
    }

    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier
    ) {
        return with(
                slot, tier,
                1.25f, 1.25f,
                0.0f, true
        );
    }

    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier,
            float knockbackResistance
    ) {
        return with(
                slot, tier,
                1.25f, 1.25f,
                knockbackResistance, true
        );
    }

    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier,
            float defenseDivider,
            float toughnessDivider,
            float knockbackResistance,
            boolean fireproof
    ) {
        if (fireproof) {
            return combine(
                    new Trait(slot, tier, defenseDivider, toughnessDivider, knockbackResistance),
                    ItemTraits.IS_FIREPROOF.withDefault()
            );
        }
        return combine(
                new Trait(slot, tier, defenseDivider, toughnessDivider, knockbackResistance)
        );
    }

    private class Trait extends ItemTraitImpl.Generic {
        private final ArmorSlot slot;
        private final ArmorTier tier;
        private final float defenseDivider;
        private final float toughnessDivider;
        private final float knockbackResistance;

        Trait(
                ArmorSlot slot,
                ArmorTier tier,
                float defenseDivider, float toughnessDivider,
                float knockbackResistance
        ) {
            this.slot = slot;
            this.tier = tier;
            this.defenseDivider = defenseDivider;
            this.toughnessDivider = toughnessDivider;
            this.knockbackResistance = knockbackResistance;
        }

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition) {
            if (definition instanceof ArmorItemDefinition armorDefiniton) {
                if (armorDefiniton.armorType() == null && armorDefiniton.material() == null) {
                    armorDefiniton.humanoidArmor(tier.armorMaterial, slot.armorType);
                }
                if (armorDefiniton.material() != tier.armorMaterial) {
                    throw new IllegalArgumentException("Armor material mismatch: " + armorDefiniton.material() + " != " + tier.armorMaterial);
                }
                var values = tier.getValues(slot);
                if (values == null) {
                    throw new IllegalArgumentException("Values for " + slot + " are not defined for " + tier);
                }

                definition.durability(slot.armorType.getDurability(values.durability()));

                if (defenseDivider > 0) {
                    definition.addAttribute(
                            Attributes.ARMOR,
                            new AttributeModifier(
                                    ARMOR_BOOST,
                                    (double) tier.armorMaterial
                                            .defense().get(ArmorType.CHESTPLATE) / defenseDivider,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.CHEST
                    );
                }

                if (toughnessDivider > 0) {
                    definition.addAttribute(
                            Attributes.ARMOR_TOUGHNESS,
                            new AttributeModifier(
                                    TOUGHNESS_BOOST,
                                    tier.armorMaterial
                                            .toughness() / toughnessDivider,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.CHEST
                    );
                }

                if (knockbackResistance > 0.0f) {
                    definition.addAttribute(
                            Attributes.KNOCKBACK_RESISTANCE,
                            new AttributeModifier(
                                    BASE_KNOCKBACK_RESISTANCE,
                                    knockbackResistance,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            EquipmentSlotGroup.MAINHAND
                    );
                }
            } else {
                throw new IllegalArgumentException("Definition must be an instance of ArmorItemDefinition");
            }


        }
    }

}
