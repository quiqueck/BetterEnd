package org.betterx.betterend.blocks;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.models.EndModels;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class HydraluxPetalColoredBlock extends HydraluxPetalBlock {
    public HydraluxPetalColoredBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }


    private static Identifier PETAL_MODEL;

    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(WoverBlockModelGenerators generator, Block block) {
        final var modelLocation = BetterEnd.C.mk("block/hydralux_petal_block_colored");
        final var mapping = new TextureMapping().put(
                TextureSlot.TEXTURE,
                new Material(BetterEnd.C.mk("block/hydralux_petal_block_colored"))
        );
        if (PETAL_MODEL == null)
            PETAL_MODEL = EndModels.PETAL_COLORED.create(modelLocation, mapping, generator.modelOutput());
        generator.acceptBlockState(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(PETAL_MODEL)));

        // Item tinting is data-driven via the item model's `tints` list and no longer follows BlockColors, so
        // this block declares ClientBlockTraits.ITEM_TINT at registration; delegating here emits that tint.
        generator.delegateItemModel(block, modelLocation);
    }
}
