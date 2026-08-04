package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFRotation;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.operator.SDFUnion;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class IceStarFeature extends Feature<IceStarFeatureConfig> {


    public IceStarFeature() {
        super(IceStarFeatureConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<IceStarFeatureConfig> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        IceStarFeatureConfig cfg = featureConfig.config();
        // If this log never appears, the placement pipeline (rarity/biome filter) is rejecting the
        // feature before it runs; if it appears, the star is actually being built at that position.
        org.betterx.betterend.util.WorldgenDebug.log("IceStarFeature: placing star at %s", pos);
        float size = MHelper.randRange(cfg.minSize, cfg.maxSize, random);
        int count = MHelper.randRange(cfg.minCount, cfg.maxCount, random);
        List<Vector3f> points = getFibonacciPoints(count);
        SDF sdf = null;
        SDF spike = new SDFCappedCone().setRadius1(3 + (size - 5) * 0.2F)
                                       .setRadius2(0)
                                       .setHeight(size)
                                       .setBlock(EndDecorBlocks.DENSE_SNOW);
        spike = new SDFTranslate().setTranslate(0, size - 0.5F, 0).setSource(spike);
        for (Vector3f point : points) {
            SDF rotated = spike;
            point = MHelper.normalize(point);
            float angle = MHelper.angle(MHelper.YP, point);
            if (angle > 0.01F && angle < 3.14F) {
                Vector3f axis = MHelper.normalize(MHelper.cross(MHelper.YP, point));
                rotated = new SDFRotation().setRotation(axis, angle).setSource(spike);
            } else if (angle > 1) {
                rotated = new SDFRotation().setRotation(MHelper.YP, (float) Math.PI).setSource(spike);
            }
            sdf = (sdf == null) ? rotated : new SDFUnion().setSourceA(sdf).setSourceB(rotated);
        }

        int x1 = (pos.getX() >> 4) << 4;
        int z1 = (pos.getZ() >> 4) << 4;
        pos = new BlockPos(x1 + random.nextInt(16), MHelper.randRange(32, 128, random), z1 + random.nextInt(16));

        final float ancientRadius = size * 0.7F;
        final float denseRadius = size * 0.9F;
        final float iceRadius = size < 7 ? size * 5 : size * 1.3F;
        final float randScale = size * 0.3F;

        final BlockPos center = pos;
        final BlockState ice = EndDecorBlocks.EMERALD_ICE.defaultBlockState();
        final BlockState dense = EndDecorBlocks.DENSE_EMERALD_ICE.defaultBlockState();
        final BlockState ancient = EndDecorBlocks.ANCIENT_EMERALD_ICE.defaultBlockState();
        final SDF sdfCopy = sdf;

        sdf.addPostProcess((info) -> {
            BlockPos bpos = info.getPos();
            float px = bpos.getX() - center.getX();
            float py = bpos.getY() - center.getY();
            float pz = bpos.getZ() - center.getZ();
            float distance = MHelper.length(px, py, pz) + sdfCopy.getDistance(
                    px,
                    py,
                    pz
            ) * 0.4F + random.nextFloat() * randScale;
            if (distance < ancientRadius) {
                return ancient;
            } else if (distance < denseRadius) {
                return dense;
            } else if (distance < iceRadius) {
                return ice;
            }
            return info.getState();
        // The star's spikes fan out to `size` in any direction and size can be large; clip the flood-fill to
        // the write zone - behaviour-neutral (writes out there were already dropped by WorldGenRegion) and
        // it removes the "Detected unsafe terrain read during worldgen" spam. See WriteZone.
        }).fillRecursive(world, pos, WriteZone.of(world).toBoundingBox());

        return true;
    }

    private List<Vector3f> getFibonacciPoints(int count) {
        float max = count - 1;
        List<Vector3f> result = new ArrayList<Vector3f>(count);
        for (int i = 0; i < count; i++) {
            float y = 1F - (i / max) * 2F;
            float radius = (float) Math.sqrt(1F - y * y);
            float theta = MHelper.PHI * i;
            float x = (float) Math.cos(theta) * radius;
            float z = (float) Math.sin(theta) * radius;
            result.add(new Vector3f(x, y, z));
        }
        return result;
    }
}
