package org.betterx.betterend.integration.rei;


import org.betterx.betterend.registry.block.EndFunctionalBlocks;
//import org.betterx.bclib.blocks.BaseFurnaceBlock;
//import org.betterx.bclib.recipes.AlloyingRecipe;
//import org.betterx.bclib.recipes.AnvilRecipe;
//import org.betterx.betterend.BetterEnd;
//import org.betterx.betterend.blocks.basis.EndAnvilBlock;
//import org.betterx.betterend.recipe.builders.InfusionRecipe;
//import org.betterx.betterend.registry.EndBlocks;
//
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.BlastingRecipe;
//import net.minecraft.world.item.crafting.RecipeType;
//import net.minecraft.world.level.block.Blocks;
//
//import net.fabricmc.fabric.api.registry.FuelRegistry;
//import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
//
//import com.google.common.collect.Lists;
//import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
//import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
//import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//import me.shedaniel.rei.api.common.entry.EntryIngredient;
//import me.shedaniel.rei.api.common.entry.EntryStack;
//import me.shedaniel.rei.api.common.util.EntryIngredients;
//import me.shedaniel.rei.api.common.util.EntryStacks;
//import me.shedaniel.rei.plugin.common.BuiltinPlugin;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;


/// /https://github.com/shedaniel/RoughlyEnoughItems/blob/6.x-1.17/default-plugin/src/main/java/me/shedaniel/rei/plugin/client/DefaultClientPlugin.java
//public class REIPlugin implements REIClientPlugin {
//    public final static ResourceLocation PLUGIN_ID = BetterEnd.C.mk("rei_plugin");
//    public final static CategoryIdentifier<REIAlloyingFuelDisplay> ALLOYING_FUEL = CategoryIdentifier.of(
//            BetterEnd.MOD_ID,
//            "alloying_fuel"
//    );
//    public final static CategoryIdentifier<REIAlloyingDisplay> ALLOYING = CategoryIdentifier.of(
//            BetterEnd.MOD_ID,
//            AlloyingRecipe.GROUP
//    );
//    public final static CategoryIdentifier<REIAnvilDisplay> SMITHING = CategoryIdentifier.of(
//            BetterEnd.MOD_ID,
//            AnvilRecipe.ID.getPath()
//    );
//    public final static CategoryIdentifier<REIInfusionDisplay> INFUSION = CategoryIdentifier.of(
//            BetterEnd.MOD_ID,
//            InfusionRecipe.GROUP
//    );
//
//    @Override
//    public void registerDisplays(DisplayRegistry registry) {
//        registry.registerRecipeFiller(AlloyingRecipe.class, AlloyingRecipe.TYPE, REIAlloyingDisplay::new);
//        registry.registerRecipeFiller(BlastingRecipe.class, RecipeType.BLASTING, REIBlastingDisplay::new);
//        registry.registerRecipeFiller(AnvilRecipe.class, AnvilRecipe.TYPE, REIAnvilDisplay::new);
//        registry.registerRecipeFiller(InfusionRecipe.class, InfusionRecipe.TYPE, REIInfusionDisplay::new);
//
//        if (FuelRegistry.INSTANCE instanceof FuelRegistryImpl fabricImpl) {
//            fabricImpl.getFuelTimes().forEach((item, time) -> {
//                if (time >= 2000) {
//                    final List<EntryIngredient> list = Collections.singletonList(EntryIngredients.of(item));
//                    registry.add(new REIAlloyingFuelDisplay(list, time));
//                }
//            });
//        }
//    }
//
//    @Override
//    public void registerCategories(CategoryRegistry registry) {
//        EntryStack<ItemStack> endStoneSmelter = EntryStacks.of(EndFunctionalBlocks.END_STONE_SMELTER);
//        EntryStack<ItemStack> infusionRitual = EntryStacks.of(EndFunctionalBlocks.INFUSION_PEDESTAL);
//        List<EntryStack<?>> anvils = Lists.newArrayList(EntryIngredients.ofItems(EndBlocks.getModBlocks()
//                                                                                          .stream()
//                                                                                          .filter(EndAnvilBlock.class::isInstance)
//                                                                                          .collect(Collectors.toList())));
//        anvils.add(0, EntryStacks.of(Blocks.ANVIL));
//        List<EntryStack<?>> ITEM_FURNACES = Lists.newArrayList(EntryIngredients.ofItems(EndBlocks.getModBlocks()
//                                                                                                 .stream()
//                                                                                                 .filter(BaseFurnaceBlock.class::isInstance)
//                                                                                                 .collect(Collectors.toList())));
//        EntryStack<?>[] anvilsArray = anvils.toArray(new EntryStack[0]);
//        EntryStack<?>[] ITEM_FURNACESArray = ITEM_FURNACES.toArray(new EntryStack[0]);
//
//        registry.add(
//                new REIAlloyingFuelCategory(),
//                new REIAlloyingCategory(endStoneSmelter),
//                new REIInfusionCategory(infusionRitual),
//                new REIAnvilCategory(anvilsArray)
//        );
//
//        registry.addWorkstations(ALLOYING_FUEL, endStoneSmelter);
//        registry.addWorkstations(ALLOYING, endStoneSmelter);
//        registry.addWorkstations(INFUSION, infusionRitual);
//        registry.addWorkstations(SMITHING, anvilsArray);
////        registry.removePlusButton(ALLOYING_FUEL);
////        registry.removePlusButton(SMITHING);
//
//        registry.addWorkstations(BuiltinPlugin.SMELTING, ITEM_FURNACESArray);
//        registry.addWorkstations(BuiltinPlugin.FUEL, ITEM_FURNACESArray);
//    }
//}