package org.betterx.betterend.complexmaterials.types;


import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.blocks.basis.StoneLanternBlock;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.render.TinterKeys;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

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
        // noOcclusion() moved out of EndLanternBlock's constructor (R1, WP6.14) to here.
        def.noOcclusion();
        def.lightLevel((bs) -> 15);
        // The glass slot uses the near-grayscale aurora_crystal texture, so the lantern shares AURORA_CRYSTAL's
        // palette instance - it used to express that by delegating to its provider - and bakes it into the item.
        def.addTrait(ClientBlockTraits.TINT.worldAndItem(
                TinterKeys.PALETTE_CYCLE,
                () -> EndCrystalBlocks.AURORA_PALETTE
        ));
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
                            .crafting(key.identifier(), block)
                            .shape("S", "#", "S")
                            .addMaterial('#', EndResourceItems.CRYSTAL_SHARDS)
                            .addMaterial('S', set.recipeMaterial(SlotType.SLAB))
                            .group("end_stone_lanterns")
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return StoneLanternBlock.buildModel(set, traitLookup);
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a 10x15x10 lantern), so it must not inherit the set material's sulfur cube
        // archetype - a cube renders what it swallowed as a block model, and a lantern inside one reads as
        // a bug.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
