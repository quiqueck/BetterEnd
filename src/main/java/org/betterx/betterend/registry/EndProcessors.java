package org.betterx.betterend.registry;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.structure.api.StructureKeys;
import de.ambertation.wover.structure.api.processors.StructureProcessorKey;

public class EndProcessors {
    public static final StructureProcessorKey WEATHERED_10_PERCENT = StructureKeys.processor(BetterEnd.C.mk("weather_10_percent"));
    public static final StructureProcessorKey CRACK_20_PERCENT = StructureKeys.processor(BetterEnd.C.mk("crack_20_percent"));
    public static final StructureProcessorKey CRACK_AND_WEATHER = StructureKeys.processor(BetterEnd.C.mk("crack_and_weather"));
    public static final StructureProcessorKey END_STREET = StructureKeys.processor(BetterEnd.C.mk("end_street"));
    public static final StructureProcessorKey CRYING_10_PERCENT = StructureKeys.processor(BetterEnd.C.mk("crying_10_percent"));
}
