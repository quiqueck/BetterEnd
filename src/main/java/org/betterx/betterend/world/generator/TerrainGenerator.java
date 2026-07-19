package org.betterx.betterend.world.generator;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.interfaces.BETargetChecker;
import org.betterx.betterend.mixin.common.NoiseBasedChunkGeneratorAccessor;
import org.betterx.betterend.mixin.common.NoiseChunkAccessor;
import org.betterx.betterend.mixin.common.NoiseInterpolatorAccessor;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.wover.biome.api.BiomeManager;
import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.Nullable;

public class TerrainGenerator {
    private static final Map<Point, TerrainBoolCache> TERRAIN_BOOL_CACHE_MAP = Maps.newHashMap();
    private static final ReentrantLock LOCKER = new ReentrantLock();
    private static final Point POS = new Point();
    private static final double SCALE_XZ = 8.0;
    private static final double SCALE_Y = 4.0;
    private static final float[] COEF;
    private static final Point[] OFFS;

    private static IslandLayer largeIslands;
    private static IslandLayer mediumIslands;
    private static IslandLayer smallIslands;
    private static OpenSimplexNoise noise1;
    private static OpenSimplexNoise noise2;
    private static BiomeSource biomeSource;
    public static WoverEndConfig config;
    private static Sampler sampler;

    public static void initNoise(long seed, BiomeSource biomeSource, Sampler sampler) {
        if (biomeSource instanceof BiomeSourceWithConfig bcl) {
            if (bcl.getBiomeSourceConfig() instanceof WoverEndConfig config)
                TerrainGenerator.config = config;
        }

        if (config == null) {
            throw new IllegalStateException("Biome source config is not set");
        }

        RandomSource random = new LegacyRandomSource(seed);
        largeIslands = new IslandLayer(random.nextInt(), GeneratorOptions.bigOptions);
        mediumIslands = new IslandLayer(random.nextInt(), GeneratorOptions.mediumOptions);
        smallIslands = new IslandLayer(random.nextInt(), GeneratorOptions.smallOptions);
        noise1 = new OpenSimplexNoise(random.nextInt());
        noise2 = new OpenSimplexNoise(random.nextInt());
        TERRAIN_BOOL_CACHE_MAP.clear();
        TerrainGenerator.biomeSource = biomeSource;
        TerrainGenerator.sampler = sampler;

    }

    public static void fillTerrainDensity(double[] buffer, int posX, int posZ, int scaleXZ, int scaleY, int maxHeight) {
        // try/finally so a RuntimeException from the biome source (getAverageDepth -> getNoiseBiome) or the
        // island caches can never orphan the static LOCKER and deadlock every worker + the server thread.
        LOCKER.lock();
        try {
        final float fadeOutDist = 27.0f;
        final float fadOutStart = maxHeight - (fadeOutDist + 1);
        largeIslands.clearCache();
        mediumIslands.clearCache();
        smallIslands.clearCache();

        int x = posX / scaleXZ;
        int z = posZ / scaleXZ;
        double distortion1 = noise1.eval(x * 0.1, z * 0.1) * 20 + noise2.eval(
                x * 0.2,
                z * 0.2
        ) * 10 + noise1.eval(x * 0.4, z * 0.4) * 5;
        double distortion2 = noise2.eval(x * 0.1, z * 0.1) * 20 + noise1.eval(
                x * 0.2,
                z * 0.2
        ) * 10 + noise2.eval(x * 0.4, z * 0.4) * 5;
        double px = (double) x * scaleXZ + distortion1;
        double pz = (double) z * scaleXZ + distortion2;

        largeIslands.updatePositions(px, pz, maxHeight);
        mediumIslands.updatePositions(px, pz, maxHeight);
        smallIslands.updatePositions(px, pz, maxHeight);

        float height = getAverageDepth(x << 1, z << 1) * 0.5F;

        for (int y = 0; y < buffer.length; y++) {
            double py = (double) y * scaleY;
            float dist = largeIslands.getDensity(px, py, pz, height);
            dist = dist > 1 ? dist : MHelper.max(dist, mediumIslands.getDensity(px, py, pz, height));
            dist = dist > 1 ? dist : MHelper.max(dist, smallIslands.getDensity(px, py, pz, height));
            if (dist > -0.5F) {
                dist += (float) (noise1.eval(px * 0.01, py * 0.01, pz * 0.01) * 0.02 + 0.02);
                dist += (float) (noise2.eval(px * 0.05, py * 0.05, pz * 0.05) * 0.01 + 0.01);
                dist += (float) (noise1.eval(px * 0.1, py * 0.1, pz * 0.1) * 0.005 + 0.005);
            }

            if (py >= maxHeight) dist = -1;
            else if (py > fadOutStart) {
                dist = (float) Mth.lerp((py - fadOutStart) / fadeOutDist, dist, -1);
            }
            buffer[y] = dist;
        }
        } finally {
            LOCKER.unlock();
        }
    }

