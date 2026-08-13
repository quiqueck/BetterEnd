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
     * Humanoid armor, using the tier's declared defense and toughness as-is.
     * <p>
     * The overrides exist for pieces that deliberately trade protection away - the armored elytras, which
     * pass their own - not for regular armor. Setting one here would make the numbers declared on the
     * {@link net.minecraft.world.item.equipment.ArmorMaterial} a lie, and would put the tier below the
     * vanilla material it is built on.
     */
    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier
    ) {
        return with(
                slot, tier,
                null, null,
                0.0f, true
        );
    }

    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier,
            float knockbackResistance
    ) {
        return with(
                slot, tier,
                null, null,
                knockbackResistance, true
        );
    }

    /**
     * @param defense   armor points this piece grants, or {@code null} to use the material's own value
     *                  for this slot. Authored outright rather than derived: these are read straight off
     *                  the tooltip, so they have to be numbers a player recognises.
     * @param toughness armor toughness, or {@code null} for the material's own.
     */
    public @Nullable List<ItemTrait<?, ?>> with(
            ArmorSlot slot, ArmorTier tier,
            @Nullable Float defense,
            @Nullable Float toughness,
            float knockbackResistance,
            boolean fireproof
    ) {
        if (fireproof) {
            return combine(
                    new Trait(slot, tier, defense, toughness, knockbackResistance),
                    ItemTraits.IS_FIREPROOF.withDefault()
            );
        }
        return combine(
                new Trait(slot, tier, defense, toughness, knockbackResistance)
        );
    }

    private class Trait extends ItemTraitImpl.Generic {
        private final ArmorSlot slot;
        private final ArmorTier tier;
        private final @Nullable Float defense;
        private final @Nullable Float toughness;
        private final float knockbackResistance;

        Trait(
                ArmorSlot slot,
                ArmorTier tier,
                @Nullable Float defense, @Nullable Float toughness,
                float knockbackResistance
        ) {
            this.slot = slot;
            this.tier = tier;
            this.defense = defense;
            this.toughness = toughness;
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

                // A piece that trades protection away states the value it lands on, rather than a factor
                // to divide the material's by. Dividers produced 6.666666 armor and 0.96 toughness, and
                // a player reading that off a tooltip sees noise, not a deliberate trade.
                final double armorValue = defense != null
                        ? defense
                        : tier.armorMaterial.defense().getOrDefault(slot.armorType, 0);

                if (armorValue > 0) {
                    definition.addAttribute(
                            Attributes.ARMOR,
                            new AttributeModifier(
                                    armorBoostId(slot),
                                    armorValue,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            slotGroup
                    );
                }

                final double toughnessValue = toughness != null
                        ? toughness
                        : tier.armorMaterial.toughness();

                if (toughnessValue > 0) {
                    definition.addAttribute(
                            Attributes.ARMOR_TOUGHNESS,
                            new AttributeModifier(
                                    toughnessBoostId(slot),
                                    toughnessValue,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            slotGroup
                    );
                }

                // Same slot group as the armor above, for the same reason: bound to MAINHAND this only
                // applied while the piece was *held*, which for a chestplate or an elytra is never. It
                // was silently inert on both elytras, the only two callers that pass a value.
                if (knockbackResistance > 0.0f) {
                    definition.addAttribute(
                            Attributes.KNOCKBACK_RESISTANCE,
                            new AttributeModifier(
                                    knockbackBoostId(slot),
                                    knockbackResistance,
                                    AttributeModifier.Operation.ADD_VALUE
                            ),
                            slotGroup
                    );
                }
            } else {
                throw new IllegalArgumentException("Definition must be an instance of ArmorItemDefinition");
            }


        }
    }

}
