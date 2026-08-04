package org.betterx.betterend.integration.byg.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.PosInfo;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFFlatWave;
import org.betterx.bclib.sdf.operator.SDFSmoothUnion;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.integration.Integrations;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.google.common.collect.Lists;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class NightshadeRedwoodTreeFeature extends DefaultFeature {
    private static final List<Vector3f> BRANCH;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) return false;

        // height reaches 60 and branches scale up to 15 - well past the 3x3 chunks a feature may touch.
        // Clip canGenerate, the branch splines, the trunk flood-fill and the canopy flood-fill to the write
        // zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);

        BlockState log = Integrations.BYG.getDefaultState("nightshade_log");
        BlockState wood = Integrations.BYG.getDefaultState("nightshade_wood");
        BlockState leaves = Integrations.BYG.getDefaultState("nightshade_leaves");
        BlockState leaves_flower = Integrations.BYG.getDefaultState("flowering_nightshade_leaves");

        Function<BlockPos, BlockState> splinePlacer = (bpos) -> log;
        Function<BlockState, Boolean> replace = (state) -> state.is(CommonBlockTags.END_STONES)
                || BlocksHelper.replaceableOrPlant(state);
        Function<PosInfo, BlockState> post = (info) -> {
            if (info.getState().equals(log) && (!info.getStateUp().equals(log) || !info.getStateDown().equals(log))) {
                return wood;
            }
            return info.getState();
        };
        Function<BlockState, Boolean> ignore = (state) -> state.equals(log) || state.equals(wood);

        int height = MHelper.randRange(40, 60, random);
        List<Vector3f> trunk = SplineHelper.makeSpline(0, 0, 0, 0, height, 0, height / 4);
        SplineHelper.offsetParts(trunk, random, 0.8F, 0, 0.8F);

        if (!SplineHelper.canGenerate(trunk, pos, world, replace, zone.toBoundingBox())) {
            return false;
        }

        int count = height >> 2;
        float start = trunk.size() / 3F;
        float delta = trunk.size() * 0.6F;
        float max = height - 7;
        float startAngle = random.nextFloat() * MHelper.PI2;
        for (int i = 0; i < count; i++) {
            float scale = (float) (count - i) / count * 15;
            Vector3f offset = SplineHelper.getPos(trunk, (float) i / count * delta + start);
            if (offset.y() > max) {
                break;
            }
            List<Vector3f> branch = SplineHelper.copySpline(BRANCH);
            SplineHelper.rotateSpline(branch, i * 1.3F + startAngle);
            SplineHelper.scale(branch, scale);
            SplineHelper.offsetParts(branch, random, 0.3F, 0.3F, 0.3F);
            SplineHelper.offset(branch, offset);
            SplineHelper.fillSpline(branch, world, wood, pos, replace, zone.toBoundingBox());
        }

        SDF sdf = SplineHelper.buildSDF(trunk, 2.3F, 0.8F, splinePlacer);
        SDF roots = new SDFSphere().setRadius(2F).setBlock(log);
        roots = new SDFFlatWave().setIntensity(2F)
                                 .setRaysCount(MHelper.randRange(5, 7, random))
                                 .setAngle(random.nextFloat() * MHelper.PI2)
                                 .setSource(roots);
        sdf = new SDFSmoothUnion().setRadius(2F).setSourceA(sdf).setSourceB(roots);
        sdf.setReplaceFunction(replace).addPostProcess(post).fillRecursive(world, pos, zone.toBoundingBox());
        Vector3f last = SplineHelper.getPos(trunk, trunk.size() - 1.35F);
        for (int y = 0; y < 8; y++) {
            BlockPos p = pos.offset((int) (last.x() + 0.5), (int) (last.y() + y), (int) (last.z() + 0.5));
            BlocksHelper.setWithoutUpdate(world, p, y == 4 ? wood : log);
        }

        for (int y = 0; y < 16; y++) {
            BlockPos p = pos.offset((int) (last.x() + 0.5), (int) (last.y() + y), (int) (last.z() + 0.5));
            if (world.isEmptyBlock(p)) {
                BlocksHelper.setWithoutUpdate(world, p, leaves);
            }
            float radius = (1 - y / 16F) * 3F;
            int rad = (int) (radius + 1);
            radius *= radius;
            for (int x = -rad; x <= rad; x++) {
                int x2 = x * x;
                for (int z = -rad; z <= rad; z++) {
                    int z2 = z * z;
                    if (x2 + z2 < radius - random.nextFloat() * rad) {
                        BlockPos lp = p.offset(x, 0, z);
                        if (world.isEmptyBlock(lp)) {
                            BlocksHelper.setWithoutUpdate(world, lp, leaves);
                        }
                    }
                }
            }
        }

        MutableBlockPos mut = new MutableBlockPos();
        Function<PosInfo, BlockState> leavesPost1 = (info) -> {
            if (info.getState().equals(log) || info.getState().equals(wood)) {
                for (int x = -6; x < 7; x++) {
                    int ax = Math.abs(x);
                    mut.setX(x + info.getPos().getX());
                    for (int z = -6; z < 7; z++) {
                        int az = Math.abs(z);
                        mut.setZ(z + info.getPos().getZ());
                        for (int y = -6; y < 7; y++) {
                            int ay = Math.abs(y);
                            int d = ax + ay + az;
                            if (d < 7) {
                                mut.setY(y + info.getPos().getY());
                                BlockState state = info.getState(mut);
                                if (state.getBlock() instanceof LeavesBlock) {
                                    int distance = state.getValue(LeavesBlock.DISTANCE);
                                    if (d < distance) {
                                        info.setState(mut, state.setValue(LeavesBlock.DISTANCE, d));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return info.getState();
        };
        Function<PosInfo, BlockState> leavesPost2 = (info) -> {
            if (info.getState().getBlock() instanceof LeavesBlock) {
                int distance = info.getState().getValue(LeavesBlock.DISTANCE);
                if (distance > MHelper.randRange(2, 4, random)) {
                    return Blocks.AIR.defaultBlockState();
                }
                int airCount = 0;
                for (Direction d : BlocksHelper.DIRECTIONS) {
                    if (info.getState(d).isAir()) {
                        airCount++;
                    }
                    if (airCount > 5) {
                        return Blocks.AIR.defaultBlockState();
                    }
                }
                if (random.nextInt(8) == 0) {
                    return leaves_flower.setValue(LeavesBlock.DISTANCE, distance);
                }
            }
            return info.getState();
        };

        SDF canopy = new SDFCappedCone().setRadius1(12F).setRadius2(1f).setHeight(height * 0.3F).setBlock(leaves);
        canopy = new SDFDisplacement().setFunction((vec) -> MHelper.randRange(-3F, 3F, random)).setSource(canopy);
        canopy.addPostProcess(leavesPost1)
              .addPostProcess(leavesPost2)
              .fillRecursiveIgnore(world, pos.offset(0, (int) (height * 0.75), 0), zone.toBoundingBox(), ignore);

        return true;
    }

    static {
        BRANCH = Lists.newArrayList(
                new Vector3f(0, 0, 0),
                new Vector3f(0.25F, 0.1F, 0),
                new Vector3f(0.40F, 0.2F, 0),
                new Vector3f(0.50F, 0.4F, 0),
                new Vector3f(0.55F, 0.6F, 0)
        );
    }
}
