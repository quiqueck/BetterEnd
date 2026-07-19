package org.betterx.betterend.complexmaterials.types;

import org.betterx.betterend.complexmaterials.MetalMaterial;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.SoundType;

import java.util.Optional;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Chain extends SlotFromDefinition {
    private final MetalMaterial metalMaterial;

    public Chain(MetalMaterial metalMaterial) {
        super(MetalMaterial.CHAIN);
        this.metalMaterial = metalMaterial;
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, (def) -> new ChainBlock(def.getProperties()));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        super.addSlotSpecificDefinitions(set, def);
        def.addTrait(ClientBlockTraits.RENDER_LAYER.cutout());

        def.getProperties()
           .forceSolidOn()
           .requiresCorrectToolForDrops()
           .sound(SoundType.CHAIN)
           .noOcclusion();
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return BlockTraits.RECIPE.with(
                (key, block, context) -> {
                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("N", "#", "N")
                            .addMaterial('#', metalMaterial.equipment.ingot)
                            .addMaterial('N', metalMaterial.equipment.nugget)
                            .group("end_metal_chain")
                            .build(context);
                });
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ClientModel.build();
    }

    /**
     * Kept in a separate class file: Chain itself is always loaded on the server (it's
     * instantiated for the block-set registration), and a lambda body's synthetic method does
     * not inherit an @Environment(CLIENT) annotation from its enclosing method, so leaving it
     * here would strand vanilla client-only type references in a class file the server has to
     * verify.
     */
    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> {
                        // Mirrors WoverBlockModelGenerators#createChainModel's block model (parent
                        // minecraft:block/chain, ALL texture slot, axis-aligned pillar blockstate), but
                        // deliberately WITHOUT its delegateItemModel(3D) call: the inventory item should be a
                        // flat sprite (item/<name>.png), like a vanilla chain, not the 3D chain block model.
                        final var chainTemplate = new ModelTemplate(
                                Optional.of(ResourceLocation.withDefaultNamespace("block/chain")),
                                Optional.empty(),
                                TextureSlot.ALL
                        );
                        final var mapping = new TextureMapping()
                                .put(TextureSlot.ALL, TextureMapping.getBlockTexture(block));
                        final var model = chainTemplate.create(block, mapping, generator.modelOutput());
                        generator.vanillaGenerator.createAxisAlignedPillarBlockCustomModel(
                                block, BlockModelGenerators.plainVariant(model));
                        // Flat inventory sprite from the dedicated item/<name>.png art (a chain reads better
                        // as a flat icon, matching vanilla chains).
                        generator.createFlatItem(block, TextureMapping.getItemTexture(block.asItem()));
                    });
        }
    }
}
