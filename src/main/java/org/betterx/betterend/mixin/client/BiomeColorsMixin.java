package org.betterx.betterend.mixin.client;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.ui.ColorUtil;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {
    private static final int POISON_COLOR = ColorUtil.color(92, 160, 78);
    private static final int STREAM_COLOR = ColorUtil.color(105, 213, 244);
    private static final Point[] OFFSETS;

    @Inject(method = "getAverageWaterColor", at = @At("RETURN"), cancellable = true)
    private static void be_getWaterColor(BlockAndTintGetter world, BlockPos pos, CallbackInfoReturnable<Integer> info) {
        if (Configs.CLIENT_CONFIG.sulfurWaterColor.get()) {
            BlockAndTintGetter view = world;
            MutableBlockPos mut = new MutableBlockPos();
            mut.setY(pos.getY());
            for (int i = 0; i < OFFSETS.length; i++) {
                mut.setX(pos.getX() + OFFSETS[i].x);
                mut.setZ(pos.getZ() + OFFSETS[i].y);
                if ((view.getBlockState(mut).is(EndBlocks.BRIMSTONE))) {
                    info.setReturnValue(i < 4 ? POISON_COLOR : STREAM_COLOR);
                    return;
                }
            }
        }
    }

    static {
        int index = 0;
        OFFSETS = new Point[20];
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                if ((x != 0 || z != 0) && (Math.abs(x) != 2 || Math.abs(z) != 2)) {
                    OFFSETS[index++] = new Point(x, z);
                }
            }
        }
        Arrays.sort(OFFSETS, Comparator.comparingInt(pos -> MHelper.sqr(pos.x) + MHelper.sqr(pos.y)));
    }
}
