package org.betterx.betterend.complexmaterials.types;

import org.betterx.bclib.blocks.LeveledAnvilBlock;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.tags.BlockTags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Anvil extends SlotFromDefinition {
    public final int level;

    public Anvil(int level) {
        super(MetalMaterial.ANVIL);
        this.level = level;
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(
                name, (def) -> new LeveledAnvilBlock(
                        def.getProperties(),
                        level
                )
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTags(BlockTags.ANVIL);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.identifier(), block)
                            .shape("###", " I ", "III")
                            .addMaterial('#', set.getBaseBlock(), set.getBlock(MetalMaterial.TILE))
                            .addMaterial('I', set.getItem(MetalMaterial.INGOT))
                            .group("end_metal_anvil")
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return LeveledAnvilBlock.buildModel(set, traitLookup);
    }



    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (an anvil), so it must not inherit the set material's sulfur cube archetype - a
        // cube renders what it swallowed as a block model, and an anvil inside one reads as a bug.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
