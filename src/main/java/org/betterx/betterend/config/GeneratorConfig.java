package org.betterx.betterend.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.LayerOptions;
import de.ambertation.wover.config.api.MainConfig;

import net.minecraft.core.BlockPos;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

public class GeneratorConfig extends ConfigFile {
    public final static Group GENERATOR = new Group(BetterEnd.C.namespace, "generator", 2500);

    public GeneratorConfig() {
        super(BetterEnd.C, "generator");
    }

    public final BooleanValue generateCentralIsland = new BooleanValue(
            MainConfig.STRUCTURE_GROUP.title(),
            "generate_central_island",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP);

    public final BooleanValue generateObsidianPlatform = new BooleanValue(
            MainConfig.STRUCTURE_GROUP.title(),
            "generate_obsidian_platform",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP);

    public final BooleanValue hasPortal = new BooleanValue(
            MainConfig.STRUCTURE_GROUP.title(),
            "has_portal",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP);

    public final BooleanValue replacePortal = new BooleanValue(
            MainConfig.STRUCTURE_GROUP.title(),
            "replace_portal",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP).setDependency(hasPortal);

    public final BooleanValue hasPillars = new BooleanValue(
            MainConfig.STRUCTURE_GROUP.title(),
            "has_pillars",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP);

    public final BooleanValue replacePillars = new BooleanValue(
            MainConfig.STRUCTURE_GROUP.title(),
            "replace_pillars",
            true
    ).setGroup(MainConfig.STRUCTURE_GROUP).setDependency(hasPillars);

    public final IntValue biomeSizeCaves = new IntValue(
            MainConfig.STRUCTURE_GROUP.title(), "biome_size_caves",
            32
    ).setGroup(MainConfig.STRUCTURE_GROUP).min(1).max(8192);

    public final IntValue endCityFailChance = new IntValue(
            MainConfig.STRUCTURE_GROUP.title(), "end_city_fail_chance",
            1
    ).setGroup(MainConfig.STRUCTURE_GROUP).min(1).max(50);

    public final BooleanValue newGenerator = new BooleanValue(
            GENERATOR.title(),
            "use_new_generator",
            true
    ).setGroup(GENERATOR);

    public final LayerOptionsValue bigOptions = new LayerOptionsValue(
            GENERATOR.title() + ".layers",
            "big_islands",
            new LayerOptions(300, 200, 70f / 128, 10f / 128, false)
    ).setGroup(GENERATOR).setDependency(newGenerator);

    public final LayerOptionsValue mediumOptions = new LayerOptionsValue(
            GENERATOR.title() + ".layers",
            "medium_islands",
            new LayerOptions(150, 100, 70f / 128, 20f / 128, true)
    ).setGroup(GENERATOR).setDependency(newGenerator);

    public final LayerOptionsValue smallOptions = new LayerOptionsValue(
            GENERATOR.title() + ".layers",
            "small_islands",
            new LayerOptions(60, 50, 70f / 128, 30f / 128, false)
    ).setGroup(GENERATOR).setDependency(newGenerator);


    public final BooleanValue hasDragonFights = new BooleanValue(
            MainConfig.ENTITY_GROUP.title(),
            "has_dragon_fights",
            true
    ).setGroup(MainConfig.ENTITY_GROUP);

    public final BooleanValue changeSpawn = new BooleanValue(
            MainConfig.ENTITY_GROUP.title() + ".spawn",
            "has_spawn",
            false
    ).setGroup(MainConfig.ENTITY_GROUP).hideInUI();

    public final BlockPosValue spawn = new BlockPosValue(
            MainConfig.ENTITY_GROUP.title() + ".spawn",
            "point",
            new BlockPos(20, 65, 0)
    ).setGroup(MainConfig.ENTITY_GROUP).setDependency(changeSpawn);

    public final BooleanValue changeChorusPlant = new BooleanValue(
            MainConfig.COSMETIC_GROUP.title(),
            "change_chorus_plant",
            true
    ).setGroup(MainConfig.COSMETIC_GROUP);


    public class LayerOptionsValue extends Value<LayerOptions, LayerOptionsValue> {
        public LayerOptionsValue(String path, String key, LayerOptions defaultValue) {
            super(path, key, defaultValue);
        }

        public LayerOptionsValue(String path, String key, LayerOptions defaultValue, boolean isDeprecated) {
            super(path, key, defaultValue, isDeprecated);
        }

        public LayerOptionsValue(ConfigToken<LayerOptions> token) {
            super(token);
        }

        public LayerOptionsValue(ConfigToken<LayerOptions> token, boolean isDeprecated) {
            super(token, isDeprecated);
        }

        @Override
        protected LayerOptions convert(@NotNull JsonElement el) {
            return new LayerOptions(el.getAsJsonObject());
        }

        @Override
        protected @NotNull JsonElement convert(LayerOptions value) {
            return value.toJson();
        }

        @Override
        protected @NotNull LayerOptions parseString(@NotNull String value) {
            return convert(new Gson().fromJson(value, JsonObject.class));
        }
    }
}
