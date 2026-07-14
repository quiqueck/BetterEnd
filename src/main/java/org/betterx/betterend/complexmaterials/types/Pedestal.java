package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.blocks.EndPedestal;
import static org.betterx.betterend.blocks.FlowerPotBlock.POT_LIGHT;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

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
                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("S", "#", "S")
                            .addMaterial('S', set.recipeMaterialWithFallback(SlotType.SLAB))
                            .addMaterial('#', set.recipeMaterialWithFallback(SlotType.PILLAR))
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
