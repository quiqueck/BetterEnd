package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.structures.piece.CrystalMountainPiece;
import org.betterx.betterend.world.structures.piece.PaintedMountainPiece;
import org.betterx.betterend.world.structures.piece.VoxelPiece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Regression test for BetterEnd #556: mountains (and other multi-chunk structure pieces) were cut off along
 * chunk borders in chunks generated after the world was reloaded.
 * <p>
 * A structure piece is {@code postProcess()}ed once per chunk it touches, and the start is saved with the
 * chunk in between - so a piece must produce exactly the same blocks whether all its chunks are generated in
 * one go or some of them only after the piece went through NBT and the chunks went through a save/load. That
 * second half is what broke: the {@code *_WG} worldgen heightmaps are not persisted for chunks at CARVERS or
 * later, so after a reload {@code getOrCreateHeightmapUnprimed(WORLD_SURFACE_WG)} returned an empty heightmap
 * and {@code getHeight(WORLD_SURFACE_WG)} re-primed neighbours from their current blocks (including the
 * mountain already placed there), and the mountain's height clamp collapsed at the chunk border.
 * <p>
 * The test builds a 5x5 patch of {@link ProtoChunk}s (outside the live level) with base terrain from the
 * End's own generator, then places a piece on the inner 3x3 in three ways and compares them block by block
 * to the uninterrupted reference:
 * <ul>
 *     <li><b>nbt</b>: the piece round-tripped through its NBT, on fresh chunks;</li>
 *     <li><b>reloaded</b>: the round-tripped piece on chunks that were "loaded from disk" (no WG heightmaps);</li>
 *     <li><b>interrupted</b>: one chunk column placed, then the world "saved and reloaded" (piece through NBT,
 *     all chunks lose their WG heightmaps), then the rest placed.</li>
 * </ul>
 */
public class StructurePieceReloadGameTest {
    private static final double SLOW_COLUMN_MS = 10.0;
    private static final int PATCH = 2; // chunks -2..2 exist (mountains sample terrain 12 blocks around)
    private static final int PLACE = 1; // pieces are placed into chunks -1..1

    /**
     * This test asks the End's generator for the height of thousands of columns. That is cheap on vanilla's End
     * and on BetterEnd's own terrain, but another mod's density-function End (for example Stellarity's) makes
     * every column a full scan of its density graph, and the test then keeps the server busy for many minutes
     * without testing anything about BetterEnd. So the cost of a few columns is measured first, and an End
     * where they are slow is skipped.
     */
    private static boolean skipIfHeightScansAreSlow(GameTestHelper helper) {
        final ServerLevel end = helper.getLevel().getServer().getLevel(Level.END);
        if (end == null) return false;
        final ChunkGenerator generator = end.getChunkSource().getGenerator();
        final RandomState randomState = end.getChunkSource().randomState();

        final int columns = 4;
        final long start = System.nanoTime();
        for (int i = 0; i < columns; i++) {
            generator.getBaseHeight(1000 + i * 37, -1000 - i * 53, Heightmap.Types.WORLD_SURFACE_WG, end, randomState);
        }
        final double msPerColumn = (System.nanoTime() - start) / 1e6 / columns;
        if (msPerColumn < SLOW_COLUMN_MS) return false;

        BetterEnd.LOGGER.info(
                "StructurePieceReloadGameTest skipped: a height scan takes {} ms per column here (limit {} ms)",
                String.format("%.1f", msPerColumn), SLOW_COLUMN_MS
        );
        helper.succeed();
        return true;
    }

    @GameTest(maxTicks = 2400)
    public void crystalMountainSurvivesReload(GameTestHelper helper) {
        if (skipIfHeightScansAreSlow(helper)) return;
        final Env env = new Env(helper);
        final BlockPos center = env.surfaceCenter();
        check(helper, env, "crystal mountain", () -> new CrystalMountainPiece(
                center, 24, 48, RandomSource.create(556), env.biome
        ));
        helper.succeed();
    }

