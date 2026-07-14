package org.betterx.betterend.client.models;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betterend.BetterEnd;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import com.mojang.math.Quadrant;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * BetterEnd-specific {@link BlockModelTrait} factories for blocks whose model can't be produced by a generic
 * {@code ModelTraitLibrary} shape - twisted vines, amber-moss cover/path, smaragdant rotated pillars, and the
 * neon-cactus lit stairs/slab. Mirrors {@code ModelTraitLibrary}'s public-wrapper + {@code @Environment(CLIENT)}
 * {@code Impl} pattern so that merely referencing these from block registration never resolves a client-only
 * datagen type on a dedicated server. Every method returns {@code null} outside of datagen.
 */
public class EndModelTraits {
    /** A twisted-vine model: top/middle/bottom triple-shape with cross-no-distortion variants + a flat item. */
    public static BlockModelTrait twistedVine() {
        return ModCore.isDatagen() ? Impl.twistedVine() : null;
    }

    /** The amber-moss cover: three weighted, rotated full-cube variants ({@code _1/_2/_3}) over end stone. */
    public static BlockModelTrait amberMoss() {
        return ModCore.isDatagen() ? Impl.amberMoss() : null;
    }

    /**
     * An amber-moss-style path: three weighted, rotated path models whose side texture comes from {@code base}.
     *
     * @param base supplies the terrain block whose {@code _side_N} textures the path reuses
     */
    public static BlockModelTrait amberMossPath(Supplier<Block> base) {
        return ModCore.isDatagen() ? Impl.amberMossPath(base) : null;
    }

    /**
     * A rotated pillar whose {@code end}/{@code side} textures are {@code <texture>_top}/{@code <texture>_side}.
     *
     * @param texture supplies the base texture location (without suffix)
     */
    public static BlockModelTrait rotatedPillar(Supplier<ResourceLocation> texture) {
        return ModCore.isDatagen() ? Impl.rotatedPillar(texture) : null;
    }

    /**
     * A lit (emissive) stairs model using {@code <texture>_top}/{@code _side} for its faces.
     *
     * @param texture supplies the base texture location (without suffix)
     */
    public static BlockModelTrait litStairs(Supplier<ResourceLocation> texture) {
        return ModCore.isDatagen() ? Impl.litStairs(texture) : null;
    }

    /**
     * A slab whose faces use {@code <texture>_top} (top/bottom) and {@code <texture>_side} (sides), double-slab
     * delegating to {@code source}.
     *
     * @param source  supplies the full block the double slab mirrors
     * @param texture supplies the base texture location (without suffix)
     */
    public static BlockModelTrait slabFrom(Supplier<Block> source, Supplier<ResourceLocation> texture) {
        return ModCore.isDatagen() ? Impl.slabFrom(source, texture) : null;
    }

    @Environment(EnvType.CLIENT)
    private static class Impl {
        private static void buildRotated(WoverBlockModelGenerators generator, Block block, List<ResourceLocation> models) {
            final WeightedList.Builder<Variant> variants = WeightedList.builder();
            models.forEach(model -> {
                variants.add(new Variant(model));
                variants.add(new Variant(model).withYRot(Quadrant.R90));
                variants.add(new Variant(model).withYRot(Quadrant.R180));
                variants.add(new Variant(model).withYRot(Quadrant.R270));
            });
            generator.acceptBlockState(MultiVariantGenerator.dispatch(block, new MultiVariant(variants.build())));
            generator.delegateItemModel(block, models.get(0));
        }

