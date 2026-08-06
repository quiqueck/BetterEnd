package org.betterx.betterend.client.render;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.block.api.render.TinterKey;
import de.ambertation.wover.block.api.trait.BlockTraitKey;

/**
 * BetterEnd's own {@link TinterKey}s, for colours none of wover's built-in {@code TinterKeys} shapes describe.
 * The client-side {@code BlockTintSource} for each is bound in {@link EndClientTinters}.
 */
public class EndTinterKeys {
    /**
     * The end portal's colour, which is looked up per portal id from BetterEnd's runtime {@code EndPortals}
     * config rather than derived from a fixed payload - so it cannot be expressed as a wover built-in.
     * Payload: none.
     */
    public static final TinterKey<Void> END_PORTAL = new TinterKey<>(BlockTraitKey.ofUnique(
            BetterEnd.C,
            "tint/end_portal"
    ));

    private EndTinterKeys() {
    }
}
