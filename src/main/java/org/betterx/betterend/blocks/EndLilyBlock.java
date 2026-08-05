package org.betterx.betterend.blocks;



import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.bclib.blocks.UnderwaterPlantBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

public class EndLilyBlock extends UnderwaterPlantBlock {
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;
    private static final VoxelShape SHAPE_BOTTOM = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_TOP = Block.box(2, 0, 2, 14, 6, 14);

    public EndLilyBlock(BlockBehaviour.Properties props) {
        super(props);
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
        if (!canSurvive(state, level, pos)) {
            return state.getValue(SHAPE) == TripleShape.TOP
                    ? Blocks.AIR.defaultBlockState()
                    : Blocks.WATER.defaultBlockState();
        } else {
            return state;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(pos);
        VoxelShape shape = state.getValue(SHAPE) == TripleShape.TOP ? SHAPE_TOP : SHAPE_BOTTOM;
        return shape.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(SHAPE) == TripleShape.TOP ? Fluids.EMPTY.defaultFluidState() : Fluids.WATER.getSource(
                false);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(SHAPE) == TripleShape.TOP) {
            return world.getBlockState(pos.below()).getBlock() == this;
        } else if (state.getValue(SHAPE) == TripleShape.BOTTOM) {
            return isTerrain(world.getBlockState(pos.below()));
        } else {
            BlockState up = world.getBlockState(pos.above());
            BlockState down = world.getBlockState(pos.below());
            return up.getBlock() == this && down.getBlock() == this;
        }
    }

    /**
     * Only the top segment drops (a random 1-2 lily leaves and 1-2 seeds); there is no block item -
     * replacing the block class's former {@code getDrops} override with a data-driven loot table.
     */
    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        return LootTable
                .lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(isTop(block))
                        .add(LootItem.lootTableItem(EndResourceItems.END_LILY_LEAF)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(isTop(block))
                        .add(LootItem.lootTableItem(EndWaterPlantBlocks.END_LILY_SEED)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))));
    }

    private static LootItemBlockStatePropertyCondition.Builder isTop(Block block) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SHAPE, TripleShape.TOP));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        return new ItemStack(EndWaterPlantBlocks.END_LILY_SEED);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }
}
