package org.betterx.datagen.betterend.recipes;



import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class SmithingRecipesProvider extends WoverRecipeProvider {
    public SmithingRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Smithing Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.smithing(BetterEnd.C.mk("aeternium_sword_handle"), EndEquipmentItems.AETERNIUM_SET.swordHandle)
                     .template(EndTemplates.TERMINITE_UPGRADE)
                     .base(EndResourceItems.LEATHER_WRAPPED_STICK)
                     .addon(EndMetalBlocks.TERMINITE.equipment.ingot)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("thallasium_anvil_updrade"), EndMetalBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                     .template(EndTemplates.TERMINITE_UPGRADE)
                     .base(EndMetalBlocks.THALLASIUM.getBlock(MetalMaterial.ANVIL))
                     .addon(EndMetalBlocks.TERMINITE.equipment.ingot)
                     .build(context);

        RecipeBuilder.smithing(BetterEnd.C.mk("terminite_anvil_updrade"), EndFunctionalBlocks.AETERNIUM_ANVIL)
                     .template(EndTemplates.AETERNIUM_UPGRADE)
                     .base(EndMetalBlocks.TERMINITE.getBlock(MetalMaterial.ANVIL))
                     .addon(EndEquipmentItems.AETERNIUM_SET.ingot)
                     .build(context);


        RecipeBuilder.smithing(BetterEnd.C.mk("armored_elytra"), EndEquipmentItems.ARMORED_ELYTRA)
                     .template(EndTemplates.AETERNIUM_UPGRADE)
                     .base(Items.ELYTRA)
                     .addon(EndEquipmentItems.AETERNIUM_SET.ingot)
                     .build(context);


        RecipeBuilder.smithing(BetterEnd.C.mk("netherite_hammer"), EndEquipmentItems.NETHERITE_HAMMER)
                     .template(EndTemplates.NETHERITE_UPGRADE)
                     .base(EndEquipmentItems.DIAMOND_HAMMER)
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
                EndMetalBlocks.TERMINITE.equipment.ingot
        ).build(context);

        RecipeBuilder.copySmithingTemplate(
                BetterEnd.C.mk("copy_thallasium_upgrade"),
                RecipeBuilder.CopySmithingTemplateCostLevel.CHEAP,
                EndTemplates.THALLASIUM_UPGRADE,
                EndMetalBlocks.THALLASIUM.equipment.ingot
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
