package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.blocks.basis.StoneLanternBlock;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoneLantern extends SlotFromDefinition {
    public static final StoneLantern SLOT = new StoneLantern();

    private StoneLantern() {
        super(StoneMaterial.LANTERN);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(
                name, (def) -> new StoneLanternBlock(def.getProperties())
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.lightLevel((bs) -> 15);
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    // Sets without a SLAB slot (the vanilla-backed stone sets) cannot name their matching vanilla
                    // slab here; a fallback would silently resolve to the source block and make the lantern
                    // craftable from full blocks. Those sets declare the recipe by hand instead
                    // (EndCraftingRecipesProvider#registerLantern), so skip rather than emit a wrong recipe.
                    if (set.getBlock(SlotType.SLAB) == null) return;

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("S", "#", "S")
                            .addMaterial('#', EndItems.CRYSTAL_SHARDS)
                            .addMaterial('S', set.recipeMaterial(SlotType.SLAB))
                            .group("end_stone_lanterns")
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return StoneLanternBlock.buildModel(set, traitLookup);
    }
}
