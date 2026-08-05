package org.betterx.betterend.client.render.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FlowerPotRenderState extends BlockEntityRenderState {
    public Block plant;
    public Block soil;
    public boolean vanillaPotted;
    public long seed;
}
