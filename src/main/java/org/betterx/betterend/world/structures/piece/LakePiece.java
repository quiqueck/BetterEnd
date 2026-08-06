package org.betterx.betterend.world.structures.piece;


import org.betterx.betterend.registry.block.EndPlantBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.world.biome.EndBiome;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;

import com.google.common.collect.Maps;

import java.util.Map;

public class LakePiece extends BasePiece {
    private static final BlockState ENDSTONE = Blocks.END_STONE.defaultBlockState();
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();

    // Grounding parameters (see groundColumn): the shore is a band SHORE_WIDTH blocks wide that slopes
    // from the water surface down to the surrounding terrain. Its top height at distance dOut from the
    // water edge is a blend water_weight*waterLevel + (1-water_weight)*groundLevel, where water_weight
    // runs from 1.0 at the waterline (the shore meets the water flush - no waterfall) down to
    // OUTER_WATER_WEIGHT at the outer edge, so the outermost ring is almost all ground and blends in.
    private static final int SHORE_WIDTH = 16;
    // Water-weight bounds at the outermost shore ring (mostly ground). The inner ring is fixed at 1.0
    // (both bounds), so the min bound interpolates 1.0 -> 0.1 and the max bound 1.0 -> 0.3 across the
    // band, independently; a noise field then picks the actual weight within [min,max] per column, so
    // the outer edge sits at 0.1..0.3 * waterLevel + 0.9..0.7 * groundLevel and isn't a perfect circle.
    private static final double OUTER_WATER_WEIGHT_MIN = 0.1;
    private static final double OUTER_WATER_WEIGHT_MAX = 0.3;
    // How far below a fill's starting point the real terrain is searched for.
    private static final int GROUND_SEARCH_DEPTH = 32;
    // Air gaps up to this deep are closed completely (a lake hovering slightly above its terrain).
    // Deeper cavities are NEVER filled through: caves (which generate in RAW_GENERATION, before the
    // LAKES step) stay intact and the void stays open - the lake only seals itself with a shell.
    private static final int BRIDGE_DEPTH = 8;
    // Thickness of that sealing shell. Hanging below the lowest water block of each column, it
    // follows the ellipsoid bowl (and the terraced skirt) instead of ending in a flat plane.
    private static final int SHELL_DEPTH = 4;

    // Plants scattered on the lake shore. The attached blocks (fur/wings) survive on any sturdy top,
    // the bush plants (jungle grass, umbrella moss) grow on CommonBlockTags.SOIL - trying them in a
    // random order and placing the first that can survive keeps every shore block covered.
    private static BlockState[] rimPlants;

    private static BlockState[] rimPlants() {
        if (rimPlants == null) {
            rimPlants = new BlockState[]{
                    EndPlantBlocks.JUNGLE_GRASS.defaultBlockState(),
                    EndPlantBlocks.UMBRELLA_MOSS.defaultBlockState(),
                    EndVineBlocks.BLUE_VINE_FUR.defaultBlockState(),
                    EndVineBlocks.FILALUX_WINGS.defaultBlockState(),
            };
        }
        return rimPlants;
    }
    private final Map<Integer, Byte> heightmap = Maps.newHashMap();
    private OpenSimplexNoise noise;
    private BlockPos center;
    private float radius;
    private float aspect;
    private float depth;
    private int seed;
    // The y of the water surface. Usually center.getY(), but lowered by LakeWaterLevels when this
    // lake overlaps another megalake, so all lakes of an overlapping cluster share one surface and
    // merge instead of layering. The bowl SHAPE stays centred on center - only the fill level moves,
    // which at worst leaves the upper lake as a partly dry bowl draining into the lower one.
    private int waterLevel;

    private ResourceKey<Biome> biomeID;
    private BlockState lastSurfaceMaterial;

    public LakePiece(
            BlockPos center,
            float radius,
            float depth,
            RandomSource random,
            Holder<Biome> biome,
            int waterLevel
    ) {
        super(EndStructures.LAKE_PIECE, random.nextInt(), null);
        this.center = center;
        this.radius = radius;
        this.depth = depth;
        this.seed = random.nextInt();
        this.noise = new OpenSimplexNoise(this.seed);
        this.aspect = radius / depth;
        this.biomeID = biome.unwrapKey().orElse(null);
        this.waterLevel = waterLevel;
        makeBoundingBox();
    }

