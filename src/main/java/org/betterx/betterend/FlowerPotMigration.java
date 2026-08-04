package org.betterx.betterend;

import org.betterx.bclib.api.v2.datafixer.MigrationProfile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Moves a flower pot's contents out of the block state and into a {@code betterend:flower_pot}
 * block entity.
 * <p>
 * Up to 21.0.x a filled pot was a single block state: {@code plant_id} and {@code soil_id} were
 * {@link net.minecraft.world.level.block.state.properties.IntegerProperty}s indexing two arrays
 * that {@code FlowerPotBlock#postInit} built at runtime. 26.x keeps the contents in a
 * {@code FlowerPotBlockEntity} instead, so a plain ID rename leaves every existing pot empty.
 * <p>
 * The index-to-block mapping is <b>not</b> stored in the save. The old code read it from
 * {@code config/betterend/blocks.json} ({@code flower_pots.plants} / {@code .soils}) and, when that
 * file was missing, fell back to assigning indices in block-registration order. This class does the
 * same in reverse: it prefers that config file when the instance still has it, and otherwise uses
 * {@link #DEFAULT_PLANTS} / {@link #DEFAULT_SOILS} below.
 * <p>
 * Those defaults were reconstructed from the registration order in the old jars and verified
 * against a real {@code config/betterend/blocks.json} written by 4.0.11 - reconstruction and file
 * agree on all 46 plants and all 12 soils. better-end 21.0.11 produces the same order.
 */
public class FlowerPotMigration {
    private static final String NAMESPACE = "betterend:";
    private static final String BLOCK_SUFFIX = "_flower_pot";
    private static final String BLOCK_ENTITY = "betterend:flower_pot";
    private static final String PLANT_ID = "plant_id";
    private static final String SOIL_ID = "soil_id";

    /** Plant index order used when the instance has no {@code flower_pots} config. */
    private static final String[] DEFAULT_PLANTS = {
            "mossy_glowshroom_sapling", "pythadendron_sapling", "pythadendron_leaves", "end_lotus_flower",
            "lacugrove_sapling", "lacugrove_leaves", "dragon_tree_sapling", "dragon_tree_leaves",
            "tenanea_sapling", "tenanea_leaves", "helix_tree_sapling", "umbrella_tree_sapling",
            "lucernia_sapling", "lucernia_leaves", "umbrella_moss", "creeping_moss", "chorus_grass",
            "cave_grass", "crystal_grass", "shadow_plant", "bushy_grass", "amber_grass",
            "twisted_umbrella_moss", "jungle_grass", "blooming_cooksonia", "salteago", "vaiolush_fern",
            "fracturn", "clawfern", "globulagus", "orango", "aeridium", "lutebus", "lamellarium",
            "inflexia", "flammalix", "small_jellyshroom", "bolux_mushroom", "small_amaranita_mushroom",
            "neon_cactus", "shadow_berry", "blossom_berry_seed", "amber_root_seed", "chorus_mushroom_seed",
            "murkweed", "needlegrass"
    };

    /** Soil index order used when the instance has no {@code flower_pots} config. */
    private static final String[] DEFAULT_SOILS = {
            "end_mycelium", "end_moss", "chorus_nylium", "cave_moss", "crystal_moss", "shadow_grass",
            "pink_moss", "amber_moss", "jungle_moss", "sangnum", "rutiscus", "pallidium_full"
    };

    private static Map<Integer, String> plants;
    private static Map<Integer, String> soils;

    private static synchronized void loadTables() {
        if (plants != null) return;

        plants = new HashMap<>();
        soils = new HashMap<>();

        final Path config = FabricLoader.getInstance().getConfigDir().resolve("betterend/blocks.json");
        JsonObject flowerPots = null;
        if (Files.exists(config)) {
            try (Reader reader = Files.newBufferedReader(config, StandardCharsets.UTF_8)) {
                final JsonElement root = JsonParser.parseReader(reader);
                if (root != null && root.isJsonObject()) {
                    final JsonElement pots = root.getAsJsonObject().get("flower_pots");
                    if (pots != null && pots.isJsonObject()) flowerPots = pots.getAsJsonObject();
                }
            } catch (Exception e) {
                BetterEnd.C.log.warn("Could not read flower pot IDs from " + config, e);
            }
        }

        if (flowerPots != null) {
            readTable(flowerPots, "plants", plants);
            readTable(flowerPots, "soils", soils);
        }

        if (plants.isEmpty() && soils.isEmpty()) {
            BetterEnd.C.log.info("No flower pot config found, using the default index order.");
            for (int i = 0; i < DEFAULT_PLANTS.length; i++) plants.put(i, DEFAULT_PLANTS[i]);
            for (int i = 0; i < DEFAULT_SOILS.length; i++) soils.put(i, DEFAULT_SOILS[i]);
        } else {
            BetterEnd.C.log.info("Read flower pot IDs from " + config);
        }
    }

    /**
     * Reads one {@code name [default: n] -> index} table. The key carries the config system's
     * annotation, which has to be stripped back to the plain block name.
     */
    private static void readTable(JsonObject flowerPots, String key, Map<Integer, String> into) {
        final JsonElement element = flowerPots.get(key);
        if (element == null || !element.isJsonObject()) return;

        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            try {
                String name = entry.getKey();
                final int bracket = name.indexOf('[');
                if (bracket >= 0) name = name.substring(0, bracket);
                into.put(entry.getValue().getAsInt(), name.trim());
            } catch (Exception ignored) {
                // a malformed entry only costs us that one plant
            }
        }
    }

    /**
     * @param root    A chunk's root tag
     * @param profile The active migration profile
     * @return {@code true} if any pot was migrated
     */
    public static Boolean patchChunk(CompoundTag root, MigrationProfile profile) {
        final ListTag sections = root.getList("sections").orElse(null);
        if (sections == null || sections.isEmpty()) return false;

        loadTables();

        final int chunkX = root.getInt("xPos").orElse(0);
        final int chunkZ = root.getInt("zPos").orElse(0);
        final List<CompoundTag> newEntities = new ArrayList<>();

        for (int s = 0; s < sections.size(); s++) {
            final CompoundTag section = sections.getCompound(s).orElse(null);
            if (section == null) continue;
            final CompoundTag blockStates = section.getCompound("block_states").orElse(null);
            if (blockStates == null) continue;
            final ListTag palette = blockStates.getList("palette").orElse(null);
            if (palette == null || palette.isEmpty()) continue;

            // Which palette entries are filled pots, and what do they hold?
            final Map<Integer, CompoundTag> potStates = new HashMap<>();
            for (int i = 0; i < palette.size(); i++) {
                final CompoundTag entry = palette.getCompound(i).orElse(null);
                if (entry == null) continue;
                // Every stone material has its own pot and all of them moved their contents into
                // the block entity; only end_stone's was additionally renamed (endstone_ ->
                // end_stone_), so match on the shared suffix rather than a fixed list.
                final String name = entry.getStringOr("Name", "");
                if (!name.startsWith(NAMESPACE) || !name.endsWith(BLOCK_SUFFIX)) continue;
                final CompoundTag props = entry.getCompound("Properties").orElse(null);
                if (props == null) continue;
                if (props.contains(PLANT_ID) || props.contains(SOIL_ID)) potStates.put(i, entry);
            }
            if (potStates.isEmpty()) continue;

            final int sectionY = section.getByte("Y").orElse((byte) 0);
            final int[] indices = unpack(blockStates, palette.size());
            if (indices == null) continue;

            for (int idx = 0; idx < indices.length; idx++) {
                final CompoundTag entry = potStates.get(indices[idx]);
                if (entry == null) continue;

                final CompoundTag props = entry.getCompound("Properties").orElseGet(CompoundTag::new);
                final String plant = lookup(plants, props.getStringOr(PLANT_ID, ""));
                final String soil = lookup(soils, props.getStringOr(SOIL_ID, ""));
                if (plant == null && soil == null) continue;

                final CompoundTag be = new CompoundTag();
                be.putString("id", BLOCK_ENTITY);
                be.putInt("x", chunkX * 16 + (idx & 15));
                be.putInt("y", sectionY * 16 + (idx >> 8));
                be.putInt("z", chunkZ * 16 + ((idx >> 4) & 15));
                if (plant != null) be.putString("plant", "betterend:" + plant);
                if (soil != null) be.putString("soil", "betterend:" + soil);
                newEntities.add(be);
            }

            // The contents live in the block entity now, so drop the stale properties.
            for (CompoundTag entry : potStates.values()) {
                entry.getCompound("Properties").ifPresent(props -> {
                    props.remove(PLANT_ID);
                    props.remove(SOIL_ID);
                });
            }
        }

        if (newEntities.isEmpty()) return false;

        final ListTag blockEntities = root.getList("block_entities").orElseGet(ListTag::new);
        for (CompoundTag be : newEntities) {
            if (!hasEntityAt(blockEntities, be)) blockEntities.add(be);
        }
        root.put("block_entities", blockEntities);

        BetterEnd.C.log.info("Migrated " + newEntities.size() + " flower pot(s) in chunk "
                + chunkX + "/" + chunkZ + ".");
        return true;
    }

    /** {@code 0} means "empty", every other index is looked up in the table. */
    private static String lookup(Map<Integer, String> table, String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) return null;
        try {
            final int id = Integer.parseInt(rawValue);
            return id == 0 ? null : table.get(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean hasEntityAt(ListTag blockEntities, CompoundTag candidate) {
        final int x = candidate.getInt("x").orElse(0);
        final int y = candidate.getInt("y").orElse(0);
        final int z = candidate.getInt("z").orElse(0);
        for (int i = 0; i < blockEntities.size(); i++) {
            final CompoundTag be = blockEntities.getCompound(i).orElse(null);
            if (be == null) continue;
            if (be.getInt("x").orElse(0) == x
                    && be.getInt("y").orElse(0) == y
                    && be.getInt("z").orElse(0) == z) return true;
        }
        return false;
    }

    /**
     * Expands a section's packed palette indices into one entry per block.
     * <p>
     * Since 1.16 the indices do not straddle long boundaries: each long holds
     * {@code 64 / bits} of them, low bits first, and {@code bits} is at least 4. A section without
     * a {@code data} array is entirely palette entry 0.
     */
    private static int[] unpack(CompoundTag blockStates, int paletteSize) {
        final int[] out = new int[4096];
        if (!(blockStates.get("data") instanceof LongArrayTag dataTag)) {
            return paletteSize == 1 ? out : null;
        }

        final long[] data = dataTag.getAsLongArray();
        int bits = Math.max(4, 32 - Integer.numberOfLeadingZeros(Math.max(1, paletteSize - 1)));
        final int perLong = 64 / bits;
        final long mask = (1L << bits) - 1;

        for (int i = 0; i < out.length; i++) {
            final int longIndex = i / perLong;
            if (longIndex >= data.length) return null;
            out[i] = (int) ((data[longIndex] >>> ((i % perLong) * bits)) & mask);
        }
        return out;
    }
}
