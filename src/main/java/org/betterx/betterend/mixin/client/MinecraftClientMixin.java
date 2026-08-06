package org.betterx.betterend.mixin.client;

import org.betterx.bclib.util.MHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Shadow
    public LocalPlayer player;

    @Final
    @Shadow
    public Gui gui;

    @Shadow
    public ClientLevel level;

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void be_getEndMusic(CallbackInfoReturnable<Music> info) {
        // 26.2 moved the active screen off Minecraft onto Gui (Minecraft#screen is gone).
        if (!(this.gui.screen() instanceof WinScreen) && this.player != null) {
            if (this.player.level().dimension() == Level.END) {
                // 26.2 split the in-game HUD out of Gui into Gui#hud; the boss bar lives there now.
                if (this.gui.hud.getBossOverlay().shouldPlayMusic() && MHelper.lengthSqr(
                        this.player.getX(),
                        this.player.getZ()
                ) < 250000) {
                    info.setReturnValue(Musics.END_BOSS);
                } else {
                    // 26.1: biome background music moved out of Biome onto the BACKGROUND_MUSIC environment
                    // attribute; read the biome's configured value (default over the EMPTY base).
                    BackgroundMusic backgroundMusic = this.level.getBiomeManager()
                                                                .getNoiseBiomeAtPosition(this.player.blockPosition())
                                                                .value()
                                                                .getAttributes()
                                                                .applyModifier(
                                                                        EnvironmentAttributes.BACKGROUND_MUSIC,
                                                                        BackgroundMusic.EMPTY
                                                                );
                    Music sound = backgroundMusic.select(false, false).orElse(Musics.END);
                    info.setReturnValue(sound);
                }
                info.cancel();
            }
        }
    }
}
