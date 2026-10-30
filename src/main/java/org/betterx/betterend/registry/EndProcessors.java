package org.betterx.betterend.registry;

import org.betterx.bclib.api.v3.datagen.ProcessorHelper;
import org.betterx.betterend.BetterEnd;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class EndProcessors {
    public static final ResourceKey<StructureProcessorList> WEATHERED_10_PERCENT =
            ProcessorHelper.createKey(BetterEnd.makeID("weather_10_percent"));
    public static final ResourceKey<StructureProcessorList> CRACK_20_PERCENT =
            ProcessorHelper.createKey(BetterEnd.makeID("crack_20_percent"));
    public static final ResourceKey<StructureProcessorList> CRACK_AND_WEATHER =
            ProcessorHelper.createKey(BetterEnd.makeID("crack_and_weather"));

    public static final ResourceKey<StructureProcessorList> END_STREET =
            ProcessorHelper.createKey(BetterEnd.makeID("end_street"));
    
    public static final ResourceKey<StructureProcessorList> BIOME_BUILDING =
            ProcessorHelper.createKey(BetterEnd.makeID("biome_building"));
    public static final ResourceKey<StructureProcessorList> BIOME_BUILDING_MOUND =
            ProcessorHelper.createKey(BetterEnd.makeID("biome_building_mound"));

    public static final ResourceKey<StructureProcessorList> CRASHED_SHIP =
            ProcessorHelper.createKey(BetterEnd.makeID("crashed_ship"));

    public static final ResourceKey<StructureProcessorList> CRYING_10_PERCENT =
            ProcessorHelper.createKey(BetterEnd.makeID("crying_10_percent"));
}
