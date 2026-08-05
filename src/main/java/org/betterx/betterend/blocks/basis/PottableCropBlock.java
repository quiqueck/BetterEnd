package org.betterx.betterend.blocks.basis;

import org.betterx.bclib.util.BlocksHelper;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Age-tracking berry/root crop, grown on a fixed set of {@code terrain} blocks. Reproduces the former BCLib
 * {@code BaseCropBlock} on top of vanilla {@link BushBlock}. Survival ground is the ctor-supplied terrain
 * list (an intrinsic {@code mayPlaceOn}, since it's per-instance config, not a shared trait); the crop loot
 * (a raw drop plus a chance of seeds at full age) is built by {@link #buildLoot} and attached as a
 * {@code LOOT_TABLE} trait at registration.
 * <p>
 * Passive growth ({@link #performBonemeal} on a 1-in-8 chance) runs from {@link #randomTick}, so a block
 * registered with this class needs a random-ticking {@code Properties} to ever grow on its own - attach
 * {@code org.betterx.bclib.trait.block.RandomTicksTrait} at the registration site.
 */
public class PottableCropBlock extends BushBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 14, 14);

    private final List<Block> terrain;
    private final Item drop;

    public PottableCropBlock(Properties props, Item drop, Block... terrain) {
        super(props);
        this.drop = drop;
        this.terrain = List.of(terrain);
        this.registerDefaultState(defaultBlockState().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter view, BlockPos pos) {
        return terrain.contains(state.getBlock());
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < 3) {
            BlocksHelper.setWithUpdate(level, pos, state.setValue(AGE, age + 1));
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (isBonemealSuccess(world, random, pos, state) && random.nextInt(8) == 0) {
            performBonemeal(world, random, pos, state);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    public LootTable.Builder buildLoot(LootLookupProvider provider) {
        return provider.dropPlant(
                this,
                drop, UniformGenerator.between(1, 2),
                this, UniformGenerator.between(1, 3),
                0.571f, 3,
                AGE, 3
        );
    }
}
