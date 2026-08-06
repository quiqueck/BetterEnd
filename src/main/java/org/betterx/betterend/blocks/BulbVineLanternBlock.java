package org.betterx.betterend.blocks;

import org.betterx.bclib.client.models.BCLModels;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.EndLanternBlock;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.sets.api.blocks.BlockSet;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BulbVineLanternBlock extends EndLanternBlock {
    private static final VoxelShape SHAPE_CEIL = Block.box(4, 4, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_FLOOR = Block.box(4, 0, 4, 12, 12, 12);

    public BulbVineLanternBlock(Properties settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(IS_FLOOR) ? SHAPE_FLOOR : SHAPE_CEIL;
    }

    protected static String getMetalTexture(Identifier blockId) {
        String name = blockId.getPath();
        name = name.substring(0, name.indexOf('_'));
        return name + "_bulb_vine_lantern_metal";
    }

    protected String getGlowTexture() {
        return "bulb_vine_lantern_bulb";
    }

    /**
     * Kept in a separate class file (not just an @Environment(CLIENT) method) since this Block is
     * always loaded on the server; a lambda body's synthetic method does not inherit the
     * annotation from its enclosing method, so leaving it here would strand vanilla client-only
     * type references in a class file the server actually has to verify. See PathBlockTrait/
     * StoneLanternBlock for the same fix applied elsewhere.
     */
    public static BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModCore.isDatagen() ? ClientModel.build() : null;
    }

    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> {
                        //get id of this block from registry
                        final var id = BuiltInRegistries.BLOCK.getKey(block);

                        final var mapping = new TextureMapping()
                                .put(BCLModels.GLOW, new Material(BetterEnd.C.mk("bulb_vine_lantern_bulb").withPrefix("block/")))
                                .put(BCLModels.METAL, new Material(BetterEnd.C.mk(getMetalTexture(id)).withPrefix("block/")));

                        final var floorModel = BCLModels.BULB_LANTERN_FLOOR.createWithSuffix(
                                block,
                                "_floor",
                                mapping,
                                generator.vanillaGenerator.modelOutput
                        );
                        final var ceilModel = BCLModels.BULB_LANTERN_CEIL.create(
                                block,
                                mapping,
                                generator.vanillaGenerator.modelOutput
                        );

                        final var floorCeilDispatch = PropertyDispatch
                                .modify(IS_FLOOR)
                                .select(
                                        true,
                                        (variant) -> BlockModelGenerators.plainModel(floorModel)
                                )
                                .select(
                                        false,
                                        (variant) -> BlockModelGenerators.plainModel(ceilModel)
                                );

                        generator.acceptBlockState(MultiVariantGenerator
                                .dispatch(block, BlockModelGenerators.plainVariant(ceilModel))
                                .with(floorCeilDispatch));
                        // The colored variants declare ClientBlockTraits.ITEM_TINT (bulb_vine_lantern_bulb is
                        // near-grayscale and tinted via "tintindex": 0), so this delegation emits their tint;
                        // the uncolored lanterns carry no such trait and get a plain item model.
                        generator.delegateItemModel(block, floorModel);
                    });
        }
    }
}
