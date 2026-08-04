package org.betterx.betterend.world.features;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import de.ambertation.wover.feature.api.WriteZone;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public abstract class WallScatterFeature<FC extends ScatterFeatureConfig> extends Feature<FC> {
    private static final Direction[] DIR = BlocksHelper.makeHorizontal();

    public WallScatterFeature(Codec<FC> codec) {
        super(codec);
    }

    public abstract boolean canGenerate(FC cfg, WorldGenLevel world, RandomSource random, BlockPos pos, Direction dir);

    public abstract void generate(FC cfg, WorldGenLevel world, RandomSource random, BlockPos pos, Direction dir);

    @Override
    public boolean place(FeaturePlaceContext<FC> featureConfig) {
        FC cfg = featureConfig.config();
        final RandomSource random = featureConfig.random();
        final BlockPos center = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        int maxY = world.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX(), center.getZ());
        int minY = BlocksHelper.upRay(world, new BlockPos(center.getX(), 0, center.getZ()), maxY);
        if (maxY < 10 || maxY < minY) {
            return false;
        }
        int py = MHelper.randRange(minY, maxY, random);

        // The box below reaches +/-cfg.radius on X/Z, past the 3x3 chunks a feature may touch for a large
        // enough radius. Clip it to the write zone; see WriteZone.
        final WriteZone zone = WriteZone.of(world);
        int minX = zone.clampX(center.getX() - cfg.radius);
        int maxX = zone.clampX(center.getX() + cfg.radius);
        int minZ = zone.clampZ(center.getZ() - cfg.radius);
        int maxZ = zone.clampZ(center.getZ() + cfg.radius);

        MutableBlockPos mut = new MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            mut.setX(x);
            for (int y = -cfg.radius; y <= cfg.radius; y++) {
                mut.setY(py + y);
                for (int z = minZ; z <= maxZ; z++) {
                    mut.setZ(z);
                    if (random.nextInt(4) == 0 && world.isEmptyBlock(mut) && !overLakeWater(world, mut)) {
                        shuffle(random);
                        for (Direction dir : DIR) {
                            if (canGenerate(cfg, world, random, mut, dir)) {
                                generate(cfg, world, random, mut, dir);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * True if {@code pos} hangs directly over open water within a few blocks - i.e. it sits at a lake
     * waterline/rim rather than on real ground. A lake carves at the LAKES step before wall plants scatter
     * at VEGETAL, so scanning a short downward ray sees the finished water: a water block within reach means
     * the plant would float over the lake (the plant only clings to the bowl rim), while hitting a solid
     * block first means it has genuine ground under it and is fine. Keeps wall plants (e.g. ruscus) off the
     * open lake surface without affecting cliff-wall placements (air below -> allowed).
     */
    private static boolean overLakeWater(WorldGenLevel world, BlockPos pos) {
        final MutableBlockPos m = new MutableBlockPos().set(pos);
        for (int i = 0; i < 4; i++) {
            m.setY(m.getY() - 1);
            final var state = world.getBlockState(m);
            if (!state.getFluidState().isEmpty()) {
                return true;
            }
            if (state.isSolid()) {
                return false;
            }
        }
        return false;
    }

    private void shuffle(RandomSource random) {
        for (int i = 0; i < 4; i++) {
            int j = random.nextInt(4);
            Direction d = DIR[i];
            DIR[i] = DIR[j];
            DIR[j] = d;
        }
    }
}
