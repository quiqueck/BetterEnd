package org.betterx.betterend.world.features;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.util.GlobalState;
import de.ambertation.wover.feature.api.WriteZone;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public abstract class InvertedScatterFeature<FC extends ScatterFeatureConfig> extends Feature<FC> {


    public InvertedScatterFeature(Codec<FC> codec) {
        super(codec);
    }

    public abstract boolean canGenerate(
            FC cfg,
            WorldGenLevel world,
            RandomSource random,
            BlockPos center,
            BlockPos blockPos,
            float radius
    );

    public abstract void generate(FC cfg, WorldGenLevel world, RandomSource random, BlockPos blockPos);

    @Override
    public boolean place(FeaturePlaceContext<FC> featureConfig) {
        FC cfg = featureConfig.config();
        final MutableBlockPos POS = GlobalState.stateForThread().POS;
        final RandomSource random = featureConfig.random();
        final BlockPos center = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        int maxY = world.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX(), center.getZ());
        int minY = BlocksHelper.upRay(world, new BlockPos(center.getX(), 0, center.getZ()), maxY);
        // Scatter points land up to cfg.radius from center, past the 3x3 chunks a feature may touch on an
        // unlucky roll - same over-read as ScatterFeature.place(); see WriteZone there for the
        // reasoning.
        final WriteZone zone = WriteZone.of(world);
        for (int y = maxY; y > minY; y--) {
            POS.set(center.getX(), y, center.getZ());
            if (world.getBlockState(POS).isAir() && !world.getBlockState(POS.above()).isAir()) {
                float r = MHelper.randRange(cfg.radius * 0.5F, cfg.radius, random);
                int count = MHelper.floor(r * r * MHelper.randRange(0.5F, 1.5F, random));
                for (int i = 0; i < count; i++) {
                    float pr = r * (float) Math.sqrt(random.nextFloat());
                    float theta = random.nextFloat() * MHelper.PI2;
                    float x = pr * (float) Math.cos(theta);
                    float z = pr * (float) Math.sin(theta);

                    POS.set(center.getX() + x, center.getY() - 7, center.getZ() + z);
                    if (!zone.contains(POS)) {
                        continue;
                    }
                    int up = BlocksHelper.upRay(world, POS, 16);
                    if (up > 14) continue;
                    POS.setY(POS.getY() + up);

                    if (canGenerate(cfg, world, random, center, POS, r)) {
                        generate(cfg, world, random, POS);
                    }
                }
            }
        }
        return true;
    }
}
