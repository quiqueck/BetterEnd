package org.betterx.datagen.betterend.tags;



import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.registry.item.EndFoodItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.MaterialManager;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.TagManager;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemTagProvider extends WoverTagProvider.ForItems {
    public ItemTagProvider(ModCore modCore) {
        super(modCore);
    }

    public static final TagKey<Item> CAPE_SLOT = TagManager.ITEMS.makeTag(BetterEnd.TRINKETS_CORE, "chest/cape");

    @Override
    public void prepareTags(ItemTagBootstrapContext context) {
        // Food compost chances are no longer computed here: this provider only ever ran at datagen, so the
        // ComposterAPI.allowCompost registrations it used to make here never affected the shipped game (that
        // API populates a static map that is only consulted at composter-use time on a running server/client,
        // which datagen never spins up). The same chance is now attached at each food item's definition via
        // CompostableItemTrait (see EndItems.registerEndFood/registerEndDrink and the two direct
        // defineFoodItem builders in EndFoodItems), which is resolved at real runtime by bclib's
        // ComposterBlockMixin.
        context.add(ItemTags.BEACON_PAYMENT_ITEMS, EndEquipmentItems.AETERNIUM_SET.ingot);

        context.add(CommonItemTags.IRON_INGOTS, EndMetalBlocks.THALLASIUM.equipment.ingot);

        context.add(EndTags.ALLOYING_IRON, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
        context.add(EndTags.ALLOYING_GOLD, Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.RAW_GOLD);
        context.add(EndTags.ALLOYING_COPPER, Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);

        context.add(ItemTags.FISHES, EndFoodItems.END_FISH_RAW, EndFoodItems.END_FISH_COOKED);

/*
THALLASIUM = IRON
TERMINITE = DIAMOND
AETERNIUM > NETHERITE
 */
        context.add(EndTags.ANVIL_AETERNIUM_TOOL, EndEquipmentItems.AETERNIUM_SET.get(ToolSlot.HAMMER_SLOT));

        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndTags.ANVIL_AETERNIUM_TOOL);
        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndEquipmentItems.NETHERITE_HAMMER);

        context.add(EndTags.ANVIL_DIAMOND_TOOL, EndTags.ANVIL_NETHERITE_TOOL);
        context.add(
                EndTags.ANVIL_DIAMOND_TOOL,
                EndEquipmentItems.DIAMOND_HAMMER,
                EndMetalBlocks.TERMINITE.equipment.get(ToolSlot.HAMMER_SLOT)
        );

        context.add(EndTags.ANVIL_IRON_TOOL, EndTags.ANVIL_DIAMOND_TOOL);
        context.add(
                EndTags.ANVIL_IRON_TOOL,
                EndEquipmentItems.IRON_HAMMER,
                EndEquipmentItems.GOLDEN_HAMMER,
                EndMetalBlocks.THALLASIUM.equipment.get(ToolSlot.HAMMER_SLOT)
        );

        MaterialManager.stream().forEach(m -> m.registerItemTags(context));

        context.add(CAPE_SLOT, EndEquipmentItems.CRYSTALITE_ELYTRA, EndEquipmentItems.ARMORED_ELYTRA);
    }
}
