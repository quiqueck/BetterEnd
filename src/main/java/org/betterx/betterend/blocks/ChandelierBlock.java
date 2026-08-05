package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.betterend.client.models.EndModels;
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
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Maps;

import java.util.EnumMap;

public class ChandelierBlock extends BaseAttachedBlock.Metal {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(Direction.class);

    public ChandelierBlock(Block source) {
        super(BlockBehaviour.Properties.ofFullCopy(source)
                                       .lightLevel((bs) -> 15)
                                       .noCollision()
                                       .noOcclusion()
                                       .requiresCorrectToolForDrops());
    }

    public ChandelierBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return BOUNDING_SHAPES.get(state.getValue(FACING));
    }

    /**
     * Kept in a separate class file (not just an @Environment(CLIENT) method) since this Block is
     * always loaded on the server; a lambda body's synthetic method does not inherit the
     * annotation from its enclosing method, so leaving it here would strand vanilla client-only
     * type references in a class file the server actually has to verify.
     */
    public static BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ModCore.isDatagen() ? ClientModel.build() : null;
    }

    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> {
                        final var baseTexture = TextureMapping.getBlockTexture(block).sprite();
                        final var mapping = new TextureMapping()
                                .put(EndModels.WALL, new Material(baseTexture.withSuffix("_wall")))
                                .put(EndModels.FLOOR, new Material(baseTexture.withSuffix("_floor")))
                                .put(EndModels.CEIL, new Material(baseTexture.withSuffix("_ceil")));

                        final var modelCeil = EndModels.CHANDELIER_CEIL.createWithSuffix(
                                block,
                                "_ceil",
                                mapping,
                                generator.vanillaGenerator.modelOutput
                        );
                        final var modelWall = EndModels.CHANDELIER_WALL.createWithSuffix(
                                block,
                                "_wall",
                                mapping,
                                generator.vanillaGenerator.modelOutput
                        );
                        final var modelFloor = EndModels.CHANDELIER_FLOOR.createWithSuffix(
                                block,
                                "_floor",
                                mapping,
                                generator.vanillaGenerator.modelOutput
                        );

                        final var facingDispatch = PropertyDispatch
                                .modify(BaseAttachedBlock.FACING)
                                .select(
                                        Direction.DOWN,
                                        (variant) -> BlockModelGenerators.plainModel(modelCeil)
                                )
                                .select(
                                        Direction.UP,
                                        (variant) -> BlockModelGenerators.plainModel(modelFloor)
                                )
                                .select(
                                        Direction.EAST,
                                        (variant) -> BlockModelGenerators.Y_ROT_270.apply(
                                                BlockModelGenerators.plainModel(modelWall))
                                )
                                .select(
                                        Direction.SOUTH,
                                        (variant) -> BlockModelGenerators.plainModel(modelWall)
                                )
                                .select(
                                        Direction.WEST,
                                        (variant) -> BlockModelGenerators.Y_ROT_90.apply(
                                                BlockModelGenerators.plainModel(modelWall))
                                )
                                .select(
                                        Direction.NORTH,
                                        (variant) -> BlockModelGenerators.Y_ROT_180.apply(
                                                BlockModelGenerators.plainModel(modelWall))
                                );


                        generator.acceptBlockState(
                                MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(modelCeil))
                                                     .with(facingDispatch)
                        );

                        // Flat inventory sprite from the dedicated item/<name>.png art instead of the 3D
                        // ceiling block model: chandeliers are thin/complex blocks that read better as a flat
                        // item icon. (There is no block/<name>.png, only the _wall/_floor/_ceil variants, so
                        // the flat item must be pointed at the item texture explicitly.)
                        generator.createFlatItem(block, TextureMapping.getItemTexture(block.asItem()).sprite());
                    });
        }
    }

    static {
        BOUNDING_SHAPES.put(Direction.UP, Block.box(5, 0, 5, 11, 13, 11));
        BOUNDING_SHAPES.put(Direction.DOWN, Block.box(5, 3, 5, 11, 16, 11));
        BOUNDING_SHAPES.put(Direction.NORTH, Shapes.box(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
        BOUNDING_SHAPES.put(Direction.SOUTH, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
        BOUNDING_SHAPES.put(Direction.WEST, Shapes.box(0.5, 0.0, 0.0, 1.0, 1.0, 1.0));
        BOUNDING_SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.0, 0.0, 0.5, 1.0, 1.0));
    }
}
