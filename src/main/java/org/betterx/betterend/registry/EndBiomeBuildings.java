package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.biome.EndBiomeKey;
import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.StructureKeys;
import de.ambertation.wover.structure.api.StructureManager;
import de.ambertation.wover.structure.api.pools.StructurePoolKey;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * The small buildings, ruins, stumps and logs of the land biomes (the templates in
 * {@code structure/biome/<biome>/}). Each kind of building is a vanilla jigsaw structure
 * {@code betterend:<biome>_<name>}: the numbered variants of a kind (ruins_1, ruins_2, ...) are the elements of one
 * start pool, so the jigsaw picks one of them at random. Every structure has its own structure set and its own
 * {@code has_structure/<biome>_<name>} biome tag, so each of them is found by {@code /locate structure} and
 * datapacks can move, tune or replace them, or add variants to a pool.
 * <p>
 * They replace the old {@code BuildingListFeature}, which picked a random template from the biome's
 * {@code structures.json} in one of 20 chunks and was invisible to {@code /locate}. The data generator
 * builds the JSON from this list (see {@code StructureDataProvider} and {@code EndBiomesProvider}).
 */
public final class EndBiomeBuildings {
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
            EndBiomeKey<?, ?> biome,
            String name,
            List<String> templates,
            int offsetY,
            TerrainAdjustment adjustment,
            boolean mound,
            StructurePoolKey pool,
            StructureKey.Jigsaw structure
    ) {
        public List<Identifier> templateIds() {
            return templates.stream()
                            .map(t -> BetterEnd.C.mk("biome/" + biome.key.identifier().getPath() + "/" + t))
                            .toList();
        }
    }

    private static final List<Building> ALL;

    static {
        ImmutableList.Builder<Building> b = ImmutableList.builder();

        variants(b, EndBiomes.BLOSSOMING_SPIRES, "ruins", 8, 0, TerrainAdjustment.BEARD_THIN);

        variants(b, EndBiomes.CHORUS_FOREST, "fallen_tree", 4, 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.CHORUS_FOREST, "stump", 3, 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.CHORUS_FOREST, "ruins", 8, 0, TerrainAdjustment.BEARD_THIN);

        variants(b, EndBiomes.FOGGY_MUSHROOMLAND, "fallen_tree", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.FOGGY_MUSHROOMLAND, "stump", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.FOGGY_MUSHROOMLAND, "ruins", 3, 0, TerrainAdjustment.BEARD_THIN);
        single(b, EndBiomes.FOGGY_MUSHROOMLAND, "library", 0, TerrainAdjustment.BEARD_THIN);
        single(b, EndBiomes.FOGGY_MUSHROOMLAND, "tree_house", 0, TerrainAdjustment.BEARD_THIN);

        single(b, EndBiomes.LANTERN_WOODS, "cabin", 0, TerrainAdjustment.BEARD_THIN);
        single(b, EndBiomes.LANTERN_WOODS, "light_1", 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.LANTERN_WOODS, "log", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.LANTERN_WOODS, "ruins", 2, 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.LANTERN_WOODS, "stump", 3, 0, TerrainAdjustment.BEARD_THIN);

        variants(b, EndBiomes.SHADOW_FOREST, "stump", 2, 0, TerrainAdjustment.NONE);
        variants(b, EndBiomes.SHADOW_FOREST, "fallen_log", 2, 0, TerrainAdjustment.NONE);
        variants(b, EndBiomes.SHADOW_FOREST, "ruins", 8, 0, TerrainAdjustment.BEARD_THIN);
        single(b, EndBiomes.SHADOW_FOREST, "small_mansion", -1, TerrainAdjustment.BEARD_THIN);

        single(b, EndBiomes.UMBRELLA_JUNGLE, "jellyshroom_cluster", -1, TerrainAdjustment.BEARD_THIN);
        // the two houses sit at different heights, so they cannot share one start pool
        add(b, EndBiomes.UMBRELLA_JUNGLE, "house_1", List.of("house_1"), -1, TerrainAdjustment.BEARD_THIN, true);
        single(b, EndBiomes.UMBRELLA_JUNGLE, "house_2", 0, TerrainAdjustment.BEARD_THIN);
        variants(b, EndBiomes.UMBRELLA_JUNGLE, "ruins", 6, -1, TerrainAdjustment.NONE);

        ALL = b.build();
    }

    /**
     * The templates {@code <name>_1 ... <name>_<count>} as one structure {@code <biome>_<name>}.
     */
    private static void variants(
            ImmutableList.Builder<Building> b,
            EndBiomeKey<?, ?> biome,
            String name,
            int count,
            int offsetY,
            TerrainAdjustment adjustment
    ) {
        final List<String> templates = new ArrayList<>();
        for (int i = 1; i <= count; i++) templates.add(name + "_" + i);
        add(b, biome, name, templates, offsetY, adjustment, false);
    }

    /**
     * The template {@code <template>} as one structure {@code <biome>_<template>}.
     */
    private static void single(
            ImmutableList.Builder<Building> b,
            EndBiomeKey<?, ?> biome,
            String template,
            int offsetY,
            TerrainAdjustment adjustment
    ) {
        add(b, biome, template, List.of(template), offsetY, adjustment, false);
    }

    private static void add(
            ImmutableList.Builder<Building> b,
            EndBiomeKey<?, ?> biome,
            String name,
            List<String> templates,
            int offsetY,
            TerrainAdjustment adjustment,
            boolean mound
    ) {
        final String biomeName = biome.key.identifier().getPath();
        b.add(new Building(
                biome,
                name,
                List.copyOf(templates),
                offsetY,
                adjustment,
                mound,
                StructureKeys.pool(BetterEnd.C.mk("biome/" + biomeName + "/" + name)),
                // Same step as the old feature: before the vegetation, so plants grow around (and on) the ruins.
                StructureManager.jigsaw(BetterEnd.C.mk(biomeName + "_" + name))
                                .step(Decoration.SURFACE_STRUCTURES)
        ));
    }

    /**
     * How many templates the biome has in all its buildings; the structure sets share out the old feature's
     * frequency by it.
     */
    public static int templateCount(EndBiomeKey<?, ?> biome) {
        return of(biome).stream().mapToInt(b -> b.templates().size()).sum();
    }

    public static List<Building> all() {
        return ALL;
    }

    public static List<Building> of(EndBiomeKey<?, ?> biome) {
        return ALL.stream().filter(b -> b.biome() == biome).toList();
    }

    public static void register() {
    }

    private EndBiomeBuildings() {
    }
}
