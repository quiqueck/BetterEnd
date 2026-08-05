package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.bclib.trait.block.CopyPropertiesBlockTrait;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromBlock;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanillaMetalSet extends BlockSet<VanillaMetalSet> {
    protected final Block baseBlock;
    protected final SlotMap slotMap;
    protected final Item ingot;

    public VanillaMetalSet(
            @NotNull String baseName,
            Block baseBlock,
            Item ingot,
            SlotMap slots
    ) {
        super(BetterEnd.C, baseName, SlotType.SOURCE);
        this.baseBlock = baseBlock;
        this.ingot = ingot;
        this.slotMap = slots.add(new SlotFromBlock(SlotType.SOURCE, baseBlock));
        this.buildAndRegister();
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        blockDefinition
                // Base-copy as a trait (guard-exempt, runs during build()); METAL_BLOCK layers on top.
                .addTrait(CopyPropertiesBlockTrait.of(baseBlock))
                .addTrait(BlockTraits.METAL_BLOCK);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return slotMap;
    }

    @Override
    public @Nullable Block getBlock(@NotNull SlotType type) {
        if (type == MetalMaterial.CHAIN) {
            return Blocks.IRON_CHAIN;
        }
        return super.getBlock(type);
    }

    @Override
    public @Nullable Item getItem(@NotNull SlotType type) {
        if (type == MetalMaterial.INGOT) {
            return this.ingot;
        }
        return super.getItem(type);
    }
}
