package org.betterx.betterend.integration.byg.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.sdf.SDF;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.bclib.util.SplineHelper;
import org.betterx.betterend.integration.Integrations;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.google.common.base.Function;
import org.joml.Vector3f;

import java.util.List;

public class BigEtherTreeFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) return false;

        // height reaches 60 and branches fan out radially to ~0.4*height (~24 blocks) - well past the 3x3
        // chunks a feature may touch. Clip both the branch splines and the trunk flood-fill to the write
        // zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);

        BlockState log = Integrations.BYG.getDefaultState("ether_log");
        BlockState wood = Integrations.BYG.getDefaultState("ether_wood");
        Function<BlockPos, BlockState> splinePlacer = (bpos) -> {
            return log;
        };
        Function<BlockState, Boolean> replace = (state) -> {
            return state.is(CommonBlockTags.END_STONES) || BlocksHelper.replaceableOrPlant(state);
        };

        int height = MHelper.randRange(40, 60, random);
        List<Vector3f> trunk = SplineHelper.makeSpline(0, 0, 0, 0, height, 0, height / 4);
        SplineHelper.offsetParts(trunk, random, 2F, 0, 2F);
        SDF sdf = SplineHelper.buildSDF(trunk, 2.3F, 0.8F, splinePlacer);

        int count = height / 15;
        for (int i = 1; i < count; i++) {
            float splinePos = (float) i / (float) count;
            float startAngle = random.nextFloat() * MHelper.PI2;
            float length = (1 - splinePos) * height * 0.4F;
            int points = (int) (length / 3);
            List<Vector3f> branch = SplineHelper.makeSpline(0, 0, 0, length, 0, 0, points < 2 ? 2 : points);
            SplineHelper.powerOffset(branch, length, 2F);
            int rotCount = MHelper.randRange(5, 7, random);
            Vector3f start = SplineHelper.getPos(trunk, splinePos * (trunk.size() - 1));
            for (int j = 0; j < rotCount; j++) {
                float angle = startAngle + (float) j / rotCount * MHelper.PI2;
                List<Vector3f> br = SplineHelper.copySpline(branch);
                SplineHelper.offsetParts(br, random, 0, 1, 1);
                SplineHelper.rotateSpline(br, angle);

                SplineHelper.offset(br, start);
                SplineHelper.fillSpline(br, world, wood, pos, replace, zone.toBoundingBox());
            }
        }

        sdf.setReplaceFunction((state) -> {
            return state.is(CommonBlockTags.END_STONES) || BlocksHelper.replaceableOrPlant(state);
        }).addPostProcess((info) -> {
            if (info.getState().equals(log) && (!info.getStateUp().equals(log) || !info.getStateDown().equals(log))) {
                return wood;
            }
            return info.getState();
        }).fillRecursive(world, pos, zone.toBoundingBox());

        return true;
    }
}
