package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.EndPedestal;
import org.betterx.betterend.blocks.basis.LitBaseBlock;
import org.betterx.betterend.blocks.basis.LitPillarBlock;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.client.models.EndModelTraits;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.datagen.betterend.recipes.EndCraftingRecipesProvider;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class CrystalSubblocksMaterial implements MaterialManager.Material {
    public final Block polished;
    public final Block tiles;
    public final Block pillar;
    public final Block stairs;
    public final Block slab;
    public final Block wall;
    public final Block pedestal;
    public final Block bricks;
    public final Block brick_stairs;
    public final Block brick_slab;
    public final Block brick_wall;
    private final Block source;
    private final String name;

    public CrystalSubblocksMaterial(String name, Block source) {
        this.source = source;
        this.name = name;

        polished = EndBlocks.defineBlock(name + "_polished", LitBaseBlock::new)
                             .replacePropertiesWithCopy(source)
                             .addTrait(ModCore.isDatagen() ? ClientModel.build() : null)
                             .buildAndRegister();
        tiles = EndBlocks.defineBlock(name + "_tiles", LitBaseBlock::new)
                          .replacePropertiesWithCopy(source)
                          .addTrait(ModCore.isDatagen() ? ClientModel.build() : null)
                          .buildAndRegister();
        pillar = EndBlocks.defineBlock(name + "_pillar", LitPillarBlock::new)
                           .replacePropertiesWithCopy(source)
                           .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                           .addTrait(ModelTraitLibrary.pillar())
                           .buildAndRegister();
        stairs = EndBlocks.defineBlock(name + "_stairs", p -> new StairBlock(source.defaultBlockState(), p))
                           .replacePropertiesWithCopy(source)
                           .addTrait(ModelTraitLibrary.externalModel())
                           .buildAndRegister();
        slab = EndBlocks.defineBlock(name + "_slab", SlabBlock::new)
                         .replacePropertiesWithCopy(source)
                         .addTrait(EndModelTraits.slabFrom(() -> source, () -> BetterEnd.C.mk("block/" + name)))
                         .buildAndRegister();
        wall = EndBlocks.defineBlock(name + "_wall", WallBlock::new)
                         .replacePropertiesWithCopy(source)
                         .addTrait(ModelTraitLibrary.externalModel())
                         .buildAndRegister();
        pedestal = EndBlocks.defineBlock(name + "_pedestal", EndPedestal::new)
                             .replacePropertiesWithCopy(source)
                             .addTrait(ModCore.isDatagen() ? ClientModel.buildPedestal(source) : null)
                             .buildAndRegister();
        bricks = EndBlocks.defineBlock(name + "_bricks", LitBaseBlock::new)
                           .replacePropertiesWithCopy(source)
                           .addTrait(ModCore.isDatagen() ? ClientModel.build() : null)
                           .buildAndRegister();
        brick_stairs = EndBlocks.defineBlock(name + "_bricks_stairs", p -> new StairBlock(bricks.defaultBlockState(), p))
                                 .replacePropertiesWithCopy(bricks)
                                 .addTrait(ModelTraitLibrary.externalModel())
                                 .buildAndRegister();
        brick_slab = EndBlocks.defineBlock(name + "_bricks_slab", SlabBlock::new)
                               .replacePropertiesWithCopy(bricks)
                               .addTrait(ModCore.isDatagen() ? ClientModel.buildSlab(bricks) : null)
                               .buildAndRegister();
        brick_wall = EndBlocks.defineBlock(name + "_bricks_wall", WallBlock::new)
                               .replacePropertiesWithCopy(bricks)
                               .addTrait(ModelTraitLibrary.externalModel())
                               .buildAndRegister();


        MaterialManager.register(this);
    }

    @Override
    public void registerBlockTags(TagBootstrapContext<Block> context) {
        context.add(BlockTags.STONE_BRICKS, bricks);
        context.add(BlockTags.WALLS, wall, brick_wall);
        context.add(BlockTags.SLABS, slab, brick_slab);
    }

    @Override
    public void registerItemTags(ItemTagBootstrapContext context) {
        context.add(ItemTags.SLABS, slab.asItem(), brick_slab.asItem());
        context.add(ItemTags.STONE_BRICKS, bricks.asItem());
        context.add(ItemTags.STONE_CRAFTING_MATERIALS, source.asItem());
        context.add(ItemTags.STONE_TOOL_MATERIALS, source.asItem());
    }

    @Override
    public void registerRecipes(RecipeBuilder.Context context) {
        CraftingRecipeBuilder craftingRecipeBuilder18 = RecipeBuilder.crafting(
                BetterEnd.C.mk(name + "_bricks"),
                bricks
        );
        CraftingRecipeBuilder craftingRecipeBuilder28 = craftingRecipeBuilder18.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder9 = craftingRecipeBuilder28.shape("##", "##")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder9.group("end_bricks")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder17 = RecipeBuilder.crafting(
                BetterEnd.C.mk(name + "_polished"),
                polished
        );
        CraftingRecipeBuilder craftingRecipeBuilder27 = craftingRecipeBuilder17.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder8 = craftingRecipeBuilder27.shape("##", "##")
                                                                              .addMaterial('#', bricks);
        craftingRecipeBuilder8.group("end_tile")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder16 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_tiles"), tiles);
        CraftingRecipeBuilder craftingRecipeBuilder26 = craftingRecipeBuilder16.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder7 = craftingRecipeBuilder26.shape("##", "##")
                                                                              .addMaterial('#', polished);
        craftingRecipeBuilder7.group("end_small_tile")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder25 = RecipeBuilder.crafting(
                BetterEnd.C.mk(name + "_pillar"),
                pillar
        );
        CraftingRecipeBuilder craftingRecipeBuilder6 = craftingRecipeBuilder25.shape("#", "#")
                                                                              .addMaterial('#', slab);
        craftingRecipeBuilder6.group("end_pillar")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder15 = RecipeBuilder.crafting(
                BetterEnd.C.mk(name + "_stairs"),
                stairs
        );
        CraftingRecipeBuilder craftingRecipeBuilder24 = craftingRecipeBuilder15.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder5 = craftingRecipeBuilder24.shape("#  ", "## ", "###")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder5.group("end_stone_stairs")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder14 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_slab"), slab);
        CraftingRecipeBuilder craftingRecipeBuilder23 = craftingRecipeBuilder14.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder4 = craftingRecipeBuilder23.shape("###")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder4.group("end_stone_slabs")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder13 = RecipeBuilder.crafting(
                BetterEnd.C.mk(name + "_bricks_stairs"),
                brick_stairs
        );
        CraftingRecipeBuilder craftingRecipeBuilder22 = craftingRecipeBuilder13.outputCount(4);
        CraftingRecipeBuilder craftingRecipeBuilder3 = craftingRecipeBuilder22.shape("#  ", "## ", "###")
                                                                              .addMaterial('#', bricks);
        craftingRecipeBuilder3.group("end_stone_stairs")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder12 = RecipeBuilder.crafting(
                BetterEnd.C.mk(name + "_bricks_slab"),
                brick_slab
        );
        CraftingRecipeBuilder craftingRecipeBuilder21 = craftingRecipeBuilder12.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder2 = craftingRecipeBuilder21.shape("###")
                                                                              .addMaterial('#', bricks);
        craftingRecipeBuilder2.group("end_stone_slabs")
                              .build(context);

        CraftingRecipeBuilder craftingRecipeBuilder11 = RecipeBuilder.crafting(BetterEnd.C.mk(name + "_wall"), wall);
        CraftingRecipeBuilder craftingRecipeBuilder20 = craftingRecipeBuilder11.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder1 = craftingRecipeBuilder20.shape("###", "###")
                                                                              .addMaterial('#', source);
        craftingRecipeBuilder1.group("end_wall")
                              .build(context);
        CraftingRecipeBuilder craftingRecipeBuilder10 = RecipeBuilder.crafting(
                BetterEnd.C.mk(name + "_bricks_wall"),
                brick_wall
        );
        CraftingRecipeBuilder craftingRecipeBuilder19 = craftingRecipeBuilder10.outputCount(6);
        CraftingRecipeBuilder craftingRecipeBuilder = craftingRecipeBuilder19.shape("###", "###")
                                                                             .addMaterial('#', bricks);
        craftingRecipeBuilder.group("end_wall")
                             .build(context);

        EndCraftingRecipesProvider.registerPedestal(context, name + "_pedestal", pedestal, slab, pillar);
    }

    /**
     * Kept in a separate class file (not just an @Environment(CLIENT)-guarded expression):
     * merely creating this lambda - even without ever invoking it - requires resolving the
     * client-only WoverBlockModelGenerators parameter type at the invokedynamic bootstrap site,
     * which throws immediately on a dedicated server. Gating with ModCore.isDatagen() keeps that
     * bootstrap instruction from ever executing there. See PathBlockTrait for the same pattern.
     */
    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> LitBaseBlock.provideBlockModel(generator, block)
            );
        }

        private static BlockModelTrait buildSlab(Block baseBlock) {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> generator.createSlab(block, baseBlock)
            );
        }

        private static BlockModelTrait buildPedestal(Block source) {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> PedestalBlock.provideBlockModel(
                            generator,
                            EndPedestal.createTextureMapping(source),
                            block
                    )
            );
        }
    }
}