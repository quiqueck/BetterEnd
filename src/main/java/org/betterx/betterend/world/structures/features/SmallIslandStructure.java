package org.betterx.betterend.world.structures.features;


import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.blocks.StalactiteBlock;
import org.betterx.bclib.sdf.PosInfo;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFCoordModify;
import org.betterx.bclib.sdf.operator.SDFRadialNoiseMap;
import org.betterx.bclib.sdf.operator.SDFScale3D;
import org.betterx.bclib.sdf.operator.SDFSmoothUnion;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.mixin.common.NoiseBasedChunkGeneratorAccessor;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.betterend.world.generator.TerrainGenerator;
import org.betterx.betterend.world.structures.piece.VoxelPiece;

import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

/**
 * A self-supporting, flat-topped small End island generated entirely from a signed-distance
 * field &mdash; the same VoxelPiece / {@link SDF#fillRecursive} machinery that
 * {@link GiantIceStarStructure} uses. Placed by both the {@code flower_islets} and
 * {@code waterfall_ponds} biomes so those lush void-ring biomes carry their own terrain instead
 * of relying on the (now removed) terrain-coupled small-island biome decider.
 *
 * <h2>Shape</h2>
 * The body reuses the authentic End-island cone recipe from
 * {@link org.betterx.betterend.world.generator.IslandLayer}: four {@link SDFCappedCone}s
 * smooth-unioned into a lens whose widest ring sits at the vertical centre. Instead of a uniform
 * {@code SDFScale}, the lens is scaled per-axis with {@link SDFScale3D} to an <b>ellipse</b>
 * (minor axis {@code 0.55..0.9} of the major, with a random 90&deg; orientation swap) so islands
 * no longer read as a field of identical round discs. A {@link SDFRadialNoiseMap} adds organic
 * relief to the top; its intensity is a little stronger in {@code flower_islets} (wavier rim) and
 * gentler in {@code waterfall_ponds}, where the central plateau must stay flat enough for
 * {@link org.betterx.betterend.world.features.terrain.PondWithWaterfallFeature}'s flatness gate
 * (variance &le; 3 over &plusmn;4 around the chunk centre). Radii are 6..12 (down from 10..20), so
 * the islands are noticeably smaller.
 *
 * <h2>Surface coat</h2>
 * The body is {@link Blocks#END_STONE}. The coat is applied inside the SDF via a post-process that
 * reads {@link PosInfo#getStateUp()} / {@link PosInfo#getStateDown()} (the consumer receives a
 * write-only {@code StructureWorld}, so a read-back second pass is impossible), which mirrors the
 * biomes' {@code getTopMaterial()} without touching biome JSON:
 * <ul>
 *     <li>{@code waterfall_ponds} (and any other biome): every exposed top {@code END_STONE} becomes
 *     {@link EndTerrainBlocks#END_MOSS}, as before, and ~6% of underside columns grow a short hanging
 *     {@code END_STONE_STALACTITE} strand (length 1..3, {@code IS_FLOOR=false}, SIZE tapering to the
 *     tip) for the cave-lip look beneath the ponds.</li>
 *     <li>{@code flower_islets}: the top becomes ~55% {@link EndTerrainBlocks#SANGNUM} mixed with
 *     pallidium patches. NOTE the {@code PALLIDIUM_*} blocks are full-cube {@code BaseTerrainBlock}s
 *     (model {@code cube_bottom_top}) whose <i>top texture</i> shows an increasing growth gradient
 *     (tiny&rarr;full); they are surface blocks, not carpets/layers, so they are used as the single
 *     top block (denser/sparser mossy patches) rather than physically stacked &mdash; stacking full
 *     cubes would produce umbralith-sided pillars. Additionally, mixed vine strands (~12% of underside
 *     columns, length 2..7: 50% glowing {@link EndVineBlocks#BULB_VINE}, 25%
 *     {@link EndVineBlocks#TWISTED_VINE}, 25% {@link EndVineBlocks#JUNGLE_VINE}; {@code TripleShape} set
 *     manually since worldgen writes skip block updates) hang from the island underside for the
 *     night-time flower-biome look.</li>
 * </ul>
 * The pallidium/vine decoration is emitted through {@link PosInfo#setBlockPos} (the SDF fill's
 * {@code addInfo} pass), so no extra placed feature and no FeatureSorter ordering is involved.
 *
 * <h2>Placement</h2>
 * Registered at {@link net.minecraft.world.level.levelgen.GenerationStep.Decoration#RAW_GENERATION}
 * (see {@link EndStructures#SMALL_ISLAND}) so the flat-topped island exists before the LAKES step,
 * where the pond feature carves its bowl, and before the vegetation/surface steps. The origin is
 * pulled towards the chunk centre so the pond feature's centre probe lands on the plateau. A random
 * per-start skip drops a quarter of attempts to break up the regular grid into organic gaps.
 */
