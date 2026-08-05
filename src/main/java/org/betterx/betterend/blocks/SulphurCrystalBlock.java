package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.Maps;

import java.util.EnumMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SulphurCrystalBlock extends BaseAttachedBlock.Glass implements SimpleWaterloggedBlock, LiquidBlockContainer {
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(Direction.class);
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SulphurCrystalBlock(BlockBehaviour.Properties props) {
        super(props);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(AGE, WATERLOGGED);
    }

    private static LootItemConditionalFunction.@NotNull Builder<?> applyAgeBonus(
            Block block,
            @NotNull LootLookupProvider provider,
            int i
    ) {
        return ApplyBonusCount
                .addUniformBonusCount(provider.fortune(), i)
                .when(ageCondition(block, i));
    }

    private static LootItemBlockStatePropertyCondition.@NotNull Builder ageCondition(Block block, int i) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder
                        .properties()
                        .hasProperty(AGE, i));
    }

    public static LootTable.Builder buildLoot(Block block, @NotNull LootLookupProvider provider) {
        return LootTable
                .lootTable()
                .withPool(
                        LootPool.lootPool()
                                .when(provider.hasSilkTouch())
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(block)
                                             .apply(SetItemCountFunction
                                                     .setCount(UniformGenerator.between(1, 3))
                                                     .when(ageCondition(block, 3))
                                             )
                                             .apply(SetItemCountFunction
                                                     .setCount(ConstantValue.exactly(1))
                                                     .when(InvertedLootItemCondition.invert(ageCondition(block, 3)))
                                             )
                                             .apply(ApplyBonusCount
                                                     .addOreBonusCount(provider.fortune())
                                                     .when(ageCondition(block, 3))
                                             )
                                             .apply(applyAgeBonus(block, provider, 2))
                                             .apply(applyAgeBonus(block, provider, 1))
                                             .apply(ApplyExplosionDecay.explosionDecay())
                                )
                )
                .withPool(
                        LootPool.lootPool()
                                .when(AllOfCondition.allOf(
                                        InvertedLootItemCondition.invert(provider.hasSilkTouch()),
                                        ageCondition(block, 3)
                                ))
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(EndResourceItems.CRYSTALLINE_SULPHUR)
                                             .apply(SetItemCountFunction
                                                     .setCount(UniformGenerator.between(1, 3))
                                             )
                                             .apply(ApplyExplosionDecay.explosionDecay())
                                )
                );
    }

    @Override
    public boolean canPlaceLiquid(
            @Nullable LivingEntity livingEntity,
            BlockGetter blockGetter,
            BlockPos blockPos,
            BlockState blockState,
            Fluid fluid
    ) {
        return !blockState.getValue(WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state != null) {
            LevelReader worldView = ctx.getLevel();
            BlockPos blockPos = ctx.getClickedPos();
            boolean water = worldView.getFluidState(blockPos).getType() == Fluids.WATER;
            return state.setValue(WATERLOGGED, water);
        }
        return null;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return BOUNDING_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(blockPos));
    }

    static {
        BOUNDING_SHAPES.put(Direction.UP, Shapes.box(0.125, 0.0, 0.125, 0.875F, 0.5, 0.875F));
        BOUNDING_SHAPES.put(Direction.DOWN, Shapes.box(0.125, 0.5, 0.125, 0.875F, 1.0, 0.875F));
        BOUNDING_SHAPES.put(Direction.NORTH, Shapes.box(0.125, 0.125, 0.5, 0.875F, 0.875F, 1.0));
        BOUNDING_SHAPES.put(Direction.SOUTH, Shapes.box(0.125, 0.125, 0.0, 0.875F, 0.875F, 0.5));
        BOUNDING_SHAPES.put(Direction.WEST, Shapes.box(0.5, 0.125, 0.125, 1.0, 0.875F, 0.875F));
        BOUNDING_SHAPES.put(Direction.EAST, Shapes.box(0.0, 0.125, 0.125, 0.5, 0.875F, 0.875F));
    }
}
