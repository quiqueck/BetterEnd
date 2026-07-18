package org.betterx.betterend.world.features.terrain;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.util.BlockFixer;
import org.betterx.betterend.util.GlobalState;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.FluidState;


public class EndLakeFeature extends DefaultFeature {
    private static final BlockState END_STONE = Blocks.END_STONE.defaultBlockState();
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(15152);

    public EndLakeFeature() {

    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final MutableBlockPos POS = GlobalState.stateForThread().POS;
        final RandomSource random = featureConfig.random();
        BlockPos blockPos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        double radius = MHelper.randRange(10.0, 20.0, random);
        double depth = radius * 0.5 * MHelper.randRange(0.8, 1.2, random);
        int dist = MHelper.floor(radius);
        int dist2 = MHelper.floor(radius * 1.5);
        int bott = MHelper.floor(depth);
        blockPos = getPosOnSurfaceWG(world, blockPos);

        if (blockPos.getY() < 10) return false;

        int waterLevel = blockPos.getY();

        BlockPos pos = getPosOnSurfaceRaycast(world, blockPos.north(dist).above(10), 20);
        if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
        waterLevel = MHelper.min(pos.getY(), waterLevel);

        pos = getPosOnSurfaceRaycast(world, blockPos.south(dist).above(10), 20);
        if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
        waterLevel = MHelper.min(pos.getY(), waterLevel);

        pos = getPosOnSurfaceRaycast(world, blockPos.east(dist).above(10), 20);
        if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
        waterLevel = MHelper.min(pos.getY(), waterLevel);

        pos = getPosOnSurfaceRaycast(world, blockPos.west(dist).above(10), 20);
        if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
        waterLevel = MHelper.min(pos.getY(), waterLevel);
        BlockState state;

        // Sample the real biome surface block once, before any carving disturbs it, and use it for
        // the whole shore. Reflects noise-driven surfaces and never crashes; see EndBiome.
        final BlockState borderMaterial = EndBiome.sampleTopMaterial(world, blockPos.below());

        int minX = blockPos.getX() - dist2;
        int maxX = blockPos.getX() + dist2;
        int minZ = blockPos.getZ() - dist2;
        int maxZ = blockPos.getZ() + dist2;
        int maskMinX = minX - 1;
        int maskMinZ = minZ - 1;

        boolean[][] mask = new boolean[maxX - minX + 3][maxZ - minZ + 3];
        for (int x = minX; x <= maxX; x++) {
            POS.setX(x);
            int mx = x - maskMinX;
            for (int z = minZ; z <= maxZ; z++) {
                POS.setZ(z);
                int mz = z - maskMinZ;
                if (!mask[mx][mz]) {
                    for (int y = waterLevel + 1; y <= waterLevel + 20; y++) {
                        POS.setY(y);
                        FluidState fluid = world.getFluidState(POS);
                        if (!fluid.isEmpty()) {
                            for (int i = -1; i < 2; i++) {
                                int px = mx + i;
                                for (int j = -1; j < 2; j++) {
                                    int pz = mz + j;
                                    mask[px][pz] = true;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }

        for (int x = minX; x <= maxX; x++) {
            POS.setX(x);
            int x2 = x - blockPos.getX();
            x2 *= x2;
            int mx = x - maskMinX;
            for (int z = minZ; z <= maxZ; z++) {
                POS.setZ(z);
                int z2 = z - blockPos.getZ();
                z2 *= z2;
                int mz = z - maskMinZ;
                if (!mask[mx][mz]) {
                    double size = 1;
                    for (int y = blockPos.getY(); y <= blockPos.getY() + 20; y++) {
                        POS.setY(y);
                        double add = y - blockPos.getY();
                        if (add > 5) {
                            size *= 0.8;
                            add = 5;
                        }
                        double r = (add * 1.8 + radius * (NOISE.eval(
                                x * 0.2,
                                y * 0.2,
                                z * 0.2
                        ) * 0.25 + 0.75)) - 1.0 / size;
                        if (r > 0) {
                            r *= r;
                            if (x2 + z2 <= r) {
                                state = world.getBlockState(POS);
                                if (state.is(CommonBlockTags.END_STONES)) {
                                    BlocksHelper.setWithoutUpdate(world, POS, AIR);
                                }
                                pos = POS.below();
                                if (world.getBlockState(pos).is(CommonBlockTags.END_STONES)) {
                                    state = borderMaterial;
                                    if (y > waterLevel + 1) BlocksHelper.setWithoutUpdate(world, pos, state);
                                    else if (y > waterLevel)
                                        BlocksHelper.setWithoutUpdate(
                                                world,
                                                pos,
                                                random.nextBoolean()
                                                        ? state
                                                        : EndBlocks.ENDSTONE_DUST.defaultBlockState()
                                        );
                                    else
                                        BlocksHelper.setWithoutUpdate(
                                                world,
                                                pos,
                                                EndBlocks.ENDSTONE_DUST.defaultBlockState()
                                        );
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        double aspect = (radius / depth);

        for (int x = blockPos.getX() - dist; x <= blockPos.getX() + dist; x++) {
            POS.setX(x);
            int x2 = x - blockPos.getX();
            x2 *= x2;
            int mx = x - maskMinX;
            for (int z = blockPos.getZ() - dist; z <= blockPos.getZ() + dist; z++) {
                POS.setZ(z);
                int z2 = z - blockPos.getZ();
                z2 *= z2;
                int mz = z - maskMinZ;
                if (!mask[mx][mz]) {
                    for (int y = blockPos.getY() - bott; y < blockPos.getY(); y++) {
                        POS.setY(y);
                        double y2 = (double) (y - blockPos.getY()) * aspect;
                        y2 *= y2;
                        double r = radius * (NOISE.eval(x * 0.2, y * 0.2, z * 0.2) * 0.25 + 0.75);
                        double rb = r * 1.2;
                        r *= r;
                        rb *= rb;
                        if (y2 + x2 + z2 <= r) {
                            state = world.getBlockState(POS);
                            if (canReplace(state)) {
                                state = world.getBlockState(POS.above());
                                state = canReplace(state) ? (y < waterLevel ? WATER : AIR) : state;
                                BlocksHelper.setWithoutUpdate(world, POS, state);
                            }
                            pos = POS.below();
                            if (world.getBlockState(pos).is(CommonBlockTags.END_STONES)) {
                                BlocksHelper.setWithoutUpdate(world, pos, EndBlocks.ENDSTONE_DUST.defaultBlockState());
                            }
                            pos = POS.above();
                            while (canReplace(state = world.getBlockState(pos)) && !state.isAir() && state
                                    .getFluidState()
                                    .isEmpty()) {
                                BlocksHelper.setWithoutUpdate(world, pos, pos.getY() < waterLevel ? WATER : AIR);
                                pos = pos.above();
                            }
                        }
                        // Make border
                        else if (y < waterLevel && y2 + x2 + z2 <= rb) {
                            if (world.isEmptyBlock(POS.above())) {
                                state = borderMaterial;
                                BlocksHelper.setWithoutUpdate(
                                        world,
                                        POS,
                                        random.nextBoolean() ? state : EndBlocks.ENDSTONE_DUST.defaultBlockState()
                                );
                                BlocksHelper.setWithoutUpdate(world, POS.below(), END_STONE);
                            } else {
                                BlocksHelper.setWithoutUpdate(world, POS, EndBlocks.ENDSTONE_DUST.defaultBlockState());
                                BlocksHelper.setWithoutUpdate(world, POS.below(), END_STONE);
                            }
                        }
                    }
                }
            }
        }

        BlockFixer.fixBlocks(
                world,
                new BlockPos(minX - 2, waterLevel - 2, minZ - 2),
                new BlockPos(maxX + 2, blockPos.getY() + 20, maxZ + 2)
        );

        return true;
    }

    private boolean canReplace(BlockState state) {
        return state.is(CommonBlockTags.END_STONES)
                || state.is(EndBlocks.ENDSTONE_DUST)
                || BlocksHelper.replaceableOrPlant(state)
                || state.is(CommonBlockTags.WATER_PLANT);
    }
}
