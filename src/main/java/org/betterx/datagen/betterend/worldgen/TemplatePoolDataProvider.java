package org.betterx.datagen.betterend.worldgen;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBiomeBuildings;
import org.betterx.betterend.registry.EndProcessors;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.village.VillagePools;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TemplatePoolDataProvider {

    public static void buildStructures() {

    }

    public static void bootstrap(BootstapContext<StructureTemplatePool> ctx) {
        VillagePools.bootstrap(ctx);

        final HolderGetter<StructureTemplatePool> poolGetter = ctx.lookup(Registries.TEMPLATE_POOL);
        final HolderGetter<StructureProcessorList> processorGetter = ctx.lookup(Registries.PROCESSOR_LIST);
        ctx.register(EndStructures.BLOSSOMING_SPIRES_HOUSE_POOL, new StructureTemplatePool(
                poolGetter.getOrThrow(Pools.EMPTY),
                ImmutableList.of(Pair.of(
                        StructurePoolElement.single(
                                BetterEnd.makeID("biome/blossoming_spires/house").toString(),
                                processorGetter.getOrThrow(ProcessorLists.EMPTY)
                        ),
                        1
                )),
                StructureTemplatePool.Projection.RIGID
        ));

        ctx.register(EndStructures.CRASHED_SHIP_POOL, new StructureTemplatePool(
                poolGetter.getOrThrow(Pools.EMPTY),
                ImmutableList.of(Pair.of(
                        StructurePoolElement.single(
                                BetterEnd.makeID("crashed_ship").toString(),
                                processorGetter.getOrThrow(EndProcessors.CRASHED_SHIP)
                        ),
                        1
                )),
                StructureTemplatePool.Projection.RIGID
        ));

        // all variants of a building with the same weight: the jigsaw picks one of them at random
        for (EndBiomeBuildings.Building building : EndBiomeBuildings.all()) {
            final var processors = processorGetter.getOrThrow(
                    building.mound() ? EndProcessors.BIOME_BUILDING_MOUND : EndProcessors.BIOME_BUILDING
            );
            final List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> elements = new ArrayList<>();
            for (ResourceLocation id : building.templateIds()) {
                elements.add(Pair.of(StructurePoolElement.single(id.toString(), processors), 1));
            }
            ctx.register(building.pool(), new StructureTemplatePool(
                    poolGetter.getOrThrow(Pools.EMPTY),
                    elements,
                    StructureTemplatePool.Projection.RIGID
            ));
        }
    }
}
