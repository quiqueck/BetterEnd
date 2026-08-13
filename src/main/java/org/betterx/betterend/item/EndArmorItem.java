package org.betterx.betterend.item;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ArmorTier;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;

public class EndArmorItem extends Item {
    public static final Identifier BASE_BLINDNESS_RESISTANCE = BetterEnd.C.mk("base_blindness_resistance");
    public static final Identifier BASE_KNOCKBACK_RESISTANCE = BetterEnd.C.mk("base_knockback_resistance");
    public static final Identifier MAX_HEALTH_BOOST = BetterEnd.C.mk("max_health_boost");
    public static final Identifier TOUGHNESS_BOOST = BetterEnd.C.mk("toughness_boost");
    public static final Identifier ARMOR_BOOST = BetterEnd.C.mk("armor_boost");

    /**
     * Attribute modifiers are keyed by id, and {@code LivingEntity#collectEquipmentChanges} applies each
     * equipped piece with {@code removeModifier(id)} followed by {@code addTransientModifier}. Four
     * pieces sharing one id therefore overwrite each other and only the last one survives - a full set
     * would grant a single piece's worth of armor. Vanilla avoids this by giving every slot its own id
     * ({@code minecraft:armor.helmet}, {@code armor.chestplate}, ...); these do the same.
     */
    public static Identifier armorBoostId(ArmorSlot slot) {
        return BetterEnd.C.mk("armor_boost_" + slot.name);
    }

    /** Per-slot toughness modifier id. See {@link #armorBoostId(ArmorSlot)} for why it must be unique. */
    public static Identifier toughnessBoostId(ArmorSlot slot) {
        return BetterEnd.C.mk("toughness_boost_" + slot.name);
    }

    /**
     * Per-slot knockback resistance modifier id. Only chest-slot pieces use it today, so the single
     * shared {@link #BASE_KNOCKBACK_RESISTANCE} it replaced could not yet collide - but it would the
     * moment a second slot passed a value, in exactly the way {@link #armorBoostId(ArmorSlot)} describes.
     */
    public static Identifier knockbackBoostId(ArmorSlot slot) {
        return BetterEnd.C.mk("knockback_boost_" + slot.name);
    }

    public static Properties createDefaultEndArmorSettings(ArmorSlot slot, ArmorTier tier) {
        var values = tier.getValues(slot);
        if (values == null) {
            throw new IllegalArgumentException("Values for " + slot + " are not defined for " + tier);
        }

        return EndItems.defaultSettings().durability(slot.armorType.getDurability(values.durability()));
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
