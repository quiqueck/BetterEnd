package org.betterx.betterend.item;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.interfaces.BetterEndElytra;
import de.ambertation.wover.item.api.ItemDefinition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ArmoredElytra extends Item implements BetterEndElytra {
    private final ResourceLocation wingTexture;
    private final double movementFactor;


    public ArmoredElytra(
            double movementFactor,
            ItemDefinition<?, ?> definition
    ) {
        super(definition.getProperties());
        final var name = definition.itemKey.location().getPath().toLowerCase();
        this.wingTexture = BetterEnd.C.mk("textures/entity/" + name + ".png");

        this.movementFactor = movementFactor;
    }

    @Override
    public double getMovementFactor() {
        return movementFactor;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ResourceLocation getModelTexture() {
        return wingTexture;
    }
}