public class SmallIslandStructure extends SDFStructureFeature {
    private static final int MIN_RADIUS = 6;
    private static final int MAX_RADIUS = 12;
    // waterfall_ponds islands are grown noticeably wider than flower_islets: the pond is sized to the
    // island's flat top MINUS a dry rim (see PondWithWaterfallFeature), so a bigger, flatter plateau is
    // what actually makes the water body larger while still leaving solid ground for shore plants and
    // the dragon-helix tree.
    private static final int WATERFALL_MIN_RADIUS = 12;
    private static final int WATERFALL_MAX_RADIUS = 17;
    private static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();
    private static final BlockState END_MOSS = EndTerrainBlocks.END_MOSS.defaultBlockState();
    private static final BlockState SANGNUM = EndTerrainBlocks.SANGNUM.defaultBlockState();
    private static final BlockState PALLIDIUM_FULL = EndTerrainBlocks.PALLIDIUM_FULL.defaultBlockState();
    private static final BlockState PALLIDIUM_HEAVY = EndTerrainBlocks.PALLIDIUM_HEAVY.defaultBlockState();
    private static final BlockState PALLIDIUM_THIN = EndTerrainBlocks.PALLIDIUM_THIN.defaultBlockState();
    private static final BlockState PALLIDIUM_TINY = EndTerrainBlocks.PALLIDIUM_TINY.defaultBlockState();
    // Pallidium blocks are umbralith-sided full cubes, so a pallidium surface sitting straight on end
    // stone looks disconnected; one umbralith block beneath each pallidium top makes it read as growth
    // on umbralith.
    private static final BlockState UMBRALITH = EndStoneBlocks.UMBRALITH.getBlock(SlotType.SOURCE).defaultBlockState();
    // Low-frequency spatial noise (world coords) that gathers the hanging vines/stalactites into
    // patches instead of an even sprinkle: dense where it is above the threshold, a stray or two
    // elsewhere.
    private static final OpenSimplexNoise UNDERSIDE_PATCH = new OpenSimplexNoise(0x5A9C_1DE5L);
    private static final double PATCH_FREQ = 0.14;
    private static final double PATCH_THRESHOLD = 0.05;

    public SmallIslandStructure(StructureSettings s) {
        super(s);
    }

    @Override
    public StructureType<SmallIslandStructure> type() {
        return EndStructures.SMALL_ISLAND.type();
    }

