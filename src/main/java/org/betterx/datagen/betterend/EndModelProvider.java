package org.betterx.datagen.betterend;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.betterend.complexmaterials.types.FlowerPot;
import org.betterx.betterend.complexmaterials.types.Pedestal;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.client.trait.ClientItemTraits;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import com.mojang.math.Quadrant;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EndModelProvider extends WoverModelProvider {
    /**
     * Set by {@link #bootstrapBlockStateModels} (which always runs first - vanilla generates block-state
     * models before item models) and read back by {@link #bootstrapItemModels} to know which blocks
     * already generated their own item model via {@link WoverBlockModelGenerators#hasItemModel}, so the
     * flat-item fallback there doesn't step on them.
     */
    private WoverBlockModelGenerators generator;
    private ModelOverides overrides;

    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {
        ItemModelTrait.bootstrapModels(modCore, itemModelGenerator);

        // Block items normally get their item model as a side effect of their block's model generation
        // (trait, override, or legacy BlockModelProvider) - skip those here (checked via
        // generator.hasItemModel(), which WoverBlockModelGenerators populates as it goes) so the fallback
        // below doesn't clobber them. This can't be simplified to "attempt generateFlatItem for everyone
        // and catch the already-has-a-model failure": the underlying Map.put()-based registration always
        // overwrites before it reports the conflict, so catching the exception is too late to save the
        // original (correct) registration - it wins the map, then flat-item's overwrite wins instead,
        // and vanilla's shared ItemModelOutput never notices. Every item that wasn't given an explicit
        // ItemModelTrait and doesn't already have an item model needs one too - fall back to a plain
        // flat icon for those, matching vanilla's own convention for simple items.
        ItemRegistry.forMod(BetterEnd.C).allEntries().forEach(entry -> {
            var item = entry.getValue();
            if (item instanceof BlockItem blockItem && generator.hasItemModel(blockItem.getBlock())) return;
            if (ClientItemTraits.MODEL.getRuntimeTraits(item) != null) return;
            itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        });
    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        this.generator = generator;
        final BlockRegistry registry = BlockRegistry.forMod(BetterEnd.C);
        this.overrides = buildModelOverrides(generator);

        // Blocks with an explicit entry in ModelOverides above are meant to use that custom model
        // instead of whatever their ClientBlockTraits.MODEL trait would generate (e.g. a "lit"
        // variant with custom emissive textures) - skip them here so the trait-based model isn't
        // ALSO generated, which would collide with the override's model.
        BlockModelTrait.bootstrapModels(modCore, generator, (key, block) -> !overrides.contain(block));

        // Blocks with an explicit ClientBlockTraits.MODEL trait are already fully handled by
        // BlockModelTrait.bootstrapModels() above - skip the legacy BlockModelProvider-interface
        // fallback in addFromRegistry() for them, since BaseBlock implements that interface
        // unconditionally (defaulting to a plain cube model) and running both would register the
        // same model twice.
        registry.allBlocks().forEach(block -> {
            if (!overrides.contain(block) && ClientBlockTraits.MODEL.getRuntimeTraits(block) != null) {
                overrides.ignore(block);
            }
        });

        this.addFromRegistry(generator, registry, true, overrides);
    }

    private ModelOverides buildModelOverrides(WoverBlockModelGenerators generator) {
        return ModelOverides.create()
                             .override(EndBlocks.TWISTED_VINE, createTwistedVineModel(generator))
                             .override(EndBlocks.AMBER_MOSS, createAmberMossModel(generator))
                             .override(
                                     EndBlocks.AMBER_MOSS_PATH,
                                     createAmberMossPathModel(generator, EndBlocks.AMBER_MOSS)
                             )
                             .override(
                                     EndBlocks.QUARTZ_SET.getBlock(Pedestal.SLOT), block -> PedestalBlock.provideBlockModel(
                                             generator, new TextureMapping()
                                                     .put(
                                                             TextureSlot.TOP,
                                                             IntegrationCore.MINECRAFT.mk("block/quartz_pillar_top")
                                                     )
                                                     .put(
                                                             TextureSlot.BOTTOM,
                                                             IntegrationCore.MINECRAFT.mk("block/quartz_block_bottom")
                                                     )
                                                     .put(
                                                             EndModels.BASE,
                                                             IntegrationCore.MINECRAFT.mk("block/quartz_block_side")
                                                     )
                                                     .put(
                                                             EndModels.PILLAR,
                                                             IntegrationCore.MINECRAFT.mk("block/quartz_pillar")
                                                     ), block
                                     )
                             )
                             .override(
                                     EndBlocks.PURPUR_SET.getBlock(Pedestal.SLOT), block -> PedestalBlock.provideBlockModel(
                                             generator, new TextureMapping()
                                                     .put(
                                                             TextureSlot.TOP,
                                                             IntegrationCore.MINECRAFT.mk("block/purpur_pillar_top")
                                                     )
                                                     .put(
                                                             TextureSlot.BOTTOM,
                                                             IntegrationCore.MINECRAFT.mk("block/purpur_block")
                                                     )
                                                     .put(
                                                             EndModels.BASE,
                                                             IntegrationCore.MINECRAFT.mk("block/purpur_block")
                                                     )
                                                     .put(
                                                             EndModels.PILLAR,
                                                             IntegrationCore.MINECRAFT.mk("block/purpur_pillar")
                                                     ), block
                                     )
                             )
                             .override(
                                     EndBlocks.NEON_CACTUS_BLOCK_STAIRS, block -> {
                                         final var id = TextureMapping.getBlockTexture(block);
                                         final var texture = BetterEnd.C.mk("block/neon_cactus_block");
                                         final var mapping = new TextureMapping()
                                                 .put(TextureSlot.TOP, texture.withSuffix("_top"))
                                                 .put(TextureSlot.BOTTOM, texture.withSuffix("_top"))
                                                 .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                         final var stairs = EndModels.LIT_STAIRS.create(
                                                 id,
                                                 mapping,
                                                 generator.modelOutput()
                                         );
                                         final var stairs_outer = EndModels.LIT_STAIRS_OUTER.create(
                                                 id.withSuffix("_outer"), mapping, generator.modelOutput());
                                         final var stairs_inner = EndModels.LIT_STAIRS_INNER.create(
                                                 id.withSuffix("_inner"), mapping, generator.modelOutput());
                                         generator.createStairsWithModels(block, stairs, stairs_outer, stairs_inner);
                                     }
                             ).override(
                                     EndBlocks.NEON_CACTUS_BLOCK_SLAB, block -> {
                                         final var id = TextureMapping.getBlockTexture(block);
                                         final var texture = TextureMapping.getBlockTexture(EndBlocks.NEON_CACTUS_BLOCK);
                                         final var mapping = new TextureMapping()
                                                 .put(TextureSlot.TOP, texture.withSuffix("_top"))
                                                 .put(TextureSlot.BOTTOM, texture.withSuffix("_top"))
                                                 .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                         generator.createSlab(block, EndBlocks.NEON_CACTUS_BLOCK, mapping);
                                     }
                             )
                             .ignore(EndBlocks.CRYSTAL_GRASS)
                             .ignore(EndBlocks.CYAN_MOSS)
                             .override(EndBlocks.AERIDIUM, generator::createFlatItem)
                             .ignore(EndBlocks.BUSHY_GRASS)
                             .ignore(EndBlocks.JUNGLE_FERN)
                             .ignore(EndBlocks.BULB_MOSS)
                             .ignore(EndBlocks.FLAMAEA)
                             .ignore(EndBlocks.CAVE_BUSH)
                             .ignore(EndBlocks.BULB_VINE)
                             .ignore(EndBlocks.BULB_VINE_SEED)
                             .ignore(EndBlocks.SILK_MOTH_NEST)
                             .override(EndBlocks.SILK_MOTH_HIVE, generator::delegateItemModel)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.stairs)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.wall)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.brick_stairs)
                             .ignore(EndBlocks.SMARAGDANT_SUBBLOCKS.brick_wall)
                             .override(
                                     EndBlocks.SMARAGDANT_CRYSTAL, block -> {
                                         final var texture = BetterEnd.C.mk("block/smaragdant_crystal");
                                         final var mapping = new TextureMapping()
                                                 .put(TextureSlot.END, texture.withSuffix("_top"))
                                                 .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                         generator.createRotatedPillar(block, mapping);
                                     }
                             )
                             .override(
                                     EndBlocks.BUDDING_SMARAGDANT_CRYSTAL, block -> {
                                         final var texture = BetterEnd.C.mk("block/budding_smaragdant_crystal");
                                         final var mapping = new TextureMapping()
                                                 .put(TextureSlot.END, texture.withSuffix("_top"))
                                                 .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                         generator.createRotatedPillar(block, mapping);
                                     }
                             )
                             .override(
                                     EndBlocks.SMARAGDANT_SUBBLOCKS.slab, block -> {
                                         final var texture = BetterEnd.C.mk("block/smaragdant_crystal");
                                         final var mapping = new TextureMapping()
                                                 .put(TextureSlot.TOP, texture.withSuffix("_top"))
                                                 .put(TextureSlot.BOTTOM, texture.withSuffix("_top"))
                                                 .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                         generator.createSlab(block, EndBlocks.SMARAGDANT_CRYSTAL, mapping);
                                     }
                             )
                             .override(
                                     EndBlocks.SMARAGDANT_SUBBLOCKS.pillar, block -> {
                                         final var texture = BetterEnd.C.mk("block/smaragdant_crystal_pillar");
                                         final var mapping = new TextureMapping()
                                                 .put(TextureSlot.END, texture.withSuffix("_top"))
                                                 .put(TextureSlot.SIDE, texture.withSuffix("_side"));
                                         generator.createRotatedPillar(block, mapping);
                                     }
                             )
