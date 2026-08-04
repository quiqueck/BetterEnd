package org.betterx.betterend.registry.block;

import org.betterx.bclib.api.v3.tag.BCLBlockTags;
import org.betterx.bclib.blocks.LeveledAnvilBlock;
import org.betterx.bclib.items.BaseAnvilItem;
import net.minecraft.core.Direction;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.trait.block.WeightedCrossModelTrait;
import org.betterx.bclib.blocks.StalactiteBlock;
import org.betterx.bclib.blocks.BasePlantBlock;
import org.betterx.bclib.blocks.BaseWallPlantBlock;
import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.blocks.UnderwaterPlantBlock;
import org.betterx.bclib.blocks.BaseTerrainBlock;
import org.betterx.bclib.blocks.BasePlantWithAgeBlock;
import org.betterx.bclib.trait.block.*;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.*;
import org.betterx.betterend.blocks.EndPortalBlock;
import org.betterx.betterend.blocks.basis.*;
import org.betterx.betterend.client.models.EndModelTraits;
import org.betterx.betterend.complexmaterials.*;
import org.betterx.betterend.complexmaterials.types.*;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.item.material.EndToolTier;
import org.betterx.betterend.trait.block.IceBlockTrait;
import org.betterx.betterend.trait.block.SnowBlockTrait;
import org.betterx.bclib.trait.block.StalactiteBlockTrait;
import org.betterx.bclib.trait.block.TerrainTraits;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.DefaultBlockDefinition;
import de.ambertation.wover.block.api.VanillaBlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.item.api.BlockItemDefinition;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.recipe.api.RecipeMaterial;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.registry.EndTemplates;

public class EndWoodBlocks {
    /**
     * Sapling drop chance for leaf blocks whose canopy is a large solid volume rather than the thin shell a
     * vanilla tree has - the umbrella tree's membranes and the helix tree's leaf spiral are both several
     * hundred to a few thousand blocks each. A vanilla-leaf rate (~1/16) over that many blocks pays out
     * dozens of saplings per tree; this lands at a handful for stripping a whole canopy.
     * <p>
     * Note this is a probability, not the 1-in-N denominator the pre-trait {@code FurBlock}/{@code getDrops}
     * code took. Passing a bare {@code 128} here would be a >100% chance - see
     * {@code LootTableTrait.Builder#dropLeaves(float, Block)}, which now rejects that.
     */
    private static final float CANOPY_SAPLING_CHANCE = 1F / 128F;

    public static final Block MOSSY_GLOWSHROOM_CAP = EndBlocks.defineBlock("mossy_glowshroom_cap", MossyGlowshroomCapBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    // Plant-derived light block, shroomlight-archetype (REVIEWED, per user decision): was instabreak
    // (destroyTime 0.0), axe-mineable, sound=WART_BLOCK. lightLevel(15) was already correct and is now
    // supplied by the trait instead of the direct call. No mapColor call before or after (stays default/NONE).
    public static final Block MOSSY_GLOWSHROOM_HYMENOPHORE = EndBlocks.defineBlock(
            "mossy_glowshroom_hymenophore",
            GlowingHymenophoreBlock::new
    ).addTrait(BlockTraits.PLANT_LIGHT_BLOCK.withDefault())
     .addTrait(CompostableBlockTrait.withChance(0.65f))
     .addTrait(ModelTraitLibrary.externalModel())
     .addTrait(BlockTraits.LOOT_TABLE)
     .buildAndRegister();

    public static final Block MOSSY_GLOWSHROOM_FUR = EndBlocks.defineBlock("mossy_glowshroom_fur", FurBlock::new)
            // FurBlock decorative variant: keep pass-through (walkable=false), unlike genuine cube leaves.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_LIGHT_BLUE, 15, true, 1F / 16F, EndSaplingBlocks.MOSSY_GLOWSHROOM_SAPLING, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(ModelTraitLibrary.externalModelFlatItem(null))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial MOSSY_GLOWSHROOM = new EndWoodenComplexMaterial(
            "mossy_glowshroom",
            MapColor.COLOR_GRAY,
            MapColor.WOOD
    ).setFurnitureCloth(Blocks.GRAY_WOOL)
     .setLogVariants(1, 1, 1, 1, 1)
     .setStrippedVariants(1, 1, 1, 1, 1)
     .buildAndRegister();

