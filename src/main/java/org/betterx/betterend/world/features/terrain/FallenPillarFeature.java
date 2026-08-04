package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFRotation;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import org.joml.Vector3f;

public class FallenPillarFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        pos = getPosOnSurface(
                world,
                new BlockPos(pos.getX() + random.nextInt(16), pos.getY(), pos.getZ() + random.nextInt(16))
        );
        if (!world.getBlockState(pos.below(5)).is(CommonBlockTags.END_STONES)
                || !world.getBlockState(pos).isAir()) {
            return false;
        }

        float height = MHelper.randRange(20F, 40F, random);
        float radius = MHelper.randRange(2F, 4F, random);
        SDF pillar = new SDFCappedCone().setRadius1(radius)
                                        .setRadius2(radius)
                                        .setHeight(height * 0.5F)
                                        .setBlock(Blocks.OBSIDIAN);
        pillar = new SDFTranslate().setTranslate(0, radius * 0.5F - 2, 0).setSource(pillar);
        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
        pillar = new SDFDisplacement().setFunction((vec) -> {
            return (float) (noise.eval(vec.x() * 0.3, vec.y() * 0.3, vec.z() * 0.3) * 0.5F);
        }).setSource(pillar);
        Vector3f vec = MHelper.randomHorizontal(random);
        float angle = (float) random.nextGaussian() * 0.05F + (float) Math.PI;
        pillar = new SDFRotation().setRotation(vec, angle).setSource(pillar);

        BlockState mossy = EndStoneBlocks.MOSSY_OBSIDIAN.defaultBlockState();
        pillar.addPostProcess((info) -> {
            if (info.getStateUp().isAir() && random.nextFloat() > 0.1F) {
                return mossy;
            }
            return info.getState();
        }).setReplaceFunction((state) -> {
            return state.is(CommonBlockTags.END_STONES) || BlocksHelper.replaceableOrPlant(state);
        // height reaches 40 and the pillar tips near-horizontal (angle ~= PI around a horizontal axis) -
        // clip the flood-fill to the write zone; see WriteZone.
        }).fillRecursive(world, pos, WriteZone.of(world).toBoundingBox());

        return true;
    }
}
