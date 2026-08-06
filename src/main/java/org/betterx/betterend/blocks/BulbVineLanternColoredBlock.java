package org.betterx.betterend.blocks;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.ui.ColorUtil;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BulbVineLanternColoredBlock extends BulbVineLanternBlock {
    /**
     * The colour a dyed bulb lantern glows: the dye colour with its saturation pushed to full brightness, so the
     * bulb reads as lit rather than as painted plastic. Applied once at definition time - it used to be
     * recomputed on every colour query.
     */
    public static int boostLanternColor(int color) {
        final int b = color & 255;
        final int g = (color >> 8) & 255;
        final int r = (color >> 16) & 255;
        final float[] hsv = ColorUtil.RGBtoHSB(r, g, b, new float[3]);
        return ColorUtil.HSBtoRGB(hsv[0], hsv[1], hsv[1] > 0.2 ? 1 : hsv[2]);
    }

    public BulbVineLanternColoredBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }



    @Override
    protected String getGlowTexture() {
        return "bulb_vine_lantern_overlay";
    }
}
