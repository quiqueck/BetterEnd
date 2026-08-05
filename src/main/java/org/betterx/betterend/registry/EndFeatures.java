package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.registry.features.EndPlacedCaveFeatures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import org.betterx.betterend.world.features.*;
import org.betterx.betterend.world.features.bushes.*;
import org.betterx.betterend.world.features.terrain.*;
import org.betterx.betterend.world.features.terrain.caves.CaveSurfaceCoatFeature;
import org.betterx.betterend.world.features.terrain.caves.StalactiteClusterFeature;
import org.betterx.betterend.world.features.trees.*;
import de.ambertation.wover.feature.api.FeatureManager;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class EndFeatures {
    public static final StalactiteFeature STALACTITE_FEATURE = inlineBuild("stalactite_feature", new StalactiteFeature());
    public static final BuildingListFeature BUILDING_LIST_FEATURE = inlineBuild("building_list_feature", new BuildingListFeature());
    public static final VineFeature VINE_FEATURE = inlineBuild("vine_feature", new VineFeature());
    public static final WallPlantFeature WALL_PLANT_FEATURE = inlineBuild("wall_plant_feature", new WallPlantFeature());
    public static final WallPlantOnLogFeature WALL_PLANT_ON_LOG_FEATURE = inlineBuild("wall_plant_on_log_feature", new WallPlantOnLogFeature());
    public static final GlowPillarFeature GLOW_PILLAR_FEATURE = inlineBuild("glow_pillar_feature", new GlowPillarFeature());
    public static final HydraluxFeature HYDRALUX_FEATURE = inlineBuild("hydralux_feature", new HydraluxFeature());
    public static final LanceleafFeature LANCELEAF_FEATURE = inlineBuild("lanceleaf_feature", new LanceleafFeature());
    public static final MengerSpongeFeature MENGER_SPONGE_FEATURE = inlineBuild("menger_sponge_feature", new MengerSpongeFeature());
    public static final SinglePlantFeature SINGLE_PLANT_FEATURE = inlineBuild("single_plant_feature", new SinglePlantFeature());
    public static final SingleInvertedScatterFeature SINGLE_INVERTED_SCATTER_FEATURE = inlineBuild("single_inverted_scatter_feature", new SingleInvertedScatterFeature());
    public static final DoublePlantFeature DOUBLE_PLANT_FEATURE = inlineBuild("double_plant_feature", new DoublePlantFeature());
    public static final UnderwaterPlantFeature UNDERWATER_PLANT_FEATURE = inlineBuild("underwater_plant_feature", new UnderwaterPlantFeature());
    public static final ArchFeature ARCH_FEATURE = inlineBuild("arch_feature", new ArchFeature());
    public static final ThinArchFeature THIN_ARCH_FEATURE = inlineBuild("thin_arch_feature", new ThinArchFeature());
    public static final CharniaFeature CHARNIA_FEATURE = inlineBuild("charnia_feature", new CharniaFeature());
    public static final BlueVineFeature BLUE_VINE_FEATURE = inlineBuild("blue_vine_feature", new BlueVineFeature());
    public static final FilaluxFeature FILALUX_FEATURE = inlineBuild("filalux_feature", new FilaluxFeature());
    public static final EndLilyFeature END_LILY_FEATURE = inlineBuild("end_lily_feature", new EndLilyFeature());
    public static final EndLotusFeature END_LOTUS_FEATURE = inlineBuild("end_lotus_feature", new EndLotusFeature());
    public static final EndLotusLeafFeature END_LOTUS_LEAF_FEATURE = inlineBuild("end_lotus_leaf_feature", new EndLotusLeafFeature());
    public static final BushFeature BUSH_FEATURE = inlineBuild("bush_feature", new BushFeature());
    public static final SingleBlockFeature SINGLE_BLOCK_FEATURE = inlineBuild("single_block_feature", new SingleBlockFeature());
    public static final BushWithOuterFeature BUSH_WITH_OUTER_FEATURE = inlineBuild("bush_with_outer_feature", new BushWithOuterFeature());
    public static final MossyGlowshroomFeature MOSSY_GLOWSHROOM_FEATURE = inlineBuild("mossy_glowshroom", new MossyGlowshroomFeature());
    public static final PythadendronTreeFeature PYTHADENDRON_TREE_FEATURE = inlineBuild("pythadendron_tree", new PythadendronTreeFeature());
    public static final LacugroveFeature LACUGROVE_FEATURE = inlineBuild("lacugrove", new LacugroveFeature());
    public static final DragonTreeFeature DRAGON_TREE_FEATURE = inlineBuild("dragon_tree", new DragonTreeFeature());
    public static final TenaneaFeature TENANEA_FEATURE = inlineBuild("tenanea", new TenaneaFeature());
    public static final HelixTreeFeature HELIX_TREE_FEATURE = inlineBuild("helix_tree", new HelixTreeFeature());
    public static final DragonHelixTreeFeature DRAGON_HELIX_TREE_FEATURE = inlineBuild("dragon_helix_tree", new DragonHelixTreeFeature());
    public static final UmbrellaTreeFeature UMBRELLA_TREE_FEATURE = inlineBuild("umbrella_tree", new UmbrellaTreeFeature());
    public static final JellyshroomFeature JELLYSHROOM_FEATURE = inlineBuild("jellyshroom", new JellyshroomFeature());
    public static final GiganticAmaranitaFeature GIGANTIC_AMARANITA_FEATURE = inlineBuild("gigantic_amaranita", new GiganticAmaranitaFeature());
    public static final LucerniaFeature LUCERNIA_FEATURE = inlineBuild("lucernia", new LucerniaFeature());
    public static final TenaneaBushFeature TENANEA_BUSH_FEATURE = inlineBuild("tenanea_bush", new TenaneaBushFeature());
    public static final Lumecorn LUMECORN_FEATURE = inlineBuild("lumecorn", new Lumecorn());
    public static final LargeAmaranitaFeature LARGE_AMARANITA_FEATURE = inlineBuild("large_amaranita", new LargeAmaranitaFeature());
    public static final AmaranitaPatchFeature AMARANITA_PATCH_FEATURE = inlineBuild("amaranita_patch", new AmaranitaPatchFeature());
    public static final NeonCactusFeature NEON_CACTUS_FEATURE = inlineBuild("neon_cactus", new NeonCactusFeature());

    //Ores
    public static final OreLayerFeature LAYERED_ORE_FEATURE = inlineBuild("ore_layer", new OreLayerFeature());

    //Lakes
    // The old EndLakeFeature is gone: End lakes are now the EndLakeStructure family (see
    // EndStructures.END_LAKE*), which carves per-chunk during the LAKES step and no longer strands
    // trees over the water.
    public static final DesertLakeFeature DESERT_LAKE_FEATURE = inlineBuild("desert_lake", new DesertLakeFeature());
    public static final SulphuricLakeFeature SULPHURIC_LAKE_FEATURE = inlineBuild("sulphuric_lake", new SulphuricLakeFeature());

    //Terrain
    public static final SurfaceVentFeature SURFACE_VENT_FEATURE = inlineBuild("surface_vent", new SurfaceVentFeature());
    public static final SulphurHillFeature SULPHUR_HILL_FEATURE = inlineBuild("sulphur_hill", new SulphurHillFeature());
    public static final ObsidianPillarBasementFeature OBSIDIAN_PILLAR_FEATURE = inlineBuild("obsidian_pillar_basement", new ObsidianPillarBasementFeature());
    public static final ObsidianBoulderFeature OBSIDIAN_BOULDER_FEATURE = inlineBuild("obsidian_boulder", new ObsidianBoulderFeature());
    public static final FallenPillarFeature FALLEN_PILLAR_FEATURE = inlineBuild("fallen_pillar", new FallenPillarFeature());
    public static final CrashedShipFeature CRASHED_SHIP_FEATURE = inlineBuild("crashed_ship", new CrashedShipFeature());
    public static final SilkMothNestFeature SILK_MOTH_NEST_FEATURE = inlineBuild("silk_moth_nest", new SilkMothNestFeature());
    public static final IceStarFeature ICE_STAR_FEATURE = inlineBuild("ice_star", new IceStarFeature());
    public static final SpireFeature SPIRE_FEATURE = inlineBuild("spire", new SpireFeature());
    public static final FloatingSpireFeature FLOATING_SPIRE_FEATURE = inlineBuild("floating_spire", new FloatingSpireFeature());
    public static final GeyserFeature GEYSER_FEATURE = inlineBuild("geyser", new GeyserFeature());
    public static final PondWithWaterfallFeature POND_WITH_WATERFALL_FEATURE = inlineBuild("pond_with_waterfall", new PondWithWaterfallFeature());
    public static final BiomeIslandFeature OVERWORLD_ISLAND = inlineBuild("overworld_island", new BiomeIslandFeature());

    // Caves
    public static final SmaragdantCrystalFeature SMARAGDANT_CRYSTAL_FEATURE = inlineBuild("smaragdant_crystal", new SmaragdantCrystalFeature());
    public static final BigAuroraCrystalFeature BIG_AURORA_CRYSTAL_FEATURE = inlineBuild("big_aurora_crystal", new BigAuroraCrystalFeature());
    public static final CavePumpkinFeature CAVE_PUMPKIN_FEATURE = inlineBuild("cave_pumpkin", new CavePumpkinFeature());
    public static final StalactiteClusterFeature STALACTITE_CLUSTER = inlineBuild("stalactite_cluster", new StalactiteClusterFeature());
    public static final CaveSurfaceCoatFeature CAVE_SURFACE_COAT = inlineBuild("cave_surface_coat", new CaveSurfaceCoatFeature());

    public static <F extends Feature<FC>, FC extends FeatureConfiguration> F inlineBuild(String name, F feature) {
        Identifier l = BetterEnd.C.mk(name);

        final Registry<Feature<?>> features;
        if (WorldState.registryAccess() != null) {
            features = WorldState.allStageRegistryAccess().lookupOrThrow(Registries.FEATURE);
        } else {
            features = BuiltInRegistries.FEATURE;
        }

        if (features.containsKey(l)) {
            return (F) features.get(l).map(Holder::value).orElseThrow();
        }


        return FeatureManager.register(l, feature);
    }

    // NOTE: hasCaves no longer selects any feature-based cave carving here (the legacy
    // RoundCaveFeature/TunelCaveFeature pair was removed once real WorldCarvers landed, see
    // EndCaveCarver/EndTunnelCarver + EndCarvers). The parameter is kept only because it mirrors
    // the biome-data "hasCaves" flag that EndCaveBiomeDecider consumes when deciding whether a
    // land biome gets a vertical cave-biome counterpart; it is intentionally unused in this method.
    public static void addDefaultFeatures(
            EndBiomeBuilder builder, boolean hasCaves
    ) {
        builder.feature(EndOreFeatures.THALLASIUM_ORE);
        builder.feature(EndOreFeatures.ENDER_ORE);
        builder.feature(EndTerrainFeatures.CRASHED_SHIP);
        // The cave surface coat goes on EVERY End biome, land ones included, even though it only ever paints
        // cave columns. It is not a per-biome decoration: one placement sweeps the whole chunk and decides
        // per column what to do (see CaveSurfaceCoatFeature). What it needs from the placement is simply to
        // RUN in every chunk that holds part of the cave band - and a placed feature only runs where the
        // biome at the rolled position declared it. Declaring it on the cave biomes alone left that to
        // chance: in a chunk that is part cave-biome and part land, the roll lands in a land column often
        // enough that whole chunks of cave went uncoated. Declaring it everywhere makes the roll always
        // succeed; in a chunk with no cave columns the pass costs 324 biome lookups and stops.
        builder.feature(EndPlacedCaveFeatures.CAVE_SURFACE_COAT);
    }

    public static void register() {
    }
}
