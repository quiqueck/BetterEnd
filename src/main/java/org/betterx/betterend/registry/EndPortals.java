package org.betterx.betterend.registry;

import org.betterx.bclib.util.JsonFactory;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.ui.ColorUtil;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;

public class EndPortals {

    public final static Identifier OVERWORLD_ID = Level.OVERWORLD.identifier();

    private static PortalInfo[] portals;

    public static void loadPortals() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toString(), "betterend/portals.json");
        JsonObject json;
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            json = makeDefault(file);
        } else {
            json = JsonFactory.getJsonObject(file);
        }
        if (!json.has("portals") || !json.get("portals").isJsonArray()) {
            json = makeDefault(file);
        }
        JsonArray array = json.get("portals").getAsJsonArray();
        if (array.size() == 0) {
            json = makeDefault(file);
            array = json.get("portals").getAsJsonArray();
        }
        portals = new PortalInfo[array.size()];
        for (int i = 0; i < portals.length; i++) {
            portals[i] = new PortalInfo(array.get(i).getAsJsonObject());
        }
    }

    public static int getCount() {
        return MHelper.max(portals.length - 1, 1);
    }

    public static ServerLevel getWorld(MinecraftServer server, int portalId) {
        if (portalId < 0 || portalId >= portals.length) {
            return server.overworld();
        }
        return portals[portalId].getWorld(server);
    }

    public static Identifier getWorldId(int portalId) {
        if (portalId < 0 || portalId >= portals.length) {
            return OVERWORLD_ID;
        }
        return portals[portalId].dimension;
    }

    public static int getPortalIdByItem(Identifier item) {
        for (int i = 0; i < portals.length; i++) {
            if (portals[i].item.equals(item)) {
                return i;
            }
        }
        return 0;
    }

    public static int getPortalIdByWorld(Identifier world) {
        for (int i = 0; i < portals.length; i++) {
            if (portals[i].dimension.equals(world)) {
                return i;
            }
        }
        return 0;
    }

    public static int getColor(int state) {
        return portals[state].color;
    }

    public static boolean isAvailableItem(Identifier item) {
        for (PortalInfo portal : portals) {
            if (portal.item.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private static JsonObject makeDefault(File file) {
        JsonObject jsonObject = new JsonObject();
        JsonFactory.storeJson(file, jsonObject);
        JsonArray array = new JsonArray();
        jsonObject.add("portals", array);
        array.add(makeDefault().toJson());
        JsonFactory.storeJson(file, jsonObject);
        return jsonObject;
    }

    private static PortalInfo makeDefault() {
        return new PortalInfo(
                Identifier.withDefaultNamespace("overworld"),
                BetterEnd.C.mk("eternal_crystal"),
                255,
                255,
                255
        );
    }

    private static class PortalInfo {
        private final Identifier dimension;
        private final Identifier item;
        private final int color;
        private ServerLevel world;

        PortalInfo(JsonObject obj) {
            this(
                    Identifier.parse(JsonFactory.getString(obj, "dimension", "minecraft:overworld")),
                    Identifier.parse(JsonFactory.getString(obj, "item", "betterend:eternal_crystal")),
                    JsonFactory.getInt(obj, "colorRed", 255),
                    JsonFactory.getInt(obj, "colorGreen", 255),
                    JsonFactory.getInt(obj, "colorBlue", 255)
            );
        }

        PortalInfo(Identifier dimension, Identifier item, int r, int g, int b) {
            this.dimension = dimension;
            this.item = item;
            this.color = ColorUtil.color(r, g, b);
        }

        ServerLevel getWorld(MinecraftServer server) {
            if (world != null) {
                return world;
            }
            for (ServerLevel world : server.getAllLevels()) {
                if (world.dimension().identifier().equals(dimension)) {
                    this.world = world;
                    return world;
                }
            }
            return server.overworld();
        }

        JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("dimension", dimension.toString());
            obj.addProperty("item", item.toString());
            obj.addProperty("colorRed", (color >> 16) & 255);
            obj.addProperty("colorGreen", (color >> 8) & 255);
            obj.addProperty("colorBlue", color & 255);
            return obj;
        }
    }
}
