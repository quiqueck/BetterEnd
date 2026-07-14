package org.betterx.betterend.blocks;

import org.betterx.betterend.blocks.basis.EndPlantBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MurkweedBlock extends EndPlantBlock {
    public MurkweedBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + random.nextDouble() * 0.5 + 0.5;
        double z = pos.getZ() + random.nextDouble();
        double v = random.nextDouble() * 0.1;
        world.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFFFFFFFF), x, y, z, v, v, v);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier insideBlockEffectApplier) {
        if (entity instanceof LivingEntity && !((LivingEntity) entity).hasEffect(MobEffects.BLINDNESS)) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50));
        }
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }
}
