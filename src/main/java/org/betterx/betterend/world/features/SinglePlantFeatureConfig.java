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

public class SinglePlantFeatureConfig extends ScatterFeatureConfig {
    public static final Codec<SinglePlantFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    BlockStateProvider.CODEC.fieldOf("state").forGetter(o -> o.plant),
                    Codec.INT.fieldOf("radius").forGetter(o -> o.radius),
                    Codec.BOOL.fieldOf("raw_heightmap").forGetter(o -> o.rawHeightmap),
                    Codec.INT.fieldOf("chance").forGetter(o -> o.chance)
            )
            .apply(
                    instance,
                    SinglePlantFeatureConfig::new
            ));

    public final BlockStateProvider plant;
    public final boolean rawHeightmap;
    public final int chance;

    public SinglePlantFeatureConfig(Block plant, int radius) {
        this(SimpleStateProvider.simple(plant), radius, true, 1);
    }

    public SinglePlantFeatureConfig(Block plant, int radius, int chance) {
        this(SimpleStateProvider.simple(plant), radius, true, chance);
    }

    public SinglePlantFeatureConfig(Block plant, int radius, boolean rawHeightmap) {
        this(SimpleStateProvider.simple(plant), radius, rawHeightmap, 1);
    }

    public SinglePlantFeatureConfig(Block plant, int radius, boolean rawHeightmap, int chance) {
        this(SimpleStateProvider.simple(plant), radius, rawHeightmap, chance);
    }

    public SinglePlantFeatureConfig(BlockStateProvider plant, int radius, boolean rawHeightmap, int chance) {
        super(radius);
        this.plant = plant;
        this.rawHeightmap = rawHeightmap;
        this.chance = chance;
    }

    public BlockState getPlantState(WorldGenLevel level, RandomSource rnd, BlockPos pos) {
        return plant.getState(level, rnd, pos);
    }

    public static SinglePlantFeatureConfig charnia(Block plant) {
        return new SinglePlantFeatureConfig(plant, 6);
    }
}
