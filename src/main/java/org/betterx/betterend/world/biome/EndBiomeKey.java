package org.betterx.betterend.world.biome;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndBiomeKey<C extends EndBiome.Config, PC extends EndBiome.Config> extends BiomeKey<EndBiomeBuilder> {
    public interface ConfigSupplier<C extends EndBiome.Config> {
        C get(EndBiomeKey<C, ?> key);
    }

    private final @Nullable EndBiomeKey<PC, ?> parentOrNull;

    protected EndBiomeKey(@NotNull ResourceLocation location, @Nullable EndBiomeKey<PC, ?> parentOrNull) {
        super(location);
        this.parentOrNull = parentOrNull;
    }

    @Override
    @Deprecated
    public EndBiomeBuilder bootstrap(BiomeBootstrapContext context) {
        throw new IllegalArgumentException("NetherBiomeKey must be bootstrapped with a config");
    }

    @SafeVarargs
    public final EndBiomeBuilder bootstrap(
            BiomeBootstrapContext context,
            EndBiome.Config config,
            TagKey<Biome>... typeTag
    ) {
        return new EndBiomeBuilder(context, this, typeTag)
                .parent(this.parentOrNull)
                .configure(config);
    }
}
