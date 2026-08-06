package org.betterx.betterend.blocks.entities;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.blocks.HydrothermalVentBlock;
import org.betterx.betterend.registry.EndBlockEntities;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.util.GlobalState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BlockEntityHydrothermalVent extends BlockEntity {
    private final static Vec3 POSITIVE_Y = new Vec3(0.0f, 1.0f, 0.0f);

    public BlockEntityHydrothermalVent(BlockPos blockPos, BlockState blockState) {
        super(EndBlockEntities.HYDROTHERMAL_VENT, blockPos, blockState);
    }

    public static <T extends BlockEntity> void tick(
            Level level,
            BlockPos worldPosition,
            BlockState state,
            T uncastedEntity
    ) {
        if (level != null && uncastedEntity instanceof BlockEntityHydrothermalVent && state.is(EndStoneBlocks.HYDROTHERMAL_VENT)) {
            BlockEntityHydrothermalVent blockEntity = (BlockEntityHydrothermalVent) uncastedEntity;
            if (level.isClientSide()) {
                clientTick(level, worldPosition, state, blockEntity);
            }
            //else {
            serverTick(level, worldPosition, state, blockEntity);
            //}
        }
    }

    private static void clientTick(
            Level level,
            BlockPos worldPosition,
            BlockState state,
            BlockEntityHydrothermalVent blockEntity
    ) {
        boolean active = state.getValue(HydrothermalVentBlock.ACTIVATED);
        if (active && level.getRandom().nextInt(20) == 0 && state.getValue(HydrothermalVentBlock.WATERLOGGED)) {
            double x = worldPosition.getX() + level.getRandom().nextDouble();
            double y = worldPosition.getY() + 0.9 + level.getRandom().nextDouble() * 0.3;
            double z = worldPosition.getZ() + level.getRandom().nextDouble();
            level.addParticle(EndParticles.GEYSER_PARTICLE, x, y, z, 0, 0, 0);
        }
    }

    private static void serverTick(
            Level level,
            BlockPos worldPosition,
            BlockState state,
            BlockEntityHydrothermalVent blockEntity
    ) {
        final MutableBlockPos POS = GlobalState.stateForThread().POS;
        boolean active = state.getValue(HydrothermalVentBlock.ACTIVATED);
        POS.set(worldPosition).move(Direction.UP);
        int height = active ? 85 : 25;
        // 26.2 removed BlockPos#getCenter(); Vec3.atCenterOf(Vec3i) is the surviving spelling.
        AABB box = new AABB(
                Vec3.atCenterOf(POS.offset(-1, 0, -1)),
                Vec3.atCenterOf(POS.offset(1, height, 1))
        );
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
        if (entities.size() > 0) {
            while (POS.getY() < box.maxY) {
                BlockState blockState = level.getBlockState(POS);
                if (blockState.isSolidRender()) break;
                if (blockState.isAir()) {
                    double mult = active ? 3.0 : 5.0;
                    float force = (float) ((1.0 - (POS.getY() / box.maxY)) / mult);
                    entities.stream()
                            .filter(entity -> (int) entity.getY() == POS.getY() && blockEntity.hasElytra(entity) && entity
                                    .isFallFlying())
                            .forEach(entity -> entity.moveRelative(force, POSITIVE_Y));
                }
                POS.move(Direction.UP);
            }
        }
    }

    private boolean hasElytra(LivingEntity entity) {
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        return LivingEntity.canGlideUsing(chest, EquipmentSlot.CHEST);
    }
}
