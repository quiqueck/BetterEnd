package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.wover.item.api.smithing.SmithingTemplates;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class EndTemplates {
    static final ResourceLocation EMPTY_SLOT_STICK = BetterEnd.C.mk("item/empty_slot_stick");
    static final ResourceLocation EMPTY_SLOT_HANDLE = BetterEnd.C.mk("item/empty_slot_handle");
    static final ResourceLocation EMPTY_SLOT_SWORD_HANDLE = BetterEnd.C.mk("item/empty_slot_sword_handle");
    static final ResourceLocation EMPTY_SLOT_SWORD_BLADE = BetterEnd.C.mk("item/empty_slot_sword_blade");
    static final ResourceLocation EMPTY_SLOT_PLATE = BetterEnd.C.mk("item/empty_slot_plate");
    static final ResourceLocation EMPTY_SLOT_HAMMER = BetterEnd.C.mk("item/empty_slot_hammer");

    static final ResourceLocation EMPTY_SLOT_HAMMER_HEAD = BetterEnd.C.mk("item/empty_slot_hammer_head");
    static final ResourceLocation EMPTY_SLOT_PICKAXE_HEAD = BetterEnd.C.mk("item/empty_slot_pickaxe_head");
    static final ResourceLocation EMPTY_SLOT_AXE_HEAD = BetterEnd.C.mk("item/empty_slot_axe_head");
    static final ResourceLocation EMPTY_SLOT_HOE_HEAD = BetterEnd.C.mk("item/empty_slot_hoe_head");
    static final ResourceLocation EMPTY_SLOT_SHOVEL_HEAD = BetterEnd.C.mk("item/empty_slot_shovel_head");
    static final ResourceLocation EMPTY_SLOT_ANVIL = BetterEnd.C.mk("item/empty_slot_anvil");
    static final ResourceLocation EMPTY_SLOT_ELYTRA = BetterEnd.C.mk("item/empty_slot_elytra");


    // LEATHER_HANDLE_ATTACHMENT and TOOL_ASSEMBLY must be declared before HANDLE_ATTACHMENT:
    // constructing EndItems.AETERNIUM_SET (an AeterniumSet) needs them, but HANDLE_ATTACHMENT's
    // own initializer is what triggers EndItems' class loading in the first place (via
    // EndItems.getItemRegistry()) - if they were declared after HANDLE_ATTACHMENT, that nested,
    // same-thread re-entrant access would observe them as still-unassigned (null).
    public static final SmithingTemplateItem LEATHER_HANDLE_ATTACHMENT = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "leather_handle_attachment",
                    List.of(
                            EMPTY_SLOT_HAMMER_HEAD,
                            EMPTY_SLOT_PICKAXE_HEAD,
                            EMPTY_SLOT_AXE_HEAD,
                            EMPTY_SLOT_HOE_HEAD,
                            EMPTY_SLOT_SHOVEL_HEAD
                    ),
                    List.of(EMPTY_SLOT_HANDLE)
            );

    public static final SmithingTemplateItem TOOL_ASSEMBLY = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "tool_assembly",
                    List.of(EMPTY_SLOT_SWORD_BLADE),
                    List.of(EMPTY_SLOT_SWORD_HANDLE)
            );

    public static final SmithingTemplateItem HANDLE_ATTACHMENT = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "handle_attachment",
                    List.of(
                            EMPTY_SLOT_HAMMER_HEAD,
                            EMPTY_SLOT_PICKAXE_HEAD,
                            EMPTY_SLOT_AXE_HEAD,
                            EMPTY_SLOT_HOE_HEAD,
                            EMPTY_SLOT_SHOVEL_HEAD
                    ),
                    List.of(EMPTY_SLOT_STICK)
            );

    public static final SmithingTemplateItem PLATE_UPGRADE = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "plate_upgrade",
                    SmithingTemplates.ARMOR,
                    List.of(EMPTY_SLOT_PLATE)
            );

    public static final SmithingTemplateItem THALLASIUM_UPGRADE = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "thallasium_upgrade",
                    List.of(EMPTY_SLOT_STICK),
                    List.of(SmithingTemplates.EMPTY_SLOT_INGOT)
            );

    public static final SmithingTemplateItem TERMINITE_UPGRADE = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "terminite_upgrade",
                    List.of(EMPTY_SLOT_ANVIL, EMPTY_SLOT_STICK),
                    List.of(SmithingTemplates.EMPTY_SLOT_INGOT)
            );

    public static final SmithingTemplateItem AETERNIUM_UPGRADE = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "aeternium_upgrade",
                    List.of(EMPTY_SLOT_ANVIL, EMPTY_SLOT_ELYTRA),
                    List.of(SmithingTemplates.EMPTY_SLOT_INGOT)
            );


    public static final SmithingTemplateItem NETHERITE_UPGRADE = EndItems
            .getItemRegistry()
            .registerSmithingTemplateItem(
                    "netherite_upgrade",
                    List.of(EMPTY_SLOT_HAMMER),
                    List.of(SmithingTemplates.EMPTY_SLOT_INGOT)
            );

    public static void ensureStaticallyLoaded() {
    }
}
