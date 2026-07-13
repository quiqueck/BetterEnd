package org.betterx.betterend.registry;

import org.betterx.betterend.client.render.FlowerPotItemRenderer;
import org.betterx.betterend.client.render.PedestalItemRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class EndBlockEntityRenders {
    public static void register() {
        BlockEntityRendererRegistry.register(EndBlockEntities.PEDESTAL, PedestalItemRenderer::new);
        BlockEntityRendererRegistry.register(EndBlockEntities.ETERNAL_PEDESTAL, PedestalItemRenderer::new);
        BlockEntityRendererRegistry.register(EndBlockEntities.INFUSION_PEDESTAL, PedestalItemRenderer::new);
        BlockEntityRendererRegistry.register(EndBlockEntities.FLOWER_POT, FlowerPotItemRenderer::new);
    }
}
