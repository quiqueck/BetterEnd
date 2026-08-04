package org.betterx.datagen.betterend.recipes;

import org.betterx.betterend.registry.EndSounds;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class JukeboxRegistryProvider extends WoverRegistryContentProvider<JukeboxSong> {
    public JukeboxRegistryProvider(
            ModCore modCore
    ) {
        super(modCore, "BetterEnd - Jukebox", Registries.JUKEBOX_SONG);
    }

    private static void register(
            BootstrapContext<JukeboxSong> bootstrapContext,
            ResourceKey<JukeboxSong> resourceKey,
            Holder.Reference<SoundEvent> reference,
            int lengthInSeconds,
            int comparatorOutput
    ) {
        bootstrapContext.register(resourceKey, new JukeboxSong(reference, Component.translatable(Util.makeDescriptionId("jukebox_song", resourceKey.location())), (float) lengthInSeconds, comparatorOutput));
    }

    @Override
    protected void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, EndSounds.RECORD_STRANGE_AND_ALIEN, EndSounds.RECORD_STRANGE_AND_ALIEN_SOUND, (4 * 60) + 26, 0);
        register(context, EndSounds.RECORD_GRASPING_AT_STARS, EndSounds.RECORD_GRASPING_AT_STARS_SOUND, (8 * 60) + 48, 0);
        register(context, EndSounds.RECORD_ENDSEEKER, EndSounds.RECORD_ENDSEEKER_SOUND, (7 * 60) + 41, 0);
        register(context, EndSounds.RECORD_EO_DRACONA, EndSounds.RECORD_EO_DRACONA_SOUND, (5 * 60) + 53, 0);
        register(context, EndSounds.RECORD_ENDER_HOLLOW, EndSounds.RECORD_ENDER_HOLLOW_SOUND, (3 * 60) + 39, 0);
        register(context, EndSounds.RECORD_MOONLIT_UNDERCURRENTS, EndSounds.RECORD_MOONLIT_UNDERCURRENTS_SOUND, (3 * 60) + 51, 0);

    }
}
