package org.betterx.betterend.blocks.entities;

import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.registry.EndBlockEntities;
import org.betterx.betterend.registry.EndItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jetbrains.annotations.NotNull;

public class PedestalBlockEntity extends BlockEntity implements Container {
    private ItemStack activeItem = ItemStack.EMPTY;

    public PedestalBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(EndBlockEntities.PEDESTAL, blockPos, blockState);
    }

    public PedestalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return activeItem.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return activeItem;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return removeItemNoUpdate(slot);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isEmpty();
    }

    @Override
    public void clearContent() {
        activeItem = ItemStack.EMPTY;
        setChanged();
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stored = activeItem;
        clearContent();
        return stored;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        activeItem = stack.split(1);
        setChanged();
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide()) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof PedestalBlock) {
                state = state.setValue(PedestalBlock.HAS_ITEM, !isEmpty());
                state = state.setValue(PedestalBlock.HAS_LIGHT, activeItem.getItem() == EndItems.ETERNAL_CRYSTAL);
            }
            level.setBlockAndUpdate(worldPosition, state);
            level.blockEntityChanged(worldPosition);
        }
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        activeItem = input.read("active_item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (!activeItem.isEmpty()) {
            output.store("active_item", ItemStack.CODEC, activeItem);
        }
        super.saveAdditional(output);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }
}
