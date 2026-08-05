package org.betterx.betterend.client.models;

import org.betterx.betterend.BetterEnd;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class Patterns {
    //Block Models
    public final static Identifier BLOCK_EMPTY = BetterEnd.C.mk("patterns/block/empty.json");
    public final static Identifier BLOCK_BASE = BetterEnd.C.mk("patterns/block/block.json");
    public final static Identifier BLOCK_SIDED = BetterEnd.C.mk("patterns/block/block_sided.json");
    public final static Identifier BLOCK_BOTTOM_TOP = BetterEnd.C.mk("patterns/block/block_bottom_top.json");
    public final static Identifier BLOCK_SLAB = BetterEnd.C.mk("patterns/block/slab.json");
    public final static Identifier BLOCK_STAIR = BetterEnd.C.mk("patterns/block/stairs.json");
    public final static Identifier BLOCK_STAIR_INNER = BetterEnd.C.mk("patterns/block/inner_stairs.json");
    public final static Identifier BLOCK_STAIR_OUTER = BetterEnd.C.mk("patterns/block/outer_stairs.json");
    public final static Identifier BLOCK_WALL_POST = BetterEnd.C.mk("patterns/block/wall_post.json");
    public final static Identifier BLOCK_WALL_SIDE = BetterEnd.C.mk("patterns/block/wall_side.json");
    public final static Identifier BLOCK_WALL_SIDE_TALL = BetterEnd.C.mk("patterns/block/wall_side_tall.json");
    public final static Identifier BLOCK_FENCE_POST = BetterEnd.C.mk("patterns/block/fence_post.json");
    public final static Identifier BLOCK_FENCE_SIDE = BetterEnd.C.mk("patterns/block/fence_side.json");
    public final static Identifier BLOCK_BUTTON = BetterEnd.C.mk("patterns/block/button.json");
    public final static Identifier BLOCK_BUTTON_PRESSED = BetterEnd.C.mk("patterns/block/button_pressed.json");
    public final static Identifier BLOCK_PILLAR = BetterEnd.C.mk("patterns/block/pillar.json");
    public final static Identifier BLOCK_PLATE_UP = BetterEnd.C.mk("patterns/block/pressure_plate_up.json");
    public final static Identifier BLOCK_PLATE_DOWN = BetterEnd.C.mk("patterns/block/pressure_plate_down.json");
    public final static Identifier BLOCK_DOOR_TOP = BetterEnd.C.mk("patterns/block/door_top.json");
    public final static Identifier BLOCK_DOOR_TOP_HINGE = BetterEnd.C.mk("patterns/block/door_top_hinge.json");
    public final static Identifier BLOCK_DOOR_BOTTOM = BetterEnd.C.mk("patterns/block/door_bottom.json");
    public final static Identifier BLOCK_DOOR_BOTTOM_HINGE = BetterEnd.C.mk("patterns/block/door_bottom_hinge.json");
    public final static Identifier BLOCK_CROSS = BetterEnd.C.mk("patterns/block/cross.json");
    public final static Identifier BLOCK_CROSS_SHADED = BetterEnd.C.mk("patterns/block/cross_shaded.json");
    public final static Identifier BLOCK_GATE_CLOSED = BetterEnd.C.mk("patterns/block/fence_gate_closed.json");
    public final static Identifier BLOCK_GATE_CLOSED_WALL = BetterEnd.C.mk("patterns/block/wall_gate_closed.json");
    public final static Identifier BLOCK_GATE_OPEN = BetterEnd.C.mk("patterns/block/fence_gate_open.json");
    public final static Identifier BLOCK_GATE_OPEN_WALL = BetterEnd.C.mk("patterns/block/wall_gate_open.json");
    public final static Identifier BLOCK_TRAPDOOR = BetterEnd.C.mk("patterns/block/trapdoor.json");
    public final static Identifier BLOCK_LADDER = BetterEnd.C.mk("patterns/block/ladder.json");
    public final static Identifier BLOCK_BARREL_OPEN = BetterEnd.C.mk("patterns/block/barrel_open.json");
    public final static Identifier BLOCK_PEDESTAL_DEFAULT = BetterEnd.C.mk("patterns/block/pedestal_default.json");
    public final static Identifier BLOKC_PEDESTAL_COLUMN = BetterEnd.C.mk("patterns/block/pedestal_column.json");
    public final static Identifier BLOCK_PEDESTAL_COLUMN_TOP = BetterEnd.C.mk("patterns/block/pedestal_column_top.json");
    public final static Identifier BLOCK_PEDESTAL_TOP = BetterEnd.C.mk("patterns/block/pedestal_top.json");
    public final static Identifier BLOCK_PEDESTAL_BOTTOM = BetterEnd.C.mk("patterns/block/pedestal_bottom.json");
    public final static Identifier BLOCK_PEDESTAL_PILLAR = BetterEnd.C.mk("patterns/block/pedestal_pillar.json");
    public final static Identifier BLOCK_BOOKSHELF = BetterEnd.C.mk("patterns/block/bookshelf.json");
    public final static Identifier BLOCK_STONE_LANTERN_CEIL = BetterEnd.C.mk("patterns/block/stone_lantern_ceil.json");
    public final static Identifier BLOCK_STONE_LANTERN_FLOOR = BetterEnd.C.mk("patterns/block/stone_lantern_floor.json");
    public final static Identifier BLOCK_BULB_LANTERN_FLOOR = BetterEnd.C.mk("patterns/block/bulb_lantern_floor.json");
    public final static Identifier BLOCK_BULB_LANTERN_CEIL = BetterEnd.C.mk("patterns/block/bulb_lantern_ceil.json");
    public final static Identifier BLOCK_PETAL_COLORED = BetterEnd.C.mk("models/block/block_petal_colored.json");
    public final static Identifier BLOCK_COMPOSTER = BetterEnd.C.mk("patterns/block/composter.json");
    public final static Identifier BLOCK_COLORED = BetterEnd.C.mk("patterns/block/block_colored.json");
    public final static Identifier BLOCK_BARS_POST = BetterEnd.C.mk("patterns/block/bars_post.json");
    public final static Identifier BLOCK_BARS_SIDE = BetterEnd.C.mk("patterns/block/bars_side.json");
    public final static Identifier BLOCK_ANVIL = BetterEnd.C.mk("patterns/block/anvil.json");
    public final static Identifier BLOCK_CHAIN = BetterEnd.C.mk("patterns/block/chain.json");
    public final static Identifier BLOCK_CHANDELIER_FLOOR = BetterEnd.C.mk("patterns/block/chandelier_floor.json");
    public final static Identifier BLOCK_CHANDELIER_WALL = BetterEnd.C.mk("patterns/block/chandelier_wall.json");
    public final static Identifier BLOCK_CHANDELIER_CEIL = BetterEnd.C.mk("patterns/block/chandelier_ceil.json");
    public final static Identifier BLOCK_FURNACE = BetterEnd.C.mk("patterns/block/furnace.json");
    public final static Identifier BLOCK_FURNACE_LIT = BetterEnd.C.mk("patterns/block/furnace_glow.json");
    public final static Identifier BLOCK_TOP_SIDE_BOTTOM = BetterEnd.C.mk("patterns/block/top_side_bottom.json");
    public final static Identifier BLOCK_PATH = BetterEnd.C.mk("patterns/block/path.json");
    public final static Identifier BLOCK_FLOWER_POT = BetterEnd.C.mk("patterns/block/flower_pot.json");
    public final static Identifier BLOCK_FLOWER_POT_SOIL = BetterEnd.C.mk("patterns/block/flower_pot_soil.json");
    public final static Identifier BLOCK_POTTED_LEAVES = BetterEnd.C.mk("patterns/block/potted_leaves.json");

    //Item Models
    public final static Identifier ITEM_WALL = BetterEnd.C.mk("patterns/item/pattern_wall.json");
    public final static Identifier ITEM_FENCE = BetterEnd.C.mk("patterns/item/pattern_fence.json");
    public final static Identifier ITEM_BUTTON = BetterEnd.C.mk("patterns/item/pattern_button.json");
    public final static Identifier ITEM_CHEST = BetterEnd.C.mk("patterns/item/pattern_chest.json");
    public final static Identifier ITEM_BLOCK = BetterEnd.C.mk("patterns/item/pattern_block_item.json");
    public final static Identifier ITEM_GENERATED = BetterEnd.C.mk("patterns/item/pattern_item_generated.json");
    public final static Identifier ITEM_HANDHELD = BetterEnd.C.mk("patterns/item/pattern_item_handheld.json");
    public final static Identifier ITEM_SPAWN_EGG = BetterEnd.C.mk("patterns/item/pattern_item_spawn_egg.json");

    public static Optional<String> createItemGenerated(String name) {
        return createJson(ITEM_GENERATED, name);
    }

    public static Optional<String> createBlockSimple(String name) {
        return Patterns.createJson(Patterns.BLOCK_BASE, name, name);
    }

    public static Optional<String> createBlockPillar(String name) {
        return Patterns.createJson(Patterns.BLOCK_PILLAR, name, name);
    }

    public static String createJson(Reader data, String parent, String block) {
        try (BufferedReader buffer = new BufferedReader(data)) {
            return buffer.lines().collect(Collectors.joining()).replace("%parent%", parent).replace("%block%", block);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Optional<String> createJson(Identifier patternId, String parent, String block) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try (InputStream input = resourceManager.getResource(patternId).get().open()) {
            return Optional.ofNullable(createJson(new InputStreamReader(input, StandardCharsets.UTF_8), parent, block));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public static Optional<String> createJson(Identifier patternId, String texture) {
        Map<String, String> textures = Maps.newHashMap();
        textures.put("%texture%", texture);
        return createJson(patternId, textures);
    }

    public static Optional<String> createJson(Identifier patternId, Map<String, String> textures) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try (InputStream input = resourceManager.getResource(patternId).get().open()) {
            String json = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)).lines()
                                                                                                  .collect(Collectors.joining());
            for (Entry<String, String> texture : textures.entrySet()) {
                json = json.replace(texture.getKey(), texture.getValue());
            }
            return Optional.of(json);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

}
