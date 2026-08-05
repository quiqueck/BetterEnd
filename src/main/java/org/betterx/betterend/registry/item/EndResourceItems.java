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
import net.minecraft.core.component.DataComponents;
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

public class EndResourceItems {
    public final static Item ENDER_DUST = EndItems.registerEndItem("ender_dust");

    public final static Item ENDER_SHARD = EndItems.registerEndItem("ender_shard");

    // Model is hand-authored (uses the "_small" leaf texture, not the plain item-name convention the
    // generic flat-item fallback assumes) - point the item definition at the existing hand-authored
    // model instead of letting the fallback regenerate (and overwrite) it.
    public final static Item END_LILY_LEAF = EndItems.defineEndItem("end_lily_leaf")
            .addTrait(ModCore.isDatagen() ? EndItems.ExternalItemModel.build() : null)
            .buildAndRegister();

    public final static Item END_LILY_LEAF_DRIED = EndItems.registerEndItem("end_lily_leaf_dried");

    public final static Item CRYSTAL_SHARDS = EndItems.registerEndItem("crystal_shards");

    public final static Item RAW_AMBER = EndItems.registerEndItem("raw_amber");

    public final static Item AMBER_GEM = EndItems.registerEndItem("amber_gem");

    public final static Item GLOWING_BULB = EndItems.registerEndItem("glowing_bulb");

    public final static Item CRYSTALLINE_SULPHUR = EndItems.registerEndItem("crystalline_sulphur");

    public final static Item HYDRALUX_PETAL = EndItems.registerEndItem("hydralux_petal");

    public final static Item GELATINE = EndItems.registerEndItem("gelatine");

    public static final Item ETERNAL_CRYSTAL = EndItems.defineEndItem("eternal_crystal")
            .stacksTo(16)
            .rarity(Rarity.EPIC)
            .buildAndRegister();

    public final static Item ENCHANTED_PETAL = EndItems.defineEndItem("enchanted_petal")
            .stacksTo(16)
            .rarity(Rarity.RARE)
            .addTrait(ModelTraitLibrary.itemModel(() -> HYDRALUX_PETAL))
            .buildAndRegister();

    public final static Item LEATHER_STRIPE = EndItems.registerEndItem("leather_stripe");

    public final static Item LEATHER_WRAPPED_STICK = EndItems.registerEndItem("leather_wrapped_stick");

    public final static Item SILK_FIBER = EndItems.registerEndItem("silk_fiber");

    public final static Item LUMECORN_ROD = EndItems.registerEndItem("lumecorn_rod");

    public final static Item SILK_MOTH_MATRIX = EndItems.registerEndItem("silk_moth_matrix");

    // Borrows the phantom membrane's texture, so the glint is the only thing telling the two apart
    // in an inventory - the same trick vanilla plays with the enchanted golden apple.
    public final static Item ENCHANTED_MEMBRANE = EndItems.defineEndItem("enchanted_membrane")
            .stacksTo(16)
            .rarity(Rarity.RARE)
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            .addTrait(ModelTraitLibrary.itemModel(() -> Items.PHANTOM_MEMBRANE))
            .buildAndRegister();

    public static void ensureLoaded() {}
}