//                             .ignore(EndBlocks.GOLD_CHANDELIER)
//                             .ignore(EndBlocks.IRON_CHANDELIER)
                             .ignore(EndBlocks.LUCERNIA_OUTER_LEAVES)
                             .ignore(EndBlocks.SULPHUR_CRYSTAL)
                             .override(EndBlocks.MOSSY_OBSIDIAN, generator::delegateItemModel)
                             .override(EndBlocks.HYDROTHERMAL_VENT, generator::delegateItemModel)
                             .ignore(EndBlocks.FLAVOLITE_RUNED_ETERNAL)
                             .ignore(EndBlocks.FLAVOLITE_RUNED)
                             .ignore(EndBlocks.MOSSY_GLOWSHROOM_HYMENOPHORE)
                             .ignore(EndBlocks.END_LOTUS_SEED)
                             .ignore(EndBlocks.END_LOTUS_LEAF)
                             .override(EndBlocks.CAVE_PUMPKIN, generator::delegateItemModel)
                             .ignore(EndBlocks.FILALUX)
                             .ignore(EndBlocks.CREEPING_MOSS)
                             .ignore(EndBlocks.RESPAWN_OBELISK)
                             .override(
                                     EndBlocks.TWISTED_UMBRELLA_MOSS,
                                     b -> generator.createFlatItem(
                                             b,
                                             BetterEnd.C.mk("item/twisted_umbrella_moss_small")
                                     )
                             )
                             .override(
                                     EndBlocks.TWISTED_UMBRELLA_MOSS_TALL,
                                     b -> generator.createFlatItem(
                                             b,
                                             BetterEnd.C.mk("item/twisted_umbrella_moss_large")
                                     )
                             )
                             .override(
                                     EndBlocks.UMBRELLA_MOSS,
                                     b -> generator.createFlatItem(b, BetterEnd.C.mk("item/umbrella_moss_small"))
                             )
                             .override(
                                     EndBlocks.UMBRELLA_MOSS_TALL,
                                     b -> generator.createFlatItem(b, BetterEnd.C.mk("item/umbrella_moss_large"))
                             )
                             .ignore(EndBlocks.BLOSSOM_BERRY)
                             .ignore(EndBlocks.CAVE_PUMPKIN_SEED)
                             .ignore(EndBlocks.CHORUS_MUSHROOM)
                             .ignore(EndBlocks.SHADOW_BERRY)
                             .ignore(EndBlocks.LUMECORN_SEED)
                             .ignore(EndBlocks.HYDRALUX_SAPLING)
                             .ignore(EndBlocks.CHORUS_GRASS)
                             .ignore(EndBlocks.SMALL_JELLYSHROOM)
                             .ignore(EndBlocks.CAVE_GRASS)
                             .ignore(EndBlocks.GLOWING_PILLAR_SEED)
                             .ignore(EndBlocks.END_LILY_SEED)
                             .ignore(EndBlocks.BLUE_VINE_SEED)
                             .ignore(EndBlocks.TUBE_WORM)
                             .ignore(EndBlocks.AMBER_ROOT)
                             .ignore(EndBlocks.PURPLE_POLYPORE)
                             .ignore(EndBlocks.AURANT_POLYPORE)
                             .ignore(EndBlocks.NEEDLEGRASS)
                             .ignore(EndBlocks.VIOLECITE.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.UMBRALITH.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.SULPHURIC_ROCK.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.VIRID_JADESTONE.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.AZURE_JADESTONE.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.SANDY_JADESTONE.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.FLAVOLITE.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.END_STONE_SET.getBlock(FlowerPot.SLOT))
                             .ignore(EndBlocks.SMARAGDANT_CRYSTAL_SHARD)
                             .override(EndBlocks.END_LOTUS_STEM, generator::delegateItemModel)
