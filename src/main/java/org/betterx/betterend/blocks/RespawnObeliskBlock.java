package org.betterx.betterend.blocks;



import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.particle.InfusionParticleType;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.ui.ColorUtil;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RespawnObeliskBlock extends Block {
    private static final VoxelShape VOXEL_SHAPE_BOTTOM = Block.box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape VOXEL_SHAPE_MIDDLE_TOP = Block.box(2, 0, 2, 14, 16, 14);

    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    public RespawnObeliskBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return (state.getValue(SHAPE) == TripleShape.BOTTOM) ? VOXEL_SHAPE_BOTTOM : VOXEL_SHAPE_MIDDLE_TOP;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        for (int i = 0; i < 3; i++) {
            if (!world.getBlockState(pos.above(i)).canBeReplaced()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack
    ) {
        state = this.defaultBlockState();
        BlocksHelper.setWithUpdate(world, pos, state.setValue(SHAPE, TripleShape.BOTTOM));
        BlocksHelper.setWithUpdate(world, pos.above(), state.setValue(SHAPE, TripleShape.MIDDLE));
        BlocksHelper.setWithUpdate(world, pos.above(2), state.setValue(SHAPE, TripleShape.TOP));
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
        TripleShape shape = state.getValue(SHAPE);
        if (shape == TripleShape.BOTTOM) {
            if (world.getBlockState(pos.above()).is(this)) {
                return state;
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else if (shape == TripleShape.MIDDLE) {
            if (world.getBlockState(pos.above()).is(this) && world.getBlockState(pos.below()).is(this)) {
                return state;
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            if (world.getBlockState(pos.below()).is(this)) {
                return state;
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (player.isCreative()) {
            TripleShape shape = state.getValue(SHAPE);
            if (shape == TripleShape.MIDDLE) {
                BlocksHelper.setWithUpdate(world, pos.below(), Blocks.AIR);
            } else if (shape == TripleShape.TOP) {
                BlocksHelper.setWithUpdate(world, pos.below(2), Blocks.AIR);
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    /**
     * The obelisk is three blocks tall but one item: only the {@code bottom} segment drops it, so breaking
     * any part of it yields exactly one obelisk rather than one per segment.
     * <p>
     * This replaces a {@code getDrops} override, which bypassed the loot table entirely - the generated
     * table said "always drop self", which would have handed out three obelisks per structure had the
     * override ever been removed. The override never consulted {@code survives_explosion}, so this table
     * does not either.
     */
    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition
                                .hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder
                                        .properties()
                                        .hasProperty(SHAPE, TripleShape.BOTTOM)))
                        .add(LootItem.lootTableItem(block)));
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
        boolean canActivate = itemStack.getItem() == EndResourceItems.AMBER_GEM && itemStack.getCount() > 5;
        if (hand != InteractionHand.MAIN_HAND || !canActivate) {
            if (!level.isClientSide() && !(itemStack.getItem() instanceof BlockItem) && !player.isCreative()) {
                ServerPlayer serverPlayerEntity = (ServerPlayer) player;
                serverPlayerEntity.sendOverlayMessage(
                        Component.translatable("message.betterend.fail_spawn")
                );
            }
            return InteractionResult.FAIL;
        } else if (!level.isClientSide()) {
            ServerPlayer serverPlayerEntity = (ServerPlayer) player;
            serverPlayerEntity.setRespawnPosition(
                    new ServerPlayer.RespawnConfig(
                            LevelData.RespawnData.of(level.dimension(), pos, 0.0F, 0.0F),
                            false
                    ),
                    false
            );
            serverPlayerEntity.sendOverlayMessage(Component.translatable("message.betterend.set_spawn"));
            double px = pos.getX() + 0.5;
            double py = pos.getY() + 0.5;
            double pz = pos.getZ() + 0.5;
            InfusionParticleType particle = new InfusionParticleType(new ItemStack(EndResourceItems.AMBER_GEM));
            if (level instanceof ServerLevel) {
                double py1 = py;
                double py2 = py - 0.2;
                if (state.getValue(SHAPE) == TripleShape.BOTTOM) {
                    py1 += 1;
                    py2 += 2;
                } else if (state.getValue(SHAPE) == TripleShape.MIDDLE) {
                    py1 += 0;
                    py2 += 1;
                } else {
                    py1 -= 2;
                }
                ((ServerLevel) level).sendParticles(particle, px, py1, pz, 20, 0.14, 0.5, 0.14, 0.1);
                ((ServerLevel) level).sendParticles(particle, px, py2, pz, 20, 0.14, 0.3, 0.14, 0.1);
            }
            level.playSound(null, px, py, py, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1F, 1F);
            if (!player.isCreative()) {
                itemStack.shrink(6);
            }
        }
        return player.isCreative()
                ? InteractionResult.TRY_WITH_EMPTY_HAND
                : InteractionResult.SUCCESS;
    }
}
