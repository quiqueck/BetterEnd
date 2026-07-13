package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourPlant;
import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.block.api.BlockProperties.PentaShape;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.betterend.interfaces.survives.SurvivesOnAmberMoss;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.Collections;
import java.util.List;

public class LanceleafBlock extends EndPlantBlock implements SurvivesOnAmberMoss, BehaviourPlant {

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
            return canSurviveOnTop(world, pos) && world.getBlockState(pos.above()).is(this);
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

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(SHAPE) == PentaShape.BOTTOM) {
            return Collections.singletonList(new ItemStack(EndBlocks.LANCELEAF_SEED));
        }
        return MHelper.RANDOM.nextBoolean() ? Collections.emptyList() : Collections.singletonList(new ItemStack(
                EndBlocks.LANCELEAF_SEED));
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnAmberMoss.super.isTerrain(state);
    }
}
