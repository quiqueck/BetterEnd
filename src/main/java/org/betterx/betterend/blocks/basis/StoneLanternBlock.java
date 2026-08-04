package org.betterx.betterend.blocks.basis;


import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.bclib.client.models.BCLModels;
import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.sets.api.blocks.BlockSet;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

public class StoneLanternBlock extends EndLanternBlock implements CustomColorProvider {
    private static final VoxelShape SHAPE_CEIL = box(3, 1, 3, 13, 16, 13);
    private static final VoxelShape SHAPE_FLOOR = box(3, 0, 3, 13, 15, 13);

    public StoneLanternBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public BlockColor getProvider() {
        return ((CustomColorProvider) EndCrystalBlocks.AURORA_CRYSTAL).getProvider();
    }


    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(IS_FLOOR) ? SHAPE_FLOOR : SHAPE_CEIL;
    }

    /**
     * The lambda below must not live directly in this method: annotations like
     * @Environment(CLIENT) on an enclosing method are not applied to the synthetic method javac
     * generates for the lambda body, so Fabric's stripper leaves that synthetic method (and its
     * references to vanilla client-only datagen types) behind in this class file. Since
     * StoneLanternBlock itself is always loaded on the server (it's instantiated for real blocks),
     * verifying that orphaned method would crash server startup. Keeping the lambda in a separate,
     * never-unconditionally-loaded class file avoids that.
     */
    public static BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModCore.isDatagen() ? ClientModel.build() : null;
    }

    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(
            WoverBlockModelGenerators generator,
            TextureMapping mapping,
            Block block
    ) {
        final var floorModel = BCLModels.STONE_LANTERN_FLOOR.createWithSuffix(
                block,
                "_floor",
                mapping,
                generator.vanillaGenerator.modelOutput
        );
        final var ceilModel = BCLModels.STONE_LANTERN_CEIL.create(
                block,
                mapping,
                generator.vanillaGenerator.modelOutput
        );

        final var floorCeilDispatch = PropertyDispatch
                .modify(IS_FLOOR)
                .select(true, (variant) -> BlockModelGenerators.plainModel(floorModel))
                .select(false, (variant) -> BlockModelGenerators.plainModel(ceilModel));

        generator.acceptBlockState(MultiVariantGenerator
                .dispatch(block, BlockModelGenerators.plainVariant(ceilModel))
                .with(floorCeilDispatch));
        generator.delegateItemModel(block, ceilModel);
    }

    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> {
                        final var mapping = new TextureMapping()
                                .put(BCLModels.GLASS, TextureMapping.getBlockTexture(EndCrystalBlocks.AURORA_CRYSTAL))
                                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
                                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
                                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));

                        provideBlockModel(generator, mapping, block);
                    });
        }
    }
}
