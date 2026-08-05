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

public class WallPlantFeatureConfig extends ScatterFeatureConfig {
    public static final Codec<WallPlantFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    BlockStateProvider.CODEC.fieldOf("state").forGetter(o -> o.plant),
                    Codec.INT.fieldOf("radius").forGetter(o -> o.radius)
            )
            .apply(
                    instance,
                    WallPlantFeatureConfig::new
            ));


    public final BlockStateProvider plant;

    public WallPlantFeatureConfig(Block plant, int radius) {
        this(SimpleStateProvider.simple(plant), radius);
    }

    public WallPlantFeatureConfig(BlockStateProvider plant, int radius) {
        super(radius);
        this.plant = plant;
    }

    public BlockState getPlantState(WorldGenLevel level, RandomSource rnd, BlockPos pos) {
        return plant.getState(level, rnd, pos);
    }

}
