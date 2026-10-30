package org.betterx.betterend.testmod.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Regressions for set slots whose own values were silently replaced by the set material, because
 * {@code BlockDefinition} applies traits and setters in call order and the material runs after the slot:
 * <ul>
 *     <li>End wood planks-derived blocks registered {@code FLAMMABLE.with(5, 20)} after {@code WOOD_BLOCK}'s
 *     bundled 5/5, and the first registration won, so planks, slabs, doors, ... burned like logs.</li>
 *     <li>Bulb lanterns set strength 1, the lantern sound and light 15 on the slot; {@code METAL_BLOCK} and the
 *     material setters replaced them, and the iron set's copy-base dropped them entirely (no light).</li>
 * </ul>
 */
public class SetPropertyOrderGameTest {
    private static final String[] WOODS = {
            "mossy_glowshroom", "pythadendron", "end_lotus", "lacugrove", "dragon_tree", "tenanea",
            "helix_tree", "umbrella_tree", "jellyshroom", "lucernia"
    };
    private static final String[] PLANK_PARTS = {"planks", "slab", "stairs", "fence", "door", "chair"};
    private static final String[] LOG_PARTS = {"log", "bark", "stripped_log", "stripped_bark"};
    private static final String[] BULB_LANTERNS = {
            "iron_bulb_lantern", "iron_bulb_lantern_red", "terminite_bulb_lantern", "thallasium_bulb_lantern"
    };

    private static Block block(List<String> failures, String path) {
        final Block b = BuiltInRegistries.BLOCK.getValue(Identifier.parse("betterend:" + path));
        if (b == null || BuiltInRegistries.BLOCK.getKey(b).getPath().equals("air")) {
            failures.add(path + ": not registered");
            return null;
        }
        return b;
    }

    private static void fail(GameTestHelper helper, String what, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(what + ":\n - " + String.join("\n - ", failures)));
        }
        helper.succeed();
    }

    private static void expectFlammable(List<String> failures, String path, int ignite, int burn) {
        final Block b = block(failures, path);
        if (b == null) return;
        final FlammableBlockRegistry.Entry e = FlammableBlockRegistry.getDefaultInstance().get(b);
        final int gotIgnite = e == null ? 0 : e.getIgniteOdds();
        final int gotBurn = e == null ? 0 : e.getBurnOdds();
        if (gotIgnite != ignite || gotBurn != burn) {
            failures.add(path + ": flammable " + gotIgnite + "/" + gotBurn + ", expected " + ignite + "/" + burn);
        }
    }

    @GameTest
    public void endWoodPlanksBurnFasterThanLogs(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (String wood : WOODS) {
            for (String part : PLANK_PARTS) expectFlammable(failures, wood + "_" + part, 5, 20);
            for (String part : LOG_PARTS) expectFlammable(failures, wood + "_" + part, 5, 5);
        }
        fail(helper, "End wood flammability", failures);
    }

    @GameTest
    public void bulbLanternsKeepTheirOwnProperties(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        for (String path : BULB_LANTERNS) {
            final Block b = block(failures, path);
            if (b == null) continue;
            final BlockState state = b.defaultBlockState();
            if (b.defaultDestroyTime() != 1.0F || b.getExplosionResistance() != 1.0F) {
                failures.add(path + ": strength " + b.defaultDestroyTime() + "/" + b.getExplosionResistance()
                        + ", expected 1.0/1.0");
            }
            if (state.getSoundType() != SoundType.LANTERN) failures.add(path + ": not the lantern sound");
            if (state.getLightEmission() != 15) {
                failures.add(path + ": light " + state.getLightEmission() + ", expected 15");
            }
        }
        fail(helper, "bulb lantern properties", failures);
    }
}
