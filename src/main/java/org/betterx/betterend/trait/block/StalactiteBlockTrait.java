package org.betterx.betterend.trait.block;

import static org.betterx.bclib.blocks.StalactiteBlock.IS_FLOOR;
import static org.betterx.bclib.blocks.StalactiteBlock.SIZE;
import org.betterx.bclib.client.models.BCLModels;
import org.betterx.betterend.BetterEnd;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;

import net.minecraft.client.data.models.BlockModelGenerators;
import static net.minecraft.client.data.models.BlockModelGenerators.X_ROT_180;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class StalactiteBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> {
    private static final BlockTraitKey KEY = BlockTraitKey.ofUnique(BetterEnd.C, "stalactite");

    public final Block sourceBlock;

    public static List<BlockTrait<?, ?>> withSource(Block sourceBlock) {
        return Combiner
                .of(BlockTraits.STONE_BLOCK)
                .add(
                        new StalactiteBlockTrait(sourceBlock),
                        ClientBlockTraits.RENDER_LAYER.cutout(),
                        ClientBlockTraits.MODEL.with(
                                (key, block, generator) -> {
                                    final ResourceLocation id = TextureMapping.getBlockTexture(block);
                                    final var props = PropertyDispatch.initial(IS_FLOOR, SIZE);
                                    for (int size = 0; size <= 7; size++) {
                                        final String suffix = "_" + size;
                                        final TextureMapping mapping = new TextureMapping().put(
                                                TextureSlot.CROSS,
                                                id.withSuffix(suffix)
                                        );
                                        final ResourceLocation modelLocation = BCLModels.CROSS_SHADED.createWithSuffix(
                                                block,
                                                suffix,
                                                mapping,
                                                generator.modelOutput()
                                        );
                                        final var model = BlockModelGenerators.plainVariant(modelLocation);
                                        props.select(true, size, model);
                                        props.select(false, size, model.with(X_ROT_180));
                                    }
                                    generator.acceptBlockState(MultiVariantGenerator.dispatch(block).with(props));
                                    generator.createFlatItem(block, TextureMapping.getItemTexture(block.asItem()));
                                })
                )
                .combine();
    }

    private StalactiteBlockTrait(Block sourceBlock) {
        super();
        this.sourceBlock = sourceBlock;
    }

    @Override
    public BlockTraitKey key() {
        return KEY;
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition
                .replacePropertiesWithCopy(sourceBlock)
                .noOcclusion()
        ;
    }
}
