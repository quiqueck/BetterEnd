package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.entities.InfusionPedestalEntity;
import org.betterx.betterend.client.effects.InfusionHint;
import org.betterx.betterend.client.gui.InfusionRecipeScreen;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.betterend.rituals.InfusionRitual;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class InfusionPedestal extends PedestalBlock {
    private static final VoxelShape SHAPE_DEFAULT;
    private static final VoxelShape SHAPE_PEDESTAL_TOP;

    public InfusionPedestal(ResourceKey<Block> blockKey) {
        super(Blocks.OBSIDIAN, blockKey);
        this.height = 1.08F;
    }

    @Override
    public void checkRitual(Level world, Player player, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof InfusionPedestalEntity) {
            InfusionPedestalEntity pedestal = (InfusionPedestalEntity) blockEntity;
            if (pedestal.hasRitual()) {
                InfusionRitual ritual = pedestal.getRitual();
                if (!ritual.isValid()) {
                    ritual.configure();
                }
                pedestal.getRitual().checkRecipe();
            } else {
                InfusionRitual ritual = pedestal.linkRitual(pedestal, world, pos);
                ritual.checkRecipe();
            }
        }
    }

    /**
     * Shows the socket hint as soon as the pedestal is placed - the ritual layout is the one thing a
     * player cannot discover from the block itself.
     * <p>
     * Runs client-side only. {@code setPlacedBy} is invoked on both sides (the client places the
     * block predictively), so the hint needs no packet; {@code ClientHooks} keeps the client-only
     * classes off the dedicated server's resolution path.
     */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide() && isPlaceable(state) && !InfusionRitual.allSocketsPresent(level, pos)) {
            ClientHooks.showHint(level, pos);
        }
    }

    /**
     * An empty hand on an empty infusion pedestal is the mod's "tell me about this thing" gesture:
     * with the ring incomplete it replays the socket hint, and with the ring in place it opens the
     * infusion recipe book. A pedestal that is holding something hands that item back instead - so
     * sneaking asks for the book directly, since otherwise a finished infusion would have to be
     * cleared off the pedestal before its recipe could be looked up again.
     * <p>
     * A sneak-click only reaches this with both hands empty: vanilla suppresses block use entirely
     * when the player is sneaking with anything in either hand (see {@code ServerPlayerGameMode}),
     * so the sneak branch can never swallow a block placement.
     */
    @Override
    public InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (state.is(this) && isPlaceable(state)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof InfusionPedestalEntity pedestal) {
                boolean ringComplete = InfusionRitual.allSocketsPresent(level, pos);
                boolean emptyHandOnEmptyPedestal = itemStack.isEmpty() && pedestal.isEmpty();

                if (ringComplete && (player.isSecondaryUseActive() || emptyHandOnEmptyPedestal)) {
                    if (level.isClientSide()) {
                        ClientHooks.showRecipes();
                    }
                    return InteractionResult.CONSUME;
                }
                if (emptyHandOnEmptyPedestal) {
                    if (level.isClientSide()) {
                        ClientHooks.showHint(level, pos);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hit);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new InfusionPedestalEntity(blockPos, blockState);
    }

    @Override
    public boolean hasUniqueEntity() {
        return true;
    }

    @Override
    @Deprecated
    public @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter world,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        if (state.is(this)) {
            return switch (state.getValue(STATE)) {
                case PEDESTAL_TOP -> SHAPE_PEDESTAL_TOP;
                case DEFAULT -> SHAPE_DEFAULT;
                default -> super.getShape(state, world, pos, context);
            };
        }
        return super.getShape(state, world, pos, context);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NotNull Level level,
            @NotNull BlockState blockState,
            @NotNull BlockEntityType<T> blockEntityType
    ) {
        return InfusionPedestalEntity::tickEntity;
    }

    /**
     * Fabric strips @Environment(CLIENT)-annotated fields on the server by simply removing the
     * field declaration, without patching the surviving <clinit> bytecode that assigns it - so a
     * field with a non-trivial initializer must live in its own lazily-loaded class instead of
     * being a direct field of a class (like this Block) that's always loaded on the server.
     */
    @Environment(EnvType.CLIENT)
    private static class Models {
        private static final Map<EndBlockProperties.PedestalState, ModelTemplate> PEDESTAL_MODELS = Map.of(
                EndBlockProperties.PedestalState.DEFAULT, EndModels.INFUSION_PEDESTAL_DEFAULT,
                EndBlockProperties.PedestalState.PEDESTAL_TOP, EndModels.INFUSION_PEDESTAL_TOP,
                EndBlockProperties.PedestalState.COLUMN_TOP, EndModels.PEDESTAL_COLUMN_TOP,
                EndBlockProperties.PedestalState.COLUMN, EndModels.PEDESTAL_COLUMN,
                EndBlockProperties.PedestalState.BOTTOM, EndModels.PEDESTAL_BOTTOM,
                EndBlockProperties.PedestalState.PILLAR, EndModels.PEDESTAL_PILLAR
        );
    }

    /**
     * Same reasoning as {@link Models}: the dedicated server strips {@code @Environment(CLIENT)}
     * types, so every touch of the client-only hint/GUI classes goes through this holder, which is
     * only ever resolved behind a {@code level.isClientSide()} check.
     */
    @Environment(EnvType.CLIENT)
    private static class ClientHooks {
        private static void showHint(Level level, BlockPos pos) {
            InfusionHint.trigger(level, pos);
        }

        private static void showRecipes() {
            Minecraft.getInstance().setScreen(new InfusionRecipeScreen());
        }
    }

    @Environment(EnvType.CLIENT)
    protected TextureMapping createTextureMapping() {
        final var parentTexture = TextureMapping.getBlockTexture(this).sprite();
        return new TextureMapping()
                .put(TextureSlot.TOP, new Material(parentTexture.withSuffix("_top")))
                .put(TextureSlot.BOTTOM, new Material(parentTexture.withSuffix("_base")))
                .put(EndModels.BASE, new Material(parentTexture.withSuffix("_base")))
                .put(EndModels.PILLAR, new Material(parentTexture.withSuffix("_pillar")));
    }

    @Environment(EnvType.CLIENT)
    public void provideBlockModelsInstance(WoverBlockModelGenerators generator) {
        provideBlockModel(generator, createTextureMapping(), this, Models.PEDESTAL_MODELS);
    }

    static {
        VoxelShape basinUp = Block.box(2, 3, 2, 14, 4, 14);
        VoxelShape basinDown = Block.box(0, 0, 0, 16, 3, 16);
        VoxelShape pedestalTop = Block.box(1, 9, 1, 15, 11, 15);
        VoxelShape pedestalDefault = Block.box(1, 13, 1, 15, 15, 15);
        VoxelShape pillar = Block.box(3, 0, 3, 13, 9, 13);
        VoxelShape pillarDefault = Block.box(3, 4, 3, 13, 13, 13);
        VoxelShape eyeDefault = Block.box(4, 15, 4, 12, 16, 12);
        VoxelShape eyeTop = Block.box(4, 11, 4, 12, 12, 12);
        VoxelShape basin = Shapes.or(basinDown, basinUp);
        SHAPE_DEFAULT = Shapes.or(basin, pillarDefault, pedestalDefault, eyeDefault);
        SHAPE_PEDESTAL_TOP = Shapes.or(pillar, pedestalTop, eyeTop);
    }
}
