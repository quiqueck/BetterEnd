package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.bclib.trait.block.CopyPropertiesBlockTrait;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromBlock;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;

public class VanillaStoneSet extends BlockSet<VanillaStoneSet> {
    protected final Block baseBlock;
    protected final SlotMap slotMap;
    // null means "inherit from baseBlock via CopyPropertiesBlockTrait". Must be assigned before
    // buildAndRegister() below runs addCommonBlockDefinitions(), so a subclass constructor CANNOT set
    // this after calling super(...) - the super constructor's buildAndRegister() call already invokes
    // the (virtually dispatched) override by then, reading a field the subclass hasn't assigned yet.
    // VanillaVariantStoneMaterial hit exactly this: its mapColor override read as null every time,
    // because it only reads `this` reference and Java constructors are the one place `this.color`
    // isn't reliably up to date - see the protected constructor overload below.
    protected final MapColor colorOverride;

    public VanillaStoneSet(
            @NotNull String baseName,
            Block baseBlock,
            SlotMap slots
    ) {
        this(baseName, baseBlock, slots, null);
    }

    protected VanillaStoneSet(
            @NotNull String baseName,
            Block baseBlock,
            SlotMap slots,
            MapColor colorOverride
    ) {
        super(BetterEnd.C, baseName, SlotType.SOURCE);
        this.baseBlock = baseBlock;
        this.colorOverride = colorOverride;
        this.slotMap = slots.add(new SlotFromBlock(SlotType.SOURCE, baseBlock));
        this.buildAndRegister();
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        blockDefinition
                // Base-copy as a trait (guard-exempt, runs during build()); STONE_BLOCK layers on top.
                .addTrait(CopyPropertiesBlockTrait.of(baseBlock))
                .addTrait(BlockTraits.STONE_BLOCK);
        if (colorOverride != null) {
            blockDefinition.mapColor(colorOverride);
        }
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return slotMap;
    }
}
