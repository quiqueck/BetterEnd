package org.betterx.betterend.blocks;


import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.blocks.UpDownPlantBlock;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class GlowingPillarRootsBlock extends UpDownPlantBlock {
    public static final EnumProperty<TripleShape> SHAPE = BlockProperties.TRIPLE_SHAPE;

    public GlowingPillarRootsBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SHAPE);
    }

    @Override
    protected boolean isTerrain(BlockState state) {
        return state.is(EndTerrainBlocks.AMBER_MOSS);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean includeData) {
        return new ItemStack(EndMushroomBlocks.GLOWING_PILLAR_SEED);
    }
}
