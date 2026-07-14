package org.betterx.datagen.betterend;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.client.trait.ClientItemTraits;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.client.data.models.ItemModelGenerators;
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
        final ModelOverides overrides = ModelOverides.create();

        return overrides
                             // Source blocks keep their hand-authored multi-variant blockstate (the generic
                             // SOURCE-slot cube model would overwrite it); the override only wires the item.
                             .override(EndBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE), generator::delegateItemModel)
                             .override(
                                     EndBlocks.UMBRALITH.getBlock(SlotType.SOURCE),
                                     b -> generator.delegateItemModel(b, BetterEnd.C.mk("block/umbralith_5"))
                             )
;
    }

    public EndModelProvider(ModCore modCore) {
        super(modCore);
    }
}
