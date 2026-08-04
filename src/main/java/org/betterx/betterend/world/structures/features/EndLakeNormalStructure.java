package org.betterx.betterend.world.structures.features;

import org.betterx.betterend.registry.EndStructures;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

/**
 * The medium-density End lake variant. Shares all carve logic with {@link EndLakeStructure}; only
 * its {@link #type()} (and its structure-set spacing) differ.
 */
public class EndLakeNormalStructure extends EndLakeStructure {
    public EndLakeNormalStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public StructureType<?> type() {
        return EndStructures.END_LAKE_NORMAL.type();
    }
}
