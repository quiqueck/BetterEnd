package org.betterx.betterend.registry.item;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.items.BaseDiscItem;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.item.*;
import org.betterx.betterend.item.material.AeterniumSet;
import org.betterx.betterend.item.material.EndArmorTier;
import org.betterx.betterend.trait.item.EndArmorItemTraitBuilder;
import org.betterx.betterend.trait.item.HammerTraitBuilder;
import org.betterx.betterend.util.DebugHelpers;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.complex.api.equipment.ArmorSlot;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.complex.api.equipment.ToolTiers;
import de.ambertation.wover.item.api.DefaultItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.client.trait.ClientItemTraits;
import de.ambertation.wover.item.api.client.trait.ItemModelTrait;
import de.ambertation.wover.item.api.trait.ItemTraits;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndEntities;
import org.betterx.betterend.registry.EndSounds;

public class EndDiscItems {
    // Music Discs
    public final static Item MUSIC_DISC_STRANGE_AND_ALIEN = EndItems.registerEndDisc(
            "music_disc_strange_and_alien",
            EndSounds.RECORD_STRANGE_AND_ALIEN
    );

    public final static Item MUSIC_DISC_GRASPING_AT_STARS = EndItems.registerEndDisc(
            "music_disc_grasping_at_stars",
            EndSounds.RECORD_GRASPING_AT_STARS
    );

    public final static Item MUSIC_DISC_ENDSEEKER = EndItems.registerEndDisc(
            "music_disc_endseeker",
            EndSounds.RECORD_ENDSEEKER
    );

    public final static Item MUSIC_DISC_EO_DRACONA = EndItems.registerEndDisc(
            "music_disc_eo_dracona",
            EndSounds.RECORD_EO_DRACONA
    );

    public final static Item MUSIC_DISC_ENDER_HOLLOW = EndItems.registerEndDisc(
            "music_disc_ender_hollow",
            EndSounds.RECORD_ENDER_HOLLOW
    );

    public final static Item MUSIC_DISC_MOONLIT_UNDERCURRENTS = EndItems.registerEndDisc(
            "music_disc_moonlit_undercurrents",
            EndSounds.RECORD_MOONLIT_UNDERCURRENTS
    );

    public static void ensureLoaded() {}
}
