package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.EndBlockProperties;
import org.betterx.betterend.blocks.SulphurCrystalBlock;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.util.GlobalState;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

import com.google.common.collect.Sets;

import java.util.Set;

public class SulphuricLakeFeature extends DefaultFeature {
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(15152);

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        BlockPos blockPos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        blockPos = getPosOnSurfaceWG(world, blockPos);

        if (blockPos.getY() < 57) {
            return false;
        }

        final RandomSource random = featureConfig.random();
        final MutableBlockPos POS = GlobalState.stateForThread().POS;
        double radius = MHelper.randRange(10.0, 20.0, random);
        int dist2 = MHelper.floor(radius * 1.5);

        // dist2 reaches up to 30 blocks, past the 3x3 chunks a feature may touch. Clip the scan to the write
        // zone - behaviour-neutral (writes out there were already dropped by WorldGenRegion) and it removes
        // the "Detected unsafe terrain read during worldgen" spam this loop produced. See WriteZone.
        final WriteZone zone = WriteZone.of(world);
        int minX = zone.clampX(blockPos.getX() - dist2);
        int maxX = zone.clampX(blockPos.getX() + dist2);
        int minZ = zone.clampZ(blockPos.getZ() - dist2);
        int maxZ = zone.clampZ(blockPos.getZ() + dist2);

        Set<BlockPos> brimstone = Sets.newHashSet();
        for (int x = minX; x <= maxX; x++) {
            POS.setX(x);
            int x2 = x - blockPos.getX();
            x2 *= x2;
            for (int z = minZ; z <= maxZ; z++) {
                POS.setZ(z);
                int z2 = z - blockPos.getZ();
                z2 *= z2;
                double r = radius * (NOISE.eval(x * 0.2, z * 0.2) * 0.25 + 0.75);
                double r2 = r * 1.5;
                r *= r;
                r2 *= r2;
                int dist = x2 + z2;
                if (dist <= r) {
                    POS.setY(getYOnSurface(world, x, z) - 1);
                    if (world.getBlockState(POS).is(CommonBlockTags.END_STONES)) {
                        if (isBorder(world, zone, POS)) {
                            if (random.nextInt(8) > 0) {
                                brimstone.add(POS.immutable());
                                if (random.nextBoolean()) {
                                    brimstone.add(POS.below());
                                    if (random.nextBoolean()) {
                                        brimstone.add(POS.below(2));
                                    }
                                }
                            } else {
                                if (!isAbsoluteBorder(world, zone, POS)) {
                                    BlocksHelper.setWithoutUpdate(world, POS, Blocks.WATER);
                                    //world.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 2);
                                    world.scheduleTick(POS, Fluids.WATER, 0);
                                    brimstone.add(POS.below());
                                    if (random.nextBoolean()) {
                                        brimstone.add(POS.below(2));
                                        if (random.nextBoolean()) {
                                            brimstone.add(POS.below(3));
                                        }
                                    }
                                } else {
                                    brimstone.add(POS.immutable());
                                    if (random.nextBoolean()) {
                                        brimstone.add(POS.below());
                                    }
                                }
                            }
                        } else {
                            BlocksHelper.setWithoutUpdate(world, POS, Blocks.WATER);
                            brimstone.remove(POS);
                            for (Direction dir : BlocksHelper.HORIZONTAL) {
                                // A plain POS.relative(dir) peek can step 1 block past the main loop's
                                // already-clamped x/z at the very edge columns - clamp it too.
                                BlockPos offseted = new BlockPos(
                                        zone.clampX(POS.getX() + dir.getStepX()),
                                        POS.getY(),
                                        zone.clampZ(POS.getZ() + dir.getStepZ())
                                );
                                if (world.getBlockState(offseted).is(CommonBlockTags.END_STONES)) {
                                    brimstone.add(offseted);
                                }
                            }
                            if (isDeepWater(world, zone, POS)) {
                                BlocksHelper.setWithoutUpdate(world, POS.move(Direction.DOWN), Blocks.WATER);
                                brimstone.remove(POS);
                                for (Direction dir : BlocksHelper.HORIZONTAL) {
                                    // Same one-block overshoot as the peek above, and the same clamp. POS was
                                    // moved down by the setWithoutUpdate on the line above, so getY() is
                                    // already the lowered Y this peek is meant to read at.
                                    BlockPos offseted = new BlockPos(
                                            zone.clampX(POS.getX() + dir.getStepX()),
                                            POS.getY(),
                                            zone.clampZ(POS.getZ() + dir.getStepZ())
                                    );
                                    if (world.getBlockState(offseted).is(CommonBlockTags.END_STONES)) {
                                        brimstone.add(offseted);
                                    }
                                }
                            }
                            brimstone.add(POS.below());
                            if (random.nextBoolean()) {
                                brimstone.add(POS.below(2));
                                if (random.nextBoolean()) {
                                    brimstone.add(POS.below(3));
                                }
                            }
                        }
                    }
                } else if (dist < r2) {
                    POS.setY(getYOnSurface(world, x, z) - 1);
                    if (world.getBlockState(POS).is(CommonBlockTags.END_STONES)) {
                        brimstone.add(POS.immutable());
                        if (random.nextBoolean()) {
                            brimstone.add(POS.below());
                            if (random.nextBoolean()) {
                                brimstone.add(POS.below(2));
                            }
                        }
                    }
                }
            }
        }

