package org.betterx.betterend.util;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.api.v3.bonemeal.BonemealAPI;
import org.betterx.bclib.api.v3.bonemeal.WaterGrassSpreader;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.registry.features.EndConfiguredBonemealFeature;

public class BonemealPlants {
    public static void init() {
        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.END_MOSS,
                EndConfiguredBonemealFeature.BONEMEAL_END_MOSS
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.RUTISCUS,
                EndConfiguredBonemealFeature.BONEMEAL_RUTISCUS
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.END_MYCELIUM,
                EndConfiguredBonemealFeature.BONEMEAL_END_MYCELIUM
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.JUNGLE_MOSS,
                EndConfiguredBonemealFeature.BONEMEAL_JUNGLE_MOSS
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.SANGNUM,
                EndConfiguredBonemealFeature.BONEMEAL_SANGNUM
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndStoneBlocks.MOSSY_OBSIDIAN,
                EndConfiguredBonemealFeature.BONEMEAL_MOSSY_OBSIDIAN
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndStoneBlocks.MOSSY_DRAGON_BONE,
                EndConfiguredBonemealFeature.BONEMEAL_MOSSY_DRAGON_BONE
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.CAVE_MOSS,
                EndConfiguredBonemealFeature.BONEMEAL_CAVE_MOSS
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.CHORUS_NYLIUM,
                EndConfiguredBonemealFeature.BONEMEAL_CHORUS_NYLIUM
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.CRYSTAL_MOSS,
                EndConfiguredBonemealFeature.BONEMEAL_CRYSTAL_MOSS
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.SHADOW_GRASS,
                EndConfiguredBonemealFeature.BONEMEAL_SHADOW_GRASS
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.PINK_MOSS,
                EndConfiguredBonemealFeature.BONEMEAL_PINK_MOSS
        );

        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.AMBER_MOSS,
                EndConfiguredBonemealFeature.BONEMEAL_AMBER_MOSS
        );


        // Only the full variant: PallidiumBlock.useItemOn intercepts bone meal on tiny/thin/heavy to
        // upgrade them one step, and only pallidium_full (nextLevel == null) falls through to the item,
        // where BonemealAPI can take over. Registering the thinner variants here would be dead code.
        BonemealAPI.INSTANCE.addSpreadableFeatures(
                EndTerrainBlocks.PALLIDIUM_FULL,
                EndConfiguredBonemealFeature.BONEMEAL_PALLIDIUM
        );

        BonemealAPI.INSTANCE.addSpreadableBlocks(
                EndTags.BONEMEAL_TARGET_WATER_GRASS,
                new WaterGrassSpreader(EndTags.BONEMEAL_SOURCE_WATER_GRASS)
        );

        BonemealAPI.INSTANCE.addSpreadableBlocks(
                EndTags.BONEMEAL_TARGET_DRAGON_BONE,
                EndTags.BONEMEAL_SOURCE_DRAGON_BONE
        );
    }
}