    public LakePiece(StructurePieceSerializationContext type, CompoundTag tag) {
        super(EndStructures.LAKE_PIECE, tag);
        makeBoundingBox();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.store("center", BlockPos.CODEC, center);
        tag.putFloat("radius", radius);
        tag.putFloat("depth", depth);
        tag.putInt("seed", seed);
        tag.putInt("water_level", waterLevel);
        tag.putString("biome", biomeID.identifier().toString());
    }

    @Override
    protected void fromNbt(CompoundTag tag) {
        center = tag.read("center", BlockPos.CODEC).orElse(BlockPos.ZERO);
        radius = tag.getFloatOr("radius", 0);
        depth = tag.getFloatOr("depth", 0);
        seed = tag.getIntOr("seed", 0);
        waterLevel = tag.getIntOr("water_level", center.getY());
        noise = new OpenSimplexNoise(seed);
        aspect = radius / depth;
        biomeID = ResourceKey.create(Registries.BIOME, Identifier.parse(tag.getStringOr("biome", "")));
    }

    /**
     * Returns the terrain material to use for the lake rim/floor. Prefers whatever real terrain
     * block was last sampled while carving the lake (see {@link #postProcess}), since that reflects
     * whatever the current SurfaceRules actually placed there. Only falls back to a biome lookup
     * ({@link EndBiome#findTopMaterial(Holder)}) before the very first block has been sampled.
     */
    private BlockState surfaceMaterial(WorldGenLevel world) {
        if (lastSurfaceMaterial != null) {
            return lastSurfaceMaterial;
        }
        if (biomeID != null) {
            Holder<Biome> biome = world.registryAccess()
                                        .lookupOrThrow(Registries.BIOME)
                                        .get(biomeID)
                                        .orElse(null);
            if (biome != null) {
                return EndBiome.findTopMaterial(biome);
            }
        }
        return ENDSTONE;
    }

    /**
     * Chooses a rim/floor block - biome surface material or bare end stone - from this lake's own
     * {@link #noise} field (a separate channel from the shape) rather than per-block random, so the two
     * form coherent patches along the bank instead of salt-and-pepper speckle. World coordinates keep the
     * patches consistent across chunk seams; a higher {@code endstoneBias} yields more end stone (the raw
     * noise sits in roughly {@code [-1,1]}).
     */
    private BlockState shorePatch(WorldGenLevel world, int worldX, int worldZ, double endstoneBias) {
        return noise.eval(worldX * 0.1, worldZ * 0.1, 200) < endstoneBias
                ? ENDSTONE
                : surfaceMaterial(world);
    }

