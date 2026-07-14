package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseDoublePlantBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.blocks.basis.EndPlantBlock;
import org.betterx.betterend.registry.EndBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class UmbrellaMossBlock extends EndPlantBlock {
    public UmbrellaMossBlock(BlockBehaviour.Properties props) {
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

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return world.isEmptyBlock(pos.above());
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int rot = world.random.nextInt(4);
        BlockState bs = EndBlocks.UMBRELLA_MOSS_TALL.defaultBlockState().setValue(BaseDoublePlantBlock.ROTATION, rot);
        BlocksHelper.setWithoutUpdate(world, pos, bs);
        BlocksHelper.setWithoutUpdate(world, pos.above(), bs.setValue(BaseDoublePlantBlock.TOP, true));
    }
}
