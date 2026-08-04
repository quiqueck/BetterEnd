package org.betterx.betterend.world.structures.features;

import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.structures.piece.EndBridgePiece;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Procedurally builds a ruined End-stone-brick bridge spanning from a small island (or land edge)
 * to a nearby small island. Unlike the lake structures this one does not extend
 * {@link FeatureBaseStructure}: its generation origin sits in the small-island/void ring, so the
 * {@code y >= 10} gate that {@link FeatureBaseStructure} applies to the origin column would reject
 * almost every placement (the origin column is frequently pure void). Instead the origin column is
 * used only as the centre of a deterministic radial anchor scan, and the structure is valid whenever
 * that scan finds a usable pair of endpoints.
 * <p>
 * All maths here is deterministic (only {@code getBaseHeight}, {@code getNoiseBiome} and the seeded
 * {@link net.minecraft.world.level.levelgen.structure.Structure.GenerationContext#random()}), so
 * every chunk that {@link EndBridgePiece} touches agrees on the geometry without any chunk access.
 */
public class EndBridgeStructure extends FeatureBaseStructure {
    // Radial anchor scan around the origin column.
    private static final int SCAN_DIRECTIONS = 16;
    private static final int SCAN_MIN_RADIUS = 16;
    private static final int SCAN_MAX_RADIUS = 96;
    private static final int SCAN_RADIUS_STEP = 8;

    // "Has terrain" band. Over End void, getBaseHeight returns the noise floor (noiseMinY == 0 for the
    // End; see TerrainGenerator.getSurfaceHeight and NoiseBasedChunkGeneratorHeightMixin), and vanilla
    // End void likewise reports the build floor - both well below TERRAIN_MIN_Y - so a value inside
    // [TERRAIN_MIN_Y, TERRAIN_MAX_Y] reliably means "a real island/land column", not void.
    private static final int TERRAIN_MIN_Y = 30;
    private static final int TERRAIN_MAX_Y = 90;
    private static final int VOID_MARGIN = 5;

    // Endpoint-pair constraints.
    private static final int MIN_SPAN = 24;
    private static final int MAX_SPAN = 96;
    private static final int MAX_HEIGHT_DELTA = 12;
    private static final double MIN_DIRECTION_SEPARATION = Math.PI / 2.0; // >= 90 degrees apart

    public EndBridgeStructure(Structure.StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    public StructureType<?> type() {
        return EndStructures.END_BRIDGE.type();
    }

    /**
     * Bridges may start over the void, so we cannot reuse {@link FeatureBaseStructure}'s
     * {@code y >= 10} origin gate. We always hand back a generation stub (rooted at the chunk centre)
     * and let {@link #generatePieces} decide - it simply adds no piece when no valid anchor pair
     * exists, which the vanilla structure machinery treats as "no structure here".
     */
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        final ChunkPos chunkPos = context.chunkPos();
        final BlockPos origin = new BlockPos(chunkPos.getBlockX(8), 0, chunkPos.getBlockZ(8));
        return Optional.of(new Structure.GenerationStub(
                origin,
                (structurePiecesBuilder) -> generatePieces(structurePiecesBuilder, context)
        ));
    }

    private record Anchor(int x, int y, int z, double angle, boolean island) {
        int distSqTo(Anchor o) {
            int dx = x - o.x;
            int dz = z - o.z;
            return dx * dx + dz * dz;
        }
    }

    @Override
    protected void generatePieces(StructurePiecesBuilder structurePiecesBuilder, Structure.GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();
        final ChunkGenerator chunkGenerator = context.chunkGenerator();
        final RandomState rState = context.randomState();
        final LevelHeightAccessor level = context.heightAccessor();
        final int minY = level.getMinY();

        final int ox = chunkPos.getBlockX(8);
        final int oz = chunkPos.getBlockZ(8);

        // ---- Anchor discovery -------------------------------------------------------------------
        // Scan SCAN_DIRECTIONS evenly spaced directions; in each direction take the nearest column that
        // has real terrain (island or land edge). Deterministic: same inputs -> same candidate list.
        final List<Anchor> candidates = new ArrayList<>();
        for (int d = 0; d < SCAN_DIRECTIONS; d++) {
            final double angle = (2.0 * Math.PI * d) / SCAN_DIRECTIONS;
            final double dx = Math.cos(angle);
            final double dz = Math.sin(angle);
            for (int r = SCAN_MIN_RADIUS; r <= SCAN_MAX_RADIUS; r += SCAN_RADIUS_STEP) {
                final int x = ox + (int) Math.round(dx * r);
                final int z = oz + (int) Math.round(dz * r);
                final int y = chunkGenerator.getBaseHeight(x, z, Types.WORLD_SURFACE_WG, level, rState);
                if (!hasTerrain(y, minY)) continue;

                final Holder<Biome> biome = getNoiseBiome(chunkGenerator, rState, x >> 2, y >> 2, z >> 2);
                final boolean island = biome.is(CommonBiomeTags.IS_SMALL_END_ISLAND);
                final boolean land = biome.is(CommonBiomeTags.IS_END_HIGHLAND)
                        || biome.is(CommonBiomeTags.IS_END_MIDLAND);
                if (island || land) {
                    candidates.add(new Anchor(x, y, z, angle, island));
                    break; // nearest terrain in this direction only
                }
            }
        }

        if (candidates.size() < 2) return;

        // ---- Pick the endpoint pair -------------------------------------------------------------
        // Order candidates by distance from the origin so A is the nearest anchor and B is the nearest
        // partner that satisfies every constraint (>= 90 degrees away, span in range, small height
        // delta, at least one endpoint a small island - a pure land-to-land bridge is not built).
        candidates.sort((a, b) -> Integer.compare(
                (a.x - ox) * (a.x - ox) + (a.z - oz) * (a.z - oz),
                (b.x - ox) * (b.x - ox) + (b.z - oz) * (b.z - oz)
        ));

        Anchor a = candidates.get(0);
        Anchor b = null;
        for (int i = 1; i < candidates.size(); i++) {
            final Anchor cand = candidates.get(i);
            if (!a.island && !cand.island) continue;                       // need at least one island
            if (Math.abs(a.y - cand.y) > MAX_HEIGHT_DELTA) continue;
            final double sep = angularSeparation(a.angle, cand.angle);
            if (sep < MIN_DIRECTION_SEPARATION) continue;
            final double span = Math.sqrt(a.distSqTo(cand));
            if (span < MIN_SPAN || span > MAX_SPAN) continue;
            b = cand;
            break;
        }

        if (b == null) return;

        final EndBridgePiece piece = new EndBridgePiece(
                new BlockPos(a.x, a.y, a.z),
                new BlockPos(b.x, b.y, b.z),
                random.nextLong()
        );
        structurePiecesBuilder.addPiece(piece);
    }

    /**
     * "Has terrain" predicate. Requires the sampled base height to sit clearly above the void floor
     * ({@code minY + VOID_MARGIN}) and inside the plausible island/land band. Over End void both the
     * BetterEnd island override and the vanilla End density report the build floor (see
     * {@link org.betterx.betterend.world.generator.TerrainGenerator#getSurfaceHeight} which returns
     * {@code noiseMinY} for an all-air column, and the vanilla End's negative void density which yields
     * the same), so those columns fall below {@link #TERRAIN_MIN_Y} and are rejected.
     */
    private static boolean hasTerrain(int baseHeight, int minY) {
        return baseHeight > minY + VOID_MARGIN
                && baseHeight >= TERRAIN_MIN_Y
                && baseHeight <= TERRAIN_MAX_Y;
    }

    private static double angularSeparation(double a, double b) {
        double diff = Math.abs(a - b) % (2.0 * Math.PI);
        if (diff > Math.PI) diff = 2.0 * Math.PI - diff;
        return diff;
    }
}