    @Override
    public void postProcess(
            WorldGenLevel world,
            StructureManager arg,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox blockBox,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        int minY = carveMinY();
        int maxY = this.boundingBox.maxY();
        int sx = SectionPos.sectionToBlockCoord(chunkPos.x());
        int sz = SectionPos.sectionToBlockCoord(chunkPos.z());
        MutableBlockPos mut = new MutableBlockPos();
        ChunkAccess chunk = world.getChunk(chunkPos.x(), chunkPos.z());
        for (int x = 0; x < 16; x++) {
            mut.setX(x);
            int wx = x | sx;
            double nx = wx * 0.1;
            int x2 = wx - center.getX();
            for (int z = 0; z < 16; z++) {
                mut.setZ(z);
                int wz = z | sz;
                double nz = wz * 0.1;
                int z2 = wz - center.getZ();
                float clamp = getHeightClamp(world, 8, wx, wz);
                if (clamp < 0.01) continue;

                double n = noise.eval(nx, nz) * 1.5 + 1.5;
                double x3 = MHelper.sqr(x2 + noise.eval(nx, nz, 100) * 10);
                double z3 = MHelper.sqr(z2 + noise.eval(nx, nz, -100) * 10);

                for (int y = maxY; y >= minY; y--) {
                    mut.setY((int) (y + n));
                    double y2 = MHelper.sqr((y - center.getY()) * aspect);
                    double r2 = radius * clamp;
                    double r3 = r2 + 8;
                    r2 *= r2;
                    r3 = r3 * r3 + 100;
                    double dist = x3 + y2 + z3;
                    if (dist < r2) {
                        BlockState state = chunk.getBlockState(mut);
                        // Remember the real biome surface block before carving it away, so the rim
                        // fill below reuses it. This includes END_STONES-tagged biome surfaces such
                        // as sulphuric rock, flavolite and the mosses (which noise-driven surface
                        // rules place instead of the biome's single getTopMaterial); plain end stone and
                        // end-stone dust are excluded, since both are this carve's own "nothing special"
                        // filler (endstone_dust is in the TERRAIN tag via wover:surfaces/end/stones, so
                        // sampling it back would poison the rim material to dust).
                        if (state.is(CommonBlockTags.TERRAIN)
                                && !state.is(Blocks.END_STONE)
                                && !state.is(EndTerrainBlocks.ENDSTONE_DUST)) {
                            lastSurfaceMaterial = state;
                        }
                        if (state.is(CommonBlockTags.END_STONES) || state.isAir()) {
                            state = mut.getY() < waterLevel ? WATER : CAVE_AIR;
                            chunk.setBlockState(mut, state, 3);
                        }
                    } else if (dist <= r3 && mut.getY() < center.getY()) {
                        BlockState state = chunk.getBlockState(mut);
                        BlockPos worldPos = mut.offset(sx, 0, sz);
                        if (!state.isCollisionShapeFullBlock(world, worldPos) && !state.isRedstoneConductor(
                                world,
                                worldPos
                        )) {
                            BlockState above3 = chunk.getBlockState(mut.above(3));
                            final BlockState stateAbove = chunk.getBlockState(mut.above());
                            if (stateAbove.isAir() && above3.isAir()) {
                                // ~10% end stone (bias -0.55), patched via noise instead of random.
                                state = shorePatch(world, worldPos.getX(), worldPos.getZ(), -0.55);
                            } else if (stateAbove.isAir()) {
                                // ~50% end stone (bias 0.0).
                                state = shorePatch(world, worldPos.getX(), worldPos.getZ(), 0.0);
                            } else {
                                state = above3.getFluidState().isEmpty()
                                        ? ENDSTONE
                                        : EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState();
                            }

                            // Taper the rim replacement toward the outer edge of the band so it
                            // blends into the untouched terrain instead of forming a hard ring.
                            double edgeT = (dist - r2) / (r3 - r2);
                            double placeChance = edgeT > 0.85 ? 0.2 : edgeT > 0.6 ? 0.5 : 1.0;
                            if (placeChance >= 1.0 || random.nextDouble() < placeChance) {
                                chunk.setBlockState(mut, state, 3);
                                // Every placed rim block is filled down to solid ground - the outer
                                // taper otherwise leaves single blocks floating over air.
                                fillDownToGround(chunk, mut, mut.getY() - 1);
                                // Vegetate the shore: dense right next to the water, thinning to
                                // ~25% at the outer edge of the band.
                                if (stateAbove.isAir() && random.nextDouble() < 1.0 - 0.75 * edgeT) {
                                    placeRimPlant(world, chunk, mut, worldPos, random);
                                }
                            }
                        }
                    }
                }

                // ---- Grounding (see groundColumn) -------------------------------------------
                // edgeSq > 0 exactly when this lake has surface-level water somewhere at this
                // column's distorted radius; the same clamp/noise values as the carve keep the
                // estimate consistent with what was actually placed above.
                double rc = radius * clamp;
                double ys = (waterLevel - 1 - n - center.getY()) * aspect;
                double edgeSq = rc * rc - ys * ys;
                if (edgeSq > 0) {
                    double dOut = Math.sqrt(x3 + z3) - Math.sqrt(edgeSq);
                    if (dOut <= SHORE_WIDTH + 1) {
                        groundColumn(world, chunk, mut, wx, wz, dOut);
                    }
                }
            }
        }
        fixWater(world, chunk, mut, random, sx, sz);
    }

    /** Marker for "no solid ground found within {@link #GROUND_SEARCH_DEPTH}". */
    private static final int NO_GROUND = Integer.MIN_VALUE;
    /** Marker for "fluid found before solid ground" (a lower lake this column reaches over). */
    private static final int FLUID_BELOW = Integer.MIN_VALUE + 1;

