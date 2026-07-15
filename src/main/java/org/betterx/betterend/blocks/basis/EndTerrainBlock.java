package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.BaseTerrainBlock;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EndTerrainBlock extends BaseTerrainBlock implements BlockTagProvider {
    public EndTerrainBlock(BlockBehaviour.Properties properties) {
        super(properties, Blocks.END_STONE);
    }

    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(CommonBlockTags.END_STONES, this);
    }
}
