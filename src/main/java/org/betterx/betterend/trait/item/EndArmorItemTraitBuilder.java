package org.betterx.betterend.trait.item;

import org.betterx.betterend.BetterEnd;
import static org.betterx.betterend.item.EndArmorItem.*;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ArmorTier;
import de.ambertation.wover.item.api.ArmorItemDefinition;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.trait.AbstractItemTraitBuilder;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;
import de.ambertation.wover.item.api.trait.ItemTraits;
import de.ambertation.wover.item.impl.trait.ItemTraitImpl;

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

    /**
     * Humanoid armor, using the tier's declared defense and toughness as-is (divider 1).
     * <p>
     * The dividers exist for pieces that deliberately trade protection away - the armored elytras, which
     * pass their own - not for regular armor. Applying one here would make the numbers declared on the
     * {@link net.minecraft.world.item.equipment.ArmorMaterial} a lie, and would put the tier below the
     * vanilla material it is built on.
     */
    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier
    ) {
        return with(
                slot, tier,
                1.0f, 1.0f,
                0.0f, true
        );
    }

    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier,
            float knockbackResistance
    ) {
        return with(
                slot, tier,
                1.0f, 1.0f,
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

                // Without these the piece is not enchantable outside of creative mode (see
                // ArmorSlot#humanoidArmorTags). The elytras deliberately stay out of them: they are only
                // chest-slot items, not humanoid armor, and #minecraft:chest_armor would also make them
                // trimmable. ItemTraits.ELYTRA_ITEM already puts them into the enchantable tags they should
                // have (durability + equippable), which matches vanilla's own elytra.
                if (!definition.hasTrait(ItemTraits.ELYTRA_ITEM)) {
                    definition.addTags(slot.humanoidArmorTags());
                }
                if (armorDefiniton.material() != tier.armorMaterial) {
                    throw new IllegalArgumentException("Armor material mismatch: " + armorDefiniton.material() + " != " + tier.armorMaterial);
                }
                var values = tier.getValues(slot);
                if (values == null) {
                    throw new IllegalArgumentException("Values for " + slot + " are not defined for " + tier);
                }

                definition.durability(slot.armorType.getDurability(values.durability()));

                // Both modifiers go into the slot this piece is actually worn in, and the armor value is
                // this slot's own entry in the material. Getting either wrong is silent: a modifier bound
                // to a slot the piece can never occupy is simply never applied, and it also renders under
                // the wrong "When on ..." heading in the tooltip. Every other BetterEnd tier applies its
                // material's declared defense verbatim per slot, so Crystalite does too.
                final EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(ArmorSlot.toEquipmentSlot(slot));

                if (defenseDivider > 0) {
                    definition.addAttribute(
                            Attributes.ARMOR,
                            new AttributeModifier(
                                    armorBoostId(slot),
                                    (double) tier.armorMaterial
                                            .defense().getOrDefault(slot.armorType, 0) / defenseDivider,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            slotGroup
                    );
                }

                if (toughnessDivider > 0) {
                    definition.addAttribute(
                            Attributes.ARMOR_TOUGHNESS,
                            new AttributeModifier(
                                    toughnessBoostId(slot),
                                    tier.armorMaterial
                                            .toughness() / toughnessDivider,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            slotGroup
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
