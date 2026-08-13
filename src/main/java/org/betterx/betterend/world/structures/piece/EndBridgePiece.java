package org.betterx.betterend.world.structures.piece;

import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.registry.EndStructures;
import de.ambertation.wover.sets.api.blocks.slots.StoneSlots;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * Per-chunk piece that builds one procedural End bridge between two anchor columns (a small island and
 * a nearby island or land edge). Like {@link EndLakePiece}, the piece stores only its deterministic
 * parameters (the two endpoints and a per-bridge seed); its {@link #boundingBox} spans every chunk the
 * span crosses, and each of those chunks writes only its own slice during {@code postProcess} via
 * {@link ChunkAccess} (world coordinates outside the current chunk are masked away by {@code ChunkAccess}).
 * <p>
 * The footprint itself lives in {@link EndBridgeGeometry} (endpoints only, no world reads, so every
 * chunk agrees without cross-chunk reads). On top of it this piece writes: a 5-column deck following a
 * gentle upward arch between the endpoint heights, end-stone-brick-wall railings on the outer ring only
 * (with ~20% ruined gaps), a 1-block brick underside, brick support pillars every 8 blocks where terrain
 * sits within 12 blocks below the deck, and wider ramp landings seated into the terrain at each end.
 * Deck blocks replace only air/replaceable columns (mid-span columns that would clip into solid terrain
 * are skipped); landings are the sole exception and seat onto the ground.
 * <p>
 * Two rules keep a ruined bridge crossable. Railings go only on columns that
 * {@link EndBridgeGeometry#isEdge} reports as the outer ring, which leaves the 3-wide interior
 * continuously walkable at any bridge angle (see EndBridgeWalkwayTest). And the erosion pass that drops
 * ~5% of the deck surface never drops the underside of the same column, so an eroded spot is a shallow
 * dip rather than a hole through the deck.
 */
public class EndBridgePiece extends BasePiece {
    private static final BlockState DECK = Blocks.END_STONE_BRICKS.defaultBlockState();
    private static final BlockState CRACKED =
            EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED_SOURCE).defaultBlockState();
    private static final BlockState WEATHERED =
            EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED_SOURCE).defaultBlockState();
    private static final BlockState RAILING = Blocks.END_STONE_BRICK_WALL.defaultBlockState();

    private static final int PILLAR_SPACING = 8;
    private static final int PILLAR_MAX_DROP = 12;

    private static final float WEATHERED_CHANCE = 0.10f;
    private static final float CRACKED_CHANCE = 0.15f; // applied after weathered
    private static final float RAILING_GAP_CHANCE = 0.20f;
    private static final float EROSION_CHANCE = 0.05f;

    private BlockPos start;
    private BlockPos end;
    private long seed;

    public EndBridgePiece(BlockPos start, BlockPos end, long seed) {
        super(EndStructures.END_BRIDGE_PIECE, (int) seed, null);
        this.start = start;
        this.end = end;
        this.seed = seed;
        makeBoundingBox();
    }

    public EndBridgePiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.END_BRIDGE_PIECE, tag);
        makeBoundingBox();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.store("start", BlockPos.CODEC, start);
        tag.store("end", BlockPos.CODEC, end);
        tag.putLong("seed", seed);
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        start = tag.read("start", BlockPos.CODEC).orElse(BlockPos.ZERO);
        end = tag.read("end", BlockPos.CODEC).orElse(BlockPos.ZERO);
        seed = tag.getLongOr("seed", 0L);
    }

    private void makeBoundingBox() {
        final int minX = Math.min(start.getX(), end.getX()) - 6;
        final int maxX = Math.max(start.getX(), end.getX()) + 6;
        final int minZ = Math.min(start.getZ(), end.getZ()) - 6;
        final int maxZ = Math.max(start.getZ(), end.getZ()) + 6;
        final int minY = Math.min(start.getY(), end.getY()) - PILLAR_MAX_DROP;
        final int maxY = Math.max(start.getY(), end.getY()) + 6;
        this.boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static boolean isReplaceable(BlockState state) {
        return state.isAir()
                || BlocksHelper.replaceableOrPlant(state)
                || !state.getFluidState().isEmpty();
    }

    private static boolean isTerrain(BlockState state) {
        return !isReplaceable(state);
    }

    private BlockState deckMaterial(RandomSource random) {
        final float r = random.nextFloat();
        if (r < WEATHERED_CHANCE) return WEATHERED;
        if (r < WEATHERED_CHANCE + CRACKED_CHANCE) return CRACKED;
        return DECK;
    }

    @Override
    public void postProcess(
            WorldGenLevel world,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox blockBox,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        final ChunkAccess chunk = world.getChunk(chunkPos.x(), chunkPos.z());
        final int sx = SectionPos.sectionToBlockCoord(chunkPos.x());
        final int sz = SectionPos.sectionToBlockCoord(chunkPos.z());
        final int chunkMinY = chunk.getMinY();

        final int x0 = Math.max(boundingBox.minX(), sx);
        final int x1 = Math.min(boundingBox.maxX(), sx + 15);
        final int z0 = Math.max(boundingBox.minZ(), sz);
        final int z1 = Math.min(boundingBox.maxZ(), sz + 15);
        if (x0 > x1 || z0 > z1) return;

        final EndBridgeGeometry span = new EndBridgeGeometry(start, end);
        if (span.lengthSq() < 1.0) return;

        final MutableBlockPos POS = new MutableBlockPos();

        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                // Project column centre onto the span.
                final double tc = span.t(x, z);
                final double perp = span.perp(x, z, tc);
                final boolean landing = span.isLanding(tc);
                final double halfWidth = span.halfWidth(tc);

                if (perp > halfWidth + 0.5) continue; // outside the deck footprint

                final double along = span.along(tc);
                final int deckY = span.deckY(tc);

                // Deterministic, chunk-independent per-column RNG so neighbouring chunks agree.
                final RandomSource colRandom = RandomSource.create(
                        seed
                                ^ ((long) x * 0x9E3779B97F4A7C15L)
                                ^ ((long) z * 0xC2B2AE3D27D4EB4FL)
                );

                // Erosion: ~5% of the deck surface is missing, another ~5% of the underside. Never both
                // in the same column, so an eroded column is a shallow dip instead of a hole to fall
                // through.
                final boolean erodeDeck = colRandom.nextFloat() < EROSION_CHANCE;
                final boolean erodeUnderside = !erodeDeck && colRandom.nextFloat() < EROSION_CHANCE;

                // Mid-span columns that would clip into a hill are skipped (bridge is clipped naturally).
                // Landings are the exception: they seat onto the terrain at the endpoints.
                if (!landing) {
                    POS.set(x, deckY, z);
                    if (isTerrain(chunk.getBlockState(POS))) continue;
                    POS.setY(deckY + 1);
                    if (isTerrain(chunk.getBlockState(POS))) continue;
                }

                // ---- Deck ------------------------------------------------------------------------
                boolean deckPlaced = false;
                POS.set(x, deckY, z);
                if (!erodeDeck && (landing || isReplaceable(chunk.getBlockState(POS)))) {
                    chunk.setBlockState(POS, deckMaterial(colRandom), 3);
                    deckPlaced = true;
                }

                // ---- Underside (1 block thick) ---------------------------------------------------
                POS.setY(deckY - 1);
                if (!erodeUnderside && isReplaceable(chunk.getBlockState(POS))) {
                    chunk.setBlockState(POS, deckMaterial(colRandom), 3);
                }

                // ---- Landing seating (fill one more block down onto terrain) ---------------------
                if (landing) {
                    POS.setY(deckY - 2);
                    if (isReplaceable(chunk.getBlockState(POS))) {
                        chunk.setBlockState(POS, deckMaterial(colRandom), 3);
                    }
                }

                // ---- Railings (outer ring of the deck only, ~20% ruined gaps) -------------------
                // Restricting them to edge columns keeps the interior walkable at every bridge angle;
                // a railing without deck under it (eroded away) is dropped as well.
                if (!landing && deckPlaced && span.isEdge(x, z)) {
                    POS.setY(deckY + 1);
                    if (colRandom.nextFloat() >= RAILING_GAP_CHANCE && isReplaceable(chunk.getBlockState(POS))) {
                        chunk.setBlockState(POS, RAILING, 3);
                    }
                }

                // ---- Support pillars (centreline, every PILLAR_SPACING blocks) -------------------
                final boolean centre = perp < 0.5;
                if (centre && !landing && Math.round(along) % PILLAR_SPACING == 0) {
                    final int bottom = Math.max(chunkMinY, deckY - 1 - PILLAR_MAX_DROP);
                    int terrainTop = Integer.MIN_VALUE;
                    for (int y = deckY - 2; y >= bottom; y--) {
                        POS.setY(y);
                        if (isTerrain(chunk.getBlockState(POS))) {
                            terrainTop = y;
                            break;
                        }
                    }
                    if (terrainTop != Integer.MIN_VALUE) {
                        for (int y = deckY - 2; y > terrainTop; y--) {
                            POS.setY(y);
                            if (isReplaceable(chunk.getBlockState(POS))) {
                                chunk.setBlockState(POS, deckMaterial(colRandom), 3);
                            }
                        }
                    }
                }
            }
        }
    }
}
