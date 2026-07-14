package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndPlantBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class GlowingMossBlock extends EndPlantBlock {
    public GlowingMossBlock(BlockBehaviour.Properties props) {
        super(props);
    }


    @Environment(EnvType.CLIENT)
    public boolean hasEmissiveLighting(BlockGetter world, BlockPos pos) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockGetter world, BlockPos pos) {
        return 1F;
    }
}
