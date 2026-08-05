package org.betterx.betterend.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;

public class VineFeatureConfig extends ScatterFeatureConfig {
    public static final Codec<VineFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    BlockStateProvider.CODEC.fieldOf("state").forGetter(o -> o.plant),
                    Codec.INT.fieldOf("radius").forGetter(o -> o.radius),
                    Codec.INT.fieldOf("max_length").forGetter(o -> o.maxLength)
            )
            .apply(
                    instance,
                    VineFeatureConfig::new
            ));


    public final BlockStateProvider plant;
    public final int maxLength;

    public VineFeatureConfig(Block vineBlock, int maxLength) {
        this(SimpleStateProvider.simple(vineBlock), 6, maxLength);
    }

    public VineFeatureConfig(BlockStateProvider plant, int radius, int maxLength) {
        super(radius);
        this.plant = plant;
        this.maxLength = maxLength;
    }

    public BlockState getPlantState(WorldGenLevel level, RandomSource rnd, BlockPos pos) {
        return plant.getState(level, rnd, pos);
    }

}
