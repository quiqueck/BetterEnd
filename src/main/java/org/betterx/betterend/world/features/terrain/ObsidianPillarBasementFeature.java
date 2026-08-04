package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFRotation;
import org.betterx.bclib.sdf.operator.SDFSubtraction;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.primitive.SDFCappedCone;
import org.betterx.bclib.sdf.primitive.SDFFlatland;
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

public class ObsidianPillarBasementFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        pos = getPosOnSurface(
                world,
                new BlockPos(pos.getX() + random.nextInt(16), pos.getY(), pos.getZ() + random.nextInt(16))
        );
        if (!world.getBlockState(pos.below(5)).is(CommonBlockTags.END_STONES)) {
            return false;
        }

        float height = MHelper.randRange(10F, 35F, random);
        float radius = MHelper.randRange(2F, 5F, random);
        SDF pillar = new SDFCappedCone().setRadius1(radius)
                                        .setRadius2(radius)
                                        .setHeight(height * 0.5F)
                                        .setBlock(Blocks.OBSIDIAN);
        pillar = new SDFTranslate().setTranslate(0, height * 0.5F - 3, 0).setSource(pillar);
        SDF cut = new SDFFlatland().setBlock(Blocks.OBSIDIAN);
        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
        cut = new SDFDisplacement().setFunction((vec) -> {
            return (float) (noise.eval(vec.x() * 0.2, vec.z() * 0.2) * 3);
        }).setSource(cut);
        Vector3f vec = MHelper.randomHorizontal(random);
        float angle = random.nextFloat() * 0.5F + (float) Math.PI;
        cut = new SDFRotation().setRotation(vec, angle).setSource(cut);
        cut = new SDFTranslate().setTranslate(0, height * 0.7F - 3, 0).setSource(cut);
        pillar = new SDFSubtraction().setSourceA(pillar).setSourceB(cut);
        vec = MHelper.randomHorizontal(random);
        angle = random.nextFloat() * 0.2F;
        pillar = new SDFRotation().setRotation(vec, angle).setSource(pillar);
        BlockState mossy = EndStoneBlocks.MOSSY_OBSIDIAN.defaultBlockState();
        pillar.addPostProcess((info) -> {
            if (info.getStateUp().isAir() && random.nextFloat() > 0.1F) {
                return mossy;
            }
            return info.getState();
        }).setReplaceFunction((state) -> {
            return state.is(CommonBlockTags.END_STONES) || BlocksHelper.replaceableOrPlant(state);
        // The tilt (up to ~11deg) plus the noise-displaced cut can push the flood-fill past the pillar's own
        // radius; clip it to the write zone so it can't wander into unloaded neighbour chunks. See
        // WriteZone.
        }).fillRecursive(world, pos, WriteZone.of(world).toBoundingBox());

        return true;
    }
}
