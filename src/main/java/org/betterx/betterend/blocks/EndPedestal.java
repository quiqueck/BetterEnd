package org.betterx.betterend.blocks;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EndPedestal extends PedestalBlock {
    public EndPedestal(BlockBehaviour.Properties props) {
        super(props);
    }

    /**
     * Builds the pedestal texture mapping for a material's base block. BetterEnd stone materials follow the
     * {@code <material>_polished} / {@code <material>_pillar_side} convention, but the vanilla-backed sets
     * reuse a Minecraft block as their source and ship differently-named textures - so quartz, purpur and the
     * plain vanilla stones (andesite/diorite/granite) each redefine the texture keys to the textures they
     * actually have. This is the trait-side replacement for the old per-block pedestal model overrides.
     */
    @Environment(EnvType.CLIENT)
    public static TextureMapping createTextureMapping(Block parent) {
        if (parent == Blocks.QUARTZ_BLOCK) {
            return new TextureMapping()
                    .put(TextureSlot.TOP, WoverBlockModelGenerators.vanilla("quartz_pillar_top"))
                    .put(TextureSlot.BOTTOM, WoverBlockModelGenerators.vanilla("quartz_block_bottom"))
                    .put(EndModels.BASE, WoverBlockModelGenerators.vanilla("quartz_block_side"))
                    .put(EndModels.PILLAR, WoverBlockModelGenerators.vanilla("quartz_pillar"));
        }
        if (parent == Blocks.PURPUR_BLOCK) {
            return new TextureMapping()
                    .put(TextureSlot.TOP, WoverBlockModelGenerators.vanilla("purpur_pillar_top"))
                    .put(TextureSlot.BOTTOM, WoverBlockModelGenerators.vanilla("purpur_block"))
                    .put(EndModels.BASE, WoverBlockModelGenerators.vanilla("purpur_block"))
                    .put(EndModels.PILLAR, WoverBlockModelGenerators.vanilla("purpur_pillar"));
        }

        final var id = BuiltInRegistries.BLOCK.getKey(parent);
        if (id.getNamespace().equals("minecraft")) {
            // andesite / diorite / granite: vanilla "polished_<name>" faces + BetterEnd "<name>_pillar" column
            final var polished = WoverBlockModelGenerators.vanilla("polished_" + id.getPath());
            return new TextureMapping()
                    .put(TextureSlot.TOP, polished)
                    .put(TextureSlot.BOTTOM, polished)
                    .put(EndModels.BASE, polished)
                    .put(EndModels.PILLAR, BetterEnd.C.mk("block/" + id.getPath() + "_pillar"));
        }

        final var parentTexture = TextureMapping.getBlockTexture(parent);
        final var polishedTexture = TextureMapping.getBlockTexture(parent, "_polished");
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