        private static BlockModelTrait amberMoss() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var endStone = TextureMapping.getBlockTexture(Blocks.END_STONE);
                final var baseTexture = TextureMapping.getBlockTexture(block, "_top");
                final var models = List.of("_1", "_2", "_3").stream().map(suffix -> {
                    final var side = TextureMapping.getBlockTexture(block, "_side" + suffix);
                    final var mapping = new TextureMapping()
                            .put(TextureSlot.DOWN, endStone)
                            .put(TextureSlot.UP, baseTexture)
                            .put(TextureSlot.PARTICLE, side)
                            .put(TextureSlot.EAST, side)
                            .put(TextureSlot.NORTH, side)
                            .put(TextureSlot.SOUTH, side)
                            .put(TextureSlot.WEST, side);
                    return ModelTemplates.CUBE.createWithSuffix(block, suffix, mapping, generator.modelOutput());
                }).toList();
                buildRotated(generator, block, models);
            });
        }

        private static BlockModelTrait amberMossPath(Supplier<Block> base) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var endStone = TextureMapping.getBlockTexture(Blocks.END_STONE);
                final var baseTexture = TextureMapping.getBlockTexture(block, "_top");
                final var models = List.of("_1", "_2", "_3").stream().map(suffix -> {
                    final var side = TextureMapping.getBlockTexture(base.get(), "_side" + suffix);
                    final var mapping = new TextureMapping()
                            .put(TextureSlot.BOTTOM, endStone)
                            .put(TextureSlot.TOP, baseTexture)
                            .put(TextureSlot.SIDE, side);
                    return EndModels.PATH.createWithSuffix(block, suffix, mapping, generator.modelOutput());
                }).toList();
                buildRotated(generator, block, models);
            });
        }

        private static BlockModelTrait rotatedPillar(Supplier<ResourceLocation> texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var t = texture.get();
                generator.createRotatedPillar(block, new TextureMapping()
                        .put(TextureSlot.END, t.withSuffix("_top"))
                        .put(TextureSlot.SIDE, t.withSuffix("_side")));
            });
        }

        private static BlockModelTrait litStairs(Supplier<ResourceLocation> texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var id = TextureMapping.getBlockTexture(block);
                final var t = texture.get();
                final var mapping = new TextureMapping()
                        .put(TextureSlot.TOP, t.withSuffix("_top"))
                        .put(TextureSlot.BOTTOM, t.withSuffix("_top"))
                        .put(TextureSlot.SIDE, t.withSuffix("_side"));
                final var stairs = EndModels.LIT_STAIRS.create(id, mapping, generator.modelOutput());
                final var stairsOuter = EndModels.LIT_STAIRS_OUTER.create(
                        id.withSuffix("_outer"), mapping, generator.modelOutput());
                final var stairsInner = EndModels.LIT_STAIRS_INNER.create(
                        id.withSuffix("_inner"), mapping, generator.modelOutput());
                generator.createStairsWithModels(block, stairs, stairsOuter, stairsInner);
            });
        }

        private static BlockModelTrait slabFrom(Supplier<Block> source, Supplier<ResourceLocation> texture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var t = texture.get();
                generator.createSlab(block, source.get(), new TextureMapping()
                        .put(TextureSlot.TOP, t.withSuffix("_top"))
                        .put(TextureSlot.BOTTOM, t.withSuffix("_top"))
                        .put(TextureSlot.SIDE, t.withSuffix("_side")));
            });
        }

        private static BlockModelTrait twistedVine() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                final var itemTextureLocation = TextureMapping.getBlockTexture(block, "_bottom");
                var bottomMapping = new TextureMapping().put(TextureSlot.TEXTURE, itemTextureLocation);
                var middleMapping = new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block));
                var topMapping = new TextureMapping()
                        .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block))
                        .put(EndModels.ROOTS, TextureMapping.getBlockTexture(block, "_roots"));

                var bottom1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(block, "_bottom_1", bottomMapping, generator.modelOutput());
                var bottom2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(block, "_bottom_2", bottomMapping, generator.modelOutput());
                var middle1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(block, "_middle_1", middleMapping, generator.modelOutput());
                var middle2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(block, "_middle_2", middleMapping, generator.modelOutput());
                var top = EndModels.TWISTED_VINE.createWithSuffix(block, "_top", topMapping, generator.modelOutput());

                generator.acceptBlockState(MultiVariantGenerator
                        .dispatch(block)
                        .with(PropertyDispatch.initial(BaseVineBlock.SHAPE)
                                              .select(BlockProperties.TripleShape.TOP,
                                                      new MultiVariant(WeightedList.of(new Variant(top))))
                                              .select(BlockProperties.TripleShape.MIDDLE,
                                                      new MultiVariant(WeightedList.<Variant>builder()
                                                              .add(new Variant(middle1)).add(new Variant(middle2)).build()))
                                              .select(BlockProperties.TripleShape.BOTTOM,
                                                      new MultiVariant(WeightedList.<Variant>builder()
                                                              .add(new Variant(bottom1)).add(new Variant(bottom2)).build()))));
                generator.createFlatItem(block, itemTextureLocation);
            });
        }
    }
}
