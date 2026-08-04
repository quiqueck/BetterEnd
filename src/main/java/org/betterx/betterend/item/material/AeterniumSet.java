package org.betterx.betterend.item.material;



import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.registry.EndTemplates;
import de.ambertation.wover.complex.api.equipment.ToolSlot;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

public class AeterniumSet extends ToolsWithHeadsSet {
    public AeterniumSet() {
        super(
                "aeternium",
                EndToolTier.AETERNIUM,
                EndArmorTier.AETERNIUM,
                EndResourceItems.LEATHER_WRAPPED_STICK,
                () -> EndTemplates.LEATHER_HANDLE_ATTACHMENT,
                () -> EndTemplates.TOOL_ASSEMBLY,
                false,
                EndTags.ANVIL_NETHERITE_TOOL,
                () -> EndMetalBlocks.TERMINITE.equipment
        );

    }


//    @Override
//    protected void buildArmor() {
//        add(
//                ArmorSlot.HELMET_SLOT,
//                ItemTraits.RECIPE_ITEM.with(
//                        (key, item, context) -> {
//                            RecipeBuilder.smithing(key.location(), item)
//                                         .template(EndTemplates.PLATE_UPGRADE)
//                                         .base(EndMetalBlocks.TERMINITE.helmet)
//                                         .addon(EndItems.AETERNIUM_FORGED_PLATE)
//                                         .build(context);
//                        }
//                )
//        );
//        add(
//                ArmorSlot.CHESTPLATE_SLOT,
//                ItemTraits.RECIPE_ITEM.with(
//                        (key, item, context) -> {
//                            RecipeBuilder.smithing(key.location(), item)
//                                         .template(EndTemplates.PLATE_UPGRADE)
//                                         .base(EndMetalBlocks.TERMINITE.chestplate)
//                                         .addon(EndItems.AETERNIUM_FORGED_PLATE)
//                                         .build(context);
//                        }
//                )
//        );
//        add(
//                ArmorSlot.LEGGINGS_SLOT,
//                ItemTraits.RECIPE_ITEM.with(
//                        (key, item, context) -> {
//                            RecipeBuilder.smithing(key.location(), item)
//                                         .template(EndTemplates.PLATE_UPGRADE)
//                                         .base(EndMetalBlocks.TERMINITE.leggings)
//                                         .addon(EndItems.AETERNIUM_FORGED_PLATE)
//                                         .build(context);
//                        }
//                )
//        );
//        add(
//                ArmorSlot.BOOTS_SLOT,
//                ItemTraits.RECIPE_ITEM.with(
//                        (key, item, context) -> {
//                            RecipeBuilder.smithing(key.location(), item)
//                                         .template(EndTemplates.PLATE_UPGRADE)
//                                         .base(EndMetalBlocks.TERMINITE.boots)
//                                         .addon(EndItems.AETERNIUM_FORGED_PLATE)
//                                         .build(context);
//                        }
//                )
//        );
//    }

    @Override
    public @NotNull Item.Properties commonToolProperties(Item.@NotNull Properties properties) {
        return super.commonToolProperties(properties).fireResistant();
    }

    @Override
    protected boolean hasAutoHeadRecipes() {
        // Aeternium's head/blade recipes are hand-written in AnvilRecipesProvider: one ingot each, damage 6 and
        // diamond-level tools for the sword blade. The generic trait would instead ask for up to three ingots,
        // set the damage to the tool tier and allow netherite tools for the blade, so let the provider own them.
        return false;
    }

    public @NotNull Item pickaxe() {
        return this.get(ToolSlot.PICKAXE_SLOT);
    }

    public @NotNull Item axe() {
        return this.get(ToolSlot.AXE_SLOT);
    }
}
