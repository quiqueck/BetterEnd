package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.complexmaterials.MetalMaterial;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;

public class Tile extends SlotFromDefinition {
    private final MetalMaterial metalMaterial;

    public Tile(MetalMaterial metalMaterial) {
        super(MetalMaterial.TILE);
        this.metalMaterial = metalMaterial;
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.identifier(), block)
                            .outputCount(4)
                            .shape("##", "##")
                            .addMaterial('#', metalMaterial.getBaseBlock())
                            .group("end_metal_tiles")
                            .build(context);

                    RecipeBuilder
                            .stonecutting(key.identifier().withPrefix("_stonecutting"), block)
                            .input(metalMaterial.getBaseBlock())
                            .group("end_metal_tiles")
                            .build(context);
                });
    }

    @Override
    protected BlockModelBinding buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.cube();
    }
}
