package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.PosInfo;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.*;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.blocks.HelixTreeLeavesBlock;
import org.betterx.betterend.registry.EndBlocks;

import com.mojang.math.Axis;

import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HelixTreeFeature extends DefaultFeature {
    private static final Function<PosInfo, BlockState> POST;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(BlockTags.NYLIUM)) return false;
        BlocksHelper.setWithoutUpdate(world, pos, AIR);

        float angle = random.nextFloat() * MHelper.PI2;
        float radiusRange = MHelper.randRange(4.5F, 6F, random);
        float scale = MHelper.randRange(0.5F, 1F, random);

        float dx;
        float dz;
        List<Vector3f> spline = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            float radius = (0.9F - i * 0.1F) * radiusRange;
            dx = (float) Math.sin(i + angle) * radius;
            dz = (float) Math.cos(i + angle) * radius;
            spline.add(new Vector3f(dx, i * 2, dz));
        }
        SDF sdf = SplineHelper.buildSDF(spline, 1.7F, 0.5F, (p) -> EndWoodBlocks.HELIX_TREE.getBark().defaultBlockState());
        SDF rotated = new SDFRotation().setRotation(Axis.YP, (float) Math.PI).setSource(sdf);
        sdf = new SDFUnion().setSourceA(rotated).setSourceB(sdf);

        Vector3f lastPoint = spline.get(spline.size() - 1);
        List<Vector3f> spline2 = SplineHelper.makeSpline(0, 0, 0, 0, 20, 0, 5);
        SDF stem = SplineHelper.buildSDF(
                spline2,
                1.0F,
                0.5F,
                (p) -> EndWoodBlocks.HELIX_TREE.getBark().defaultBlockState()
        );
        stem = new SDFTranslate().setTranslate(lastPoint.x(), lastPoint.y(), lastPoint.z()).setSource(stem);
        sdf = new SDFSmoothUnion().setRadius(3).setSourceA(sdf).setSourceB(stem);

        sdf = new SDFScale().setScale(scale).setSource(sdf);
        dx = 30 * scale;
        float dy1 = -20 * scale;
        float dy2 = 100 * scale;
        // fillArea reads every block in the box, and this box is up to 61 blocks wide - far past the 3x3
        // chunks a feature may touch. Clipping it to the write zone drops nothing the tree could have placed
        // there anyway (those writes were already being discarded) and removes the reads from chunks that
        // have not been carved - or even filled - yet. See WriteZone.
        final WriteZone zone = WriteZone.of(world);
        sdf.addPostProcess(POST)
           .fillArea(
                   world,
                   pos,
                   new AABB(
                           // 26.2 removed BlockPos#getCenter(); use Vec3.atCenterOf(Vec3i).
                           Vec3.atCenterOf(new BlockPos(
                                   zone.clampX(pos.getX() - (int) dx),
                                   pos.getY() + (int) dy1,
                                   zone.clampZ(pos.getZ() - (int) dx)
                           )),
                           Vec3.atCenterOf(new BlockPos(
                                   zone.clampX(pos.getX() + (int) dx),
                                   pos.getY() + (int) dy2,
                                   zone.clampZ(pos.getZ() + (int) dx)
                           ))
                   )
           );
        SplineHelper.scale(spline, scale);
        SplineHelper.fillSplineForce(
                spline,
                world,
                EndWoodBlocks.HELIX_TREE.getBark().defaultBlockState(),
                pos,
                BlockBehaviour.BlockStateBase::canBeReplaced
        );
        SplineHelper.rotateSpline(spline, (float) Math.PI);
        SplineHelper.fillSplineForce(
                spline,
                world,
                EndWoodBlocks.HELIX_TREE.getBark().defaultBlockState(),
                pos,
                BlockBehaviour.BlockStateBase::canBeReplaced
        );
        SplineHelper.scale(spline2, scale);
        BlockPos leafStart = pos.offset(
                (int) (lastPoint.x() + 0.5),
                (int) (lastPoint.y() + 0.5),
                (int) (lastPoint.z() + 0.5)
        );
        SplineHelper.fillSplineForce(
                spline2,
                world,
                EndWoodBlocks.HELIX_TREE.getLog().defaultBlockState(),
                leafStart,
                BlockBehaviour.BlockStateBase::canBeReplaced
        );

        spline.clear();
        float rad = MHelper.randRange(8F, 11F, random);
        int count = MHelper.randRange(20, 30, random);
        float scaleM = 20F / (float) count * scale * 1.75F;
        float hscale = 20F / (float) count * 0.05F;
        for (int i = 0; i <= count; i++) {
            float radius = 1 - i * hscale;
            radius = radius * radius * 2 - 1;
            radius *= radius;
            radius = (1 - radius) * rad * scale;
            dx = (float) Math.sin(i * 0.45F + angle) * radius;
            dz = (float) Math.cos(i * 0.45F + angle) * radius;
            spline.add(new Vector3f(dx, i * scaleM, dz));
        }

        Vector3f start = new Vector3f();
        Vector3f end = new Vector3f();
        lastPoint = spline.get(0);
        BlockState leaf = EndWoodBlocks.HELIX_TREE_LEAVES.defaultBlockState();
        for (int i = 1; i < spline.size(); i++) {
            Vector3f point = spline.get(i);
            int minY = MHelper.floor(lastPoint.y());
            int maxY = MHelper.floor(point.y());
            float div = point.y() - lastPoint.y();
            for (float py = minY; py <= maxY; py += 0.2F) {
                start.set(0, py, 0);
                float delta = (py - minY) / div;
                float px = Mth.lerp(delta, lastPoint.x(), point.x());
                float pz = Mth.lerp(delta, lastPoint.z(), point.z());
                end.set(px, py, pz);
                fillLine(start, end, world, leaf, leafStart, i / 2 - 1);
                float ax = Math.abs(px);
                float az = Math.abs(pz);
                if (ax > az) {
                    start.set(start.x(), start.y(), start.z() + az > 0 ? 1 : -1);
                    end.set(end.x(), end.y(), end.z() + az > 0 ? 1 : -1);
                } else {
                    start.set(start.x() + ax > 0 ? 1 : -1, start.y(), start.z());
                    end.set(end.x() + ax > 0 ? 1 : -1, end.y(), end.z());
                }
                fillLine(start, end, world, leaf, leafStart, i / 2 - 1);
            }
            lastPoint = point;
        }

        leaf = leaf.setValue(HelixTreeLeavesBlock.COLOR, 7);
        leafStart = leafStart.offset(0, (int) lastPoint.y(), 0);
        if (world.getBlockState(leafStart).isAir()) {
            BlocksHelper.setWithoutUpdate(world, leafStart, leaf);
            leafStart = leafStart.above();
            if (world.getBlockState(leafStart).isAir()) {
                BlocksHelper.setWithoutUpdate(world, leafStart, leaf);
                leafStart = leafStart.above();
                if (world.getBlockState(leafStart).isAir()) {
                    BlocksHelper.setWithoutUpdate(world, leafStart, leaf);
                }
            }
        }

        // Helix leaves are a custom Block without the WATERLOGGED property, so this is a no-op for the
        // leaves today; kept for uniformity (and its trunk shares the shore-flood scan).
        EndTreeHelper.waterlogSubmerged(world, pos, 20);
        return true;
    }

    private void fillLine(
            Vector3f start,
            Vector3f end,
            WorldGenLevel world,
            BlockState state,
            BlockPos pos,
            int offset
    ) {
        float dx = end.x() - start.x();
        float dy = end.y() - start.y();
        float dz = end.z() - start.z();
        float max = MHelper.max(Math.abs(dx), Math.abs(dy), Math.abs(dz));
        int count = MHelper.floor(max + 1);
        dx /= max;
        dy /= max;
        dz /= max;
        float x = start.x();
        float y = start.y();
        float z = start.z();

        MutableBlockPos bPos = new MutableBlockPos();
        for (int i = 0; i < count; i++) {
            bPos.set(x + pos.getX(), y + pos.getY(), z + pos.getZ());
            int color = MHelper.floor((float) i / (float) count * 7F + 0.5F) + offset;
            color = Mth.clamp(color, 0, 7);
            if (world.getBlockState(bPos).canBeReplaced()) {
                BlocksHelper.setWithoutUpdate(world, bPos, state.setValue(HelixTreeLeavesBlock.COLOR, color));
            }
            x += dx;
            y += dy;
            z += dz;
        }
        bPos.set(end.x() + pos.getX(), end.y() + pos.getY(), end.z() + pos.getZ());
        if (world.getBlockState(bPos).canBeReplaced()) {
            BlocksHelper.setWithoutUpdate(world, bPos, state.setValue(HelixTreeLeavesBlock.COLOR, 7));
        }
    }

    static {
        POST = (info) -> {
            if (EndWoodBlocks.HELIX_TREE.isTreeLog(info.getStateUp()) && EndWoodBlocks.HELIX_TREE.isTreeLog(info.getStateDown())) {
                return EndWoodBlocks.HELIX_TREE.getLog().defaultBlockState();
            }
            return info.getState();
        };
    }
}
