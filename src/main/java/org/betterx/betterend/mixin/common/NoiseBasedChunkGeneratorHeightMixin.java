package org.betterx.betterend.mixin.common;

import org.betterx.betterend.interfaces.BETargetChecker;
import org.betterx.betterend.world.generator.TerrainGenerator;

import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * BetterEnd builds the End's island terrain in {@link TerrainGenerator#fillSlice} (via
 * {@link NoiseChunkMixin}), which bypasses the vanilla density function. The vanilla
 * {@code getBaseHeight}/{@code getBaseColumn} (and therefore {@code getFirstOccupiedHeight}) still
 * evaluate that now-unused vanilla density, so structure placement sees a central-island-plus-void
 * world and places structures at the void floor (y = 0) instead of on the real islands.
 * <p>
 * This mixin redirects those two height queries to {@link TerrainGenerator#getSurfaceHeight} - the
 * exact same island density {@code fillSlice} uses - but ONLY when the End target flag is set on the
 * {@link NoiseGeneratorSettings}. That is the very flag {@link NoiseChunkMixin} uses to decide
 * whether it replaces the terrain, so the override and the terrain always agree, and the vanilla End
 * / other dimensions are left untouched.
 */
@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorHeightMixin {
    @Shadow
    @Final
    private Holder<NoiseGeneratorSettings> settings;

    @Inject(method = "getBaseHeight", at = @At("HEAD"), cancellable = true)
    private void be_getBaseHeight(
            int x,
            int z,
            Heightmap.Types type,
            LevelHeightAccessor level,
            RandomState random,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (!be_isEndTarget()) return;
        cir.setReturnValue(be_islandSurfaceY(x, z));
    }

    @Inject(method = "getBaseColumn", at = @At("HEAD"), cancellable = true)
    private void be_getBaseColumn(
            int x,
            int z,
            LevelHeightAccessor level,
            RandomState random,
            CallbackInfoReturnable<NoiseColumn> cir
    ) {
        if (!be_isEndTarget()) return;

        final int surfaceY = be_islandSurfaceY(x, z);
        final int minY = level.getMinY();
        final int height = level.getHeight();
        final BlockState solid = settings.value().defaultBlock();
        final BlockState air = Blocks.AIR.defaultBlockState();

        final BlockState[] column = new BlockState[height];
        for (int i = 0; i < height; i++) {
            column[i] = (minY + i) <= surfaceY ? solid : air;
        }
        cir.setReturnValue(new NoiseColumn(minY, column));
    }

    @Unique
    private boolean be_isEndTarget() {
        return settings.isBound() && BETargetChecker.class.cast(settings.value()).be_isTarget();
    }

    @Unique
    private int be_islandSurfaceY(int x, int z) {
        final NoiseSettings ns = settings.value().noiseSettings();
        return TerrainGenerator.getSurfaceHeight(
                x,
                z,
                ns.getCellWidth(),
                ns.getCellHeight(),
                ns.height(),
                ns.minY()
        );
    }
}
