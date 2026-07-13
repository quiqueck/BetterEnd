package org.betterx.betterend.particle;

import org.betterx.betterend.registry.EndParticles;
import org.betterx.ui.ColorUtil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

public class InfusionParticleType extends ParticleType<InfusionParticleType> implements ParticleOptions {
    public static final MapCodec<InfusionParticleType> CODEC = Codec
            .withAlternative(ItemStack.SINGLE_ITEM_CODEC, Item.CODEC, ItemStack::new)
            .xmap((itemStack) -> new InfusionParticleType(EndParticles.INFUSION, itemStack), (itemParticleOption) -> itemParticleOption.itemStack)
            .fieldOf("item");

    public static final StreamCodec<? super RegistryFriendlyByteBuf, InfusionParticleType> STREAM_CODEC = ItemStack.STREAM_CODEC
            .map(
                    (itemStack) -> new InfusionParticleType(EndParticles.INFUSION, itemStack),
                    (itemParticleOption) -> itemParticleOption.itemStack
            );


    private final ParticleType<InfusionParticleType> type;
    private final ItemStack itemStack;

    private InfusionParticleType(ParticleType<InfusionParticleType> particleType, ItemStack stack) {
        super(true);
        this.type = particleType;
        this.itemStack = stack;
    }

    public InfusionParticleType(ItemStack stack) {
        this(EndParticles.INFUSION, stack);
    }

    @Environment(EnvType.CLIENT)
    public float[] getPalette() {
        int color = ColorUtil.extractColor(itemStack.getItem());
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
