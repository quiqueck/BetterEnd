package org.betterx.betterend.blocks;

import org.betterx.betterend.client.models.EndModels;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;

public class GlowingHymenophoreBlock extends Block {
    public GlowingHymenophoreBlock(Properties props) {
        super(props);
    }

    public static void provideUnshadedCubeModel(
            WoverBlockModelGenerators generator,
            Block glowingHymenophoreBlock
    ) {
        final var location = generator.createSimpleTemplatedBlock(
                glowingHymenophoreBlock,
                EndModels.CUBE_NO_SHADE,
                TextureMapping.defaultTexture(glowingHymenophoreBlock)
        );
        generator.delegateItemModel(glowingHymenophoreBlock, location);
    }
}
