package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.complexmaterials.MetalMaterial;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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
        // BaseOreBlock retired: a plain DropExperienceBlock whose properties keep the original 3/9 SAND
        // STONE values, plus ORE_BLOCK.dropping(rawOre, 1, 3), which bundles the ORE_BLOCK classification
        // (c:ores + pickaxe tag) with the parameterized ore-loot trait - together reproducing what
        // BaseOreBlock's BehaviourOre marker and BlockLootProvider used to supply.
        //
        // ORE_BLOCK's own trait (added below via addTrait) already sets instrument=BASEDRUM/reqTool/
        // strength(3,9)/sound=STONE, but this slot's addTrait() call happens inside startBlockDefinition(),
        // which BlockSet.createDefaultDefinitions() runs *before* MetalMaterial.addCommonBlockDefinitions()
        // applies its metal defaults (mapColor(METAL)/instrument(IRON_XYLOPHONE)/strength(5,6)/sound(IRON))
        // and the per-metal settingsSupplier color. Since BlockDefinition.build() applies every chained/
        // trait-configured property setter in call order (last write wins), those later metal defaults
        // would otherwise clobber ore's own values. Only the block factory - which builds the actual Block
        // instance and therefore runs after all of that - can still win, so the ore-specific
        // mapColor/instrument/strength/sound overrides have to be applied here, directly on
        // def.getProperties(), rather than as chain setters on def.
        return registry.defineDefaultBlock(
                name,
                (def) -> new DropExperienceBlock(
                        UniformInt.of(1, 1),
                        def.getProperties()
                           .mapColor(MapColor.SAND)
                           .instrument(NoteBlockInstrument.BASEDRUM)
                           .requiresCorrectToolForDrops()
                           .destroyTime(3F)
                           .explosionResistance(9F)
                           .sound(SoundType.STONE)
                )
        )
                .addTrait(BlockTraits.ORE_BLOCK.dropping(() -> metalMaterial.rawOre, 1, 3));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTags(BlockTags.DRAGON_IMMUNE);
        def.addItemTags(metalMaterial.alloyingOre);
    }

    @Override
    protected BlockModelBinding buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModelTraitLibrary.cube();
    }
}
