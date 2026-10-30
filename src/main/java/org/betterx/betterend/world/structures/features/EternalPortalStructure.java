package org.betterx.betterend.world.structures.features;

import org.betterx.bclib.api.v2.levelgen.structures.StructurePlacementType;
import org.betterx.bclib.api.v2.levelgen.structures.TemplateStructure;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import de.ambertation.wover.structure.api.StructureUtils;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.List;
import java.util.Optional;

public class EternalPortalStructure extends TemplateStructure {
    public EternalPortalStructure(StructureSettings s) {
        super(s, List.of(
                        cfg("portal/eternal_portal", -2, StructurePlacementType.FLOOR, 1.0f)
                )
        );
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!StructureUtils.isValidBiome(context)) return Optional.empty();

        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final LevelHeightAccessor levelHeightAccessor = context.heightAccessor();

        long x = (long) chunkPos.x() * (long) chunkPos.x();
        long z = (long) chunkPos.z() * (long) chunkPos.z();
        if (x + z < 1024L) {
            return Optional.empty();
        }
        if (chunkGenerator.getBaseHeight(
                chunkPos.getBlockX(8),
                chunkPos.getBlockZ(8),
                Heightmap.Types.WORLD_SURFACE_WG,
                levelHeightAccessor,
                context.randomState()
        ) < 5) {
            return Optional.empty();
        }
        return super.findGenerationPoint(context);
    }

    @Override
    public StructureType<EternalPortalStructure> type() {
        return EndStructures.ETERNAL_PORTAL.type();
    }

    @Override
    protected int erosion(RandomSource rnd) {
        return rnd.nextInt(5);
    }

    @Override
    protected boolean cover(RandomSource rnd) {
        return true;
    }

    /**
     * The runed frame is unbreakable, so erosion alone often leaves it whole; one of its blocks is always missing,
     * so a found portal has to be repaired before it can be lit.
     */
    @Override
    protected Block alwaysBroken() {
        return EndStoneBlocks.FLAVOLITE_RUNED_ETERNAL;
    }

    public static TemplateStructure.Config cfg(String name, int offsetY, StructurePlacementType type, float chance) {
        return new TemplateStructure.Config(BetterEnd.C.mk(name), offsetY, type, chance);
    }
}
