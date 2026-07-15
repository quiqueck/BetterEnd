package org.betterx.betterend.blocks;

import net.minecraft.world.level.block.Block;
import org.betterx.wover.block.api.CustomBlockItemProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class AeterniumBlock extends Block implements CustomBlockItemProvider {
    public AeterniumBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public BlockItem getCustomBlockItem(ResourceLocation blockID, Item.Properties settings) {
        return new BlockItem(this, settings.fireResistant());
    }
}
