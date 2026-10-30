package org.betterx.betterend.world.structures.piece;


import org.betterx.bclib.blocks.BaseDoublePlantBlock;
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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LakePiece extends BasePiece {
    private static final BlockState ENDSTONE = Blocks.END_STONE.defaultBlockState();
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();

    // Under-water sealing (groundColumn) runs up to this far outside the waterline.
    private static final int SHORE_WIDTH = 16;
    // How far below a fill's starting point the real terrain is searched for.
    private static final int GROUND_SEARCH_DEPTH = 32;
    // Air gaps up to this deep are closed completely (a lake hovering slightly above its terrain).
    // Deeper cavities are NEVER filled through: caves (which generate in RAW_GENERATION, before the
    // LAKES step) stay intact and the void stays open - the lake only seals itself with a shell.
    private static final int BRIDGE_DEPTH = 8;
    // Thickness of that sealing shell. Hanging below the lowest water block of each column, it
    // follows the ellipsoid bowl (and the terraced skirt) instead of ending in a flat plane.
    private static final int SHELL_DEPTH = 4;
    // How far outside the waterline the bank stays at full height before it starts stepping down, so
    // the water is always held by a full-height rim.
    private static final double SKIRT_FLUSH = 3;
    // The bank steps down one block per two blocks of distance until it meets the ground, so it can reach
    // this far out (GROUND_SEARCH_DEPTH blocks of drop); the bounding box covers it.
    private static final int MAX_BANK_DISTANCE = 4 + 2 * 32 + 4;
    // The bank raises the ground by at most this much; where the profile would have to build higher (the
    // lake borders a cliff or a much lower island layer) the raise tapers off again instead of piling a
    // mound onto the lower terrain, so the natural cliff stays the only drop there.
    private static final int BANK_MAX_RAISE = 5;
    // Neighbourhood (columns) over which the raw shore distance is relaxed, see shoreDistances. The height
    // clamp it needs reads biomes up to DIST_RELAX + 9 blocks outside the chunk, inside the 3x3 chunk window.
    private static final int DIST_RELAX = 6;
    // How far (blocks) a dry carve stays away from the water of a higher, overlapping lake.
    private static final int DIKE_KEEP = 3;

    // Plants of the lake shore with their weights. Each column picks one by a hash of its position among
    // those that can survive on its ground (see placeRimPlant); offset (XZ) plants dominate so the shore
    // does not look gridded. The shore ground is mostly end moss, with jungle moss, end mycelium, chorus
    // nylium, shadow grass and bare end stone / end stone dust in places:
    //   jungle grass        any soil (all mosses, mycelium, nylium)       XZ
    //   umbrella moss       end moss, jungle moss, end mycelium           XZ
    //   twisted umbr. moss  end moss, jungle moss, end mycelium           XZ
    //   creeping moss       end moss, end mycelium                        XZ
    //   blooming cooksonia  end moss                                      XZ
    //   salteago            end moss                                      XZ
    //   vaiolush fern       end moss                                      XZ
    //   chorus grass        chorus nylium                                 XZ
    //   shadow plant        shadow grass                                  XZ
    //   filalux wings       moss / soil only (SHORE_SOIL_ONLY)            XZ
    //   blue vine fur       any sturdy top (also bare stone and dust)     none - see RIM_FUR_CHANCE
    //   amber grass         amber moss                                    XZ
    private static BlockState[] rimPlants;
    private static int[] rimWeights;

    private static BlockState[] rimPlants() {
        if (rimPlants == null) {
            rimWeights = new int[]{30, 22, 12, 12, 6, 4, 4, 10, 8, 8, 5, 10};
            rimSoilOnly = new boolean[]{false, false, false, false, false, false, false, false, false, true, false, false};
            rimPlants = new BlockState[]{
                    EndPlantBlocks.JUNGLE_GRASS.defaultBlockState(),
                    EndPlantBlocks.UMBRELLA_MOSS.defaultBlockState(),
                    EndPlantBlocks.TWISTED_UMBRELLA_MOSS.defaultBlockState(),
                    EndPlantBlocks.CREEPING_MOSS.defaultBlockState(),
                    EndPlantBlocks.BLOOMING_COOKSONIA.defaultBlockState(),
                    EndPlantBlocks.SALTEAGO.defaultBlockState(),
                    EndPlantBlocks.VAIOLUSH_FERN.defaultBlockState(),
                    EndPlantBlocks.CHORUS_GRASS.defaultBlockState(),
                    EndPlantBlocks.SHADOW_PLANT.defaultBlockState(),
                    EndVineBlocks.FILALUX_WINGS.defaultBlockState(),
                    EndVineBlocks.BLUE_VINE_FUR.defaultBlockState(),
                    EndPlantBlocks.AMBER_GRASS.defaultBlockState(),
            };
        }
        return rimPlants;
    }

    // Plants that may only grow on moss / soil on the shore, never on end stone dust, sand or bare end stone,
    // although their own survival rule would allow it: filalux wings (the flat red discs) looked out of place
    // on the sandy, dusty stretches of the shore.
    private static boolean[] rimSoilOnly;

    // Blue vine fur has no position offset, so placed on every column it lines up in a visible grid. Where it
    // is picked it is kept only with this chance, so it stays a scattered accent.
    private static final double RIM_FUR_CHANCE = 0.35;

    // Concurrent: a lake spans multiple chunks, and its BasePiece can be postProcess()'d by different
    // worldgen worker threads for adjacent chunks at the same time (parallel worldgen, e.g. c2me).
    private final Map<Integer, Byte> heightmap = new ConcurrentHashMap<>();
    private OpenSimplexNoise noise;
    private SteppedSkirt skirt;
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
     * The lake biome's own top material, the fallback rim/floor material for a column whose surface is
     * bare end stone or end stone dust (see {@link #columnMaterials}).
     */
    private BlockState biomeMaterial(WorldGenLevel world) {
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
    private SteppedSkirt skirt() {
        if (skirt == null) skirt = new SteppedSkirt(noise, SKIRT_FLUSH);
        return skirt;
    }

    private BlockState shorePatch(BlockState material, int worldX, int worldZ, double endstoneBias) {
        return noise.eval(worldX * 0.1, worldZ * 0.1, 200) < endstoneBias
                ? ENDSTONE
                : material;
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
        // Each column's own surface material, read before this piece changes anything (see columnMaterials).
        final BlockState[] mats = columnMaterials(world, chunk, maxY);
        // The shore's distance field for this chunk, see shoreDistances.
        final double[] shoreDist = shoreDistances(world, sx, sz);
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

                double n = noise.eval(nx, nz) * 1.5 + 1.5;
                double x3 = MHelper.sqr(x2 + noise.eval(nx, nz, 100) * 10);
                double z3 = MHelper.sqr(z2 + noise.eval(nx, nz, -100) * 10);

                // ---- Bank (see SteppedSkirt) ----------------------------------------------------
                // Bank columns step down from the water surface to the surrounding terrain (or to
                // the surface of a lower, overlapping lake). The bank height depends only on the
                // column's distance to the waterline (shoreDistance) and on the column itself, so
                // neighbouring columns - also across chunk borders - differ by at most one block,
                // and there is no fixed band width: the bank keeps stepping down until it meets the
                // ground. It is planned before carving, from the column as the terrain left it, and
                // the rim below never builds above it.
                final int surfaceY = waterLevel - 1;
                final double dOut = shoreDist[x * 16 + z];
                int bankTop = surfaceY;
                if (dOut > 0 && dOut < MAX_BANK_DISTANCE) {
                    mut.setY(surfaceY);
                    final SteppedSkirt.Ground ground = SteppedSkirt.probe(chunk, mut, surfaceY, GROUND_SEARCH_DEPTH);
                    final int low = ground.reference();
                    if ((ground.found() || ground.overFluid()) && low < surfaceY) {
                        bankTop = SteppedSkirt.taper(low, skirt().height(wx, wz, dOut, surfaceY), BANK_MAX_RAISE);
                        // Over a lower lake this is a dike that ends at least one block above its surface.
                        if (bankTop > low) {
                            SteppedSkirt.fill(chunk, mut, bankTop, ground, shorePatch(mats[x * 16 + z], wx, wz, -0.55), SHELL_DEPTH);
                        }
                    }
                }
                // Only where this lake's biome reaches (clamp > 0): a lake must not shave down the banks of a
                // neighbouring lake in the next biome over, which has its own, higher water level.
                if (dOut < MAX_BANK_DISTANCE && clamp >= 0.01) {
                    // The same profile upward (inside the waterline: down to the water level): terrain higher than the water is cut back to at most one block
                    // per two blocks of distance above the waterline, so the dry carve of the bowl does not
                    // end in a wall where the lake meets higher ground. Tapered like the bank: a hill that
                    // rises far above the profile keeps its shape.
                    final int envelope = 2 * surfaceY + 1 - skirt().height(wx, wz, dOut, surfaceY);
                    cutBack(chunk, mut, surfaceY + 1, envelope);
                }
                if (clamp < 0.01) continue;

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
                        // Plants are carved with the rock they stand on: left behind they would float
                        // in the bowl or over the water (e.g. the shore plants of an overlapping lake).
                        if (state.is(CommonBlockTags.END_STONES) || state.isAir() || isLoosePlant(state)) {
                            final boolean dry = mut.getY() >= waterLevel;
                            // Above our own water, a block next to water belongs to a higher, overlapping
                            // lake (its bed or its dike): carving it would leave that lake's water
                            // standing as a wall, whichever lake happened to generate first.
                            if (!dry || state.isAir() || !nearFluid(chunk, mut, DIKE_KEEP)) {
                                setChunkBlock(chunk, mut, dry ? CAVE_AIR : WATER);
                            }
                        }
                    } else if (dist <= r3 && mut.getY() < center.getY() && mut.getY() <= bankTop) {
                        BlockState state = chunk.getBlockState(mut);
                        BlockPos worldPos = mut.offset(sx, 0, sz);
                        // Never build the rim into water: that is an overlapping lake's surface, and a
                        // rim block (plus its shore plant) placed there would float on it.
                        if (state.getFluidState().isEmpty()
                                && !state.isCollisionShapeFullBlock(world, worldPos)
                                && !state.isRedstoneConductor(world, worldPos)) {
                            BlockState above3 = chunk.getBlockState(mut.above(3));
                            final BlockState stateAbove = chunk.getBlockState(mut.above());
                            if (stateAbove.isAir() && above3.isAir()) {
                                // ~10% end stone (bias -0.55), patched via noise instead of random.
                                state = shorePatch(mats[x * 16 + z], worldPos.getX(), worldPos.getZ(), -0.55);
                            } else if (stateAbove.isAir()) {
                                // ~50% end stone (bias 0.0).
                                state = shorePatch(mats[x * 16 + z], worldPos.getX(), worldPos.getZ(), 0.0);
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
                                setChunkBlock(chunk, mut, state);
                                // Every placed rim block is filled down to solid ground - the outer
                                // taper otherwise leaves single blocks floating over air.
                                fillDownToGround(chunk, mut, mut.getY() - 1);
                            }
                        }
                    }
                }

                // ---- Waterline ring ------------------------------------------------------------
                // The distance field is computed from the surface row of the carve formula and can be
                // off by a block or two from where the carve really put water. A column the bank left
                // to the water (dOut <= 0) that ended up dry is raised to the bank's full height here,
                // so the waterline never leaves a pit between the bank and the water.
                if (dOut <= SKIRT_FLUSH) {
                    mut.setY(surfaceY);
                    if (chunk.getFluidState(mut).isEmpty()) {
                        final SteppedSkirt.Ground ground = SteppedSkirt.probe(chunk, mut, surfaceY, GROUND_SEARCH_DEPTH);
                        if (ground.found() && !ground.overFluid() && ground.groundY() < surfaceY) {
                            SteppedSkirt.fill(chunk, mut, surfaceY, ground, shorePatch(mats[x * 16 + z], wx, wz, -0.55), SHELL_DEPTH);
                        }
                    }
                }

                // ---- Grounding under the water (see groundColumn) ----------------------------
                if (dOut <= SHORE_WIDTH + 1) {
                    groundColumn(chunk, mut);
                }
            }
        }
        placeShorePlants(world, chunk, mut, sx, sz, shoreDist, mats);
        fixWater(world, chunk, mut, random, sx, sz, mats);
        LakeFloorLife.place(world, chunk, sx, sz, waterLevel - 1, biomeID, seed);
    }

    /**
     * Vegetates the shore once its shape is final: every dry column within {@link #SHORE_PLANTS_TAIL_END} blocks of
     * the water - bank, rim or untouched natural ground alike - gets a shore plant on its top block if
     * {@link #shorePlantHere} says so. Which plant fits the ground is decided by {@link #placeRimPlant}.
     */
    private void placeShorePlants(
            WorldGenLevel world,
            ChunkAccess chunk,
            MutableBlockPos mut,
            int sx,
            int sz,
            double[] shoreDist,
            BlockState[] mats
    ) {
        final int surfaceY = waterLevel - 1;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final double d = shoreDist[x * 16 + z];
                final int wx = sx + x;
                final int wz = sz + z;
                if (d >= SHORE_PLANTS_TAIL_END) continue;
                final boolean patch = d < SHORE_PLANTS_END && shorePatchHere(wx, wz, d);
                if (!patch && !shorePlantHere(wx, wz, d)) continue;
                mut.set(x, surfaceY + 8, z);
                while (mut.getY() > surfaceY - 12 && SteppedSkirt.passable(chunk.getBlockState(mut))
                        && chunk.getFluidState(mut).isEmpty()) {
                    mut.setY(mut.getY() - 1);
                }
                final BlockState top = chunk.getBlockState(mut);
                if (!top.getFluidState().isEmpty() || SteppedSkirt.passable(top)) continue;
                mut.setY(mut.getY() + 1);
                if (!chunk.getBlockState(mut).isAir()) continue;
                mut.setY(mut.getY() - 1);
                if (patch && isBareGround(top)) {
                    // A bare end stone / dust / sand stretch of the shore gets a patch of the column's moss.
                    final BlockState moss = mats[x * 16 + z].is(CommonBlockTags.SOIL)
                            ? mats[x * 16 + z]
                            : EndTerrainBlocks.END_MOSS.defaultBlockState();
                    setChunkBlock(chunk, mut, moss);
                }
                if (!shorePlantHere(wx, wz, d)) continue;
                placeRimPlant(world, chunk, mut, new BlockPos(wx, mut.getY(), wz));
            }
        }
    }

    /** Marker for "no solid ground found within {@link #GROUND_SEARCH_DEPTH}". */
    private static final int NO_GROUND = Integer.MIN_VALUE;
    /** Marker for "fluid found before solid ground" (a lower lake this column reaches over). */
    private static final int FLUID_BELOW = Integer.MIN_VALUE + 1;

    /**
     * Closes the gap below the water of one column: filled completely when solid ground lies within
     * {@link #BRIDGE_DEPTH} blocks, otherwise only a {@link #SHELL_DEPTH} thick shell that follows the bowl
     * (caves and the void below stay open). Bank columns are handled before carving by the stepped skirt.
     */
    private void groundColumn(ChunkAccess chunk, MutableBlockPos mut) {
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
        }
        // Bank columns were already stepped down to the terrain before carving (see SteppedSkirt).
    }

    /**
     * Cuts an open-sky column (chunk-local x/z in {@code mut}) back towards {@code envelope}: its terrain top
     * above {@code fromY} is lowered by the excess over the envelope, tapered like
     * {@link SteppedSkirt#taper}. Columns with a cave or overhang above their top are left alone, and fluids and
     * blocks the bank may not replace (structures) stop the cut, and so does water within
     * {@link #DIKE_KEEP} blocks (the bank of a higher lake).
     */
    private void cutBack(ChunkAccess chunk, MutableBlockPos mut, int fromY, int envelope) {
        final int origY = mut.getY();
        int top = fromY - 1;
        for (int y = fromY; y < fromY + GROUND_SEARCH_DEPTH; y++) {
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            if (state.isAir() || SteppedSkirt.passable(state)) break;
            if (!state.getFluidState().isEmpty() || !state.is(CommonBlockTags.TERRAIN) && !state.is(CommonBlockTags.END_STONES)) {
                mut.setY(origY);
                return;
            }
            top = y;
        }
        if (top < fromY) {
            mut.setY(origY);
            return;
        }
        for (int y = top + 1; y <= top + 4; y++) {   // open sky above, not a cave ceiling
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            if (!state.isAir() && !SteppedSkirt.passable(state)) {
                mut.setY(origY);
                return;
            }
        }
        final int newTop = Math.max(fromY - 1, top - (SteppedSkirt.taper(envelope, top, BANK_MAX_RAISE) - envelope));
        if (newTop >= top) {
            mut.setY(origY);
            return;
        }
        // Never cut into what holds another (higher) lake's water.
        for (int y = newTop + 1; y <= top; y++) {
            mut.setY(y);
            if (nearFluid(chunk, mut, DIKE_KEEP)) {
                mut.setY(origY);
                return;
            }
        }
        final BlockState surface = chunk.getBlockState(mut.setY(top));
        for (int y = top + 4; y > newTop; y--) {   // the cut terrain and whatever grew on it
            mut.setY(y);
            setChunkBlock(chunk, mut, Blocks.AIR.defaultBlockState());
        }
        mut.setY(newTop);
        if (surface.is(CommonBlockTags.TERRAIN)) setChunkBlock(chunk, mut, surface);
        mut.setY(origY);
    }

    /**
     * Whether a fluid lies within {@code reach} blocks horizontally (same y, inside this chunk) or right above.
     * Used to keep the dike of a higher, overlapping lake: {@link #DIKE_KEEP} blocks of it survive this
     * lake's dry carve whatever order the two lakes generate in.
     */
    private static boolean nearFluid(ChunkAccess chunk, MutableBlockPos mut, int reach) {
        final int x = mut.getX();
        final int y = mut.getY();
        final int z = mut.getZ();
        boolean wet = !chunk.getFluidState(mut.setY(y + 1)).isEmpty();
        mut.setY(y);
        for (int dx = -reach; dx <= reach && !wet; dx++) {
            final int px = x + dx;
            if (px < 0 || px > 15) continue;
            final int rz = reach - Math.abs(dx);
            for (int dz = -rz; dz <= rz && !wet; dz++) {
                final int pz = z + dz;
                if (pz < 0 || pz > 15 || (dx == 0 && dz == 0)) continue;
                wet = !chunk.getFluidState(mut.set(px, y, pz)).isEmpty();
            }
        }
        mut.set(x, y, z);
        return wet;
    }

    /**
     * This lake's shape at a column as an implicit function: negative under the surface water, zero on the
     * waterline, positive outside. Uses exactly the carve's formula (noise-distorted radius, height clamp,
     * surface row) but in squared form ({@code r^2 - edge^2}), which stays smooth where the clamp shrinks the
     * lake to nothing - the square-root form has an infinite slope there, and its distance estimate jumped by
     * several blocks between neighbouring columns. The value is not a distance yet, see {@link #shoreDistance}.
     */
    private double edgeFunction(WorldGenLevel world, int wx, int wz) {
        final double nx = wx * 0.1;
        final double nz = wz * 0.1;
        final double n = noise.eval(nx, nz) * 1.5 + 1.5;
        final double x3 = MHelper.sqr(wx - center.getX() + noise.eval(nx, nz, 100) * 10);
        final double z3 = MHelper.sqr(wz - center.getZ() + noise.eval(nx, nz, -100) * 10);
        final double ys = (waterLevel - 1 - n - center.getY()) * aspect;
        // Far outside the shore the exact value does not matter; skip the (biome-reading) clamp there.
        if (Math.sqrt(x3 + z3) - radius > 4 * MAX_BANK_DISTANCE) return x3 + z3 - radius * radius + ys * ys;
        final double rc = radius * getHeightClamp(world, 8, wx, wz);
        return x3 + z3 - rc * rc + ys * ys;
    }

    /**
     * The rim/floor material of every column of a chunk: the column's own topmost terrain block within the
     * piece's height range if that is a real surface (moss, nylium, sulphuric rock, ...), otherwise - bare end
     * stone or end stone dust, or no terrain at all - the biome's top material.
     * <p>
     * This used to be one field on the piece, overwritten by whichever chunk sampled last. A lake spans many
     * chunks that are generated in no fixed order (and in parallel), so the material a chunk got depended on
     * which other chunks had already been generated. Reading each column's own surface, before this piece
     * changes it, makes the result depend on that column only.
     */
    private BlockState[] columnMaterials(WorldGenLevel world, ChunkAccess chunk, int maxY) {
        final BlockState fallback = biomeMaterial(world);
        final BlockState[] out = new BlockState[256];
        final MutableBlockPos p = new MutableBlockPos();
        final int bottom = Math.max(carveMinY(), chunk.getMinY());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                BlockState found = fallback;
                for (int y = maxY; y >= bottom; y--) {
                    final BlockState state = chunk.getBlockState(p.set(x, y, z));
                    if (state.isAir() || !state.getFluidState().isEmpty() || SteppedSkirt.passable(state)) continue;
                    if (state.is(CommonBlockTags.TERRAIN)
                            && !state.is(Blocks.END_STONE)
                            && !state.is(EndTerrainBlocks.ENDSTONE_DUST)) {
                        found = state.getBlock().defaultBlockState();
                    }
                    break;
                }
                out[x * 16 + z] = found;
            }
        }
        return out;
    }

    /**
     * Distance (blocks) of each column of a chunk outside the waterline (negative under the water), indexed
     * {@code x * 16 + z}.
     * <p>
     * First a raw estimate per column: {@link #edgeFunction} divided by its gradient (the usual signed-distance
     * normalisation; the gradient from the column's neighbours). That is exact on the waterline but erratic
     * further out - the noise distortion makes the gradient swing, and the estimate jumped by up to five blocks
     * between neighbouring columns, which became a wall in the bank. So the raw values are then relaxed over a
     * {@link #DIST_RELAX}-column neighbourhood: {@code d(c) = min(raw(n) + |c - n|)}. The result changes by at
     * most about one block per column, never exceeds the raw estimate, and is a pure function of world
     * coordinates (the neighbourhood is computed analytically, not read from other chunks), so it is the same
     * in every chunk.
     */
    private double[] shoreDistances(WorldGenLevel world, int sx, int sz) {
        final int k = DIST_RELAX;
        final int span = 16 + 2 * k + 2;          // raw estimates on [-k, 16 + k) need f on [-k - 1, 16 + k]
        final double[] f = new double[span * span];
        for (int gx = 0; gx < span; gx++) {
            for (int gz = 0; gz < span; gz++) {
                f[gx * span + gz] = edgeFunction(world, sx + gx - k - 1, sz + gz - k - 1);
            }
        }
        final int rs = 16 + 2 * k;
        final double[] raw = new double[rs * rs];
        for (int rx = 0; rx < rs; rx++) {
            for (int rz = 0; rz < rs; rz++) {
                final int i = (rx + 1) * span + rz + 1;
                final double gx = (f[i + span] - f[i - span]) * 0.5;
                final double gz = (f[i + 1] - f[i - 1]) * 0.5;
                raw[rx * rs + rz] = f[i] / Math.max(1.0, Math.sqrt(gx * gx + gz * gz));
            }
        }
        final double[] out = new double[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double best = raw[(x + k) * rs + z + k];
                for (int dx = -k; dx <= k; dx++) {
                    for (int dz = -k; dz <= k; dz++) {
                        final double v = raw[(x + k + dx) * rs + z + k + dz] + Math.sqrt(dx * dx + dz * dz);
                        if (v < best) best = v;
                    }
                }
                out[x * 16 + z] = best;
            }
        }
        return out;
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
            setChunkBlock(chunk, mut, ENDSTONE);
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
            setChunkBlock(chunk, mut, ENDSTONE);
        }
    }

    /**
     * Places one shore plant on top of the block at {@code surface}: a weighted choice, by a hash of the column,
     * among the {@link #rimPlants()} that can survive there (so soil plants land on moss and the fur also
     * covers bare end stone and dust; plants marked soil-only never go on dust, sand or bare stone). Plants without a position offset are kept only with
     * {@link #RIM_FUR_CHANCE}.
     *
     * @param surface      the block position in chunk-local X/Z (world Y)
     * @param surfaceWorld the same block in world coordinates, for {@code canSurvive} lookups
     */
    private void placeRimPlant(
            WorldGenLevel world,
            ChunkAccess chunk,
            MutableBlockPos surface,
            BlockPos surfaceWorld
    ) {
        final BlockState[] plants = rimPlants();
        final BlockPos plantLocal = surface.above();
        final BlockPos plantWorld = surfaceWorld.above();
        final BlockState ground = chunk.getBlockState(surface);
        final boolean soil = ground.is(CommonBlockTags.SOIL)
                && !ground.is(EndTerrainBlocks.ENDSTONE_DUST)
                && !ground.is(net.minecraft.tags.BlockTags.SAND)
                && !ground.is(Blocks.END_STONE);
        int total = 0;
        final boolean[] fits = new boolean[plants.length];
        for (int i = 0; i < plants.length; i++) {
            fits[i] = (soil || !rimSoilOnly[i]) && plants[i].canSurvive(world, plantWorld);
            if (fits[i]) total += rimWeights[i];
        }
        if (total == 0) return;
        double pick = hash01(surfaceWorld.getX(), surfaceWorld.getZ(), 0x5EEDL) * total;
        for (int i = 0; i < plants.length; i++) {
            if (!fits[i]) continue;
            pick -= rimWeights[i];
            if (pick < 0) {
                final BlockState plant = plants[i];
                if (!plant.hasOffsetFunction()
                        && hash01(surfaceWorld.getX(), surfaceWorld.getZ(), 0xF0F0L) >= RIM_FUR_CHANCE) {
                    return;
                }
                setChunkBlock(chunk, plantLocal, plant);
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
            int sz,
            BlockState[] mats
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
                                bState = shorePatch(mats[x * 16 + z], x | sx, z | sz, 0.0);
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
                                        bState = shorePatch(mats[x * 16 + z], x | sx, z | sz, 0.0);
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
                            markForPostProcessing(chunk, mut.move(Direction.DOWN).immutable());
                        }
                    } else {
                        final BlockState bState = chunk.getBlockState(mut);
                        // A plant whose support this lake (or an overlapping one) carved away or turned
                        // into water would float: remove it. Runs bottom-up, so the upper half of a
                        // double plant follows its lower half.
                        if (isLoosePlant(bState) && !staysSupported(world, bState, mut.offset(sx, 0, sz))) {
                            setChunkBlock(chunk, mut, Blocks.AIR.defaultBlockState());
                        } else if (bState.isRandomlyTicking()) {
                            markForPostProcessing(chunk, mut.immutable());
                        }
                    }
                }
            }
        }
    }

    /** Shore plants are dense up to this distance (blocks) from the water ... */
    private static final double SHORE_PLANTS_DENSE = 4;
    /** ... and thin out down to the sporadic tail at this distance. */
    private static final double SHORE_PLANTS_END = 7;
    /** Chance of a sporadic shore plant beyond SHORE_PLANTS_END ... */
    private static final double SHORE_PLANTS_TAIL = 0.04;
    /** ... kept up to this distance ... */
    private static final double SHORE_PLANTS_TAIL_FULL = 12;
    /** ... and fading linearly to none at this distance. */
    private static final double SHORE_PLANTS_TAIL_END = 16;

    /**
     * Whether the lake puts a shore plant on the bank column {@code wx/wz}, {@code dOut} blocks from the water.
     * The chance is 1 up to {@link #SHORE_PLANTS_DENSE} blocks, falls off smoothly to the sporadic
     * {@link #SHORE_PLANTS_TAIL} at {@link #SHORE_PLANTS_END}, stays there up to {@link #SHORE_PLANTS_TAIL_FULL}
     * and fades out by {@link #SHORE_PLANTS_TAIL_END}, and each column draws against it with a hash of its world position (not the
     * chunk's random, no noise field): the thinning looks like scattered individual plants, and it is the same
     * whichever chunk or order a column is generated in.
     */
    private boolean shorePlantHere(int wx, int wz, double dOut) {
        if (dOut >= SHORE_PLANTS_TAIL_END) return false;
        final double p;
        if (dOut <= SHORE_PLANTS_DENSE) {
            p = 1.0;
        } else if (dOut < SHORE_PLANTS_END) {
            final double t = (dOut - SHORE_PLANTS_DENSE) / (SHORE_PLANTS_END - SHORE_PLANTS_DENSE);
            p = SHORE_PLANTS_TAIL + (1.0 - SHORE_PLANTS_TAIL) * (1.0 - t * t * (3 - 2 * t));
        } else if (dOut <= SHORE_PLANTS_TAIL_FULL) {
            p = SHORE_PLANTS_TAIL;
        } else {
            p = SHORE_PLANTS_TAIL * (SHORE_PLANTS_TAIL_END - dOut) / (SHORE_PLANTS_TAIL_END - SHORE_PLANTS_TAIL_FULL);
        }
        return hash01(wx, wz, 0L) < p;
    }

    /** Chance that a bare shore column up to SHORE_PLANTS_DENSE blocks from the water gets a moss patch. */
    private static final double SHORE_MOSS_PATCH = 0.6;

    /** End stone, end stone dust or sand: shore ground nothing but the rare blue vine fur grows on. */
    private static boolean isBareGround(BlockState state) {
        return state.is(Blocks.END_STONE)
                || state.is(EndTerrainBlocks.ENDSTONE_DUST)
                || state.is(net.minecraft.tags.BlockTags.SAND);
    }

    /**
     * Whether a bare shore column becomes a moss patch (which the shore plants then grow on): moss patches
     * follow a low-frequency noise, so they form clusters with bare stretches between them, and each column
     * inside a cluster draws against {@link #SHORE_MOSS_PATCH} (fading out like the plants by
     * {@link #SHORE_PLANTS_END}) with its own position hash.
     */
    private boolean shorePatchHere(int wx, int wz, double dOut) {
        if (noise.eval(wx * 0.12, wz * 0.12, 800) < -0.15) return false;
        double p = SHORE_MOSS_PATCH;
        if (dOut > SHORE_PLANTS_DENSE) {
            final double t = Math.min(1.0, (dOut - SHORE_PLANTS_DENSE) / (SHORE_PLANTS_END - SHORE_PLANTS_DENSE));
            p *= 1.0 - t * t * (3 - 2 * t);
        }
        return hash01(wx, wz, 0xB055L) < p;
    }

    /** A well-mixed pseudo-random value in [0, 1) from a column position, this lake's seed and a salt. */
    private double hash01(int wx, int wz, long salt) {
        long h = (long) wx * 0x9E3779B97F4A7C15L ^ (long) wz * 0xC2B2AE3D27D4EB4FL ^ (seed + salt) * 0x165667B19E3779F9L;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        return (h >>> 11) * 0x1.0p-53;
    }

    /**
     * Whether a plant already standing at {@code pos} still has its support. {@code canSurvive} is the
     * placement check, and for double plants it also asks for free space above the lower half - which the
     * upper half occupies, so every tall plant the cleanup looked at was removed. Double plants use their
     * own "stays" check instead.
     */
    private static boolean staysSupported(WorldGenLevel world, BlockState state, BlockPos pos) {
        if (state.getBlock() instanceof BaseDoublePlantBlock plant) return plant.canStayAt(state, world, pos);
        return state.canSurvive(world, pos);
    }

    /** A plant (or other loose, replaceable block) that is neither air nor a fluid. */
    private static boolean isLoosePlant(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty() && BlocksHelper.replaceableOrPlant(state);
    }

    /**
     * Marks a block for the chunk's post-processing pass (water and randomly ticking blocks settle there).
     * Only a chunk that is still being generated has that pass: a full chunk (a lake placed with
     * {@code /place}) only logs a warning per call, tens of thousands of them.
     */
    private static void markForPostProcessing(ChunkAccess chunk, BlockPos pos) {
        if (!(chunk instanceof LevelChunk)) chunk.markPosForPostProcessing(pos);
    }

    private void makeEndstonePillar(ChunkAccess chunk, MutableBlockPos mut, BlockState terrain) {
        setChunkBlock(chunk, mut, terrain);
        mut.setY(mut.getY() - 1);
        while (!chunk.getFluidState(mut).isEmpty()) {
            setChunkBlock(chunk, mut, ENDSTONE);
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

        h = worldgenSurfaceHeight(world, pos.getX(), pos.getZ());
        h = Mth.abs(h - center.getY());
        h = h < 8 ? 1 : 0;

        heightmap.put(p, (byte) h);
        return h;
    }

    // Height clamps per column (radius 8, the only one used): the shore's distance field asks for the clamp of
    // a 30x30 window around every chunk the lake touches and the carve for every column again, so each column's
    // 200-column average was recomputed many times over. Concurrent: chunks of one lake generate in parallel.
    private final Map<Long, Float> clampCache = new ConcurrentHashMap<>();

    private float getHeightClamp(WorldGenLevel world, int radius, int posX, int posZ) {
        if (radius != 8) return computeHeightClamp(world, radius, posX, posZ);
        final long key = ((long) posX << 32) | (posZ & 0xFFFFFFFFL);
        Float c = clampCache.get(key);
        if (c == null) {
            c = computeHeightClamp(world, radius, posX, posZ);
            clampCache.put(key, c);
        }
        return c;
    }

    private float computeHeightClamp(WorldGenLevel world, int radius, int posX, int posZ) {
        MutableBlockPos mut = new MutableBlockPos();
        // Sample the biome at the top of the world, not at y=0. Cave biomes are vertical biomes that own
        // the column up to the cave band (which can reach above a low lake's water level), so at y=0
        // getHeight saw the cave biome under the lake, never matched biomeID and returned 0 for every
        // column: megalakes carved nothing at all. Above the band only the land biome is left (the same
        // probe FeatureBaseStructure#mayStartInBiome uses; see MountainPiece#getHeightClamp too).
        mut.setY(world.getMaxY());
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
        // radius + 8 covers the rim band; the stepped bank reaches MAX_BANK_DISTANCE farther out, plus
        // ~10 blocks of horizontal noise distortion of the shape.
        final int margin = 8 + MAX_BANK_DISTANCE + 10;
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