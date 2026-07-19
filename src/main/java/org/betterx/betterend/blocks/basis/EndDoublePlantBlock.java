package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

/**
 * Two-tall plant (a {@link #TOP} half stacked on a bottom half, sharing a random {@link #ROTATION}).
 * Reproduces the former BCLib {@code BaseDoublePlantBlock} on vanilla {@link Block}. Ground survival for the
 * bottom half defaults to the block's {@code SurvivesOnBlockTrait} ({@link #isValidGround}); render layer
 * and loot come from traits at registration. Bonemeal drops a copy (subclasses may override).
 */
public class EndDoublePlantBlock extends Block implements BonemealableBlock {
    private static final VoxelShape SHAPE = box(4, 2, 4, 12, 16, 12);
    public static final IntegerProperty ROTATION = org.betterx.wover.block.api.BlockProperties.ROTATION;
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public EndDoublePlantBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(TOP, ROTATION);
    }

    protected boolean isValidGround(BlockState state) {
        return SurvivesOnBlockTrait.survivesOn(this, state);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(pos);
        return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.below());
        BlockState up = world.getBlockState(pos.above());
        return state.getValue(TOP) ? down.getBlock() == this : isValidGround(down) && (up.canBeReplaced());
    }

    public boolean canStayAt(BlockState state, LevelReader world, BlockPos pos) {
        BlockState down = world.getBlockState(pos.below());
        BlockState up = world.getBlockState(pos.above());
        return state.getValue(TOP) ? down.getBlock() == this : isValidGround(down) && (up.getBlock() == this);
    }

    @Override
    protected @NotNull BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction neighborDirection,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource randomSource
    ) {
        if (!canStayAt(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return state;
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        ItemEntity item = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                new ItemStack(this)
        );
        level.addFreshEntity(item);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        int rot = world.random.nextInt(4);
        BlockState bs = this.defaultBlockState().setValue(ROTATION, rot);
        BlocksHelper.setWithoutUpdate(world, pos, bs);
        BlocksHelper.setWithoutUpdate(world, pos.above(), bs.setValue(TOP, true));
    }

    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        // Double/tall plants drop with ANY tool (like their single counterparts), not shears-only.
        return provider.dropDoublePlant(block);
    }
}
