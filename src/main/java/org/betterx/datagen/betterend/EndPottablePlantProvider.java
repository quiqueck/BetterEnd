package org.betterx.datagen.betterend;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndTags;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.pottable.api.PottablePlant;
import org.betterx.wover.pottable.api.PottablePlantRegistry;
import org.betterx.wover.pottable.api.datagen.WoverPottablePlantRegistryProvider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Serializes the {@link PottablePlant} entries for BetterEnd.
 * <p>
 * In addition to every BetterEnd block carrying a
 * {@link org.betterx.wover.pottable.api.trait.PottablePlantBlockTrait} (handled by the
 * super class), this provider explicitly registers the standard VANILLA flower-pot plant
 * set. Those vanilla blocks have no BetterEnd trait, so they need to be added by hand.
 * <p>
 * The list is the authoritative "vanilla can pot this" set, i.e. exactly the blocks that
 * have a {@code minecraft:block/potted_<name>} model in vanilla. All of them are restricted
 * to the {@link EndTags#SURVIVES_ON_DIRT} soils (vanilla dirt-likes) via {@code valid_soils}.
 * <p>
 * The generated entries are keyed in the {@code betterend} namespace (e.g.
 * {@code betterend:dandelion}) so all pottable-plant data ships in one place.
 */
public class EndPottablePlantProvider extends WoverPottablePlantRegistryProvider {
    /**
     * The vanilla flower-pot plant set (content block -> vanilla {@code potted_<name>} model).
     * Note the two blocks whose potted model name does NOT follow {@code potted_<blockname>}:
     * {@link Blocks#AZALEA} -> {@code potted_azalea_bush} and {@link Blocks#FLOWERING_AZALEA}
     * -> {@code potted_flowering_azalea_bush}. That mismatch is handled in the client renderer
     * ({@code EndFlowerPotModels}), not here.
     */
    public static final List<Block> VANILLA_PLANTS = List.of(
            // Small flowers
            Blocks.DANDELION,
            Blocks.POPPY,
            Blocks.BLUE_ORCHID,
            Blocks.ALLIUM,
            Blocks.AZURE_BLUET,
            Blocks.RED_TULIP,
            Blocks.ORANGE_TULIP,
            Blocks.WHITE_TULIP,
            Blocks.PINK_TULIP,
            Blocks.OXEYE_DAISY,
            Blocks.CORNFLOWER,
            Blocks.LILY_OF_THE_VALLEY,
            Blocks.WITHER_ROSE,
            Blocks.TORCHFLOWER,
            Blocks.OPEN_EYEBLOSSOM,
            Blocks.CLOSED_EYEBLOSSOM,
            // Saplings / propagule
            Blocks.OAK_SAPLING,
            Blocks.SPRUCE_SAPLING,
            Blocks.BIRCH_SAPLING,
            Blocks.JUNGLE_SAPLING,
            Blocks.ACACIA_SAPLING,
            Blocks.DARK_OAK_SAPLING,
            Blocks.PALE_OAK_SAPLING,
            Blocks.CHERRY_SAPLING,
            Blocks.MANGROVE_PROPAGULE,
            // Ferns / bush / misc plants
            Blocks.FERN,
            Blocks.DEAD_BUSH,
            Blocks.CACTUS,
            Blocks.BAMBOO,
            Blocks.BROWN_MUSHROOM,
            Blocks.RED_MUSHROOM,
            Blocks.CRIMSON_FUNGUS,
            Blocks.WARPED_FUNGUS,
            Blocks.CRIMSON_ROOTS,
            Blocks.WARPED_ROOTS,
            Blocks.AZALEA,
            Blocks.FLOWERING_AZALEA
    );

    public EndPottablePlantProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrap(BootstrapContext<PottablePlant> context) {
        // Register every BetterEnd block carrying the PottablePlantBlockTrait.
        super.bootstrap(context);

        // Register the vanilla flower-pot plant set explicitly (they have no trait), all
        // restricted to the vanilla dirt-like soils.
        for (Block plant : VANILLA_PLANTS) {
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(plant);
            PottablePlantRegistry.register(
                    context,
                    PottablePlantRegistry.createKey(BetterEnd.C.id(blockId.getPath())),
                    plant,
                    EndTags.SURVIVES_ON_DIRT
            );
        }
    }
}
