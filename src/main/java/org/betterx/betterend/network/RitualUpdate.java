package org.betterx.betterend.network;

import de.ambertation.wunderlib.network.ClientBoundMessage;
import de.ambertation.wunderlib.network.NetworkRegistry;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.rituals.EternalRitual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

public record RitualUpdate(BlockPos center, Direction.Axis axis, byte flags) {
    static final byte ACTIVE_FLAG = 1;
    static final byte WILL_ACTIVATE_FLAG = 2;

    // StreamCodec.composite() accepts StreamCodec<? super B, T> contravariantly, so a plain
    // StreamCodec<ByteBuf, Axis> (ByteBufCodecs.STRING_UTF8 isn't generic in its buffer type) is fine here
    // without pinning it to RegistryFriendlyByteBuf.
    private static final StreamCodec<io.netty.buffer.ByteBuf, Direction.Axis> AXIS_CODEC =
            ByteBufCodecs.STRING_UTF8.map(Direction.Axis::byName, Direction.Axis::getName);

    public static final ClientBoundMessage<RitualUpdate> KEY = NetworkRegistry.registerClientBound(
            BetterEnd.C.mk("ritual_update"),
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RitualUpdate::center,
                    AXIS_CODEC, RitualUpdate::axis,
                    ByteBufCodecs.BYTE, RitualUpdate::flags,
                    RitualUpdate::new
            )
    );

    public static RitualUpdate of(EternalRitual ritual) {
        byte flags = 0;
        if (ritual.isActive()) flags |= ACTIVE_FLAG;
        if (ritual.willActivate()) flags |= WILL_ACTIVATE_FLAG;
        return new RitualUpdate(ritual.getCenter(), ritual.getAxis(), flags);
    }

    public boolean isActive() {
        return (flags & ACTIVE_FLAG) != 0;
    }

    public boolean willActivate() {
        return (flags & WILL_ACTIVATE_FLAG) != 0;
    }

    public static void send(ServerLevel level, EternalRitual ritual) {
        NetworkRegistry.sendToClient(level, KEY, of(ritual));
    }
}
