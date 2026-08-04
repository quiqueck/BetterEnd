package org.betterx.betterend.trait.block;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.world.level.block.Block;

public class IceBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> {
    private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(BetterEnd.C, "ice");

    public final Block baseIceBlock;

    public static IceBlockTrait withBase(Block baseIceBlock) {
        return new IceBlockTrait(baseIceBlock);
    }

    private IceBlockTrait(Block baseIceBlock) {
        super();
        this.baseIceBlock = baseIceBlock;
    }

    @Override
    public BlockTraitKey key() {
        return KEY;
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition
                .replacePropertiesWithCopy(baseIceBlock)
                .addTags(MineableTags.PICKAXE)
                .addTrait(ClientBlockTraits.RENDER_LAYER.translucent())
                .addTrait(BlockTraits.LOOT_TABLE.dropWithSilktouch())
                .addTrait(ModelTraitLibrary.cube());
    }
}
