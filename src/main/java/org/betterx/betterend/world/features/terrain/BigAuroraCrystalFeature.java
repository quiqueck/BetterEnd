package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndCrystalBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFRotation;
import org.betterx.bclib.sdf.primitive.SDFHexPrism;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import org.joml.Vector3f;

public class BigAuroraCrystalFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        int maxY = pos.getY() + BlocksHelper.upRay(world, pos, 16);
        int minY = pos.getY() - BlocksHelper.downRay(world, pos, 16);

        if (maxY - minY < 10) {
            return false;
        }

        int y = MHelper.randRange(minY, maxY, random);
        pos = new BlockPos(pos.getX(), y, pos.getZ());

        int height = MHelper.randRange(5, 25, random);
        SDF prism = new SDFHexPrism().setHeight(height)
                                     .setRadius(MHelper.randRange(1.7F, 3F, random))
                                     .setBlock(EndCrystalBlocks.AURORA_CRYSTAL);
        Vector3f vec = MHelper.randomHorizontal(random);
        prism = new SDFRotation().setRotation(vec, random.nextFloat()).setSource(prism);
        prism.setReplaceFunction((bState) -> {
            return bState.is(CommonBlockTags.END_STONES)
                    || BlocksHelper.replaceableOrPlant(bState)
                    || bState.is(CommonBlockTags.LEAVES);
        });
        // The prism is randomly tilted (any axis, up to a full turn) and height reaches 25, so a shallow
        // tilt can swing tens of blocks horizontally - well past the 3x3 chunks a feature may touch. Clip
        // the flood-fill to the write zone; see WriteZone.
        prism.fillRecursive(world, pos, WriteZone.of(world).toBoundingBox());
        BlocksHelper.setWithoutUpdate(world, pos, EndCrystalBlocks.AURORA_CRYSTAL);

        return true;
    }
}
