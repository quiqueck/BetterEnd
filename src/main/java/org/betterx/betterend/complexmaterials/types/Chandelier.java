package org.betterx.betterend.complexmaterials.types;


import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.blocks.ChandelierBlock;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndItems;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Chandelier extends SlotFromDefinition {
    public static final Chandelier SLOT = new Chandelier();

    private Chandelier() {
        super(MetalMaterial.CHANDELIER);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, (def) -> new ChandelierBlock(def.getProperties()));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTrait(ClientBlockTraits.RENDER_LAYER.cutout());
        def.lightLevel((bs) -> 15)
           .noCollission()
           .noOcclusion()
           .isValidSpawn((state, level, pos, entity) -> false)
           .requiresCorrectToolForDrops();
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("I#I", " # ")
                            .addMaterial('#', set.getItem(MetalMaterial.INGOT))
                            .addMaterial('I', EndResourceItems.LUMECORN_ROD)
                            .group("end_metal_chandelier")
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ChandelierBlock.buildModel(set, traitLookup);
    }
}
