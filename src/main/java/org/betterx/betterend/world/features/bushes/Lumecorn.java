package org.betterx.betterend.world.features.bushes;


import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.blocks.EndBlockProperties.LumecornShape;
import org.betterx.betterend.blocks.LumecornBlock;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class Lumecorn extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) return false;

        int height = MHelper.randRange(4, 7, random);
        MutableBlockPos mut = new MutableBlockPos().set(pos);
        for (int i = 1; i < height; i++) {
            mut.move(Direction.UP);
            if (!world.isEmptyBlock(mut)) {
                return false;
            }
        }
        mut.set(pos);
        BlockState topMiddle = EndMushroomBlocks.LUMECORN.defaultBlockState()
                                                 .setValue(LumecornBlock.SHAPE, LumecornShape.LIGHT_TOP_MIDDLE);
        BlockState middle = EndMushroomBlocks.LUMECORN.defaultBlockState()
                                              .setValue(LumecornBlock.SHAPE, LumecornShape.LIGHT_MIDDLE);
        BlockState bottom = EndMushroomBlocks.LUMECORN.defaultBlockState()
                                              .setValue(LumecornBlock.SHAPE, LumecornShape.LIGHT_BOTTOM);
        BlockState top = EndMushroomBlocks.LUMECORN.defaultBlockState().setValue(LumecornBlock.SHAPE, LumecornShape.LIGHT_TOP);
        if (height == 4) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    mut,
                    EndMushroomBlocks.LUMECORN.defaultBlockState().setValue(LumecornBlock.SHAPE, LumecornShape.BOTTOM_SMALL)
            );
            BlocksHelper.setWithoutUpdate(world, mut.move(Direction.UP), bottom);
            BlocksHelper.setWithoutUpdate(world, mut.move(Direction.UP), topMiddle);
            BlocksHelper.setWithoutUpdate(world, mut.move(Direction.UP), top);
            return true;
        }
        if (random.nextBoolean()) {
            BlocksHelper.setWithoutUpdate(
                    world,
                    mut,
                    EndMushroomBlocks.LUMECORN.defaultBlockState().setValue(LumecornBlock.SHAPE, LumecornShape.BOTTOM_SMALL)
            );
        } else {
            BlocksHelper.setWithoutUpdate(
                    world,
                    mut,
                    EndMushroomBlocks.LUMECORN.defaultBlockState().setValue(LumecornBlock.SHAPE, LumecornShape.BOTTOM_BIG)
            );
            BlocksHelper.setWithoutUpdate(
                    world,
                    mut.move(Direction.UP),
                    EndMushroomBlocks.LUMECORN.defaultBlockState().setValue(LumecornBlock.SHAPE, LumecornShape.MIDDLE)
            );
            height--;
        }
        BlocksHelper.setWithoutUpdate(world, mut.move(Direction.UP), bottom);
        for (int i = 4; i < height; i++) {
            BlocksHelper.setWithoutUpdate(world, mut.move(Direction.UP), middle);
        }
        BlocksHelper.setWithoutUpdate(world, mut.move(Direction.UP), topMiddle);
        BlocksHelper.setWithoutUpdate(world, mut.move(Direction.UP), top);
        return false;
    }
}
