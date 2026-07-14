package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.blocks.FlowerPotBlock;
import static org.betterx.betterend.blocks.FlowerPotBlock.POT_LIGHT;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.recipe.api.RecipeMaterial;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

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
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(3)
                            .shape("# #", " # ")
                            .addMaterial('#', brick)
                            .group("end_pots")
                            .build(context);
                });
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        asFlowerPot(def);
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return recipe(set.recipeMaterialWithFallback(SlotType.BRICK));
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return FlowerPotBlock.buildModel();
    }
}
