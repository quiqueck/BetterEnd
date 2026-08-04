package org.betterx.betterend.world.structures.features;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.piece.SulphuricCavePiece;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

/**
 * Replaces the old {@code SulphuricCaveFeature} (a decoration feature whose ~35-block-radius
 * scan/carve reached past the FEATURES chunk-status's 1-chunk write radius, producing "Detected
 * unsafe terrain read during worldgen" warnings). Structures generate their piece's blocks per
 * intersecting chunk via {@link SulphuricCavePiece#postProcess} - the same mechanism vanilla itself
 * uses for multi-chunk structures - so there is no analogous write-radius limit here, and the whole
 * legacy algorithm (real {@code WATER}/rock-shell/brimstone/vent placement) ports into the piece
 * without needing a carver + decoration-feature split. See {@code EndLakeStructure}, which replaced
 * {@code EndLakeFeature} for the identical reason.
 * <p>
 * {@link #generatePieces} must find its anchor point using only {@link ChunkGenerator#getBaseHeight}
 * (via {@link RandomState}) - the same generator-agnostic height query {@code EndLakeStructure} and
 * {@code SDFStructureFeature} already use - because BetterEnd must generate correctly under both End
 * terrain generators ({@code WoverEndConfig.EndBiomeGeneratorType.VANILLA} and {@code .PAULEVS}), and
 * BetterEnd's own {@code TerrainGenerator} density state only reflects real terrain under
 * {@code PAULEVS}. The legacy feature additionally scanned real blocks to center its sphere somewhere
 * between "just under the surface" and "the bottom of the island's rock mass"; that live scan needs a
 * chunk this structure-start context does not have, so the vertical anchor here is instead a
 * surface-relative offset of the same rough magnitude (the legacy {@code radius*1.3+5} margin). Any
 * part of the sphere that ends up in void/thin terrain simply carves nothing there in
 * {@link SulphuricCavePiece#postProcess} - graceful degradation, not a crash, the same category of
 * approximation {@code EndCaveCarver}/{@code EndLakeStructure} already accept elsewhere.
 */
public class EndSulphuricCaveStructure extends FeatureBaseStructure {
    public EndSulphuricCaveStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public StructureType<?> type() {
        return EndStructures.SULPHURIC_CAVE.type();
    }

    @Override
    protected void generatePieces(StructurePiecesBuilder structurePiecesBuilder, Structure.GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final RandomState rState = context.randomState();
        final LevelHeightAccessor levelHeightAccessor = context.heightAccessor();

        final int x = chunkPos.getBlockX(MHelper.randRange(4, 12, random));
        final int z = chunkPos.getBlockZ(MHelper.randRange(4, 12, random));
        final int surfaceY = chunkGenerator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG, levelHeightAccessor, rState);

        // Same "nothing here" guard as the legacy feature's own low-Y bail, and EndLakeStructure's
        // y < 10 guard - both mean "void floor, no real island at this column".
        if (surfaceY < 10) return;

        final float radius = (float) MHelper.randRange(10.0, 30.0, random);

        // See class javadoc: approximates the legacy top/bottom scan's window without needing a live
        // block scan. margin mirrors the legacy radius*1.3+5 constant used on both sides of that window.
        final int margin = MHelper.floor(radius * 1.3F + 5);
        final int y = surfaceY - margin - random.nextInt(Math.max(1, MHelper.floor(radius)));
        if (y < 10) return;

        final SulphuricCavePiece piece = new SulphuricCavePiece(new BlockPos(x, y, z), radius, random);
        structurePiecesBuilder.addPiece(piece);
    }
}
