package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.betterend.registry.block.EndWallPlantBlocks;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.*;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.sdf.primitive.SDFFlatland;
import org.betterx.bclib.sdf.primitive.SDFPrimitive;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.HydrothermalVentBlock;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.features.EndConfiguredLakeFeature;
import org.betterx.betterend.util.BlockFixer;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.function.Function;

public class GeyserFeature extends DefaultFeature {
    protected static final Function<BlockState, Boolean> REPLACE1;
    protected static final Function<BlockState, Boolean> REPLACE2;
    private static final Function<BlockState, Boolean> IGNORE;
    private static final Direction[] HORIZONTAL = BlocksHelper.makeHorizontal();

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final WorldGenLevel world = featureConfig.level();
        final BlockPos pos = getPosOnSurfaceWG(world, featureConfig.origin());
        final ChunkGenerator chunkGenerator = featureConfig.chunkGenerator();
        // The SDF sculpting below (cones/bowls/caves up to radius1 ~= halfHeight * 0.5, halfHeight up to 20)
        // and the closing BlockFixer box are otherwise bounded only by their own shapes/radii, not by the
        // chunks a feature may touch. Clipping both to the write zone is behaviour-neutral (writes out there
        // were already dropped by WorldGenRegion) and removes reads from chunks that have not been carved -
        // or even filled - yet. See WriteZone.
        final WriteZone zone = WriteZone.of(world);

        if (pos.getY() < 10) {
            return false;
        }

        MutableBlockPos bpos = new MutableBlockPos().set(pos);
        bpos.setY(bpos.getY() - 1);
        BlockState state = world.getBlockState(bpos);
        while (state.is(CommonBlockTags.END_STONES) || !state.getFluidState().isEmpty() && bpos.getY() > 5) {
            bpos.setY(bpos.getY() - 1);
            state = world.getBlockState(bpos);
        }

        if (pos.getY() - bpos.getY() < 25) {
            return false;
        }

