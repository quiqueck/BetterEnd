package org.betterx.betterend.mixin.common;

import net.minecraft.world.level.levelgen.NoiseChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NoiseChunk.class)
public interface NoiseChunkAccessor {
    // 26.1: NoiseChunk no longer keeps a `noiseSettings` field - NoiseChunkMixin captures it
    // directly from the constructor parameter instead (see be_noiseSettings).

    @Accessor("cellCountXZ")
    int bnv_getCellCountXZ();

    @Accessor("cellCountY")
    int bnv_getCellCountY();

    @Accessor("firstCellZ")
    int bnv_getFirstCellZ();

    @Accessor("cellNoiseMinY")
    int bnv_getCellNoiseMinY();
}
