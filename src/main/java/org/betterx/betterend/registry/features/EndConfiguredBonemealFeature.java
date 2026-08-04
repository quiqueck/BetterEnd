package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.Features;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.WeightedBlockPatch;
import de.ambertation.wover.feature.api.configured.configurators.WithConfiguration;
import de.ambertation.wover.feature.api.features.config.ConditionFeatureConfig;

import net.minecraft.world.level.levelgen.feature.Feature;

public class EndConfiguredBonemealFeature {
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<ConditionFeatureConfig>, ConditionFeatureConfig>> BONEMEAL_END_MOSS = ConfiguredFeatureManager
            .configuration(BetterEnd.C.mk("bonemeal_end_moss"), Features.CONDITION);

    public static final ConfiguredFeatureKey<WithConfiguration<Feature<ConditionFeatureConfig>, ConditionFeatureConfig>> BONEMEAL_RUTISCUS = ConfiguredFeatureManager
            .configuration(BetterEnd.C.mk("bonemeal_rutiscus"), Features.CONDITION);

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_END_MYCELIUM = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_end_mycelium"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_JUNGLE_MOSS = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_jungle_moss"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_SANGNUM = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_sangnum"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_MOSSY_DRAGON_BONE = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_mossy_dragon_bone"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_MOSSY_OBSIDIAN = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_mossy_obsidian"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_CAVE_MOSS = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_cave_moss"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_CHORUS_NYLIUM = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_chorus_nylium"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_CRYSTAL_MOSS = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_crystal_moss"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_SHADOW_GRASS = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_shadow_grass"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_PINK_MOSS = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_pink_moss"));

    public static final ConfiguredFeatureKey<WeightedBlockPatch> BONEMEAL_AMBER_MOSS = ConfiguredFeatureManager
            .bonemeal(BetterEnd.C.mk("bonemeal_amber_moss"));
}
