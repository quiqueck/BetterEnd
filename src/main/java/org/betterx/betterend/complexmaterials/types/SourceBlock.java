package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.complexmaterials.MetalMaterial;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.tags.BlockTags;

public class SourceBlock extends SlotFromDefinition {
    private final MetalMaterial metalMaterial;

    public SourceBlock(MetalMaterial metalMaterial) {
        super(SlotType.SOURCE);
        this.metalMaterial = metalMaterial;
    }

    @Override
    public String getName(BlockSet<?> set) {
        return set.baseName + "_block";
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTags(BlockTags.BEACON_BASE_BLOCKS);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder.crafting(key.location(), block)
                                 .shape("###", "###", "###")
                                 .addMaterial('#', metalMaterial.equipment.ingot)
                                 .group("end_metal_blocks")
                                 .build(context);
                }
        );
    }

    @Override
    protected BlockModelBinding buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.cube();
    }
}
