package org.betterx.betterend.blocks.entities;

import org.betterx.betterend.registry.EndBlockEntities;
import org.betterx.betterend.rituals.EternalRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EternalPedestalEntity extends PedestalBlockEntity {
    private EternalRitual linkedRitual;

    public EternalPedestalEntity(BlockPos blockPos, BlockState blockState) {
        super(EndBlockEntities.ETERNAL_PEDESTAL, blockPos, blockState);
    }

    public boolean hasRitual() {
        return linkedRitual != null;
    }

    public void linkRitual(EternalRitual ritual) {
        this.linkedRitual = ritual;
    }

    public EternalRitual getRitual() {
        return linkedRitual;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (hasRitual()) {
            linkedRitual.setWorld(level);
        }
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
        input.read("ritual", CompoundTag.CODEC).ifPresent(ritualTag -> {
            linkedRitual = new EternalRitual(level);
            linkedRitual.fromTag(ritualTag);
        });
        super.loadAdditional(input);
    }
}
