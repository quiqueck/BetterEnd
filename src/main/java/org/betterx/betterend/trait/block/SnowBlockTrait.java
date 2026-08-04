package org.betterx.betterend.trait.block;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

public class SnowBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> {
    private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(BetterEnd.C, "snow");

    public static List<BlockTrait<?, ?>> withDefault() {
        return Combiner.of(
                new SnowBlockTrait(),
                BlockTraits.LOOT_TABLE.dropSelf(),
                ModelTraitLibrary.cube(),
                BlockTraits.MINEABLE_WITH.needsShovel()
        ).combine();
    }

    private SnowBlockTrait() {
    }

    @Override
    public BlockTraitKey key() {
        return KEY;
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition
                .mapColor(MapColor.SNOW)
                .requiresCorrectToolForDrops()
                .strength(0.2F)
                .sound(SoundType.SNOW);
        ;
    }
}