package org.betterx.datagen.betterend.recipes;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.material.EndToolTier;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;

public class AnvilRecipesProvider extends WoverRecipeProvider {
    public AnvilRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Anvil Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("ender_pearl_to_dust"), EndItems.ENDER_DUST)
                        .setPrimaryInputAndUnlock(Items.ENDER_PEARL)
                        .setAnvilLevel(EndToolTier.THALLASIUM.level)
                        .setAllowedTools(EndTags.ANVIL_IRON_TOOL)
                        .setDamage(5)
                        .build(context);

        BCLRecipeBuilder.anvil(BetterEnd.C.mk("ender_shard_to_dust"), EndItems.ENDER_DUST)
                        .setPrimaryInputAndUnlock(EndItems.ENDER_SHARD)
                        .setAnvilLevel(EndToolTier.THALLASIUM.level)
                        .setAllowedTools(EndTags.ANVIL_IRON_TOOL)
                        .setDamage(3)
                        .build(context);

        final int anvilLevel = EndToolTier.AETERNIUM.level;
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_axe_head"), EndItems.AETERNIUM_SET.axeHead)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_pickaxe_head"), EndItems.AETERNIUM_SET.pickaxeHead)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_shovel_head"), EndItems.AETERNIUM_SET.shovelHead)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_hoe_head"), EndItems.AETERNIUM_SET.hoeHead)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_hammer_head"), EndItems.AETERNIUM_SET.hammerHead)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_sword_blade"), EndItems.AETERNIUM_SET.swordBlade)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_DIAMOND_TOOL)
                        .setDamage(6)
                        .build(context);
        BCLRecipeBuilder.anvil(BetterEnd.C.mk("aeternium_forged_plate"), EndItems.AETERNIUM_SET.forgedPlate)
                        .setPrimaryInputAndUnlock(EndItems.AETERNIUM_SET.ingot)
                        .setAnvilLevel(anvilLevel)
                        .setAllowedTools(EndTags.ANVIL_NETHERITE_TOOL)
                        .setDamage(6)
                        .build(context);
    }
}