    /**
     * Grounds one column of the lake so the water body never hovers above lower terrain like a
     * stone bowl (previously, water carved into open air was just given a one-block end-stone
     * shell by {@link #fixWater}).
     * <p>
     * Columns under the water close the gap between the lowest water block and the real terrain -
     * but only if that gap is at most {@link #BRIDGE_DEPTH} deep. Deeper cavities are never filled
     * through: caves (generated in RAW_GENERATION, before the LAKES step) keep their volume and
     * the void stays open; the lake merely seals itself with a {@link #SHELL_DEPTH} thick shell
     * whose underside follows the bowl shape of the water body instead of ending in a flat plane.
     * <p>
     * Columns beside the water become the bank: over a {@link #SHORE_WIDTH}-wide band the shore top is
     * a blend of the water surface and the real ground - flush with the water at the edge (no waterfall)
     * and sloping down to meet the surrounding terrain at the outer ring (see the interpolation in this
     * method). Columns whose terrain already reaches that height (the normal sunken-lake case) are left
     * untouched.
     */
    private void groundColumn(WorldGenLevel world, ChunkAccess chunk, MutableBlockPos mut, int wx, int wz, double dOut) {
        final int surfaceY = waterLevel - 1;
        mut.setY(surfaceY);
        if (!chunk.getBlockState(mut).getFluidState().isEmpty()) {
            // Under the water: walk down to the lowest water block, then close the gap below it.
            int bottom = surfaceY;
            while (bottom - 1 > chunk.getMinY()) {
                mut.setY(bottom - 1);
                if (chunk.getBlockState(mut).getFluidState().isEmpty()) break;
                bottom--;
            }
            fillBelow(chunk, mut, bottom - 1);
            return;
        }

        // The bank: how far down is the real surrounding terrain?
        final int groundTop = findGroundBelow(chunk, mut, surfaceY);
        if (groundTop == FLUID_BELOW) return; // a lower lake this reaches over - its own bank handles it
        if (groundTop >= surfaceY) return;    // terrain already at/above the water - nothing to slope down
        if (groundTop == NO_GROUND) {
            // No terrain within reach (deep cliff/void): place nothing. The placement-time corner gate
            // rejects lakes hanging over drops, so this is rare; where it still happens a floating
            // shore block would look worse than simply ending the shore.
            return;
        }

        // Ring interpolation: the shore top blends from the water surface at the waterline (dOut 0,
        // weight fixed 1.0) to almost the real ground at the outer edge, so the bank slopes down to meet
        // the surrounding terrain instead of standing as a wall or floating over lower ground. The min
        // and max water-weight bounds interpolate independently (1.0 -> 0.1 and 1.0 -> 0.3) and a noise
        // field picks the weight within them. Only fills where the terrain is below the target; existing
        // higher terrain is left untouched.
        final double t = Math.min(1.0, dOut / SHORE_WIDTH);
        final double minW = 1.0 - t * (1.0 - OUTER_WATER_WEIGHT_MIN);
        final double maxW = 1.0 - t * (1.0 - OUTER_WATER_WEIGHT_MAX);
        final double noise01 = noise.eval(wx * 0.1, wz * 0.1, 300) * 0.5 + 0.5;
        final double waterWeight = minW + noise01 * (maxW - minW);
        final int target = (int) Math.round(waterWeight * surfaceY + (1.0 - waterWeight) * groundTop);
        if (target <= groundTop) return; // outer ring already meets the ground

        mut.setY(target);
        final BlockState state = chunk.getBlockState(mut);
        if (!state.getFluidState().isEmpty()) return;
        if (!state.isAir() && !BlocksHelper.replaceableOrPlant(state)) return; // terrain reaches the target
        chunk.setBlockState(mut, shorePatch(world, wx, wz, -0.55), 3);
        // Fill the shore solidly down to the real ground with end stone so it never floats.
        fillDownToGround(chunk, mut, target - 1);
    }

    /** Maximum air gap the shore/rim downfill will close; deeper gaps are left untouched. */
    private static final int MAX_AIR_FILL = 4;