        brimstone.forEach((bpos) -> {
            placeBrimstone(world, zone, bpos, random);
        });

        return true;
    }

    /** Clamps only the horizontal step of a relative(dir) peek - dir may include UP/DOWN, which never
     *  needs clamping since the write zone only bounds x/z. */
    private static BlockPos clampedRelative(WriteZone zone, BlockPos pos, Direction dir) {
        return new BlockPos(
                zone.clampX(pos.getX() + dir.getStepX()),
                pos.getY() + dir.getStepY(),
                zone.clampZ(pos.getZ() + dir.getStepZ())
        );
    }

    // Each helper below peeks up to 3 blocks past the (already write-zone-clamped) column the main
    // loop is currently at. Clamping the peek position too - not just the loop bounds - keeps these
    // reads inside the zone even for columns sitting right at its edge. Behaviour-neutral in the same
    // sense as the main loop's clamp: a peek that would have landed outside the zone reads that
    // boundary column instead, which only affects the outermost couple of columns of the lake.
    private boolean isBorder(WorldGenLevel world, WriteZone zone, BlockPos pos) {
        int y = pos.getY() + 1;
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            int x = zone.clampX(pos.getX() + dir.getStepX());
            int z = zone.clampZ(pos.getZ() + dir.getStepZ());
            if (getYOnSurface(world, x, z) < y) {
                return true;
            }
        }
        return false;
    }

    private boolean isAbsoluteBorder(WorldGenLevel world, WriteZone zone, BlockPos pos) {
        int y = pos.getY() - 2;
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            int x = zone.clampX(pos.getX() + dir.getStepX() * 3);
            int z = zone.clampZ(pos.getZ() + dir.getStepZ() * 3);
            if (getYOnSurface(world, x, z) < y) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeepWater(WorldGenLevel world, WriteZone zone, BlockPos pos) {
        int y = pos.getY() + 1;
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            int x1 = zone.clampX(pos.getX() + dir.getStepX());
            int z1 = zone.clampZ(pos.getZ() + dir.getStepZ());
            int x2 = zone.clampX(pos.getX() + dir.getStepX() * 2);
            int z2 = zone.clampZ(pos.getZ() + dir.getStepZ() * 2);
            int x3 = zone.clampX(pos.getX() + dir.getStepX() * 3);
            int z3 = zone.clampZ(pos.getZ() + dir.getStepZ() * 3);
            if (getYOnSurface(world, x1, z1) < y
                    || getYOnSurface(world, x2, z2) < y
                    || getYOnSurface(world, x3, z3) < y) {
                return false;
            }
        }
        return true;
    }

    private void placeBrimstone(WorldGenLevel world, WriteZone zone, BlockPos pos, RandomSource random) {
        BlockState state = getBrimstone(world, zone, pos);
        BlocksHelper.setWithoutUpdate(world, pos, state);
        if (state.getValue(EndBlockProperties.ACTIVE)) {
            makeShards(world, zone, pos, random);
        }
    }

    private BlockState getBrimstone(WorldGenLevel world, WriteZone zone, BlockPos pos) {
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            if (world.getBlockState(clampedRelative(zone, pos, dir)).is(Blocks.WATER)) {
                return EndStoneBlocks.BRIMSTONE.defaultBlockState().setValue(EndBlockProperties.ACTIVE, true);
            }
        }
        return EndStoneBlocks.BRIMSTONE.defaultBlockState();
    }

    private void makeShards(WorldGenLevel world, WriteZone zone, BlockPos pos, RandomSource random) {
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            BlockPos side;
            if (random.nextInt(16) == 0 && world.getBlockState((side = clampedRelative(zone, pos, dir))).is(Blocks.WATER)) {
                BlockState state = EndStoneBlocks.SULPHUR_CRYSTAL.defaultBlockState()
                                                            .setValue(SulphurCrystalBlock.WATERLOGGED, true)
                                                            .setValue(SulphurCrystalBlock.FACING, dir)
                                                            .setValue(SulphurCrystalBlock.AGE, random.nextInt(3));
                BlocksHelper.setWithoutUpdate(world, side, state);
            }
        }
    }
}
