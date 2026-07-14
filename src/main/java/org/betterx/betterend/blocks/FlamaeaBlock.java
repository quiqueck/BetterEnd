package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.wover.block.api.CustomBlockItemProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlamaeaBlock extends EndPlantBlock implements CustomBlockItemProvider {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public FlamaeaBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPE;
    }

    @Override
    public BlockItem getCustomBlockItem(ResourceLocation blockID, Item.Properties settings) {
        return new PlaceOnWaterBlockItem(this, settings);
    }
}