    public static final Block PYTHADENDRON_LEAVES = EndBlocks.defineBlock("pythadendron_leaves", p -> new TintedParticleLeavesBlock(0.01F, p))
            // generateModel=false: LeavesBlockTrait's own model factory would give the item a flat
            // 2D icon and, since block model traits don't dedupe, collide with the one below - supply
            // our own cube model + properly item-delegated model instead.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, -1, EndSaplingBlocks.PYTHADENDRON_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial PYTHADENDRON = new EndWoodenComplexMaterial(
            "pythadendron",
            MapColor.COLOR_MAGENTA,
            MapColor.COLOR_PURPLE
    ).setFurnitureCloth(Blocks.BLACK_WOOL)
     .setLogVariants(1, 1, 1, 1)
     .buildAndRegister();

    public static final Block END_LOTUS_STEM = EndBlocks.defineBlock("end_lotus_stem", EndLotusStemBlock::new)
            .addTrait(BlockTraits.WOOD_BLOCK)
            .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem())
            .addTrait(BlockTraits.LOOT_TABLE.dropSelf())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial END_LOTUS = new EndWoodenComplexMaterial(
            "end_lotus",
            MapColor.COLOR_LIGHT_BLUE,
            MapColor.COLOR_CYAN
    ).setFurnitureCloth(Blocks.LIGHT_BLUE_WOOL)
     // end_lotus is a plant, not wood - its boat is a raft (like vanilla's bamboo raft), not a real boat
     .useRaft()
     .buildAndRegister();

    public static final Block LACUGROVE_LEAVES = EndBlocks.defineBlock("lacugrove_leaves", p -> new TintedParticleLeavesBlock(0.01F, p))
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_CYAN, 0, false, -1, EndSaplingBlocks.LACUGROVE_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial LACUGROVE = new EndWoodenComplexMaterial(
            "lacugrove",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_YELLOW
    ).setFurnitureCloth(Blocks.CYAN_WOOL)
     .setLogVariants(1, 1, 1, 1)
     .buildAndRegister();

    public static final Block DRAGON_TREE_LEAVES = EndBlocks.defineBlock("dragon_tree_leaves", p -> new TintedParticleLeavesBlock(0.01F, p))
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_MAGENTA, 0, false, -1, EndSaplingBlocks.DRAGON_TREE_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.cube())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial DRAGON_TREE = new EndWoodenComplexMaterial(
            "dragon_tree",
            MapColor.COLOR_BLACK,
            MapColor.COLOR_MAGENTA
    ).setFurnitureCloth(Blocks.BLACK_WOOL)
     .setLogVariants(1, 1, 1, 1)
     .buildAndRegister();

    public static final Block TENANEA_LEAVES = EndBlocks.defineBlock("tenanea_leaves", p -> new TintedParticleLeavesBlock(0.01F, p))
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_PINK, 0, false, -1, EndSaplingBlocks.TENANEA_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            // Restores the pre-migration no-ambient-occlusion look: wover's ModelTraitLibrary.cube() parents
            // minecraft:block/cube_all (AO on), which darkened the leaves' interior faces. cube_noshade has
            // "shade": false on every face.
            .addTrait(NoAmbientOcclusionCubeModelTrait.withParent(BetterEnd.C.mk("block/cube_noshade")))
            .buildAndRegister();

    public static final Block TENANEA_FLOWERS = EndBlocks.defineBlock("tenanea_flowers", TenaneaFlowersBlock::new)
            // mapColor=PLANT: TenaneaFlowersBlock's now-removed constructor mutation hardcoded
            // MapColor.PLANT regardless of this trait's color argument, silently overriding the
            // COLOR_PINK declared here. Corrected to PLANT to match the frozen golden (mapColor=7).
            .addTrait(VineBlockTrait.withColor(MapColor.PLANT, 15, false, false))
            .addTrait(ModelTraitLibrary.externalModel())
            .buildAndRegister();

    // defineBlock, NOT defineBlockOnly: this block shipped a BlockItem (and a shears/silk-touch self-drop)
    // through 21.0.11. The item was lost when the wover migration made it block-only to sidestep an item-model
    // question, which in turn forced the empty loot table below it. Both are restored here - existing worlds
    // hold betterend:tenanea_outer_leaves stacks in containers that would otherwise be dropped on load.
    public static final Block TENANEA_OUTER_LEAVES = EndBlocks.defineBlock("tenanea_outer_leaves", FurBlock::new)
            // FurBlock decorative variant: keep pass-through (walkable=false), unlike genuine cube leaves.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_PINK, 0, false, 1F / 32F, EndSaplingBlocks.TENANEA_SAPLING, false, false))
            .addTrait(VegetationTagTrait.plant())
            // Flat item/generated icon over the block texture - what the hand-authored
            // models/item/tenanea_outer_leaves.json was before the migration dropped it.
            .addTrait(EndModelTraits.upLeaf(BetterEnd.C.mk("block/tenanea_outer_leaves"),
                    WeightedTemplateModelTrait.Item.flat(BetterEnd.C.mk("block/tenanea_outer_leaves"))))
            .buildAndRegister();

    public static final EndWoodenComplexMaterial TENANEA = new EndWoodenComplexMaterial(
            "tenanea",
            MapColor.COLOR_BROWN,
            MapColor.COLOR_PINK
    ).setFurnitureCloth(Blocks.PINK_WOOL)
     .buildAndRegister();

    public static final Block HELIX_TREE_LEAVES = EndBlocks.defineBlock("helix_tree_leaves", HelixTreeLeavesBlock::new)
            // CANOPY_SAPLING_CHANCE: HelixTreeFeature draws the leaf spiral as ~25 stacked, radius-8-to-11
            // filled discs, so a tree is on the order of a thousand leaf blocks - see the constant.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 0, false, CANOPY_SAPLING_CHANCE, EndSaplingBlocks.HELIX_TREE_SAPLING, false))
            // Its model is a plain texture-swap child of the shared betterend:block/tint_cube (tinted full cube),
            // single-variant, item delegated to the block model - generate it instead of hand-authoring.
            .addTrait(TemplateModelTrait.cube(BetterEnd.C.mk("block/tint_cube"), false))
            .sound(SoundType.WART_BLOCK)
            .buildAndRegister();

    public static final EndWoodenComplexMaterial HELIX_TREE = new EndWoodenComplexMaterial(
            "helix_tree",
            MapColor.COLOR_GRAY,
            MapColor.COLOR_ORANGE
    ).setFurnitureCloth(Blocks.GRAY_WOOL)
     .buildAndRegister();

    public static final Block UMBRELLA_TREE_MEMBRANE = EndBlocks.defineBlock(
            "umbrella_tree_membrane",
            UmbrellaTreeMembraneBlock::new
    ).replacePropertiesWithCopy(Blocks.SLIME_BLOCK)
     // Translucent canopy membrane: solid (walkable=true) so players can stand on the umbrella-tree canopy.
     // generateModel=false so LeavesBlockTrait does not attach its default cube-block + FLAT-item model;
     // instead ModelTraitLibrary.cube() supplies a cube block model whose inventory item is the 3D block
     // (delegated item model), matching how the membrane rendered as a solid block item previously.
     // CANOPY_SAPLING_CHANCE: UmbrellaTreeFeature builds 1-3 filled dome shells of radius 4-12 per tree,
     // so a canopy is several hundred membrane blocks - see the constant.
     .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_BLUE, 0, false, CANOPY_SAPLING_CHANCE, EndSaplingBlocks.UMBRELLA_TREE_SAPLING, false, true))
     .addTrait(ModelTraitLibrary.cube())
     .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
     .buildAndRegister();

    public static final Block UMBRELLA_TREE_CLUSTER = EndBlocks.defineBlock(
            "umbrella_tree_cluster",
            UmbrellaTreeClusterBlock::new
    ).replacePropertiesWithCopy(Blocks.NETHER_WART_BLOCK)
     .addTrait(BlockTraits.WOOD_BLOCK)
     .addTrait(BlockTraits.LOOT_TABLE)
     .addTrait(ModelTraitLibrary.cube())
     .mapColor(MapColor.COLOR_PURPLE)
     .lightLevel((bs) -> 15)
     .buildAndRegister();

    public static final Block UMBRELLA_TREE_CLUSTER_EMPTY = EndBlocks.defineBlock(
            "umbrella_tree_cluster_empty",
            UmbrellaTreeClusterEmptyBlock::new
    ).replacePropertiesWithCopy(Blocks.NETHER_WART_BLOCK)
     .addTrait(BlockTraits.WOOD_BLOCK)
     .addTrait(BlockTraits.LOOT_TABLE)
     .addTrait(ModelTraitLibrary.cube())
     .mapColor(MapColor.COLOR_PURPLE)
     .randomTicks()
     .buildAndRegister();

    public static final EndWoodenComplexMaterial UMBRELLA_TREE = new EndWoodenComplexMaterial(
            "umbrella_tree",
            MapColor.COLOR_BLUE,
            MapColor.COLOR_GREEN
    ).setFurnitureCloth(Blocks.MAGENTA_WOOL)
     .buildAndRegister();

    public static final Block JELLYSHROOM_CAP_PURPLE = EndBlocks.defineBlock(
            "jellyshroom_cap_purple",
            p -> new JellyshroomCapBlock(
                    p,
                    217, 142, 255,
                    164, 0, 255
            )
    ).replacePropertiesWithCopy(Blocks.SLIME_BLOCK)
     .mapColor(MapColor.COLOR_PURPLE)
     .addTrait(BlockTraits.LOOT_TABLE)
     .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
     .addTrait(ModCore.isDatagen() ? ClientBlockTraits.MODEL.with(
             (key, block, generator) -> JellyshroomCapBlock.provideBlockModel(generator, block)
     ) : null)
     .buildAndRegister();

    public static final EndWoodenComplexMaterial JELLYSHROOM = new EndWoodenComplexMaterial(
            "jellyshroom",
            MapColor.COLOR_PURPLE,
            MapColor.COLOR_LIGHT_BLUE
    ).setFurnitureCloth(Blocks.PURPLE_WOOL)
     .buildAndRegister();

    public static final Block LUCERNIA_LEAVES = EndBlocks.defineBlock("lucernia_leaves", p -> new TintedParticleLeavesBlock(0.01F, p))
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_ORANGE, 0, false, -1, EndSaplingBlocks.LUCERNIA_SAPLING, false))
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(ModelTraitLibrary.externalModelDelegatedItem(() -> BetterEnd.C.mk("block/lucernia_leaves_1")))
            .buildAndRegister();

    // defineBlock, NOT defineBlockOnly: same regression as TENANEA_OUTER_LEAVES above - it shipped a BlockItem
    // and a shears/silk-touch self-drop through 21.0.11 and lost both to the same migration commit.
    public static final Block LUCERNIA_OUTER_LEAVES = EndBlocks.defineBlock("lucernia_outer_leaves", FurBlock::new)
            // FurBlock decorative variant: keep pass-through (walkable=false), unlike genuine cube leaves.
            .addTrait(LeavesBlockTrait.withColor(MapColor.COLOR_RED, 0, false, 1F / 32F, EndSaplingBlocks.LUCERNIA_SAPLING, false, false))
            .addTrait(VegetationTagTrait.plant())
            .addTrait(EndModelTraits.lucerniaOuterLeaves())
            .buildAndRegister();

    public static final EndWoodenComplexMaterial LUCERNIA = new EndWoodenComplexMaterial(
            "lucernia",
            MapColor.COLOR_ORANGE,
            MapColor.COLOR_ORANGE
    ).setFurnitureCloth(Blocks.WHITE_WOOL)
     .setLogVariants(16, 1, 16, 1)
     .buildAndRegister();

    public static final EndWoodenComplexMaterial LUCERNIA_JELLY = new JellyLucerniaWoodMaterial()
            .setFurnitureCloth(Blocks.BROWN_WOOL)
            .buildAndRegister();

    public static void ensureLoaded() {}
}
