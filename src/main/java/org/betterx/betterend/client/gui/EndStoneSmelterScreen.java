package org.betterx.betterend.client.gui;

import org.betterx.betterend.BetterEnd;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndStoneSmelterScreen extends AbstractContainerScreen<EndStoneSmelterMenu> {
    private final static Identifier BACKGROUND_TEXTURE = BetterEnd.C.mk("textures/gui/smelter_gui.png");

    public EndStoneSmelterScreen(EndStoneSmelterMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    public void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                leftPos,
                topPos,
                0,
                0,
                imageWidth,
                imageHeight,
                256,
                256
        );
        int progress;
        if (menu.isBurning()) {
            progress = menu.getFuelProgress();
            guiGraphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    BACKGROUND_TEXTURE,
                    leftPos + 56,
                    topPos + 36 + 12 - progress,
                    176,
                    12 - progress,
                    14,
                    progress + 1,
                    256,
                    256
            );
        }
        progress = menu.getSmeltProgress();
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                leftPos + 92,
                topPos + 34,
                176,
                14,
                progress + 1,
                16,
                256,
                256
        );

        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }
}
