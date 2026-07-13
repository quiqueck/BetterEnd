package org.betterx.betterend.blocks.entities;

import org.betterx.betterend.registry.EndBlockEntities;
import org.betterx.betterend.rituals.InfusionRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class InfusionPedestalEntity extends PedestalBlockEntity {
    private InfusionRitual linkedRitual;

    public InfusionPedestalEntity(BlockPos blockPos, BlockState blockState) {
        super(EndBlockEntities.INFUSION_PEDESTAL, blockPos, blockState);
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        if (hasRitual()) {
            linkedRitual.setLocation(world, this.getBlockPos());
        } else {
            linkRitual(this, world, this.getBlockPos());
        }
    }

    public InfusionRitual linkRitual(InfusionPedestalEntity pedestal, Level world, BlockPos pos) {
        linkedRitual = new InfusionRitual(pedestal, world, pos);
        linkedRitual.configure();
        return linkedRitual;
    }

    public InfusionRitual getRitual() {
        return linkedRitual;
    }

    public boolean hasRitual() {
        return linkedRitual != null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (hasRitual()) {
            output.store("ritual", CompoundTag.CODEC, linkedRitual.toTag(new CompoundTag()));
        }
        super.saveAdditional(output);
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("ritual", CompoundTag.CODEC).ifPresent(ritualTag -> {
            if (!hasRitual()) {
                linkedRitual = new InfusionRitual(this, level, worldPosition);
            }
            linkedRitual.fromTag(ritualTag);
            linkedRitual.configure();
        });
    }

    public static <T extends BlockEntity> void tickEntity(
            Level level,
            BlockPos blockPos,
            BlockState blockState,
            T uncastedEntity
    ) {
        if (uncastedEntity instanceof InfusionPedestalEntity) {
            InfusionPedestalEntity blockEntity = (InfusionPedestalEntity) uncastedEntity;
            if (blockEntity.hasRitual()) {
                blockEntity.linkedRitual.tick();
            }
            //PedestalBlockEntity.tick(level, blockPos, blockState, blockEntity);
        }
    }
}
