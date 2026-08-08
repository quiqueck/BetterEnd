package org.betterx.betterend.testmod.gametest;

import org.betterx.betterend.world.structures.piece.LakePiece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Dynamic companion to {@code StructurePieceThreadSafetyAuditTest} (src/test): rather than
 * pattern-matching bytecode, this drives a real {@link LakePiece}'s {@code getHeightClamp}/{@code
 * getHeight} - the exact methods named in
 * <a href="https://github.com/quiqueck/BetterEnd/issues/594">issue #594</a>'s thread dump - from many
 * threads at once, on one shared piece instance, with genuinely overlapping coordinates. That
 * reproduces what two c2me workers processing adjacent chunks of the same lake actually do to the
 * shared height cache. If the cache regresses to a plain, non-concurrent map, this reliably hangs or
 * throws instead of passing; the bounded {@code invokeAll} timeout turns that hang into an ordinary
 * test failure instead of wedging the run the way it wedged the reporter's dedicated server.
 * <p>
 * This is a GameTest (not a plain {@code src/test} JUnit test) because constructing a real {@code
 * LakePiece} touches {@code BuiltInRegistries} (structure piece type registration) and needs a real
 * {@code Holder<Biome>}; doing that outside a fully booted game trips over third-party mixins (e.g.
 * WTHIT hooking {@code ToolMaterial} init) that only work under a genuine Fabric/Mixin runtime.
 * <p>
 * The concurrent calls themselves deliberately do <b>not</b> go through the real {@code ServerLevel}
 * (i.e. not {@code helper.getLevel()}): an earlier version of this test did, and it deadlocked - the
 * worker threads' {@code world.getBiome(...)} calls block on something only the main server thread
 * can service, and the main thread was itself blocked waiting on {@code invokeAll()}. Real chunk/world
 * access from arbitrary background threads is exactly the kind of engine-level question c2me's own
 * scheduler solves and this test isn't trying to re-solve; it only cares whether {@link LakePiece}'s
 * own cache is safe. So the fake {@link WorldGenLevel} below answers {@code getBiome} with a
 * plain in-memory {@code Holder} obtained from the level once, up front, on the main thread - no
 * background thread ever touches the live level - and throws if anything else is called, which
 * {@code getHeightClamp}/{@code getHeight} never do on this (deliberately biome-mismatched) path.
 * <p>
 * Deliberately calls the two {@code private} methods directly (via reflection) instead of the public
 * {@code postProcess()} entry point: the bug lived entirely inside them, and reaching them this way
 * avoids the much larger {@code ChunkAccess}/{@code StructureManager}/{@code ChunkGenerator} surface
 * {@code postProcess()} needs for block placement, none of which is relevant to the cache race.
 */
public class LakePieceConcurrentHeightCacheGameTest {
    @GameTest(maxTicks = 1200)
    public void getHeightClampSurvivesConcurrentOverlappingCalls(GameTestHelper helper) throws Throwable {
        final Holder<Biome> pieceBiome = helper.getLevel()
                                                .registryAccess()
                                                .lookupOrThrow(Registries.BIOME)
                                                .getOrThrow(Biomes.END_HIGHLANDS);
        // Deliberately a different biome than pieceBiome: getHeight()'s "world.getBiome(pos).is(biomeID)"
        // check then always takes the false branch, which still exercises the exact
        // heightmap.getOrDefault/.put calls from the report, without needing world.getHeight() too.
        final Holder<Biome> worldBiome = helper.getLevel()
                                                .registryAccess()
                                                .lookupOrThrow(Registries.BIOME)
                                                .getOrThrow(Biomes.PLAINS);

        final LakePiece piece = new LakePiece(
                helper.absolutePos(new BlockPos(0, 5, 0)),
                48f,
                12f,
                RandomSource.create(42),
                pieceBiome,
                helper.absolutePos(new BlockPos(0, 5, 0)).getY()
        );
        final WorldGenLevel fakeWorld = fakeWorldGenLevel(worldBiome);

        final Method getHeightClamp = LakePiece.class.getDeclaredMethod(
                "getHeightClamp", WorldGenLevel.class, int.class, int.class, int.class
        );
        getHeightClamp.setAccessible(true);

        final int threads = 8;
        final int callsPerThread = 200;
        // Wide enough, with close-together per-thread offsets, that every thread's sample disc
        // overlaps its neighbors' - the same cache keys get hit from multiple threads at once, exactly
        // as adjacent chunks of one lake do in the real bug.
        final int radius = 24;
        final BlockPos origin = helper.absolutePos(BlockPos.ZERO);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            final List<Callable<Object>> tasks = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                final int threadOffset = t;
                tasks.add(() -> {
                    for (int i = 0; i < callsPerThread; i++) {
                        final int x = origin.getX() + (threadOffset * 4) + (i % 16);
                        final int z = origin.getZ() + (i * 7) % 32;
                        getHeightClamp.invoke(piece, fakeWorld, radius, x, z);
                    }
                    return null;
                });
            }

            final List<Future<Object>> results = pool.invokeAll(tasks, 30, TimeUnit.SECONDS);

            final List<String> failures = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                final Future<Object> result = results.get(i);
                if (result.isCancelled()) {
                    failures.add("worker " + i + " did not complete within the timeout - this is exactly the "
                            + "symptom from #594: a corrupted HashMap leaves a thread spinning forever inside "
                            + "HashMap$TreeNode.find");
                    continue;
                }
                try {
                    result.get();
                } catch (ExecutionException e) {
                    final Throwable cause = e.getCause() instanceof InvocationTargetException ite
                            ? ite.getCause()
                            : e.getCause();
                    failures.add("worker " + i + " threw " + cause);
                }
            }

            if (!failures.isEmpty()) {
                throw helper.assertionException(Component.literal(
                        "Concurrent LakePiece.getHeightClamp() calls failed:\n - " + String.join("\n - ", failures)
                ));
            }
        } finally {
            pool.shutdownNow();
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                throw helper.assertionException(Component.literal("worker pool did not shut down cleanly"));
            }
        }

        helper.succeed();
    }

    private static WorldGenLevel fakeWorldGenLevel(Holder<Biome> biome) {
        final InvocationHandler handler = (proxy, method, args) -> switch (method.getName()) {
            case "getBiome" -> biome;
            case "equals" -> proxy == (args.length > 0 ? args[0] : null);
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" -> "FakeWorldGenLevel";
            default -> throw new UnsupportedOperationException(
                    "WorldGenLevel." + method.getName() + "() not stubbed for this test"
            );
        };
        return (WorldGenLevel) Proxy.newProxyInstance(
                LakePieceConcurrentHeightCacheGameTest.class.getClassLoader(),
                new Class<?>[]{WorldGenLevel.class},
                handler
        );
    }
}
