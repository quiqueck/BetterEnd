package org.betterx.betterend.particle;

import org.betterx.betterend.registry.EndParticles;
import org.betterx.ui.ColorUtil;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

public class InfusionParticleType extends ParticleType<InfusionParticleType> implements ParticleOptions {
    public static final MapCodec<InfusionParticleType> CODEC = ItemStackTemplate.CODEC
            .xmap((template) -> new InfusionParticleType(EndParticles.INFUSION, template), (itemParticleOption) -> itemParticleOption.itemStackTemplate)
            .fieldOf("item");

    public static final StreamCodec<? super RegistryFriendlyByteBuf, InfusionParticleType> STREAM_CODEC = ItemStackTemplate.STREAM_CODEC
            .map(
                    (template) -> new InfusionParticleType(EndParticles.INFUSION, template),
                    (itemParticleOption) -> itemParticleOption.itemStackTemplate
            );


    private final ParticleType<InfusionParticleType> type;
    private final ItemStackTemplate itemStackTemplate;

    private InfusionParticleType(ParticleType<InfusionParticleType> particleType, ItemStackTemplate template) {
        super(true);
        this.type = particleType;
        this.itemStackTemplate = template;
    }

    public InfusionParticleType(ItemStack stack) {
        this(EndParticles.INFUSION, ItemStackTemplate.fromNonEmptyStack(stack));
    }

    @Environment(EnvType.CLIENT)
    public float[] getPalette() {
        int color = ColorUtil.extractColor(itemStackTemplate.item().value());
        return ColorUtil.toFloatArray(color);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return this.type;
    }


    @Override
    public @NotNull MapCodec<InfusionParticleType> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, InfusionParticleType> streamCodec() {
        return STREAM_CODEC;
    }
}
