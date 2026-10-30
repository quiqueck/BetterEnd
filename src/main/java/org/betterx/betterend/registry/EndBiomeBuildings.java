package org.betterx.betterend.registry;

import org.betterx.bclib.api.v2.levelgen.structures.BCLStructure;
import org.betterx.bclib.api.v2.levelgen.structures.BCLStructureBuilder;
import org.betterx.betterend.BetterEnd;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * The small buildings, ruins, stumps and logs of the land biomes (the templates in
 * {@code structures/biome/<biome>/}). Each kind of building is a vanilla jigsaw structure
 * {@code betterend:<biome>_<name>}: the numbered variants of a kind (ruins_1, ruins_2, ...) are the elements of one
 * start pool, so the jigsaw picks one of them at random. Every structure has its own structure set and its own
 * {@code has_structure/<biome>_<name>} biome tag, so each of them is found by {@code /locate structure} and
 * datapacks can move, tune or replace them, or add variants to a pool.
 * <p>
 * They replace the old {@code BuildingListFeature}, which picked a random template from the biome's
 * {@code structures.json} in one of 20 chunks and was invisible to {@code /locate}.
 */
public final class EndBiomeBuildings {
    /**
     * Lowest ground a building may stand on. Minecraft 1.20 has no {@code dimension_padding} for jigsaw
     * structures, so {@code JigsawStructureMixin} rejects starts of these pools below it. Above the void the
     * heightmap reports the bottom of the world, so this also keeps them out of the void.
     */
    public static final int MIN_GROUND_Y = 58;
    private static final String POOL_PREFIX = "biome/";

    /**
     * One kind of building of a biome.
     *
     * @param templates  the template names, one pool element each
     * @param offsetY    vertical offset of the template's floor relative to the first free block above the ground
     * @param adjustment {@link TerrainAdjustment#BEARD_THIN} fills the ground under and around the template (the old
     *                   {@code surface}/{@code object} terrain merge), {@link TerrainAdjustment#NONE} leaves it as is
     * @param mound      the template is built into a mound of end stone that belongs to it, so its end stone is placed
     *                   (other templates only have end stone as foundation filler in their bottom layer)
     */
    public record Building(
            String biome,
            String name,
            List<String> templates,
            int offsetY,
            TerrainAdjustment adjustment,
            boolean mound,
            ResourceKey<StructureTemplatePool> pool,
            BCLStructure<JigsawStructure> structure
    ) {
        public List<ResourceLocation> templateIds() {
            return templates.stream().map(t -> BetterEnd.makeID(POOL_PREFIX + biome + "/" + t)).toList();
        }
    }

    private record Kind(String biome, String name, List<String> templates, int offsetY, TerrainAdjustment adjustment, boolean mound) {
    }

    private static final List<Kind> KINDS = new ArrayList<>();

