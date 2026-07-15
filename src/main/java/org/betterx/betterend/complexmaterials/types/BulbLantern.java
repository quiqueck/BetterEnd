package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.blocks.BulbVineLanternBlock;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndItems;
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

import net.minecraft.world.level.block.SoundType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BulbLantern extends SlotFromDefinition {
    public static final BulbLantern SLOT = new BulbLantern();

    private BulbLantern() {
        super(MetalMaterial.BULB_LANTERN);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, (def) -> new BulbVineLanternBlock(def.getProperties()));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTrait(ClientBlockTraits.RENDER_LAYER.cutout());
        def.addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe());
        def.getProperties()
           .destroyTime(1)
           .explosionResistance(1)
           .lightLevel((bs) -> 15)
           .sound(SoundType.LANTERN);
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("C", "I", "#")
                            .addMaterial('C', set.getBlock(MetalMaterial.CHAIN))
                            .addMaterial('I', set.getItem(MetalMaterial.INGOT))
                            .addMaterial('#', EndItems.GLOWING_BULB)
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BulbVineLanternBlock.buildModel(set, traitLookup);
    }
}
