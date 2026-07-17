package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.types.FlowerPot;
import org.betterx.betterend.complexmaterials.types.Pedestal;
import org.betterx.betterend.complexmaterials.types.StoneLantern;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
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
                EndBlocks.ENDER_BLOCK
        );
        craftingRecipeBuilder62.shape("OO", "OO")
                               .addMaterial('O', Items.ENDER_PEARL)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder27 = RecipeBuilder
                .crafting(BetterEnd.C.mk("ender_block_to_perl"), Items.ENDER_PEARL)
                .addMaterial('#', EndBlocks.ENDER_BLOCK);
        craftingRecipeBuilder27.outputCount(4)
                               .shapeless()
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder61 = RecipeBuilder.crafting(
                BetterEnd.C.mk("end_stone_smelter"),
                EndBlocks.END_STONE_SMELTER
        );
        craftingRecipeBuilder61.shape("T#T", "V V", "T#T")
                               .addMaterial('#', Blocks.END_STONE_BRICKS)
                               .addMaterial('T', EndBlocks.THALLASIUM.equipment.ingot)
                               .addMaterial('V', CommonItemTags.FURNACES)
                               .build(context);

        registerPedestal(
                context,
                "andesite_pedestal",
                EndBlocks.ANDESITE_SET.getBlock(Pedestal.SLOT),
                Blocks.POLISHED_ANDESITE_SLAB,
                Blocks.POLISHED_ANDESITE
        );
        registerPedestal(
                context,
                "diorite_pedestal",
                EndBlocks.DIORITE_SET.getBlock(Pedestal.SLOT),
                Blocks.POLISHED_DIORITE_SLAB,
                Blocks.POLISHED_DIORITE
        );
        registerPedestal(
                context,
                "granite_pedestal",
                EndBlocks.GRANITE_SET.getBlock(Pedestal.SLOT),
                Blocks.POLISHED_GRANITE_SLAB,
                Blocks.POLISHED_GRANITE
        );
        registerPedestal(
                context,
                "quartz_pedestal",
                EndBlocks.QUARTZ_SET.getBlock(Pedestal.SLOT),
                Blocks.QUARTZ_SLAB,
                Blocks.QUARTZ_PILLAR
        );
        registerPedestal(
                context,
                "purpur_pedestal",
                EndBlocks.PURPUR_SET.getBlock(Pedestal.SLOT),
                Blocks.PURPUR_SLAB,
                Blocks.PURPUR_PILLAR
        );

        CraftingRecipeBuilder craftingRecipeBuilder60 = RecipeBuilder.crafting(
                BetterEnd.C.mk("infusion_pedestal"),
                EndBlocks.INFUSION_PEDESTAL
        );
        craftingRecipeBuilder60.shape(" Y ", "O#O", " # ")
                               .addMaterial('O', Items.ENDER_PEARL)
                               .addMaterial('Y', Items.ENDER_EYE)
                               .addMaterial('#', Blocks.OBSIDIAN)
                               .build(context);

        String material = "aeternium";
        CraftingRecipeBuilder craftingRecipeBuilder59 = RecipeBuilder.crafting(
                BetterEnd.C.mk(material + "_block"),
                EndBlocks.AETERNIUM_BLOCK
        );
        craftingRecipeBuilder59.shape("III", "III", "III")
                               .addMaterial('I', EndItems.AETERNIUM_SET.ingot)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder26 = RecipeBuilder
                .crafting(BetterEnd.C.mk(material + "_block_to_ingot"), EndItems.AETERNIUM_SET.ingot)
                .addMaterial('#', EndBlocks.AETERNIUM_BLOCK);
        craftingRecipeBuilder26.outputCount(9)
                               .shapeless()
                               .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("blue_vine_seed_dye"), Items.BLUE_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.BLUE_VINE_SEED)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("creeping_moss_dye"), Items.CYAN_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.CREEPING_MOSS)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("umbrella_moss_dye"), Items.YELLOW_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.UMBRELLA_MOSS)
                     .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder25 = RecipeBuilder.crafting(
                BetterEnd.C.mk("umbrella_moss_tall_dye"),
                Items.YELLOW_DYE
        );
        craftingRecipeBuilder25.outputCount(2)
                               .shapeless()
                               .addMaterial('#', EndBlocks.UMBRELLA_MOSS_TALL)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("shadow_plant_dye"), Items.BLACK_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.SHADOW_PLANT)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder58 = RecipeBuilder.crafting(BetterEnd.C.mk("paper"), Items.PAPER);
        CraftingRecipeBuilder craftingRecipeBuilder24 = craftingRecipeBuilder58.shape("###")
                                                                               .addMaterial(
                                                                                       '#',
                                                                                       EndItems.END_LILY_LEAF_DRIED
                                                                               );
        craftingRecipeBuilder24.outputCount(3)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder57 = RecipeBuilder.crafting(
                BetterEnd.C.mk("aurora_block"),
                EndBlocks.AURORA_CRYSTAL
        );
        craftingRecipeBuilder57.shape("##", "##")
                               .addMaterial('#', EndItems.CRYSTAL_SHARDS)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder56 = RecipeBuilder.crafting(
                BetterEnd.C.mk("lotus_block"),
                EndBlocks.END_LOTUS.getLog()
        );
        craftingRecipeBuilder56.shape("##", "##")
                               .addMaterial('#', EndBlocks.END_LOTUS_STEM)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder23 = RecipeBuilder
                .crafting(BetterEnd.C.mk("needlegrass_stick"), Items.STICK)
                .shapeless();
        craftingRecipeBuilder23.outputCount(2)
                               .addMaterial('#', EndBlocks.NEEDLEGRASS)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder22 = RecipeBuilder
                .crafting(BetterEnd.C.mk("shadow_berry_seeds"), EndBlocks.SHADOW_BERRY)
                .shapeless();
        craftingRecipeBuilder22.outputCount(4)
                               .addMaterial('#', EndItems.SHADOW_BERRY_RAW)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("purple_polypore_dye"), Items.PURPLE_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.PURPLE_POLYPORE)
                     .build(context);

        registerLantern(
                context,
                "end_stone_lantern",
                EndBlocks.END_STONE_SET.getBlock(StoneLantern.SLOT),
                Blocks.END_STONE_BRICK_SLAB
        );
        registerLantern(
                context,
                "andesite_lantern",
                EndBlocks.ANDESITE_SET.getBlock(StoneLantern.SLOT),
                Blocks.ANDESITE_SLAB
        );
        registerLantern(
                context,
                "diorite_lantern",
                EndBlocks.DIORITE_SET.getBlock(StoneLantern.SLOT),
                Blocks.DIORITE_SLAB
        );
        registerLantern(
                context,
                "granite_lantern",
                EndBlocks.GRANITE_SET.getBlock(StoneLantern.SLOT),
                Blocks.GRANITE_SLAB
        );
        registerLantern(
                context,
                "quartz_lantern",
                EndBlocks.QUARTZ_SET.getBlock(StoneLantern.SLOT),
                Blocks.QUARTZ_SLAB
        );
        registerLantern(
                context,
                "purpur_lantern",
                EndBlocks.PURPUR_SET.getBlock(StoneLantern.SLOT),
                Blocks.PURPUR_SLAB
        );
        registerLantern(
                context,
                "blackstone_lantern",
                EndBlocks.BLACKSTONE_SET.getBlock(StoneLantern.SLOT),
                Blocks.BLACKSTONE_SLAB
        );

        CraftingRecipeBuilder craftingRecipeBuilder55 = RecipeBuilder.crafting(
                BetterEnd.C.mk("amber_gem"),
                EndItems.AMBER_GEM
        );
        craftingRecipeBuilder55.shape("##", "##")
                               .addMaterial('#', EndItems.RAW_AMBER)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder54 = RecipeBuilder.crafting(
                BetterEnd.C.mk("amber_block"),
                EndBlocks.AMBER_BLOCK
        );
        craftingRecipeBuilder54.shape("##", "##")
                               .addMaterial('#', EndItems.AMBER_GEM)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder21 = RecipeBuilder.crafting(
                BetterEnd.C.mk("amber_gem_block"),
                EndItems.AMBER_GEM
        );
        craftingRecipeBuilder21.outputCount(4)
                               .shapeless()
                               .addMaterial('#', EndBlocks.AMBER_BLOCK)
                               .build(context);
        // iron_bulb_lantern is generated from the BULB_LANTERN slot's recipe trait (Auto Recipe Provider).
        RecipeBuilder.crafting(BetterEnd.C.mk("twisted_moss_dye"), Items.PINK_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.TWISTED_MOSS)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("byshy_grass_dye"), Items.MAGENTA_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.BUSHY_GRASS)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("tail_moss_dye"), Items.GRAY_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.TAIL_MOSS)
                     .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder52 = RecipeBuilder.crafting(
                BetterEnd.C.mk("petal_block"),
                EndBlocks.HYDRALUX_PETAL_BLOCK
        );
        craftingRecipeBuilder52.shape("##", "##")
                               .addMaterial('#', EndItems.HYDRALUX_PETAL)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("petal_white_dye"), Items.WHITE_DYE)
                     .shapeless()
                     .addMaterial('#', EndItems.HYDRALUX_PETAL)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder8 = RecipeBuilder
                .crafting(BetterEnd.C.mk("sweet_berry_jelly_potion"), EndItems.SWEET_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndItems.GELATINE)
                .addMaterial('W', waterPotion())
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', Items.SWEET_BERRIES);
        craftingRecipeBuilder8.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder7 = RecipeBuilder
                .crafting(BetterEnd.C.mk("shadow_berry_jelly_potion"), EndItems.SHADOW_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndItems.GELATINE)
                .addMaterial('W', waterPotion())
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndItems.SHADOW_BERRY_COOKED);
        craftingRecipeBuilder7.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder6 = RecipeBuilder
                .crafting(BetterEnd.C.mk("blossom_berry_jelly_potion"), EndItems.BLOSSOM_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndItems.GELATINE)
                .addMaterial('W', waterPotion())
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndItems.BLOSSOM_BERRY);
        craftingRecipeBuilder6.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder5 = RecipeBuilder
                .crafting(BetterEnd.C.mk("sweet_berry_jelly"), EndItems.SWEET_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndItems.GELATINE)
                .addMaterial('W', CommonItemTags.WATER_BOTTLES)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', Items.SWEET_BERRIES);
        craftingRecipeBuilder5.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder4 = RecipeBuilder
                .crafting(BetterEnd.C.mk("shadow_berry_jelly"), EndItems.SHADOW_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndItems.GELATINE)
                .addMaterial('W', CommonItemTags.WATER_BOTTLES)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndItems.SHADOW_BERRY_COOKED);
        craftingRecipeBuilder4.group("end_berries")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder3 = RecipeBuilder
                .crafting(BetterEnd.C.mk("blossom_berry_jelly"), EndItems.BLOSSOM_BERRY_JELLY)
                .shapeless()
                .addMaterial('J', EndItems.GELATINE)
                .addMaterial('W', CommonItemTags.WATER_BOTTLES)
                .addMaterial('S', Items.SUGAR)
                .addMaterial('B', EndItems.BLOSSOM_BERRY);
        craftingRecipeBuilder3.group("end_berries")
                              .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("sulphur_gunpowder"), Items.GUNPOWDER)
                     .shapeless()
                     .addMaterial('S', EndItems.CRYSTALLINE_SULPHUR)
                     .addMaterial('C', Items.COAL, Items.CHARCOAL)
                     .addMaterial('B', Items.BONE_MEAL)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder51 = RecipeBuilder.crafting(
                BetterEnd.C.mk("dense_emerald_ice"),
                EndBlocks.DENSE_EMERALD_ICE
        );
        craftingRecipeBuilder51.shape("##", "##")
                               .addMaterial('#', EndBlocks.EMERALD_ICE)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder50 = RecipeBuilder.crafting(
                BetterEnd.C.mk("ancient_emerald_ice"),
                EndBlocks.ANCIENT_EMERALD_ICE
        );
        craftingRecipeBuilder50.shape("###", "###", "###")
                               .addMaterial('#', EndBlocks.DENSE_EMERALD_ICE)
                               .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_cyan_dye"), Items.CYAN_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.CHARNIA_CYAN)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_green_dye"), Items.GREEN_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.CHARNIA_GREEN)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_light_blue_dye"), Items.LIGHT_BLUE_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.CHARNIA_LIGHT_BLUE)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_orange_dye"), Items.ORANGE_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.CHARNIA_ORANGE)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_purple_dye"), Items.PURPLE_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.CHARNIA_PURPLE)
                     .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("charnia_red_dye"), Items.RED_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.CHARNIA_RED)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder49 = RecipeBuilder.crafting(
                BetterEnd.C.mk("respawn_obelisk"),
                EndBlocks.RESPAWN_OBELISK
        );
        craftingRecipeBuilder49.shape("CSC", "CSC", "AAA")
                               .addMaterial('C', EndBlocks.AURORA_CRYSTAL)
                               .addMaterial('S', EndItems.ETERNAL_CRYSTAL)
                               .addMaterial('A', EndBlocks.AMBER_BLOCK)
                               .build(context);

        RecipeBuilder.crafting(BetterEnd.C.mk("twisted_umbrella_moss_dye"), Items.PURPLE_DYE)
                     .shapeless()
                     .addMaterial('#', EndBlocks.TWISTED_UMBRELLA_MOSS)
                     .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder20 = RecipeBuilder.crafting(
                BetterEnd.C.mk("twisted_umbrella_moss_dye_tall"), Items.PURPLE_DYE);
        craftingRecipeBuilder20.outputCount(2)
                               .shapeless()
                               .addMaterial('#', EndBlocks.TWISTED_UMBRELLA_MOSS_TALL)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder19 = RecipeBuilder
                .crafting(BetterEnd.C.mk("leather_to_stripes"), EndItems.LEATHER_STRIPE)
                .shapeless()
                .addMaterial('L', Items.LEATHER);
        craftingRecipeBuilder19.outputCount(3)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder48 = RecipeBuilder.crafting(
                BetterEnd.C.mk("stripes_to_leather"),
                Items.LEATHER
        );
        craftingRecipeBuilder48.shape("SSS")
                               .addMaterial('S', EndItems.LEATHER_STRIPE)
                               .build(context);
        RecipeBuilder.crafting(BetterEnd.C.mk("leather_wrapped_stick"), EndItems.LEATHER_WRAPPED_STICK)
                     .shapeless()
                     .addMaterial('S', Items.STICK)
                     .addMaterial('L', EndItems.LEATHER_STRIPE)
                     .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder18 = RecipeBuilder.crafting(
                BetterEnd.C.mk("fiber_string"),
                Items.STRING
        );
        CraftingRecipeBuilder craftingRecipeBuilder47 = craftingRecipeBuilder18.outputCount(6);
        craftingRecipeBuilder47.shape("#", "#", "#")
                               .addMaterial('#', EndItems.SILK_FIBER)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder46 = RecipeBuilder.crafting(
                BetterEnd.C.mk("ender_eye_amber"),
                Items.ENDER_EYE
        );
        craftingRecipeBuilder46.shape("SAS", "APA", "SAS")
                               .addMaterial('S', EndItems.CRYSTAL_SHARDS)
                               .addMaterial('A', EndItems.AMBER_GEM)
                               .addMaterial('P', Items.ENDER_PEARL)
                               .build(context);

        // iron_chandelier/gold_chandelier are generated from the CHANDELIER slot's recipe trait
        // (Auto Recipe Provider).

        CraftingRecipeBuilder craftingRecipeBuilder17 = RecipeBuilder.crafting(
                BetterEnd.C.mk("missing_tile"),
                EndBlocks.MISSING_TILE
        );
        CraftingRecipeBuilder craftingRecipeBuilder43 = craftingRecipeBuilder17.outputCount(4);
        craftingRecipeBuilder43.shape("#P", "P#")
                               .addMaterial(
                                       '#',
                                       EndBlocks.VIOLECITE.getBlock(SlotType.SOURCE),
                                       EndBlocks.VIOLECITE.getBlock(SlotType.BRICK),
                                       EndBlocks.VIOLECITE.getBlock(SlotType.TILES)
                               )
                               .addMaterial('P', Blocks.PURPUR_BLOCK)
                               .build(context);

        registerHammer(context, "iron", Items.IRON_INGOT, EndItems.IRON_HAMMER);
        registerHammer(context, "golden", Items.GOLD_INGOT, EndItems.GOLDEN_HAMMER);
        registerHammer(context, "diamond", Items.DIAMOND, EndItems.DIAMOND_HAMMER);

        CraftingRecipeBuilder craftingRecipeBuilder42 = RecipeBuilder.crafting(
                BetterEnd.C.mk("charcoal_block"),
                EndBlocks.CHARCOAL_BLOCK
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
                               .addMaterial('#', EndBlocks.CHARCOAL_BLOCK)
                               .build(context);
        // end_stone_furnace is generated from the Furnace slot's recipe trait (Auto Recipe Provider), which
        // builds the identical recipe off the set's base block and adds the "end_stone_furnaces" group.
        CraftingRecipeBuilder craftingRecipeBuilder40 = RecipeBuilder.crafting(
                BetterEnd.C.mk("filalux_lantern"),
                EndBlocks.FILALUX_LANTERN
        );
        craftingRecipeBuilder40.shape("###", "###", "###")
                               .addMaterial('#', EndBlocks.FILALUX)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder39 = RecipeBuilder.crafting(
                BetterEnd.C.mk("silk_moth_hive"),
                EndBlocks.SILK_MOTH_HIVE
        );
        craftingRecipeBuilder39.shape("#L#", "LML", "#L#")
                               .addMaterial('#', EndBlocks.TENANEA.getBlock(SlotType.PLANKS))
                               .addMaterial('L', EndBlocks.TENANEA_LEAVES)
                               .addMaterial('M', EndItems.SILK_MOTH_MATRIX)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder38 = RecipeBuilder.crafting(
                BetterEnd.C.mk("cave_pumpkin_pie"),
                EndItems.CAVE_PUMPKIN_PIE
        );
        craftingRecipeBuilder38.shape("SBS", "BPB", "SBS")
                               .addMaterial('P', EndBlocks.CAVE_PUMPKIN)
                               .addMaterial('B', EndItems.BLOSSOM_BERRY, EndItems.SHADOW_BERRY_RAW)
                               .addMaterial('S', Items.SUGAR)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder15 = RecipeBuilder.crafting(
                BetterEnd.C.mk("cave_pumpkin_seeds"),
                EndBlocks.CAVE_PUMPKIN_SEED
        );
        craftingRecipeBuilder15.outputCount(4)
                               .shapeless()
                               .addMaterial('#', EndBlocks.CAVE_PUMPKIN)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder37 = RecipeBuilder.crafting(
                BetterEnd.C.mk("neon_cactus_block"),
                EndBlocks.NEON_CACTUS_BLOCK
        );
        craftingRecipeBuilder37.shape("##", "##")
                               .addMaterial('#', EndBlocks.NEON_CACTUS)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder36 = RecipeBuilder.crafting(
                BetterEnd.C.mk("neon_cactus_block_slab"),
                EndBlocks.NEON_CACTUS_BLOCK_SLAB
        );
        CraftingRecipeBuilder craftingRecipeBuilder14 = craftingRecipeBuilder36.shape("###");
        craftingRecipeBuilder14.outputCount(6)
                               .addMaterial('#', EndBlocks.NEON_CACTUS_BLOCK)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder35 = RecipeBuilder.crafting(
                BetterEnd.C.mk("neon_cactus_block_stairs"),
                EndBlocks.NEON_CACTUS_BLOCK_STAIRS
        );
        CraftingRecipeBuilder craftingRecipeBuilder13 = craftingRecipeBuilder35.shape("#  ", "## ", "###");
        craftingRecipeBuilder13.outputCount(4)
                               .addMaterial('#', EndBlocks.NEON_CACTUS_BLOCK)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder34 = RecipeBuilder.crafting(
                BetterEnd.C.mk("sugar_from_root"),
                Items.SUGAR
        );
        craftingRecipeBuilder34.shape("###")
                               .addMaterial('#', EndItems.AMBER_ROOT_RAW)
                               .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder12 = RecipeBuilder.crafting(
                BetterEnd.C.mk("endstone_flower_pot"),
                EndBlocks.END_STONE_SET.getBlock(FlowerPot.SLOT)
        );
        CraftingRecipeBuilder craftingRecipeBuilder33 = craftingRecipeBuilder12.outputCount(3);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder33.shape("# #", " # ")
                                                                             .addMaterial('#', Blocks.END_STONE_BRICKS);
        craftingRecipeBuilder.group("end_pots")
                             .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder11 = RecipeBuilder.crafting(
                BetterEnd.C.mk("dragon_bone_block"),
                EndBlocks.DRAGON_BONE_BLOCK
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
                EndBlocks.SMARAGDANT_CRYSTAL
        );
        craftingRecipeBuilder29.shape("##", "##")
                               .addMaterial('#', EndBlocks.SMARAGDANT_CRYSTAL_SHARD)
                               .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder28 = RecipeBuilder.crafting(
                BetterEnd.C.mk("tined_glass_from_smaragdant"), Blocks.TINTED_GLASS);
        craftingRecipeBuilder28.shape(" # ", "#G#", " # ")
                               .addMaterial('#', EndBlocks.SMARAGDANT_CRYSTAL_SHARD)
                               .addMaterial('G', Blocks.GLASS)
                               .build(context);
    }

    private static void registerLantern(RecipeBuilder.Context context, String name, Block lantern, Block slab) {
        CraftingRecipeBuilder craftingRecipeBuilder1 = RecipeBuilder.crafting(BetterEnd.C.mk(name), lantern);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder1.shape("S", "#", "S")
                                                                            .addMaterial('#', EndItems.CRYSTAL_SHARDS)
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

    private ItemStack waterPotion() {
        return stackFor(Items.POTION, Potions.WATER);
    }


    private static ItemStack stackFor(ItemLike content, Holder<Potion> potion) {
        final var stack = new ItemStack(content);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }
}
