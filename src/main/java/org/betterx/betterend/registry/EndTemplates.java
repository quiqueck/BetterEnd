package org.betterx.betterend.registry;


import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.item.api.SmithingTemplateDefinition;
import de.ambertation.wover.item.api.smithing.SmithingTemplates;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class EndTemplates {
    static final Identifier EMPTY_SLOT_STICK = BetterEnd.C.mk("item/empty_slot_stick");
    static final Identifier EMPTY_SLOT_HANDLE = BetterEnd.C.mk("item/empty_slot_handle");
    static final Identifier EMPTY_SLOT_SWORD_HANDLE = BetterEnd.C.mk("item/empty_slot_sword_handle");
    static final Identifier EMPTY_SLOT_SWORD_BLADE = BetterEnd.C.mk("item/empty_slot_sword_blade");
    static final Identifier EMPTY_SLOT_PLATE = BetterEnd.C.mk("item/empty_slot_plate");
    static final Identifier EMPTY_SLOT_HAMMER = BetterEnd.C.mk("item/empty_slot_hammer");

    static final Identifier EMPTY_SLOT_HAMMER_HEAD = BetterEnd.C.mk("item/empty_slot_hammer_head");
    static final Identifier EMPTY_SLOT_PICKAXE_HEAD = BetterEnd.C.mk("item/empty_slot_pickaxe_head");
    static final Identifier EMPTY_SLOT_AXE_HEAD = BetterEnd.C.mk("item/empty_slot_axe_head");
    static final Identifier EMPTY_SLOT_HOE_HEAD = BetterEnd.C.mk("item/empty_slot_hoe_head");
    static final Identifier EMPTY_SLOT_SHOVEL_HEAD = BetterEnd.C.mk("item/empty_slot_shovel_head");
    static final Identifier EMPTY_SLOT_ANVIL = BetterEnd.C.mk("item/empty_slot_anvil");
    static final Identifier EMPTY_SLOT_ELYTRA = BetterEnd.C.mk("item/empty_slot_elytra");


    // LEATHER_HANDLE_ATTACHMENT and TOOL_ASSEMBLY must be declared before HANDLE_ATTACHMENT:
    // constructing EndEquipmentItems.AETERNIUM_SET (an AeterniumSet) needs them, but HANDLE_ATTACHMENT's
    // own initializer is what triggers EndItems' class loading in the first place (via
    // EndItems.getItemRegistry()) - if they were declared after HANDLE_ATTACHMENT, that nested,
    // same-thread re-entrant access would observe them as still-unassigned (null).
    public static final SmithingTemplateItem LEATHER_HANDLE_ATTACHMENT = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("leather_handle_attachment", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(
                    EMPTY_SLOT_HAMMER_HEAD,
                    EMPTY_SLOT_PICKAXE_HEAD,
                    EMPTY_SLOT_AXE_HEAD,
                    EMPTY_SLOT_HOE_HEAD,
                    EMPTY_SLOT_SHOVEL_HEAD
            ))
            .additionalSlotEmptyIcons(List.of(EMPTY_SLOT_HANDLE))
            .buildAndRegister();

    public static final SmithingTemplateItem TOOL_ASSEMBLY = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("tool_assembly", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(EMPTY_SLOT_SWORD_BLADE))
            .additionalSlotEmptyIcons(List.of(EMPTY_SLOT_SWORD_HANDLE))
            .buildAndRegister();

    public static final SmithingTemplateItem HANDLE_ATTACHMENT = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("handle_attachment", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(
                    EMPTY_SLOT_HAMMER_HEAD,
                    EMPTY_SLOT_PICKAXE_HEAD,
                    EMPTY_SLOT_AXE_HEAD,
                    EMPTY_SLOT_HOE_HEAD,
                    EMPTY_SLOT_SHOVEL_HEAD
            ))
            .additionalSlotEmptyIcons(List.of(EMPTY_SLOT_STICK))
            .buildAndRegister();

    public static final SmithingTemplateItem PLATE_UPGRADE = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("plate_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(SmithingTemplates.ARMOR)
            .additionalSlotEmptyIcons(List.of(EMPTY_SLOT_PLATE))
            .buildAndRegister();

    public static final SmithingTemplateItem THALLASIUM_UPGRADE = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("thallasium_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(EMPTY_SLOT_STICK))
            .additionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
            .buildAndRegister();

    public static final SmithingTemplateItem TERMINITE_UPGRADE = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("terminite_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(EMPTY_SLOT_ANVIL, EMPTY_SLOT_STICK))
            .additionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
            .buildAndRegister();

    public static final SmithingTemplateItem AETERNIUM_UPGRADE = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("aeternium_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(EMPTY_SLOT_ANVIL, EMPTY_SLOT_ELYTRA))
            .additionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
            .buildAndRegister();


    public static final SmithingTemplateItem NETHERITE_UPGRADE = EndItems
            .getItemRegistry()
            .defineSmithingTemplate("netherite_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
            .baseSlotEmptyIcons(List.of(EMPTY_SLOT_HAMMER))
            .additionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
            .buildAndRegister();

    public static void ensureStaticallyLoaded() {
    }
}