    private static float getAverageDepth(int x, int z) {
        if (biomeSource == null) {
            return 0;
        }
        WoverBiomeData biome = getBiomeData(biomeSource, x, z);
        if (biome != null && biome.terrainHeight < 0.1F) {
            return 0F;
        }
        float depth = 0F;
        for (int i = 0; i < OFFS.length; i++) {
            int px = x + OFFS[i].x;
            int pz = z + OFFS[i].y;
            biome = getBiomeData(biomeSource, px, pz);
            depth += biome == null ? 0 : (biome.terrainHeight * COEF[i]);
        }
        return depth;
    }

    private static @Nullable WoverBiomeData getBiomeData(BiomeSource biomeSource, int x, int z) {
        if (BiomeManager.biomeDataForHolder(biomeSource.getNoiseBiome(x, 0, z, sampler)) instanceof WoverBiomeData biome) {
            return biome;
        }
        return null;
    }

    static {
        float sum = 0;
        List<Float> coef = Lists.newArrayList();
        List<Point> pos = Lists.newArrayList();
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                float dist = MHelper.length(x, z) / 3F;
                if (dist <= 1) {
                    sum += dist;
                    coef.add(dist);
                    pos.add(new Point(x, z));
                }
            }
        }
        OFFS = pos.toArray(new Point[]{});
        COEF = new float[coef.size()];
        for (int i = 0; i < COEF.length; i++) {
            COEF[i] = coef.get(i) / sum;
        }
    }

    public static Boolean isLand(int x, int z, int maxHeight) {
        int sectionX = TerrainBoolCache.scaleCoordinate(x);
        int sectionZ = TerrainBoolCache.scaleCoordinate(z);
        final int stepY = (int) Math.ceil(maxHeight / SCALE_Y);
        LOCKER.lock();
        // try/finally so an exception below can never orphan the static LOCKER (see fillTerrainDensity).
        try {
        POS.setLocation(sectionX, sectionZ);

        TerrainBoolCache section = TERRAIN_BOOL_CACHE_MAP.get(POS);
        if (section == null) {
            if (TERRAIN_BOOL_CACHE_MAP.size() > 64) {
                TERRAIN_BOOL_CACHE_MAP.clear();
            }
            section = new TerrainBoolCache();
            TERRAIN_BOOL_CACHE_MAP.put(new Point(POS.x, POS.y), section);
        }
        byte value = section.getData(x, z);
        if (value > 0) {
            return value > 1;
        }

        double px = (x >> 1) + 0.5;
        double pz = (z >> 1) + 0.5;

        double distortion1 = noise1.eval(px * 0.1, pz * 0.1) * 20 + noise2.eval(px * 0.2, pz * 0.2) * 10 + noise1.eval(
                px * 0.4,
                pz * 0.4
        ) * 5;
        double distortion2 = noise2.eval(px * 0.1, pz * 0.1) * 20 + noise1.eval(px * 0.2, pz * 0.2) * 10 + noise2.eval(
                px * 0.4,
                pz * 0.4
        ) * 5;
        px = px * SCALE_XZ + distortion1;
        pz = pz * SCALE_XZ + distortion2;

        largeIslands.updatePositions(px, pz, maxHeight);
        mediumIslands.updatePositions(px, pz, maxHeight);
        smallIslands.updatePositions(px, pz, maxHeight);

        boolean result = false;
        for (int y = 0; y < stepY; y++) {
            double py = (double) y * SCALE_Y;
            float dist = largeIslands.getDensity(px, py, pz);
            dist = dist > 1 ? dist : MHelper.max(dist, mediumIslands.getDensity(px, py, pz));
            dist = dist > 1 ? dist : MHelper.max(dist, smallIslands.getDensity(px, py, pz));
            if (dist > -0.5F) {
                dist += (float) (noise1.eval(px * 0.01, py * 0.01, pz * 0.01) * 0.02 + 0.02);
                dist += (float) (noise2.eval(px * 0.05, py * 0.05, pz * 0.05) * 0.01 + 0.01);
                dist += (float) (noise1.eval(px * 0.1, py * 0.1, pz * 0.1) * 0.005 + 0.005);
            }
            if (dist > -0.01) {
                result = true;
                break;
            }
        }

        section.setData(x, z, (byte) (result ? 2 : 1));

        return result;
        } finally {
            LOCKER.unlock();
        }
    }

    public static void onServerLevelInit(ServerLevel level, LevelStem levelStem, long seed) {
        if (level.dimension() == Level.END) {
            final ChunkGenerator chunkGenerator = levelStem.generator();
            if (chunkGenerator instanceof NoiseBasedChunkGenerator) {
                Holder<NoiseGeneratorSettings> sHolder = ((NoiseBasedChunkGeneratorAccessor) chunkGenerator)
                        .be_getSettings();
                if (chunkGenerator.getBiomeSource() instanceof WoverEndBiomeSource bcl) {
                    BETargetChecker.class
                            .cast(sHolder.value())
                            .be_setTarget(bcl.getBiomeSourceConfig().generatorVersion == WoverEndConfig.EndBiomeGeneratorType.PAULEVS);
                } else {
                    BETargetChecker.class
                            .cast(sHolder.value())
                            .be_setTarget(false);
                }

            }
            initNoise(
                    seed,
                    chunkGenerator.getBiomeSource(),
                    level.getChunkSource().randomState().sampler()
            );
        }
    }

    public static void makeObsidianPlatform(ServerLevelAccessor serverLevel, CallbackInfo info) {
        if (!GeneratorOptions.generateObsidianPlatform()) {
            info.cancel();
        } else if (GeneratorOptions.changeSpawn()) {
            BlockPos blockPos = GeneratorOptions.getSpawn();
            int i = blockPos.getX();
            int j = blockPos.getY() - 2;
            int k = blockPos.getZ();

            BlockPos
                    .betweenClosed(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2)
                    .forEach((blockPosx) -> serverLevel.setBlock(blockPosx, Blocks.AIR.defaultBlockState(), BlockHelper.SET_OBSERV));

            BlockPos
                    .betweenClosed(i - 2, j, k - 2, i + 2, j, k + 2)
                    .forEach((blockPosx) -> serverLevel.setBlock(blockPosx, Blocks.OBSIDIAN.defaultBlockState(), BlockHelper.SET_OBSERV));
            info.cancel();
        }
    }

    public static void fillSlice(
            boolean primarySlice,
            int x,
            List<NoiseChunk.NoiseInterpolator> interpolators,
            NoiseChunkAccessor accessor,
            NoiseSettings noiseSettings
    ) {
        final int sizeY = noiseSettings.getCellHeight();
        final int sizeXZ = noiseSettings.getCellWidth();
        final int cellSizeXZ = accessor.bnv_getCellCountXZ() + 1;
        final int firstCellZ = accessor.bnv_getFirstCellZ();

        x *= sizeXZ;
        for (int cellXZ = 0; cellXZ < cellSizeXZ; ++cellXZ) {
            int z = (firstCellZ + cellXZ) * sizeXZ;
            for (NoiseChunk.NoiseInterpolator noiseInterpolator : interpolators) {
                if (noiseInterpolator instanceof NoiseInterpolatorAccessor interpolator) {
                    final double[] ds = (primarySlice
                            ? interpolator.be_getSlice0()
                            : interpolator.be_getSlice1())[cellXZ];
                    fillTerrainDensity(ds, x, z, sizeXZ, sizeY, noiseSettings.height());
                }
            }
        }
    }
}
