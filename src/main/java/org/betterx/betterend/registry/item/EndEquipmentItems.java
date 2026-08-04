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

public class EndEquipmentItems {
    public static final AeterniumSet AETERNIUM_SET = new AeterniumSet();

    public static final CrystaliteHelmet CRYSTALITE_HELMET = CrystaliteHelmet.definition(
            EndItems.getItemRegistry(), "crystalite_helmet"
    ).buildAndRegister();

    public static final CrystaliteChestplate CRYSTALITE_CHESTPLATE = CrystaliteChestplate.definition(
            EndItems.getItemRegistry(), "crystalite_chestplate"
    ).buildAndRegister();

    public static final Item CRYSTALITE_LEGGINGS = CrystaliteLeggings.definition(
            EndItems.getItemRegistry(), "crystalite_leggings"
    ).buildAndRegister();

    public static final Item CRYSTALITE_BOOTS = CrystaliteBoots.definition(
            EndItems.getItemRegistry(), "crystalite_boots"
    ).buildAndRegister();

    public static final Item ARMORED_ELYTRA = EndItems.getItemRegistry()
            .defineArmorItem(
                    "elytra_armored",
                    (def) -> new ArmoredElytra(0.97, def)
            )
            .addTrait(ItemTraits.ELYTRA_ITEM)
            .addTrait(EndArmorItemTraitBuilder.BUILDER.with(
                    ArmorSlot.CHESTPLATE_SLOT,
                    EndArmorTier.AETERNIUM_ELYTRA,
                    1.15f, 1.15f, 0.5f,
                    true
            ))
            .durability(900)
            .rarity(Rarity.EPIC)
            .buildAndRegister();

    public static final Item CRYSTALITE_ELYTRA = EndItems.getItemRegistry()
            .defineArmorItem(
                    "elytra_crystalite",
                    (def) -> new ArmoredElytra(1.0, def)
            )
            .addTrait(ItemTraits.ELYTRA_ITEM.with(EndResourceItems.ENCHANTED_MEMBRANE))
            .addTrait(EndArmorItemTraitBuilder.BUILDER.with(
                    ArmorSlot.CHESTPLATE_SLOT,
                    EndArmorTier.CRYSTALITE_ELYTRA,
                    1.2f, 1.25f, 0.5f,
                    false
            ))
            .durability(650)
            .rarity(Rarity.EPIC)
            .buildAndRegister();

    public static final Item IRON_HAMMER = EndItems.getItemRegistry()
            .defineToolItem("iron_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.IRON_TOOL, 0.2f))
            .addTags(CommonItemTags.HAMMERS)
            .buildAndRegister();

    public static final Item GOLDEN_HAMMER = EndItems.getItemRegistry()
            .defineToolItem("golden_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.GOLD_TOOL, 0.3f))
            .addTags(CommonItemTags.HAMMERS)
            .buildAndRegister();

    public static final Item DIAMOND_HAMMER = EndItems.getItemRegistry()
            .defineToolItem("diamond_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.DIAMOND_TOOL, 0.2f))
            .addTags(CommonItemTags.HAMMERS)
            .buildAndRegister();

    public static final Item NETHERITE_HAMMER = EndItems.getItemRegistry()
            .defineToolItem("netherite_hammer")
            .addTrait(HammerTraitBuilder.BUILDER.with(ToolTiers.NETHERITE_TOOL, 0.2f))
            .addTags(CommonItemTags.HAMMERS)
            .addTrait(ItemTraits.IS_FIREPROOF)
            .buildAndRegister();

    public static void ensureLoaded() {}
}
