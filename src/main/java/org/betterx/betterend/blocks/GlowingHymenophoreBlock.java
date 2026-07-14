package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;

public class GlowingHymenophoreBlock extends BaseBlock.Wood {
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
