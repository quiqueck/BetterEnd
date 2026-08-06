package org.betterx.betterend.client.render;

import org.betterx.betterend.blocks.EndPortalBlock;
import org.betterx.betterend.registry.EndPortals;
import de.ambertation.wover.block.api.client.render.ClientTinterRegistry;
import de.ambertation.wover.client.api.WoverClientTraitEntrypoint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Binds BetterEnd's own {@link EndTinterKeys} to the {@code BlockTintSource}s that implement them. This is the
 * extension path every mod uses for a colour wover's built-in shapes cannot describe: declare a
 * {@code TinterKey} in common code, bind its client behaviour here, then attach
 * {@code ClientBlockTraits.TINT} to the block.
 * <p>
 * Registered through the {@code wover.client.traits} entrypoint, so it runs in both the client and the datagen
 * launch.
 */
@Environment(EnvType.CLIENT)
public class EndClientTinters implements WoverClientTraitEntrypoint {
    @Override
    public void registerClientTraits() {
        // Portal colours are configurable at runtime (EndPortals is loaded from config), so unlike wover's
        // built-in shapes there is no payload that could carry them - the lookup has to happen per query.
        ClientTinterRegistry.register(
                EndTinterKeys.END_PORTAL,
                (payload, block) -> state -> EndPortals.getColor(state.getValue(EndPortalBlock.PORTAL))
        );
    }
}
