package org.betterx.betterend.client;

import org.betterx.bclib.integration.modmenu.ModMenu;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.render.BetterEndSkyRenderer;
import org.betterx.betterend.client.render.EndFlowerPotModels;
import org.betterx.betterend.config.Configs;
import org.betterx.betterend.config.screen.ConfigScreen;
import org.betterx.betterend.events.ItemTooltipCallback;
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
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;

public class BetterEndClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EndBlockEntityRenders.register();
        EndFlowerPotModels.register();
        EndScreens.register();
        EndParticles.register();
        EndEntitiesRenders.register();
        EndModelProviders.register();
        MultiModelItem.register();
        registerTooltips();


        ModMenu.addModMenuScreen(BetterEnd.C.modId, ConfigScreen::new);

        //TODO: Fabric's ModelLoadingPlugin.Context.resolveModel() was removed in favor of the
        // modifyModelOnLoad()/BeforeBake event family, which no longer exposes a simple
        // "load a different model by id" helper. Re-enable the custom chorus flower/plant
        // model swap (GeneratorOptions.changeChorusPlant()) once a working replacement is found.

        if (Configs.CLIENT_CONFIG.customSky.get()) {
            DimensionRenderingRegistry.registerSkyRenderer(Level.END, new BetterEndSkyRenderer());
        }
        //TODO: Trinkets integration disabled (dependency commented out in build.gradle)
//        if (BetterEnd.TRINKETS_CORE.isLoaded()) {
//            org.betterx.betterend.integration.trinkets.ElytraClient.register();
//        }
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
