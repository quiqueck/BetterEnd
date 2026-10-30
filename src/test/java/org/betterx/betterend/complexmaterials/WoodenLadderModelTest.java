package org.betterx.betterend.complexmaterials;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Headless check of the ladder models emitted by runDatagenClient. */
class WoodenLadderModelTest {
    private static final String LADDER_TEMPLATE = "betterend:block/ladder";
    private static final Path GENERATED = Path.of("src/main/generated");
    private static final Path RESOURCES = Path.of("src/main/resources");

    @Test
    void registeredWoodenLaddersResolveTheThreeDimensionalTemplate() throws IOException {
        final Set<String> ladders = registeredWoodSetLadders();
        // LADDER_BLOCK contributes CLIMBABLE when each slot registers; do not enumerate model files.
        final Set<String> taggedLadders = new TreeSet<>();
        for (JsonElement entry : read(GENERATED.resolve("data/minecraft/tags/block/climbable.json"))
                .getAsJsonArray("values")) {
            final String id = entry.getAsString();
            if (id.startsWith("betterend:") && id.endsWith("_" + SlotType.LADDER.suffix())) taggedLadders.add(id);
        }
        assertFalse(ladders.isEmpty(), "No wooden ladder registrations found");
        assertEquals(ladders, taggedLadders, "Wood-set registrations and generated ladder tags disagree");

        for (String ladder : ladders) {
            String parent = ladder.replace(":", ":block/");
            final Set<String> visited = new HashSet<>();
            final Map<String, String> textures = new HashMap<>();
            while (!LADDER_TEMPLATE.equals(parent) && parent.startsWith("betterend:")) {
                assertTrue(visited.add(parent), ladder + " has a cyclic model parent chain: " + visited);
                final JsonObject model = model(parent);
                collectTextures(model, textures);
                parent = model.has("parent") ? model.get("parent").getAsString() : "";
            }
            assertEquals(LADDER_TEMPLATE, parent, ladder + " resolved to the wrong ladder template");

            final JsonObject template = model(parent);
            collectTextures(template, textures);
            final Set<String> references = new TreeSet<>();
            collectReferences(template, references);
            assertFalse(references.isEmpty(), "The ladder template has no texture references");
            references.add("#particle");
            for (String reference : references) {
                final Set<String> seen = new HashSet<>();
                String texture = reference;
                while (texture.startsWith("#")) {
                    assertTrue(seen.add(texture), ladder + " has a cyclic texture reference: " + seen);
                    assertTrue(textures.containsKey(texture.substring(1)), ladder + " does not bind " + texture);
                    texture = textures.get(texture.substring(1));
                }
                assertFalse(texture.isBlank(), ladder + " has an empty texture for " + reference);
                final String[] id = texture.split(":", 2);
                assertEquals(2, id.length, ladder + " has an unqualified texture: " + texture);
                assertTrue(Files.isRegularFile(RESOURCES.resolve("assets/" + id[0] + "/textures/" + id[1] + ".png")),
                        ladder + " references a missing texture: " + texture);
            }
        }
    }

    private static Set<String> registeredWoodSetLadders() throws IOException {
        // shortcut: literal-named full wood sets; extend discovery if registration moves to helpers/subclasses.
        final String material = "org/betterx/betterend/complexmaterials/EndWoodenComplexMaterial";
        final ClassNode registry = new ClassNode();
        try (var input = WoodenLadderModelTest.class.getResourceAsStream(
                "/org/betterx/betterend/registry/block/EndWoodBlocks.class")) {
            assertNotNull(input, "Compiled wood registrations are missing from the test classpath");
            new ClassReader(input).accept(registry, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
        final Set<String> ladders = new TreeSet<>();
        for (var method : registry.methods) {
            if (!"<clinit>".equals(method.name)) continue;
            boolean constructing = false;
            String name = null;
            for (var instruction : method.instructions) {
                if (instruction instanceof TypeInsnNode type && type.getOpcode() == Opcodes.NEW
                        && material.equals(type.desc)) {
                    constructing = true;
                    name = null;
                } else if (constructing && name == null && instruction instanceof LdcInsnNode literal
                        && literal.cst instanceof String value) {
                    name = value;
                } else if (constructing && instruction instanceof MethodInsnNode call
                        && material.equals(call.owner) && "buildAndRegister".equals(call.name)) {
                    assertNotNull(name, "Wood-set registration no longer uses a literal base name");
                    ladders.add("betterend:" + name + "_" + SlotType.LADDER.suffix());
                    constructing = false;
                }
            }
        }
        return ladders;
    }

    private static JsonObject model(String id) throws IOException {
        final String[] parts = id.split(":", 2);
        final Path relative = Path.of("assets", parts[0], "models", parts[1] + ".json");
        final Path generated = GENERATED.resolve(relative);
        return read(Files.isRegularFile(generated) ? generated : RESOURCES.resolve(relative));
    }

    private static JsonObject read(Path path) throws IOException {
        try (var reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static void collectTextures(JsonObject model, Map<String, String> textures) {
        if (model.has("textures")) {
            model.getAsJsonObject("textures").entrySet()
                 .forEach(entry -> textures.putIfAbsent(entry.getKey(), entry.getValue().getAsString()));
        }
    }

    private static void collectReferences(JsonElement element, Set<String> references) {
        if (element.isJsonObject()) {
            element.getAsJsonObject().entrySet().forEach(entry -> collectReferences(entry.getValue(), references));
        } else if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(value -> collectReferences(value, references));
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()
                && element.getAsString().startsWith("#")) {
            references.add(element.getAsString());
        }
    }
}
