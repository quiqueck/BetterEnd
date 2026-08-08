package org.betterx.datagen.betterend;


import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverModelProvider;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.client.trait.ClientItemTraits;
import de.ambertation.wover.item.api.client.trait.ItemModelTrait;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.item.BlockItem;

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

            if (item instanceof BlockItem blockItem) {
                // A block item whose block generated a block model but registered no item model for it - e.g.
                // the wooden furniture, whose model trait only emits the block model. Delegate to that block
                // model, which is what these shipped pre-migration; the flat fallback below would look for an
                // item/<name> texture, which does not exist for a block.
                itemModelGenerator.itemModelOutput.accept(
                        item,
                        ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(blockItem.getBlock()))
                );
                return;
            }
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
        // fallback in addFromRegistry() for them, so a block carrying a MODEL trait does not also
        // get a plain cube model registered by the fallback (which would register the same model twice).
        registry.allBlocks().forEach(block -> {
            if (!overrides.contain(block) && ClientBlockTraits.MODEL.getRuntimeTraits(block) != null) {
                overrides.ignore(block);
            }
        });

        this.addFromRegistry(generator, registry, true, overrides);
    }

    private ModelOverides buildModelOverrides(WoverBlockModelGenerators generator) {
        final ModelOverides overrides = ModelOverides.create();

        return overrides
                             // Source blocks keep their hand-authored multi-variant blockstate (the generic
                             // SOURCE-slot cube model would overwrite it); the override only wires the item.
                             .override(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE), generator::delegateItemModel)
                             .override(
                                     EndStoneBlocks.UMBRALITH.getBlock(SlotType.SOURCE),
                                     b -> generator.delegateItemModel(b, BetterEnd.C.mk("block/umbralith_5"))
                             )
                             // Violecite's brick wall keeps its hand-authored blockstate and models: the post is a
                             // full-height pillar using the distinct violecite_post_side/_top textures, and the two
                             // side models cap their top face with violecite_brick_wall_top. The generic BRICK_WALL
                             // slot models (vanilla template_wall_*) can only put one texture on every face, so they
                             // cannot express either. The override wires just the item, pointing it at the custom
                             // post - which is what this block shipped with before the _bricks -> _brick rename
                             // orphaned the old plural-named assets.
                             .override(
                                     EndStoneBlocks.VIOLECITE.getBlock(SlotType.BRICK_WALL),
                                     b -> generator.delegateItemModel(
                                             b,
                                             BetterEnd.C.mk("block/violecite_brick_wall_post")
                                     )
                             )
                             // Sulphuric rock's brick wall likewise keeps hand-authored models so its two side
                             // models can cap their top face with sulphuric_rock_brick_wall_top - a texture that
                             // had been drawn for this block but was never wired up to any model. Its post and
                             // inventory models are byte-for-byte the vanilla template_wall_post / wall_inventory
                             // ones datagen used to emit (there is no sulphuric_rock_post_* art to use instead),
                             // so only the sides change; the item still points at the unchanged inventory model.
                             .override(
                                     EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.BRICK_WALL),
                                     b -> generator.delegateItemModel(
                                             b,
                                             BetterEnd.C.mk("block/sulphuric_rock_brick_wall_inventory")
                                     )
                             )
                             // Aurora crystal keeps its hand-authored blockstate/model, so the override is what
                             // registers its item model at all. The tint itself comes from the block's
                             // ClientBlockTraits.TINT trait, which delegateItemModel applies; registering
                             // this as an override makes BlockModelTrait.bootstrapModels skip the block's
                             // EXTERNAL_MODEL item delegation, so there is exactly one item model, not a
                             // duplicate.
                             .override(EndCrystalBlocks.AURORA_CRYSTAL, b -> generator.delegateItemModel(
                                     b,
                                     BetterEnd.C.mk("item/aurora_crystal")
                             ))
;
    }

    public EndModelProvider(ModCore modCore) {
        super(modCore);
    }
}