    @GameTest(maxTicks = 2400)
    public void paintedMountainSurvivesReload(GameTestHelper helper) {
        if (skipIfHeightScansAreSlow(helper)) return;
        final Env env = new Env(helper);
        final BlockPos center = env.surfaceCenter();
        final BlockState[] slices = {
                Blocks.END_STONE.defaultBlockState(),
                Blocks.PURPUR_BLOCK.defaultBlockState(),
                Blocks.END_STONE_BRICKS.defaultBlockState()
        };
        check(helper, env, "painted mountain", () -> new PaintedMountainPiece(
                center, 24, 48, RandomSource.create(556), env.biome, slices
        ));
        helper.succeed();
    }

    /** VoxelPiece keeps all its blocks in BCLib's StructureWorld; its NBT round trip must be lossless. */
    @GameTest(maxTicks = 2400)
    public void voxelPieceSurvivesReload(GameTestHelper helper) {
        if (skipIfHeightScansAreSlow(helper)) return;
        final Env env = new Env(helper);
        final BlockPos center = env.surfaceCenter().above(20);
        final BlockState[] states = {
                Blocks.END_STONE.defaultBlockState(),
                Blocks.PURPUR_BLOCK.defaultBlockState(),
                Blocks.PURPUR_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST),
                Blocks.PURPUR_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST),
                Blocks.WATER.defaultBlockState()
        };
        check(helper, env, "voxel piece", () -> new VoxelPiece(world -> {
            final RandomSource random = RandomSource.create(556);
            for (int x = -14; x <= 14; x++) {
                for (int y = -8; y <= 8; y++) {
                    for (int z = -14; z <= 14; z++) {
                        if (x * x + 3 * y * y + z * z < 14 * 14) {
                            world.setBlock(center.offset(x, y, z), states[random.nextInt(states.length)]);
                        }
                    }
                }
            }
        }, 0));
        helper.succeed();
    }

    private static void check(GameTestHelper helper, Env env, String name, Supplier<StructurePiece> factory) {
        final Map<ChunkPos, ProtoChunk> reference = env.freshPatch();
        env.placeAll(factory.get(), reference, chunk -> true);

        final Map<ChunkPos, ProtoChunk> terrain = env.freshPatch();
        int placed = 0;
        for (int cx = -PLACE; cx <= PLACE; cx++) {
            for (int cz = -PLACE; cz <= PLACE; cz++) {
                placed += env.countDifferences(terrain.get(new ChunkPos(cx, cz)), reference.get(new ChunkPos(cx, cz)));
            }
        }
        if (placed < 200) {
            throw helper.assertionException(Component.literal(
                    name + ": reference placement only changed " + placed + " blocks, test setup is not meaningful"));
        }

        final List<String> failures = new ArrayList<>();

        final Map<ChunkPos, ProtoChunk> nbt = env.freshPatch();
        env.placeAll(env.roundTrip(factory.get()), nbt, chunk -> true);
        env.compare("nbt", reference, nbt, failures);

        final Map<ChunkPos, ProtoChunk> reloaded = env.reload(env.freshPatch());
        env.placeAll(env.roundTrip(factory.get()), reloaded, chunk -> true);
        env.compare("reloaded", reference, reloaded, failures);

        final StructurePiece before = factory.get();
        Map<ChunkPos, ProtoChunk> interrupted = env.freshPatch();
        env.placeAll(before, interrupted, chunk -> chunk.x() == -PLACE);
        interrupted = env.reload(interrupted);
        env.placeAll(env.roundTrip(before), interrupted, chunk -> chunk.x() != -PLACE);
        env.compare("interrupted", reference, interrupted, failures);

        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    name + " (" + placed + " blocks) placed differently after a reload:\n - " + String.join("\n - ", failures)));
        }
    }

    private static final class Env {
        final ServerLevel end;
        final ChunkGenerator generator;
        final RandomState randomState;
        final PalettedContainerFactory containers;
        final Holder<Biome> biome;
        final int originX;
        final int originZ;
        final Map<Long, Integer> baseHeights = new HashMap<>();
        Map<ChunkPos, ProtoChunk> current;
        final WorldGenLevel world;

        Env(GameTestHelper helper) {
            end = helper.getLevel().getServer().getLevel(Level.END);
            if (end == null) throw helper.assertionException(Component.literal("the End is not loaded"));
            generator = end.getChunkSource().getGenerator();
            randomState = end.getChunkSource().randomState();
            containers = PalettedContainerFactory.create(end.registryAccess());
            biome = end.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.THE_END);
            world = fakeWorld();

            // Find a spot on solid End terrain (the main island).
            int ox = 0, oz = 0;
            search:
            for (int r = 0; r < 512; r += 16) {
                for (int dx = -r; dx <= r; dx += 16) {
                    for (int dz = -r; dz <= r; dz += 16) {
                        if (baseHeight((dx & ~15) + 8, (dz & ~15) + 8) > end.getMinY() + 40) {
                            ox = dx & ~15;
                            oz = dz & ~15;
                            break search;
                        }
                    }
                }
            }
            originX = ox;
            originZ = oz;
        }

        int baseHeight(int x, int z) {
            return baseHeights.computeIfAbsent(
                    BlockPos.asLong(x, 0, z),
                    k -> generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, end, randomState)
            );
        }

        BlockPos surfaceCenter() {
            final int x = originX + 8;
            final int z = originZ + 8;
            return new BlockPos(x, baseHeight(x, z), z);
        }

        ChunkPos worldPos(ChunkPos patchPos) {
            return new ChunkPos(patchPos.x() + (originX >> 4), patchPos.z() + (originZ >> 4));
        }

        ProtoChunk newChunk(int cx, int cz) {
            return new ProtoChunk(worldPos(new ChunkPos(cx, cz)), UpgradeData.EMPTY, end, containers, null);
        }

        /** Base terrain, as a chunk still in generation has it: WG heightmaps primed, then the final ones. */
        Map<ChunkPos, ProtoChunk> freshPatch() {
            final Map<ChunkPos, ProtoChunk> patch = new HashMap<>();
            for (int cx = -PATCH; cx <= PATCH; cx++) {
                for (int cz = -PATCH; cz <= PATCH; cz++) {
                    final ProtoChunk chunk = newChunk(cx, cz);
                    final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                    final int minX = chunk.getPos().getMinBlockX();
                    final int minZ = chunk.getPos().getMinBlockZ();
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            final int top = baseHeight(minX + x, minZ + z);
                            for (int y = end.getMinY(); y < top; y++) {
                                chunk.setBlockState(pos.set(minX + x, y, minZ + z), Blocks.END_STONE.defaultBlockState(), 0);
                            }
                        }
                    }
                    chunk.setPersistedStatus(ChunkStatus.CARVERS);
                    Heightmap.primeHeightmaps(chunk, ChunkStatus.FINAL_HEIGHTMAPS);
                    patch.put(new ChunkPos(cx, cz), chunk);
                }
            }
            return patch;
        }

        /** What a save/load does to a chunk at CARVERS or later: same blocks, but no WG heightmaps. */
        Map<ChunkPos, ProtoChunk> reload(Map<ChunkPos, ProtoChunk> patch) {
            final Map<ChunkPos, ProtoChunk> result = new HashMap<>();
            patch.forEach((key, chunk) -> {
                final ProtoChunk copy = newChunk(key.x(), key.z());
                copy.setPersistedStatus(ChunkStatus.CARVERS);
                forEachBlock(chunk, (pos, state) -> copy.setBlockState(pos, state, 0));
                Heightmap.primeHeightmaps(copy, ChunkStatus.FINAL_HEIGHTMAPS);
                result.put(key, copy);
            });
            return result;
        }

        void placeAll(
                StructurePiece piece,
                Map<ChunkPos, ProtoChunk> patch,
                java.util.function.Predicate<ChunkPos> which
        ) {
            current = patch;
            for (int cx = -PLACE; cx <= PLACE; cx++) {
                for (int cz = -PLACE; cz <= PLACE; cz++) {
                    final ChunkPos key = new ChunkPos(cx, cz);
                    if (!which.test(key)) continue;
                    final ChunkPos chunkPos = worldPos(key);
                    final BoundingBox box = new BoundingBox(
                            chunkPos.getMinBlockX(), end.getMinY(), chunkPos.getMinBlockZ(),
                            chunkPos.getMaxBlockX(), end.getMaxY(), chunkPos.getMaxBlockZ()
                    );
                    if (!piece.getBoundingBox().intersects(box)) continue;
                    piece.postProcess(
                            world, null, generator,
                            RandomSource.create(31L * chunkPos.x() + 1_000_003L * chunkPos.z()),
                            box, chunkPos, surfaceCenter()
                    );
                }
            }
        }

        StructurePiece roundTrip(StructurePiece piece) {
            final StructurePieceSerializationContext context = StructurePieceSerializationContext.fromLevel(end);
            final CompoundTag tag = piece.createTag(context);
            return piece.getType().load(context, tag);
        }

        void compare(String scenario, Map<ChunkPos, ProtoChunk> expected, Map<ChunkPos, ProtoChunk> actual, List<String> failures) {
            for (int cx = -PLACE; cx <= PLACE; cx++) {
                for (int cz = -PLACE; cz <= PLACE; cz++) {
                    final ChunkPos key = new ChunkPos(cx, cz);
                    final int diff = countDifferences(expected.get(key), actual.get(key));
                    if (diff > 0) {
                        failures.add(scenario + ": chunk " + key.x() + "," + key.z() + " differs in " + diff + " blocks");
                    }
                }
            }
        }

        int countDifferences(ChunkAccess a, ChunkAccess b) {
            int diff = 0;
            final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            final int minX = a.getPos().getMinBlockX();
            final int minZ = a.getPos().getMinBlockZ();
            for (int y = end.getMinY(); y <= end.getMaxY(); y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        pos.set(minX + x, y, minZ + z);
                        if (a.getBlockState(pos) != b.getBlockState(pos)) diff++;
                    }
                }
            }
            return diff;
        }

        void forEachBlock(ChunkAccess chunk, java.util.function.BiConsumer<BlockPos, BlockState> consumer) {
            final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            final int minX = chunk.getPos().getMinBlockX();
            final int minZ = chunk.getPos().getMinBlockZ();
            for (int y = end.getMinY(); y <= end.getMaxY(); y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        pos.set(minX + x, y, minZ + z);
                        final BlockState state = chunk.getBlockState(pos);
                        if (!state.isAir()) consumer.accept(pos, state);
                    }
                }
            }
        }

        ChunkAccess chunkAt(int worldChunkX, int worldChunkZ) {
            final ChunkPos key = new ChunkPos(worldChunkX - (originX >> 4), worldChunkZ - (originZ >> 4));
            final ChunkAccess chunk = current.get(key);
            if (chunk == null) {
                throw new IllegalStateException("piece reached outside the test patch: chunk " + key);
            }
            return chunk;
        }

        /** Answers the way {@code WorldGenRegion} does, backed by the test patch. */
        private WorldGenLevel fakeWorld() {
            final InvocationHandler handler = (proxy, method, args) -> {
                final int argc = args == null ? 0 : args.length;
                switch (method.getName()) {
                    case "getChunk":
                        if (argc >= 2 && args[0] instanceof Integer x && args[1] instanceof Integer z) {
                            return chunkAt(x, z);
                        }
                        if (argc == 1 && args[0] instanceof BlockPos p) {
                            return chunkAt(SectionPos.blockToSectionCoord(p.getX()), SectionPos.blockToSectionCoord(p.getZ()));
                        }
                        break;
                    case "getHeight":
                        if (argc == 0) return end.getHeight();
                        if (argc == 3) {
                            final int x = (Integer) args[1];
                            final int z = (Integer) args[2];
                            return chunkAt(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z))
                                    .getHeight((Heightmap.Types) args[0], x & 15, z & 15) + 1;
                        }
                        break;
                    case "getBlockState":
                        final BlockPos bp = (BlockPos) args[0];
                        return chunkAt(SectionPos.blockToSectionCoord(bp.getX()), SectionPos.blockToSectionCoord(bp.getZ()))
                                .getBlockState(bp);
                    case "getBiome":
                        return biome;
                    case "getMinY":
                        return end.getMinY();
                    case "getMaxY":
                        return end.getMaxY();
                    case "getLevel":
                        return end;
                    case "registryAccess":
                        return end.registryAccess();
                    case "equals":
                        return proxy == args[0];
                    case "hashCode":
                        return System.identityHashCode(proxy);
                    case "toString":
                        return "StructurePieceReloadGameTest.FakeWorldGenLevel";
                }
                throw new UnsupportedOperationException("WorldGenLevel." + method.getName() + " not stubbed for this test");
            };
            return (WorldGenLevel) Proxy.newProxyInstance(
                    StructurePieceReloadGameTest.class.getClassLoader(),
                    new Class<?>[]{WorldGenLevel.class},
                    handler
            );
        }
    }
}
