package org.betterx.datagen.betterend.tags;


import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.block.EndOreBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWaterPlantBlocks;
import org.betterx.betterend.complexmaterials.MaterialManager;
import static org.betterx.betterend.complexmaterials.MetalMaterial.ORE;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.datagen.betterend.worldgen.EndBiomesProvider;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class BlockTagProvider extends WoverTagProvider.ForBlocks {
    public BlockTagProvider(ModCore modCore) {
        super(modCore, Set.of(EndTags.INCORRECT_FOR_AETERNIUM_TOOL));
    }


    private static void addEndGround(TagBootstrapContext<Block> context, Block bl) {
        context.add(CommonBlockTags.END_STONES, bl);
    }

    private static void addSurvivesOn(TagBootstrapContext<Block> context) {
        // The flower_islets void-ring islands (SmallIslandStructure) coat their tops in sangnum and
        // pallidium instead of end moss, so every ground plant the flower_islets biome plants must be
        // allowed to root on those blocks - otherwise the vegetation still refuses to grow there.
        final Block[] FLOWER_ISLET_COAT = {
                EndTerrainBlocks.SANGNUM,
                EndTerrainBlocks.PALLIDIUM_FULL,
                EndTerrainBlocks.PALLIDIUM_HEAVY,
                EndTerrainBlocks.PALLIDIUM_THIN,
                EndTerrainBlocks.PALLIDIUM_TINY
        };

        context.add(
                EndTags.SURVIVES_ON_PALLIDIUM,
                EndTerrainBlocks.PALLIDIUM_FULL,
                EndTerrainBlocks.PALLIDIUM_HEAVY,
                EndTerrainBlocks.PALLIDIUM_THIN,
                EndTerrainBlocks.PALLIDIUM_TINY
        );
        context.add(EndTags.SURVIVES_ON_AMBER_MOSS, EndTerrainBlocks.AMBER_MOSS);
        // 26.2's vanilla sulfur and cinnabar now make up the sulphur spring floor and the sulphuric
        // cave alongside brimstone, so anything that could root on brimstone roots on those too.
        context.add(
                EndTags.SURVIVES_ON_BRIMSTONE,
                EndStoneBlocks.BRIMSTONE,
                Blocks.SULFUR,
                Blocks.CINNABAR
        );
        context.add(EndTags.SURVIVES_ON_CHORUS_NYLIUM, EndTerrainBlocks.CHORUS_NYLIUM);
        context.add(
                EndTags.SURVIVES_ON_END_BONE,
                EndTerrainBlocks.SANGNUM,
                EndStoneBlocks.MOSSY_OBSIDIAN,
                EndStoneBlocks.MOSSY_DRAGON_BONE
        );
        context.add(EndTags.SURVIVES_ON_END_MOSS, EndTerrainBlocks.END_MOSS);
        context.add(EndTags.SURVIVES_ON_END_MOSS, FLOWER_ISLET_COAT);
        context.add(EndTags.SURVIVES_ON_END_STONE, CommonBlockTags.END_STONES);
        context.add(
                EndTags.SURVIVES_ON_END_STONE_OR_TREES,
                EndTags.SURVIVES_ON_END_STONE,
                BlockTags.LEAVES,
                BlockTags.LOGS
        );
        context.add(EndTags.SURVIVES_ON_JUNGLE_MOSS, EndTerrainBlocks.JUNGLE_MOSS);
        context.add(
                EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM,
                EndTerrainBlocks.END_MOSS,
                EndTerrainBlocks.END_MYCELIUM,
                EndTerrainBlocks.JUNGLE_MOSS
        );
        context.add(EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM, FLOWER_ISLET_COAT);
        context.add(EndTags.SURVIVES_ON_MOSS_OR_DUST, EndTerrainBlocks.END_MOSS, EndTerrainBlocks.ENDSTONE_DUST);
        context.add(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM, EndTerrainBlocks.END_MOSS, EndTerrainBlocks.END_MYCELIUM);
        context.add(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM, FLOWER_ISLET_COAT);
        context.add(EndTags.SURVIVES_ON_PINK_MOSS, EndTerrainBlocks.PINK_MOSS);
        context.add(EndTags.SURVIVES_ON_RUTISCUS, EndTerrainBlocks.RUTISCUS);
        context.add(EndTags.SURVIVES_ON_RUTISCUS, FLOWER_ISLET_COAT);
        context.add(EndTags.SURVIVES_ON_SHADOW_GRASS, EndTerrainBlocks.SHADOW_GRASS);
        context.add(EndTags.SURVIVES_ON_SULPHURIC_ROCK, EndStoneBlocks.SULPHURIC_ROCK.getBaseBlock());

        // Vanilla dirt-like soils accepted by the BetterEnd flower pot. Keep this in sync
        // with EndPottableSoilProvider.VANILLA_SOILS (this tag is the valid_soils of the
        // vanilla flower-pot plant set).
        context.add(
                EndTags.SURVIVES_ON_DIRT,
                Blocks.DIRT,
                Blocks.GRASS_BLOCK,
                Blocks.COARSE_DIRT,
                Blocks.PODZOL,
                Blocks.ROOTED_DIRT,
                Blocks.MUD,
                Blocks.MOSS_BLOCK,
                Blocks.MYCELIUM
        );
    }

    @Override
    public void prepareTags(TagBootstrapContext<Block> context) {
        addEndGround(context, EndMetalBlocks.THALLASIUM.getBlock(ORE));
        addEndGround(context, EndTerrainBlocks.ENDSTONE_DUST);
        addEndGround(context, EndOreBlocks.AMBER_ORE);
        addEndGround(context, EndTerrainBlocks.CAVE_MOSS);

        context.add(
                CommonBlockTags.END_STONES,
                EndOreBlocks.ENDER_ORE,
                EndStoneBlocks.BRIMSTONE
        );
        context.add(CommonBlockTags.END_STONES, EndStoneBlocks.BRIMSTONE);
        context.add(BlockTags.ANVIL, EndFunctionalBlocks.AETERNIUM_ANVIL);
        context.add(BlockTags.BEACON_BASE_BLOCKS, EndMetalBlocks.AETERNIUM_BLOCK);
        context.add(
                BlockTags.DRAGON_IMMUNE,
                EndOreBlocks.ENDER_ORE,
                EndFunctionalBlocks.ETERNAL_PEDESTAL,
                EndStoneBlocks.FLAVOLITE_RUNED_ETERNAL,
                EndStoneBlocks.FLAVOLITE_RUNED
        );

        context.add(EndTags.BONEMEAL_TARGET_WATER_GRASS, CommonBlockTags.END_STONES);

        EndBiomesProvider
                .BIOMES
                .values()
                .stream()
                .filter(info -> info.config() instanceof EndBiome.Config)
                .map(info -> info.config())
                .forEach(config -> {
                    config.surfaceMaterial().addBiomeSurfaceToEndGroup(context, CommonBlockTags.END_STONES);
                });

        MaterialManager.stream().forEach(m -> m.registerBlockTags(context));

        Block[] charnias = new Block[]{
                EndWaterPlantBlocks.CHARNIA_CYAN,
                EndWaterPlantBlocks.CHARNIA_GREEN,
                EndWaterPlantBlocks.CHARNIA_ORANGE,
                EndWaterPlantBlocks.CHARNIA_LIGHT_BLUE,
                EndWaterPlantBlocks.CHARNIA_PURPLE,
                EndWaterPlantBlocks.CHARNIA_RED
        };

        for (Block charnia : charnias) {
            context.add(EndTags.BONEMEAL_SOURCE_WATER_GRASS, charnia);
        }

        addSurvivesOn(context);
    }
}
