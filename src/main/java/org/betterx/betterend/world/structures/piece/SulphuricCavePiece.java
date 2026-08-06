package org.betterx.betterend.world.structures.piece;

import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndWallPlantBlocks;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.world.features.terrain.SulphurFloorMix;
import org.betterx.betterend.blocks.EndBlockProperties;
import org.betterx.betterend.blocks.SulphurCrystalBlock;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.util.BlockFixer;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Per-chunk structure piece that carves a sulphuric cave: a direct port of the legacy
 * {@code SulphuricCaveFeature}'s carve, split across every chunk the shape touches the same way
 * {@link EndLakePiece} splits an End lake. All shape-defining randomness (center, radius, water
 * level, vent-cluster columns) is resolved once - either by {@code EndSulphuricCaveStructure} before
 * construction or in this constructor - and stored/serialized, because {@link #postProcess} runs
 * once per intersecting chunk and only ever sees that one chunk's real block data: nothing here may
 * depend on a live scan of a *different* chunk than the one currently being generated.
 * <p>
 * The one legacy behavior this intentionally drops: the old feature rejected the whole cave if a
 * live scan found the island too thin at the origin column. That scan needed real terrain and is not
 * reproducible generator-agnostically at structure-start time (see
 * {@code EndSulphuricCaveStructure}'s javadoc). Instead, the vertical center is a surface-relative
 * offset, and any part of the sphere that lands in void/thin terrain simply carves nothing there
 * (the same graceful degradation {@code EndLakePiece}'s own approximations already accept) rather
 * than rejecting the whole placement.
 */
public class SulphuricCavePiece extends BasePiece {
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();
    private static final Direction[] HORIZONTAL = BlocksHelper.makeHorizontal();

    private BlockPos center;
    private float radius;
    private int waterLevel;
    private int[] ventDX;
    private int[] ventDZ;

    public SulphuricCavePiece(BlockPos center, float radius, RandomSource random) {
        super(EndStructures.SULPHURIC_CAVE_PIECE, random.nextInt(), null);
        this.center = center;
        this.radius = radius;
        // Same water-level draw as legacy: somewhere in the upper 20-100% of the sphere's radius
        // above its center.
        this.waterLevel = center.getY() + MHelper.randRange(MHelper.floor(radius * 0.8), MHelper.floor(radius), random);
        resolveVentColumns(random);
        makeBoundingBox();
    }

    public SulphuricCavePiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.SULPHURIC_CAVE_PIECE, tag);
        makeBoundingBox();
    }

    /**
     * Legacy 1-in-4 chance for a hydrothermal-vent cluster, drawn once here (not per-chunk) so every
     * chunk the cave spans agrees on whether/where vents exist. Each column's jittered offset from
     * {@link #center} is resolved now too, for the same reason - {@link #postProcess} only decides
     * per column whether ITS chunk is responsible for drilling that one column.
     */
    private void resolveVentColumns(RandomSource random) {
        if (random.nextInt(4) != 0) {
            ventDX = new int[0];
            ventDZ = new int[0];
            return;
        }
        int count = MHelper.randRange(5, 20, random);
        ventDX = new int[count];
        ventDZ = new int[count];
        for (int i = 0; i < count; i++) {
            ventDX[i] = MHelper.floor(random.nextGaussian() * 2 + 0.5);
            ventDZ[i] = MHelper.floor(random.nextGaussian() * 2 + 0.5);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.store("center", BlockPos.CODEC, center);
        tag.putFloat("radius", radius);
        tag.putInt("water_level", waterLevel);
        tag.putIntArray("vent_dx", ventDX);
        tag.putIntArray("vent_dz", ventDZ);
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        center = tag.read("center", BlockPos.CODEC).orElse(BlockPos.ZERO);
        radius = tag.getFloatOr("radius", 0);
        waterLevel = tag.getIntOr("water_level", center.getY());
        ventDX = tag.getIntArray("vent_dx").orElse(new int[0]);
        ventDZ = tag.getIntArray("vent_dz").orElse(new int[0]);
    }

    private void makeBoundingBox() {
        int reach = MHelper.floor(radius) + 5;
        int minX = center.getX() - reach;
        int maxX = center.getX() + reach;
        int minZ = center.getZ() - reach;
        int maxZ = center.getZ() + reach;
        int minY = MHelper.floor(center.getY() - (reach) / 1.6) - 1;
        int maxY = MHelper.floor(center.getY() + (reach) / 1.6) + 1;
        this.boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private boolean isReplaceable(BlockState state) {
        return state.is(CommonBlockTags.END_STONES)
                // The sulfur/cinnabar this piece itself places must carve like the brimstone it replaced,
                // or a later pass removes the rock around it and leaves it standing in open water.
                || SulphurFloorMix.isDepositBlock(state)
                || state.is(EndStoneBlocks.HYDROTHERMAL_VENT)
                || state.is(EndStoneBlocks.VENT_BUBBLE_COLUMN)
                || state.is(EndStoneBlocks.SULPHUR_CRYSTAL)
                || BlocksHelper.replaceableOrPlant(state)
                || state.is(CommonBlockTags.WATER_PLANT)
                || state.is(BlockTags.LEAVES);
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

        final int cx = center.getX();
        final int cy = center.getY();
        final int cz = center.getZ();
        final int reach = MHelper.floor(radius) + 5;

        // Intersection of the cave's full footprint (center +/- reach) with the current chunk.
        final int x0 = Math.max(cx - reach, sx);
        final int x1 = Math.min(cx + reach, sx + 15);
        final int z0 = Math.max(cz - reach, sz);
        final int z1 = Math.min(cz + reach, sz + 15);
        if (x0 > x1 || z0 > z1) return;

        final int chunkMinY = chunk.getMinY();
        final int chunkMaxY = chunkMinY + chunk.getHeight() - 1;
        final int y1 = Math.max(MHelper.floor(cy - reach / 1.6), chunkMinY);
        final int y2 = Math.min(MHelper.floor(cy + reach / 1.6), chunkMaxY);

        final OpenSimplexNoise noise = new OpenSimplexNoise(MHelper.getSeed(534, cx, cz));
        final double hr = radius * 0.75;
        final double nr = radius * 0.25;

        final MutableBlockPos mut = new MutableBlockPos();
        final BlockState rock = EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE).defaultBlockState();
        final Set<BlockPos> brimstone = Sets.newHashSet();

        // ---- Sphere carve + outer shell (legacy main loop) ---------------------------------------
        for (int x = x0; x <= x1; x++) {
            final int xsq = (x - cx) * (x - cx);
            mut.setX(x);
            for (int z = z0; z <= z1; z++) {
                final int zsq = (z - cz) * (z - cz);
                mut.setZ(z);
                for (int y = y1; y <= y2; y++) {
                    double ysq = (y - cy) * 1.6;
                    ysq *= ysq;
                    mut.setY(y);
                    final double r = noise.eval(x * 0.1, y * 0.1, z * 0.1) * nr + hr;
                    final double r2 = r + 5;
                    final double dist = xsq + ysq + zsq;
                    if (dist < r * r) {
                        BlockState state = chunk.getBlockState(mut);
                        if (isReplaceable(state)) {
                            chunk.setBlockState(mut, y < waterLevel ? WATER : CAVE_AIR, 3);
                        }
                    } else if (dist < r2 * r2) {
                        BlockState state = chunk.getBlockState(mut);
                        if (state.is(CommonBlockTags.END_STONES) || state.is(Blocks.AIR)) {
                            double v = noise.eval(x * 0.1, y * 0.1, z * 0.1)
                                    + noise.eval(x * 0.03, y * 0.03, z * 0.03) * 0.5;
                            if (v > 0.4) {
                                brimstone.add(mut.immutable());
                            } else {
                                chunk.setBlockState(mut, rock, 3);
                            }
                        }
                    }
                }
            }
        }

        brimstone.forEach((pos) -> placeBrimstone(chunk, pos, random));

        placeSulfurSpikeClusters(chunk, random, x0, x1, z0, z1, y1, y2);

        // ---- Hydrothermal vent + tube-worm columns (legacy 25%-per-cave cluster) ------------------
        for (int i = 0; i < ventDX.length; i++) {
            final int wx = cx + ventDX[i];
            final int wz = cz + ventDZ[i];
            if (wx < x0 || wx > x1 || wz < z0 || wz > z1) continue; // this column belongs to another chunk
            placeVentColumn(world, chunk, random, wx, wz, cx, cz, chunkMinY, chunkMaxY);
        }

        BlockFixer.fixBlocks(world, new BlockPos(x0, y1, z0), new BlockPos(x1, y2, z1), blockBox);
    }

    private void placeVentColumn(
            WorldGenLevel world,
            ChunkAccess chunk,
            RandomSource random,
            int wx,
            int wz,
            int cx,
            int cz,
            int chunkMinY,
            int chunkMaxY
    ) {
        final int dist = MHelper.floor(3 - MHelper.length(wx - cx, wz - cz)) + random.nextInt(2);
        if (dist <= 0) return;

        final MutableBlockPos mut = new MutableBlockPos(wx, center.getY(), wz);
        BlockState state = chunk.getBlockState(mut);
        while (!state.getFluidState().isEmpty() || state.is(CommonBlockTags.WATER_PLANT)) {
            mut.setY(mut.getY() - 1);
            if (mut.getY() <= chunkMinY) return;
            state = chunk.getBlockState(mut);
        }
        if (!state.is(CommonBlockTags.END_STONES) || chunk.getBlockState(mut.above()).is(EndStoneBlocks.HYDROTHERMAL_VENT)) {
            return;
        }

        for (int j = 0; j <= dist; j++) {
            chunk.setBlockState(mut, EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE).defaultBlockState(), 3);
            MHelper.shuffle(HORIZONTAL, random);
            for (Direction dir : HORIZONTAL) {
                BlockPos p = mut.relative(dir);
                if (random.nextBoolean() && chunk.getBlockState(p).is(Blocks.WATER)) {
                    chunk.setBlockState(
                            p,
                            EndWallPlantBlocks.TUBE_WORM.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, dir),
                            3
                    );
                }
            }
            mut.setY(mut.getY() + 1);
        }
        chunk.setBlockState(mut, EndStoneBlocks.HYDROTHERMAL_VENT.defaultBlockState(), 3);
        mut.setY(mut.getY() + 1);
        state = chunk.getBlockState(mut);
        while (state.is(Blocks.WATER)) {
            chunk.setBlockState(mut, EndStoneBlocks.VENT_BUBBLE_COLUMN.defaultBlockState(), 3);
            world.scheduleTick(mut.immutable(), EndStoneBlocks.VENT_BUBBLE_COLUMN, MHelper.randRange(8, 32, random));
            mut.setY(mut.getY() + 1);
            if (mut.getY() > chunkMaxY) break;
            state = chunk.getBlockState(mut);
        }
    }

    private void placeBrimstone(ChunkAccess chunk, BlockPos pos, RandomSource random) {
        BlockState state = getBrimstone(chunk, pos);
        // Same deposit as the sulphur lake bed and the geyser bowl: where the shell is buried behind at
        // least SulphurFloorMix.MIN_DEPTH blocks of cover and touches no air, it can be vanilla sulfur or
        // (more rarely) cinnabar instead of brimstone. Active, water-touching brimstone always wins - it
        // carries the shards. See SulphurFloorMix for the shared rule.
        if (!state.getValue(EndBlockProperties.ACTIVE) && isBuriedInChunk(chunk, pos) && !facesOpenSpaceInChunk(chunk, pos)) {
            BlockState mixed = SulphurFloorMix.pickByNoise(pos.getX(), pos.getY(), pos.getZ());
            if (mixed != null) {
                chunk.setBlockState(pos, mixed, 3);
                return;
            }
        }
        chunk.setBlockState(pos, state, 3);
        if (state.getValue(EndBlockProperties.ACTIVE)) {
            makeShards(chunk, pos, random);
        }
    }

    /**
     * Scatters clusters of 26.2's vanilla sulfur spikes across the cave's floor and ceiling.
     * <p>
     * Anchored on a per-chunk grid rather than a plain random walk so a cluster is not cut in half by a
     * chunk border: each candidate is seeded from its own block position, so the same seed always
     * produces the same spikes regardless of which chunk generates first.
     * <p>
     * Spikes are only hung from brimstone or sulphuric rock, and never in water - the flooded lower half
     * of the cave keeps its vents and tube worms instead.
     */
    private void placeSulfurSpikeClusters(
            ChunkAccess chunk,
            RandomSource random,
            int x0, int x1, int z0, int z1, int y1, int y2
    ) {
        final MutableBlockPos mut = new MutableBlockPos();
        final int attempts = (x1 - x0 + 1) * (z1 - z0 + 1) / 24;
        for (int i = 0; i < attempts; i++) {
            final int x = MHelper.randRange(x0, x1, random);
            final int z = MHelper.randRange(z0, z1, random);
            final boolean fromCeiling = random.nextBoolean();

            // Scan the whole column for an open/solid boundary rather than probing outward from a random
            // height: a random y almost always starts inside rock, where the old walk either anchored
            // with no room to grow or gave up immediately, which is why no spikes ever appeared.
            // A floor anchor is air with anchor rock below; a ceiling anchor is air with rock above.
            int surfaceY = Integer.MIN_VALUE;
            int found = 0;
            for (int y = y1 + 1; y <= y2 - 1; y++) {
                mut.set(x, y, z);
                if (!chunk.getBlockState(mut).isAir()) continue; // water counts as blocked - no spikes underwater
                mut.set(x, fromCeiling ? y + 1 : y - 1, z);
                if (!isSpikeAnchor(chunk.getBlockState(mut))) continue;
                // Reservoir-sample so every boundary in the column is equally likely, without
                // collecting them into a list first.
                found++;
                if (random.nextInt(found) == 0) {
                    surfaceY = y;
                }
            }
            if (surfaceY == Integer.MIN_VALUE) continue;

            final int cluster = 1 + random.nextInt(4);
            for (int c = 0; c < cluster; c++) {
                final int sx = x + MHelper.randRange(-2, 2, random);
                final int sz = z + MHelper.randRange(-2, 2, random);
                if (sx < x0 || sx > x1 || sz < z0 || sz > z1) continue;
                placeSpike(chunk, random, mut, sx, surfaceY, sz, fromCeiling, y1, y2);
            }
        }
    }

    /** Spikes grow out of the cave's own stone, not out of ore or decoration. */
    private static boolean isSpikeAnchor(BlockState state) {
        return state.is(EndStoneBlocks.BRIMSTONE)
                || state.is(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE))
                || state.is(Blocks.SULFUR)
                || state.is(Blocks.CINNABAR);
    }

    /**
     * Grows one spike of 1-3 segments away from the anchor surface. Mirrors vanilla's speleothem shape:
     * a base at the rock, optional middle segments, and a tip at the end.
     */
    private static void placeSpike(
            ChunkAccess chunk,
            RandomSource random,
            MutableBlockPos mut,
            int x, int openY, int z,
            boolean fromCeiling,
            int y1, int y2
    ) {
        final Direction tipDirection = fromCeiling ? Direction.DOWN : Direction.UP;
        final int grow = fromCeiling ? -1 : 1;

        // openY is the first open block off the surface, so the rock it hangs from is one step back.
        // Re-check it here because the cluster jitter may have moved this spike off the anchor the
        // column scan found.
        mut.set(x, openY - grow, z);
        if (!isSpikeAnchor(chunk.getBlockState(mut))) return;

        final int length = 1 + random.nextInt(3);
        for (int n = 0; n < length; n++) {
            final int y = openY + grow * n;
            if (y < y1 || y > y2) return;
            mut.set(x, y, z);
            final BlockState existing = chunk.getBlockState(mut);
            if (!existing.isAir()) return; // stop at water, rock, or an earlier spike

            final SpeleothemThickness thickness;
            if (n == length - 1) {
                thickness = SpeleothemThickness.TIP; // a single-block spike is just a tip
            } else if (n == 0) {
                thickness = SpeleothemThickness.BASE;
            } else {
                thickness = SpeleothemThickness.MIDDLE;
            }
            chunk.setBlockState(
                    mut,
                    Blocks.SULFUR_SPIKE.defaultBlockState()
                          .setValue(SpeleothemBlock.TIP_DIRECTION, tipDirection)
                          .setValue(SpeleothemBlock.THICKNESS, thickness),
                    3
            );
        }
    }

    /**
     * Chunk-local counterpart of {@link SulphurFloorMix#isBuried}. Only walks straight up, so it never
     * leaves this chunk column and needs no bounds check beyond the chunk's own height limits.
     */
    private static boolean isBuriedInChunk(ChunkAccess chunk, BlockPos pos) {
        MutableBlockPos above = new MutableBlockPos();
        for (int i = 1; i <= SulphurFloorMix.MIN_DEPTH; i++) {
            int y = pos.getY() + i;
            if (y > chunk.getMaxY()) {
                return false;
            }
            above.set(pos.getX(), y, pos.getZ());
            if (chunk.getBlockState(above).isAir()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Chunk-local counterpart of {@link SulphurFloorMix#facesOpenSpace}. Air only, matching it. A horizontal step can leave this
     * chunk, where {@code chunk.getBlockState} would report air for terrain that simply has not been
     * built yet - treat leaving the chunk as "exposed" so the deposit never bleeds onto a cave wall
     * because of a neighbour that was not loaded.
     */
    private static boolean facesOpenSpaceInChunk(ChunkAccess chunk, BlockPos pos) {
        MutableBlockPos side = new MutableBlockPos();
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            int x = pos.getX() + dir.getStepX();
            int y = pos.getY() + dir.getStepY();
            int z = pos.getZ() + dir.getStepZ();
            if (x < minX || x > minX + 15 || z < minZ || z > minZ + 15
                    || y < chunk.getMinY() || y > chunk.getMaxY()) {
                return true;
            }
            side.set(x, y, z);
            if (chunk.getBlockState(side).isAir()) {
                return true;
            }
        }
        return false;
    }

    private BlockState getBrimstone(ChunkAccess chunk, BlockPos pos) {
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            if (chunk.getBlockState(pos.relative(dir)).is(Blocks.WATER)) {
                return EndStoneBlocks.BRIMSTONE.defaultBlockState().setValue(EndBlockProperties.ACTIVE, true);
            }
        }
        return EndStoneBlocks.BRIMSTONE.defaultBlockState();
    }

    private void makeShards(ChunkAccess chunk, BlockPos pos, RandomSource random) {
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            BlockPos side;
            if (random.nextInt(16) == 0 && chunk.getBlockState((side = pos.relative(dir))).is(Blocks.WATER)) {
                BlockState state = EndStoneBlocks.SULPHUR_CRYSTAL.defaultBlockState()
                                                            .setValue(SulphurCrystalBlock.WATERLOGGED, true)
                                                            .setValue(SulphurCrystalBlock.FACING, dir)
                                                            .setValue(SulphurCrystalBlock.AGE, random.nextInt(3));
                chunk.setBlockState(side, state, 3);
            }
        }
    }
}