    @Override
    public java.util.Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // Void structure: islands must generate precisely where NO terrain exists, so the inherited
        // y >= 10 terrain gate would reject every chunk this structure is for. Stub Y at the middle
        // of the island height range (48..68); the biome check at the stub still gates placement.
        return findVoidGenerationPoint(context, 58);
    }

    /**
     * The per-island geometry random draws, pulled OUT of {@link #getSDF} and into
     * {@link #generatePieces} so the native-terrain overlap gate can use the actual drawn island
     * radius before the piece is built. Draw order inside {@link #drawGeometry}: radius, aspect,
     * swap, noiseSeed (identical to the old lazy {@code getSDF} order).
     */
    private record IslandGeometry(float radius, float scaleX, float scale, float scaleZ,
                                  float noiseIntensity, int noiseSeed) {}

    private static IslandGeometry drawGeometry(RandomSource random, boolean flowerIslets) {
        final float radius = flowerIslets
                ? MHelper.randRange(MIN_RADIUS, MAX_RADIUS, random)
                : MHelper.randRange(WATERFALL_MIN_RADIUS, WATERFALL_MAX_RADIUS, random);
        // Unit island half-width is 0.5, so the base per-axis scale = radius / 0.5 = 2 * radius.
        final float scale = radius / 0.5F;

        // Ellipse: squash one horizontal axis relative to the other, with a random 90-degree
        // orientation swap so islands read as varied ovals instead of identical round discs. The
        // vertical scale stays at the un-squashed base so the island keeps a natural lens height.
        // waterfall_ponds stays closer to round (0.8..0.95) so the plateau is wide on BOTH axes and the
        // pond does not get pinched by a strongly squashed minor axis; flower_islets can squash harder.
        final float aspect = flowerIslets
                ? MHelper.randRange(0.55F, 0.9F, random)
                : MHelper.randRange(0.8F, 0.95F, random);
        final float major = scale;
        final float minor = scale * aspect;
        final boolean swap = random.nextBoolean();
        final float scaleX = swap ? minor : major;
        final float scaleZ = swap ? major : minor;

        // Vertical noise amplitude in world blocks is ~ intensity * scale. flower_islets gets a wavier
        // top (no pond needs a flat plateau there); waterfall_ponds stays gentle so the central plateau
        // remains inside the pond flatness gate. (SDFRadialNoiseMap peaks at the centre and fades to the
        // rim, so we keep the waterfall value modest to protect the pond probe.)
        final float noiseIntensity = (flowerIslets ? 1.6F : 0.8F) / scale;

        final int noiseSeed = random.nextInt();
        return new IslandGeometry(radius, scaleX, scale, scaleZ, noiseIntensity, noiseSeed);
    }

    protected static SDF getSDF(RandomSource random, boolean flowerIslets, IslandGeometry geom) {
        // Authentic End-island cone stack (mirrors IslandLayer): a wide lower lens plus a shallow
        // top cap, all END_STONE.
        SDF cone1 = makeCone(0, 0.4F, 0.2F, -0.3F);
        SDF cone2 = makeCone(0.4F, 0.5F, 0.1F, -0.1F);
        SDF cone3 = makeCone(0.5F, 0.45F, 0.03F, 0.0F);
        SDF cone4 = makeCone(0.45F, 0, 0.02F, 0.03F);

        SDF coneBottom = new SDFSmoothUnion().setRadius(0.02F).setSourceA(cone1).setSourceB(cone2);
        SDF coneTop = new SDFSmoothUnion().setRadius(0.02F).setSourceA(cone3).setSourceB(cone4);
        SDF noise = new SDFRadialNoiseMap().setSeed(geom.noiseSeed())
                                           .setRadius(0.5F)
                                           .setIntensity(geom.noiseIntensity())
                                           .setSource(coneTop);
        SDF island = new SDFSmoothUnion().setRadius(0.01F).setSourceA(noise).setSourceB(coneBottom);
        // SDFScale3D preserves the sign of the distance field per-axis (fillRecursive only tests the
        // sign), giving a true ellipse rather than a uniformly scaled disc.
        SDF scaled = new SDFScale3D().setScale(geom.scaleX(), geom.scale(), geom.scaleZ()).setSource(island);

        // Outline randomisation: warp the horizontal sample coordinates with two independent noise
        // channels BEFORE the (scaled) cone field is evaluated - the same trick TerrainGenerator uses
        // for the native islands. This bends the silhouette into lobes and bays instead of a clean
        // ellipse; y is untouched so the plateau stays flat for the pond gate. Seeds derive from the
        // island's geometry seed (no extra random draws - the documented draw order is unchanged).
        final OpenSimplexNoise warpX = new OpenSimplexNoise(geom.noiseSeed() ^ 0x51AB_11E5L);
        final OpenSimplexNoise warpZ = new OpenSimplexNoise(geom.noiseSeed() ^ 0x0FF5_E7C0L);
        final float warpAmp = geom.radius() * 0.35F;
        // fillRecursive queries getDistance in LOCAL island coords (~[-radius, +radius]). The old
        // frequency 0.09 spanned <1 noise feature across the island, so it merely TRANSLATED the
        // ellipse (why it still looked like an ellipse). 0.22 spans ~4-5 features across a 20-block
        // island => several genuine lobes and bays. Amplitude 0.35*radius makes them pronounced
        // without pinching the island apart.
        SDF warped = new SDFCoordModify().setFunction((p) -> {
            final float wx = (float) warpX.eval(p.x() * 0.22, p.z() * 0.22) * warpAmp;
            final float wz = (float) warpZ.eval(p.x() * 0.22, p.z() * 0.22) * warpAmp;
            p.set(p.x() + wx, p.y(), p.z() + wz);
        }).setSource(scaled);

        return flowerIslets
                ? warped.addPostProcess((info) -> flowerCoat(info, random))
                : warped.addPostProcess((info) -> waterfallCoat(info, random));
    }

    /**
     * waterfall_ponds coat: END_MOSS on every exposed top, plus short hanging END_STONE_STALACTITE
     * strands under ~6% of underside columns for the cave-lip look beneath the ponds.
     */
    private static BlockState waterfallCoat(PosInfo info, RandomSource random) {
        BlockState state = info.getState();
        if (!state.is(Blocks.END_STONE)) {
            return state;
        }
        if (info.getStateUp().isAir()) {
            return END_MOSS;
        }
        if (info.getStateDown().isAir() && inUndersidePatch(info.getPos(), random, 0.55F, 0.05F)) {
            hangStalactite(info, random);
        }
        return state;
    }

    /**
     * True when this underside column should carry a hanging decoration: dense ({@code inChance}) inside
     * a {@link #UNDERSIDE_PATCH} patch, sparse ({@code outChance}) outside it, so vines/stalactites form
     * clusters rather than an even sprinkle.
     */
    private static boolean inUndersidePatch(BlockPos pos, RandomSource random, float inChance, float outChance) {
        final boolean inPatch = UNDERSIDE_PATCH.eval(pos.getX() * PATCH_FREQ, pos.getZ() * PATCH_FREQ) > PATCH_THRESHOLD;
        return random.nextFloat() < (inPatch ? inChance : outChance);
    }

    /**
     * Hangs a short END_STONE_STALACTITE strand (length 1..3) into the void below the island,
     * mirroring {@link org.betterx.betterend.world.features.terrain.StalactiteFeature}'s ceiling
     * (non-stalagnate) case: the widest segment sits at the attachment and the SIZE tapers to 0 at
     * the tip, {@code IS_FLOOR=false}. States are set manually since worldgen writes skip updateShape().
     */
    private static void hangStalactite(PosInfo info, RandomSource random) {
        final int length = MHelper.randRange(2, 5, random);
        final BlockPos base = info.getPos();
        for (int k = 1; k <= length; k++) {
            final int size = Mth.clamp(length - k, 0, 7);
            info.setBlockPos(
                    base.below(k),
                    EndStoneBlocks.END_STONE_STALACTITE.defaultBlockState()
                                  .setValue(StalactiteBlock.SIZE, size)
                                  .setValue(StalactiteBlock.IS_FLOOR, false)
            );
        }
    }

    /**
     * flower_islets coat: sangnum-dominant top with pallidium patches, plus glowing bulb-vine strands
     * hanging from the island underside. Only touches the END_STONE body; decoration blocks we add via
     * {@link PosInfo#setBlockPos} pass straight through (they are not END_STONE), and interior/side
     * end stone stays end stone.
     */
    private static BlockState flowerCoat(PosInfo info, RandomSource random) {
        BlockState state = info.getState();
        if (!state.is(Blocks.END_STONE)) {
            return state;
        }
        if (info.getStateUp().isAir()) {
            // Exposed top surface above the end-stone body.
            if (random.nextFloat() < 0.55F) {
                return SANGNUM;
            }
            // Pallidium patch: put one umbralith block directly beneath it so the umbralith-sided
            // pallidium cube reads as growth on umbralith rather than floating on end stone.
            info.setBlockPos(info.getPos().below(), UMBRALITH);
            final float p = random.nextFloat();
            if (p < 0.5F) return PALLIDIUM_FULL;
            if (p < 0.8F) return PALLIDIUM_HEAVY;
            if (p < 0.93F) return PALLIDIUM_THIN;
            return PALLIDIUM_TINY;
        }
        if (info.getStateDown().isAir() && inUndersidePatch(info.getPos(), random, 0.7F, 0.08F)) {
            // Underside column: hang a glowing vine strand into the void below - clustered into patches.
            hangVineStrand(info, random);
        }
        return state;
    }

    /**
     * Hangs a mixed vine strand (length 2..7) from the island underside. Vine type is picked per
     * strand: 50% {@link EndVineBlocks#BULB_VINE} (glowing), 25% {@link EndVineBlocks#TWISTED_VINE},
     * 25% {@link EndVineBlocks#JUNGLE_VINE}. All three are {@link BaseVineBlock}s carrying the
     * {@code TripleShape} SHAPE property (BULB_VINE via BulbVineBlock extends BaseVineBlock;
     * TWISTED_VINE and JUNGLE_VINE are plain BaseVineBlock), so the manual TOP/MIDDLE/BOTTOM state
     * logic ports unchanged. States are set manually because worldgen writes bypass updateShape().
     */
    private static void hangVineStrand(PosInfo info, RandomSource random) {
        final int length = MHelper.randRange(3, 10, random);
        final Block vine = pickVine(random);
        final BlockPos base = info.getPos();
        for (int k = 1; k <= length; k++) {
            // Top segment attaches under the end stone; lowest is BOTTOM; the rest MIDDLE.
            final TripleShape shape = (k == length)
                    ? TripleShape.BOTTOM
                    : (k == 1 ? TripleShape.TOP : TripleShape.MIDDLE);
            info.setBlockPos(
                    base.below(k),
                    vine.defaultBlockState().setValue(BaseVineBlock.SHAPE, shape)
            );
        }
    }

    private static Block pickVine(RandomSource random) {
        final float p = random.nextFloat();
        if (p < 0.5F) return EndVineBlocks.BULB_VINE;
        if (p < 0.75F) return EndVineBlocks.TWISTED_VINE;
        return EndVineBlocks.JUNGLE_VINE;
    }

    private static SDF makeCone(float radiusBottom, float radiusTop, float height, float minY) {
        float hh = height * 0.5F;
        SDF sdf = new SDFCappedCone().setHeight(hh).setRadius1(radiusBottom).setRadius2(radiusTop).setBlock(END_STONE);
        return new SDFTranslate().setTranslate(0, minY + hh, 0).setSource(sdf);
    }

    @Override
    public void generatePieces(StructurePiecesBuilder structurePiecesBuilder, GenerationContext context) {
        final RandomSource random = context.random();
        final ChunkPos chunkPos = context.chunkPos();

        // Draw order (documented): skip-roll -> x -> z -> y -> geometry(radius, aspect, swap, noiseSeed)
        // -> [overlap gate, no draw] -> piece seed. The geometry draws used to happen LAZILY inside the
        // fill lambda (after the piece seed); they are pulled forward to here so the overlap gate can
        // use the actual drawn island radius before the piece is built. This shifts the RNG sequence,
        // so existing seeds render differently - acceptable while worldgen is still in flux.

        // Per-start organic skip: consume the random FIRST so a quarter of the remaining island attempts
        // drop out. Combined with the sparser structure-set spacing and the in-cell offset below, this
        // replaces the dense, regular polka-dot grid with scattered gaps.
        if (random.nextFloat() < 0.25F) {
            org.betterx.betterend.util.WorldgenDebug.log(
                    "SmallIslandStructure: skipped (organic gap) at chunk %s", chunkPos
            );
            return;
        }

        // Pull the island towards the chunk centre (8,8) so the pond feature's centre probe lands on
        // the plateau. y is the approximate island TOP surface (the widest ring sits at the flood
        // start, and the flat top ends up a little above it).
        int x = chunkPos.getBlockX(MHelper.randRange(6, 10, random));
        int z = chunkPos.getBlockZ(MHelper.randRange(6, 10, random));
        int y = MHelper.randRange(48, 68, random);
        BlockPos start = new BlockPos(x, y, z);

        // Choose the coat palette from the biome we are actually generating in: flower_islets gets the
        // sangnum/pallidium + vine treatment; waterfall_ponds (and anything else) keeps end moss +
        // stalactites. (No random draw.)
        final boolean flowerIslets = isFlowerIslets(context, x, y, z);

        // Geometry draws (radius/aspect/swap/noiseSeed) - AFTER the position draws so the overlap gate
        // below can probe with the true island radius.
        final IslandGeometry geom = drawGeometry(random, flowerIslets);

        // Never overlap native End terrain: probe TerrainGenerator.isLand at the island centre and at
        // +-radius on each horizontal axis (block coords -> quart via >>2). isLand is static +
        // LOCKER-guarded and safe off-thread. If ANY probe hits native land, skip this island without
        // adding a piece. (TerrainGenerator may be uninitialized during early datagen -> its null guard
        // returns false = fails open = island generates; acceptable.)
        final int maxHeight = terrainMaxHeight(context);
        final int r = Math.round(geom.radius());
        if (overlapsNativeLand(x, z, r, maxHeight)) {
            org.betterx.betterend.util.WorldgenDebug.log(
                    "SmallIslandStructure: skipped (native End terrain overlap) at chunk %s (r=%d)", chunkPos, r
            );
            return;
        }

        org.betterx.betterend.util.WorldgenDebug.log(
                "SmallIslandStructure: piece at %s (chunk %s, flowerIslets=%s)", start, chunkPos, flowerIslets
        );
        VoxelPiece piece = new VoxelPiece((world) -> {
            getSDF(random, flowerIslets, geom).fillRecursive(world, start);
        }, random.nextInt());
        structurePiecesBuilder.addPiece(piece);
    }

    /**
     * Probes {@link TerrainGenerator#isLand} at the island centre and at {@code +-r} on each horizontal
     * axis, converting block coordinates to quart positions ({@code >> 2}). Returns true if ANY probe
     * lands on native End terrain, so the caller can skip the island rather than overlap real land.
     */
    private static boolean overlapsNativeLand(int blockX, int blockZ, int r, int maxHeight) {
        final int[][] probes = {{0, 0}, {r, 0}, {-r, 0}, {0, r}, {0, -r}};
        for (int[] p : probes) {
            final int qx = (blockX + p[0]) >> 2;
            final int qz = (blockZ + p[1]) >> 2;
            if (TerrainGenerator.isLand(qx, qz, maxHeight)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The world's noise-generation height ({@code noiseSettings().height()}) - the same {@code maxHeight}
     * the biome deciders feed {@link TerrainGenerator#isLand}, so the overlap probe matches the cached
     * land/void classification. Falls back to 128 for non-noise generators (never the case for the End).
     */
    private static int terrainMaxHeight(GenerationContext context) {
        // Cast from the ChunkGenerator supertype (not the concrete NoiseBasedChunkGenerator) so the
        // mixin-applied accessor interface is visible to the compiler, mirroring
        // TerrainGenerator.onServerLevelInit.
        final ChunkGenerator cg = context.chunkGenerator();
        if (cg instanceof NoiseBasedChunkGenerator) {
            return ((NoiseBasedChunkGeneratorAccessor) cg).be_getSettings().value().noiseSettings().height();
        }
        return 128;
    }

    private boolean isFlowerIslets(GenerationContext context, int x, int y, int z) {
        // Biome coordinates are quart-positions (>> 2).
        Holder<Biome> biome = getNoiseBiome(
                context.chunkGenerator(), context.randomState(), x >> 2, y >> 2, z >> 2
        );
        return biome.is(EndBiomes.FLOWER_ISLETS.key);
    }
}
