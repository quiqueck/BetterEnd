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
 * Geometry (all derived from the endpoints + seed, so every chunk agrees without cross-chunk reads):
 * a 3-wide deck following a gentle upward arch between the endpoint heights, end-stone-brick-wall
 * railings on both edges (with ~20% ruined gaps), a 1-block brick underside, brick support pillars
 * every 8 blocks where terrain sits within 12 blocks below the deck, and 5-wide ramp landings seated
 * into the terrain at each end. Deck blocks replace only air/replaceable columns (mid-span columns that
 * would clip into solid terrain are skipped); landings are the sole exception and seat onto the ground.
 */
public class EndBridgePiece extends BasePiece {
    private static final BlockState DECK = Blocks.END_STONE_BRICKS.defaultBlockState();
    private static final BlockState CRACKED =
            EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.CRACKED_SOURCE).defaultBlockState();
    private static final BlockState WEATHERED =
            EndStoneBlocks.END_STONE_BRICK_VARIATIONS.getBlock(StoneSlots.WEATHERED_SOURCE).defaultBlockState();
    private static final BlockState RAILING = Blocks.END_STONE_BRICK_WALL.defaultBlockState();

    private static final int LANDING_LEN = 4;   // last N blocks of each end widen into a ramp landing
    private static final int PILLAR_SPACING = 8;
    private static final int PILLAR_MAX_DROP = 12;

    private static final float WEATHERED_CHANCE = 0.10f;
    private static final float CRACKED_CHANCE = 0.15f; // applied after weathered
    private static final float RAILING_GAP_CHANCE = 0.20f;

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
        final ChunkAccess chunk = world.getChunk(chunkPos.x, chunkPos.z);
        final int sx = SectionPos.sectionToBlockCoord(chunkPos.x);
        final int sz = SectionPos.sectionToBlockCoord(chunkPos.z);
        final int chunkMinY = chunk.getMinY();

        final int x0 = Math.max(boundingBox.minX(), sx);
        final int x1 = Math.min(boundingBox.maxX(), sx + 15);
        final int z0 = Math.max(boundingBox.minZ(), sz);
        final int z1 = Math.min(boundingBox.maxZ(), sz + 15);
        if (x0 > x1 || z0 > z1) return;

        final double ax = start.getX() + 0.5;
        final double az = start.getZ() + 0.5;
        final double ay = start.getY();
        final double by = end.getY();
        final double dxSeg = (end.getX() + 0.5) - ax;
        final double dzSeg = (end.getZ() + 0.5) - az;
        final double segLenSq = dxSeg * dxSeg + dzSeg * dzSeg;
        if (segLenSq < 1.0) return;
        final double spanLen = Math.sqrt(segLenSq);
        final double rise = Mth.clamp(spanLen / 20.0, 2.0, 5.0);

        final MutableBlockPos POS = new MutableBlockPos();

        for (int x = x0; x <= x1; x++) {
            for (int z = z0; z <= z1; z++) {
                // Project column centre onto the span.
                double t = ((x + 0.5 - ax) * dxSeg + (z + 0.5 - az) * dzSeg) / segLenSq;
                final double tc = Mth.clamp(t, 0.0, 1.0);
                final double cx = ax + tc * dxSeg;
                final double cz = az + tc * dzSeg;
                final double perp = Math.sqrt((x + 0.5 - cx) * (x + 0.5 - cx) + (z + 0.5 - cz) * (z + 0.5 - cz));

                final double along = tc * spanLen;
                final double distEnd = Math.min(along, spanLen - along);
                final boolean landing = distEnd < LANDING_LEN;
                final double halfWidth = landing ? 2.0 : 1.0; // deck width 5 (ramp) or 3

                if (perp > halfWidth + 0.5) continue; // outside the deck footprint

                final int deckY = Mth.floor(Mth.lerp(tc, ay, by) + rise * 4.0 * tc * (1.0 - tc) + 0.5);

                // Deterministic, chunk-independent per-column RNG so neighbouring chunks agree.
                final RandomSource colRandom = RandomSource.create(
                        seed
                                ^ ((long) x * 0x9E3779B97F4A7C15L)
                                ^ ((long) z * 0xC2B2AE3D27D4EB4FL)
                );

                // Mid-span columns that would clip into a hill are skipped (bridge is clipped naturally).
                // Landings are the exception: they seat onto the terrain at the endpoints.
                if (!landing) {
                    POS.set(x, deckY, z);
                    if (isTerrain(chunk.getBlockState(POS))) continue;
                    POS.setY(deckY + 1);
                    if (isTerrain(chunk.getBlockState(POS))) continue;
                }

                // ---- Deck ------------------------------------------------------------------------
                POS.set(x, deckY, z);
                if (landing || isReplaceable(chunk.getBlockState(POS))) {
                    chunk.setBlockState(POS, deckMaterial(colRandom), 3);
                }

                // ---- Underside (1 block thick) ---------------------------------------------------
                POS.setY(deckY - 1);
                if (isReplaceable(chunk.getBlockState(POS))) {
                    chunk.setBlockState(POS, deckMaterial(colRandom), 3);
                }

                // ---- Landing seating (fill one more block down onto terrain) ---------------------
                if (landing) {
                    POS.setY(deckY - 2);
                    if (isReplaceable(chunk.getBlockState(POS))) {
                        chunk.setBlockState(POS, deckMaterial(colRandom), 3);
                    }
                }

                // ---- Railings (edge columns of the 3-wide deck, ~20% ruined gaps) ---------------
                final boolean edge = perp > halfWidth - 0.5;
                if (edge && !landing) {
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
