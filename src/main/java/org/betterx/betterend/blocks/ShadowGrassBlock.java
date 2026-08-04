package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseTerrainBlock;
import org.betterx.betterend.registry.EndParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ShadowGrassBlock extends BaseTerrainBlock {
    public ShadowGrassBlock(BlockBehaviour.Properties properties) {
        super(properties, Blocks.END_STONE);
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (random.nextInt(32) == 0) {
            world.addParticle(
                    EndParticles.BLACK_SPORE,
                    (double) pos.getX() + random.nextDouble(),
                    (double) pos.getY() + 1.1D,
                    (double) pos.getZ() + random.nextDouble(),
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }
}
