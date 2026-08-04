package org.betterx.betterend.world.features.terrain;


import org.betterx.betterend.registry.block.EndStoneBlocks;
import de.ambertation.wover.sets.api.blocks.SlotType;

import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.HydrothermalVentBlock;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SurfaceVentFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        pos = getPosOnSurface(
                world,
                new BlockPos(pos.getX() + random.nextInt(16), pos.getY(), pos.getZ() + random.nextInt(16))
        );
        if (!world.getBlockState(pos.below(3)).is(CommonBlockTags.END_STONES)) {
            return false;
        }

        MutableBlockPos mut = new MutableBlockPos();
        int count = MHelper.randRange(15, 30, random);
        BlockState vent = EndStoneBlocks.HYDROTHERMAL_VENT.defaultBlockState()
                                                     .setValue(HydrothermalVentBlock.WATERLOGGED, false);
        for (int i = 0; i < count; i++) {
            mut.set(pos)
               .move(MHelper.floor(random.nextGaussian() * 2 + 0.5), 5, MHelper.floor(random.nextGaussian() * 2 + 0.5));
            int dist = MHelper.floor(2 - MHelper.length(
                    mut.getX() - pos.getX(),
                    mut.getZ() - pos.getZ()
            )) + random.nextInt(2);
            if (dist > 0) {
                BlockState state = world.getBlockState(mut);
                for (int n = 0; n < 10 && state.isAir(); n++) {
                    mut.setY(mut.getY() - 1);
                    state = world.getBlockState(mut);
                }
                if (state.is(CommonBlockTags.END_STONES) && !world.getBlockState(mut.above())
                                                                  .is(EndStoneBlocks.HYDROTHERMAL_VENT)) {
                    for (int j = 0; j <= dist; j++) {
                        BlocksHelper.setWithoutUpdate(world, mut, EndStoneBlocks.SULPHURIC_ROCK.getBlock(SlotType.SOURCE));
                        mut.setY(mut.getY() + 1);
                    }
                    BlocksHelper.setWithoutUpdate(world, mut, vent);
                }
            }
        }

        return true;
    }
}
