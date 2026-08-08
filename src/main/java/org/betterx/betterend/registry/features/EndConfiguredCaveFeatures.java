package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.world.features.CavePumpkinFeature;
import org.betterx.betterend.world.features.VineFeature;
import org.betterx.betterend.world.features.VineFeatureConfig;
import org.betterx.betterend.world.features.bushes.BushFeature;
import org.betterx.betterend.world.features.bushes.BushFeatureConfig;
import org.betterx.betterend.world.features.terrain.*;
import org.betterx.betterend.world.features.terrain.caves.CaveSurfaceCoatConfig;
import org.betterx.betterend.world.features.terrain.caves.CaveSurfaceCoatFeature;
import org.betterx.betterend.world.features.terrain.caves.StalactiteClusterConfig;
import org.betterx.betterend.world.features.terrain.caves.StalactiteClusterFeature;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.WithConfiguration;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class EndConfiguredCaveFeatures {
    public static final ConfiguredFeatureKey<WithConfiguration<SmaragdantCrystalFeature, NoneFeatureConfiguration>> SMARAGDANT_CRYSTAL = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("smaragdant_crystal"), EndFeatures.SMARAGDANT_CRYSTAL_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<SingleBlockFeature, SimpleBlockConfiguration>> SMARAGDANT_CRYSTAL_SHARD = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("smaragdant_crystal_shard"), EndFeatures.SINGLE_BLOCK_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<BigAuroraCrystalFeature, NoneFeatureConfiguration>> BIG_AURORA_CRYSTAL = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("big_aurora_crystal"), EndFeatures.BIG_AURORA_CRYSTAL_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<BushFeature, BushFeatureConfig>> CAVE_BUSH = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("cave_bush"), EndFeatures.BUSH_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<SingleBlockFeature, SimpleBlockConfiguration>> CAVE_GRASS = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("cave_grass"), EndFeatures.SINGLE_BLOCK_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<VineFeature, VineFeatureConfig>> RUBINEA = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("rubinea"), EndFeatures.VINE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<VineFeature, VineFeatureConfig>> MAGNULA = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("magnula"), EndFeatures.VINE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteFeature, StalactiteFeatureConfig>> END_STONE_STALACTITE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("end_stone_stalactite"), EndFeatures.STALACTITE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteFeature, StalactiteFeatureConfig>> END_STONE_STALAGMITE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("end_stone_stalagmite"), EndFeatures.STALACTITE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteFeature, StalactiteFeatureConfig>> END_STONE_STALACTITE_CAVEMOSS = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("end_stone_stalactite_cavemoss"), EndFeatures.STALACTITE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteFeature, StalactiteFeatureConfig>> END_STONE_STALAGMITE_CAVEMOSS = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("end_stone_stalagmite_cavemoss"), EndFeatures.STALACTITE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteFeature, StalactiteFeatureConfig>> END_STONE_WITH_CAVEMOSS_STALACTITE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("end_stone_with_cavemoss_stalactite"), EndFeatures.STALACTITE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteFeature, StalactiteFeatureConfig>> END_STONE_WITH_CAVEMOSS_STALAGMITE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("end_stone_with_cavemoss_stalagmite"), EndFeatures.STALACTITE_FEATURE);

    public static final ConfiguredFeatureKey<WithConfiguration<CavePumpkinFeature, NoneFeatureConfiguration>> CAVE_PUMPKIN = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("cave_pumpkin"), EndFeatures.CAVE_PUMPKIN_FEATURE);

    // WP4.3: dripstone-style stalactite clusters (own their per-column floor/ceiling placement).
    // Cave surface coat: one instance for every cave biome and every face - the shell pass coats floor,
    // wall and ceiling in one run, and the materials come from the EndCaveBiome at placement time.
    public static final ConfiguredFeatureKey<WithConfiguration<CaveSurfaceCoatFeature, CaveSurfaceCoatConfig>> CAVE_SURFACE_COAT = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("cave_surface_coat"), EndFeatures.CAVE_SURFACE_COAT);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteClusterFeature, StalactiteClusterConfig>> STALACTITE_CLUSTER_PLAIN = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("stalactite_cluster_plain"), EndFeatures.STALACTITE_CLUSTER);
    public static final ConfiguredFeatureKey<WithConfiguration<StalactiteClusterFeature, StalactiteClusterConfig>> STALACTITE_CLUSTER_CAVEMOSS = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("stalactite_cluster_cavemoss"), EndFeatures.STALACTITE_CLUSTER);

    // WP4.4: lush cave vegetation patches (vanilla VegetationPatchFeature, data-only).
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<VegetationPatchConfiguration>, VegetationPatchConfiguration>> CAVE_LUSH_FLOOR_PATCH = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("cave_lush_floor_patch"), Feature.VEGETATION_PATCH);
    public static final ConfiguredFeatureKey<WithConfiguration<Feature<VegetationPatchConfiguration>, VegetationPatchConfiguration>> CAVE_LUSH_CEILING_PATCH = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("cave_lush_ceiling_patch"), Feature.VEGETATION_PATCH);

}
