package org.betterx.betterend.blocks;



import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import de.ambertation.wover.loot.api.LootLookupProvider;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@SuppressWarnings("deprecation")
public class LumecornBlock extends Block {
    public static final EnumProperty<EndBlockProperties.LumecornShape> SHAPE = EnumProperty.create(
            "shape",
            EndBlockProperties.LumecornShape.class
    );
    private static final VoxelShape SHAPE_BOTTOM = Block.box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape SHAPE_TOP = Block.box(6, 0, 6, 10, 8, 10);

    public LumecornBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return state.getValue(SHAPE) == EndBlockProperties.LumecornShape.LIGHT_TOP ? SHAPE_TOP : SHAPE_BOTTOM;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        EndBlockProperties.LumecornShape shape = state.getValue(SHAPE);
        if (shape == EndBlockProperties.LumecornShape.BOTTOM_BIG || shape == EndBlockProperties.LumecornShape.BOTTOM_SMALL) {
            return world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES);
        } else if (shape == EndBlockProperties.LumecornShape.LIGHT_TOP) {
            return world.getBlockState(pos.below()).is(this);
        } else {
            return world.getBlockState(pos.below()).is(this) && world.getBlockState(pos.above()).is(this);
        }
    }

    @Override
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
        if (!canSurvive(state, world, pos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return state;
        }
    }

    /**
     * The lower three segments yield 1-2 lumecorn seeds; every other segment has a one-in-two chance of a
     * single lumecorn rod.
     * <p>
     * This replaces a {@code getDrops} override, which bypassed the loot table entirely and drew its
     * randomness from {@code MHelper.RANDOM}. {@code MHelper.randRange} is inclusive at both ends, so it
     * maps onto {@link UniformGenerator#between} unchanged, and {@code nextBoolean()} onto a flat 0.5
     * chance. The override never consulted {@code survives_explosion}, so this table does not either.
     */
    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        final LootItemCondition.Builder seedBearing =
                shapeIs(provider, block, EndBlockProperties.LumecornShape.BOTTOM_BIG)
                        .or(shapeIs(provider, block, EndBlockProperties.LumecornShape.BOTTOM_SMALL))
                        .or(shapeIs(provider, block, EndBlockProperties.LumecornShape.MIDDLE));

        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(seedBearing)
                        .add(LootItem.lootTableItem(EndMushroomBlocks.LUMECORN_SEED)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(seedBearing.invert())
                        .when(LootItemRandomChanceCondition.randomChance(0.5F))
                        .add(LootItem.lootTableItem(EndResourceItems.LUMECORN_ROD)));
    }

    private static LootItemCondition.Builder shapeIs(
            LootLookupProvider provider,
            Block block,
            EndBlockProperties.LumecornShape shape
    ) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SHAPE, shape));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        EndBlockProperties.LumecornShape shape = blockState.getValue(SHAPE);
        if (shape == EndBlockProperties.LumecornShape.BOTTOM_BIG || shape == EndBlockProperties.LumecornShape.BOTTOM_SMALL || shape == EndBlockProperties.LumecornShape.MIDDLE) {
            return new ItemStack(EndMushroomBlocks.LUMECORN_SEED);
        }
        return new ItemStack(EndResourceItems.LUMECORN_ROD);
    }
}
