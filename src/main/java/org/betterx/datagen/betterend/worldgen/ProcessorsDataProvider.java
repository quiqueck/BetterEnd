package org.betterx.datagen.betterend.worldgen;

import org.betterx.bclib.api.v3.datagen.ProcessorHelper;
import org.betterx.bclib.complexmaterials.set.stone.StoneSlots;
import org.betterx.bclib.complexmaterials.set.wood.WoodSlots;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndProcessors;
import org.betterx.betterend.registry.EndTags;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import com.google.common.collect.ImmutableList;

public class ProcessorsDataProvider {

    public static void bootstrap(BootstapContext<StructureProcessorList> bootstapContext) {
        // Neither template air nor template end stone replace the ground. Air in a building sunk into a slope
        // would cut away the surface block the surface rules painted; plain end stone only appears in the
        // bottom layer of some templates (Umbrella Jungle jellyshroom_cluster, ruins_2/6) as a foundation and
        // would show up as bare stone. beard_thin already shapes the ground in the noise, where the surface
        // rules still paint it, and the vegetation fills the rest.
        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.BIOME_BUILDING,
                ImmutableList.of(new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK, Blocks.AIR, Blocks.END_STONE)))
        );
        // ... except for a building set into a mound of end stone (Umbrella Jungle house_1): its end stone is the
        // mound and has to stay.
        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.BIOME_BUILDING_MOUND,
                ImmutableList.of(new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK, Blocks.AIR)))
        );

        // Template air does not carve the ground the ship is buried in, and a quarter of the hull is gone.
        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.CRASHED_SHIP,
                ImmutableList.of(
                        new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK, Blocks.AIR)),
                        new BlockRotProcessor(bootstapContext.lookup(Registries.BLOCK).getOrThrow(EndTags.CRASHED_SHIP_ROTS), 0.75f)
                )
        );
        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.CRYING_10_PERCENT,
                ImmutableList.of(new RuleProcessor(ImmutableList.of(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.OBSIDIAN, 0.1f),
                        AlwaysTrueTest.INSTANCE,
                        Blocks.CRYING_OBSIDIAN.defaultBlockState()
                ))))
        );
        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.WEATHERED_10_PERCENT,
                ImmutableList.of(new RuleProcessor(ImmutableList.of(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.1f),
                        AlwaysTrueTest.INSTANCE,
                        EndBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED).defaultBlockState()
                ))))
        );

        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.CRACK_20_PERCENT,
                ImmutableList.of(new RuleProcessor(ImmutableList.of(new ProcessorRule(
                        new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.2f),
                        AlwaysTrueTest.INSTANCE,
                        EndBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED).defaultBlockState()
                ))))
        );

        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.CRACK_AND_WEATHER,
                ImmutableList.of(new RuleProcessor(ImmutableList.of(
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.2f),
                                AlwaysTrueTest.INSTANCE,
                                EndBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED).defaultBlockState()
                        ),
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.1f),
                                AlwaysTrueTest.INSTANCE,
                                EndBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED).defaultBlockState()
                        )
                )))
        );

        ProcessorHelper.register(
                bootstapContext,
                EndProcessors.END_STREET,
                ImmutableList.of(new RuleProcessor(ImmutableList.of(

                        new ProcessorRule(
                                new BlockMatchTest(Blocks.END_STONE_BRICKS),
                                new BlockMatchTest(Blocks.WATER),
                                EndBlocks.PYTHADENDRON.getBlock(WoodSlots.PLANKS).defaultBlockState()
                        ),
                        new ProcessorRule(
                                new BlockMatchTest(EndBlocks.ENDSTONE_DUST),
                                new BlockMatchTest(Blocks.WATER),
                                Blocks.WATER.defaultBlockState()
                        ),
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.03f),
                                AlwaysTrueTest.INSTANCE,
                                EndBlocks.SHADOW_GRASS_PATH.defaultBlockState()
                        ),
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.2f),
                                AlwaysTrueTest.INSTANCE,
                                EndBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED).defaultBlockState()
                        ),
                        new ProcessorRule(
                                new RandomBlockMatchTest(Blocks.END_STONE_BRICKS, 0.1f),
                                AlwaysTrueTest.INSTANCE,
                                EndBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED).defaultBlockState()
                        )
                )))
        );

    }
}
