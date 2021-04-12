package ru.betterend.world.processors;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.WorldView;

public class TerrainStructureProcessor extends StructureProcessor {
	@Override
	public StructureBlockInfo process(WorldView worldView, BlockPos pos, BlockPos blockPos,
			StructureBlockInfo structureBlockInfo, StructureBlockInfo structureBlockInfo2,
			StructurePlacementData structurePlacementData) {
		BlockPos bpos = structureBlockInfo2.pos;
		if (structureBlockInfo2.state.is(Blocks.END_STONE) && worldView.isAir(bpos.up())) {
			BlockState top = worldView.getBiome(structureBlockInfo2.pos).getGenerationSettings()
					.getSurfaceBuilderConfig().getTopMaterial();
			return new StructureBlockInfo(bpos, top, structureBlockInfo2.tag);
		}
		return structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.RULE;
	}
}
