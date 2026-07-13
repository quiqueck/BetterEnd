package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.behaviours.interfaces.BehaviourShearablePlant;
import org.betterx.bclib.interfaces.SurvivesOnWater;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.wover.block.api.CustomBlockItemProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.google.common.collect.Lists;

import java.util.List;

public class FlamaeaBlock extends EndPlantBlock implements CustomBlockItemProvider, BehaviourShearablePlant, SurvivesOnWater {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public FlamaeaBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Lists.newArrayList(new ItemStack(this));
    }

    @Override
    public BlockItem getCustomBlockItem(ResourceLocation blockID, Item.Properties settings) {
        return new PlaceOnWaterBlockItem(this, settings);
    }

    @Override
    public boolean isTerrain(BlockState state) {
        return SurvivesOnWater.super.isTerrain(state);
    }


//    @Override
//    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
//        return SurvivesOnWater.super.canSurvive(state, level, pos);
//    }
}
