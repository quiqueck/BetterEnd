package org.betterx.betterend.blocks;

import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.models.EndModels;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class HydraluxPetalColoredBlock extends HydraluxPetalBlock implements CustomColorProvider {
    public HydraluxPetalColoredBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public BlockColor getProvider() {
        return (state, world, pos, tintIndex) -> BlocksHelper.getBlockColor(this);
    }

    private static ResourceLocation PETAL_MODEL;

    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(WoverBlockModelGenerators generator, Block block) {
        final var modelLocation = BetterEnd.C.mk("block/hydralux_petal_block_colored");
        final var mapping = new TextureMapping().put(
                TextureSlot.TEXTURE,
                BetterEnd.C.mk("block/hydralux_petal_block_colored")
        );
        if (PETAL_MODEL == null)
            PETAL_MODEL = EndModels.PETAL_COLORED.create(modelLocation, mapping, generator.modelOutput());
        generator.acceptBlockState(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(PETAL_MODEL)));

        // Plain delegateItemModel() would reference the block model without a tint source, so the item
        // rendered untinted (white/grayscale) - in 1.21.6 item tinting is data-driven via the item model's
        // `tints` list, it no longer follows BlockColors automatically. Emit a constant tint from this
        // block's own CustomColorProvider instead (same fix as EndModelProvider's aurora crystal override).
        final int tint = ((CustomColorProvider) block).getProvider().getColor(block.defaultBlockState(), null, null, 0);
        generator.vanillaGenerator.itemModelOutput.accept(
                block.asItem(),
                ItemModelUtils.tintedModel(modelLocation, ItemModelUtils.constantTint(tint))
        );
        generator.markItemModelProvided(block);
    }
}
