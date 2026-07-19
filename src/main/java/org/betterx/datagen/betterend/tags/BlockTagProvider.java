package org.betterx.datagen.betterend.tags;

import org.betterx.betterend.complexmaterials.MaterialManager;
import static org.betterx.betterend.complexmaterials.MetalMaterial.ORE;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndTags;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.datagen.betterend.worldgen.EndBiomesProvider;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

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
        context.add(
                EndTags.SURVIVES_ON_PALLIDIUM,
                EndBlocks.PALLIDIUM_FULL,
                EndBlocks.PALLIDIUM_HEAVY,
                EndBlocks.PALLIDIUM_THIN,
                EndBlocks.PALLIDIUM_TINY
        );
        context.add(EndTags.SURVIVES_ON_AMBER_MOSS, EndBlocks.AMBER_MOSS);
        context.add(EndTags.SURVIVES_ON_BRIMSTONE, EndBlocks.BRIMSTONE);
        context.add(EndTags.SURVIVES_ON_CHORUS_NYLIUM, EndBlocks.CHORUS_NYLIUM);
        context.add(
                EndTags.SURVIVES_ON_END_BONE,
                EndBlocks.SANGNUM,
                EndBlocks.MOSSY_OBSIDIAN,
                EndBlocks.MOSSY_DRAGON_BONE
        );
        context.add(EndTags.SURVIVES_ON_END_MOSS, EndBlocks.END_MOSS);
        context.add(EndTags.SURVIVES_ON_END_STONE, CommonBlockTags.END_STONES);
        context.add(
                EndTags.SURVIVES_ON_END_STONE_OR_TREES,
                EndTags.SURVIVES_ON_END_STONE,
                BlockTags.LEAVES,
                BlockTags.LOGS
        );
        context.add(EndTags.SURVIVES_ON_JUNGLE_MOSS, EndBlocks.JUNGLE_MOSS);
        context.add(
                EndTags.SURVIVES_ON_JUNGLE_MOSS_OR_MYCELIUM,
                EndBlocks.END_MOSS,
                EndBlocks.END_MYCELIUM,
                EndBlocks.JUNGLE_MOSS
        );
        context.add(EndTags.SURVIVES_ON_MOSS_OR_DUST, EndBlocks.END_MOSS, EndBlocks.ENDSTONE_DUST);
        context.add(EndTags.SURVIVES_ON_MOSS_OR_MYCELIUM, EndBlocks.END_MOSS, EndBlocks.END_MYCELIUM);
        context.add(EndTags.SURVIVES_ON_PINK_MOSS, EndBlocks.PINK_MOSS);
        context.add(EndTags.SURVIVES_ON_RUTISCUS, EndBlocks.RUTISCUS);
        context.add(EndTags.SURVIVES_ON_SHADOW_GRASS, EndBlocks.SHADOW_GRASS);
        context.add(EndTags.SURVIVES_ON_SULPHURIC_ROCK, EndBlocks.SULPHURIC_ROCK.getBaseBlock());

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
        addEndGround(context, EndBlocks.THALLASIUM.getBlock(ORE));
        addEndGround(context, EndBlocks.ENDSTONE_DUST);
        addEndGround(context, EndBlocks.AMBER_ORE);
        addEndGround(context, EndBlocks.CAVE_MOSS);

        context.add(
                CommonBlockTags.END_STONES,
                EndBlocks.ENDER_ORE,
                EndBlocks.BRIMSTONE
        );
        context.add(CommonBlockTags.END_STONES, EndBlocks.BRIMSTONE);
        context.add(BlockTags.ANVIL, EndBlocks.AETERNIUM_ANVIL);
        context.add(BlockTags.BEACON_BASE_BLOCKS, EndBlocks.AETERNIUM_BLOCK);
        context.add(
                BlockTags.DRAGON_IMMUNE,
                EndBlocks.ENDER_ORE,
                EndBlocks.ETERNAL_PEDESTAL,
                EndBlocks.FLAVOLITE_RUNED_ETERNAL,
                EndBlocks.FLAVOLITE_RUNED
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
                EndBlocks.CHARNIA_CYAN,
                EndBlocks.CHARNIA_GREEN,
                EndBlocks.CHARNIA_ORANGE,
                EndBlocks.CHARNIA_LIGHT_BLUE,
                EndBlocks.CHARNIA_PURPLE,
                EndBlocks.CHARNIA_RED
        };

        for (Block charnia : charnias) {
            context.add(EndTags.BONEMEAL_SOURCE_WATER_GRASS, charnia);
        }

        addSurvivesOn(context);
    }
}
