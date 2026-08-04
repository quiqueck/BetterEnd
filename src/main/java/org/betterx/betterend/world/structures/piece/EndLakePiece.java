package org.betterx.betterend.world.structures.piece;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.util.BlockFixer;
import org.betterx.betterend.world.biome.EndBiome;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;

/**
 * Per-chunk structure piece that carves an End lake.
 * <p>
 * This is a port of the old {@code EndLakeFeature}'s carve: the same radius (10-20),
 * depth ({@code radius * 0.5 * [0.8..1.2]}) and the same static {@link OpenSimplexNoise} ellipsoid
 * shape (seed {@code 15152}). Shore blocks (the blocks bordering the water horizontally) deviate
 * from the old feature: they are the biome surface material with only a tiny random dust sprinkle
 * ({@link #SHORE_DUST_CHANCE}) instead of the old dust-heavy mix. The only structural
 * difference is that a feature wrote across chunk boundaries via {@code world.setBlock} (which is
 * what stranded trees over lakes: a neighbour chunk decorated its trees before the lake flooded
 * the ground from under them), whereas this piece runs during the chunk's own {@code LAKES}
 * structure step and only writes into the chunk currently being generated. The full lake still
 * forms across chunks because the piece {@link #boundingBox} spans them all and each chunk carves
 * its own slice during its own generation.
 */
public class EndLakePiece extends BasePiece {
    private static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();
    // The lake shape is deterministic and world-wide identical, exactly like the old feature which
    // used a single static noise instance. Keeping it static means every chunk that carves a slice
    // of the same lake agrees on the shape without serializing the noise field.
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(15152);
    // Chance for a single shore block (bordering the water horizontally) to be end-stone dust instead
    // of the biome surface material. Dust shores looked wrong in most biomes, so the surface block
    // always wins apart from this tiny sprinkle.
    private static final float SHORE_DUST_CHANCE = 0.05f;

    private BlockPos center;
    private int waterLevel;
    private float radius;
    private float depth;
    private ResourceKey<Biome> biomeID;
    private BlockState cachedBorderMaterial;
    private BlockState sampledSurfaceMaterial;

    public EndLakePiece(
            BlockPos center,
            int waterLevel,
            float radius,
            float depth,
            RandomSource random,
            Holder<Biome> biome
    ) {
        super(EndStructures.END_LAKE_PIECE, random.nextInt(), null);
        this.center = center;
        this.waterLevel = waterLevel;
        this.radius = radius;
        this.depth = depth;
        this.biomeID = biome.unwrapKey().orElse(null);
        makeBoundingBox();
    }

