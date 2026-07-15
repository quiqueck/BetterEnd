package org.betterx.betterend.blocks.basis;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A non-full-cube {@link Block}: doesn't suffocate, isn't a simple full block, and doesn't allow mob
 * spawning on top. Reproduces the former BCLib {@code BaseBlockNotFull} directly on vanilla {@link Block}
 * (non-occlusion itself is set via {@code Properties.noOcclusion()} at registration). Material families
 * (wood/stone/metal) that previously used the {@code BaseBlockNotFull.Wood/Stone/Metal} inner classes now
 * add the matching material trait at registration instead.
 */
public class EndBlockNotFull extends Block {
    public EndBlockNotFull(Properties settings) {
        super(settings);
    }

    public boolean canSuffocate(BlockState state, BlockGetter view, BlockPos pos) {
        return false;
    }

    public boolean isSimpleFullBlock(BlockState state, BlockGetter view, BlockPos pos) {
        return false;
    }

    public boolean allowsSpawning(BlockState state, BlockGetter view, BlockPos pos, EntityType<?> type) {
        return false;
    }
}