    static {
        variants("blossoming_spires", "ruins", 8, 0, TerrainAdjustment.BEARD_THIN);

        variants("chorus_forest", "fallen_tree", 4, 0, TerrainAdjustment.BEARD_THIN);
        variants("chorus_forest", "stump", 3, 0, TerrainAdjustment.BEARD_THIN);
        variants("chorus_forest", "ruins", 8, 0, TerrainAdjustment.BEARD_THIN);

        variants("foggy_mushroomland", "fallen_tree", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants("foggy_mushroomland", "stump", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants("foggy_mushroomland", "ruins", 3, 0, TerrainAdjustment.BEARD_THIN);
        single("foggy_mushroomland", "library", 0, TerrainAdjustment.BEARD_THIN);
        single("foggy_mushroomland", "tree_house", 0, TerrainAdjustment.BEARD_THIN);

        single("lantern_woods", "cabin", 0, TerrainAdjustment.BEARD_THIN);
        single("lantern_woods", "light_1", 0, TerrainAdjustment.BEARD_THIN);
        variants("lantern_woods", "log", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants("lantern_woods", "ruins", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants("lantern_woods", "stump", 3, 0, TerrainAdjustment.BEARD_THIN);

        variants("shadow_forest", "stump", 2, 0, TerrainAdjustment.NONE);
        variants("shadow_forest", "fallen_log", 2, 0, TerrainAdjustment.NONE);
        variants("shadow_forest", "ruins", 8, 0, TerrainAdjustment.BEARD_THIN);
        single("shadow_forest", "small_mansion", -1, TerrainAdjustment.BEARD_THIN);

        single("umbrella_jungle", "jellyshroom_cluster", -1, TerrainAdjustment.BEARD_THIN);
        // the two houses sit at different heights, so they cannot share one start pool
        KINDS.add(new Kind("umbrella_jungle", "house_1", List.of("house_1"), -1, TerrainAdjustment.BEARD_THIN, true));
        single("umbrella_jungle", "house_2", 0, TerrainAdjustment.BEARD_THIN);
        variants("umbrella_jungle", "ruins", 6, -1, TerrainAdjustment.NONE);

        // BYG integration biome: only exists (and so only gets these) when BYG is loaded
        variants("old_bulbis_gardens", "fallen_tree", 3, 0, TerrainAdjustment.NONE);
        variants("old_bulbis_gardens", "tree_stump", 3, 0, TerrainAdjustment.BEARD_THIN);
    }

    /**
     * The templates {@code <name>_1 ... <name>_<count>} as one structure {@code <biome>_<name>}.
     */
    private static void variants(String biome, String name, int count, int offsetY, TerrainAdjustment adjustment) {
        final List<String> templates = new ArrayList<>();
        for (int i = 1; i <= count; i++) templates.add(name + "_" + i);
        KINDS.add(new Kind(biome, name, List.copyOf(templates), offsetY, adjustment, false));
    }

    /**
     * The template {@code <template>} as one structure {@code <biome>_<template>}.
     */
    private static void single(String biome, String template, int offsetY, TerrainAdjustment adjustment) {
        KINDS.add(new Kind(biome, template, List.of(template), offsetY, adjustment, false));
    }

    private static List<Building> build() {
        return KINDS.stream().map(k -> {
            final int biomeTemplates = KINDS.stream()
                                            .filter(o -> o.biome.equals(k.biome))
                                            .mapToInt(o -> o.templates.size())
                                            .sum();
            // One set per structure, so /locate finds exactly the structure it is asked for (a set shared by
            // several structures with the same biomes would let /locate report a spot where another one of
            // them generates). The old feature put one random template of the biome's list into roughly one
            // in 20 chunks, and failed often; a set nearly always succeeds, so all buildings of a biome
            // together aim at about one in 30 chunks, shared out by the number of templates of each:
            // spacing = sqrt(30 * templates in the biome / templates of this structure).
            final int spacing = (int) Math.round(Math.sqrt(30.0 * biomeTemplates / k.templates.size()));
            final ResourceKey<StructureTemplatePool> pool = ResourceKey.create(
                    Registries.TEMPLATE_POOL,
                    BetterEnd.makeID(POOL_PREFIX + k.biome + "/" + k.name)
            );
            final BCLStructure<JigsawStructure> structure = BCLStructureBuilder
                    .jigsaw(BetterEnd.makeID(k.biome + "_" + k.name))
                    .startPool(pool)
                    .adjustment(k.adjustment)
                    .projectStartToHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                    // one more than the offset: the jigsaw sets the lowest layer of a template into the ground
                    // (vanilla templates have a foundation layer there), but these templates stand on the
                    // surface, and sunk by one their bottom layer would replace the painted surface block
                    .startHeight(ConstantHeight.of(VerticalAnchor.absolute(k.offsetY + 1)))
                    // max depth must be >= 1: vanilla's JigsawPlacement only adds the start piece inside its
                    // "maxDepth > 0" branch. The templates have no jigsaw blocks, so nothing else attaches.
                    .maxDepth(1)
                    // Same step as the old feature: before the vegetation, so plants grow around (and on) the ruins.
                    .step(Decoration.SURFACE_STRUCTURES)
                    .randomPlacement(spacing, spacing / 2)
                    .build();
            return new Building(k.biome, k.name, k.templates, k.offsetY, k.adjustment, k.mound, pool, structure);
        }).toList();
    }

    private static List<Building> BUILT;

    public static List<Building> all() {
        if (BUILT == null) BUILT = build();
        return BUILT;
    }

    public static List<Building> of(ResourceLocation biome) {
        if (!BetterEnd.MOD_ID.equals(biome.getNamespace())) return List.of();
        return all().stream().filter(b -> b.biome.equals(biome.getPath())).toList();
    }

    /**
     * {@code true} for the start pool of one of these buildings.
     */
    public static boolean isBuildingPool(Holder<StructureTemplatePool> pool) {
        return pool.unwrapKey()
                   .map(k -> BetterEnd.MOD_ID.equals(k.location().getNamespace())
                           && k.location().getPath().startsWith(POOL_PREFIX))
                   .orElse(false);
    }

    public static void register() {
        all();
    }

    private EndBiomeBuildings() {
    }
}
