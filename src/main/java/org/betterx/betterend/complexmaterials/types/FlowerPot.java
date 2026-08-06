package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.blocks.FlowerPotBlock;
import static org.betterx.betterend.blocks.FlowerPotBlock.POT_LIGHT;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.recipe.api.RecipeMaterial;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlowerPot extends SlotFromDefinition {
    public static final FlowerPot SLOT = new FlowerPot();

    private FlowerPot() {
        super(StoneMaterial.FLOWER_POT);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(
                name, (def) -> new FlowerPotBlock(def.getProperties())
        );
    }

    public static BlockDefinition<?, ?> asFlowerPot(BlockDefinition<?, ?> def) {
        return def.lightLevel(state -> state.getValue(POT_LIGHT) * 5)
                  .addTrait(ClientBlockTraits.RENDER_LAYER.cutout());
    }

    public static BlockRecipeTrait recipe(RecipeMaterial brick) {
        return BlockTraits.RECIPE.with(potRecipe(brick));
    }

    private static BlockRecipeTrait.RecipeFactory potRecipe(RecipeMaterial brick) {
        return (key, block, context) -> {
            RecipeBuilder
                    .crafting(key.identifier(), block)
                    .outputCount(3)
                    .shape("# #", " # ")
                    .addMaterial('#', brick)
                    .group("end_pots")
                    .build(context);
        };
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        asFlowerPot(def);
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        final BlockRecipeTrait.RecipeFactory potRecipe = potRecipe(set.recipeMaterial(SlotType.BRICK));
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    // Sets without a BRICK slot (the vanilla-backed stone sets) cannot name their matching vanilla
                    // brick here; a fallback would silently resolve to the source block and emit a second pot
                    // recipe - from full blocks - under the block's own ID. Those sets declare the recipe by hand
                    // instead (EndCraftingRecipesProvider), so skip rather than emit a wrong recipe.
                    if (set.getBlock(SlotType.BRICK) == null) return;

                    potRecipe.buildRecipe(key, block, context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return FlowerPotBlock.buildModel();
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a flower pot), so it must not inherit the set material's sulfur cube archetype -
        // a cube renders what it swallowed as a block model, and a flower pot inside one reads as a bug.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
