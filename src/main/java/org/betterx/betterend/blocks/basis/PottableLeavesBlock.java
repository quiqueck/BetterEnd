package org.betterx.betterend.blocks.basis;

import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Thin BetterEnd leaves base over vanilla {@link TintedParticleLeavesBlock} (fixing the leaf-particle
 * chance to the historic {@code 0.01}). All leaves behaviour - decay, mineable, compost, render layer,
 * loot and tags - comes from {@code LeavesBlockTrait} at registration. Leaves have no ground survival
 * check, so the old {@code SurvivesOnBlocks} implementation (which returned an empty list) is dropped.
 */
public class PottableLeavesBlock extends TintedParticleLeavesBlock {
    public PottableLeavesBlock(BlockBehaviour.Properties properties) {
        super(0.01F, properties);
    }
}
