package org.betterx.betterend.complexmaterials.types;


import de.ambertation.wover.block.api.render.BlockRenderTraits;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.blocks.BulbVineLanternBlock;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

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
        def.addTrait(BlockRenderTraits.RENDER_LAYER.cutout());
        def.addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe());
        // noOcclusion() moved out of EndLanternBlock's constructor (R1, WP6.14) to here.
        def.noOcclusion();
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.identifier(), block)
                            .shape("C", "I", "#")
                            .addMaterial('C', set.getBlock(MetalMaterial.CHAIN))
                            .addMaterial('I', set.getItem(MetalMaterial.INGOT))
                            .addMaterial('#', EndResourceItems.GLOWING_BULB)
                            .build(context);
                });
    }

    @Override
    protected BlockModelBinding buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BulbVineLanternBlock.buildModel(set, traitLookup);
    }

    @Override
    protected void finalizeBlockDefinitions(
            BlockSet<?> set,
            BlockDefinition<?, ? extends BlockDefinition<?, ?>> def,
            BiConsumer<SlotType, Block> blockDefinitionConsumer
    ) {
        // The lantern's own strength, sound and light go here, after the set material: METAL_BLOCK and the
        // material's strength/sound setters run in the common hook (after addSlotSpecificDefinitions), and
        // VanillaMetalSet's copy-base trait replaces the properties wholesale, so anything set earlier was
        // lost (the iron bulb lantern gave no light at all). This branch has no finalizeDefinitions hook, so
        // the setters are queued here, right before the definition is built.
        def.strength(1.0F)
           .lightLevel((bs) -> 15)
           .sound(SoundType.LANTERN);
        super.finalizeBlockDefinitions(set, def, blockDefinitionConsumer);
    }
}
