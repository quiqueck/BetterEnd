package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.item.EndResourceItems;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

public class SilkMothHiveBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty FULLNESS = EndBlockProperties.FULLNESS;

    public SilkMothHiveBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(defaultBlockState().setValue(FULLNESS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, FULLNESS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, dir);
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
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
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
            @NotNull ItemStack itemStack,
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult blockHitResult
    ) {
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getMainHandItem();
            if (LootUtil.isShear(stack) && state.getValue(FULLNESS) == 3) {
                BlocksHelper.setWithUpdate(level, pos, state.setValue(FULLNESS, 0));
                Direction dir = state.getValue(FACING);
                double px = pos.getX() + dir.getStepX() + 0.5;
                double py = pos.getY() + dir.getStepY() + 0.5;
                double pz = pos.getZ() + dir.getStepZ() + 0.5;
                ItemStack drop = new ItemStack(EndResourceItems.SILK_FIBER, MHelper.randRange(8, 16, level.getRandom()));
                ItemEntity entity = new ItemEntity(level, px, py, pz, drop);
                level.addFreshEntity(entity);
                if (level.getRandom().nextInt(4) == 0) {
                    drop = new ItemStack(EndResourceItems.SILK_MOTH_MATRIX);
                    entity = new ItemEntity(level, px, py, pz, drop);
                    level.addFreshEntity(entity);
                }
                if (!player.isCreative()) {
                    stack.setDamageValue(stack.getDamageValue() + 1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }
}
