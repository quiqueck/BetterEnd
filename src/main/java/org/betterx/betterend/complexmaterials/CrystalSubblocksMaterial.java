package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.EndPedestal;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.client.models.EndModelTraits;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.datagen.betterend.recipes.EndCraftingRecipesProvider;
import org.betterx.bclib.trait.block.TemplateModelTrait;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.recipe.api.CraftingRecipeBuilder;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class CrystalSubblocksMaterial implements MaterialManager.Material {
    /** The shared, hand-authored emissive stair templates every lit-crystal stair child model parents from. */
    private static final net.minecraft.resources.ResourceLocation LIT_STAIRS = BetterEnd.C.mk("block/lit_stairs");
    private static final net.minecraft.resources.ResourceLocation LIT_STAIRS_INNER = BetterEnd.C.mk("block/lit_stairs_inner");
    private static final net.minecraft.resources.ResourceLocation LIT_STAIRS_OUTER = BetterEnd.C.mk("block/lit_stairs_outer");
    /** The shared, hand-authored emissive wall templates the lit-crystal bricks wall child models parent from. */
    private static final net.minecraft.resources.ResourceLocation LIT_WALL_POST = BetterEnd.C.mk("block/lit_wall_post");
    private static final net.minecraft.resources.ResourceLocation LIT_WALL_SIDE = BetterEnd.C.mk("block/lit_wall_side");
    private static final net.minecraft.resources.ResourceLocation LIT_WALL_SIDE_TALL = BetterEnd.C.mk("block/lit_wall_side_tall");

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

        polished = EndBlocks.defineBlock(name + "_polished", Block::new)
                             .replacePropertiesWithCopy(source)
                             .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                             .addTrait(EndModelTraits.unshadedCube())
                             .buildAndRegister();
        tiles = EndBlocks.defineBlock(name + "_tiles", Block::new)
                          .replacePropertiesWithCopy(source)
                          .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                          .addTrait(EndModelTraits.unshadedCube())
                          .buildAndRegister();
        pillar = EndBlocks.defineBlock(name + "_pillar", RotatedPillarBlock::new)
                           .replacePropertiesWithCopy(source)
                           .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                           .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
                           .addTrait(ModelTraitLibrary.pillar())
                           .buildAndRegister();
        stairs = EndBlocks.defineBlock(name + "_stairs", p -> new StairBlock(source.defaultBlockState(), p))
                           .replacePropertiesWithCopy(source)
                           .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                           .addTrait(TemplateModelTrait.stairs(
                                   LIT_STAIRS, LIT_STAIRS_INNER, LIT_STAIRS_OUTER,
                                   BetterEnd.C.mk("block/" + name + "_top"),
                                   BetterEnd.C.mk("block/" + name + "_top"),
                                   BetterEnd.C.mk("block/" + name + "_side")))
                           .buildAndRegister();
        slab = EndBlocks.defineBlock(name + "_slab", SlabBlock::new)
                         .replacePropertiesWithCopy(source)
                         .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                         .addTrait(EndModelTraits.slabFrom(() -> source, () -> BetterEnd.C.mk("block/" + name)))
                         .buildAndRegister();
        wall = EndBlocks.defineBlock(name + "_wall", WallBlock::new)
                         .replacePropertiesWithCopy(source)
                         .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                         .addTrait(ModelTraitLibrary.externalModel())
                         .buildAndRegister();
        pedestal = EndBlocks.defineBlock(name + "_pedestal", EndPedestal::new)
                             .replacePropertiesWithCopy(source)
                             .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                             .addTrait(ModCore.isDatagen() ? ClientModel.buildPedestal(source) : null)
                             .addTags(EndTags.PEDESTALS)
                             .buildAndRegister();
        bricks = EndBlocks.defineBlock(name + "_bricks", Block::new)
                           .replacePropertiesWithCopy(source)
                           .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                           .addTrait(EndModelTraits.unshadedCube())
                           .buildAndRegister();
        brick_stairs = EndBlocks.defineBlock(name + "_bricks_stairs", p -> new StairBlock(bricks.defaultBlockState(), p))
                                 .replacePropertiesWithCopy(bricks)
                                 .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                                 .addTrait(TemplateModelTrait.stairs(
                                         LIT_STAIRS, LIT_STAIRS_INNER, LIT_STAIRS_OUTER,
                                         BetterEnd.C.mk("block/" + name + "_bricks"),
                                         BetterEnd.C.mk("block/" + name + "_bricks"),
                                         BetterEnd.C.mk("block/" + name + "_bricks")))
                                 .buildAndRegister();
        brick_slab = EndBlocks.defineBlock(name + "_bricks_slab", SlabBlock::new)
                               .replacePropertiesWithCopy(bricks)
                               .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                               .addTrait(ModCore.isDatagen() ? ClientModel.buildSlab(bricks) : null)
                               .buildAndRegister();
        brick_wall = EndBlocks.defineBlock(name + "_bricks_wall", WallBlock::new)
                               .replacePropertiesWithCopy(bricks)
                               .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
                               .addTrait(TemplateModelTrait.wall(
                                       LIT_WALL_POST, LIT_WALL_SIDE, LIT_WALL_SIDE_TALL,
                                       BetterEnd.C.mk("block/" + name + "_bricks")))
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