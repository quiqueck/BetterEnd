package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.bclib.blocks.BasePlantBlock;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EndLotusFlowerBlock extends BasePlantBlock {
    private static final VoxelShape SHAPE_OUTLINE = Block.box(2, 0, 2, 14, 14, 14);
    private static final VoxelShape SHAPE_COLLISION = Block.box(0, 0, 0, 16, 2, 16);

    public EndLotusFlowerBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_OUTLINE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE_COLLISION;
    }

    /**
     * Drops a random 1-2 lotus seeds (there is no block item for this block) - replacing the block class's
     * former {@code getDrops} override with a data-driven loot table.
     */
    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        return LootTable
                .lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(EndWaterPlantBlocks.END_LOTUS_SEED)
                                     .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        return new ItemStack(EndWaterPlantBlocks.END_LOTUS_SEED);
    }
}
