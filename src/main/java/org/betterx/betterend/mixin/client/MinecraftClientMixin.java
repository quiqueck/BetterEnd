package org.betterx.betterend.mixin.client;

import org.betterx.bclib.util.MHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.Level;

import java.util.Optional;

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

    @Shadow
    public Screen screen;

    @Final
    @Shadow
    public Gui gui;

    @Shadow
    public ClientLevel level;

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void be_getEndMusic(CallbackInfoReturnable<MusicInfo> info) {
        if (!(this.screen instanceof WinScreen) && this.player != null) {
            if (this.player.level().dimension() == Level.END) {
                if (this.gui.getBossOverlay().shouldPlayMusic() && MHelper.lengthSqr(
                        this.player.getX(),
                        this.player.getZ()
                ) < 250000) {
                    info.setReturnValue(new MusicInfo(Musics.END_BOSS));
                } else {
                    Optional<WeightedList<Music>> tracks = this.level.getBiomeManager()
                                                                     .getNoiseBiomeAtPosition(this.player.blockPosition())
                                                                     .value()
                                                                     .getBackgroundMusic();
                    Music sound = tracks
                            .flatMap(list -> list.getRandom(this.level.random))
                            .orElse(Musics.END);
                    info.setReturnValue(new MusicInfo(sound));
                }
                info.cancel();
            }
        }
    }
}
