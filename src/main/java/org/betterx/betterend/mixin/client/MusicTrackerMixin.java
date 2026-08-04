package org.betterx.betterend.mixin.client;

import org.betterx.betterend.config.Configs;
import org.betterx.betterend.world.biome.EndBiome;
import de.ambertation.wover.biome.api.BiomeManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicTrackerMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Final
    @Shadow
    private RandomSource random;

    @Shadow
    private SoundInstance currentMusic;

    @Shadow
    private int nextSongDelay;

    private static float volume = 1;
    private static float srcVolume = 0;
    private static long time;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void be_onTick(CallbackInfo info) {
        if (Configs.CLIENT_CONFIG.blendBiomeMusic.get()) {
            MusicInfo musicInfo = minecraft.getSituationalMusic();
            Music musicSound = musicInfo.music();
            if (be_checkNullSound(musicSound) && volume > 0 && be_shouldChangeSound(musicSound) && be_isCorrectBiome()) {
                if (volume > 0) {
                    if (srcVolume < 0) {
                        srcVolume = currentMusic.getVolume();
                    }
                    if (currentMusic instanceof AbstractSoundInstance) {
                        ((AbstractSoundInstanceAccessor) currentMusic).setVolume(volume);
                    }
                    minecraft.getSoundManager()
                             .updateSourceVolume(currentMusic.getSource(), currentMusic.getVolume() * volume);
                    long t = System.currentTimeMillis();
                    if (volume == 1 && time == 0) {
                        time = t;
                    }
                    float delta = (t - time) * 0.0005F;
                    time = t;
                    volume -= delta;
                    if (volume < 0) {
                        volume = 0;
                    }
                }
                if (volume == 0) {
                    volume = 1;
                    time = 0;
                    srcVolume = -1;
                    this.minecraft.getSoundManager().stop(this.currentMusic);
                    this.nextSongDelay = Mth.nextInt(this.random, 0, musicSound.minDelay() / 2);
                    this.currentMusic = null;
                }
                if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
                    this.startPlaying(musicInfo);
                }
                info.cancel();
            } else {
                volume = 1;
            }
        }
    }

    private boolean be_isCorrectBiome() {
        if (minecraft.level == null) {
            return false;
        }
        Holder<Biome> biome = minecraft.level.getBiome(minecraft.player.blockPosition());
        return BiomeManager.biomeDataForHolder(biome) instanceof EndBiome;
    }

    @Unique
    private boolean be_shouldChangeSound(Music musicSound) {
        return currentMusic != null && !musicSound
                .event()
                .value()
                .location()
                .equals(this.currentMusic.getLocation()) && musicSound.replaceCurrentMusic();
    }

    @Unique
    private boolean be_checkNullSound(Music musicSound) {
        return musicSound != null && musicSound.event() != null;
    }

    @Shadow
    public abstract void startPlaying(MusicInfo musicInfo);
}
