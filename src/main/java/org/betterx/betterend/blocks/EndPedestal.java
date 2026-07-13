package org.betterx.betterend.blocks;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EndPedestal extends PedestalBlock {
    public EndPedestal(BlockBehaviour.Properties props) {
        super(props);
    }


    @Environment(EnvType.CLIENT)
    protected static TextureMapping createTextureMapping(Block parent) {
        final var parentTexture = BetterEnd.C.convertNamespace(TextureMapping.getBlockTexture(parent));
        final var polishedTexture = BetterEnd.C.convertNamespace(TextureMapping.getBlockTexture(parent, "_polished"));
        return new TextureMapping()
                .put(TextureSlot.TOP, polishedTexture)
                .put(TextureSlot.BOTTOM, polishedTexture)
                .put(EndModels.BASE, polishedTexture)
                .put(EndModels.PILLAR, parentTexture.withSuffix("_pillar_side"));
    }


    /**
     * Kept in a separate class file (not just an @Environment(CLIENT) method) since this Block is
     * always loaded on the server; a lambda body's synthetic method does not inherit the
     * annotation from its enclosing method, so leaving it here would strand vanilla client-only
     * type references (TextureMapping via createTextureMapping's return type) in a class file the
     * server actually has to verify.
     */
    public static BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ClientModel.build(set);
    }

    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build(BlockSet<?> set) {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> {
                        provideBlockModel(generator, createTextureMapping(set.getBaseBlock()), block);
                    }
            );
        }
    }
}
