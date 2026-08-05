package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.blocks.EndPedestal;
import static org.betterx.betterend.blocks.FlowerPotBlock.POT_LIGHT;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.registry.EndTags;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Pedestal extends SlotFromDefinition {
    public static final Pedestal SLOT = new Pedestal();

    private Pedestal() {
        super(StoneMaterial.PEDESTAL);
    }

    public static BlockDefinition<?, ?> asPedestal(BlockDefinition<?, ?> def) {
        return def.lightLevel(state -> state.getValue(POT_LIGHT) * 5)
                  .addTrait(ClientBlockTraits.RENDER_LAYER.cutout());
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(
                name, (def) -> new EndPedestal(def.getProperties())
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTags(EndTags.PEDESTALS);
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    // Sets without SLAB/PILLAR slots (the vanilla-backed stone sets) cannot name their matching
                    // vanilla polished slab/pillar here; a fallback would silently resolve both to the source block
                    // and make the pedestal craftable from plain stone. Those sets declare the recipe by hand
                    // instead (EndCraftingRecipesProvider#registerPedestal), so skip rather than emit a wrong recipe.
                    if (set.getBlock(SlotType.SLAB) == null || set.getBlock(SlotType.PILLAR) == null) return;

                    RecipeBuilder
                            .crafting(key.identifier(), block)
                            .shape("S", "#", "S")
                            .addMaterial('S', set.recipeMaterial(SlotType.SLAB))
                            .addMaterial('#', set.recipeMaterial(SlotType.PILLAR))
                            .outputCount(2)
                            .group("end_stone_pedestal")
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return EndPedestal.buildModel(set, traitLookup);
    }
}
