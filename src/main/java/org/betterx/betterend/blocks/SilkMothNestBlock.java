package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.item.EndResourceItems;
import net.minecraft.world.level.block.Block;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.LootUtil;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.entity.SilkMothEntity;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.EndItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class SilkMothNestBlock extends Block {
    public static final BooleanProperty ACTIVE = EndBlockProperties.ACTIVE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty FULLNESS = EndBlockProperties.FULLNESS;
    private static final VoxelShape TOP = box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape BOTTOM = box(0, 0, 0, 16, 16, 16);

    public SilkMothNestBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(defaultBlockState().setValue(ACTIVE, true).setValue(FULLNESS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(ACTIVE, FACING, FULLNESS);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(ACTIVE) ? BOTTOM : TOP;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, dir);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction facing,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (!state.getValue(ACTIVE)) {
            final BlockPos above = pos.above();
            // Cube leaves only, matching SilkMothNestFeature.canGenerate - a nest under the thin
            // *_outer_leaves would hang half a block clear of it.
            if (canSupportCenter(world, above, Direction.DOWN)
                    || BlocksHelper.isCubeLeaves(world, above, world.getBlockState(above))) {
                return state;
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return BlocksHelper.rotateHorizontal(state, rotation, FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return BlocksHelper.mirrorHorizontal(state, mirror, FACING);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return state.getValue(ACTIVE) ? Collections.singletonList(new ItemStack(this)) : Collections.emptyList();
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!state.getValue(ACTIVE) && player.isCreative()) {
            BlocksHelper.setWithUpdate(world, pos.below(), Blocks.AIR);
        }
        BlockState up = world.getBlockState(pos.above());
        if (up.is(this) && !up.getValue(ACTIVE)) {
            BlocksHelper.setWithUpdate(world, pos.above(), Blocks.AIR);
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE)) {
            return;
        }
        if (random.nextBoolean()) {
            return;
        }
        Direction dir = state.getValue(FACING);
        BlockPos spawn = pos.relative(dir);
        if (!world.getBlockState(spawn).isAir()) {
            return;
        }
        int count = world.getEntities(
                EndEntities.SILK_MOTH.type(), new AABB(pos).inflate(16), (entity) -> {
                    return true;
                }
        ).size();
        if (count > 6) {
            return;
        }
        SilkMothEntity moth = new SilkMothEntity(EndEntities.SILK_MOTH.type(), world);
        moth.snapTo(spawn.getX() + 0.5, spawn.getY() + 0.5, spawn.getZ() + 0.5, dir.toYRot(), 0);
        moth.setDeltaMovement(new Vec3(dir.getStepX() * 0.4, 0, dir.getStepZ() * 0.4));
        moth.setHive(world, pos);
        world.addFreshEntity(moth);
        world.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1, 1);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult blockHitResult
    ) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (LootUtil.isShear(stack) && state.getValue(ACTIVE) && state.getValue(FULLNESS) == 3) {
                BlocksHelper.setWithUpdate(level, pos, state.setValue(FULLNESS, 0));
                Direction dir = state.getValue(FACING);
                double px = pos.getX() + dir.getStepX() + 0.5;
                double py = pos.getY() + dir.getStepY() + 0.5;
                double pz = pos.getZ() + dir.getStepZ() + 0.5;
                ItemStack drop = new ItemStack(EndResourceItems.SILK_FIBER, MHelper.randRange(1, 4, level.getRandom()));
                ItemEntity entity = new ItemEntity(level, px, py, pz, drop);
                level.addFreshEntity(entity);
                drop = new ItemStack(EndResourceItems.SILK_MOTH_MATRIX, MHelper.randRange(1, 3, level.getRandom()));
                entity = new ItemEntity(level, px, py, pz, drop);
                level.addFreshEntity(entity);
                if (!player.isCreative()) {
                    stack.setDamageValue(stack.getDamageValue() + 1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }
}
