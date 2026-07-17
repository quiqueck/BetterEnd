package org.betterx.betterend.complexmaterials.types;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ore extends SlotFromDefinition {
    private final MetalMaterial metalMaterial;

    public Ore(MetalMaterial metalMaterial) {
        super(MetalMaterial.ORE);
        this.metalMaterial = metalMaterial;
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        // BaseOreBlock retired: a plain DropExperienceBlock whose ctor keeps the original 3/9 SAND STONE
        // properties (running last, it wins over MetalMaterial.addCommonBlockDefinitions' metal defaults),
        // plus the ORE_BLOCK trait (c:ores + pickaxe tag) and the parameterized ore-loot trait that together
        // reproduce what BaseOreBlock's BehaviourOre marker and BlockLootProvider used to supply.
        return registry.defineDefaultBlock(
                name,
                (def) -> new DropExperienceBlock(
                        UniformInt.of(1, 1),
                        BehaviourBuilders.createStone(def.getProperties(), MapColor.SAND)
                                .requiresCorrectToolForDrops()
                                .destroyTime(3F)
                                .explosionResistance(9F)
                                .sound(SoundType.STONE)
                )
        )
                .addTrait(BlockTraits.ORE_BLOCK)
                .addTrait(BlockTraits.LOOT_TABLE.dropOre(() -> metalMaterial.rawOre, 1, 3));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTags(BlockTags.DRAGON_IMMUNE);
        def.addItemTags(metalMaterial.alloyingOre);
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.cube();
    }
}
