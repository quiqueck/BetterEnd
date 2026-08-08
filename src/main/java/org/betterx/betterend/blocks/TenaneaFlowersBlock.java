package org.betterx.betterend.blocks;

import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class TenaneaFlowersBlock extends BaseVineBlock {
    public static final Vec3i[] COLORS;

    public TenaneaFlowersBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);
        if (random.nextInt(32) == 0) {
            double x = (double) pos.getX() + random.nextGaussian() + 0.5;
            double z = (double) pos.getZ() + random.nextGaussian() + 0.5;
            double y = (double) pos.getY() + random.nextDouble();
            world.addParticle(EndParticles.TENANEA_PETAL, x, y, z, 0, 0, 0);
        }
    }

    static {
        COLORS = new Vec3i[]{
                new Vec3i(250, 111, 222),
                new Vec3i(167, 89, 255),
                new Vec3i(120, 207, 239),
                new Vec3i(255, 87, 182)
        };
    }
}