    public EndLakePiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.END_LAKE_PIECE, tag);
        makeBoundingBox();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.store("center", BlockPos.CODEC, center);
        tag.putInt("water_level", waterLevel);
        tag.putFloat("radius", radius);
        tag.putFloat("depth", depth);
        tag.putString("biome", biomeID == null ? "" : biomeID.location().toString());
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        center = tag.read("center", BlockPos.CODEC).orElse(BlockPos.ZERO);
        waterLevel = tag.getIntOr("water_level", center.getY());
        radius = tag.getFloatOr("radius", 0);
        depth = tag.getFloatOr("depth", 0);
        biomeID = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(tag.getStringOr("biome", "")));
    }

    private void makeBoundingBox() {
        int dist2 = MHelper.floor(radius * 1.5);
        int bott = MHelper.floor(depth);
        // + SHORE_WIDTH: the sloped shore band extends beyond the carved footprint; - extra depth so
        // the shore downfill stays inside the box.
        int minX = center.getX() - dist2 - 2 - SHORE_WIDTH;
        int maxX = center.getX() + dist2 + 2 + SHORE_WIDTH;
        int minZ = center.getZ() - dist2 - 2 - SHORE_WIDTH;
        int maxZ = center.getZ() + dist2 + 2 + SHORE_WIDTH;
        int minY = waterLevel - bott - 4 - MAX_AIR_FILL - SHELL_DEPTH;
        int maxY = center.getY() + 22;
        this.boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * The shore/border material. Prefers a real surface block sampled from the terrain while carving
     * (see {@link #sampledSurfaceMaterial}), exactly like {@code LakePiece#surfaceMaterial}, so
     * noise-driven surfaces (mosses, sulphuric rock, flavolite) are reflected instead of just the
     * biome's single {@code getTopMaterial()}. Falls back to the stored biome via the crash-safe
     * {@link EndBiome#findTopMaterial(Holder)} (a {@link Holder} lookup, never a position/biome lookup
     * during gen), and finally to plain end stone. The old feature sampled this per-lake with
     * {@code EndBiome.sampleTopMaterial}; the biome-level resolve keeps the material consistent across
     * the chunks that make up one lake and cannot trigger the "Requested chunk unavailable" crash.
     */
    private BlockState borderMaterial(WorldGenLevel world) {
        if (sampledSurfaceMaterial != null) {
            return sampledSurfaceMaterial;
        }
        if (cachedBorderMaterial != null) {
            return cachedBorderMaterial;
        }
        BlockState result = END_STONE;
        if (biomeID != null) {
            Holder<Biome> biome = world.registryAccess()
                                       .lookupOrThrow(Registries.BIOME)
                                       .get(biomeID)
                                       .orElse(null);
            if (biome != null) {
                result = EndBiome.findTopMaterial(biome);
            }
        }
        cachedBorderMaterial = result;
        return result;
    }

    /**
     * Remembers the real biome surface block before the carve replaces it, so the shore fill reuses
     * whatever the surface rules actually placed (nylium/moss) rather than only the biome's single
     * top material. Mirrors {@code LakePiece}: any {@link CommonBlockTags#TERRAIN} block that is not
     * plain end stone counts as a genuine surface; plain end stone is the "nothing special" filler and
     * is ignored so it never becomes the shore material.
     */
    private void sampleSurface(BlockState state) {
        // Only capture the genuine biome surface (nylium/moss/rutiscus). END_STONE and ENDSTONE_DUST are
        // both in CommonBlockTags.TERRAIN (endstone_dust via wover:surfaces/end/stones), and ENDSTONE_DUST
        // is exactly what this carve lays as shore filler - sampling it back would poison borderMaterial()
        // to dust and turn the whole shore to dust. Exclude both so the real surface always wins.
        if (state.is(CommonBlockTags.TERRAIN)
                && !state.is(Blocks.END_STONE)
                && !state.is(EndTerrainBlocks.ENDSTONE_DUST)) {
            sampledSurfaceMaterial = state;
        }
    }

    /**
     * Picks a shore block: the biome surface material, with a {@link #SHORE_DUST_CHANCE} chance of
     * end-stone dust as a small accent.
     */
    private BlockState shoreMaterial(RandomSource random, BlockState border) {
        return random.nextFloat() < SHORE_DUST_CHANCE
                ? EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState()
                : border;
    }

    private boolean canReplace(BlockState state) {
        return state.is(CommonBlockTags.END_STONES)
                || state.is(EndTerrainBlocks.ENDSTONE_DUST)
                || BlocksHelper.replaceableOrPlant(state)
                || state.is(CommonBlockTags.WATER_PLANT);
    }

    // ---- Shore grounding (ported from LakePiece's megalake shore model) ------------------------
    // The shore is a band SHORE_WIDTH blocks wide sloping from the water surface down to the real
    // terrain: its top height is a blend w*waterLevel + (1-w)*groundLevel where the min/max bounds
    // of w interpolate independently from 1.0-1.0 at the waterline to 0.1-0.3 at the outer rim, and
    // a noise field picks w within them per column.
    private static final int SHORE_WIDTH = 8;
    private static final double OUTER_WATER_WEIGHT_MIN = 0.1;
    private static final double OUTER_WATER_WEIGHT_MAX = 0.3;
    /** Maximum air gap the shore downfill closes; deeper gaps are left untouched. */
    private static final int MAX_AIR_FILL = 4;
    /** How far below a fill's starting point the real terrain is searched for. */
    private static final int GROUND_SEARCH_DEPTH = 24;
    /** Air gaps under the water body up to this deep are closed completely. */
    private static final int BRIDGE_DEPTH = 8;
    /** Thickness of the sealing shell under floating water (follows the bowl shape). */
    private static final int SHELL_DEPTH = 4;
    private static final int NO_GROUND = Integer.MIN_VALUE;
    private static final int FLUID_BELOW = Integer.MIN_VALUE + 1;

    /**
     * Grounds one shore column (see the constant block above): under water it closes small gaps or
     * seals with a shell; beside the water it places the sloped shore block and fills it down to
     * terrain (at most {@link #MAX_AIR_FILL} air blocks, replacing the terrain block it lands on
     * with end stone).
     */
    private void groundColumn(
            WorldGenLevel world,
            ChunkAccess chunk,
            MutableBlockPos mut,
            RandomSource random,
            int wx,
            int wz,
            double dOut
    ) {
        final int surfaceY = waterLevel - 1;
        mut.set(wx, surfaceY, wz);
        if (!chunk.getBlockState(mut).getFluidState().isEmpty()) {
            int bottom = surfaceY;
            while (bottom - 1 > chunk.getMinY()) {
                mut.setY(bottom - 1);
                if (chunk.getBlockState(mut).getFluidState().isEmpty()) break;
                bottom--;
            }
            fillBelowWater(chunk, mut, bottom - 1);
            return;
        }

        final int groundTop = findGroundBelow(chunk, mut, surfaceY);
        if (groundTop == FLUID_BELOW || groundTop == NO_GROUND) return;
        if (groundTop >= surfaceY) return;

        final double t = Math.min(1.0, dOut / SHORE_WIDTH);
        final double minW = 1.0 - t * (1.0 - OUTER_WATER_WEIGHT_MIN);
        final double maxW = 1.0 - t * (1.0 - OUTER_WATER_WEIGHT_MAX);
        final double n01 = NOISE.eval(wx * 0.1, 300, wz * 0.1) * 0.5 + 0.5;
        final double w = minW + n01 * (maxW - minW);
        final int target = (int) Math.round(w * surfaceY + (1.0 - w) * groundTop);
        if (target <= groundTop) return;

        mut.setY(target);
        final BlockState state = chunk.getBlockState(mut);
        if (!state.getFluidState().isEmpty()) return;
        if (!state.isAir() && !BlocksHelper.replaceableOrPlant(state)) return;
        chunk.setBlockState(mut, shoreMaterial(random, borderMaterial(world)), 3);
        fillDownToGround(chunk, mut, target - 1);
    }

    /** First solid y scanning down, or {@link #FLUID_BELOW} / {@link #NO_GROUND}. */
    private int findGroundBelow(ChunkAccess chunk, MutableBlockPos mut, int from) {
        final int floor = Math.max(from - GROUND_SEARCH_DEPTH, chunk.getMinY());
        for (int y = from; y > floor; y--) {
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            if (!state.getFluidState().isEmpty()) return FLUID_BELOW;
            if (!state.isAir() && !BlocksHelper.replaceableOrPlant(state)) return y;
        }
        return NO_GROUND;
    }

    /** Fill down at most {@link #MAX_AIR_FILL} air blocks; the ground block hit becomes end stone. */
    private void fillDownToGround(ChunkAccess chunk, MutableBlockPos mut, int from) {
        final int origY = mut.getY();
        int groundY = Integer.MIN_VALUE;
        final int floor = Math.max(from - MAX_AIR_FILL, chunk.getMinY());
        for (int y = from; y >= floor; y--) {
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            if (!state.getFluidState().isEmpty()) {
                mut.setY(origY);
                return;
            }
            if (!state.isAir() && !BlocksHelper.replaceableOrPlant(state)) {
                groundY = y;
                break;
            }
        }
        if (groundY == Integer.MIN_VALUE) {
            mut.setY(origY);
            return;
        }
        for (int y = from; y >= groundY; y--) {
            mut.setY(y);
            chunk.setBlockState(mut, END_STONE, 3);
        }
        mut.setY(origY);
    }

    /** Under the water: close gaps up to {@link #BRIDGE_DEPTH} fully, else a {@link #SHELL_DEPTH} shell. */
    private void fillBelowWater(ChunkAccess chunk, MutableBlockPos mut, int from) {
        final int groundTop = findGroundBelow(chunk, mut, from);
        final int stopAt;
        if (groundTop != NO_GROUND && groundTop != FLUID_BELOW && from - groundTop <= BRIDGE_DEPTH) {
            stopAt = groundTop;
        } else {
            stopAt = from - SHELL_DEPTH;
        }
        for (int y = from; y > stopAt; y--) {
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            if (!state.getFluidState().isEmpty()) break;
            if (!state.isAir() && !BlocksHelper.replaceableOrPlant(state)) break;
            chunk.setBlockState(mut, END_STONE, 3);
        }
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

        final double radius = this.radius;
        final double depth = this.depth;
        final int cx = center.getX();
        final int cy = center.getY();
        final int cz = center.getZ();
        final int dist = MHelper.floor(radius);
        final int dist2 = MHelper.floor(radius * 1.5);
        final int bott = MHelper.floor(depth);

        // Intersection of the lake footprint (center +/- dist2) with the current chunk. Every write
        // below uses world coordinates that fall inside these bounds, so ChunkAccess masks them back
        // to this chunk and nothing leaks into a neighbour.
        final int x0 = Math.max(cx - dist2, sx);
        final int x1 = Math.min(cx + dist2, sx + 15);
        final int z0 = Math.max(cz - dist2, sz);
        final int z1 = Math.min(cz + dist2, sz + 15);
        if (x0 > x1 || z0 > z1) return;

        final MutableBlockPos POS = new MutableBlockPos();

        // ---- Mask ---------------------------------------------------------------------------------
        // Skip columns that already contain surface water (an existing lake) so we do not carve into
        // it. The old feature scanned this across the whole footprint; here it is chunk-local (we can
        // only read the chunk being generated), which is close enough - existing water rarely sits in
        // the exact spot of a freshly placed lake.
        final int maskMinX = x0 - 1;
        final int maskMinZ = z0 - 1;
        final boolean[][] mask = new boolean[x1 - x0 + 3][z1 - z0 + 3];
        for (int x = x0; x <= x1; x++) {
            POS.setX(x);
            for (int z = z0; z <= z1; z++) {
                POS.setZ(z);
                for (int y = waterLevel + 1; y <= waterLevel + 20; y++) {
                    POS.setY(y);
                    FluidState fluid = chunk.getFluidState(POS);
                    if (!fluid.isEmpty()) {
                        for (int i = -1; i < 2; i++) {
                            int px = x - maskMinX + i;
                            if (px < 0 || px >= mask.length) continue;
                            for (int j = -1; j < 2; j++) {
                                int pz = z - maskMinZ + j;
                                if (pz < 0 || pz >= mask[px].length) continue;
                                mask[px][pz] = true;
                            }
                        }
                        break;
                    }
                }
            }
        }

        BlockState state;

        // ---- Rim above the waterline (feature loop 2) --------------------------------------------
        for (int x = x0; x <= x1; x++) {
            POS.setX(x);
            int x2 = x - cx;
            x2 *= x2;
            int mx = x - maskMinX;
            for (int z = z0; z <= z1; z++) {
                POS.setZ(z);
                int z2 = z - cz;
                z2 *= z2;
                int mz = z - maskMinZ;
                if (mask[mx][mz]) continue;
                double size = 1;
                for (int y = cy; y <= cy + 20; y++) {
                    POS.setY(y);
                    double add = y - cy;
                    if (add > 5) {
                        size *= 0.8;
                        add = 5;
                    }
                    double r = (add * 1.8 + radius * (NOISE.eval(x * 0.2, y * 0.2, z * 0.2) * 0.25 + 0.75)) - 1.0 / size;
                    if (r > 0) {
                        r *= r;
                        if (x2 + z2 <= r) {
                            state = chunk.getBlockState(POS);
                            sampleSurface(state);
                            if (state.is(CommonBlockTags.END_STONES)) {
                                chunk.setBlockState(POS, AIR, 3);
                            }
                            BlockPos below = POS.below();
                            BlockState belowState = chunk.getBlockState(below);
                            sampleSurface(belowState);
                            if (belowState.is(CommonBlockTags.END_STONES)) {
                                chunk.setBlockState(
                                        below,
                                        shoreMaterial(random, borderMaterial(world)),
                                        3
                                );
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        // ---- Lake bowl below the waterline (feature loop 3) --------------------------------------
        double aspect = radius / depth;
        int bx0 = Math.max(cx - dist, sx);
        int bx1 = Math.min(cx + dist, sx + 15);
        int bz0 = Math.max(cz - dist, sz);
        int bz1 = Math.min(cz + dist, sz + 15);
        for (int x = bx0; x <= bx1; x++) {
            POS.setX(x);
            int x2 = x - cx;
            x2 *= x2;
            int mx = x - maskMinX;
            for (int z = bz0; z <= bz1; z++) {
                POS.setZ(z);
                int z2 = z - cz;
                z2 *= z2;
                int mz = z - maskMinZ;
                if (mask[mx][mz]) continue;
                for (int y = cy - bott; y < cy; y++) {
                    POS.setY(y);
                    double y2 = (y - cy) * aspect;
                    y2 *= y2;
                    double r = radius * (NOISE.eval(x * 0.2, y * 0.2, z * 0.2) * 0.25 + 0.75);
                    double rb = r * 1.2;
                    r *= r;
                    rb *= rb;
                    if (y2 + x2 + z2 <= r) {
                        state = chunk.getBlockState(POS);
                        sampleSurface(state);
                        if (canReplace(state)) {
                            state = chunk.getBlockState(POS.above());
                            state = canReplace(state) ? (y < waterLevel ? WATER : AIR) : state;
                            chunk.setBlockState(POS, state, 3);
                        }
                        BlockPos below = POS.below();
                        if (chunk.getBlockState(below).is(CommonBlockTags.END_STONES)) {
                            chunk.setBlockState(below, EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState(), 3);
                        }
                        MutableBlockPos up = POS.above().mutable();
                        while (true) {
                            state = chunk.getBlockState(up);
                            final boolean belowWater = up.getY() < waterLevel;
                            if (belowWater
                                    && state.hasProperty(BlockStateProperties.WATERLOGGED)
                                    && !state.getValue(BlockStateProperties.WATERLOGGED)) {
                                // Submerged waterloggable block (e.g. leaves from a neighbouring chunk's
                                // tree that this lake floods): flood it instead of leaving a dry pocket
                                // (air bubbles). Keep walking up the column past it.
                                chunk.setBlockState(up, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
                            } else if (canReplace(state) && !state.isAir() && state.getFluidState().isEmpty()) {
                                chunk.setBlockState(up, belowWater ? WATER : AIR, 3);
                            } else {
                                break;
                            }
                            up.setY(up.getY() + 1);
                        }
                    }
                    // Border
                    else if (y < waterLevel && y2 + x2 + z2 <= rb) {
                        chunk.setBlockState(POS, shoreMaterial(random, borderMaterial(world)), 3);
                        chunk.setBlockState(POS.below(), END_STONE, 3);
                    }
                }
            }
        }

        // Reinstate the original feature's final BlockFixer pass (dropped in the port), CLAMPED to this
        // chunk's slice of the lake footprint. BlockFixer removes unsupported floaters (bug 2: cyan moss
        // stranded above the water) and schedules fluid ticks that settle water into air pockets around
        // submerged blocks (bug 3b). Most branches write only at each column position and its vertical
        // (above/below) neighbours, so restricting the start/end to [x0,z0]..[x1,z1] (the
        // chunk-intersect-footprint bounds computed above) keeps those writes inside the generating chunk.
        // The one exception is the chorus flood-fill, which spreads horizontally and could escape this
        // chunk at a shore (Chorus Forest uses END_LAKE_RARE). Passing blockBox - the game-sanctioned
        // writable region for this postProcess - as the writeBounds makes BlockFixer clean chorus up to
        // that boundary and never beyond, avoiding the "Requested chunk unavailable" crash. Its horizontal
        // READS (fluid-flow scheduling, one block into a neighbour) are safe because the piece boundingBox
        // keeps those chunks loaded during postProcess.
        // ---- Shore grounding pass (ported from LakePiece) -----------------------------------------
        // Per column: estimate the horizontal distance beyond the lake's water edge at surface level
        // (the bowl condition of loop 3 solved for the surface y) and slope the shore down to the
        // surrounding terrain via the ring-interpolated water weight. Runs over the footprint plus
        // the shore band, clamped to this chunk.
        {
            // `aspect` (radius / depth) is already defined by the bowl loop above.
            final double ySurf = (waterLevel - 1 - cy) * aspect;
            final double ySurfSq = ySurf * ySurf;
            final int gx0 = Math.max(cx - dist2 - SHORE_WIDTH, sx);
            final int gx1 = Math.min(cx + dist2 + SHORE_WIDTH, sx + 15);
            final int gz0 = Math.max(cz - dist2 - SHORE_WIDTH, sz);
            final int gz1 = Math.min(cz + dist2 + SHORE_WIDTH, sz + 15);
            for (int x = gx0; x <= gx1; x++) {
                final int dx2 = (x - cx) * (x - cx);
                for (int z = gz0; z <= gz1; z++) {
                    final int dz2 = (z - cz) * (z - cz);
                    final double rSurf = radius * (NOISE.eval(x * 0.2, (waterLevel - 1) * 0.2, z * 0.2) * 0.25 + 0.75);
                    final double edgeSq = rSurf * rSurf - ySurfSq;
                    if (edgeSq <= 0) continue; // no surface-level water at this radius
                    final double dOut = Math.sqrt(dx2 + dz2) - Math.sqrt(edgeSq);
                    if (dOut > SHORE_WIDTH + 1) continue;
                    groundColumn(world, chunk, POS, random, x, z, dOut);
                }
            }
        }

        final int fixMinY = waterLevel - bott - 2;
        final int fixMaxY = cy + 20;
        BlockFixer.fixBlocks(world, new BlockPos(x0, fixMinY, z0), new BlockPos(x1, fixMaxY, z1), blockBox);
    }
}
