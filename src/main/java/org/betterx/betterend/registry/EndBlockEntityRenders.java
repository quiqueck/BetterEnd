package org.betterx.betterend.registry;

import org.betterx.betterend.blocks.entities.PedestalBlockEntity;
import org.betterx.betterend.client.render.FlowerPotItemRenderer;
import org.betterx.betterend.client.render.PedestalItemRenderer;
import org.betterx.betterend.client.render.state.PedestalRenderState;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class EndBlockEntityRenders {
    public static void register() {
        // Pre-typing the provider (rather than inlining PedestalItemRenderer::new at each call)
        // sidesteps a javac inference limitation: register()'s wildcard bound (`? super E`) combined
        // with a bare constructor reference fails to infer E for the EternalPedestalEntity/
        // InfusionPedestalEntity subtypes ("incompatible equality constraints").
        BlockEntityRendererProvider<PedestalBlockEntity, PedestalRenderState> pedestalRenderer = PedestalItemRenderer::new;
        BlockEntityRendererRegistry.register(EndBlockEntities.PEDESTAL, pedestalRenderer);
        BlockEntityRendererRegistry.register(EndBlockEntities.ETERNAL_PEDESTAL, pedestalRenderer);
        BlockEntityRendererRegistry.register(EndBlockEntities.INFUSION_PEDESTAL, pedestalRenderer);
        BlockEntityRendererRegistry.register(EndBlockEntities.FLOWER_POT, FlowerPotItemRenderer::new);
    }
}
