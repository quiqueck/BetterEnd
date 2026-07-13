package org.betterx.betterend.blocks;

import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.ui.ColorUtil;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BulbVineLanternColoredBlock extends BulbVineLanternBlock implements CustomColorProvider {
    public BulbVineLanternColoredBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public BlockColor getProvider() {
        return (state, world, pos, tintIndex) -> getColor();
    }

    private int getColor() {
        int color = BlocksHelper.getBlockColor(this);
        int b = (color & 255);
        int g = ((color >> 8) & 255);
        int r = ((color >> 16) & 255);
        float[] hsv = ColorUtil.RGBtoHSB(r, g, b, new float[3]);
        return ColorUtil.HSBtoRGB(hsv[0], hsv[1], hsv[1] > 0.2 ? 1 : hsv[2]);
    }

    @Override
    protected String getGlowTexture() {
        return "bulb_vine_lantern_overlay";
    }
}
