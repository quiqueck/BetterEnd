package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseBlockWithEntity;
import org.betterx.bclib.interfaces.AlloyingRecipeWorkstation;
import org.betterx.betterend.blocks.entities.EndStoneSmelterBlockEntity;
import org.betterx.betterend.registry.EndBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Lists;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndStoneSmelter extends BaseBlockWithEntity.Stone implements AlloyingRecipeWorkstation {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final String ID = "end_stone_smelter";

    public EndStoneSmelter(BlockBehaviour.Properties props) {
        super(BehaviourBuilders.createStone(props, MapColor.COLOR_YELLOW)
                               .lightLevel(state -> state.getValue(LIT) ? 15 : 0)
                               .strength(4F, 100F)
                               .requiresCorrectToolForDrops()
        );
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState blockState,
            Level level,
            BlockPos blockPos,
            Player player,
            BlockHitResult blockHitResult
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        this.openScreen(level, blockPos, player);
        return InteractionResult.CONSUME;
    }

    private void openScreen(Level world, BlockPos pos, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EndStoneSmelterBlockEntity) {
            player.openMenu((EndStoneSmelterBlockEntity) blockEntity);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EndStoneSmelterBlockEntity(blockPos, blockState);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drop = Lists.newArrayList(new ItemStack(this));
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof EndStoneSmelterBlockEntity) {
            EndStoneSmelterBlockEntity smelterBlockEntity = (EndStoneSmelterBlockEntity) blockEntity;
            for (int i = 0; i < smelterBlockEntity.getContainerSize(); i++) {
                ItemStack item = smelterBlockEntity.getItem(i);
                if (!item.isEmpty()) {
                    drop.add(item);
                }
            }
        }
        return drop;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;
            if (random.nextDouble() < 0.1D) {
                world.playLocalSound(
                        x,
                        y,
                        z,
                        SoundEvents.BLASTFURNACE_FIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F,
                        false
                );
            }

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double defOffset = random.nextDouble() * 0.6D - 0.3D;
            double offX = axis == Direction.Axis.X ? direction.getStepX() * 0.52D : defOffset;
            double offY = random.nextDouble() * 9.0D / 16.0D;
            double offZ = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52D : defOffset;
            world.addParticle(ParticleTypes.SMOKE, x + offX, y + offY, z + offZ, 0.0D, 0.0D, 0.0D);
        }
    }


    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NotNull Level level,
            @NotNull BlockState inputBlockState,
            @NotNull BlockEntityType<T> inputBlockEntityType
    ) {
        return level instanceof ServerLevel serverLevel
                ? createTickerHelper(
                inputBlockEntityType,
                EndBlockEntities.END_STONE_SMELTER,
                (__, blockPos, blockState, abstractFurnaceBlockEntity) ->
                        EndStoneSmelterBlockEntity.serverTick(
                                serverLevel, blockPos, blockState, abstractFurnaceBlockEntity
                        )
        )
                : null;
    }
}
