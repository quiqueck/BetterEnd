package org.betterx.betterend.mixin.common;

import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 26.1 retarget: Level.getSharedSpawnPos() was removed - a dimension's spawn point is now carried
// by LevelData.RespawnData (pos+dimension+yaw+pitch) via ServerLevel.getRespawnData(). Mixin onto
// ServerLevel directly since the original guard only ever fired for ServerLevel instances anyway.
@Mixin(ServerLevel.class)
public class LevelMixin {

    @Inject(method = "getRespawnData", at = @At("HEAD"), cancellable = true)
    private void be_getRespawnData(CallbackInfoReturnable<LevelData.RespawnData> info) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (GeneratorOptions.changeSpawn() && self.dimension() == Level.END) {
            BlockPos pos = GeneratorOptions.getSpawn();
            info.setReturnValue(LevelData.RespawnData.of(self.dimension(), pos, 0.0F, 0.0F));
        }
    }
}
