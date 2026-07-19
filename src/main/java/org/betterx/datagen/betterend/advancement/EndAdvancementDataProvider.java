package org.betterx.datagen.betterend.advancement;

import org.betterx.bclib.api.v2.advancement.AdvancementManager;
import org.betterx.bclib.api.v3.datagen.AdvancementDataProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.EndTemplates;
import org.betterx.wover.complex.api.equipment.ArmorSlot;
import org.betterx.wover.complex.api.equipment.ToolSlot;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;

import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EndAdvancementDataProvider extends AdvancementDataProvider {
    public EndAdvancementDataProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(List.of(BetterEnd.MOD_ID), output, registryLookup);
    }

    @Override
    @SuppressWarnings("removal")
    protected void bootstrap(HolderLookup.Provider lookup) {
        final HolderLookup.RegistryLookup<Structure> structures = lookup.lookupOrThrow(Registries.STRUCTURE);
        final HolderLookup.RegistryLookup<Biome> biomeLookup = lookup.lookupOrThrow(Registries.BIOME);
        ResourceLocation root = AdvancementManager.Builder
                .create(BetterEnd.C.mk("root"))
                .startDisplay(EndBlocks.END_MYCELIUM)
                .frame(AdvancementType.TASK)
                .hideFromChat()
                // 1.21.2+ resolves the advancement background through the GUI sprite atlas, so it needs a
                // sprite id (no "textures/" prefix, no ".png"), not the old raw texture path.
                .background(ResourceLocation.withDefaultNamespace("gui/advancements/backgrounds/end"))
                .endDisplay()
                .addCriterion(
                        "welcome",
                        PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())
                )
                .requirements(Strategy.OR)
                .build();

        ResourceLocation enterEnd = AdvancementManager.Builder
                .create(BetterEnd.C.mk("enter_end"))
                .startDisplay(EndBlocks.CAVE_MOSS)
                .endDisplay()
                .parent(root)
                .addCriterion(
                        "entered_end",
                        ChangeDimensionTrigger
                                .TriggerInstance
                                .changedDimensionTo(Level.END)
                )
                .requirements(Strategy.OR)
                .build();

        ResourceLocation portal = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal"))
                .parent(enterEnd)
                .startDisplay(EndBlocks.ETERNAL_PEDESTAL)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addAtStructureCriterion("eternal_portal", EndStructures.ETERNAL_PORTAL.getHolder(structures))
                .requirements(Strategy.OR)
                .build();

        ResourceLocation portalOn = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal_on"))
                .parent(portal)
                .startDisplay(EndItems.ETERNAL_CRYSTAL)
                .endDisplay()
                .addCriterion("turn_on", BECriteria.PORTAL_ON_TRIGGER)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation portalTravel = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal_travel"))
                .parent(portalOn)
                .startDisplay(Items.GRASS_BLOCK)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .addCriterion("travel", BECriteria.PORTAL_TRAVEL_TRIGGER)
                .requirements(Strategy.OR)
                .build();

        final var biomes = biomeLookup
                .listElementIds()
                .filter(id -> id.location().getNamespace().equals(BetterEnd.C.modId))
                .toList();

        if (!biomes.isEmpty()) {
            ResourceLocation allTheBiomes = AdvancementManager.Builder
                    .create(BetterEnd.C.mk("all_the_biomes"))
                    .parent(enterEnd)
                    .startDisplay(EndItems.AETERNIUM_SET.get(ArmorSlot.BOOTS_SLOT))
                    .frame(AdvancementType.CHALLENGE)
                    .endDisplay()
                    .addVisitBiomesCriterion(biomes
                            .stream()
                            .sorted(Comparator.comparing(ResourceKey::location))
                            .map(key -> (Holder<Biome>) biomeLookup.get(key).orElseThrow())
                            .toList())
                    .requirements(Strategy.AND)
                    .rewardXP(1500)
                    .build();


            ResourceLocation village = AdvancementManager.Builder
                    .create(BetterEnd.C.mk("village"))
                    .parent(allTheBiomes)
                    .startDisplay(EndBlocks.TENANEA.getBlock(WoodSlots.DOOR))
                    .frame(AdvancementType.GOAL)
                    .endDisplay()
                    .addAtStructureCriterion("end_village", EndStructures.END_VILLAGE.getHolder(structures))
                    .requirements(Strategy.OR)
                    .build();
        }

        ResourceLocation allElytras = AdvancementManager.Builder
                .create(BetterEnd.C.mk("all_elytras"))
                .parent(enterEnd)
                .startDisplay(EndItems.CRYSTALITE_ELYTRA)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addInventoryChangedCriterion("vanilla", Items.ELYTRA)
                .addInventoryChangedCriterion("crystalite", EndItems.CRYSTALITE_ELYTRA)
                .addInventoryChangedCriterion("armored", EndItems.ARMORED_ELYTRA)
                .requirements(Strategy.AND)
                .build();

        ResourceLocation infusion = AdvancementManager.Builder
                .create(BetterEnd.C.mk("infusion"))
                .parent(enterEnd)
                .startDisplay(EndBlocks.INFUSION_PEDESTAL)
                .endDisplay()
                .addInventoryChangedCriterion("infusion_pedestal", EndBlocks.INFUSION_PEDESTAL)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation infusionFinished = AdvancementManager.Builder
                .create(BetterEnd.C.mk("infusion_finished"))
                .parent(infusion)
                .startDisplay(Items.ENDER_EYE)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addCriterion("finished", BECriteria.INFUSION_FINISHED_TRIGGER)
                .requirements(Strategy.OR)
                .build();


        ResourceLocation allTheTemplates = AdvancementManager.Builder
                .create(BetterEnd.C.mk("all_the_templates"))
                .parent(enterEnd)
                .startDisplay(EndTemplates.TOOL_ASSEMBLY)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .addInventoryChangedAnyCriterion("got_handle", EndTemplates.HANDLE_ATTACHMENT)
                .addInventoryChangedAnyCriterion("got_tool", EndTemplates.TOOL_ASSEMBLY)
                .addInventoryChangedAnyCriterion("got_leather", EndTemplates.LEATHER_HANDLE_ATTACHMENT)
                .addInventoryChangedAnyCriterion("got_plate", EndTemplates.PLATE_UPGRADE)
                .addInventoryChangedAnyCriterion("got_terminite", EndTemplates.TERMINITE_UPGRADE)
                .addInventoryChangedAnyCriterion("got_aeternium", EndTemplates.AETERNIUM_UPGRADE)
                .addInventoryChangedAnyCriterion("got_thallasium", EndTemplates.THALLASIUM_UPGRADE)
                .addInventoryChangedAnyCriterion("got_netherite", EndTemplates.NETHERITE_UPGRADE)
                .requirements(Strategy.AND)
                .rewardXP(1500)
                .build();

        ResourceLocation hammer = AdvancementManager.Builder
                .create(BetterEnd.C.mk("hammer"))
                .parent(enterEnd)
                .startDisplay(EndItems.DIAMOND_HAMMER)
                .endDisplay()
                .addInventoryChangedCriterion("got_diamond_hammer", EndItems.DIAMOND_HAMMER)
                .addInventoryChangedCriterion("got_thallasium_hammer", EndBlocks.THALLASIUM.equipment.get(ToolSlot.HAMMER_SLOT))
                .addInventoryChangedCriterion("got_terminite_hammer", EndBlocks.TERMINITE.equipment.get(ToolSlot.HAMMER_SLOT))
                .requirements(Strategy.OR)
                .build();

        ResourceLocation thallasiumAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("thallasium_anvil"))
                .parent(hammer)
                .startDisplay(EndBlocks.THALLASIUM.getBlock(MetalMaterial.ANVIL))
                .endDisplay()
                .addInventoryChangedCriterion("got_thallasium_anvil", EndBlocks.THALLASIUM.getBlock(MetalMaterial.ANVIL))
                .requirements(Strategy.OR)
                .build();

        ResourceLocation thallasiumPlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("thallasium_plate"))
                .parent(thallasiumAnvil)
                .startDisplay(EndBlocks.THALLASIUM.equipment.forgedPlate)
                .endDisplay()
                .addInventoryChangedCriterion("got_thallasium_plate", EndBlocks.THALLASIUM.equipment.forgedPlate)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation terminiteAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("terminite_anvil"))
                .parent(thallasiumAnvil)
                .startDisplay(EndBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                .endDisplay()
                .addInventoryChangedCriterion("got_terminite_anvil", EndBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                .requirements(Strategy.OR)
                .build();

        ResourceLocation terminitePlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("terminite_plate"))
                .parent(terminiteAnvil)
                .startDisplay(EndBlocks.TERMINITE.equipment.forgedPlate)
                .endDisplay()
                .addInventoryChangedCriterion("got_erminite_plate", EndBlocks.TERMINITE.equipment.forgedPlate)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation aeterniumAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_anvil"))
                .parent(terminiteAnvil)
                .startDisplay(EndBlocks.AETERNIUM_ANVIL)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_anvil", EndBlocks.AETERNIUM_ANVIL)
                .requirements(Strategy.OR)
                .rewardXP(500)
                .build();

        ResourceLocation aeterniumHammerHead = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_hammer_head"))
                .parent(aeterniumAnvil)
                .startDisplay(EndItems.AETERNIUM_SET.hammerHead)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_hammer_head", EndItems.AETERNIUM_SET.hammerHead)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation aeterniumHammer = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_hammer"))
                .parent(aeterniumHammerHead)
                .startDisplay(EndItems.AETERNIUM_SET.get(ToolSlot.HAMMER_SLOT))
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_hammer", EndItems.AETERNIUM_SET.get(ToolSlot.HAMMER_SLOT))
                .requirements(Strategy.OR)
                .build();

        ResourceLocation aeterniumPlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_plate"))
                .parent(aeterniumHammer)
                .startDisplay(EndItems.AETERNIUM_SET.forgedPlate)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_plate", EndItems.AETERNIUM_SET.forgedPlate)
                .requirements(Strategy.OR)
                .rewardXP(200)
                .build();

        ResourceLocation thallasiumArmor = addArmor(EndBlocks.THALLASIUM)
                .parent(thallasiumPlate)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation thallasiumHead = addToolHeads(EndBlocks.THALLASIUM)
                .parent(thallasiumAnvil)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation thallasium = addTools(EndBlocks.THALLASIUM)
                .parent(thallasiumHead)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation terminiteHead = addToolHeads(EndBlocks.TERMINITE)
                .parent(terminiteAnvil)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation terminite = addTools(EndBlocks.TERMINITE)
                .parent(terminiteHead)
                .requirements(Strategy.OR)
                .build();

        ResourceLocation terminiteArmor = addArmor(EndBlocks.TERMINITE)
                .parent(terminitePlate)
                .requirements(Strategy.OR)
                .build();


        ResourceLocation aeterniumHead = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_tool_head"))
                .startDisplay(EndItems.AETERNIUM_SET.pickaxeHead)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .parent(aeterniumHammer)
                .addInventoryChangedCriterion("got_aeternium_pickaxe_head", EndItems.AETERNIUM_SET.pickaxeHead)
                .addInventoryChangedCriterion("got_aeternium_hoe_head", EndItems.AETERNIUM_SET.hoeHead)
                .addInventoryChangedCriterion("got_aeternium_axe_head", EndItems.AETERNIUM_SET.axeHead)
                .addInventoryChangedCriterion("got_aeternium_shovel_head", EndItems.AETERNIUM_SET.shovelHead)
                .addInventoryChangedCriterion(
                        "got_aeternium_sword_head",
                        EndItems.AETERNIUM_SET.swordBlade,
                        EndItems.AETERNIUM_SET.swordHandle
                )
                .requirements(Strategy.AND)
                .rewardXP(200)
                .build();

        ResourceLocation aeternium = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_tool"))
                .startDisplay(EndItems.AETERNIUM_SET.pickaxe())
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .parent(aeterniumHead)
                .addInventoryChangedCriterion("got_aeternium_pickaxe", EndItems.AETERNIUM_SET.pickaxe())
                .addInventoryChangedCriterion("got_aeternium_hoe", EndItems.AETERNIUM_SET.get(ToolSlot.HOE_SLOT))
                .addInventoryChangedCriterion("got_aeternium_axe", EndItems.AETERNIUM_SET.axe())
                .addInventoryChangedCriterion("got_aeternium_shovel", EndItems.AETERNIUM_SET.get(ToolSlot.SHOVEL_SLOT))
                .addInventoryChangedCriterion("got_aeternium_sword", EndItems.AETERNIUM_SET.get(ToolSlot.SWORD_SLOT))
                .requirements(Strategy.AND)
                .rewardXP(2000)
                .build();

        ResourceLocation aeterniumArmor = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_armor"))
                .startDisplay(EndItems.AETERNIUM_SET.get(ArmorSlot.CHESTPLATE_SLOT))
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .parent(aeterniumPlate)
                .addInventoryChangedCriterion("got_aeternium_helmet", EndItems.AETERNIUM_SET.get(ArmorSlot.HELMET_SLOT))
                .addInventoryChangedCriterion("got_aeternium_chestplate", EndItems.AETERNIUM_SET.get(ArmorSlot.CHESTPLATE_SLOT))
                .addInventoryChangedCriterion("got_aeternium_leggings", EndItems.AETERNIUM_SET.get(ArmorSlot.LEGGINGS_SLOT))
                .addInventoryChangedCriterion("got_aeternium_boots", EndItems.AETERNIUM_SET.get(ArmorSlot.BOOTS_SLOT))
                .requirements(Strategy.AND)
                .rewardXP(2000)
                .build();
    }

    AdvancementManager.Builder addTools(MetalMaterial mat) {
        return AdvancementManager.Builder
                .create(BetterEnd.C.mk(mat.baseName + "_tool"))
                .startDisplay(mat.equipment.get(ToolSlot.PICKAXE_SLOT))
                .endDisplay()
                .addInventoryChangedCriterion(
                        "got_" + mat.baseName + "_pickaxe",
                        mat.equipment.get(ToolSlot.PICKAXE_SLOT)
                )
                .addInventoryChangedCriterion("got_" + mat.baseName + "_hoe", mat.equipment.get(ToolSlot.HOE_SLOT))
                .addInventoryChangedCriterion("got_" + mat.baseName + "_axe", mat.equipment.get(ToolSlot.AXE_SLOT))
                .addInventoryChangedCriterion(
                        "got_" + mat.baseName + "_shovel",
                        mat.equipment.get(ToolSlot.SHOVEL_SLOT)
                )
                .addInventoryChangedCriterion(
                        "got_" + mat.baseName + "_sword",
                        mat.equipment.get(ToolSlot.SWORD_SLOT)
                );
    }

    AdvancementManager.Builder addToolHeads(MetalMaterial mat) {
        return AdvancementManager.Builder
                .create(BetterEnd.C.mk(mat.baseName + "_tool_head"))
                .startDisplay(mat.equipment.pickaxeHead)
                .endDisplay()
                .addInventoryChangedCriterion("got_" + mat.baseName + "_pickaxe_head", mat.equipment.pickaxeHead)
                .addInventoryChangedCriterion("got_" + mat.baseName + "_hoe_head", mat.equipment.hoeHead)
                .addInventoryChangedCriterion("got_" + mat.baseName + "_axe_head", mat.equipment.axeHead)
                .addInventoryChangedCriterion("got_" + mat.baseName + "_shovel_head", mat.equipment.shovelHead)
                .addInventoryChangedCriterion(
                        "got_" + mat.baseName + "_sword_head",
                        mat.equipment.swordBlade,
                        mat.equipment.swordHandle
                );
    }

    AdvancementManager.Builder addArmor(MetalMaterial mat) {
        return AdvancementManager.Builder
                .create(BetterEnd.C.mk(mat.baseName + "_armor"))
                .startDisplay(mat.equipment.get(ArmorSlot.CHESTPLATE_SLOT))
                .endDisplay()
                .addInventoryChangedCriterion(
                        "got_" + mat.baseName + "_helmet",
                        mat.equipment.get(ArmorSlot.HELMET_SLOT)
                )
                .addInventoryChangedCriterion(
                        "got_" + mat.baseName + "_chestplate",
                        mat.equipment.get(ArmorSlot.CHESTPLATE_SLOT)
                )
                .addInventoryChangedCriterion(
                        "got_" + mat.baseName + "_leggings",
                        mat.equipment.get(ArmorSlot.LEGGINGS_SLOT)
                )
                .addInventoryChangedCriterion("got_" + mat.baseName + "_boots", mat.equipment.get(ArmorSlot.BOOTS_SLOT));
    }
}
