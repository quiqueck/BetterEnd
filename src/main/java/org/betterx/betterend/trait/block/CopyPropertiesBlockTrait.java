package org.betterx.betterend.trait.block;

import org.betterx.betterend.BetterEnd;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A {@link BlockTrait} that establishes a block's <b>base</b> properties by copying them from another block
 * (via {@link BlockDefinition#replacePropertiesWithCopy(BlockBehaviour)}).
 * <p>
 * It exists specifically so a set-material can establish its base properties <b>as a trait</b> rather than
 * needing a dedicated lifecycle hook. {@code replacePropertiesWithCopy} must be the first operation on the
 * fluent chain (it is the eager base), but in a block set the slot-specific configuration runs before the
 * material's common-definition hook, so a direct call there would trip the chain-start guard. This trait
 * sidesteps that: its {@link #configure(BlockDefinition)} runs during {@code build()} (with
 * {@code collectingSetters != null}), which is <b>exempt</b> from the guard, and its eager copy still lands
 * before every phase-2 setter, so those apply on top of it - preserving the original layering.
 * <p>
 * Datagen-neutral: it only sets block properties and contributes nothing to the generated tree. The key is
 * shared (not {@code ofUnique}) and {@code keepLatestOnly()} is left at its {@code false} default, mirroring
 * {@code NetherProps}, so it coexists with (and runs in call order relative to) other traits.
 */
public final class CopyPropertiesBlockTrait {
    // Shared, interned key. keepLatestOnly() is false (default), so instances all run in call order.
    private static final BlockTraitKey KEY = BlockTraitKey.of(BetterEnd.C, "copy_properties");

    private CopyPropertiesBlockTrait() {
    }

    /**
     * A trait whose {@code configure(...)} copies {@code source}'s properties onto the definition as its base.
     *
     * @param source the block whose properties should be copied as the base
     * @return a base-copy trait for {@code source}
     */
    public static BlockTrait<?, ?> of(BlockBehaviour source) {
        return new BlockTraitImpl.Generic() {
            @Override
            public BlockTraitKey key() {
                return KEY;
            }

            @Override
            public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
                definition.replacePropertiesWithCopy(source);
            }
        };
    }
}
