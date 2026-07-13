package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.TripleShape;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class BulbVineBlock extends BaseVineBlock {
    public BulbVineBlock(BlockBehaviour.Properties props) {
        super(props, 15, true);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        boolean canPlace = super.canSurvive(state, world, pos);
        return (state.is(this) && state.getValue(SHAPE) == TripleShape.BOTTOM)
                ? canPlace
                : canPlace && world.getBlockState(
                        pos.below()).is(this);
    }

//    @Override
//    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
//        if (state.getValue(SHAPE) == TripleShape.BOTTOM) {
//            return Lists.newArrayList(new ItemStack(EndItems.GLOWING_BULB));
//        } else if (MHelper.RANDOM.nextInt(8) == 0) {
//            return Lists.newArrayList(new ItemStack(EndBlocks.BULB_VINE_SEED));
//        } else {
//            return Lists.newArrayList();
//        }
//    }

    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        var bottom = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder
                        .properties()
                        .hasProperty(SHAPE, BlockProperties.TripleShape.BOTTOM));

        return LootTable
                .lootTable()
                .withPool(
                        LootPool
                                .lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(EndItems.GLOWING_BULB.asItem())
                                             .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                )
                                .when(bottom)
                )
                .withPool(
                        LootPool
                                .lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(EndBlocks.BULB_VINE_SEED.asItem())
                                             .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                             .when(ExplosionCondition.survivesExplosion())
                                             .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                                     provider.fortune(),
                                                     LootLookupProvider.VANILLA_LEAVES_SAPLING_CHANCES
                                             ))
                                )
                                .when(bottom.invert())
                );
    }
}
