package org.betterx.betterend.mixin.common;

import org.betterx.betterend.registry.EndEnchantments;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.Nullable;

// Resonance (hammer enchantment) area-mines a cube around the targeted block. There is no vanilla hook
// for "break more than one block", so this reimplements ServerPlayerGameMode#destroyBlock's body for
// every block in the cube, sharing a single pre-swing copy of the tool stack across all of them so that
// Fortune/Silk Touch/etc. carry over to every block, while ItemStack#mineBlock (durability + stats) is
// only ever invoked once, for the targeted block - exactly as if a single block had been mined.
@Mixin(value = ServerPlayerGameMode.class, priority = 900)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;
    @Shadow
    @Final
    protected ServerPlayer player;
    @Shadow
    private GameType gameModeForPlayer;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void be_resonanceDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        final ItemStack mainHand = this.player.getMainHandItem();
        if (!mainHand.is(CommonItemTags.HAMMERS)) return;
        if (this.player.isShiftKeyDown()) return;
        final int resonanceLevel = EnchantmentUtils.getItemEnchantmentLevel(this.level, EndEnchantments.RESONANCE.key(), mainHand);
        if (resonanceLevel <= 0) return;

        final BlockState primaryState = this.level.getBlockState(pos);
        if (!mainHand.canDestroyBlock(primaryState, this.level, pos, this.player)) {
            info.setReturnValue(false);
            return;
        }

        final ItemStack destroyedWith = mainHand.copy();
        final BlockState primaryAdjustedState = be_breakOne(pos, primaryState, destroyedWith, true);
        if (primaryAdjustedState == null) {
            info.setReturnValue(false);
            return;
        }

        // The face the player is looking at stays centered on the two side axes, but the cube is shifted
        // to extend purely away from the player along the view axis - a 3x3x3/5x5x5 tunnel bored forward
        // from the targeted block, rather than a cube that would also eat into the wall behind the player.
        final int radius = resonanceLevel;
        final Direction facing = this.player.getNearestViewDirection();
        final int[] xRange = be_axisRange(facing.getStepX(), radius);
        final int[] yRange = be_axisRange(facing.getStepY(), radius);
        final int[] zRange = be_axisRange(facing.getStepZ(), radius);

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = xRange[0]; dx <= xRange[1]; dx++) {
            for (int dy = yRange[0]; dy <= yRange[1]; dy++) {
                for (int dz = zRange[0]; dz <= zRange[1]; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    final BlockPos extraPos = cursor.immutable();
                    be_breakOne(extraPos, this.level.getBlockState(extraPos), destroyedWith, false);
                }
            }
        }

        if (!this.player.preventsBlockDrops()) {
            mainHand.mineBlock(this.level, primaryAdjustedState, pos, this.player);
        }

        info.setReturnValue(true);
    }

    /**
     * @param step   the view direction's step along this axis: 0 for the two side axes (centered range),
     *               or ±1 for the axis the player is looking along (range extends only away from the player)
     * @param radius Resonance's level (1 for a 3x3x3 cube, 2 for 5x5x5)
     * @return {@code [min, max]} offsets to scan along this axis
     */
    private static int[] be_axisRange(int step, int radius) {
        if (step > 0) return new int[]{0, 2 * radius};
        if (step < 0) return new int[]{-2 * radius, 0};
        return new int[]{-radius, radius};
    }

    /**
     * Mirrors {@code ServerPlayerGameMode#destroyBlock}'s body for a single block, minus the tool-damage
     * call, which the caller applies once for the whole swing.
     *
     * @return the adjusted block state if the block was removed, or {@code null} if it was skipped/denied
     */
    @Nullable
    private BlockState be_breakOne(BlockPos pos, BlockState state, ItemStack destroyedWith, boolean isPrimary) {
        if (state.isAir()) return null;

        if (!isPrimary) {
            if (state.getDestroySpeed(this.level, pos) < 0.0F) return null;
            if (this.level.getServer().isUnderSpawnProtection(this.level, pos, this.player)) return null;
            if (!destroyedWith.canDestroyBlock(state, this.level, pos, this.player)) return null;
        }

        final BlockEntity blockEntity = this.level.getBlockEntity(pos);
        final Block block = state.getBlock();
        if (block instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) {
            if (isPrimary) this.level.sendBlockUpdated(pos, state, state, 3);
            return null;
        }
        if (this.player.blockActionRestricted(this.level, pos, this.gameModeForPlayer)) return null;

        final BlockState adjustedState = block.playerWillDestroy(this.level, pos, state, this.player);
        final boolean changed = this.level.removeBlock(pos, false);
        if (changed) block.destroy(this.level, pos, adjustedState);

        if (!this.player.preventsBlockDrops()) {
            final boolean canDrop = this.player.hasCorrectToolForDrops(adjustedState);
            if (changed && canDrop) {
                block.playerDestroy(this.level, this.player, pos, adjustedState, blockEntity, destroyedWith);
            }
        }

        return adjustedState;
    }
}
