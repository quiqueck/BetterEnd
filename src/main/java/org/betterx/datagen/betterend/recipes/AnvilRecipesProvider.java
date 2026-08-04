package org.betterx.datagen.betterend.recipes;


import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.material.EndToolTier;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTags;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;

public class AnvilRecipesProvider extends WoverRecipeProvider {
    public AnvilRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Anvil Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("ender_pearl_to_dust"), EndResourceItems.ENDER_DUST)
                        .setPrimaryInputAndUnlock(Items.ENDER_PEARL)
                        .setAnvilLevel(EndToolTier.THALLASIUM.level)
                        .setAllowedTools(EndTags.ANVIL_IRON_TOOL)
                        .setDamage(5)
                        .build(context);

        BCLRecipeBuilder.anvil(BetterEnd.C.mk("ender_shard_to_dust"), EndResourceItems.ENDER_DUST)
                        .setPrimaryInputAndUnlock(EndResourceItems.ENDER_SHARD)
                        .setAnvilLevel(EndToolTier.THALLASIUM.level)
                        .setAllowedTools(EndTags.ANVIL_IRON_TOOL)
                        .setDamage(3)
                        .build(context);

        final int anvilLevel = EndToolTier.AETERNIUM.level;
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_axe_head"), EndEquipmentItems.AETERNIUM_SET.axeHead)
                        .setPrimaryInputAndUnlock(EndEquipmentItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_pickaxe_head"), EndEquipmentItems.AETERNIUM_SET.pickaxeHead)
                        .setPrimaryInputAndUnlock(EndEquipmentItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_shovel_head"), EndEquipmentItems.AETERNIUM_SET.shovelHead)
                        .setPrimaryInputAndUnlock(EndEquipmentItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_hoe_head"), EndEquipmentItems.AETERNIUM_SET.hoeHead)
                        .setPrimaryInputAndUnlock(EndEquipmentItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_hammer_head"), EndEquipmentItems.AETERNIUM_SET.hammerHead)
                        .setPrimaryInputAndUnlock(EndEquipmentItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_sword_blade"), EndEquipmentItems.AETERNIUM_SET.swordBlade)
                        .setPrimaryInputAndUnlock(EndEquipmentItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_DIAMOND_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_forged_plate"), EndEquipmentItems.AETERNIUM_SET.forgedPlate)
                        .setPrimaryInputAndUnlock(EndEquipmentItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
    }
}
