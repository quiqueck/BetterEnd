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
    /**
     * 1-in-N chance of actually growing a formation once every other check has passed. This feature is
     * invoked from two places - CaveFeatureProvider's dedicated floorScatter AND, more frequently, as the
     * low-weight "default" filler of the lush floor vegetation patch (up to 3 patches/chunk, each rolling
     * dozens of columns at a 0.3 chance apiece) - neither of which can be throttled from the placement side
     * alone (the vegetation-patch invocation has no placement-modifier chain to attach onceEvery to). Gating
     * here covers both call sites uniformly. Tuned from an in-game report of ~10 formations generating in a
     * single cave with no gate at all; a whole hex-prism formation is meant to be a rare highlight, not a
     * common filler, so the target is roughly none-or-one per cave. Adjust if that reads wrong in practice.
     */
    private static final int RARITY_DIVISOR = 10;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (random.nextInt(RARITY_DIVISOR) != 0) {
            return false;
        }

        // Reject origins exposed to open sky/void: the floorScatter placement's findSolidFloor has no
        // ceiling requirement, so it can just as easily land on the outdoor surface of the landmass
        // containing the cave as on an actual cave floor - see SmaragdantCrystalFeature.touchesOpenSky for
        // the same failure mode. The vertical-clearance check below can't catch this on its own: a wide-open
        // outdoor gap satisfies "at least 10 blocks of air" just as trivially as a real chamber does.
        if (pos.getY() + 1 >= getYOnSurface(world, pos.getX(), pos.getZ())) {
            return false;
        }

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
