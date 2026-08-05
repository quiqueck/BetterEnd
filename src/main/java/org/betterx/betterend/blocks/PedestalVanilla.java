package org.betterx.betterend.blocks;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.client.models.EndModels;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class PedestalVanilla extends PedestalBlock {
    protected final Block parent;

    public PedestalVanilla(BlockBehaviour.Properties props, Block parent) {
        super(props);
        this.parent = parent;
    }

    @Environment(EnvType.CLIENT)
    protected TextureMapping createTextureMapping() {
        final var parentTexture = BuiltInRegistries.BLOCK.getKey(parent);
        final var name = parentTexture.getPath().replace("_block", "");
        final var baseTextureLocation = Identifier.fromNamespaceAndPath(parentTexture.getNamespace(), name);
        final var polishedTexture = baseTextureLocation.withPrefix("block/polished_");
        final var pillarTexture = BetterEnd.C.convertNamespace(baseTextureLocation
                .withPrefix("block/")
                .withSuffix("_pillar"));

        return new TextureMapping()
                .put(TextureSlot.TOP, new Material(polishedTexture))
                .put(TextureSlot.BOTTOM, new Material(polishedTexture))
                .put(EndModels.BASE, new Material(polishedTexture))
                .put(EndModels.PILLAR, new Material(pillarTexture));
    }
}
