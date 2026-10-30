package org.betterx.betterend.registry;

import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiome;
import org.betterx.bclib.api.v2.levelgen.biomes.BCLBiomeBuilder;
import org.betterx.bclib.api.v2.levelgen.biomes.BiomeAPI;
import org.betterx.bclib.api.v3.levelgen.features.BCLConfigureFeature;
import org.betterx.bclib.api.v3.levelgen.features.BCLFeature;
import org.betterx.bclib.api.v3.levelgen.features.BCLFeatureBuilder;
import org.betterx.bclib.api.v3.levelgen.features.config.ConditionFeatureConfig;
import org.betterx.bclib.api.v3.levelgen.features.placement.InBiome;
import org.betterx.betterend.BetterEnd;
import org.betterx.worlds.together.tag.v3.TagManager;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.cave.EndCaveBiome;
import org.betterx.betterend.world.features.*;
import org.betterx.betterend.world.features.bushes.*;
import org.betterx.betterend.world.features.terrain.*;
import org.betterx.betterend.world.features.terrain.caves.CaveChunkPopulatorFeature;
import org.betterx.betterend.world.features.terrain.caves.RoundCaveFeature;
import org.betterx.betterend.world.features.terrain.caves.TunelCaveFeature;
import org.betterx.betterend.world.features.trees.*;
import org.betterx.worlds.together.world.event.WorldBootstrap;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class EndFeatures {
    public static final StalactiteFeature STALACTITE_FEATURE = inlineBuild(
            "stalactite_feature",
            new StalactiteFeature()
    );

    public static final VineFeature VINE_FEATURE = inlineBuild(
            "vine_feature",
            new VineFeature()
    );

    public static final WallPlantFeature WALL_PLANT_FEATURE = inlineBuild(
            "wall_plant_feature",
            new WallPlantFeature()
    );

    public static final WallPlantOnLogFeature WALL_PLANT_ON_LOG_FEATURE = inlineBuild(
            "wall_plant_on_log_feature",
            new WallPlantOnLogFeature()
    );

    public static final GlowPillarFeature GLOW_PILLAR_FEATURE = inlineBuild(
            "glow_pillar_feature",
            new GlowPillarFeature()
    );

    public static final HydraluxFeature HYDRALUX_FEATURE = inlineBuild(
            "hydralux_feature",
            new HydraluxFeature()
    );

    public static final LanceleafFeature LANCELEAF_FEATURE = inlineBuild(
            "lanceleaf_feature",
            new LanceleafFeature()
    );

    public static final MengerSpongeFeature MENGER_SPONGE_FEATURE = inlineBuild(
            "menger_sponge_feature",
            new MengerSpongeFeature()
    );

    public static final CaveChunkPopulatorFeature CAVE_CHUNK_POPULATOR = inlineBuild(
            "cave_chunk_populator",
            new CaveChunkPopulatorFeature()
    );

    public static final SinglePlantFeature SINGLE_PLANT_FEATURE = inlineBuild(
            "single_plant_feature",
            new SinglePlantFeature()
    );

    public static final SingleInvertedScatterFeature SINGLE_INVERTED_SCATTER_FEATURE = inlineBuild(
            "single_inverted_scatter_feature",
            new SingleInvertedScatterFeature()
    );

    public static final DoublePlantFeature DOUBLE_PLANT_FEATURE = inlineBuild(
            "double_plant_feature",
            new DoublePlantFeature()
    );

    public static final UnderwaterPlantFeature UNDERWATER_PLANT_FEATURE = inlineBuild(
            "underwater_plant_feature",
            new UnderwaterPlantFeature()
    );

    public static final ArchFeature ARCH_FEATURE = inlineBuild(
            "arch_feature",
            new ArchFeature()
    );

    public static final ThinArchFeature THIN_ARCH_FEATURE = inlineBuild(
            "thin_arch_feature",
            new ThinArchFeature()
    );

    public static final CharniaFeature CHARNIA_FEATURE = inlineBuild(
            "charnia_feature",
            new CharniaFeature()
    );

    public static final BlueVineFeature BLUE_VINE_FEATURE = inlineBuild(
            "blue_vine_feature",
            new BlueVineFeature()
    );

    public static final FilaluxFeature FILALUX_FEATURE = inlineBuild(
            "filalux_feature",
            new FilaluxFeature()
    );

    public static final EndLilyFeature END_LILY_FEATURE = inlineBuild(
            "end_lily_feature",
            new EndLilyFeature()
    );

    public static final EndLotusFeature END_LOTUS_FEATURE = inlineBuild(
            "end_lotus_feature",
            new EndLotusFeature()
    );

    public static final EndLotusLeafFeature END_LOTUS_LEAF_FEATURE = inlineBuild(
            "end_lotus_leaf_feature",
            new EndLotusLeafFeature()
    );

    public static final BushFeature BUSH_FEATURE = inlineBuild(
            "bush_feature",
            new BushFeature()
    );

    public static final SingleBlockFeature SINGLE_BLOCK_FEATURE = inlineBuild(
            "single_block_feature",
            new SingleBlockFeature()
    );

    public static final BushWithOuterFeature BUSH_WITH_OUTER_FEATURE = inlineBuild(
            "bush_with_outer_feature",
            new BushWithOuterFeature()
    );
    public static final BCLFeature<MossyGlowshroomFeature, NoneFeatureConfiguration> MOSSY_GLOWSHROOM = registerVegetation(
            "mossy_glowshroom",
            inlineBuild("mossy_glowshroom", new MossyGlowshroomFeature()),
            2
    );
    public static final BCLFeature<PythadendronTreeFeature, NoneFeatureConfiguration> PYTHADENDRON_TREE = registerVegetation(
            "pythadendron_tree",
            inlineBuild("pythadendron_tree", new PythadendronTreeFeature()),
            1
    );
    public static final BCLFeature<LacugroveFeature, NoneFeatureConfiguration> LACUGROVE = registerVegetation(
            "lacugrove",
            inlineBuild("lacugrove", new LacugroveFeature()),
            4
    );
    public static final BCLFeature<DragonTreeFeature, NoneFeatureConfiguration> DRAGON_TREE = registerVegetation(
            "dragon_tree",
            inlineBuild("dragon_tree", new DragonTreeFeature()),
            2
    );
    public static final BCLFeature<TenaneaFeature, NoneFeatureConfiguration> TENANEA = registerVegetation(
            "tenanea",
            inlineBuild("tenanea", new TenaneaFeature()),
            3
    );
    public static final BCLFeature<HelixTreeFeature, NoneFeatureConfiguration> HELIX_TREE = registerVegetation(
            "helix_tree",
            inlineBuild("helix_tree", new HelixTreeFeature()),
            1
    );
    public static final BCLFeature<UmbrellaTreeFeature, NoneFeatureConfiguration> UMBRELLA_TREE = registerVegetation(
            "umbrella_tree",
            inlineBuild("umbrella_tree", new UmbrellaTreeFeature()),
            2
    );
    public static final BCLFeature<JellyshroomFeature, NoneFeatureConfiguration> JELLYSHROOM = registerVegetation(
            "jellyshroom",
            inlineBuild("jellyshroom", new JellyshroomFeature()),
            2
    );
    public static final BCLFeature<GiganticAmaranitaFeature, NoneFeatureConfiguration> GIGANTIC_AMARANITA = registerVegetation(
            "gigantic_amaranita",
            inlineBuild("gigantic_amaranita", new GiganticAmaranitaFeature()),
            1
    );
    public static final BCLFeature<LucerniaFeature, NoneFeatureConfiguration> LUCERNIA = registerVegetation(
            "lucernia",
            inlineBuild("lucernia", new LucerniaFeature()),
            3
    );

    // Bushes //
    public static final BCLFeature<BushFeature, BushFeatureConfig> PYTHADENDRON_BUSH = registerVegetation(
            "pythadendron_bush",
            BUSH_FEATURE,
            new BushFeatureConfig(
                    EndBlocks.PYTHADENDRON_LEAVES,
                    EndBlocks.PYTHADENDRON.getBark()
            ),
            3
    );
    public static final BCLFeature<BushFeature, BushFeatureConfig> DRAGON_TREE_BUSH = registerVegetation(
            "dragon_tree_bush",
            BUSH_FEATURE,
            new BushFeatureConfig(
                    EndBlocks.DRAGON_TREE_LEAVES,
                    EndBlocks.DRAGON_TREE.getBark()
            ),
            5
    );
    public static final BCLFeature<TenaneaBushFeature, NoneFeatureConfiguration> TENANEA_BUSH = registerVegetation(
            "tenanea_bush",
            inlineBuild("tenanea_bush", new TenaneaBushFeature()),
            12
    );
    public static final BCLFeature<Lumecorn, NoneFeatureConfiguration> LUMECORN = registerVegetation(
            "lumecorn",
            inlineBuild("lumecorn", new Lumecorn()),
            5
    );
    public static final BCLFeature<LargeAmaranitaFeature, NoneFeatureConfiguration> LARGE_AMARANITA = registerVegetation(
            "large_amaranita",
            inlineBuild("large_amaranita", new LargeAmaranitaFeature()),
            5
    );
    public static final BCLFeature<BushWithOuterFeature, BushWithOuterFeatureConfig> LUCERNIA_BUSH = registerVegetation(
            "lucernia_bush",
            BUSH_WITH_OUTER_FEATURE,
            new BushWithOuterFeatureConfig(
                    EndBlocks.LUCERNIA_LEAVES,
                    EndBlocks.LUCERNIA_OUTER_LEAVES,
                    EndBlocks.LUCERNIA.getBark()
            ),
            10
    );
    public static final BCLFeature<BushWithOuterFeature, BushWithOuterFeatureConfig> LUCERNIA_BUSH_RARE = registerVegetation(
            "lucernia_bush_rare",
            BUSH_WITH_OUTER_FEATURE,
            new BushWithOuterFeatureConfig(
                    EndBlocks.LUCERNIA_LEAVES,
                    EndBlocks.LUCERNIA_OUTER_LEAVES,
                    EndBlocks.LUCERNIA.getBark()
            ),
            1
    );
    public static final BCLFeature<NeonCactusFeature, NoneFeatureConfiguration> NEON_CACTUS = registerVegetation(
            "neon_cactus",
            inlineBuild("neon_cactus", new NeonCactusFeature()),
            2
    );

    // Plants //
    public static final BCLFeature<DoublePlantFeature, DoublePlantFeatureConfig> UMBRELLA_MOSS = registerVegetation(
            "umbrella_moss",
            new DoublePlantFeatureConfig(
                    EndBlocks.UMBRELLA_MOSS,
                    EndBlocks.UMBRELLA_MOSS_TALL,
                    5
            ),
            3
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> CREEPING_MOSS = registerVegetation(
            "creeping_moss",
            new SinglePlantFeatureConfig(
                    EndBlocks.CREEPING_MOSS,
                    5
            ),
            3
    );
    public static final BCLFeature<BlueVineFeature, ScatterFeatureConfig> BLUE_VINE = registerVegetation(
            "blue_vine",
            BLUE_VINE_FEATURE,
            ScatterFeatureConfig.blueVine(),
            1
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> CHORUS_GRASS = registerVegetation(
            "chorus_grass",
            new SinglePlantFeatureConfig(EndBlocks.CHORUS_GRASS, 4),
            3
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> CRYSTAL_GRASS = registerVegetation(
            "crystal_grass",
            new SinglePlantFeatureConfig(
                    EndBlocks.CRYSTAL_GRASS,
                    8,
                    false
            ),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> SHADOW_PLANT = registerVegetation(
            "shadow_plant",
            new SinglePlantFeatureConfig(EndBlocks.SHADOW_PLANT, 6),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> MURKWEED = registerVegetation(
            "murkweed",
            new SinglePlantFeatureConfig(EndBlocks.MURKWEED, 3),
            2
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> NEEDLEGRASS = registerVegetation(
            "needlegrass",
            new SinglePlantFeatureConfig(EndBlocks.NEEDLEGRASS, 3),
            1
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> SHADOW_BERRY = registerVegetation(
            "shadow_berry",
            new SinglePlantFeatureConfig(EndBlocks.SHADOW_BERRY, 2),
            1
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> BUSHY_GRASS = registerVegetation(
            "bushy_grass",
            new SinglePlantFeatureConfig(
                    EndBlocks.BUSHY_GRASS,
                    8,
                    false
            ),
            10
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> BUSHY_GRASS_WG = registerVegetation(
            "bushy_grass_wg",
            new SinglePlantFeatureConfig(EndBlocks.BUSHY_GRASS, 5),
            8
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> AMBER_GRASS = registerVegetation(
            "amber_grass",
            new SinglePlantFeatureConfig(EndBlocks.AMBER_GRASS, 6),
            7
    );
    public static final BCLFeature<LanceleafFeature, ScatterFeatureConfig> LANCELEAF = registerVegetation(
            "lanceleaf",
            LANCELEAF_FEATURE,
            ScatterFeatureConfig.lanceleaf(),
            2
    );
    public static final BCLFeature<GlowPillarFeature, ScatterFeatureConfig> GLOW_PILLAR = registerVegetation(
            "glow_pillar",
            GLOW_PILLAR_FEATURE,
            ScatterFeatureConfig.glowPillar(),
            1
    );
    public static final BCLFeature<DoublePlantFeature, DoublePlantFeatureConfig> TWISTED_UMBRELLA_MOSS = registerVegetation(
            "twisted_umbrella_moss",
            new DoublePlantFeatureConfig(
                    EndBlocks.TWISTED_UMBRELLA_MOSS,
                    EndBlocks.TWISTED_UMBRELLA_MOSS_TALL,
                    6
            ),
            3
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> JUNGLE_GRASS = registerVegetation(
            "jungle_grass",
            new SinglePlantFeatureConfig(
                    EndBlocks.JUNGLE_GRASS,
                    7,
                    3
            ),
            6
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> SMALL_JELLYSHROOM_FLOOR = registerVegetation(
            "small_jellyshroom_floor",
            new SinglePlantFeatureConfig(
                    EndBlocks.SMALL_JELLYSHROOM,
                    5,
                    5
            ),
            2
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> BLOSSOM_BERRY = registerVegetation(
            "blossom_berry",
            new SinglePlantFeatureConfig(
                    EndBlocks.BLOSSOM_BERRY,
                    4,
                    4
            ),
            3
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> BLOOMING_COOKSONIA = registerVegetation(
            "blooming_cooksonia",
            new SinglePlantFeatureConfig(
                    EndBlocks.BLOOMING_COOKSONIA,
                    5
            ),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> SALTEAGO = registerVegetation(
            "salteago",
            new SinglePlantFeatureConfig(EndBlocks.SALTEAGO, 5),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> VAIOLUSH_FERN = registerVegetation(
            "vaiolush_fern",
            new SinglePlantFeatureConfig(
                    EndBlocks.VAIOLUSH_FERN,
                    5
            ),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> FRACTURN = registerVegetation(
            "fracturn",
            new SinglePlantFeatureConfig(EndBlocks.FRACTURN, 5),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> UMBRELLA_MOSS_RARE = registerVegetation(
            "umbrella_moss_rare",
            new SinglePlantFeatureConfig(
                    EndBlocks.UMBRELLA_MOSS,
                    3
            ),
            2
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> CREEPING_MOSS_RARE = registerVegetation(
            "creeping_moss_rare",
            new SinglePlantFeatureConfig(
                    EndBlocks.CREEPING_MOSS,
                    3
            ),
            2
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> TWISTED_UMBRELLA_MOSS_RARE = registerVegetation(
            "twisted_umbrella_moss_rare",
            new SinglePlantFeatureConfig(
                    EndBlocks.TWISTED_UMBRELLA_MOSS,
                    3
            ),
            2
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> ORANGO = registerVegetation(
            "orango",
            new SinglePlantFeatureConfig(EndBlocks.ORANGO, 5),
            6
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> AERIDIUM = registerVegetation(
            "aeridium",
            new SinglePlantFeatureConfig(EndBlocks.AERIDIUM, 5, 4),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> LUTEBUS = registerVegetation(
            "lutebus",
            new SinglePlantFeatureConfig(EndBlocks.LUTEBUS, 5, 2),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> LAMELLARIUM = registerVegetation(
            "lamellarium",
            new SinglePlantFeatureConfig(EndBlocks.LAMELLARIUM, 5),
            6
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> SMALL_AMARANITA = registerVegetation(
            "small_amaranita",
            new SinglePlantFeatureConfig(
                    EndBlocks.SMALL_AMARANITA_MUSHROOM,
                    5,
                    5
            ),
            4
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> GLOBULAGUS = registerVegetation(
            "globulagus",
            new SinglePlantFeatureConfig(EndBlocks.GLOBULAGUS, 5, 3),
            6
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> CLAWFERN = registerVegetation(
            "clawfern",
            new SinglePlantFeatureConfig(EndBlocks.CLAWFERN, 5, 4),
            5
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> BOLUX_MUSHROOM = registerVegetation(
            "bolux_mushroom",
            new SinglePlantFeatureConfig(
                    EndBlocks.BOLUX_MUSHROOM,
                    5,
                    5
            ),
            2
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> CHORUS_MUSHROOM = registerVegetation(
            "chorus_mushroom",
            new SinglePlantFeatureConfig(
                    EndBlocks.CHORUS_MUSHROOM,
                    3,
                    5
            ),
            1
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> AMBER_ROOT = registerVegetation(
            "amber_root",
            new SinglePlantFeatureConfig(EndBlocks.AMBER_ROOT, 5, 5),
            1
    );
    //public static final BCLFeature PEARLBERRY = redisterVegetation("pearlberry", new SinglePlantFeatureConfig(EndBlocks.PEARLBERRY, 5, 5), 1);
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> INFLEXIA = registerVegetation(
            "inflexia",
            new SinglePlantFeatureConfig(
                    EndBlocks.INFLEXIA,
                    7,
                    false,
                    3
            ),
            16
    );
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> FLAMMALIX = registerVegetation(
            "flammalix",
            new SinglePlantFeatureConfig(
                    EndBlocks.FLAMMALIX,
                    3,
                    false,
                    7
            ),
            5
    );

    // Vines //
    public static final BCLFeature<VineFeature, VineFeatureConfig> DENSE_VINE = registerVegetation(
            "dense_vine",
            VINE_FEATURE, new VineFeatureConfig(EndBlocks.DENSE_VINE, 24),
            3
    );
    public static final BCLFeature<VineFeature, VineFeatureConfig> TWISTED_VINE = registerVegetation(
            "twisted_vine",
            VINE_FEATURE, new VineFeatureConfig(EndBlocks.TWISTED_VINE, 24),
            1
    );
    public static final BCLFeature<VineFeature, VineFeatureConfig> BULB_VINE = registerVegetation(
            "bulb_vine",
            VINE_FEATURE, new VineFeatureConfig(EndBlocks.BULB_VINE, 24),
            5
    );
    public static final BCLFeature<VineFeature, VineFeatureConfig> JUNGLE_VINE = registerVegetation(
            "jungle_vine",
            VINE_FEATURE, new VineFeatureConfig(EndBlocks.JUNGLE_VINE, 24),
            5
    );

    // Ceil plants
    public static final BCLFeature<SingleInvertedScatterFeature, SinglePlantFeatureConfig> SMALL_JELLYSHROOM_CEIL = registerVegetation(
            "small_jellyshroom_ceil",
            SINGLE_INVERTED_SCATTER_FEATURE, new SinglePlantFeatureConfig(
                    EndBlocks.SMALL_JELLYSHROOM,
                    8
            ),
            8
    );

    // Wall Plants //
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> PURPLE_POLYPORE = registerVegetation(
            "purple_polypore",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.PURPLE_POLYPORE,
                    3
            ),
            5
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> AURANT_POLYPORE = registerVegetation(
            "aurant_polypore",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.AURANT_POLYPORE,
                    3
            ),
            5
    );
    public static final BCLFeature<WallPlantFeature, WallPlantFeatureConfig> TAIL_MOSS = registerVegetation(
            "tail_moss",
            WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndBlocks.TAIL_MOSS, 3),
            15
    );
    public static final BCLFeature<WallPlantFeature, WallPlantFeatureConfig> CYAN_MOSS = registerVegetation(
            "cyan_moss",
            WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndBlocks.CYAN_MOSS, 3),
            15
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> TAIL_MOSS_WOOD = registerVegetation(
            "tail_moss_wood",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.TAIL_MOSS,
                    4
            ),
            25
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> CYAN_MOSS_WOOD = registerVegetation(
            "cyan_moss_wood",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.CYAN_MOSS,
                    4
            ),
            25
    );
    public static final BCLFeature<WallPlantFeature, WallPlantFeatureConfig> TWISTED_MOSS = registerVegetation(
            "twisted_moss",
            WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndBlocks.TWISTED_MOSS, 6),
            15
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> TWISTED_MOSS_WOOD = registerVegetation(
            "twisted_moss_wood",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.TWISTED_MOSS,
                    6
            ),
            25
    );
    public static final BCLFeature<WallPlantFeature, WallPlantFeatureConfig> BULB_MOSS = registerVegetation(
            "bulb_moss",
            WALL_PLANT_FEATURE, new WallPlantFeatureConfig(EndBlocks.BULB_MOSS, 6),
            1
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> BULB_MOSS_WOOD = registerVegetation(
            "bulb_moss_wood",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.BULB_MOSS,
                    6
            ),
            15
    );
    public static final BCLFeature<WallPlantFeature, WallPlantFeatureConfig> SMALL_JELLYSHROOM_WALL = registerVegetation(
            "small_jellyshroom_wall",
            WALL_PLANT_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.SMALL_JELLYSHROOM,
                    4
            ),
            4
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> SMALL_JELLYSHROOM_WOOD = registerVegetation(
            "small_jellyshroom_wood",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.SMALL_JELLYSHROOM,
                    4
            ),
            8
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> JUNGLE_FERN_WOOD = registerVegetation(
            "jungle_fern_wood",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(
                    EndBlocks.JUNGLE_FERN,
                    3
            ),
            12
    );
    public static final BCLFeature<WallPlantFeature, WallPlantFeatureConfig> RUSCUS = registerVegetation(
            "ruscus",
            WALL_PLANT_FEATURE,
            new WallPlantFeatureConfig(EndBlocks.RUSCUS, 6),
            10
    );
    public static final BCLFeature<WallPlantOnLogFeature, WallPlantFeatureConfig> RUSCUS_WOOD = registerVegetation(
            "ruscus_wood",
            WALL_PLANT_ON_LOG_FEATURE, new WallPlantFeatureConfig(EndBlocks.RUSCUS, 6),
            10
    );

    // Sky plants
    public static final BCLFeature<FilaluxFeature, ScatterFeatureConfig> FILALUX = registerVegetation(
            "filalux",
            FILALUX_FEATURE,
            ScatterFeatureConfig.filalux(),
            1
    );

    // Water //
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> BUBBLE_CORAL = registerVegetation(
            "bubble_coral",
            UNDERWATER_PLANT_FEATURE, new SinglePlantFeatureConfig(
                    EndBlocks.BUBBLE_CORAL,
                    6
            ),
            10
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> BUBBLE_CORAL_RARE = registerVegetation(
            "bubble_coral_rare",
            UNDERWATER_PLANT_FEATURE, new SinglePlantFeatureConfig(
                    EndBlocks.BUBBLE_CORAL,
                    3
            ),
            4
    );
    public static final BCLFeature<EndLilyFeature, ScatterFeatureConfig> END_LILY = registerVegetation(
            "end_lily",
            END_LILY_FEATURE,
            new ScatterFeatureConfig(6),
            10
    );
    public static final BCLFeature<EndLilyFeature, ScatterFeatureConfig> END_LILY_RARE = registerVegetation(
            "end_lily_rare",
            END_LILY_FEATURE,
            new ScatterFeatureConfig(3),
            4
    );
    public static final BCLFeature<EndLotusFeature, ScatterFeatureConfig> END_LOTUS = registerVegetation(
            "end_lotus",
            END_LOTUS_FEATURE,
            new ScatterFeatureConfig(7),
            6
    );
    public static final BCLFeature<EndLotusLeafFeature, ScatterFeatureConfig> END_LOTUS_LEAF = registerVegetation(
            "end_lotus_leaf",
            END_LOTUS_LEAF_FEATURE, new ScatterFeatureConfig(20),
            4
    );
    public static final BCLFeature<HydraluxFeature, ScatterFeatureConfig> HYDRALUX = registerVegetation(
            "hydralux",
            HYDRALUX_FEATURE,
            new ScatterFeatureConfig(5),
            5
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> POND_ANEMONE = registerVegetation(
            "pond_anemone",
            UNDERWATER_PLANT_FEATURE, new SinglePlantFeatureConfig(
                    EndBlocks.POND_ANEMONE,
                    6
            ),
            10
    );

    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> CHARNIA_RED = registerVegetation(
            "charnia_red",
            CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndBlocks.CHARNIA_RED),
            10
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> CHARNIA_PURPLE = registerVegetation(
            "charnia_purple",
            CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndBlocks.CHARNIA_PURPLE),
            10
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> CHARNIA_CYAN = registerVegetation(
            "charnia_cyan",
            CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndBlocks.CHARNIA_CYAN),
            10
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> CHARNIA_LIGHT_BLUE = registerVegetation(
            "charnia_light_blue",
            CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndBlocks.CHARNIA_LIGHT_BLUE),
            10
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> CHARNIA_ORANGE = registerVegetation(
            "charnia_orange",
            CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndBlocks.CHARNIA_ORANGE),
            10
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> CHARNIA_GREEN = registerVegetation(
            "charnia_green",
            CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndBlocks.CHARNIA_GREEN),
            10
    );
    public static final BCLFeature<MengerSpongeFeature, ScatterFeatureConfig> MENGER_SPONGE = registerVegetation(
            "menger_sponge",
            MENGER_SPONGE_FEATURE,
            new ScatterFeatureConfig(5),
            1
    );
    public static final BCLFeature<UnderwaterPlantFeature, SinglePlantFeatureConfig> CHARNIA_RED_RARE = registerVegetation(
            "charnia_red_rare",
            CHARNIA_FEATURE, SinglePlantFeatureConfig.charnia(EndBlocks.CHARNIA_RED),
            2
    );
    public static final BCLFeature<BiomeIslandFeature, NoneFeatureConfiguration> BIOME_ISLAND = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("overworld_island"),
                    inlineBuild("overworld_island", new BiomeIslandFeature())
            )
            .build()
            .place()
            .decoration(Decoration.RAW_GENERATION)
            .build();
    public static final BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> FLAMAEA = registerVegetation(
            "flamaea",
            new SinglePlantFeatureConfig(EndBlocks.FLAMAEA, 12, false, 5),
            20
    );

    public static final EndLakeFeature END_LAKE_FEATURE = inlineBuild("end_lake", new EndLakeFeature());
    // Terrain //
    public static final BCLFeature<EndLakeFeature, NoneFeatureConfiguration> END_LAKE = registerLake(
            "end_lake",
            END_LAKE_FEATURE,
            4
    );
    public static final BCLFeature<EndLakeFeature, NoneFeatureConfiguration> END_LAKE_NORMAL = registerLake(
            "end_lake_normal",
            END_LAKE_FEATURE,
            20
    );
    public static final BCLFeature<EndLakeFeature, NoneFeatureConfiguration> END_LAKE_RARE = registerLake(
            "end_lake_rare",
            END_LAKE_FEATURE,
            40
    );
    public static final BCLFeature<DesertLakeFeature, NoneFeatureConfiguration> DESERT_LAKE = registerLake(
            "desert_lake",
            inlineBuild("desert_lake", new DesertLakeFeature()),
            8
    );
    public static final BCLFeature<RoundCaveFeature, NoneFeatureConfiguration> ROUND_CAVE = registerRawGen(
            "round_cave",
            inlineBuild("round_cave", new RoundCaveFeature()),
            2
    );
    public static final BCLFeature<SpireFeature, NoneFeatureConfiguration> SPIRE = registerRawGen(
            "spire",
            inlineBuild("spire", new SpireFeature()),
            4
    );
    public static final BCLFeature<FloatingSpireFeature, NoneFeatureConfiguration> FLOATING_SPIRE = registerRawGen(
            "floating_spire",
            inlineBuild("floating_spire", new FloatingSpireFeature()),
            8
    );
    public static final BCLFeature<GeyserFeature, NoneFeatureConfiguration> GEYSER = registerRawGen(
            "geyser",
            inlineBuild("geyser", new GeyserFeature()),
            8
    );
    public static final BCLFeature<SulphuricLakeFeature, NoneFeatureConfiguration> SULPHURIC_LAKE = registerLake(
            "sulphuric_lake",
            inlineBuild("sulphuric_lake", new SulphuricLakeFeature()),
            8
    );
    public static final BCLFeature<SulphuricCaveFeature, NoneFeatureConfiguration> SULPHURIC_CAVE = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("sulphuric_cave"),
                    inlineBuild("sulphuric_cave", new SulphuricCaveFeature())
            )
            .build()
            .place()
            .decoration(Decoration.RAW_GENERATION)
            .count(2)
            .squarePlacement()
            .onlyInBiome()
            .build();

    public static final IceStarFeature ICE_STAR_FEATURE = inlineBuild("ice_star", new IceStarFeature());
    public static final BCLFeature<IceStarFeature, IceStarFeatureConfig> ICE_STAR = registerRawGen(
            "ice_star",
            ICE_STAR_FEATURE,
            new IceStarFeatureConfig(5, 15, 10, 25),
            15
    );
    public static final BCLFeature<IceStarFeature, IceStarFeatureConfig> ICE_STAR_SMALL = registerRawGen(
            "ice_star_small",
            ICE_STAR_FEATURE,
            new IceStarFeatureConfig(3, 5, 7, 12),
            8
    );
    public static final BCLFeature<SurfaceVentFeature, NoneFeatureConfiguration> SURFACE_VENT = registerChanced(
            "surface_vent",
            inlineBuild("surface_vent", new SurfaceVentFeature()),
            4
    );
    public static final BCLFeature<SulphurHillFeature, NoneFeatureConfiguration> SULPHUR_HILL = registerChanced(
            "sulphur_hill",
            inlineBuild("sulphur_hill", new SulphurHillFeature()),
            8
    );
    public static final BCLFeature<ObsidianPillarBasementFeature, NoneFeatureConfiguration> OBSIDIAN_PILLAR_BASEMENT = registerChanced(
            "obsidian_pillar_basement",
            inlineBuild("obsidian_pillar_basement", new ObsidianPillarBasementFeature()),
            8
    );
    public static final BCLFeature<ObsidianBoulderFeature, NoneFeatureConfiguration> OBSIDIAN_BOULDER = registerChanced(
            "obsidian_boulder",
            inlineBuild("obsidian_boulder", new ObsidianBoulderFeature()),
            10
    );
    public static final BCLFeature<FallenPillarFeature, NoneFeatureConfiguration> FALLEN_PILLAR = registerChanced(
            "fallen_pillar",
            inlineBuild("fallen_pillar", new FallenPillarFeature()),
            20
    );
    public static final BCLFeature<TunelCaveFeature, NoneFeatureConfiguration> TUNEL_CAVE = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("tunel_cave"),
                    inlineBuild("tunel_cave", new TunelCaveFeature())
            ).build()
            .place()
            .decoration(Decoration.RAW_GENERATION)
            .count(1)
            .onlyInBiome()
            .build();
    public static final BCLFeature<ArchFeature, ArchFeatureConfig> UMBRALITH_ARCH = registerChanced(
            "umbralith_arch",
            ARCH_FEATURE, new ArchFeatureConfig(
                    EndBlocks.UMBRALITH.stone,
                    ArchFeatureConfig.SurfaceFunction.UMBRA_VALLEY
            ),
            10
    );
    public static final BCLFeature<ThinArchFeature, ThinArchFeatureConfig> THIN_UMBRALITH_ARCH = registerChanced(
            "thin_umbralith_arch",
            THIN_ARCH_FEATURE,
            new ThinArchFeatureConfig(EndBlocks.UMBRALITH.stone),
            15
    );

    // Ores //
    public static final BCLFeature<OreFeature, OreConfiguration> THALLASIUM_ORE = registerOre(
            "thallasium_ore",
            EndBlocks.THALLASIUM.ore,
            24,
            8
    );
    public static final BCLFeature<OreFeature, OreConfiguration> ENDER_ORE = registerOre(
            "ender_ore",
            EndBlocks.ENDER_ORE,
            12,
            4
    );
    public static final BCLFeature<OreFeature, OreConfiguration> AMBER_ORE = registerOre(
            "amber_ore",
            EndBlocks.AMBER_ORE,
            60,
            6
    );
    public static final BCLFeature<OreFeature, OreConfiguration> DRAGON_BONE_BLOCK_ORE = registerOre(
            "dragon_bone_ore",
            EndBlocks.DRAGON_BONE_BLOCK,
            24,
            8
    );
    public static final BCLFeature<OreLayerFeature, OreLayerFeatureConfig> VIOLECITE_LAYER = registerLayer(
            "violecite_layer",
            EndBlocks.VIOLECITE,
            15,
            16,
            128,
            8
    );

    public static final BCLFeature<OreLayerFeature, OreLayerFeatureConfig> FLAVOLITE_LAYER = registerLayer(
            "flavolite_layer",
            EndBlocks.FLAVOLITE,
            12,
            16,
            128,
            6
    );

    // Buildings

    // Mobs
    public static final BCLFeature<SilkMothNestFeature, NoneFeatureConfiguration> SILK_MOTH_NEST = registerChanced(
            "silk_moth_nest",
            inlineBuild("silk_moth_nest", new SilkMothNestFeature()),
            2
    );

    // Caves
    public static final BCLConfigureFeature<SmaragdantCrystalFeature, NoneFeatureConfiguration> SMARAGDANT_CRYSTAL = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("smaragdant_crystal"),
                    inlineBuild("smaragdant_crystal", new SmaragdantCrystalFeature())
            )
            .build();
    public static final BCLConfigureFeature<SingleBlockFeature, SimpleBlockConfiguration> SMARAGDANT_CRYSTAL_SHARD = BCLFeatureBuilder
            .start(BetterEnd.makeID("smaragdant_crystal_shard"), SINGLE_BLOCK_FEATURE)
            .configuration(new SimpleBlockConfiguration(SimpleStateProvider.simple(EndBlocks.SMARAGDANT_CRYSTAL_SHARD)))
            .build();

    public static final BCLConfigureFeature<BigAuroraCrystalFeature, NoneFeatureConfiguration> BIG_AURORA_CRYSTAL = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("big_aurora_crystal"),
                    inlineBuild("big_aurora_crystal", new BigAuroraCrystalFeature())
            )
            .build();
    public static final BCLConfigureFeature<BushFeature, BushFeatureConfig> CAVE_BUSH = BCLFeatureBuilder
            .start(BetterEnd.makeID("cave_bush"), BUSH_FEATURE)
            .configuration(new BushFeatureConfig(EndBlocks.CAVE_BUSH, EndBlocks.CAVE_BUSH))
            .build();
    public static final BCLConfigureFeature<SingleBlockFeature, SimpleBlockConfiguration> CAVE_GRASS = BCLFeatureBuilder
            .start(BetterEnd.makeID("cave_grass"), SINGLE_BLOCK_FEATURE)
            .configuration(new SimpleBlockConfiguration(SimpleStateProvider.simple(EndBlocks.CAVE_GRASS)))
            .build();
    public static final BCLConfigureFeature<VineFeature, VineFeatureConfig> RUBINEA = BCLFeatureBuilder
            .start(BetterEnd.makeID("rubinea"), VINE_FEATURE)
            .configuration(new VineFeatureConfig(EndBlocks.RUBINEA, 8))
            .build();

    public static final BCLConfigureFeature<VineFeature, VineFeatureConfig> MAGNULA = BCLFeatureBuilder
            .start(BetterEnd.makeID("magnula"), VINE_FEATURE)
            .configuration(new VineFeatureConfig(EndBlocks.MAGNULA, 8))
            .build();

    public static final BCLConfigureFeature<StalactiteFeature, StalactiteFeatureConfig> END_STONE_STALACTITE = BCLFeatureBuilder
            .start(BetterEnd.makeID("end_stone_stalactite"), STALACTITE_FEATURE)
            .configuration(new StalactiteFeatureConfig(true, EndBlocks.END_STONE_STALACTITE, Blocks.END_STONE))
            .build();


    public static final BCLConfigureFeature<StalactiteFeature, StalactiteFeatureConfig> END_STONE_STALAGMITE = BCLFeatureBuilder
            .start(BetterEnd.makeID("end_stone_stalagmite"), STALACTITE_FEATURE)
            .configuration(new StalactiteFeatureConfig(false, EndBlocks.END_STONE_STALACTITE, Blocks.END_STONE))
            .build();
    public static final BCLConfigureFeature<StalactiteFeature, StalactiteFeatureConfig> END_STONE_STALACTITE_CAVEMOSS = BCLFeatureBuilder
            .start(BetterEnd.makeID("end_stone_stalactite_cavemoss"), STALACTITE_FEATURE)
            .configuration(new StalactiteFeatureConfig(
                    true,
                    EndBlocks.END_STONE_STALACTITE_CAVEMOSS,
                    Blocks.END_STONE,
                    EndBlocks.CAVE_MOSS
            ))
            .build();
    public static final BCLConfigureFeature<StalactiteFeature, StalactiteFeatureConfig> END_STONE_STALAGMITE_CAVEMOSS = BCLFeatureBuilder
            .start(BetterEnd.makeID("end_stone_stalagmite_cavemoss"), STALACTITE_FEATURE)
            .configuration(new StalactiteFeatureConfig(
                    false,
                    EndBlocks.END_STONE_STALACTITE_CAVEMOSS,
                    EndBlocks.CAVE_MOSS
            ))
            .build();
    public static final BCLConfigureFeature<CavePumpkinFeature, NoneFeatureConfiguration> CAVE_PUMPKIN = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("cave_pumpkin"),
                    inlineBuild("cave_pumpkin", new CavePumpkinFeature())
            )
            .build();


    private static final Holder<PlacedFeature> BONEMEAL_END_MOSS_NOT_GLOWING_GRASSLANDS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_end_moss_not_glowing_grasslands"))
            .add(EndBlocks.CREEPING_MOSS, 10)
            .add(EndBlocks.UMBRELLA_MOSS, 10)
            .inlinePlace().build().placedFeature;

    private static final Holder<PlacedFeature> BONEMEAL_END_MOSS_GLOWING_GRASSLANDS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_end_moss_glowing_grasslands"))
            .add(EndBlocks.CREEPING_MOSS, 10)
            .add(EndBlocks.UMBRELLA_MOSS, 10)
            .add(EndBlocks.BLOOMING_COOKSONIA, 100)
            .add(EndBlocks.VAIOLUSH_FERN, 100)
            .add(EndBlocks.FRACTURN, 100)
            .add(EndBlocks.SALTEAGO, 100)
            .add(EndBlocks.TWISTED_UMBRELLA_MOSS, 10)
            .inlinePlace().build().placedFeature;

    public static final BCLConfigureFeature<Feature<ConditionFeatureConfig>, ConditionFeatureConfig> BONEMEAL_END_MOSS = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("bonemeal_end_moss"),
                    BCLFeature.CONDITION
            )
            .configuration(new ConditionFeatureConfig(
                    InBiome.matchingID(EndBiomes.GLOWING_GRASSLANDS.location()),
                    BONEMEAL_END_MOSS_GLOWING_GRASSLANDS,
                    BONEMEAL_END_MOSS_NOT_GLOWING_GRASSLANDS
            ))
            .build();

    private static final Holder<PlacedFeature> BONEMEAL_RUTISCUS_NOT_LANTERN_WOODS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_rutiscus_not_lantern_woods"))
            .add(EndBlocks.ORANGO, 100)
            .add(EndBlocks.AERIDIUM, 20)
            .add(EndBlocks.LUTEBUS, 20)
            .add(EndBlocks.LAMELLARIUM, 100)
            .inlinePlace()
            .build().placedFeature;

    private static final Holder<PlacedFeature> BONEMEAL_RUTISCUS_LANTERN_WOODS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_rutiscus_lantern_woods"))
            .add(EndBlocks.AERIDIUM, 20)
            .add(EndBlocks.BOLUX_MUSHROOM, 5)
            .add(EndBlocks.LAMELLARIUM, 100)
            .inlinePlace()
            .build().placedFeature;

    public static final BCLConfigureFeature<Feature<ConditionFeatureConfig>, ConditionFeatureConfig> BONEMEAL_RUTISCUS = BCLFeatureBuilder
            .start(
                    BetterEnd.makeID("bonemeal_rutiscus"),
                    BCLFeature.CONDITION
            )
            .configuration(new ConditionFeatureConfig(
                    InBiome.matchingID(EndBiomes.LANTERN_WOODS.location()),
                    BONEMEAL_RUTISCUS_LANTERN_WOODS,
                    BONEMEAL_RUTISCUS_NOT_LANTERN_WOODS
            ))
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_END_MYCELIUM = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_end_mycelium"))
            .add(EndBlocks.CREEPING_MOSS, 100)
            .add(EndBlocks.UMBRELLA_MOSS, 100)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_JUNGLE_MOSS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_jungle_moss"))
            .add(EndBlocks.JUNGLE_GRASS, 100)
            .add(EndBlocks.TWISTED_UMBRELLA_MOSS, 100)
            .add(EndBlocks.SMALL_JELLYSHROOM, 10)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_SANGNUM = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_sangnum"))
            .add(EndBlocks.CLAWFERN, 100)
            .add(EndBlocks.GLOBULAGUS, 100)
            .add(EndBlocks.SMALL_AMARANITA_MUSHROOM, 10)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_MOSSY_DRAGON_BONE = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_mossy_dragon_bone"))
            .add(EndBlocks.CLAWFERN, 100)
            .add(EndBlocks.GLOBULAGUS, 100)
            .add(EndBlocks.SMALL_AMARANITA_MUSHROOM, 10)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_MOSSY_OBSIDIAN = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_mossy_obsidian"))
            .add(EndBlocks.CLAWFERN, 100)
            .add(EndBlocks.GLOBULAGUS, 100)
            .add(EndBlocks.SMALL_AMARANITA_MUSHROOM, 10)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_CAVE_MOSS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_cave_moss"))
            .add(EndBlocks.CAVE_GRASS, 100)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_CHORUS_NYLIUM = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_chorus_nylium"))
            .add(EndBlocks.CHORUS_GRASS, 100)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_CRYSTAL_MOSS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_crystal_moss"))
            .add(EndBlocks.CRYSTAL_GRASS, 100)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_SHADOW_GRASS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_shadow_grass"))
            .add(EndBlocks.SHADOW_PLANT, 100)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_PINK_MOSS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_pink_moss"))
            .add(EndBlocks.BUSHY_GRASS, 100)
            .build();

    public static final BCLConfigureFeature<RandomPatchFeature, RandomPatchConfiguration> BONEMEAL_AMBER_MOSS = BCLFeatureBuilder
            .startBonemealPatch(BetterEnd.makeID("bonemeal_amber_moss"))
            .add(EndBlocks.AMBER_GRASS, 100)
            .build();


    public static <F extends Feature<FC>, FC extends FeatureConfiguration> F inlineBuild(String name, F feature) {
        ResourceLocation l = BetterEnd.makeID(name);

        final Registry<Feature<?>> features;
        if (WorldBootstrap.getLastRegistryAccess() != null) {
            features = WorldBootstrap.getLastRegistryAccess().registryOrThrow(Registries.FEATURE);
        } else {
            features = BuiltInRegistries.FEATURE;
        }

        if (features.containsKey(l)) {
            return (F) features.get(l);
        }


        return BCLFeature.register(l, feature);
    }

    private static BCLFeature<DoublePlantFeature, DoublePlantFeatureConfig> registerVegetation(
            String name,
            DoublePlantFeatureConfig config,
            int density
    ) {
        return registerVegetation(name, DOUBLE_PLANT_FEATURE, config, density);
    }

    private static BCLFeature<SinglePlantFeature, SinglePlantFeatureConfig> registerVegetation(
            String name,
            SinglePlantFeatureConfig config,
            int density
    ) {
        return registerVegetation(name, SINGLE_PLANT_FEATURE, config, density);
    }

    private static <F extends Feature<NoneFeatureConfiguration>> BCLFeature<F, NoneFeatureConfiguration> registerVegetation(
            String name,
            F feature,
            int density
    ) {
        return registerVegetation(name, feature, NoneFeatureConfiguration.NONE, density);
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> BCLFeature<F, FC> registerVegetation(
            String name,
            F feature,
            FC config,
            int density
    ) {
        ResourceLocation id = BetterEnd.makeID(name);
        return BCLFeatureBuilder.start(id, feature)
                                .configuration(config)
                                .build()
                                .place()
                                .onEveryLayerMax(density)
                                .onlyInBiome()
                                .build();
    }

    private static <F extends Feature<NoneFeatureConfiguration>> BCLFeature<F, NoneFeatureConfiguration> registerRawGen(
            String name,
            F feature,
            int chance
    ) {
        return registerRawGen(name, feature, FeatureConfiguration.NONE, chance);
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> BCLFeature<F, FC> registerRawGen(
            String name,
            F feature,
            FC config,
            int chance
    ) {
        return BCLFeatureBuilder
                .start(BetterEnd.makeID(name), feature)
                .configuration(config)
                .build()
                .place()
                .decoration(Decoration.RAW_GENERATION)
                .onceEvery(chance)
                .squarePlacement()
                .onlyInBiome()
                .build();
    }

    private static <F extends Feature<NoneFeatureConfiguration>> BCLFeature<F, NoneFeatureConfiguration> registerLake(
            String name,
            F feature,
            int chance
    ) {
        return BCLFeatureBuilder
                .start(BetterEnd.makeID(name), feature)
                .build()
                .place()
                .decoration(Decoration.LAKES)
                .onceEvery(chance)
                .squarePlacement()
                .onlyInBiome()
                .build();
    }

    private static <F extends Feature<NoneFeatureConfiguration>> BCLFeature<F, NoneFeatureConfiguration> registerChanced(
            String name,
            F feature,
            int chance
    ) {
        return registerChanced(name, feature, FeatureConfiguration.NONE, chance);
    }

    private static <F extends Feature<FC>, FC extends FeatureConfiguration> BCLFeature<F, FC> registerChanced(
            String name,
            F feature,
            FC config,
            int chance
    ) {
        return
                BCLFeatureBuilder
                        .start(BetterEnd.makeID(name), feature)
                        .configuration(config)
                        .build()
                        .place()
                        .decoration(Decoration.SURFACE_STRUCTURES)
                        .onceEvery(chance)
                        .squarePlacement()
                        .onlyInBiome()
                        .build();
    }

    private static BCLFeature<OreFeature, OreConfiguration> registerOre(
            String name,
            Block blockOre,
            int veins,
            int veinSize
    ) {
        return BCLFeatureBuilder
                .startOre(BetterEnd.makeID(name))
                .add(Blocks.END_STONE, blockOre)
                .veinSize(veinSize)
                .discardChanceOnAirExposure(0)
                .build()
                .place()
                .decoration(Decoration.UNDERGROUND_ORES)
                .count(veins)
                .randomHeight8FromFloorCeil()
                .squarePlacement()
                .onlyInBiome()
                .build();
    }

    private static BCLFeature<OreLayerFeature, OreLayerFeatureConfig> registerLayer(
            String name,
            Block block,
            float radius,
            int minY,
            int maxY,
            int count
    ) {
        OreLayerFeature layer = inlineBuild("ore_layer", new OreLayerFeature());
        return BCLFeatureBuilder
                .start(BetterEnd.makeID(name), layer)
                .configuration(new OreLayerFeatureConfig(block.defaultBlockState(), radius, minY, maxY))
                .build()
                .place()
                .onlyInBiome()
                .decoration(GenerationStep.Decoration.UNDERGROUND_ORES)
                .count(count)
                .build();
    }

    private static BCLFeature<OreLayerFeature, OreLayerFeatureConfig> registerLayer(
            String name,
            StoneMaterial material,
            float radius,
            int minY,
            int maxY,
            int count
    ) {
        return registerLayer(name, material.stone, radius, minY, maxY, count);
    }

    public static void addBiomeFeatures(ResourceLocation id, Holder<Biome> biome) {
        if (!BetterEnd.MOD_ID.equals(id.getNamespace())) {
            BiomeAPI.addBiomeFeature(biome, FLAVOLITE_LAYER);
            BiomeAPI.addBiomeFeature(biome, THALLASIUM_ORE);
            BiomeAPI.addBiomeFeature(biome, ENDER_ORE);
            // like the old feature, the crashed ship also comes to the End biomes of other mods (not to the
            // main island)
            if (!id.equals(Biomes.THE_END.location())) {
                TagManager.BIOMES.add(EndStructures.CRASHED_SHIP.biomeTag, biome.value());
            }

            BCLBiome bclbiome = BiomeAPI.getBiome(id);
            boolean hasCaves = !(bclbiome instanceof EndCaveBiome);
            if (!(bclbiome instanceof EndCaveBiome) && bclbiome instanceof EndBiome endBiome) {
                hasCaves = endBiome.hasCaves();
            }

            if (hasCaves && !BiomeAPI.wasRegisteredAsEndVoidBiome(id) /*!BiomeAPI.END_VOID_BIOME_PICKER.containsImmutable(id)*/) {
                BiomeAPI.addBiomeFeature(biome, ROUND_CAVE);
                BiomeAPI.addBiomeFeature(biome, TUNEL_CAVE);
            }
        }
    }

    public static BCLBiomeBuilder addDefaultFeatures(
            ResourceLocation biomeID,
            BCLBiomeBuilder builder,
            boolean hasCaves
    ) {
        builder.feature(THALLASIUM_ORE);
        builder.feature(ENDER_ORE);
        builder.structure(EndStructures.CRASHED_SHIP);

        if (hasCaves) {
            builder.feature(ROUND_CAVE);
            builder.feature(TUNEL_CAVE);
        }

        return builder;
    }

    public static void register() {
    }
}
