package org.betterx.betterend.world.features.terrain;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.sdf.operator.SDFCoordModify;
import org.betterx.bclib.sdf.operator.SDFDisplacement;
import org.betterx.bclib.sdf.operator.SDFRotation;
import org.betterx.bclib.sdf.operator.SDFUnion;
import org.betterx.bclib.sdf.primitive.SDFTorus;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThinArchFeature extends Feature<ThinArchFeatureConfig> {

    public ThinArchFeature() {
        super(ThinArchFeatureConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<ThinArchFeatureConfig> featurePlaceContext) {
        final ThinArchFeatureConfig cfg = featurePlaceContext.config();
        final WorldGenLevel world = featurePlaceContext.level();
        BlockPos origin = featurePlaceContext.origin();
        RandomSource random = featurePlaceContext.random();
        BlockState state = cfg.block.getState(random, origin);
        Block block = state.getBlock();

        BlockPos pos = DefaultFeature.getPosOnSurfaceWG(
                world,
                new BlockPos(
                        (origin.getX() & 0xFFFFFFF0) | 7,
                        0,
                        (origin.getZ() & 0xFFFFFFF0) | 7
                )
        );
        if (!world.getBlockState(pos.below(5)).is(CommonBlockTags.END_STONES)) {
            return false;
        }

        SDF sdf = null;
        float bigRadius = MHelper.randRange(15F, 20F, random);
        float variation = bigRadius * 0.3F;
        int count = MHelper.randRange(2, 4, random);

        for (int i = 0; i < count; i++) {
            float smallRadius = MHelper.randRange(0.6F, 1.3F, random);
            SDF arch = new SDFTorus().setBigRadius(bigRadius - random.nextFloat() * variation)
                                     .setSmallRadius(smallRadius)
                                     .setBlock(block);
            float angle = (i - count * 0.5F) * 0.3F + random.nextFloat() * 0.05F + (float) Math.PI * 0.5F;
            arch = new SDFRotation().setRotation(Axis.XP, angle).setSource(arch);
            sdf = sdf == null ? arch : new SDFUnion().setSourceA(sdf).setSourceB(arch);
        }

        sdf = new SDFRotation().setRotation(MHelper.randomHorizontal(random), random.nextFloat() * MHelper.PI2)
                               .setSource(sdf);

        OpenSimplexNoise noise = new OpenSimplexNoise(random.nextLong());
        sdf = new SDFCoordModify().setFunction(vec -> {
            float dx = (float) noise.eval(vec.y() * 0.02, vec.z() * 0.02);
            float dy = (float) noise.eval(vec.x() * 0.02, vec.z() * 0.02);
            float dz = (float) noise.eval(vec.x() * 0.02, vec.y() * 0.02);
            vec.add(dx * 10, dy * 10, dz * 10);
        }).setSource(sdf);
        sdf = new SDFDisplacement().setFunction(vec -> {
            float offset = vec.y() / bigRadius - 0.5F;
            return Mth.clamp(offset * 3, -10F, 0F);
        }).setSource(sdf);

        float side = (bigRadius + 2.5F) * 2;
        if (side > 47) {
            side = 47;
        }
        sdf.fillArea(world, pos, AABB.ofSize(Vec3.atCenterOf(pos), side, side, side));
        return true;
    }
}
