package org.betterx.betterend.client.render;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public interface WingModelOverride<T extends LivingEntity> {
    Identifier wingTextureOverride(T livingEntity);
}
