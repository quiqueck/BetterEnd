package org.betterx.betterend.mixin.common;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.GeneratorOptions;
import org.betterx.wover.state.api.WorldConfig;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.SpikeFeature.EndSpike;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndSpike.class)
public class EndSpikeMixin {
    @Final
    @Shadow
    private int height;

    @Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
    private void be_getSpikeHeight(CallbackInfoReturnable<Integer> info) {
        if (!GeneratorOptions.isDirectSpikeHeight()) {
            int x = getCenterX();
            int z = getCenterZ();
            String pillarID = String.format("%d_%d", x, z);
            CompoundTag pillar = WorldConfig.getCompoundTag(BetterEnd.C, "pillars");
            int minY = pillar.getInt(pillarID).orElse(65);
            int maxY = minY + height - 54;
            info.setReturnValue(maxY);
        }
    }

    @Shadow
    public int getCenterX() {
        return 0;
    }

    @Shadow
    public int getCenterZ() {
        return 0;
    }
}
