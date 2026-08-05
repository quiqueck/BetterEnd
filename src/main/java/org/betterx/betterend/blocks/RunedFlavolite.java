package org.betterx.betterend.blocks;

import org.betterx.bclib.util.BlocksHelper;
import de.ambertation.wover.block.api.BlockProperties;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Backs both {@code flavolite_runed} and {@code flavolite_runed_eternal}. The eternal one is registered with
 * a negative strength, which is the bedrock convention for "unbreakable", and
 * {@link BlocksHelper#isInvulnerableUnsafe} is simply a test of that.
 * <p>
 * Which of the two a given instance is decides whether it drops anything, and that used to be a
 * {@code getDrops} override here. It is a per-registration fact rather than a per-state one, so it now lives
 * on the registrations: {@code flavolite_runed} keeps {@code dropSelf()} and the eternal variant carries no
 * loot table at all. {@link #dropFromExplosion} still has to ask, because that is not loot-table territory.
 */
public class RunedFlavolite extends Block {
    public static final BooleanProperty ACTIVATED = BlockProperties.ACTIVE;

    public RunedFlavolite(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(ACTIVATED);
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return !BlocksHelper.isInvulnerableUnsafe(this.defaultBlockState());
    }
}
