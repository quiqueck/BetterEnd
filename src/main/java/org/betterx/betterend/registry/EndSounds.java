package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class EndSounds {
    // Music
    public static final Holder<SoundEvent> MUSIC_FOREST = register("music", "forest");
    public static final Holder<SoundEvent> MUSIC_WATER = register("music", "water");
    public static final Holder<SoundEvent> MUSIC_DARK = register("music", "dark");
    public static final Holder<SoundEvent> MUSIC_OPENSPACE = register("music", "openspace");
    public static final Holder<SoundEvent> MUSIC_CAVES = register("music", "caves");

    // Ambient
    public static final Holder<SoundEvent> AMBIENT_FOGGY_MUSHROOMLAND = register("ambient", "foggy_mushroomland");
    public static final Holder<SoundEvent> AMBIENT_CHORUS_FOREST = register("ambient", "chorus_forest");
    public static final Holder<SoundEvent> AMBIENT_MEGALAKE = register("ambient", "megalake");
    public static final Holder<SoundEvent> AMBIENT_DUST_WASTELANDS = register("ambient", "dust_wastelands");
    public static final Holder<SoundEvent> AMBIENT_MEGALAKE_GROVE = register("ambient", "megalake_grove");
    public static final Holder<SoundEvent> AMBIENT_BLOSSOMING_SPIRES = register("ambient", "blossoming_spires");
    public static final Holder<SoundEvent> AMBIENT_SULPHUR_SPRINGS = register("ambient", "sulphur_springs");
    public static final Holder<SoundEvent> AMBIENT_UMBRELLA_JUNGLE = register("ambient", "umbrella_jungle");
    public static final Holder<SoundEvent> AMBIENT_GLOWING_GRASSLANDS = register("ambient", "glowing_grasslands");
    public static final Holder<SoundEvent> AMBIENT_CAVES = register("ambient", "caves");
    public static final Holder<SoundEvent> AMBIENT_AMBER_LAND = register("ambient", "amber_land");
    public static final Holder<SoundEvent> UMBRA_VALLEY = register("ambient", "umbra_valley");

    // Entity
    public static final Holder<SoundEvent> ENTITY_DRAGONFLY = register("entity", "dragonfly");
    public static final Holder<SoundEvent> ENTITY_SHADOW_WALKER = register("entity", "shadow_walker");
    public static final Holder<SoundEvent> ENTITY_SHADOW_WALKER_DAMAGE = register("entity", "shadow_walker_damage");
    public static final Holder<SoundEvent> ENTITY_SHADOW_WALKER_DEATH = register("entity", "shadow_walker_death");

    // Records
    public static final Holder.Reference<SoundEvent> RECORD_STRANGE_AND_ALIEN_SOUND = register("record", "strange_and_alien");
    public static final Holder.Reference<SoundEvent> RECORD_GRASPING_AT_STARS_SOUND = register("record", "grasping_at_stars");
    public static final Holder.Reference<SoundEvent> RECORD_ENDSEEKER_SOUND = register("record", "endseeker");
    public static final Holder.Reference<SoundEvent> RECORD_EO_DRACONA_SOUND = register("record", "eo_dracona");

    public static final ResourceKey<JukeboxSong> RECORD_STRANGE_AND_ALIEN = createSongKey("strange_and_alien");
    public static final ResourceKey<JukeboxSong> RECORD_GRASPING_AT_STARS = createSongKey("grasping_at_stars");
    public static final ResourceKey<JukeboxSong> RECORD_ENDSEEKER = createSongKey("endseeker");
    public static final ResourceKey<JukeboxSong> RECORD_EO_DRACONA = createSongKey("eo_dracona");

    public static void register() {
    }

    private static ResourceKey<JukeboxSong> createSongKey(String string) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, BetterEnd.C.mk(string));
    }

    private static Holder.Reference<SoundEvent> register(String type, String id) {
        ResourceLocation loc = BetterEnd.C.mk("betterend." + type + "." + id);
        return Registry.registerForHolder(
                BuiltInRegistries.SOUND_EVENT,
                loc, SoundEvent.createVariableRangeEvent(loc)
        );
    }
}
