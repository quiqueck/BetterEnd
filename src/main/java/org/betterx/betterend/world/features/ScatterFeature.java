package org.betterx.betterend.world.features;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.util.GlobalState;
import de.ambertation.wover.feature.api.WriteZone;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public abstract class ScatterFeature<FC extends ScatterFeatureConfig> extends Feature<FC> {
    public ScatterFeature(Codec<FC> codec) {
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

    protected BlockPos getCenterGround(FC cfg, WorldGenLevel world, BlockPos pos) {
        return DefaultFeature.getPosOnSurfaceWG(world, pos);
    }

    protected boolean canSpawn(FC cfg, WorldGenLevel world, BlockPos pos) {
        if (pos.getY() < 5) {
            return false;
        } else return world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES);
    }

    protected boolean getGroundPlant(FC cfg, WorldGenLevel world, MutableBlockPos pos) {
        int down = BlocksHelper.downRay(world, pos, 16);
        if (down > Math.abs(getYOffset() * 2)) {
            return false;
        }
        pos.setY(pos.getY() - down);
        return true;
    }

    protected int getYOffset() {
        return 5;
    }

    protected int getChance() {
        return 1;
    }

    @Override
    public boolean place(FeaturePlaceContext<FC> featureConfig) {
        FC cfg = featureConfig.config();
        final MutableBlockPos POS = GlobalState.stateForThread().POS;
        final RandomSource random = featureConfig.random();
        BlockPos center = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        center = getCenterGround(cfg, world, center);

        if (!canSpawn(cfg, world, center)) {
            return false;
        }

        float r = MHelper.randRange(cfg.radius * 0.5F, cfg.radius, random);
        int count = MHelper.floor(r * r * MHelper.randRange(1.5F, 3F, random));
        // Scatter points land up to cfg.radius from center (20 for end_lotus_leaf) - past the 3x3 chunks a
        // feature may touch on an unlucky roll. Skipping an out-of-zone point is behaviour-neutral (a plant
        // placed there would have been silently dropped by WorldGenRegion anyway) and removes the "Detected
        // unsafe terrain read during worldgen" spam every ScatterFeature subclass produced. See
        // WriteZone.
        final WriteZone zone = WriteZone.of(world);
        synchronized (this) {
            for (int i = 0; i < count; i++) {
                float pr = r * (float) Math.sqrt(random.nextFloat());
                float theta = random.nextFloat() * MHelper.PI2;
                float x = pr * (float) Math.cos(theta);
                float z = pr * (float) Math.sin(theta);

                POS.set(center.getX() + x, center.getY() + getYOffset(), center.getZ() + z);
                if (!zone.contains(POS)) {
                    continue;
                }

                if (getGroundPlant(cfg, world, POS) && canGenerate(
                        cfg,
                        world,
                        random,
                        center,
                        POS,
                        r
                ) && (getChance() < 2 || random.nextInt(getChance()) == 0)) {
                    generate(cfg, world, random, POS);
                }
            }
        }

        return true;
    }
}
