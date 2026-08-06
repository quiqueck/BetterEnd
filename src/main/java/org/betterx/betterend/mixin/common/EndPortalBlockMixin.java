package org.betterx.betterend.mixin.common;

import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.phys.Vec3;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    /**
     * 26.2 removed the {@code BlockPos#getBottomCenter()} instance method and replaced every call with the
     * static {@code Vec3.atBottomCenterOf(Vec3i)}. Verified with
     * {@code javap -p -c net.minecraft.world.level.block.EndPortalBlock}:
     * <pre>
     * 26.1.2: 83: invokevirtual Method net/minecraft/core/BlockPos.getBottomCenter:()Lnet/minecraft/world/phys/Vec3;
     *        228: invokevirtual Method net/minecraft/core/BlockPos.getBottomCenter:()Lnet/minecraft/world/phys/Vec3;
     * 26.2:    83: invokestatic  Method net/minecraft/world/phys/Vec3.atBottomCenterOf:(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;
     *         228: invokestatic  Method net/minecraft/world/phys/Vec3.atBottomCenterOf:(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;
     * </pre>
     * {@code javap -p net.minecraft.core.BlockPos} in 26.2 lists no {@code getBottomCenter} (and no
     * {@code getCenter}) at all, so the old target matched zero instructions. This build has no refmap and no
     * mixin annotation processor, so it compiled anyway and would have thrown at mixin-apply time.
     * <p>
     * Both call sites are still inside {@code getPortalDestination} at the same two offsets as 26.1.2, so both
     * are wrapped exactly as before. Only the one at 83 can ever see {@code END_SPAWN_POINT} (the other
     * operates on {@code respawnData().pos()} routed through {@code Entity#adjustSpawnLocation}); the identity
     * guard makes the second a no-op, which is the 26.1 behaviour too. Because the call is now static, the
     * handler's first parameter is the call's {@code Vec3i} argument rather than a {@code BlockPos} receiver.
     */
    @WrapOperation(
            method = "getPortalDestination",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;atBottomCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;")
    )
    Vec3 be_changeSpawnInEnd(Vec3i pos, Operation<Vec3> original) {
        if (GeneratorOptions.changeSpawn() && pos == ServerLevel.END_SPAWN_POINT) {
            BlockPos spawn = GeneratorOptions.getSpawn();
            return original.call(spawn);
        }

        return original.call(pos);
    }
}
