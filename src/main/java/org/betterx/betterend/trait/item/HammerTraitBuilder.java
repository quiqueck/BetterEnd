package org.betterx.betterend.trait.item;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.complex.api.equipment.ToolTier;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ToolItemDefinition;
import de.ambertation.wover.item.api.trait.AbstractItemTraitBuilder;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;
import de.ambertation.wover.item.impl.trait.ItemTraitImpl;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

import java.util.List;
import org.jetbrains.annotations.Nullable;


public class HammerTraitBuilder extends AbstractItemTraitBuilder.Generic {
    public final static ResourceLocation ATTACK_KNOCKBACK_MODIFIER_ID = BetterEnd.C.mk("base_knockback");
    public static final HammerTraitBuilder BUILDER = new HammerTraitBuilder();

    private HammerTraitBuilder() {
        super(ItemTraitKey.ofUnique(BetterEnd.C, "hammer"));
    }

    public @Nullable List<ItemTrait<?, ?>> with(
            ToolTier tier,
            float knockback
    ) {
        return combine(
                new Trait(tier, knockback)
        );
    }

    private class Trait extends ItemTraitImpl.Generic {
        private final ToolTier tier;
        private final float knockback;

        Trait(
                ToolTier tier,
                float knockback
        ) {
            this.tier = tier;
            this.knockback = knockback;
        }

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition) {
            if (ModCore.isDatagen()) {
                definition.addTags(CommonItemTags.HAMMERS);
            }
            final var values = tier.getValues(ToolSlot.HAMMER_SLOT);
            if (values != null) {
                if (definition instanceof ToolItemDefinition toolDef) {
                    toolDef.tool(
                            tier.toolMaterial, MineableTags.HAMMER,
                            values.attackDamage(), values.attackSpeed(), values.disableBlockingForSeconds()
                    );
                } else {
                    throw new IllegalArgumentException("Definition must be a ToolItemDefinition");
                }
            }

            if (knockback != 0) {
                definition.addAttribute(
                        Attributes.ATTACK_KNOCKBACK,
                        new AttributeModifier(
                                ATTACK_KNOCKBACK_MODIFIER_ID,
                                knockback,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                );
            }
        }
    }

}