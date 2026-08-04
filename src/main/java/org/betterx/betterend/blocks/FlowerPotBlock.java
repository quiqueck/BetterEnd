package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.entities.FlowerPotBlockEntity;
import org.betterx.betterend.client.models.EndModels;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.pottable.api.PottablePlant;
import de.ambertation.wover.pottable.api.PottablePlantRegistry;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class FlowerPotBlock extends Block implements EntityBlock {
    public static final IntegerProperty POT_LIGHT = EndBlockProperties.POT_LIGHT;
    private static final VoxelShape SHAPE_EMPTY;
    private static final VoxelShape SHAPE_FULL;

    public FlowerPotBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(POT_LIGHT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POT_LIGHT);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new FlowerPotBlockEntity(blockPos, blockState);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, Builder builder) {
        List<ItemStack> drop = Lists.newArrayList(new ItemStack(this));
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof FlowerPotBlockEntity flowerPot) {
            flowerPot.getSoilBlock().ifPresent(block -> drop.add(new ItemStack(block)));
            flowerPot.getPlantBlock().ifPresent(block -> drop.add(new ItemStack(block)));
        }
        return drop;
    }

    @Override
    protected @NotNull InteractionResult useItemOn(
            @NotNull ItemStack itemStack,
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand interactionHand,
            @NotNull BlockHitResult blockHitResult
    ) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof FlowerPotBlockEntity flowerPot)) {
            return InteractionResult.PASS;
        }

        if (flowerPot.getSoil().isEmpty()) {
            if (!(itemStack.getItem() instanceof BlockItem item)) {
                return InteractionResult.PASS;
            }
            // Look the soil registry up lazily (only in the branch that needs it) and null-safe:
            // using lookupOrThrow here - and eagerly for BOTH registries above - meant that if either
            // datapack registry was absent at runtime, the whole interaction threw server-side while
            // the client had already returned CONSUME, so the click did *nothing at all* (no sound, no
            // seated soil, no placed block). Seating soil must not depend on the plant registry.
            Registry<PottableSoil> soils = level.registryAccess()
                                                .lookup(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY)
                                                .orElse(null);
            Block block = item.getBlock();
            ResourceKey<Block> blockKey = block.builtInRegistryHolder().key();
            if (soils == null || findByBlock(soils, blockKey, soil -> soil.block) == null) {
                // Empty pot: a plant can only be placed once soil is in the pot. This used to return
                // silently, so trying to plant a seed/sapling first looked broken. Give an audible deny
                // and FAIL so the interaction is not a silent no-op.
                level.playSound(
                        player,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        SoundEvents.DISPENSER_FAIL,
                        SoundSource.BLOCKS,
                        0.6F,
                        1
                );
                return InteractionResult.FAIL;
            }
            flowerPot.setSoil(Optional.of(blockKey));
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }
            level.playSound(
                    player,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.SOUL_SOIL_PLACE,
                    SoundSource.BLOCKS,
                    1,
                    1
            );
            return InteractionResult.SUCCESS;
        }

        if (itemStack.isEmpty()) {
            if (flowerPot.getPlant().isPresent()) {
                Optional<Block> plantBlock = flowerPot.getPlantBlock();
                flowerPot.setPlant(Optional.empty());
                plantBlock.ifPresent(block -> player.addItem(new ItemStack(block)));
                return InteractionResult.SUCCESS;
            }
            Optional<Block> soilBlock = flowerPot.getSoilBlock();
            if (soilBlock.isPresent()) {
                flowerPot.setSoil(Optional.empty());
                player.addItem(new ItemStack(soilBlock.get()));
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!(itemStack.getItem() instanceof BlockItem item)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        Registry<PottablePlant> plants = level.registryAccess()
                                              .lookup(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY)
                                              .orElse(null);
        Block block = item.getBlock();
        ResourceKey<Block> blockKey = block.builtInRegistryHolder().key();
        PottablePlant plant = plants == null ? null : findByBlock(plants, blockKey, p -> p.block);
        if (plant == null) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        Block soilBlock = flowerPot.getSoilBlock().orElse(null);
        if (soilBlock == null || !plant.isValidSoil(soilBlock)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        flowerPot.setPlant(Optional.of(blockKey));
        level.playSound(
                player,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SoundEvents.HOE_TILL,
                SoundSource.BLOCKS,
                1,
                1
        );
        if (!player.isCreative()) {
            itemStack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private static <T> T findByBlock(
            Registry<T> registry,
            ResourceKey<Block> blockKey,
            Function<T, ResourceKey<Block>> accessor
    ) {
        for (T entry : registry) {
            if (accessor.apply(entry).equals(blockKey)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        if (view.getBlockEntity(pos) instanceof FlowerPotBlockEntity flowerPot && flowerPot.getPlant().isPresent()) {
            return SHAPE_FULL;
        }
        return SHAPE_EMPTY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_EMPTY;
    }

    /**
     * The lambda below must not live directly in this method: annotations like
     * @Environment(CLIENT) on an enclosing method are not applied to the synthetic method javac
     * generates for the lambda body, so Fabric's stripper leaves that synthetic method (and its
     * references to vanilla client-only datagen types) behind in this class file. Since
     * FlowerPotBlock itself is always loaded on the server (it's instantiated for real blocks),
     * verifying that orphaned method would crash server startup. Keeping the lambda in a separate,
     * never-unconditionally-loaded class file avoids that.
     */
    public static BlockModelTrait buildModel() {
        return ModCore.isDatagen() ? ClientModel.build() : null;
    }

    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> {
                        final ResourceLocation texture = TextureMapping.getBlockTexture(block);
                        final ResourceLocation location = EndModels.FLOWER_POT.create(
                                block,
                                new TextureMapping().put(TextureSlot.TEXTURE, texture),
                                generator.modelOutput()
                        );
                        final var variant = BlockModelGenerators.plainVariant(location);
                        generator.acceptBlockState(
                                MultiVariantGenerator
                                        .dispatch(block)
                                        .with(PropertyDispatch.initial(POT_LIGHT)
                                                              .select(0, variant)
                                                              .select(1, variant)
                                                              .select(2, variant)
                                                              .select(3, variant))
                        );
                        // Render the pot item as the 3D block model (same approach as
                        // umbrella_tree_membrane), not a flat sprite. Delegates the item model to the
                        // block model generated just above.
                        generator.delegateItemModel(block, location);
                    });
        }
    }

    static {
        SHAPE_EMPTY = Shapes.or(Block.box(4, 1, 4, 12, 8, 12), Block.box(5, 0, 5, 11, 1, 11));
        SHAPE_FULL = Shapes.or(SHAPE_EMPTY, Block.box(3, 8, 3, 13, 16, 13));
    }
}
