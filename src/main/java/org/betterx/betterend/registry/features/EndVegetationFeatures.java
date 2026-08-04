package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class EndVegetationFeatures {
    //Trees
    public static final PlacedConfiguredFeatureKey MOSSY_GLOWSHROOM = PlacedFeatureManager.createKey(EndConfiguredVegetation.MOSSY_GLOWSHROOM);
    public static final PlacedConfiguredFeatureKey PYTHADENDRON_TREE = PlacedFeatureManager.createKey(EndConfiguredVegetation.PYTHADENDRON_TREE);
    public static final PlacedConfiguredFeatureKey LACUGROVE = PlacedFeatureManager.createKey(EndConfiguredVegetation.LACUGROVE);
    public static final PlacedConfiguredFeatureKey DRAGON_TREE = PlacedFeatureManager.createKey(EndConfiguredVegetation.DRAGON_TREE);
    public static final PlacedConfiguredFeatureKey TENANEA = PlacedFeatureManager.createKey(EndConfiguredVegetation.TENANEA);
    public static final PlacedConfiguredFeatureKey HELIX_TREE = PlacedFeatureManager.createKey(EndConfiguredVegetation.HELIX_TREE);
    public static final PlacedConfiguredFeatureKey DRAGON_HELIX_TREE = PlacedFeatureManager.createKey(EndConfiguredVegetation.DRAGON_HELIX_TREE);
    public static final PlacedConfiguredFeatureKey UMBRELLA_TREE = PlacedFeatureManager.createKey(EndConfiguredVegetation.UMBRELLA_TREE);
    public static final PlacedConfiguredFeatureKey JELLYSHROOM = PlacedFeatureManager.createKey(EndConfiguredVegetation.JELLYSHROOM);
    public static final PlacedConfiguredFeatureKey GIGANTIC_AMARANITA = PlacedFeatureManager.createKey(EndConfiguredVegetation.GIGANTIC_AMARANITA);
    public static final PlacedConfiguredFeatureKey LUCERNIA = PlacedFeatureManager.createKey(EndConfiguredVegetation.LUCERNIA);

    //Bushes
    public static final PlacedConfiguredFeatureKey TENANEA_BUSH = PlacedFeatureManager.createKey(EndConfiguredVegetation.TENANEA_BUSH);
    public static final PlacedConfiguredFeatureKey LUMECORN = PlacedFeatureManager.createKey(EndConfiguredVegetation.LUMECORN);
    public static final PlacedConfiguredFeatureKey LARGE_AMARANITA = PlacedFeatureManager.createKey(EndConfiguredVegetation.LARGE_AMARANITA);
    public static final PlacedFeatureKey NEON_CACTUS = createVegetationKey("neon_cactus");
    public static final PlacedFeatureKey PYTHADENDRON_BUSH = createVegetationKey("pythadendron_bush");
    public static final PlacedFeatureKey DRAGON_TREE_BUSH = createVegetationKey("dragon_tree_bush");
    public static final PlacedFeatureKey LUCERNIA_BUSH = createVegetationKey("lucernia_bush");
    public static final PlacedFeatureKey LUCERNIA_BUSH_RARE = createVegetationKey("lucernia_bush_rare");

    //Vines
    public static final PlacedFeatureKey DENSE_VINE = createVegetationKey("dense_vine");
    public static final PlacedFeatureKey TWISTED_VINE = createVegetationKey("twisted_vine");
    public static final PlacedFeatureKey BULB_VINE = createVegetationKey("bulb_vine");
    public static final PlacedFeatureKey JUNGLE_VINE = createVegetationKey("jungle_vine");
    public static final PlacedFeatureKey BLUE_VINE = createVegetationKey("blue_vine");

    // Ceil plants
    public static final PlacedFeatureKey SMALL_JELLYSHROOM_CEIL = createVegetationKey("small_jellyshroom_ceil");

    // Wall Plants
    public static final PlacedFeatureKey PURPLE_POLYPORE = createVegetationKey("purple_polypore");
    public static final PlacedFeatureKey AURANT_POLYPORE = createVegetationKey("aurant_polypore");
    public static final PlacedFeatureKey TAIL_MOSS = createVegetationKey("tail_moss");
    public static final PlacedFeatureKey CYAN_MOSS = createVegetationKey("cyan_moss");
    public static final PlacedFeatureKey TAIL_MOSS_WOOD = createVegetationKey("tail_moss_wood");
    public static final PlacedFeatureKey CYAN_MOSS_WOOD = createVegetationKey("cyan_moss_wood");
    public static final PlacedFeatureKey TWISTED_MOSS = createVegetationKey("twisted_moss");
    public static final PlacedFeatureKey TWISTED_MOSS_WOOD = createVegetationKey("twisted_moss_wood");
    public static final PlacedFeatureKey BULB_MOSS = createVegetationKey("bulb_moss");
    public static final PlacedFeatureKey BULB_MOSS_WOOD = createVegetationKey("bulb_moss_wood");
    public static final PlacedFeatureKey SMALL_JELLYSHROOM_WALL = createVegetationKey("small_jellyshroom_wall");
    public static final PlacedFeatureKey SMALL_JELLYSHROOM_WOOD = createVegetationKey("small_jellyshroom_wood");
    public static final PlacedFeatureKey JUNGLE_FERN_WOOD = createVegetationKey("jungle_fern_wood");
    public static final PlacedFeatureKey RUSCUS = createVegetationKey("ruscus");
    public static final PlacedFeatureKey RUSCUS_WOOD = createVegetationKey("ruscus_wood");

    // Sky plants
    public static final PlacedFeatureKey FILALUX = createVegetationKey("filalux");

    // Water
    public static final PlacedFeatureKey BUBBLE_CORAL = createVegetationKey("bubble_coral");
    public static final PlacedFeatureKey BUBBLE_CORAL_RARE = createVegetationKey("bubble_coral_rare");
    public static final PlacedFeatureKey END_LILY = createVegetationKey("end_lily");
    public static final PlacedFeatureKey END_LILY_RARE = createVegetationKey("end_lily_rare");
    public static final PlacedFeatureKey END_LOTUS = createVegetationKey("end_lotus");
    public static final PlacedFeatureKey END_LOTUS_LEAF = createVegetationKey("end_lotus_leaf");
    public static final PlacedFeatureKey HYDRALUX = createVegetationKey("hydralux");
    public static final PlacedFeatureKey POND_ANEMONE = createVegetationKey("pond_anemone");
    public static final PlacedFeatureKey MENGER_SPONGE = createVegetationKey("menger_sponge");
    public static final PlacedFeatureKey FLAMAEA = createVegetationKey("flamaea");
    public static final PlacedFeatureKey CHARNIA_RED = createVegetationKey("charnia_red");
    public static final PlacedFeatureKey CHARNIA_PURPLE = createVegetationKey("charnia_purple");
    public static final PlacedFeatureKey CHARNIA_CYAN = createVegetationKey("charnia_cyan");
    public static final PlacedFeatureKey CHARNIA_LIGHT_BLUE = createVegetationKey("charnia_light_blue");
    public static final PlacedFeatureKey CHARNIA_ORANGE = createVegetationKey("charnia_orange");
    public static final PlacedFeatureKey CHARNIA_GREEN = createVegetationKey("charnia_green");
    public static final PlacedFeatureKey CHARNIA_RED_RARE = createVegetationKey("charnia_red_rare");

    // Plants
    public static final PlacedFeatureKey UMBRELLA_MOSS = createVegetationKey("umbrella_moss");
    public static final PlacedFeatureKey CREEPING_MOSS = createVegetationKey("creeping_moss");
    public static final PlacedFeatureKey CHORUS_GRASS = createVegetationKey("chorus_grass");
    public static final PlacedFeatureKey CRYSTAL_GRASS = createVegetationKey("crystal_grass");
    public static final PlacedFeatureKey CRYSTAL_MOSS_COVER = createVegetationKey("crystal_moss_cover");
    public static final PlacedFeatureKey SHADOW_PLANT = createVegetationKey("shadow_plant");
    public static final PlacedFeatureKey MURKWEED = createVegetationKey("murkweed");
    public static final PlacedFeatureKey NEEDLEGRASS = createVegetationKey("needlegrass");
    public static final PlacedFeatureKey SHADOW_BERRY = createVegetationKey("shadow_berry");
    public static final PlacedFeatureKey BUSHY_GRASS = createVegetationKey("bushy_grass");
    public static final PlacedFeatureKey BUSHY_GRASS_WG = createVegetationKey("bushy_grass_wg");
    public static final PlacedFeatureKey AMBER_GRASS = createVegetationKey("amber_grass");
    public static final PlacedFeatureKey TWISTED_UMBRELLA_MOSS = createVegetationKey("twisted_umbrella_moss");
    public static final PlacedFeatureKey JUNGLE_GRASS = createVegetationKey("jungle_grass");
    public static final PlacedFeatureKey SMALL_JELLYSHROOM_FLOOR = createVegetationKey("small_jellyshroom_floor");
    public static final PlacedFeatureKey BLOSSOM_BERRY = createVegetationKey("blossom_berry");
    public static final PlacedFeatureKey BLOOMING_COOKSONIA = createVegetationKey("blooming_cooksonia");
    public static final PlacedFeatureKey SALTEAGO = createVegetationKey("salteago");
    public static final PlacedFeatureKey VAIOLUSH_FERN = createVegetationKey("vaiolush_fern");
    public static final PlacedFeatureKey FRACTURN = createVegetationKey("fracturn");
    public static final PlacedFeatureKey UMBRELLA_MOSS_RARE = createVegetationKey("umbrella_moss_rare");
    public static final PlacedFeatureKey CREEPING_MOSS_RARE = createVegetationKey("creeping_moss_rare");
    public static final PlacedFeatureKey TWISTED_UMBRELLA_MOSS_RARE = createVegetationKey("twisted_umbrella_moss_rare");
    public static final PlacedFeatureKey ORANGO = createVegetationKey("orango");
    public static final PlacedFeatureKey AERIDIUM = createVegetationKey("aeridium");
    public static final PlacedFeatureKey LUTEBUS = createVegetationKey("lutebus");
    public static final PlacedFeatureKey LAMELLARIUM = createVegetationKey("lamellarium");
    public static final PlacedFeatureKey SMALL_AMARANITA = createVegetationKey("small_amaranita");
    public static final PlacedFeatureKey AMARANITA_PATCH = createVegetationKey("amaranita_patch");
    public static final PlacedFeatureKey GLOBULAGUS = createVegetationKey("globulagus");
    public static final PlacedFeatureKey CLAWFERN = createVegetationKey("clawfern");
    public static final PlacedFeatureKey BOLUX_MUSHROOM = createVegetationKey("bolux_mushroom");
    public static final PlacedFeatureKey CHORUS_MUSHROOM = createVegetationKey("chorus_mushroom");
    public static final PlacedFeatureKey AMBER_ROOT = createVegetationKey("amber_root");
    public static final PlacedFeatureKey INFLEXIA = createVegetationKey("inflexia");
    public static final PlacedFeatureKey FLAMMALIX = createVegetationKey("flammalix");
    public static final PlacedFeatureKey LANCELEAF = createVegetationKey("lanceleaf");
    public static final PlacedFeatureKey GLOW_PILLAR = createVegetationKey("glow_pillar");


    private static <F extends Feature<FC>, FC extends FeatureConfiguration> PlacedFeatureKey createVegetationKey(
            String name
    ) {
        return PlacedFeatureManager
                .createKey(BetterEnd.C.mk(name))
                .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);
    }
}
