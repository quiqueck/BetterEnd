package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.blocks.BaseBlockNotFull;
import org.betterx.betterend.blocks.EndBlockProperties;
import org.betterx.betterend.blocks.EndBlockProperties.PedestalState;
import org.betterx.betterend.blocks.InfusionPedestal;
import org.betterx.betterend.blocks.entities.InfusionPedestalEntity;
import org.betterx.betterend.blocks.entities.PedestalBlockEntity;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.rituals.InfusionRitual;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockTagProvider;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Lists;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends BaseBlockNotFull implements EntityBlock, BlockTagProvider {
    public final static EnumProperty<PedestalState> STATE = EndBlockProperties.PEDESTAL_STATE;
    public static final BooleanProperty HAS_ITEM = EndBlockProperties.HAS_ITEM;
    public static final BooleanProperty HAS_LIGHT = BlockProperties.HAS_LIGHT;

    private static final VoxelShape SHAPE_DEFAULT;
    private static final VoxelShape SHAPE_COLUMN;
    private static final VoxelShape SHAPE_PILLAR;
    private static final VoxelShape SHAPE_PEDESTAL_TOP;
    private static final VoxelShape SHAPE_COLUMN_TOP;
    private static final VoxelShape SHAPE_BOTTOM;


    protected float height = 1.0F;

    public PedestalBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(
                stateDefinition
                        .any()
                        .setValue(STATE, PedestalState.DEFAULT)
                        .setValue(HAS_ITEM, false)
                        .setValue(HAS_LIGHT, false)
        );
    }

    public PedestalBlock(Block parent, ResourceKey<Block> blockKey) {
        this(
                BlockBehaviour.Properties.ofFullCopy(parent)
                        .setId(blockKey)
                        .lightLevel(getLuminance(parent.defaultBlockState()))
        );
    }

    private static ToIntFunction<BlockState> getLuminance(BlockState parent) {
        final int light = parent.getLightEmission();
        if (light > 0) {
            return state -> light;
        }
        return state -> state.getValue(HAS_LIGHT) ? 12 : 0;
    }

    public float getHeight(BlockState state) {
        if (state.getBlock() instanceof PedestalBlock && state.getValue(STATE) == PedestalState.PEDESTAL_TOP) {
            return this.height - 0.2F;
        }
        return this.height;
    }

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
        if (!state.is(this) || !isPlaceable(state)) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PedestalBlockEntity pedestal) {
            if (pedestal.isEmpty()) {
                if (itemStack.isEmpty()) return InteractionResult.CONSUME;
                pedestal.setItem(0, itemStack);
                level.blockEntityChanged(pos);
                checkRitual(level, player, pos);
                return InteractionResult.SUCCESS;
            } else {
                ItemStack stack = pedestal.getItem(0);
                if (player.addItem(stack)) {
                    pedestal.removeItemNoUpdate(0);
                    level.blockEntityChanged(pos);
                    checkRitual(level, player, pos);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        MutableBlockPos posMutable = new MutableBlockPos();
        for (Point point : InfusionRitual.getMap()) {
            posMutable.set(blockPos).move(point.x, 0, point.y);
            BlockState state = levelAccessor.getBlockState(posMutable);
            if (state.getBlock() instanceof InfusionPedestal) {
                BlockEntity blockEntity = levelAccessor.getBlockEntity(posMutable);
                if (blockEntity instanceof InfusionPedestalEntity pedestal) {
                    if (pedestal.hasRitual()) {
                        pedestal.getRitual().setDirty();
                    }
                }
                break;
            }
        }
    }

    public void checkRitual(Level world, Player player, BlockPos pos) {
        MutableBlockPos posMutable = new MutableBlockPos();
        for (Point point : InfusionRitual.getMap()) {
            posMutable.set(pos).move(point.x, 0, point.y);
            BlockState state = world.getBlockState(posMutable);
            if (state.getBlock() instanceof InfusionPedestal) {
                ((InfusionPedestal) state.getBlock()).checkRitual(world, player, posMutable);
                break;
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState upState = world.getBlockState(pos.above());
        BlockState downState = world.getBlockState(pos.below());
        boolean upSideSolid = upState.isFaceSturdy(world, pos.above(), Direction.DOWN) || upState.is(BlockTags.WALLS);
        boolean hasPedestalOver = upState.getBlock() instanceof PedestalBlock;
        boolean hasPedestalUnder = downState.getBlock() instanceof PedestalBlock;
        if (!hasPedestalOver && hasPedestalUnder && upSideSolid) {
            return defaultBlockState().setValue(STATE, PedestalState.COLUMN_TOP);
        } else if (!hasPedestalOver && !hasPedestalUnder && upSideSolid) {
            return defaultBlockState().setValue(STATE, PedestalState.COLUMN);
        } else if (hasPedestalUnder && hasPedestalOver) {
            return defaultBlockState().setValue(STATE, PedestalState.PILLAR);
        } else if (hasPedestalUnder) {
            return defaultBlockState().setValue(STATE, PedestalState.PEDESTAL_TOP);
        } else if (hasPedestalOver) {
            return defaultBlockState().setValue(STATE, PedestalState.BOTTOM);
        }
        return defaultBlockState();
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos posFrom,
            BlockState newState,
            RandomSource randomSource
    ) {
        BlockState updated = getUpdatedState(
                state,
                direction,
                newState,
                world,
                scheduledTickAccess,
                pos,
                posFrom,
                randomSource
        );
        if (!updated.is(this)) return updated;
        if (!isPlaceable(updated)) {
            moveStoredStack(world, updated, pos);
        }
        return updated;
    }

    private BlockState getUpdatedState(
            BlockState state,
            Direction direction,
            BlockState newState,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            BlockPos posFrom,
            RandomSource randomSource
    ) {
        if (!state.is(this)) {
            return state.updateShape(world, scheduledTickAccess, pos, direction, posFrom, newState, randomSource);
        }
        if (direction != Direction.UP && direction != Direction.DOWN) return state;
        BlockState upState = world.getBlockState(pos.above());
        BlockState downState = world.getBlockState(pos.below());
        boolean upSideSolid = upState.isFaceSturdy(world, pos.above(), Direction.DOWN) || upState.is(BlockTags.WALLS);
        boolean hasPedestalOver = upState.getBlock() instanceof PedestalBlock;
        boolean hasPedestalUnder = downState.getBlock() instanceof PedestalBlock;
        if (direction == Direction.UP) {
            upSideSolid = newState.isFaceSturdy(world, posFrom, Direction.DOWN) || newState.is(BlockTags.WALLS);
            hasPedestalOver = newState.getBlock() instanceof PedestalBlock;
        } else {
            hasPedestalUnder = newState.getBlock() instanceof PedestalBlock;
        }
        BlockState updatedState;
        if (!hasPedestalOver && hasPedestalUnder && upSideSolid) {
            updatedState = state.setValue(STATE, PedestalState.COLUMN_TOP);
        } else if (!hasPedestalOver && !hasPedestalUnder && upSideSolid) {
            updatedState = state.setValue(STATE, PedestalState.COLUMN);
        } else if (hasPedestalUnder && hasPedestalOver) {
            updatedState = state.setValue(STATE, PedestalState.PILLAR);
        } else if (hasPedestalUnder) {
            updatedState = state.setValue(STATE, PedestalState.PEDESTAL_TOP);
        } else if (hasPedestalOver) {
            updatedState = state.setValue(STATE, PedestalState.BOTTOM);
        } else {
            updatedState = state.setValue(STATE, PedestalState.DEFAULT);
        }
        if (!isPlaceable(updatedState)) {
            updatedState = updatedState.setValue(HAS_ITEM, false).setValue(HAS_LIGHT, false);
        }
        return updatedState;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<ItemStack> drop = Lists.newArrayList(super.getDrops(state, builder));
        if (state.is(this)) {
            if (isPlaceable(state)) {
                BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
                if (blockEntity instanceof PedestalBlockEntity pedestal) {
                    if (!pedestal.isEmpty()) {
                        drop.add(pedestal.getItem(0));
                    }
                }
            } else {
                return drop;
            }
        }
        return drop;
    }

    private void moveStoredStack(LevelReader world, BlockState state, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PedestalBlockEntity pedestal && state.is(this)) {
            ItemStack stack = pedestal.removeItemNoUpdate(0);
            if (!stack.isEmpty()) {
                moveStoredStack(blockEntity, world, stack, pos.above());
            }
        }
    }

    private void moveStoredStack(BlockEntity blockEntity, LevelReader world, ItemStack stack, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.is(this)) {
            dropStoredStack(blockEntity, stack, pos);
        } else if (state.getValue(STATE).equals(PedestalState.PILLAR)) {
            moveStoredStack(blockEntity, world, stack, pos.above());
        } else if (!isPlaceable(state)) {
            dropStoredStack(blockEntity, stack, pos);
        } else if (blockEntity instanceof PedestalBlockEntity pedestal) {
            if (pedestal.isEmpty()) {
                pedestal.setItem(0, stack);
            } else {
                dropStoredStack(blockEntity, stack, pos);
            }
        } else {
            dropStoredStack(blockEntity, stack, pos);
        }
    }

    private void dropStoredStack(BlockEntity blockEntity, ItemStack stack, BlockPos pos) {
        if (blockEntity != null && blockEntity.getLevel() != null) {
            Level world = blockEntity.getLevel();
            Block.popResource(world, getDropPos(world, pos), stack);
        }
    }

    private BlockPos getDropPos(LevelAccessor world, BlockPos pos) {
        BlockPos dropPos;
        if (world.getBlockState(pos).isAir()) {
            return pos;
        }
        if (world.getBlockState(pos.above()).isAir()) {
            return pos.above();
        }
        for (int i = 2; i < Direction.values().length; i++) {
            dropPos = pos.relative(Direction.from3DDataValue(i));
            if (world.getBlockState(dropPos).isAir()) {
                return dropPos.immutable();
            }
        }
        return getDropPos(world, pos.above());
    }

    public boolean isPlaceable(BlockState state) {
        if (!state.is(this)) return false;
        PedestalState currentState = state.getValue(STATE);
        return currentState == PedestalState.DEFAULT || currentState == PedestalState.PEDESTAL_TOP;
    }

    @Override
    public @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter world,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        if (state.is(this)) {
            return switch (state.getValue(STATE)) {
                case BOTTOM -> SHAPE_BOTTOM;
                case PEDESTAL_TOP -> SHAPE_PEDESTAL_TOP;
                case COLUMN_TOP -> SHAPE_COLUMN_TOP;
                case PILLAR -> SHAPE_PILLAR;
                case COLUMN -> SHAPE_COLUMN;
                default -> SHAPE_DEFAULT;
            };
        }
        return super.getShape(state, world, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(STATE, HAS_ITEM, HAS_LIGHT);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PedestalBlockEntity(blockPos, blockState);
    }

    public boolean hasUniqueEntity() {
        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return state.getBlock() instanceof PedestalBlock;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
        return state.getValue(HAS_ITEM) ? 15 : 0;
    }

    /**
     * Fabric strips @Environment(CLIENT)-annotated fields on the server by simply removing the
     * field declaration, without patching the surviving <clinit> bytecode that assigns it - so a
     * field with a non-trivial initializer must live in its own lazily-loaded class instead of
     * being a direct field of a class (like this Block) that's always loaded on the server.
     */
    @Environment(EnvType.CLIENT)
    private static class Models {
        private static final Map<PedestalState, ModelTemplate> PEDESTAL_MODELS = Map.of(
                PedestalState.DEFAULT, EndModels.PEDESTAL_DEFAULT,
                PedestalState.PEDESTAL_TOP, EndModels.PEDESTAL_TOP,
                PedestalState.COLUMN_TOP, EndModels.PEDESTAL_COLUMN_TOP,
                PedestalState.COLUMN, EndModels.PEDESTAL_COLUMN,
                PedestalState.BOTTOM, EndModels.PEDESTAL_BOTTOM,
                PedestalState.PILLAR, EndModels.PEDESTAL_PILLAR
        );
    }


    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(
            WoverBlockModelGenerators generator,
            TextureMapping mapping,
            Block pedestalBlock
    ) {
        provideBlockModel(generator, mapping, pedestalBlock, Models.PEDESTAL_MODELS);
    }

    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(
            WoverBlockModelGenerators generator,
            TextureMapping mapping,
            Block pedestalBlock,
            Map<PedestalState, ModelTemplate> pedestalModels
    ) {
        final ResourceLocation id = TextureMapping.getBlockTexture(pedestalBlock);

        var properties = PropertyDispatch.modify(STATE);
        // The first model built also serves as the default for dispatch - build each model
        // exactly once, since generator.vanillaGenerator.modelOutput rejects duplicates.
        ResourceLocation defaultModel = null;

        for (var entry : pedestalModels.entrySet()) {
            final String suffix = "_" + entry.getKey();
            ResourceLocation model = entry
                    .getValue()
                    .createWithSuffix(pedestalBlock, suffix, mapping, generator.vanillaGenerator.modelOutput);
            properties = properties.select(entry.getKey(), (variant) -> BlockModelGenerators.plainModel(model));
            if (defaultModel == null) {
                defaultModel = model;
            }
        }

        generator.acceptBlockState(MultiVariantGenerator
                .dispatch(pedestalBlock, BlockModelGenerators.plainVariant(defaultModel))
                .with(properties));
        generator.delegateItemModel(pedestalBlock, id.withSuffix("_default"));
    }

    @Environment(EnvType.CLIENT)
    protected static TextureMapping createTextureMapping(Block sourceBlock) {
        final var parentTexture = TextureMapping.getBlockTexture(sourceBlock);
        return new TextureMapping()
                .put(TextureSlot.TOP, parentTexture.withSuffix("_top"))
                .put(TextureSlot.BOTTOM, parentTexture.withSuffix("_bottom"))
                .put(EndModels.BASE, parentTexture.withSuffix("_base"))
                .put(EndModels.PILLAR, parentTexture.withSuffix("_pillar"));
    }

    public static BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return ClientModelBuilder.build(set);
    }

    @Environment(EnvType.CLIENT)
    private static class ClientModelBuilder {
        private static BlockModelTrait build(BlockSet<?> set) {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> {
                        provideBlockModel(generator, createTextureMapping(set.getBaseBlock()), block);
                    }
            );
        }
    }


    static {
        VoxelShape basinUp = Block.box(2, 3, 2, 14, 4, 14);
        VoxelShape basinDown = Block.box(0, 0, 0, 16, 3, 16);
        VoxelShape columnTopUp = Block.box(1, 14, 1, 15, 16, 15);
        VoxelShape columnTopDown = Block.box(2, 13, 2, 14, 14, 14);
        VoxelShape pedestalTop = Block.box(1, 8, 1, 15, 10, 15);
        VoxelShape pedestalDefault = Block.box(1, 12, 1, 15, 14, 15);
        VoxelShape pillar = Block.box(3, 0, 3, 13, 8, 13);
        VoxelShape pillarDefault = Block.box(3, 0, 3, 13, 12, 13);
        VoxelShape columnTop = Shapes.or(columnTopDown, columnTopUp);
        VoxelShape basin = Shapes.or(basinDown, basinUp);
        SHAPE_PILLAR = Block.box(3, 0, 3, 13, 16, 13);
        SHAPE_DEFAULT = Shapes.or(basin, pillarDefault, pedestalDefault);
        SHAPE_PEDESTAL_TOP = Shapes.or(pillar, pedestalTop);
        SHAPE_COLUMN_TOP = Shapes.or(SHAPE_PILLAR, columnTop);
        SHAPE_COLUMN = Shapes.or(basin, SHAPE_PILLAR, columnTop);
        SHAPE_BOTTOM = Shapes.or(basin, SHAPE_PILLAR);
    }

    @Override
    public void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context) {
        context.add(EndTags.PEDESTALS, this);
    }
}
