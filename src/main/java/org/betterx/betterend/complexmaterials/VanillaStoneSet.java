package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromBlock;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

public class VanillaStoneSet extends BlockSet<VanillaStoneSet> {
    protected final Block baseBlock;
    protected final SlotMap slotMap;

    public VanillaStoneSet(
            @NotNull String baseName,
            Block baseBlock,
            SlotMap slots
    ) {
        super(BetterEnd.C, baseName, SlotType.SOURCE);
        this.baseBlock = baseBlock;
        this.slotMap = slots.add(new SlotFromBlock(SlotType.SOURCE, baseBlock));
        this.buildAndRegister();
    }

    @Override
    protected void addBaseBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        // Base-copy must be the chain-start; this hook runs before the slot's own configuration.
        blockDefinition.replacePropertiesWithCopy(baseBlock);
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        blockDefinition.addTrait(BlockTraits.STONE_BLOCK);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return slotMap;
    }
}
