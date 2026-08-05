package org.betterx.betterend.client.render.state;

import org.betterx.betterend.rituals.InfusionRitual;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PedestalRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();

    /**
     * Fade of the infusion socket hint ({@code 0} = not showing) and, per catalyst socket, whether a
     * pedestal already stands there. Only ever populated for the infusion pedestal the hint belongs
     * to - see {@code InfusionHint}.
     */
    public float hintIntensity;
    public float hintPulse;
    public final boolean[] hintSockets = new boolean[InfusionRitual.getMap().length];

    /**
     * Eternal portal vision: ghost crystals to turn above the ritual's pedestals, and outlines for the
     * frame blocks still missing - both relative to this pedestal. Only ever filled for the one
     * pedestal the vision is anchored to; see {@code EternalHint}.
     */
    public float visionIntensity;
    public final java.util.List<net.minecraft.core.BlockPos> visionCrystals = new java.util.ArrayList<>();
    public final java.util.List<net.minecraft.core.BlockPos> visionFrame = new java.util.ArrayList<>();

    public boolean empty;
    public boolean isBlockItem;
    public boolean isEndCrystal;
    public boolean isEternalCrystal;
    public boolean activated;
    public float height;
    public int age;
    public float tickDelta;
}
