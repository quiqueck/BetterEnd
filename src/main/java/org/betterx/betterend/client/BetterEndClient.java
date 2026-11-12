package org.betterx.betterend.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.render.BetterEndSkyRenderer;
import org.betterx.betterend.events.ItemTooltipCallback;
import org.betterx.betterend.integration.Integrations;
import org.betterx.betterend.interfaces.MultiModelItem;
import org.betterx.betterend.item.CrystaliteArmor;
import org.betterx.betterend.registry.*;
import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

public class BetterEndClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EndBlockEntityRenders.register();
        EndScreens.register();
        EndParticles.register();
        EndEntitiesRenders.register();
        EndModelProviders.register();
        MultiModelItem.register();
        ClientOptions.init();
        registerTooltips();

        ResourceLocation checkFlowerId = new ResourceLocation("item/chorus_flower");
        ResourceLocation checkPlantId = new ResourceLocation("item/chorus_plant");
        ResourceLocation toLoadFlowerId = new ResourceLocation("betterend", "item/custom_chorus_flower");
        ResourceLocation toLoadPlantId = new ResourceLocation("betterend", "item/custom_chorus_plant");
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager -> (resourceId, context) -> {
            if (GeneratorOptions.changeChorusPlant()) {
                if (resourceId.equals(checkFlowerId)) {
                    return context.loadModel(toLoadFlowerId);
                } else if (resourceId.equals(checkPlantId)) {
                    return context.loadModel(toLoadPlantId);
                }
            }
            return null;
        });

        if (ClientOptions.isCustomSky()) {
            DimensionRenderingRegistry.registerSkyRenderer(Level.END, new BetterEndSkyRenderer());
        }
        if (BetterEnd.RUNS_TRINKETS) {
            org.betterx.betterend.integration.trinkets.ElytraClient.register();
        }

        //connected textures and material maps for BYG blocks, only needed (and only valid) when BYG is installed
        if (Integrations.BYG.modIsInstalled()) {
            FabricLoader.getInstance().getModContainer(BetterEnd.MOD_ID).ifPresent(mod ->
                    ResourceManagerHelper.registerBuiltinResourcePack(
                            BetterEnd.makeID("byg_extensions"),
                            mod,
                            ResourcePackActivationType.ALWAYS_ENABLED
                    )
            );
        }
    }

    public static void registerTooltips() {
        ItemTooltipCallback.EVENT.register((player, stack, context, lines) -> {
            if (stack.getItem() instanceof CrystaliteArmor) {
                boolean hasSet = false;
                if (player != null) {
                    hasSet = CrystaliteArmor.hasFullSet(player);
                }
                MutableComponent setDesc = Component.translatable("tooltip.armor.crystalite_set");

                setDesc.setStyle(Style.EMPTY.applyFormats(
                        hasSet ? ChatFormatting.BLUE : ChatFormatting.DARK_GRAY,
                        ChatFormatting.ITALIC
                ));
                lines.add(Component.empty());
                lines.add(setDesc);
            }
        });
    }
}
