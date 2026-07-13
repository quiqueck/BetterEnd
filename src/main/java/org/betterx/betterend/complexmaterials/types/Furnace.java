package org.betterx.betterend.complexmaterials.types;

import org.betterx.bclib.blocks.BaseFurnaceBlock;
import org.betterx.bclib.registry.BaseBlockEntities;
import static org.betterx.betterend.blocks.EndStoneSmelter.LIT;
import org.betterx.betterend.complexmaterials.StoneMaterial;
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
import org.betterx.wover.tag.api.predefined.CommonItemTags;
import org.betterx.wover.tag.api.predefined.CommonPoiTags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Furnace extends SlotFromDefinition {
    public static final Furnace SLOT = new Furnace();

    private Furnace() {
        super(StoneMaterial.FURNACE);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(
                name, (def) -> new BaseFurnaceBlock(def.getProperties())
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def
                .lightLevel(state -> state.getValue(LIT) ? 15 : 0)
                .addTrait(ClientBlockTraits.RENDER_LAYER.cutout())
                .addTrait(BlockTraits.VALID_BLOCK_ENTITY.with(BaseBlockEntities.FURNACE))
                .addTags(CommonPoiTags.ARMORER_WORKSTATION)
                .addItemTags(CommonItemTags.FURNACES);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("###", "# #", "###")
                            .addMaterial('#', set.getBaseBlock())
                            .group("end_stone_furnaces")
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BaseFurnaceBlock.buildModel(set, traitLookup);
    }
}
