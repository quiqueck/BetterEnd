package org.betterx.betterend.integration;


import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.bclib.integration.ModIntegration;
import org.betterx.bclib.trait.block.PlantLikeBlockTrait;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.HydraluxPetalColoredBlock;
import org.betterx.betterend.complexmaterials.ColoredMaterial;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.ui.ColorUtil;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.client.trait.ClientBlockTraits;
import de.ambertation.wover.core.api.ModCore;

import net.minecraft.world.level.ItemLike;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Maps;

import java.awt.*;
import java.util.Map;

public class FlamboyantRefabricatedIntegration extends ModIntegration {
    public FlamboyantRefabricatedIntegration() {
        super(BetterEnd.FLAMBOYANT);
    }

    @Override
    public void init() {
        // Insertion-ordered so the variants register (and so appear in the creative tab) in the order
        // they are listed below rather than shuffled by RGB hash - see ColoredMaterial's own maps.
        Map<Integer, String> colors = Maps.newLinkedHashMap();
        Map<Integer, ItemLike> dyes = Maps.newLinkedHashMap();

        addColor("fead1d", "amber", colors, dyes);
        addColor("bd9a5f", "beige", colors, dyes);
        addColor("edeada", "cream", colors, dyes);
        addColor("33430e", "dark_green", colors, dyes);
        addColor("639920", "forest_green", colors, dyes);
        addColor("f0618c", "hot_pink", colors, dyes);
        addColor("491c7b", "indigo", colors, dyes);
        addColor("65291b", "maroon", colors, dyes);
        addColor("2c3969", "navy", colors, dyes);
        addColor("827c17", "olive", colors, dyes);
        addColor("7bc618", "pale_green", colors, dyes);
        addColor("f4a4bd", "pale_pink", colors, dyes);
        addColor("f8d45a", "pale_yellow", colors, dyes);
        addColor("6bb1cf", "sky_blue", colors, dyes);
        addColor("6e8c9c", "slate_gray", colors, dyes);
        addColor("b02454", "violet", colors, dyes);

        new ColoredMaterial(
                HydraluxPetalColoredBlock::new,
                EndDecorBlocks.HYDRALUX_PETAL_BLOCK,
                colors,
                dyes,
                true,
                // Same nature-tab marker as the built-in tinted petals in EndDecorBlocks, so these land
                // beside their untinted source block instead of the catch-all blocks tab.
                (def) -> def.addTrait(ModCore.isDatagen() ? ClientModel.build() : null)
                            .addTrait(PlantLikeBlockTrait.withDefault())
        );
    }

    private void addColor(String hex, String name, Map<Integer, String> colors, Map<Integer, ItemLike> dyes) {
        int color = ColorUtil.color(hex);
        colors.put(color, name);
        dyes.put(color, getItem(name + "_dye"));

        System.out.println(name + " " + color + " " + new Color(color));
    }

    /**
     * Kept in a separate class file (not just an @Environment(CLIENT)-guarded expression):
     * merely creating this lambda - even without ever invoking it - requires resolving the
     * client-only WoverBlockModelGenerators parameter type at the invokedynamic bootstrap site,
     * which throws immediately on a dedicated server. Gating with ModCore.isDatagen() keeps that
     * bootstrap instruction from ever executing there. See PathBlockTrait for the same pattern.
     */
    @Environment(EnvType.CLIENT)
    private static class ClientModel {
        private static BlockModelTrait build() {
            return ClientBlockTraits.MODEL.with(
                    (key, block, generator) -> HydraluxPetalColoredBlock.provideBlockModel(generator, block)
            );
        }
    }
}
