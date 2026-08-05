package org.betterx.betterend.world.structures.features;

import org.betterx.bclib.api.v2.levelgen.structures.StructurePlacementType;
import org.betterx.bclib.api.v2.levelgen.structures.TemplateStructure;
import org.betterx.bclib.util.StructureHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.piece.NBTPiece;
import de.ambertation.wover.structure.api.StructureUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;
import java.util.Optional;

public class EternalPortalStructure extends TemplateStructure {
    private static final Identifier STRUCTURE_ID = BetterEnd.C.mk("portal/eternal_portal");
    private static final StructureTemplate STRUCTURE = StructureHelper.readStructure(STRUCTURE_ID);

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

    protected void generatePieces(StructurePiecesBuilder structurePiecesBuilder, GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final LevelHeightAccessor levelHeightAccessor = context.heightAccessor();
        int x = chunkPos.getBlockX(8);
        int z = chunkPos.getBlockZ(8);
        int y = chunkGenerator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG, levelHeightAccessor, context.randomState());
        structurePiecesBuilder.addPiece(new NBTPiece(
                STRUCTURE_ID,
                STRUCTURE,
                new BlockPos(x, y - 4, z),
                random.nextInt(5),
                true,
                random
        ));
    }

    public static TemplateStructure.Config cfg(String name, int offsetY, StructurePlacementType type, float chance) {
        return new TemplateStructure.Config(BetterEnd.C.mk(name), offsetY, type, chance);
    }
}
