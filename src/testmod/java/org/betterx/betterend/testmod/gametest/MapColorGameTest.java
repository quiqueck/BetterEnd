package org.betterx.betterend.testmod.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Reproduces FTB Chunks' ColorMapLoader#apply logic (github.com/FTBTeam/FTB-Chunks,
 * common/.../client/ColorMapLoader.java) against every registered betterend: block, to find whatever
 * block makes {@link Block#defaultMapColor()} return a literal null - see
 * https://github.com/quiqueck/BetterEnd/issues/591. FTB Chunks calls defaultMapColor() twice
 * (once in a `!= MapColor.NONE` guard, once as the value it stores), so this checks both calls.
 */
public class MapColorGameTest {
    @GameTest
    public void everyBlockHasANonNullDefaultMapColor(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            final Block block = entry.getValue();
            final String id = entry.getKey().identifier().toString();
            if (!id.startsWith("betterend:")) continue;

            if (block instanceof AirBlock || block instanceof FireBlock
                    || block instanceof GrassBlock || block instanceof LeavesBlock
                    || block instanceof VineBlock || block instanceof FlowerPotBlock) {
                continue;
            }

            MapColor first;
            try {
                first = block.defaultMapColor();
            } catch (Throwable t) {
                failures.add(id + ": defaultMapColor() (1st call) threw " + t);
                continue;
            }

            if (first == null) {
                failures.add(id + ": defaultMapColor() (1st call) returned null");
                continue;
            }
            if (first == MapColor.NONE) continue;

            MapColor second;
            try {
                second = block.defaultMapColor();
            } catch (Throwable t) {
                failures.add(id + ": defaultMapColor() (2nd call) threw " + t);
                continue;
            }

            if (second == null) {
                failures.add(id + ": defaultMapColor() 1st call=" + first + " but 2nd call=null");
            } else if (second != first) {
                failures.add(id + ": defaultMapColor() is not stable across calls (1st=" + first + ", 2nd=" + second + ")");
            }
        }

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "blocks with a broken defaultMapColor():\n - " + String.join("\n - ", failures)
            ));
        }
        helper.succeed();
    }
}
