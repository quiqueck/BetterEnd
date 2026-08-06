package org.betterx.datagen.betterend.recipes;



import org.betterx.betterend.registry.block.EndCropBlocks;
import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndLightBlocks;
import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.betterend.registry.block.EndWallPlantBlocks;
import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.registry.item.EndEquipmentItems;
import org.betterx.betterend.registry.item.EndFoodItems;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.types.FlowerPot;
import org.betterx.betterend.complexmaterials.types.Pedestal;
import org.betterx.betterend.complexmaterials.types.StoneLantern;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.CraftingRecipeBuilder;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class EndCraftingRecipesProvider extends WoverRecipeProvider {
    public EndCraftingRecipesProvider(ModCore modCore) {
        super(modCore, "BetterEnd - Crafting Recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        CraftingRecipeBuilder craftingRecipeBuilder62 = RecipeBuilder.crafting(
                BetterEnd.C.mk("ender_perl_to_block"),
                EndMetalBlocks.ENDER_BLOCK
        );
        craftingRecipeBuilder62.shape("OO", "OO")
                               .addMaterial('O', Items.ENDER_PEARL)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder27 = RecipeBuilder
                .crafting(BetterEnd.C.mk("ender_block_to_perl"), Items.ENDER_PEARL)
                .addMaterial('#', EndMetalBlocks.ENDER_BLOCK);
        craftingRecipeBuilder27.outputCount(4)
                               .shapeless()
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder61 = RecipeBuilder.crafting(
                BetterEnd.C.mk("end_stone_smelter"),
                EndFunctionalBlocks.END_STONE_SMELTER
        );
        craftingRecipeBuilder61.shape("T#T", "V V", "T#T")
                               .addMaterial('#', Blocks.END_STONE_BRICKS)
                               .addMaterial('T', EndMetalBlocks.THALLASIUM.equipment.ingot)
                               .addMaterial('V', CommonItemTags.FURNACES)
                               .build(context);

        registerPedestal(
                context,
                "andesite_pedestal",
                EndStoneBlocks.ANDESITE_SET.getBlock(Pedestal.SLOT),
                Blocks.POLISHED_ANDESITE_SLAB,
                Blocks.POLISHED_ANDESITE
        );
        registerPedestal(
                context,
                "diorite_pedestal",
                EndStoneBlocks.DIORITE_SET.getBlock(Pedestal.SLOT),
                Blocks.POLISHED_DIORITE_SLAB,
                Blocks.POLISHED_DIORITE
        );
        registerPedestal(
                context,
                "granite_pedestal",
                EndStoneBlocks.GRANITE_SET.getBlock(Pedestal.SLOT),
                Blocks.POLISHED_GRANITE_SLAB,
                Blocks.POLISHED_GRANITE
        );
        registerPedestal(
                context,
                "quartz_pedestal",
                EndStoneBlocks.QUARTZ_SET.getBlock(Pedestal.SLOT),
                Blocks.QUARTZ_SLAB,
                Blocks.QUARTZ_PILLAR
        );
        registerPedestal(
                context,
                "purple_pedestal",
                EndStoneBlocks.PURPUR_SET.getBlock(Pedestal.SLOT),
                Blocks.PURPUR_SLAB,
                Blocks.PURPUR_PILLAR
        );

        CraftingRecipeBuilder craftingRecipeBuilder60 = RecipeBuilder.crafting(
                BetterEnd.C.mk("infusion_pedestal"),
                EndFunctionalBlocks.INFUSION_PEDESTAL
        );
        craftingRecipeBuilder60.shape(" Y ", "O#O", " # ")
                               .addMaterial('O', Items.ENDER_PEARL)
                               .addMaterial('Y', Items.ENDER_EYE)
                               .addMaterial('#', Blocks.OBSIDIAN)
                               .build(context);

        String material = "aeternium";
        CraftingRecipeBuilder craftingRecipeBuilder59 = RecipeBuilder.crafting(
                BetterEnd.C.mk(material + "_block"),
                EndMetalBlocks.AETERNIUM_BLOCK
        );
        craftingRecipeBuilder59.shape("III", "III", "III")
                               .addMaterial('I', EndEquipmentItems.AETERNIUM_SET.ingot)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder26 = RecipeBuilder
                .crafting(BetterEnd.C.mk(material + "_block_to_ingot"), EndEquipmentItems.AETERNIUM_SET.ingot)
                .addMaterial('#', EndMetalBlocks.AETERNIUM_BLOCK);
        craftingRecipeBuilder26.outputCount(9)
                               .shapeless()
                               .build(context);

        // 26.2 added a vanilla sulfur spike. Grinding one down yields the same four crystalline
        // sulphur a spike is worth, and four of them pack back into a spike - so the two recipes are
        // a lossless round trip rather than a source or a sink.
        CraftingRecipeBuilder sulfurSpikeToSulphur = RecipeBuilder
                .crafting(BetterEnd.C.mk("sulfur_spike_to_crystalline_sulphur"), EndResourceItems.CRYSTALLINE_SULPHUR)
                .addMaterial('#', Blocks.SULFUR_SPIKE);
        sulfurSpikeToSulphur.outputCount(4)
                            .shapeless()
                            .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("crystalline_sulphur_to_sulfur_spike"), Blocks.SULFUR_SPIKE)
                     .shape("OO", "OO")
                     .addMaterial('O', EndResourceItems.CRYSTALLINE_SULPHUR)
                     .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("blue_vine_seed_dye"), Items.DYE.blue())
                     .shapeless()
                     .addMaterial('#', EndVineBlocks.BLUE_VINE_SEED)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("creeping_moss_dye"), Items.DYE.cyan())
                     .shapeless()
                     .addMaterial('#', EndPlantBlocks.CREEPING_MOSS)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("umbrella_moss_dye"), Items.DYE.yellow())
                     .shapeless()
                     .addMaterial('#', EndPlantBlocks.UMBRELLA_MOSS)
                     .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder25 = RecipeBuilder.crafting(
                BetterEnd.C.mk("umbrella_moss_tall_dye"),
                Items.DYE.yellow()
        );
        craftingRecipeBuilder25.outputCount(2)
                               .shapeless()
                               .addMaterial('#', EndPlantBlocks.UMBRELLA_MOSS_TALL)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("shadow_plant_dye"), Items.DYE.black())
                     .shapeless()
                     .addMaterial('#', EndPlantBlocks.SHADOW_PLANT)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder58 = RecipeBuilder.crafting(BetterEnd.C.mk("paper"), Items.PAPER);
        CraftingRecipeBuilder craftingRecipeBuilder24 = craftingRecipeBuilder58.shape("###")
                                                                               .addMaterial(
                                                                                       '#',
                                                                                       EndResourceItems.END_LILY_LEAF_DRIED
                                                                               );
        craftingRecipeBuilder24.outputCount(3)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder57 = RecipeBuilder.crafting(
                BetterEnd.C.mk("aurora_block"),
                EndCrystalBlocks.AURORA_CRYSTAL
        );
        craftingRecipeBuilder57.shape("##", "##")
                               .addMaterial('#', EndResourceItems.CRYSTAL_SHARDS)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder56 = RecipeBuilder.crafting(
                BetterEnd.C.mk("lotus_block"),
                EndWoodBlocks.END_LOTUS.getLog()
        );
        craftingRecipeBuilder56.shape("##", "##")
                               .addMaterial('#', EndWoodBlocks.END_LOTUS_STEM)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder23 = RecipeBuilder
                .crafting(BetterEnd.C.mk("needlegrass_stick"), Items.STICK)
                .shapeless();
        craftingRecipeBuilder23.outputCount(2)
                               .addMaterial('#', EndPlantBlocks.NEEDLEGRASS)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder22 = RecipeBuilder
                .crafting(BetterEnd.C.mk("shadow_berry_seeds"), EndCropBlocks.SHADOW_BERRY)
                .shapeless();
        craftingRecipeBuilder22.outputCount(4)
                               .addMaterial('#', EndFoodItems.SHADOW_BERRY_RAW)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("purple_polypore_dye"), Items.DYE.purple())
                     .shapeless()
                     .addMaterial('#', EndWallPlantBlocks.PURPLE_POLYPORE)
                     .build(context);

        registerLantern(
                context,
                "end_stone_lantern",
                EndStoneBlocks.END_STONE_SET.getBlock(StoneLantern.SLOT),
                Blocks.END_STONE_BRICK_SLAB
        );
        registerLantern(
                context,
                "andesite_lantern",
                EndStoneBlocks.ANDESITE_SET.getBlock(StoneLantern.SLOT),
                Blocks.ANDESITE_SLAB
        );
        registerLantern(
                context,
                "diorite_lantern",
                EndStoneBlocks.DIORITE_SET.getBlock(StoneLantern.SLOT),
                Blocks.DIORITE_SLAB
        );
        registerLantern(
                context,
                "granite_lantern",
                EndStoneBlocks.GRANITE_SET.getBlock(StoneLantern.SLOT),
                Blocks.GRANITE_SLAB
        );
        registerLantern(
                context,
                "quartz_lantern",
                EndStoneBlocks.QUARTZ_SET.getBlock(StoneLantern.SLOT),
                Blocks.QUARTZ_SLAB
        );
        registerLantern(
                context,
                "purple_lantern",
                EndStoneBlocks.PURPUR_SET.getBlock(StoneLantern.SLOT),
                Blocks.PURPUR_SLAB
        );
        registerLantern(
                context,
                "blackstone_lantern",
                EndStoneBlocks.BLACKSTONE_SET.getBlock(StoneLantern.SLOT),
                Blocks.BLACKSTONE_SLAB
        );

        CraftingRecipeBuilder craftingRecipeBuilder55 = RecipeBuilder.crafting(
                BetterEnd.C.mk("amber_gem"),
                EndResourceItems.AMBER_GEM
        );
        craftingRecipeBuilder55.shape("##", "##")
                               .addMaterial('#', EndResourceItems.RAW_AMBER)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder54 = RecipeBuilder.crafting(
                BetterEnd.C.mk("amber_block"),
                EndMetalBlocks.AMBER_BLOCK
        );
        craftingRecipeBuilder54.shape("##", "##")
                               .addMaterial('#', EndResourceItems.AMBER_GEM)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder21 = RecipeBuilder.crafting(
                BetterEnd.C.mk("amber_gem_block"),
                EndResourceItems.AMBER_GEM
        );
        craftingRecipeBuilder21.outputCount(4)
                               .shapeless()
                               .addMaterial('#', EndMetalBlocks.AMBER_BLOCK)
                               .build(context);
        // iron_bulb_lantern is generated from the BULB_LANTERN slot's recipe trait (Auto Recipe Provider).
        RecipeBuilder.crafting(BetterEnd.C.mk("twisted_moss_dye"), Items.DYE.pink())
                     .shapeless()
                     .addMaterial('#', EndWallPlantBlocks.TWISTED_MOSS)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("byshy_grass_dye"), Items.DYE.magenta())
                     .shapeless()
                     .addMaterial('#', EndPlantBlocks.BUSHY_GRASS)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("tail_moss_dye"), Items.DYE.gray())
                     .shapeless()
                     .addMaterial('#', EndWallPlantBlocks.TAIL_MOSS)
                     .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder52 = RecipeBuilder.crafting(
                BetterEnd.C.mk("petal_block"),
                EndDecorBlocks.HYDRALUX_PETAL_BLOCK
        );
        craftingRecipeBuilder52.shape("##", "##")
                               .addMaterial('#', EndResourceItems.HYDRALUX_PETAL)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("petal_white_dye"), Items.DYE.white())
                     .shapeless()
                     .addMaterial('#', EndResourceItems.HYDRALUX_PETAL)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder8 = RecipeBuilder
                .crafting(BetterEnd.C.mk("sweet_berry_jelly_potion"), EndFoodItems.SWEET_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndResourceItems.GELATINE)
                .addMaterial('W', Items.POTION)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', Items.SWEET_BERRIES);
        craftingRecipeBuilder8.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder7 = RecipeBuilder
                .crafting(BetterEnd.C.mk("shadow_berry_jelly_potion"), EndFoodItems.SHADOW_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndResourceItems.GELATINE)
                .addMaterial('W', Items.POTION)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndFoodItems.SHADOW_BERRY_COOKED);
        craftingRecipeBuilder7.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder6 = RecipeBuilder
                .crafting(BetterEnd.C.mk("blossom_berry_jelly_potion"), EndFoodItems.BLOSSOM_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndResourceItems.GELATINE)
                .addMaterial('W', Items.POTION)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndFoodItems.BLOSSOM_BERRY);
        craftingRecipeBuilder6.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder5 = RecipeBuilder
                .crafting(BetterEnd.C.mk("sweet_berry_jelly"), EndFoodItems.SWEET_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndResourceItems.GELATINE)
                .addMaterial('W', CommonItemTags.WATER_BOTTLES)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', Items.SWEET_BERRIES);
        craftingRecipeBuilder5.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder4 = RecipeBuilder
                .crafting(BetterEnd.C.mk("shadow_berry_jelly"), EndFoodItems.SHADOW_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndResourceItems.GELATINE)
                .addMaterial('W', CommonItemTags.WATER_BOTTLES)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndFoodItems.SHADOW_BERRY_COOKED);
        craftingRecipeBuilder4.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder3 = RecipeBuilder
                .crafting(BetterEnd.C.mk("blossom_berry_jelly"), EndFoodItems.BLOSSOM_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndResourceItems.GELATINE)
                .addMaterial('W', CommonItemTags.WATER_BOTTLES)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndFoodItems.BLOSSOM_BERRY);
        craftingRecipeBuilder3.group("end_berries")
                              .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("sulphur_gunpowder"), Items.GUNPOWDER)
                     .shapeless()
                     .addMaterial('S', EndResourceItems.CRYSTALLINE_SULPHUR)
                     .addMaterial('C', Items.COAL, Items.CHARCOAL)
                     .addMaterial('B', Items.BONE_MEAL)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder51 = RecipeBuilder.crafting(
                BetterEnd.C.mk("dense_emerald_ice"),
                EndDecorBlocks.DENSE_EMERALD_ICE
        );
        craftingRecipeBuilder51.shape("##", "##")
                               .addMaterial('#', EndDecorBlocks.EMERALD_ICE)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder50 = RecipeBuilder.crafting(
                BetterEnd.C.mk("ancient_emerald_ice"),
                EndDecorBlocks.ANCIENT_EMERALD_ICE
        );
        craftingRecipeBuilder50.shape("###", "###", "###")
                               .addMaterial('#', EndDecorBlocks.DENSE_EMERALD_ICE)
                               .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_cyan_dye"), Items.DYE.cyan())
                     .shapeless()
                     .addMaterial('#', EndWaterPlantBlocks.CHARNIA_CYAN)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_green_dye"), Items.DYE.green())
                     .shapeless()
                     .addMaterial('#', EndWaterPlantBlocks.CHARNIA_GREEN)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_light_blue_dye"), Items.DYE.lightBlue())
                     .shapeless()
                     .addMaterial('#', EndWaterPlantBlocks.CHARNIA_LIGHT_BLUE)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_orange_dye"), Items.DYE.orange())
                     .shapeless()
                     .addMaterial('#', EndWaterPlantBlocks.CHARNIA_ORANGE)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_purple_dye"), Items.DYE.purple())
                     .shapeless()
                     .addMaterial('#', EndWaterPlantBlocks.CHARNIA_PURPLE)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_red_dye"), Items.DYE.red())
                     .shapeless()
                     .addMaterial('#', EndWaterPlantBlocks.CHARNIA_RED)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder49 = RecipeBuilder.crafting(
                BetterEnd.C.mk("respawn_obelisk"),
                EndFunctionalBlocks.RESPAWN_OBELISK
        );
        craftingRecipeBuilder49.shape("CSC", "CSC", "AAA")
                               .addMaterial('C', EndCrystalBlocks.AURORA_CRYSTAL)
                               .addMaterial('S', EndResourceItems.ETERNAL_CRYSTAL)
                               .addMaterial('A', EndMetalBlocks.AMBER_BLOCK)
                               .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("twisted_umbrella_moss_dye"), Items.DYE.purple())
                     .shapeless()
                     .addMaterial('#', EndPlantBlocks.TWISTED_UMBRELLA_MOSS)
                     .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder20 = RecipeBuilder.crafting(
                BetterEnd.C.mk("twisted_umbrella_moss_dye_tall"), Items.DYE.purple());
        craftingRecipeBuilder20.outputCount(2)
                               .shapeless()
                               .addMaterial('#', EndPlantBlocks.TWISTED_UMBRELLA_MOSS_TALL)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder19 = RecipeBuilder
                .crafting(BetterEnd.C.mk("leather_to_stripes"), EndResourceItems.LEATHER_STRIPE)
                .shapeless()
                .addMaterial('L', Items.LEATHER);
        craftingRecipeBuilder19.outputCount(3)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder48 = RecipeBuilder.crafting(
                BetterEnd.C.mk("stripes_to_leather"),
                Items.LEATHER
        );
        craftingRecipeBuilder48.shape("SSS")
                               .addMaterial('S', EndResourceItems.LEATHER_STRIPE)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("leather_wrapped_stick"), EndResourceItems.LEATHER_WRAPPED_STICK)
                     .shapeless()
                     .addMaterial('S', Items.STICK)
                     .addMaterial('L', EndResourceItems.LEATHER_STRIPE)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder18 = RecipeBuilder.crafting(
                BetterEnd.C.mk("fiber_string"),
                Items.STRING
        );
        CraftingRecipeBuilder craftingRecipeBuilder47 = craftingRecipeBuilder18.outputCount(6);
        craftingRecipeBuilder47.shape("#", "#", "#")
                               .addMaterial('#', EndResourceItems.SILK_FIBER)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder46 = RecipeBuilder.crafting(
                BetterEnd.C.mk("ender_eye_amber"),
                Items.ENDER_EYE
        );
        craftingRecipeBuilder46.shape("SAS", "APA", "SAS")
                               .addMaterial('S', EndResourceItems.CRYSTAL_SHARDS)
                               .addMaterial('A', EndResourceItems.AMBER_GEM)
                               .addMaterial('P', Items.ENDER_PEARL)
                               .build(context);

        // iron_chandelier/gold_chandelier are generated from the CHANDELIER slot's recipe trait
        // (Auto Recipe Provider).

        CraftingRecipeBuilder craftingRecipeBuilder17 = RecipeBuilder.crafting(
                BetterEnd.C.mk("missing_tile"),
                EndStoneBlocks.MISSING_TILE
        );
        CraftingRecipeBuilder craftingRecipeBuilder43 = craftingRecipeBuilder17.outputCount(4);
        craftingRecipeBuilder43.shape("#P", "P#")
                               .addMaterial(
                                       '#',
                                       EndStoneBlocks.VIOLECITE.getBlock(SlotType.SOURCE),
                                       EndStoneBlocks.VIOLECITE.getBlock(SlotType.BRICK),
                                       EndStoneBlocks.VIOLECITE.getBlock(SlotType.TILES)
                               )
                               .addMaterial('P', Blocks.PURPUR_BLOCK)
                               .build(context);

        registerHammer(context, "iron", Items.IRON_INGOT, EndEquipmentItems.IRON_HAMMER);
        registerHammer(context, "golden", Items.GOLD_INGOT, EndEquipmentItems.GOLDEN_HAMMER);
        registerHammer(context, "diamond", Items.DIAMOND, EndEquipmentItems.DIAMOND_HAMMER);

        CraftingRecipeBuilder craftingRecipeBuilder42 = RecipeBuilder.crafting(
                BetterEnd.C.mk("charcoal_block"),
                EndMetalBlocks.CHARCOAL_BLOCK
        );
        craftingRecipeBuilder42.shape("###", "###", "###")
                               .addMaterial('#', Items.CHARCOAL)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder16 = RecipeBuilder.crafting(
                BetterEnd.C.mk("charcoal_from_block"),
                Items.CHARCOAL
        );
        craftingRecipeBuilder16.outputCount(9)
                               .shapeless()
                               .addMaterial('#', EndMetalBlocks.CHARCOAL_BLOCK)
                               .build(context);
        // end_stone_furnace is generated from the Furnace slot's recipe trait (Auto Recipe Provider), which
        // builds the identical recipe off the set's base block and adds the "end_stone_furnaces" group.
        CraftingRecipeBuilder craftingRecipeBuilder40 = RecipeBuilder.crafting(
                BetterEnd.C.mk("filalux_lantern"),
                EndLightBlocks.FILALUX_LANTERN
        );
        craftingRecipeBuilder40.shape("###", "###", "###")
                               .addMaterial('#', EndVineBlocks.FILALUX)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder39 = RecipeBuilder.crafting(
                BetterEnd.C.mk("silk_moth_hive"),
                EndFunctionalBlocks.SILK_MOTH_HIVE
        );
        craftingRecipeBuilder39.shape("#L#", "LML", "#L#")
                               .addMaterial('#', EndWoodBlocks.TENANEA.getBlock(SlotType.PLANKS))
                               .addMaterial('L', EndWoodBlocks.TENANEA_LEAVES)
                               .addMaterial('M', EndResourceItems.SILK_MOTH_MATRIX)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder38 = RecipeBuilder.crafting(
                BetterEnd.C.mk("cave_pumpkin_pie"),
                EndFoodItems.CAVE_PUMPKIN_PIE
        );
        craftingRecipeBuilder38.shape("SBS", "BPB", "SBS")
                               .addMaterial('P', EndCropBlocks.CAVE_PUMPKIN)
                               .addMaterial('B', EndFoodItems.BLOSSOM_BERRY, EndFoodItems.SHADOW_BERRY_RAW)
                               .addMaterial('S', Items.SUGAR)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder15 = RecipeBuilder.crafting(
                BetterEnd.C.mk("cave_pumpkin_seeds"),
                EndCropBlocks.CAVE_PUMPKIN_SEED
        );
        craftingRecipeBuilder15.outputCount(4)
                               .shapeless()
                               .addMaterial('#', EndCropBlocks.CAVE_PUMPKIN)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder37 = RecipeBuilder.crafting(
                BetterEnd.C.mk("neon_cactus_block"),
                EndDecorBlocks.NEON_CACTUS_BLOCK
        );
        craftingRecipeBuilder37.shape("##", "##")
                               .addMaterial('#', EndPlantBlocks.NEON_CACTUS)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder36 = RecipeBuilder.crafting(
                BetterEnd.C.mk("neon_cactus_block_slab"),
                EndDecorBlocks.NEON_CACTUS_BLOCK_SLAB
        );
        CraftingRecipeBuilder craftingRecipeBuilder14 = craftingRecipeBuilder36.shape("###");
        craftingRecipeBuilder14.outputCount(6)
                               .addMaterial('#', EndDecorBlocks.NEON_CACTUS_BLOCK)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder35 = RecipeBuilder.crafting(
                BetterEnd.C.mk("neon_cactus_block_stairs"),
                EndDecorBlocks.NEON_CACTUS_BLOCK_STAIRS
        );
        CraftingRecipeBuilder craftingRecipeBuilder13 = craftingRecipeBuilder35.shape("#  ", "## ", "###");
        craftingRecipeBuilder13.outputCount(4)
                               .addMaterial('#', EndDecorBlocks.NEON_CACTUS_BLOCK)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder34 = RecipeBuilder.crafting(
                BetterEnd.C.mk("sugar_from_root"),
                Items.SUGAR
        );
        craftingRecipeBuilder34.shape("###")
                               .addMaterial('#', EndFoodItems.AMBER_ROOT_RAW)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder12 = RecipeBuilder.crafting(
                BetterEnd.C.mk("end_stone_flower_pot"),
                EndStoneBlocks.END_STONE_SET.getBlock(FlowerPot.SLOT)
        );
        CraftingRecipeBuilder craftingRecipeBuilder33 = craftingRecipeBuilder12.outputCount(3);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder33.shape("# #", " # ")
                                                                             .addMaterial('#', Blocks.END_STONE_BRICKS);
        craftingRecipeBuilder.group("end_pots")
                             .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder11 = RecipeBuilder.crafting(
                BetterEnd.C.mk("dragon_bone_block"),
                EndStoneBlocks.DRAGON_BONE_BLOCK
        );
        CraftingRecipeBuilder craftingRecipeBuilder32 = craftingRecipeBuilder11.outputCount(8);
        craftingRecipeBuilder32.shape("###", "#D#", "###")
                               .addMaterial('#', Blocks.BONE_BLOCK)
                               .addMaterial('D', Items.DRAGON_BREATH)
                               .build(context);
        // dragon_bone_slab/dragon_bone_stairs are generated from the RecipeTraitLibrary.slab()/stairs() traits on
        // the block definitions (Auto Recipe Provider), which build the identical shape/count and additionally
        // emit the matching stonecutter recipes.

        CraftingRecipeBuilder craftingRecipeBuilder29 = RecipeBuilder.crafting(
                BetterEnd.C.mk("smaragdant_crystal"),
                EndCrystalBlocks.SMARAGDANT_CRYSTAL
        );
        craftingRecipeBuilder29.shape("##", "##")
                               .addMaterial('#', EndCrystalBlocks.SMARAGDANT_CRYSTAL_SHARD)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder28 = RecipeBuilder.crafting(
                BetterEnd.C.mk("tined_glass_from_smaragdant"), Blocks.TINTED_GLASS);
        craftingRecipeBuilder28.shape(" # ", "#G#", " # ")
                               .addMaterial('#', EndCrystalBlocks.SMARAGDANT_CRYSTAL_SHARD)
                               .addMaterial('G', Blocks.GLASS)
                               .build(context);
    }

    private static void registerLantern(RecipeBuilder.Context context, String name, Block lantern, Block slab) {
        CraftingRecipeBuilder craftingRecipeBuilder1 = RecipeBuilder.crafting(BetterEnd.C.mk(name), lantern);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder1.shape("S", "#", "S")
                                                                            .addMaterial('#', EndResourceItems.CRYSTAL_SHARDS)
                                                                            .addMaterial('S', slab);
        craftingRecipeBuilder.group("end_stone_lanterns")
                             .build(context);
    }

    public static void registerPedestal(
            RecipeBuilder.Context context,
            String name,
            Block pedestal,
            Block slab,
            Block pillar
    ) {
        CraftingRecipeBuilder craftingRecipeBuilder1 = RecipeBuilder.crafting(BetterEnd.C.mk(name), pedestal);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder1.shape("S", "#", "S")
                                                                            .addMaterial('S', slab)
                                                                            .addMaterial('#', pillar);
        craftingRecipeBuilder.outputCount(2)
                             .build(context);
    }

    private static void registerHammer(RecipeBuilder.Context context, String name, Item material, Item result) {
        CraftingRecipeBuilder craftingRecipeBuilder = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_hammer"), result);
        craftingRecipeBuilder.shape("I I", "I#I", " # ")
                             .addMaterial('I', material)
                             .addMaterial('#', Items.STICK)
                             .build(context);
    }

}