    /**
     * Fills end stone downward from {@code from} through air/replaceable blocks - but only when solid
     * ground lies within {@link #MAX_AIR_FILL} blocks (deeper gaps are left alone; the placement-time
     * corner gate keeps lakes off steep drops, so long support pillars are neither needed nor wanted).
     * The terrain block the fill lands on is replaced with end stone too, so the column never sits on
     * grass/moss. Restores {@code mut}'s Y before returning.
     */
    private void fillDownToGround(ChunkAccess chunk, MutableBlockPos mut, int from) {
        final int origY = mut.getY();
        // Measure the gap first: find solid ground within MAX_AIR_FILL blocks.
        int groundY = Integer.MIN_VALUE;
        final int floor = Math.max(from - MAX_AIR_FILL, chunk.getMinY());
        for (int y = from; y >= floor; y--) {
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            if (!state.getFluidState().isEmpty()) {
                mut.setY(origY);
                return; // never fill toward water
            }
            if (!state.isAir() && !BlocksHelper.replaceableOrPlant(state)) {
                groundY = y;
                break;
            }
        }
        if (groundY == Integer.MIN_VALUE) {
            mut.setY(origY);
            return; // more than MAX_AIR_FILL blocks of air below - do not fill
        }
        for (int y = from; y >= groundY; y--) {
            mut.setY(y);
            chunk.setBlockState(mut, ENDSTONE, 3);
        }
        mut.setY(origY);
    }

    /**
     * Scans down from {@code from} (inclusive) for at most {@link #GROUND_SEARCH_DEPTH} blocks.
     * Returns the y of the first solid block, {@link #FLUID_BELOW} if a fluid comes first, or
     * {@link #NO_GROUND} if there is neither.
     */
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

