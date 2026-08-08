package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.complexmaterials.EndWoodenComplexMaterial;
import org.betterx.betterend.registry.block.EndWoodBlocks;

import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.test.api.gametest.FurnitureSweep;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Covers vanilla-parity storage/workstation slots and sitting furniture across every BetterEnd wood
 * material, generically - one assertion per material rather than one test class per wood.
 * <p>
 * <b>Correction to the original checklist item</b>: crafting tables are not vanilla villager POI, and
 * nothing in this codebase makes them one - only {@code CraftingTableBlockBuilder} adding
 * {@code CommonBlockTags.WORKBENCHES} exists, no POI tag. That item as originally phrased was based on
 * a false premise, so this class checks barrel (fisherman) and composter (farmer) POI instead, which
 * genuinely are wired via WorldWeaver's POI mixin (a common tag - {@code #c:barrels}/
 * {@code #wover:composters} - that vanilla's own workstation POI types already reference).
 */
public class FurnitureParityGameTest {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    private static final Map<String, EndWoodenComplexMaterial> MATERIALS = Map.ofEntries(
            Map.entry("mossy_glowshroom", EndWoodBlocks.MOSSY_GLOWSHROOM),
            Map.entry("pythadendron", EndWoodBlocks.PYTHADENDRON),
            Map.entry("end_lotus", EndWoodBlocks.END_LOTUS),
            Map.entry("lacugrove", EndWoodBlocks.LACUGROVE),
            Map.entry("dragon_tree", EndWoodBlocks.DRAGON_TREE),
            Map.entry("tenanea", EndWoodBlocks.TENANEA),
            Map.entry("helix_tree", EndWoodBlocks.HELIX_TREE),
            Map.entry("umbrella_tree", EndWoodBlocks.UMBRELLA_TREE),
            Map.entry("jellyshroom", EndWoodBlocks.JELLYSHROOM),
            Map.entry("lucernia", EndWoodBlocks.LUCERNIA),
            Map.entry("lucernia_jelly", EndWoodBlocks.LUCERNIA_JELLY)
    );

    @GameTest
    public void everyMaterialHasWorkingStorageAndWorkstationSlots(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        MATERIALS.forEach((name, material) -> {
            // lucernia_jellyshroom deliberately has no wood set of its own - JellyLucerniaWoodMaterial's
            // own javadoc explains it only defines a hanging sign and furniture, skipping
            // super.createDefaultDefinitions() on purpose. Not a gap to report.
            if (name.equals("lucernia_jelly")) return;

            // SlotType.SHELF is a vanilla 1.21.9-style ShelfBlock slot - that block type does not exist
            // on this (1.21.6-1.21.8) branch at all, so it's dropped from this check here; every other
            // slot below is unaffected.
            for (SlotType slot : List.of(
                    SlotType.CHEST, SlotType.BARREL, SlotType.COMPOSTER,
                    SlotType.CRAFTING_TABLE, SlotType.BOOKSHELF
            )) {
                if (material.getBlock(slot) == null) {
                    failures.add(name + ": missing a " + slot + " block");
                }
            }
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Storage/workstation slot regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void barrelIsAFishermanPoiAndComposterIsAFarmerPoi(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        MATERIALS.forEach((name, material) -> {
            checkPoi(helper, material.getBlock(SlotType.BARREL), PoiTypes.FISHERMAN, name + " barrel", failures);
            checkPoi(helper, material.getBlock(SlotType.COMPOSTER), PoiTypes.FARMER, name + " composter", failures);
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Villager POI regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static void checkPoi(
            GameTestHelper helper,
            Block block,
            ResourceKey<PoiType> expectedType,
            String label,
            List<String> failures
    ) {
        if (block == null) return; // already reported by the other test
        final BlockState state = block.defaultBlockState();
        final var poiType = PoiTypes.forState(state);
        if (poiType.isEmpty()) {
            failures.add(label + " is not registered as any villager POI type");
            return;
        }
        if (!poiType.get().is(expectedType)) {
            failures.add(label + " is a POI, but not " + expectedType.location());
        }
    }

    @GameTest
    public void chairBarstoolAndTaburetCanBeSatOn(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        MATERIALS.forEach((name, material) -> {
            checkSittable(helper, material.getBlock(SlotType.CHAIR), name + " chair", failures);
            checkSittable(helper, material.getBlock(SlotType.BAR_STOOL), name + " bar stool", failures);
            checkSittable(helper, material.getBlock(SlotType.TABURET), name + " taburet", failures);
        });

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "Sittable-furniture regression:\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }

    private static void checkSittable(GameTestHelper helper, Block block, String label, List<String> failures) {
        if (block == null) {
            failures.add(label + " does not exist");
            return;
        }
        helper.setBlock(POS, block);
        final String failure = FurnitureSweep.assertPlayerCanSit(helper, POS, label);
        if (failure != null) {
            failures.add(failure);
        }
    }
}
