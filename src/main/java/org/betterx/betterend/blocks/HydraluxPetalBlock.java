package org.betterx.betterend.blocks;

import net.minecraft.world.level.block.Block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class HydraluxPetalBlock extends Block {
    public HydraluxPetalBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void fallOn(
            @NotNull Level level,
            @NotNull BlockState blockState,
            @NotNull BlockPos blockPos,
            @NotNull Entity entity,
            double f
    ) {
    }
}
