package org.betterx.datagen.betterend.worldgen;

import org.betterx.betterend.registry.EndBiomeBuildings;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.biome.EndBiomeKey;
import org.betterx.betterend.world.biome.air.BiomeIceStarfield;
import org.betterx.betterend.world.biome.air.FlowerIsletsBiome;
import org.betterx.betterend.world.biome.air.WaterfallPondsBiome;
import org.betterx.betterend.world.biome.cave.*;
import org.betterx.betterend.world.biome.land.*;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverBiomeProvider;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;


import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class EndBiomesProvider extends WoverBiomeProvider {
    public record BiomeInfo(EndBiome.Config config, TagKey<Biome>[] tags) {
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

        // Ice stars belong to the void (outermost small-island ring) only, not the barrens ring inside
        // it. Removing IS_END_BARRENS empties the barrens tag, so that ring falls back to the picker's
        // default (vanilla end_barrens) - barren islands with no ice stars.
        putBiome(
                EndBiomes.ICE_STARFIELD,
                new BiomeIceStarfield(),
                CommonBiomeTags.IS_SMALL_END_ISLAND
        );

        // Lush void-ring small-island biomes. They are placed as ordinary void small-island patches
        // via the IS_SMALL_END_ISLAND tag; each grows its own flat-topped end-stone terrain through
        // EndStructures.SMALL_ISLAND (RAW_GENERATION) instead of relying on the (removed)
        // terrain-coupled small-island biome decider. Each opts into END_BRIDGE and SMALL_ISLAND in
        // its class.
        putBiome(
                EndBiomes.FLOWER_ISLETS,
                new FlowerIsletsBiome(),
                CommonBiomeTags.IS_SMALL_END_ISLAND
        );
        putBiome(
                EndBiomes.WATERFALL_PONDS,
                new WaterfallPondsBiome(),
                CommonBiomeTags.IS_SMALL_END_ISLAND
        );

        putBiome(EndBiomes.EMPTY_END_CAVE, new EmptyEndCaveBiome(EndBiomes.EMPTY_END_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.EMPTY_SMARAGDANT_CAVE, new EmptySmaragdantCaveBiome(EndBiomes.EMPTY_SMARAGDANT_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.LUSH_SMARAGDANT_CAVE, new LushSmaragdantCaveBiome(EndBiomes.LUSH_SMARAGDANT_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.EMPTY_AURORA_CAVE, new EmptyAuroraCaveBiome(EndBiomes.EMPTY_AURORA_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.LUSH_AURORA_CAVE, new LushAuroraCaveBiome(EndBiomes.LUSH_AURORA_CAVE), EndTags.IS_END_CAVE);
        putBiome(EndBiomes.JADE_CAVE, new JadeCaveBiome(EndBiomes.JADE_CAVE), EndTags.IS_END_CAVE);
    }

    @SafeVarargs
    private static void putBiome(EndBiomeKey<?, ?> key, EndBiome.Config config, TagKey<Biome>... tags) {
        BIOMES.put(key, new BiomeInfo(config, tags));
    }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        for (Map.Entry<EndBiomeKey<?, ?>, BiomeInfo> e : BIOMES.entrySet()) {
            final EndBiomeBuilder builder = e.getKey().bootstrap(context, e.getValue().config, e.getValue().tags);
            // the biome's buildings and ruins, each a structure of its own (see EndBiomeBuildings)
            for (EndBiomeBuildings.Building building : EndBiomeBuildings.of(e.getKey())) {
                builder.structure(building.structure());
            }
            builder.register();
        }
    }
}
