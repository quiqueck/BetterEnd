package org.betterx.betterend.config.screen;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.config.Configs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class ConfigScreen extends de.ambertation.wunderlib.ui.vanilla.ConfigScreen {
    public static final Identifier BE_LOGO_LOCATION = BetterEnd.C.id("icon.png");

    public ConfigScreen(Screen parent) {
        super(parent, BE_LOGO_LOCATION, Component.translatable("be_config"), List.of(Configs.CLIENT_CONFIG, Configs.GENERATOR_CONFIG));
    }

    @Override
    public void onClose() {
        super.onClose();
        Configs.saveConfigs();
    }
}
