package org.betterx.datagen.betterend.worldgen;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndProcessors;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.village.VillagePools;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverStructureProvider;
import de.ambertation.wover.sets.api.blocks.slots.StoneSlots;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.structure.api.sets.StructureSetManager;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import org.jetbrains.annotations.NotNull;

public class StructureDataProvider extends WoverStructureProvider {
    public StructureDataProvider(@NotNull ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapSturctures(BootstrapContext<Structure> context) {
        EndStructures.GIANT_MOSSY_GLOWSHROOM.bootstrap(context).register();
        EndStructures.MEGALAKE.bootstrap(context).register();
        EndStructures.MEGALAKE_SMALL.bootstrap(context).register();
        EndStructures.END_LAKE.bootstrap(context).register();
        EndStructures.END_LAKE_NORMAL.bootstrap(context).register();
        EndStructures.END_LAKE_RARE.bootstrap(context).register();
        EndStructures.END_BRIDGE.bootstrap(context).register();
        EndStructures.MOUNTAIN.bootstrap(context).register();
        EndStructures.PAINTED_MOUNTAIN.bootstrap(context).register();
        EndStructures.ETERNAL_PORTAL.bootstrap(context).register();
        EndStructures.GIANT_ICE_STAR.bootstrap(context).register();
        EndStructures.SMALL_ISLAND.bootstrap(context).register();
        EndStructures.SULPHURIC_CAVE.bootstrap(context).register();
        EndStructures.END_VILLAGE
                .bootstrap(context)
                .startPool(VillagePools.START)
                .adjustment(TerrainAdjustment.BEARD_THIN)
                .projectStartToHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
                .maxDepth(6)
                .startHeight(ConstantHeight.of(VerticalAnchor.absolute(0)))
                .register();
    }

    @Override
    protected void bootstrapSets(BootstrapContext<StructureSet> context) {
        StructureSetManager
                .bootstrap(EndStructures.GIANT_MOSSY_GLOWSHROOM, context)
                .randomPlacement(16, 8)
                .register();
        StructureSetManager
                .bootstrap(EndStructures.MEGALAKE, context)
                .addStructure(EndStructures.MEGALAKE_SMALL)
                .randomPlacement(4, 1)
                .register();

        // The three End lake variants replace the old EndLakeFeature (onceEvery 4/20/40). A
        // RandomSpread with spacing S places roughly one structure per S*S chunks. The common variant's
        // spacing was bumped past the naive sqrt(chance)=2 estimate: unlike the old chunk-decoration
        // feature (real Bernoulli variance, its own rejection paths), a structure_set attempt nearly
        // always succeeds, so the naive conversion read as far too dense in practice.
        StructureSetManager
                .bootstrap(EndStructures.END_LAKE, context)
                .randomPlacement(6, 1)
                .register();
        StructureSetManager
                .bootstrap(EndStructures.END_LAKE_NORMAL, context)
                .randomPlacement(5, 2)
                .register();
        StructureSetManager
                .bootstrap(EndStructures.END_LAKE_RARE, context)
                .randomPlacement(6, 2)
                .register();
        StructureSetManager
                .bootstrap(EndStructures.END_BRIDGE, context)
                .randomPlacement(6, 2)
                .register();
        StructureSetManager
                .bootstrap(EndStructures.MOUNTAIN, context)
                .addStructure(EndStructures.PAINTED_MOUNTAIN)
                .randomPlacement(3, 2)
                .register();
        StructureSetManager
                .bootstrap(EndStructures.ETERNAL_PORTAL, context)
                .randomPlacement(40, 12)
                .register();
        StructureSetManager
                .bootstrap(EndStructures.GIANT_ICE_STAR, context)
                .randomPlacement(16, 8)
                .register();

        // Spacing 5 / separation 2 (previously 2 / 1): ~6x fewer islands so a flower/pond patch reads
        // as a handful of scattered islets rather than a dense, regular polka-dot grid. The larger
        // in-cell separation also jitters each island off the lattice. Biome-gated via has_structure so
        // it only spawns inside flower_islets / waterfall_ponds.
        StructureSetManager
                .bootstrap(EndStructures.SMALL_ISLAND, context)
                .randomPlacement(5, 2)
                .register();

        // Replaces the legacy SulphuricCaveFeature's count(2)-per-chunk placement. Per the END_LAKE
        // comment above, a structure_set attempt nearly always succeeds (no per-attempt Bernoulli
        // rejection the way a feature had), so this starts noticeably wider than a naive sqrt(chance)
        // translation of "2 per chunk" would suggest - tune from the manual smoke test.
        StructureSetManager
                .bootstrap(EndStructures.SULPHURIC_CAVE, context)
                .randomPlacement(9, 4)
                .register();

        StructureSetManager
                .bootstrap(EndStructures.END_VILLAGE, context)
                .randomPlacement(34, 8)
                .register();
    }

    @Override
    protected void bootstrapPools(BootstrapContext<StructureTemplatePool> context) {
        VillagePools.TERMINATORS_KEY
                .bootstrap(context)
                .startSingleEnd(BetterEnd.C.mk("village/terminators/stree_terminator_01")).emptyProcessor().endElement()
                .projection(StructureTemplatePool.Projection.TERRAIN_MATCHING)
                .register();

        VillagePools.START
                .bootstrap(context)
                .terminator(VillagePools.TERMINATORS_KEY)
                .startSingleEnd(BetterEnd.C.mk("village/center/light_pyramid_01"))
                .weight(2)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/center/light_pyramid_02"))
                .weight(1)
                .emptyProcessor()
                .endElement()
                .projection(StructureTemplatePool.Projection.RIGID)
                .register();

        VillagePools.HOUSES_KEY
                .bootstrap(context)
                .terminator(VillagePools.TERMINATORS_KEY)
                .addEmptyElement(5)
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_01"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_02"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_03"))
                .weight(2)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_04"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_05"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_06"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_07"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_08"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_09"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_10"))
                .weight(2)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_11"))
                .weight(1)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_12"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_13"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_14"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_15"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_16"))
                .weight(2)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/small_house_17"))
                .weight(4)
                .processor(EndProcessors.CRACK_AND_WEATHER)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/houses/animal_pen_01"))
                .weight(3)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/decoration/stable_01"))
                .weight(2)
                .processor(EndProcessors.CRACK_20_PERCENT)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/decoration/pond_01"))
                .weight(1)
                .processor(EndProcessors.WEATHERED_10_PERCENT)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/decoration/respawn_01"))
                .weight(1)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/decoration/respawn_02"))
                .weight(1)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/decoration/fountain_01"))
                .weight(1)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/work_01"))
                .weight(1)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .projection(StructureTemplatePool.Projection.RIGID)
                .register();

        VillagePools.STREET_KEY
                .bootstrap(context)
                .terminator(VillagePools.TERMINATORS_KEY)
                .startSingleEnd(BetterEnd.C.mk("village/streets/street_01"))
                .weight(6)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/streets/street_02"))
                .weight(5)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/streets/street_03"))
                .weight(7)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/streets/street_04"))
                .weight(6)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/streets/curve_01"))
                .weight(8)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/streets/curve_02"))
                .weight(8)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/streets/t_crossing_01"))
                .weight(4)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/streets/t_crossing_02"))
                .weight(4)
                .processor(EndProcessors.END_STREET)
                .endElement()
                .projection(StructureTemplatePool.Projection.TERRAIN_MATCHING)
                .register();

        VillagePools.STREET_DECO_KEY
                .bootstrap(context)
                .terminator(VillagePools.TERMINATORS_KEY)
                .addEmptyElement(5)
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/lamp_02"))
                .weight(4)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/lamp_05"))
                .weight(2)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/lamp_06"))
                .weight(3)
                .emptyProcessor()
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/obsidian_01"))
                .weight(2)
                .processor(EndProcessors.CRYING_10_PERCENT)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/obsidian_02"))
                .weight(3)
                .processor(EndProcessors.CRYING_10_PERCENT)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/obsidian_03"))
                .weight(2)
                .processor(EndProcessors.CRYING_10_PERCENT)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/obsidian_04"))
                .weight(2)
                .processor(EndProcessors.CRYING_10_PERCENT)
                .endElement()
                .startSingleEnd(BetterEnd.C.mk("village/street_decoration/obsidian_05"))
                .weight(1)
                .processor(EndProcessors.CRYING_10_PERCENT)
                .endElement()
                .addFeature(VillagePools.CHORUS_VILLAGE.getHolder(context), 2)
                .projection(StructureTemplatePool.Projection.RIGID)
                .register();

        VillagePools.DECORATIONS_KEY
                .bootstrap(context)
                .terminator(VillagePools.TERMINATORS_KEY)
                .projection(StructureTemplatePool.Projection.RIGID)
                .register();

    }

    @Override
    protected void bootstrapProcessors(BootstrapContext<StructureProcessorList> bootstapContext) {
        EndProcessors
                .CRYING_10_PERCENT
                .bootstrap(bootstapContext).startRule().add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.OBSIDIAN, 0.1f),
                        AlwaysTrueTest.INSTANCE,
                        Blocks.CRYING_OBSIDIAN.defaultBlockState()
                )).endRule().register();

        EndProcessors
                .WEATHERED_10_PERCENT
                .bootstrap(bootstapContext).startRule().add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.1f),
                        AlwaysTrueTest.INSTANCE,
                        EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED_SOURCE).defaultBlockState()
                )).endRule().register();

        EndProcessors
                .CRACK_20_PERCENT
                .bootstrap(bootstapContext).startRule().add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.2f),
                        AlwaysTrueTest.INSTANCE,
                        EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED_SOURCE).defaultBlockState()
                )).endRule().register();

        EndProcessors
                .CRACK_AND_WEATHER
                .bootstrap(bootstapContext).startRule()
                .add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.2f),
                        AlwaysTrueTest.INSTANCE,
                        EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED_SOURCE).defaultBlockState()
                ))
                .add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.1f),
                        AlwaysTrueTest.INSTANCE,
                        EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED_SOURCE).defaultBlockState()
                ))
                .endRule().register();

        EndProcessors
                .END_STREET
                .bootstrap(bootstapContext).startRule()
                .add(new ProcessorRule(
                        new BlockMatchTest(Blocks.END_STONE_BRICKS),
                        new BlockMatchTest(Blocks.WATER),
                        EndWoodBlocks.PYTHADENDRON.getBlock(WoodSlots.PLANKS).defaultBlockState()
                ))
                .add(new ProcessorRule(
                        new BlockMatchTest(EndTerrainBlocks.ENDSTONE_DUST),
                        new BlockMatchTest(Blocks.WATER),
                        Blocks.WATER.defaultBlockState()
                ))
                .add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.03f),
                        AlwaysTrueTest.INSTANCE,
                        EndTerrainBlocks.SHADOW_GRASS_PATH.defaultBlockState()
                ))
                .add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.2f),
                        AlwaysTrueTest.INSTANCE,
                        EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED_SOURCE).defaultBlockState()
                ))
                .add(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.1f),
                        AlwaysTrueTest.INSTANCE,
                        EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED_SOURCE).defaultBlockState()
                ))
                .endRule().register();
    }

    @Override
    protected void prepareBiomeTags(TagBootstrapContext<Biome> context) {
        // The End bridge spawns in the small-island/void ring. The vanilla small-island biome is not a
        // BetterEnd biome class (so it cannot call `.structure(END_BRIDGE)` itself), so we add it to the
        // structure's `has_structure/end_bridge` biome tag here. Any BetterEnd biome tagged
        // IS_SMALL_END_ISLAND can additionally opt in via `.structure(EndStructures.END_BRIDGE)` in its
        // biome class; two dedicated void biomes arriving in a follow-up task will do exactly that.
        context.add(EndStructures.END_BRIDGE.biomeTag(), Biomes.SMALL_END_ISLANDS);
    }

}
