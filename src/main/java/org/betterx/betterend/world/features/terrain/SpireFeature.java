package org.betterx.betterend.world.features.terrain;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFSmoothUnion;
import org.betterx.bclib.sdf.operator.SDFTranslate;
import org.betterx.bclib.sdf.primitive.SDFSphere;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.features.EndConfiguredVegetation;
import de.ambertation.wover.feature.api.WriteZone;
import org.betterx.betterend.world.biome.EndBiome;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

public class SpireFeature extends DefaultFeature {
    protected static final Function<BlockState, Boolean> REPLACE;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        final ChunkGenerator chunkGenerator = featureConfig.chunkGenerator();
        pos = getPosOnSurfaceWG(world, pos);
        if (pos.getY() < 10 || !world.getBlockState(pos.below(3))
                                     .is(CommonBlockTags.END_STONES) || !world.getBlockState(pos.below(6))
                                                                              .is(CommonBlockTags.END_STONES)) {
            return false;
        }

        SDF sdf = new SDFSphere().setRadius(MHelper.randRange(2, 3, random)).setBlock(Blocks.END_STONE);
        int count = MHelper.randRange(3, 7, random);
        for (int i = 0; i < count; i++) {
            float rMin = (i * 1.3F) + 2.5F;
            sdf = addSegment(sdf, MHelper.randRange(rMin, rMin + 1.5F, random), random);
        }
        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
        sdf = new SDFDisplacement().setFunction((vec) -> {
            return (float) (Math.abs(noise.eval(
                    vec.x() * 0.1,
                    vec.y() * 0.1,
                    vec.z() * 0.1
            )) * 3F + Math.abs(noise.eval(
                    vec.x() * 0.3,
                    vec.y() * 0.3 + 100,
                    vec.z() * 0.3
            )) * 1.3F);
        }).setSource(sdf);
        final BlockPos center = pos;
        // Cap the spire with the real surface block of the ground it grows from (sampled once,
        // before the spire overwrites it), so noise-driven surfaces are reflected; see EndBiome.
        final BlockState spireTop = EndBiome.sampleTopMaterial(world, center.below());
        List<BlockPos> support = Lists.newArrayList();
        sdf.setReplaceFunction(REPLACE).addPostProcess((info) -> {
            if (info.getStateUp().isAir()) {
                if (random.nextInt(16) == 0) {
                    support.add(info.getPos().above());
                }
                return spireTop;
            } else if (info.getState(Direction.UP, 3).isAir()) {
                return EndBiome.findUnderMaterial(world, info.getPos());
//				return world.getBiome(info.getPos())
//							.getGenerationSettings()
//							.getSurfaceBuilderConfig()
//							.getUnderMaterial();
            }
            return info.getState();
        }).fillRecursive(world, center, WriteZone.of(world).toBoundingBox());

        support.forEach((bpos) -> {
            Holder<Biome> biome = world.getBiome(bpos);
            if (biome != null && biome.equals(EndBiomes.BLOSSOMING_SPIRES)) {
                EndConfiguredVegetation.TENANEA_BUSH.placeInWorld(world, bpos, random, chunkGenerator);
            }
        });

        return true;
    }

    protected SDF addSegment(SDF sdf, float radius, RandomSource random) {
        SDF sphere = new SDFSphere().setRadius(radius).setBlock(Blocks.END_STONE);
        SDF offseted = new SDFTranslate().setTranslate(0, radius + random.nextFloat() * 0.25F * radius, 0)
                                         .setSource(sdf);
        return new SDFSmoothUnion().setRadius(radius * 0.5F).setSourceA(sphere).setSourceB(offseted);
    }

    static {
        REPLACE = (state) -> {
            if (state.is(CommonBlockTags.END_STONES)) {
                return true;
            }
            if (state.getBlock() instanceof LeavesBlock || state.is(BlockTags.LEAVES)) {
                return true;
            }
            return BlocksHelper.replaceableOrPlant(state);
        };
    }
}