//                             .ignore(EndBlocks.THALLASIUM.chandelier)
//                             .ignore(EndBlocks.TERMINITE.chandelier)
                             .ignore(EndBlocks.RUTISCUS)
                             .ignore(EndBlocks.MURKWEED)
                             .ignore(EndBlocks.LANCELEAF_SEED)
                             .ignore(EndBlocks.CHARNIA_RED)
                             .ignore(EndBlocks.CHARNIA_CYAN)
                             .ignore(EndBlocks.CHARNIA_GREEN)
                             .ignore(EndBlocks.CHARNIA_ORANGE)
                             .ignore(EndBlocks.CHARNIA_PURPLE)
                             .ignore(EndBlocks.CHARNIA_LIGHT_BLUE)
                             .ignore(EndBlocks.BOLUX_MUSHROOM)
                             .ignore(EndBlocks.SMALL_AMARANITA_MUSHROOM)
                             .ignore(EndBlocks.BUBBLE_CORAL)
                             .ignore(EndBlocks.FILALUX_WINGS)
                             .override(EndBlocks.SALTEAGO, generator::createFlatItem)
                             .ignore(EndBlocks.RUBINEA)
                             .ignore(EndBlocks.RUSCUS)
                             .ignore(EndBlocks.TENANEA_FLOWERS)
                             .ignore(EndBlocks.MAGNULA)
                             .override(EndBlocks.CLAWFERN, generator::createFlatItem)
                             .override(EndBlocks.LARGE_AMARANITA_MUSHROOM, generator::createFlatItem)
                             .override(EndBlocks.LANCELEAF, generator::createFlatItem)
                             .override(EndBlocks.END_LILY, generator::createFlatItem)
                             .override(EndBlocks.LUMECORN, generator::createFlatItem)
                             .override(EndBlocks.END_LOTUS_FLOWER, generator::createFlatItem)
                             .override(EndBlocks.HYDRALUX, generator::createFlatItem)
                             .override(EndBlocks.LAMELLARIUM, generator::createFlatItem)
                             .override(EndBlocks.LUTEBUS, generator::createFlatItem)
                             .override(EndBlocks.MOSSY_GLOWSHROOM_CAP, generator::delegateItemModel)
                             .override(EndBlocks.MOSSY_GLOWSHROOM_FUR, generator::createFlatItem)
                             .override(EndBlocks.AMARANITA_FUR, generator::createFlatItem)
                             .override(EndBlocks.ORANGO, generator::createFlatItem)
                             .override(EndBlocks.FRACTURN, generator::createFlatItem)
                             .override(EndBlocks.GLOBULAGUS, generator::createFlatItem)
                             .override(EndBlocks.FLAMMALIX, generator::createFlatItem)
                             .override(EndBlocks.AMBER_GRASS, generator::createFlatItem)
                             .override(EndBlocks.BLOOMING_COOKSONIA, generator::createFlatItem)
                             .override(EndBlocks.BLUE_VINE, generator::createFlatItem)
                             .override(EndBlocks.BLUE_VINE_FUR, generator::createFlatItem)
                             .ignore(EndBlocks.DENSE_VINE)
                             .override(EndBlocks.GLOWING_PILLAR_LEAVES, generator::createFlatItem)
                             .override(EndBlocks.GLOWING_PILLAR_ROOTS, generator::createFlatItem)
                             //.ignore(BYGBlocks.IVIS_MOSS).ignore(BYGBlocks.IVIS_VINE).ignore(BYGBlocks.NIGHTSHADE_MOSS)
                             .override(EndBlocks.JUNGLE_GRASS, generator::createFlatItem)
                             .ignore(EndBlocks.JUNGLE_VINE)
                             .ignore(EndBlocks.POND_ANEMONE)
                             .override(EndBlocks.SHADOW_PLANT, generator::createFlatItem)
                             .override(EndBlocks.TAIL_MOSS, generator::createFlatItem)
                             .override(EndBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE), generator::delegateItemModel)
                             .override(EndBlocks.TENANEA_OUTER_LEAVES, generator::delegateItemModel)
                             .override(
                                     EndBlocks.UMBRALITH.getBlock(SlotType.SOURCE),
                                     b -> generator.delegateItemModel(b, BetterEnd.C.mk("block/umbralith_5"))
                             )
                             .override(EndBlocks.TWISTED_MOSS, generator::createFlatItem)
                             .override(EndBlocks.VAIOLUSH_FERN, generator::createFlatItem)
                             .ignore(EndBlocks.BRIMSTONE)
                             .override(EndBlocks.HELIX_TREE_LEAVES, generator::delegateItemModel)
                             .override(EndBlocks.MENGER_SPONGE, generator::delegateItemModel)
                             .override(EndBlocks.MENGER_SPONGE_WET, generator::delegateItemModel)
                             .override(
                                     EndBlocks.VIOLECITE.getBlock(SlotType.BRICK_WALL),
                                     b -> generator.delegateItemModel(
                                             b,
                                             BetterEnd.C.mk("block/violecite_bricks_wall_post")
                                     )
                             )
                             .override(EndBlocks.DRAGON_TREE.getBark(), generator::delegateItemModel)
                             .override(EndBlocks.DRAGON_TREE.getLog(), generator::delegateItemModel)
                             .override(
                                     EndBlocks.NEON_CACTUS,
                                     b -> generator.delegateItemModel(b, BetterEnd.C.mk("block/neon_cactus_small"))
                             )
                             .ignore(EndBlocks.AMARANITA_STEM)
                             .ignore(EndBlocks.MOSSY_DRAGON_BONE);
    }

    private static ModelOverides.@NotNull BlockModelProvider createAmberMossPathModel(
            WoverBlockModelGenerators generator,
            Block baseBlock
    ) {
        return block -> {
            final var endStone = TextureMapping.getBlockTexture(Blocks.END_STONE);
            final var baseTexture = TextureMapping.getBlockTexture(block, "_top");
            final var models = List.of("_1", "_2", "_3").stream().map(suffix -> {
                final var side = TextureMapping.getBlockTexture(baseBlock, "_side" + suffix);
                final var mapping = new TextureMapping()
                        .put(TextureSlot.BOTTOM, endStone)
                        .put(TextureSlot.TOP, baseTexture)
                        .put(TextureSlot.SIDE, side);
                return EndModels.PATH.createWithSuffix(block, suffix, mapping, generator.modelOutput());
            }).toList();

            buildRotated(generator, block, models);
        };
    }

    private static ModelOverides.@NotNull BlockModelProvider createAmberMossModel(WoverBlockModelGenerators generator) {
        return block -> {
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
        };
    }

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

    private static ModelOverides.@NotNull BlockModelProvider createTwistedVineModel(WoverBlockModelGenerators generator) {
        return block -> {
            final var itemTextureLocation = TextureMapping.getBlockTexture(block, "_bottom");
            var bottomMapping = new TextureMapping()
                    .put(TextureSlot.TEXTURE, itemTextureLocation);
            var middleMapping = new TextureMapping()
                    .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block));
            var topMapping = new TextureMapping()
                    .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(block))
                    .put(EndModels.ROOTS, TextureMapping.getBlockTexture(block, "_roots"));

            var bottom_1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(
                    block,
                    "_bottom_1",
                    bottomMapping,
                    generator.modelOutput()
            );
            var bottom_2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(
                    block,
                    "_bottom_2",
                    bottomMapping,
                    generator.modelOutput()
            );
            var middle_1 = EndModels.CROSS_NO_DISTORTION.createWithSuffix(
                    block,
                    "_middle_1",
                    middleMapping,
                    generator.modelOutput()
            );
            var middle_2 = EndModels.CROSS_NO_DISTORTION_INVERTED.createWithSuffix(
                    block,
                    "_middle_2",
                    middleMapping,
                    generator.modelOutput()
            );
            var top = EndModels.TWISTED_VINE.createWithSuffix(block, "_top", topMapping, generator.modelOutput());

            generator.acceptBlockState(MultiVariantGenerator
                    .dispatch(block)
                    .with(PropertyDispatch.initial(BaseVineBlock.SHAPE)
                                          .select(
                                                  BlockProperties.TripleShape.TOP,
                                                  new MultiVariant(WeightedList.of(new Variant(top)))
                                          )
                                          .select(
                                                  BlockProperties.TripleShape.MIDDLE,
                                                  new MultiVariant(WeightedList.<Variant>builder()
                                                          .add(new Variant(middle_1))
                                                          .add(new Variant(middle_2))
                                                          .build())
                                          )
                                          .select(
                                                  BlockProperties.TripleShape.BOTTOM,
                                                  new MultiVariant(WeightedList.<Variant>builder()
                                                          .add(new Variant(bottom_1))
                                                          .add(new Variant(bottom_2))
                                                          .build())
                                          )
                    )
            );

            generator.createFlatItem(block, itemTextureLocation);
        };
    }

    public EndModelProvider(ModCore modCore) {
        super(modCore);
    }
}
