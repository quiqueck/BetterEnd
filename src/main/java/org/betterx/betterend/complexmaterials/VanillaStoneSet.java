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
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        blockDefinition
                // Base-copy as a trait (guard-exempt, runs during build()); STONE_BLOCK layers on top.
                .addTrait(CopyPropertiesBlockTrait.of(baseBlock))
                .addTrait(BlockTraits.STONE_BLOCK);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return slotMap;
    }
}
