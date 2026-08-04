package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.world.features.bushes.LargeAmaranitaFeature;
import org.betterx.betterend.world.features.bushes.Lumecorn;
import org.betterx.betterend.world.features.bushes.TenaneaBushFeature;
import org.betterx.betterend.world.features.trees.*;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.WithConfiguration;

import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndConfiguredVegetation {
    public static final ConfiguredFeatureKey<WithConfiguration<DragonTreeFeature, NoneFeatureConfiguration>> DRAGON_TREE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("dragon_tree"), EndFeatures.DRAGON_TREE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<GiganticAmaranitaFeature, NoneFeatureConfiguration>> GIGANTIC_AMARANITA = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("gigantic_amaranita"), EndFeatures.GIGANTIC_AMARANITA_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<HelixTreeFeature, NoneFeatureConfiguration>> HELIX_TREE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("helix_tree"), EndFeatures.HELIX_TREE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<DragonHelixTreeFeature, NoneFeatureConfiguration>> DRAGON_HELIX_TREE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("dragon_helix_tree"), EndFeatures.DRAGON_HELIX_TREE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<JellyshroomFeature, NoneFeatureConfiguration>> JELLYSHROOM = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("jellyshroom"), EndFeatures.JELLYSHROOM_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<LacugroveFeature, NoneFeatureConfiguration>> LACUGROVE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("lacugrove"), EndFeatures.LACUGROVE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<LucerniaFeature, NoneFeatureConfiguration>> LUCERNIA = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("lucernia"), EndFeatures.LUCERNIA_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<MossyGlowshroomFeature, NoneFeatureConfiguration>> MOSSY_GLOWSHROOM = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("mossy_glowshroom"), EndFeatures.MOSSY_GLOWSHROOM_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<PythadendronTreeFeature, NoneFeatureConfiguration>> PYTHADENDRON_TREE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("pythadendron_tree"), EndFeatures.PYTHADENDRON_TREE_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<TenaneaFeature, NoneFeatureConfiguration>> TENANEA = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("tenanea"), EndFeatures.TENANEA_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<UmbrellaTreeFeature, NoneFeatureConfiguration>> UMBRELLA_TREE = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("umbrella_tree"), EndFeatures.UMBRELLA_TREE_FEATURE);

    public static final ConfiguredFeatureKey<WithConfiguration<LargeAmaranitaFeature, NoneFeatureConfiguration>> LARGE_AMARANITA = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("large_amaranita"), EndFeatures.LARGE_AMARANITA_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<Lumecorn, NoneFeatureConfiguration>> LUMECORN = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("lumecorn"), EndFeatures.LUMECORN_FEATURE);
    public static final ConfiguredFeatureKey<WithConfiguration<TenaneaBushFeature, NoneFeatureConfiguration>> TENANEA_BUSH = ConfiguredFeatureManager.configuration(BetterEnd.C.mk("tenanea_bush"), EndFeatures.TENANEA_BUSH_FEATURE);
}
