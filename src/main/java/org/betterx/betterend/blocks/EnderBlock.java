package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EnderBlock extends BaseBlock.Stone {

    public EnderBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Environment(EnvType.CLIENT)
    public int getColor(BlockState state, BlockGetter world, BlockPos pos) {
        return 0xFF005548;
    }
}
