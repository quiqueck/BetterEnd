package org.betterx.datagen.betterend.worldgen;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.util.JsonFactory;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;
import org.betterx.betterend.world.biome.air.BiomeIceStarfield;
import org.betterx.betterend.world.biome.cave.*;
import org.betterx.betterend.world.biome.land.*;
import org.betterx.betterend.world.features.BuildingListFeature;
import org.betterx.betterend.world.features.BuildingListFeatureConfig;
import org.betterx.betterend.world.features.NBTFeature;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.WithConfiguration;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class EndBiomesProvider extends WoverBiomeProvider {
    public record BiomeInfo(EndBiome.Config config, TagKey<Biome> tag,
                            List<BuildingListFeature.StructureInfo> structures,
                            ConfiguredFeatureKey<WithConfiguration<BuildingListFeature, BuildingListFeatureConfig>> configuredFeatureKey,
                            PlacedConfiguredFeatureKey placed) {
    }

    public static final Map<EndBiomeKey<?, ?>, BiomeInfo> BIOMES = new HashMap<>();

    public EndBiomesProvider(@NotNull ModCore modCore) {
        super(modCore);
    }

    public static void loadAllBiomeConfigs() {
        putBiome(EndBiomes.FOGGY_MUSHROOMLAND, new FoggyMushroomlandBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.CHORUS_FOREST, new ChorusForestBiome(), CommonBiomeTags.IS_END_HIGHLAND);
        putBiome(EndBiomes.DUST_WASTELANDS, new DustWastelandsBiome(), CommonBiomeTags.IS_END_MIDLAND);
        putBiome(EndBiomes.NEON_OASIS, new NeonOasisBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.PAINTED_MOUNTAINS, new PaintedMountainsBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.MEGALAKE, new MegalakeBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.MEGALAKE_GROVE, new MegalakeGroveBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.CRYSTAL_MOUNTAINS, new CrystalMountainsBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.SHADOW_FOREST, new ShadowForestBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.AMBER_LAND, new AmberLandBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.BLOSSOMING_SPIRES, new BlossomingSpiresBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.SULPHUR_SPRINGS, new SulphurSpringsBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.UMBRELLA_JUNGLE, new UmbrellaJungleBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.GLOWING_GRASSLANDS, new GlowingGrasslandsBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.DRAGON_GRAVEYARDS, new DragonGraveyardsBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.DRY_SHRUBLAND, new DryShrublandBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.LANTERN_WOODS, new LanternWoodsBiome(), CommonBiomeTags.IS_END_LAND);
        putBiome(EndBiomes.UMBRA_VALLEY, new UmbraValleyBiome(), CommonBiomeTags.IS_END_LAND);

        putBiome(EndBiomes.ICE_STARFIELD, new BiomeIceStarfield(), CommonBiomeTags.IS_SMALL_END_ISLAND);

        putBiome(EndBiomes.EMPTY_END_CAVE, new EmptyEndCaveBiome(EndBiomes.EMPTY_END_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.EMPTY_SMARAGDANT_CAVE, new EmptySmaragdantCaveBiome(EndBiomes.EMPTY_SMARAGDANT_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.LUSH_SMARAGDANT_CAVE, new LushSmaragdantCaveBiome(EndBiomes.LUSH_SMARAGDANT_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.EMPTY_AURORA_CAVE, new EmptyAuroraCaveBiome(EndBiomes.EMPTY_AURORA_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.LUSH_AURORA_CAVE, new LushAuroraCaveBiome(EndBiomes.LUSH_AURORA_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.JADE_CAVE, new JadeCaveBiome(EndBiomes.JADE_CAVE), EndTags.IS_END_CAVE);
    }

    private static void putBiome(EndBiomeKey<?, ?> key, EndBiome.Config config, TagKey<Biome> tag) {
        final List<BuildingListFeature.StructureInfo> structures = getBiomeStructures(key.key.location());
        PlacedConfiguredFeatureKey placed = null;
        ConfiguredFeatureKey<WithConfiguration<BuildingListFeature, BuildingListFeatureConfig>> configuredFeatureKey = null;
        if (structures != null) {
            configuredFeatureKey
                    = ConfiguredFeatureManager.configuration(
                    ResourceLocation.fromNamespaceAndPath(
                            key.key.location().getNamespace(),
                            key.key.location().getPath() + "_structures"
                    ),
                    EndFeatures.BUILDING_LIST_FEATURE
            );

            placed = PlacedFeatureManager
                    .createKey(configuredFeatureKey)
                    .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);
        }

        BIOMES.put(key, new BiomeInfo(config, tag, structures, configuredFeatureKey, placed));
    }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        for (Map.Entry<EndBiomeKey<?, ?>, BiomeInfo> e : BIOMES.entrySet()) {
            final EndBiomeBuilder builder = e.getKey().bootstrap(context, e.getValue().config, e.getValue().tag);
            if (e.getValue().placed != null)
                builder.feature(e.getValue().placed);
            builder.register();
        }
    }

    private static List<BuildingListFeature.StructureInfo> getBiomeStructures(
            ResourceLocation loc
    ) {
        String ns = loc.getNamespace();
        String nm = loc.getPath();
        return getBiomeStructures(ns, nm);
    }

    private static List<BuildingListFeature.StructureInfo> getBiomeStructures(
            String ns,
            String nm
    ) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ns, nm + "_structures");

        if (WorldState.allStageRegistryAccess() != null) {
            Registry<PlacedFeature> features = WorldState.allStageRegistryAccess()
                                                         .lookupOrThrow(Registries.PLACED_FEATURE);
            if (features.containsKey(id)) {
                BCLib.LOGGER.info("Feature for " + id + " was already build");
            }
        }

        String path = "/data/" + ns + "/structure/biome/" + nm + "/";
        InputStream inputstream = EndFeatures.class.getResourceAsStream(path + "structures.json");
        if (inputstream != null) {
            JsonObject obj = JsonFactory.getJsonObject(inputstream);
            JsonArray structures = obj.getAsJsonArray("structures");
            if (structures != null) {
                List<BuildingListFeature.StructureInfo> list = Lists.newArrayList();
                structures.forEach((entry) -> {
                    JsonObject e = entry.getAsJsonObject();
                    String structure = path + e.get("nbt").getAsString() + ".nbt";
                    NBTFeature.TerrainMerge terrainMerge = NBTFeature.TerrainMerge.getFromString(e
                            .get("terrainMerge")
                            .getAsString());
                    int offsetY = e.get("offsetY").getAsInt();
                    list.add(new BuildingListFeature.StructureInfo(structure, offsetY, terrainMerge));
                });

                if (!list.isEmpty()) {
                    return list;
                }
            }
        }
        return null;
    }
}
