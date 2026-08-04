package org.betterx.betterend.world.features.bushes;


import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import de.ambertation.wover.block.api.BlockProperties;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
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

public class LargeAmaranitaFeature extends DefaultFeature {
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos pos = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) return false;

        MutableBlockPos mut = new MutableBlockPos().set(pos);
        int height = MHelper.randRange(2, 3, random);
        for (int i = 1; i < height; i++) {
            mut.setY(mut.getY() + 1);
            if (!world.isEmptyBlock(mut)) {
                return false;
            }
        }
        mut.set(pos);

        BlockState state = EndMushroomBlocks.LARGE_AMARANITA_MUSHROOM.defaultBlockState();
        BlocksHelper.setWithUpdate(world, mut, state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.BOTTOM));
        if (height > 2) {
            BlocksHelper.setWithUpdate(
                    world,
                    mut.move(Direction.UP),
                    state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.MIDDLE)
            );
        }
        BlocksHelper.setWithUpdate(
                world,
                mut.move(Direction.UP),
                state.setValue(BlockProperties.TRIPLE_SHAPE, TripleShape.TOP)
        );

        return true;
    }
}
