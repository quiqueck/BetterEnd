package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndCropBlocks;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CavePumpkinBlock extends Block {
    public static final BooleanProperty SMALL = BlockProperties.SMALL;
    private static final VoxelShape SHAPE_SMALL;
    private static final VoxelShape SHAPE_BIG;

    public CavePumpkinBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(defaultBlockState().setValue(SMALL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SMALL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(SMALL) ? SHAPE_SMALL : SHAPE_BIG;
    }

    /**
     * A small (unripe) cave pumpkin yields a seed; a full-grown one yields the pumpkin itself.
     * <p>
     * This replaces a {@code getDrops} override, which bypassed the loot table entirely - the generated
     * table said "always drop self", so the two disagreed and only the override ran. The override never
     * consulted {@code survives_explosion}, so this table does not either.
     */
    public static LootTable.Builder buildLoot(Block block, LootLookupProvider provider) {
        final LootItemCondition.Builder small = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SMALL, true));

        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(small)
                        .add(LootItem.lootTableItem(EndCropBlocks.CAVE_PUMPKIN_SEED)))
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(small.invert())
                        .add(LootItem.lootTableItem(block)));
    }

    static {
        VoxelShape lantern = Block.box(1, 0, 1, 15, 13, 15);
        VoxelShape cap = Block.box(0, 12, 0, 16, 15, 16);
        VoxelShape top = Block.box(5, 15, 5, 11, 16, 11);
        SHAPE_BIG = Shapes.or(lantern, cap, top);

        lantern = Block.box(5, 7, 5, 11, 13, 11);
        cap = Block.box(4, 12, 4, 12, 15, 12);
        top = Block.box(6, 15, 6, 10, 16, 10);
        SHAPE_SMALL = Shapes.or(lantern, cap, top);
    }
}
