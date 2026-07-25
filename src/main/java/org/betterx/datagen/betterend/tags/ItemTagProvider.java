package org.betterx.datagen.betterend.tags;

import org.betterx.bclib.api.v2.ComposterAPI;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.MaterialManager;
import org.betterx.betterend.item.tool.EndHammerItem;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemTagProvider extends WoverTagProvider.ForItems {
    public ItemTagProvider(ModCore modCore) {
        super(modCore);
    }

    public static final TagKey<Item> CAPE_SLOT = TagManager.ITEMS.makeTag(BetterEnd.TRINKETS_CORE, "chest/cape");

    @Override
    public void prepareTags(ItemTagBootstrapContext context) {
        EndItems.getModItems().forEach(item -> {
            FoodProperties food = item.components().get(DataComponents.FOOD);
            if (food != null) {
                float compost = food.nutrition() * food.saturation() * 0.18F;
                ComposterAPI.allowCompost(compost, item);
            }

            if (item instanceof EndHammerItem) {
                context.add(CommonItemTags.HAMMERS, item);
            }
        });

        context.add(ItemTags.BEACON_PAYMENT_ITEMS, EndItems.AETERNIUM_INGOT);

        context.add(CommonItemTags.IRON_INGOTS, EndBlocks.THALLASIUM.ingot);

        context.add(EndTags.ALLOYING_IRON, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
        context.add(EndTags.ALLOYING_GOLD, Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.RAW_GOLD);
        context.add(EndTags.ALLOYING_COPPER, Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);

        context.add(ItemTags.FISHES, EndItems.END_FISH_RAW, EndItems.END_FISH_COOKED);

/*
THALLASIUM = IRON
TERMINITE = DIAMOND
AETERNIUM > NETHERITE
 */
        context.add(EndTags.ANVIL_AETERNIUM_TOOL, EndItems.AETERNIUM_HAMMER);

        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndTags.ANVIL_AETERNIUM_TOOL);
        context.add(EndTags.ANVIL_NETHERITE_TOOL, EndItems.NETHERITE_HAMMER);

        context.add(EndTags.ANVIL_DIAMOND_TOOL, EndTags.ANVIL_NETHERITE_TOOL);
        context.add(EndTags.ANVIL_DIAMOND_TOOL, EndItems.DIAMOND_HAMMER, EndBlocks.TERMINITE.hammer);

        context.add(EndTags.ANVIL_IRON_TOOL, EndTags.ANVIL_DIAMOND_TOOL);
        context.add(EndTags.ANVIL_IRON_TOOL, EndItems.IRON_HAMMER, EndItems.GOLDEN_HAMMER, EndBlocks.THALLASIUM.hammer);

        MaterialManager.stream().forEach(m -> m.registerItemTags(context));

        context.add(CAPE_SLOT, Items.ELYTRA, EndItems.CRYSTALITE_ELYTRA, EndItems.ARMORED_ELYTRA);
    }
}
