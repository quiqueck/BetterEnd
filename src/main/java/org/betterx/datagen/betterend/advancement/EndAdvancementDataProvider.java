package org.betterx.datagen.betterend.advancement;



import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.bclib.api.v2.advancement.AdvancementManager;
import org.betterx.bclib.api.v3.datagen.AdvancementDataProvider;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.advancements.BECriteria;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.EndTemplates;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;

import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EndAdvancementDataProvider extends AdvancementDataProvider {
    public EndAdvancementDataProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(List.of(BetterEnd.MOD_ID), output, registryLookup);
    }

    @Override
    @SuppressWarnings("removal")
    protected void bootstrap(HolderLookup.Provider lookup) {
        final HolderLookup.RegistryLookup<Structure> structures = lookup.lookupOrThrow(Registries.STRUCTURE);
        final HolderLookup.RegistryLookup<Biome> biomeLookup = lookup.lookupOrThrow(Registries.BIOME);
        Identifier root = AdvancementManager.Builder
                .create(BetterEnd.C.mk("root"))
                .startDisplay(EndTerrainBlocks.END_MYCELIUM)
                .frame(AdvancementType.TASK)
                .hideFromChat()
                // 1.21.2+ resolves the advancement background through the GUI sprite atlas, so it needs a
                // sprite id (no "textures/" prefix, no ".png"), not the old raw texture path.
                .background(Identifier.withDefaultNamespace("gui/advancements/backgrounds/end"))
                .endDisplay()
                .addCriterion(
                        "welcome",
                        PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location())
                )
                .requirements(Strategy.OR)
                .build();

        Identifier enterEnd = AdvancementManager.Builder
                .create(BetterEnd.C.mk("enter_end"))
                .startDisplay(EndTerrainBlocks.CAVE_MOSS)
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

        Identifier portal = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal"))
                .parent(enterEnd)
                .startDisplay(EndFunctionalBlocks.ETERNAL_PEDESTAL)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addAtStructureCriterion("eternal_portal", EndStructures.ETERNAL_PORTAL.getHolder(structures))
                .requirements(Strategy.OR)
                .build();

        Identifier portalOn = AdvancementManager.Builder
                .create(BetterEnd.C.mk("portal_on"))
                .parent(portal)
                .startDisplay(EndResourceItems.ETERNAL_CRYSTAL)
                .endDisplay()
                .addCriterion("turn_on", BECriteria.PORTAL_ON_TRIGGER)
                .requirements(Strategy.OR)
                .build();

        Identifier portalTravel = AdvancementManager.Builder
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
                .filter(id -> id.identifier().getNamespace().equals(BetterEnd.C.modId))
                .toList();

        if (!biomes.isEmpty()) {
            Identifier allTheBiomes = AdvancementManager.Builder
                    .create(BetterEnd.C.mk("all_the_biomes"))
                    .parent(enterEnd)
                    .startDisplay(EndEquipmentItems.AETERNIUM_SET.get(ArmorSlot.BOOTS_SLOT))
                    .frame(AdvancementType.CHALLENGE)
                    .endDisplay()
                    .addVisitBiomesCriterion(biomes
                            .stream()
                            .sorted(Comparator.comparing(ResourceKey::identifier))
                            .map(key -> (Holder<Biome>) biomeLookup.get(key).orElseThrow())
                            .toList())
                    .requirements(Strategy.AND)
                    .rewardXP(1500)
                    .build();


            Identifier village = AdvancementManager.Builder
                    .create(BetterEnd.C.mk("village"))
                    .parent(allTheBiomes)
                    .startDisplay(EndWoodBlocks.TENANEA.getBlock(WoodSlots.DOOR))
                    .frame(AdvancementType.GOAL)
                    .endDisplay()
                    .addAtStructureCriterion("end_village", EndStructures.END_VILLAGE.getHolder(structures))
                    .requirements(Strategy.OR)
                    .build();
        }

        Identifier allElytras = AdvancementManager.Builder
                .create(BetterEnd.C.mk("all_elytras"))
                .parent(enterEnd)
                .startDisplay(EndEquipmentItems.CRYSTALITE_ELYTRA)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addInventoryChangedCriterion("vanilla", Items.ELYTRA)
                .addInventoryChangedCriterion("crystalite", EndEquipmentItems.CRYSTALITE_ELYTRA)
                .addInventoryChangedCriterion("armored", EndEquipmentItems.ARMORED_ELYTRA)
                .requirements(Strategy.AND)
                .build();

        Identifier infusion = AdvancementManager.Builder
                .create(BetterEnd.C.mk("infusion"))
                .parent(enterEnd)
                .startDisplay(EndFunctionalBlocks.INFUSION_PEDESTAL)
                .endDisplay()
                .addInventoryChangedCriterion("infusion_pedestal", EndFunctionalBlocks.INFUSION_PEDESTAL)
                .requirements(Strategy.OR)
                .build();

        Identifier infusionFinished = AdvancementManager.Builder
                .create(BetterEnd.C.mk("infusion_finished"))
                .parent(infusion)
                .startDisplay(Items.ENDER_EYE)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addCriterion("finished", BECriteria.INFUSION_FINISHED_TRIGGER)
                .requirements(Strategy.OR)
                .build();


        Identifier allTheTemplates = AdvancementManager.Builder
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

        Identifier hammer = AdvancementManager.Builder
                .create(BetterEnd.C.mk("hammer"))
                .parent(enterEnd)
                .startDisplay(EndEquipmentItems.DIAMOND_HAMMER)
                .endDisplay()
                .addInventoryChangedCriterion("got_diamond_hammer", EndEquipmentItems.DIAMOND_HAMMER)
                .addInventoryChangedCriterion("got_thallasium_hammer", EndMetalBlocks.THALLASIUM.equipment.get(ToolSlot.HAMMER_SLOT))
                .addInventoryChangedCriterion("got_terminite_hammer", EndMetalBlocks.TERMINITE.equipment.get(ToolSlot.HAMMER_SLOT))
                .requirements(Strategy.OR)
                .build();

        Identifier thallasiumAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("thallasium_anvil"))
                .parent(hammer)
                .startDisplay(EndMetalBlocks.THALLASIUM.getBlock(MetalMaterial.ANVIL))
                .endDisplay()
                .addInventoryChangedCriterion("got_thallasium_anvil", EndMetalBlocks.THALLASIUM.getBlock(MetalMaterial.ANVIL))
                .requirements(Strategy.OR)
                .build();

        Identifier thallasiumPlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("thallasium_plate"))
                .parent(thallasiumAnvil)
                .startDisplay(EndMetalBlocks.THALLASIUM.equipment.forgedPlate)
                .endDisplay()
                .addInventoryChangedCriterion("got_thallasium_plate", EndMetalBlocks.THALLASIUM.equipment.forgedPlate)
                .requirements(Strategy.OR)
                .build();

        Identifier terminiteAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("terminite_anvil"))
                .parent(thallasiumAnvil)
                .startDisplay(EndMetalBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                .endDisplay()
                .addInventoryChangedCriterion("got_terminite_anvil", EndMetalBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                .requirements(Strategy.OR)
                .build();

        Identifier terminitePlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("terminite_plate"))
                .parent(terminiteAnvil)
                .startDisplay(EndMetalBlocks.TERMINITE.equipment.forgedPlate)
                .endDisplay()
                .addInventoryChangedCriterion("got_erminite_plate", EndMetalBlocks.TERMINITE.equipment.forgedPlate)
                .requirements(Strategy.OR)
                .build();

        Identifier aeterniumAnvil = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_anvil"))
                .parent(terminiteAnvil)
                .startDisplay(EndFunctionalBlocks.AETERNIUM_ANVIL)
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_anvil", EndFunctionalBlocks.AETERNIUM_ANVIL)
                .requirements(Strategy.OR)
                .rewardXP(500)
                .build();

        Identifier aeterniumHammerHead = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_hammer_head"))
                .parent(aeterniumAnvil)
                .startDisplay(EndEquipmentItems.AETERNIUM_SET.hammerHead)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_hammer_head", EndEquipmentItems.AETERNIUM_SET.hammerHead)
                .requirements(Strategy.OR)
                .build();

        Identifier aeterniumHammer = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_hammer"))
                .parent(aeterniumHammerHead)
                .startDisplay(EndEquipmentItems.AETERNIUM_SET.get(ToolSlot.HAMMER_SLOT))
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_hammer", EndEquipmentItems.AETERNIUM_SET.get(ToolSlot.HAMMER_SLOT))
                .requirements(Strategy.OR)
                .build();

        Identifier aeterniumPlate = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_plate"))
                .parent(aeterniumHammer)
                .startDisplay(EndEquipmentItems.AETERNIUM_SET.forgedPlate)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .addInventoryChangedCriterion("got_aeternium_plate", EndEquipmentItems.AETERNIUM_SET.forgedPlate)
                .requirements(Strategy.OR)
                .rewardXP(200)
                .build();

        Identifier thallasiumArmor = addArmor(EndMetalBlocks.THALLASIUM)
                .parent(thallasiumPlate)
                .requirements(Strategy.OR)
                .build();

        Identifier thallasiumHead = addToolHeads(EndMetalBlocks.THALLASIUM)
                .parent(thallasiumAnvil)
                .requirements(Strategy.OR)
                .build();

        Identifier thallasium = addTools(EndMetalBlocks.THALLASIUM)
                .parent(thallasiumHead)
                .requirements(Strategy.OR)
                .build();

        Identifier terminiteHead = addToolHeads(EndMetalBlocks.TERMINITE)
                .parent(terminiteAnvil)
                .requirements(Strategy.OR)
                .build();

        Identifier terminite = addTools(EndMetalBlocks.TERMINITE)
                .parent(terminiteHead)
                .requirements(Strategy.OR)
                .build();

        Identifier terminiteArmor = addArmor(EndMetalBlocks.TERMINITE)
                .parent(terminitePlate)
                .requirements(Strategy.OR)
                .build();


        Identifier aeterniumHead = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_tool_head"))
                .startDisplay(EndEquipmentItems.AETERNIUM_SET.pickaxeHead)
                .frame(AdvancementType.GOAL)
                .endDisplay()
                .parent(aeterniumHammer)
                .addInventoryChangedCriterion("got_aeternium_pickaxe_head", EndEquipmentItems.AETERNIUM_SET.pickaxeHead)
                .addInventoryChangedCriterion("got_aeternium_hoe_head", EndEquipmentItems.AETERNIUM_SET.hoeHead)
                .addInventoryChangedCriterion("got_aeternium_axe_head", EndEquipmentItems.AETERNIUM_SET.axeHead)
                .addInventoryChangedCriterion("got_aeternium_shovel_head", EndEquipmentItems.AETERNIUM_SET.shovelHead)
                .addInventoryChangedCriterion(
                        "got_aeternium_sword_head",
                        EndEquipmentItems.AETERNIUM_SET.swordBlade,
                        EndEquipmentItems.AETERNIUM_SET.swordHandle
                )
                .requirements(Strategy.AND)
                .rewardXP(200)
                .build();

        Identifier aeternium = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_tool"))
                .startDisplay(EndEquipmentItems.AETERNIUM_SET.pickaxe())
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .parent(aeterniumHead)
                .addInventoryChangedCriterion("got_aeternium_pickaxe", EndEquipmentItems.AETERNIUM_SET.pickaxe())
                .addInventoryChangedCriterion("got_aeternium_hoe", EndEquipmentItems.AETERNIUM_SET.get(ToolSlot.HOE_SLOT))
                .addInventoryChangedCriterion("got_aeternium_axe", EndEquipmentItems.AETERNIUM_SET.axe())
                .addInventoryChangedCriterion("got_aeternium_shovel", EndEquipmentItems.AETERNIUM_SET.get(ToolSlot.SHOVEL_SLOT))
                .addInventoryChangedCriterion("got_aeternium_sword", EndEquipmentItems.AETERNIUM_SET.get(ToolSlot.SWORD_SLOT))
                .requirements(Strategy.AND)
                .rewardXP(2000)
                .build();

        Identifier aeterniumArmor = AdvancementManager.Builder
                .create(BetterEnd.C.mk("aeternium_armor"))
                .startDisplay(EndEquipmentItems.AETERNIUM_SET.get(ArmorSlot.CHESTPLATE_SLOT))
                .frame(AdvancementType.CHALLENGE)
                .endDisplay()
                .parent(aeterniumPlate)
                .addInventoryChangedCriterion("got_aeternium_helmet", EndEquipmentItems.AETERNIUM_SET.get(ArmorSlot.HELMET_SLOT))
                .addInventoryChangedCriterion("got_aeternium_chestplate", EndEquipmentItems.AETERNIUM_SET.get(ArmorSlot.CHESTPLATE_SLOT))
                .addInventoryChangedCriterion("got_aeternium_leggings", EndEquipmentItems.AETERNIUM_SET.get(ArmorSlot.LEGGINGS_SLOT))
                .addInventoryChangedCriterion("got_aeternium_boots", EndEquipmentItems.AETERNIUM_SET.get(ArmorSlot.BOOTS_SLOT))
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