        int halfHeight = MHelper.randRange(10, 20, random);
        float radius1 = halfHeight * 0.5F;
        float radius2 = halfHeight * 0.1F + 0.5F;
        SDF sdf = new SDFCappedCone().setHeight(halfHeight)
                                     .setRadius1(radius1)
                                     .setRadius2(radius2)
                                     .setBlock(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));
        sdf = new SDFTranslate().setTranslate(0, halfHeight - 3, 0).setSource(sdf);

        int count = halfHeight;
        for (int i = 0; i < count; i++) {
            int py = i << 1;
            float delta = (float) i / (float) (count - 1);
            float radius = Mth.lerp(delta, radius1, radius2) * 1.3F;

            SDF bowl = new SDFCappedCone().setHeight(radius)
                                          .setRadius1(0)
                                          .setRadius2(radius)
                                          .setBlock(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));

            SDF brimstone = new SDFCappedCone().setHeight(radius)
                                               .setRadius1(0)
                                               .setRadius2(radius)
                                               .setBlock(EndStoneBlocks.BRIMSTONE);
            brimstone = new SDFTranslate().setTranslate(0, 2F, 0).setSource(brimstone);
            bowl = new SDFSubtraction().setSourceA(bowl).setSourceB(brimstone);
            bowl = new SDFUnion().setSourceA(brimstone).setSourceB(bowl);

            SDF water = new SDFCappedCone().setHeight(radius).setRadius1(0).setRadius2(radius).setBlock(Blocks.WATER);
            water = new SDFTranslate().setTranslate(0, 4, 0).setSource(water);
            bowl = new SDFSubtraction().setSourceA(bowl).setSourceB(water);
            bowl = new SDFUnion().setSourceA(water).setSourceB(bowl);

            final OpenSimplexNoise noise1 = new OpenSimplexNoise(random.nextLong());
            final OpenSimplexNoise noise2 = new OpenSimplexNoise(random.nextLong());

            bowl = new SDFCoordModify().setFunction((vec) -> {
                float dx = (float) noise1.eval(vec.x() * 0.1, vec.y() * 0.1, vec.z() * 0.1);
                float dz = (float) noise2.eval(vec.x() * 0.1, vec.y() * 0.1, vec.z() * 0.1);
                vec.set(vec.x() + dx, vec.y(), vec.z() + dz);
            }).setSource(bowl);

            SDF cut = new SDFFlatland().setBlock(Blocks.AIR);
            cut = new SDFInvert().setSource(cut);
            cut = new SDFTranslate().setTranslate(0, radius - 2, 0).setSource(cut);
            bowl = new SDFSubtraction().setSourceA(bowl).setSourceB(cut);

            bowl = new SDFTranslate().setTranslate(radius, py - radius, 0).setSource(bowl);
            bowl = new SDFRotation().setRotation(Axis.YP, i * 4F).setSource(bowl);
            sdf = new SDFUnion().setSourceA(sdf).setSourceB(bowl);
        }
        sdf.setReplaceFunction(REPLACE2).fillRecursive(world, pos, zone.toBoundingBox());

        radius2 = radius2 * 0.5F;
        if (radius2 < 0.7F) {
            radius2 = 0.7F;
        }
        final OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());

        SDFPrimitive obj1;
        SDFPrimitive obj2;

        obj1 = new SDFCappedCone().setHeight(halfHeight + 5).setRadius1(radius1 * 0.5F).setRadius2(radius2);
        sdf = new SDFTranslate().setTranslate(0, halfHeight - 13, 0).setSource(obj1);
        sdf = new SDFDisplacement().setFunction((vec) -> (float) noise.eval(
                vec.x() * 0.3F,
                vec.y() * 0.3F,
                vec.z() * 0.3F
        ) * 0.5F).setSource(sdf);

        obj2 = new SDFSphere().setRadius(radius1);
        SDF cave = new SDFScale3D().setScale(1.5F, 1, 1.5F).setSource(obj2);
        cave = new SDFDisplacement().setFunction((vec) -> (float) noise.eval(
                vec.x() * 0.1F,
                vec.y() * 0.1F,
                vec.z() * 0.1F
        ) * 2F).setSource(cave);
        cave = new SDFTranslate().setTranslate(0, -halfHeight - 10, 0).setSource(cave);

        sdf = new SDFSmoothUnion().setRadius(5).setSourceA(cave).setSourceB(sdf);

        obj1.setBlock(WATER);
        obj2.setBlock(WATER);
        sdf.setReplaceFunction(REPLACE2);
        sdf.fillRecursive(world, pos, zone.toBoundingBox());

        obj1.setBlock(EndStoneBlocks.BRIMSTONE);
        obj2.setBlock(EndStoneBlocks.BRIMSTONE);
        new SDFDisplacement().setFunction((vec) -> -2F)
                             .setSource(sdf)
                             .setReplaceFunction(REPLACE1)
                             .fillRecursiveIgnore(world, pos, zone.toBoundingBox(), IGNORE);

        obj1.setBlock(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));
        obj2.setBlock(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));
        new SDFDisplacement().setFunction((vec) -> -4F)
                             .setSource(cave)
                             .setReplaceFunction(REPLACE1)
                             .fillRecursiveIgnore(world, pos, zone.toBoundingBox(), IGNORE);

        obj1.setBlock(Blocks.END_STONE);
        obj2.setBlock(Blocks.END_STONE);
        new SDFDisplacement().setFunction((vec) -> -6F)
                             .setSource(cave)
                             .setReplaceFunction(REPLACE1)
                             .fillRecursiveIgnore(world, pos, zone.toBoundingBox(), IGNORE);

        BlocksHelper.setWithoutUpdate(world, pos, WATER);
        MutableBlockPos mut = new MutableBlockPos().set(pos);
        count = getYOnSurface(world, pos.getX(), pos.getZ()) - pos.getY();
        for (int i = 0; i < count; i++) {
            BlocksHelper.setWithoutUpdate(world, mut, WATER);
            for (Direction dir : BlocksHelper.HORIZONTAL) {
                BlocksHelper.setWithoutUpdate(world, mut.relative(dir), WATER);
            }
            mut.setY(mut.getY() + 1);
        }

        // The two vent-cluster loops below jitter mut around pos with an unbounded gaussian offset
        // (rare tails reach well past the 3x3 chunks a feature may touch) and then read/write freely -
        // unlike the SDF sculpting above, nothing here was clamped to the write zone. distRaw/dist are
        // computed from the true (unclamped) offset so the cluster's own math stays unchanged; only the
        // actual world-touching position is clamped, same behaviour-neutral approach as the SDF calls.
        for (int i = 0; i < 150; i++) {
            int dx = MHelper.floor(random.nextGaussian() * 4 + 0.5);
            int dz = MHelper.floor(random.nextGaussian() * 4 + 0.5);
            float distRaw = MHelper.length(dx, dz);
            int dist = MHelper.floor(6 - distRaw) + random.nextInt(2);
            if (dist >= 0) {
                mut.set(zone.clampX(pos.getX() + dx), pos.getY() - halfHeight - 10, zone.clampZ(pos.getZ() + dz));
                state = world.getBlockState(mut);
                while (!state.getFluidState().isEmpty() || state.is(CommonBlockTags.WATER_PLANT)) {
                    mut.setY(mut.getY() - 1);
                    state = world.getBlockState(mut);
                }
                if (state.is(CommonBlockTags.END_STONES) && !world.getBlockState(mut.above())
                                                                  .is(EndStoneBlocks.HYDROTHERMAL_VENT)) {
                    for (int j = 0; j <= dist; j++) {
                        BlocksHelper.setWithoutUpdate(world, mut, EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));
                        MHelper.shuffle(HORIZONTAL, random);
                        for (Direction dir : HORIZONTAL) {
                            // mut's x/z is already zone-clamped, but a further 1-block relative(dir)
                            // peek can still step past the zone right at its edge - clamp this too.
                            BlockPos p = new BlockPos(
                                    zone.clampX(mut.getX() + dir.getStepX()),
                                    mut.getY(),
                                    zone.clampZ(mut.getZ() + dir.getStepZ())
                            );
                            if (random.nextBoolean() && world.getBlockState(p).is(Blocks.WATER)) {
                                BlocksHelper.setWithoutUpdate(
                                        world,
                                        p,
                                        EndWallPlantBlocks.TUBE_WORM.defaultBlockState()
                                                           .setValue(HorizontalDirectionalBlock.FACING, dir)
                                );
                            }
                        }
                        mut.setY(mut.getY() + 1);
                    }
                    state = EndStoneBlocks.HYDROTHERMAL_VENT.defaultBlockState()
                                                       .setValue(HydrothermalVentBlock.ACTIVATED, distRaw < 2);
                    BlocksHelper.setWithoutUpdate(world, mut, state);
                    mut.setY(mut.getY() + 1);
                    state = world.getBlockState(mut);
                    while (state.is(Blocks.WATER)) {
                        BlocksHelper.setWithoutUpdate(world, mut, EndStoneBlocks.VENT_BUBBLE_COLUMN.defaultBlockState());
                        mut.setY(mut.getY() + 1);
                        state = world.getBlockState(mut);
                    }
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            int dx = MHelper.floor(random.nextGaussian() * 0.7 + 0.5);
            int dz = MHelper.floor(random.nextGaussian() * 0.7 + 0.5);
            float distRaw = MHelper.length(dx, dz);
            int dist = MHelper.floor(6 - distRaw) + random.nextInt(2);
            if (dist >= 0) {
                mut.set(zone.clampX(pos.getX() + dx), pos.getY() - halfHeight - 10, zone.clampZ(pos.getZ() + dz));
                state = world.getBlockState(mut);
                while (state.is(Blocks.WATER)) {
                    mut.setY(mut.getY() - 1);
                    state = world.getBlockState(mut);
                }
                if (state.is(CommonBlockTags.END_STONES)) {
                    for (int j = 0; j <= dist; j++) {
                        BlocksHelper.setWithoutUpdate(world, mut, EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));
                        mut.setY(mut.getY() + 1);
                    }
                    state = EndStoneBlocks.HYDROTHERMAL_VENT.defaultBlockState()
                                                       .setValue(HydrothermalVentBlock.ACTIVATED, distRaw < 2);
                    BlocksHelper.setWithoutUpdate(world, mut, state);
                    mut.setY(mut.getY() + 1);
                    state = world.getBlockState(mut);
                    while (state.is(Blocks.WATER)) {
                        BlocksHelper.setWithoutUpdate(world, mut, EndStoneBlocks.VENT_BUBBLE_COLUMN.defaultBlockState());
                        mut.setY(mut.getY() + 1);
                        state = world.getBlockState(mut);
                    }
                }
            }
        }

        EndConfiguredLakeFeature.SULPHURIC_LAKE.placeInWorld(world, pos, random);

        double distance = radius1 * 1.7;
        BlockPos start = new BlockPos(
                zone.clampX(pos.getX() - (int) distance),
                pos.getY() + (int) (-halfHeight - 15 - distance),
                zone.clampZ(pos.getZ() - (int) distance)
        );
        BlockPos end = new BlockPos(
                zone.clampX(pos.getX() + (int) distance),
                pos.getY() + (int) (-halfHeight - 5 + distance),
                zone.clampZ(pos.getZ() + (int) distance)
        );
        BlockFixer.fixBlocks(world, start, end);

        return true;
    }

    static {
        REPLACE1 = (state) -> state.isAir() || (state.is(CommonBlockTags.END_STONES));

        REPLACE2 = (state) -> {
            if (state.is(CommonBlockTags.END_STONES) || state.is(EndStoneBlocks.HYDROTHERMAL_VENT) || state.is(EndStoneBlocks.SULPHUR_CRYSTAL)) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };

        IGNORE = (state) -> state.is(Blocks.WATER) || state.is(Blocks.CAVE_AIR) || state.is(EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE)) || state
                .is(EndStoneBlocks.BRIMSTONE);
    }
}