    /**
     * Closes the gap below {@code from}: filled completely with end stone when solid ground lies
     * within {@link #BRIDGE_DEPTH} blocks, otherwise (cave, cliff or void below) only a
     * {@link #SHELL_DEPTH} thick shell is placed and the cavity underneath is left untouched.
     */
    private void fillBelow(ChunkAccess chunk, MutableBlockPos mut, int from) {
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
            chunk.setBlockState(mut, ENDSTONE, 3);
        }
    }

    /**
     * Places one shore plant on top of the just-placed rim block at {@code surface}. Tries the
     * {@link #rimPlants()} in a random order and uses the first that can survive on the rim block, so
     * soil-only plants land on mossy shores while the attached fur/wings cover bare end stone/dust.
     *
     * @param surface      the rim block position in chunk-local X/Z (world Y)
     * @param surfaceWorld the same block in world coordinates, for {@code canSurvive} lookups
     */
    private void placeRimPlant(
            WorldGenLevel world,
            ChunkAccess chunk,
            MutableBlockPos surface,
            BlockPos surfaceWorld,
            RandomSource random
    ) {
        final BlockState[] plants = rimPlants();
        final BlockPos plantLocal = surface.above();
        final BlockPos plantWorld = surfaceWorld.above();
        final int start = random.nextInt(plants.length);
        for (int i = 0; i < plants.length; i++) {
            final BlockState plant = plants[(start + i) % plants.length];
            if (plant.canSurvive(world, plantWorld)) {
                chunk.setBlockState(plantLocal, plant, 3);
                return;
            }
        }
    }

    private void fixWater(
            WorldGenLevel world,
            ChunkAccess chunk,
            MutableBlockPos mut,
            RandomSource random,
            int sx,
            int sz
    ) {
        int minY = carveMinY();
        int maxY = this.boundingBox.maxY();
        for (int x = 0; x < 16; x++) {
            mut.setX(x);
            for (int z = 0; z < 16; z++) {
                mut.setZ(z);
                for (int y = minY; y <= maxY; y++) {
                    mut.setY(y);
                    FluidState state = chunk.getFluidState(mut);
                    if (!state.isEmpty()) {
                        mut.setY(y - 1);
                        if (chunk.getBlockState(mut).isAir()) {
                            mut.setY(y + 1);

                            BlockState bState = chunk.getBlockState(mut);
                            if (bState.isAir()) {
                                bState = shorePatch(world, x | sx, z | sz, 0.0);
                            } else {
                                bState = bState.getFluidState().isEmpty()
                                        ? ENDSTONE
                                        : EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState();
                            }

                            mut.setY(y);

                            makeEndstonePillar(chunk, mut, bState);
                        } else if (x > 1 && x < 15 && z > 1 && z < 15) {
                            mut.setY(y);
                            for (Direction dir : BlocksHelper.HORIZONTAL) {
                                BlockPos wPos = mut.offset(dir.getStepX(), 0, dir.getStepZ());
                                if (chunk.getBlockState(wPos).isAir()) {
                                    mut.setY(y + 1);
                                    BlockState bState = chunk.getBlockState(mut);
                                    if (bState.isAir()) {
                                        bState = shorePatch(world, x | sx, z | sz, 0.0);
                                    } else {
                                        bState = bState.getFluidState().isEmpty()
                                                ? ENDSTONE
                                                : EndTerrainBlocks.ENDSTONE_DUST.defaultBlockState();
                                    }
                                    mut.setY(y);
                                    makeEndstonePillar(chunk, mut, bState);
                                    break;
                                }
                            }
                        } else if (chunk.getBlockState(mut.move(Direction.UP)).isAir()) {
                            chunk.markPosForPostProcessing(mut.move(Direction.DOWN).immutable());
                        }
                    } else if (chunk.getBlockState(mut).isRandomlyTicking()) {
                        chunk.markPosForPostProcessing(mut.immutable());
                    }
                }
            }
        }
    }

    private void makeEndstonePillar(ChunkAccess chunk, MutableBlockPos mut, BlockState terrain) {
        chunk.setBlockState(mut, terrain, 3);
        mut.setY(mut.getY() - 1);
        while (!chunk.getFluidState(mut).isEmpty()) {
            chunk.setBlockState(mut, ENDSTONE, 3);
            mut.setY(mut.getY() - 1);
        }
    }

    private int getHeight(WorldGenLevel world, BlockPos pos) {
        int p = ((pos.getX() & 2047) << 11) | (pos.getZ() & 2047);
        int h = heightmap.getOrDefault(p, Byte.MIN_VALUE);
        if (h > Byte.MIN_VALUE) {
            return h;
        }

        if (!world.getBiome(pos).is(biomeID)) {
            heightmap.put(p, (byte) 0);
            return 0;
        }

        h = world.getHeight(Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
        h = Mth.abs(h - center.getY());
        h = h < 8 ? 1 : 0;

        heightmap.put(p, (byte) h);
        return h;
    }

    private float getHeightClamp(WorldGenLevel world, int radius, int posX, int posZ) {
        MutableBlockPos mut = new MutableBlockPos();
        int r2 = radius * radius;
        float height = 0;
        float max = 0;
        for (int x = -radius; x <= radius; x++) {
            mut.setX(posX + x);
            int x2 = x * x;
            for (int z = -radius; z <= radius; z++) {
                mut.setZ(posZ + z);
                int z2 = z * z;
                if (x2 + z2 < r2) {
                    float mult = 1 - (float) Math.sqrt(x2 + z2) / radius;
                    max += mult;
                    height += getHeight(world, mut) * mult;
                }
            }
        }
        height /= max;
        return Mth.clamp(height, 0, 1);
    }

    /**
     * The vertical range of the bowl carve (and of {@link #fixWater}); this was the box's Y range
     * before the grounding pass extended the box further down for its fills.
     */
    private int carveMinY() {
        return MHelper.floor(center.getY() - depth - 8);
    }

    private void makeBoundingBox() {
        // radius + 8 covers the rim band; the grounding shore reaches SHORE_WIDTH farther out, plus
        // ~10 blocks of horizontal noise distortion of the shape.
        final int margin = 8 + SHORE_WIDTH + 10;
        int minX = MHelper.floor(center.getX() - radius - margin);
        // Deepest possible grounding write: a full bridge fill just below the carve.
        int minY = carveMinY() - BRIDGE_DEPTH - SHELL_DEPTH;
        int minZ = MHelper.floor(center.getZ() - radius - margin);
        int maxX = MHelper.floor(center.getX() + radius + margin);
        int maxY = MHelper.floor(center.getY() + depth);
        int maxZ = MHelper.floor(center.getZ() + radius + margin);
        this.boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
}