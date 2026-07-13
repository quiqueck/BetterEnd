package org.betterx.betterend.blocks;

import org.betterx.bclib.behaviours.BehaviourBuilders;
import org.betterx.bclib.blocks.BaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;

public class HydraluxPetalBlock extends BaseBlock.Wood {
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
