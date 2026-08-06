package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndPlantBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.PentaShape;
import org.betterx.bclib.trait.block.SurvivesOnBlockTrait;
import org.betterx.bclib.blocks.BasePlantBlock;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class LanceleafBlock extends BasePlantBlock {

    public static final EnumProperty<PentaShape> SHAPE = BlockProperties.PENTA_SHAPE;
    public static final IntegerProperty ROTATION = BlockProperties.ROTATION;

    public LanceleafBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE, ROTATION);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        PentaShape shape = state.getValue(SHAPE);
        if (shape == PentaShape.TOP) {
            return world.getBlockState(pos.below()).is(this);
        } else if (shape == PentaShape.BOTTOM) {
            return SurvivesOnBlockTrait.survivesOn(this, world.getBlockState(pos.below()))
                    && world.getBlockState(pos.above()).is(this);
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

    private static LootItemBlockStatePropertyCondition.Builder isBottom(Block block) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SHAPE, PentaShape.BOTTOM));
    }

    /**
     * The bottom segment always drops one seed; every other segment drops one seed with a 50% chance -
     * replacing the block class's former {@code getDrops} override with a data-driven loot table.
     */
    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        return LootTable
                .lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(isBottom(block))
                        .add(LootItem.lootTableItem(EndPlantBlocks.LANCELEAF_SEED)))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(InvertedLootItemCondition.invert(isBottom(block)))
                        .when(LootItemRandomChanceCondition.randomChance(0.5F))
                        .add(LootItem.lootTableItem(EndPlantBlocks.LANCELEAF_SEED)));
    }
}
