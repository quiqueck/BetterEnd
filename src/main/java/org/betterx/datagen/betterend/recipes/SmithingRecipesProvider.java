package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class SmithingRecipesProvider extends WoverRecipeProvider {
    public SmithingRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Smithing Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_sword_handle"), EndItems.AETERNIUM_SET.swordHandle)
                     .template(EndTemplates.TERMINITE_UPGRADE)
                     .base(EndItems.LEATHER_WRAPPED_STICK)
                     .addon(EndBlocks.TERMINITE.equipment.ingot)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("thallasium_anvil_updrade"), EndBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                     .template(EndTemplates.TERMINITE_UPGRADE)
                     .base(EndBlocks.THALLASIUM.getBlock(MetalMaterial.ANVIL))
                     .addon(EndBlocks.TERMINITE.equipment.ingot)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("terminite_anvil_updrade"), EndBlocks.AETERNIUM_ANVIL)
                     .template(EndTemplates.AETERNIUM_UPGRADE)
                     .base(EndBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                     .addon(EndItems.AETERNIUM_SET.ingot)
                     .build(context);


        RecipeBuilder.smithing(BetterEnd.C.mk("armored_elytra"), EndItems.ARMORED_ELYTRA)
                     .template(EndTemplates.AETERNIUM_UPGRADE)
                     .base(Items.ELYTRA)
                     .addon(EndItems.AETERNIUM_SET.ingot)
                     .build(context);


        RecipeBuilder.smithing(BetterEnd.C.mk("netherite_hammer"), EndItems.NETHERITE_HAMMER)
                     .template(EndTemplates.NETHERITE_UPGRADE)
                     .base(EndItems.DIAMOND_HAMMER)
                     .addon(Items.NETHERITE_INGOT)
                     .build(context);


        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_plate_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.PLATE_UPGRADE,
                Items.IRON_INGOT
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_leather_handle_attachment"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.LEATHER_HANDLE_ATTACHMENT,
                Items.LEATHER
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_handle_attachment"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.HANDLE_ATTACHMENT,
                Items.DIAMOND
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_terminite_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.TERMINITE_UPGRADE,
                EndBlocks.TERMINITE.equipment.ingot
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_thallasium_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.THALLASIUM_UPGRADE,
                EndBlocks.THALLASIUM.equipment.ingot
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_aeternium_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.AETERNIUM_UPGRADE,
                Blocks.LAPIS_BLOCK
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_tool_assembly"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.TOOL_ASSEMBLY,
                Blocks.IRON_BLOCK
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_netherite_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.REGULAR,
                EndTemplates.NETHERITE_UPGRADE,
                Blocks.NETHERRACK
        ).build(context);
    }
}
