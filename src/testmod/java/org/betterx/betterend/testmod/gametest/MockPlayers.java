package org.betterx.betterend.testmod.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.UUID;

/**
 * A survival-mode {@link ServerPlayer} for GameTests.
 *
 * <h2>Why not {@code GameTestHelper}'s own mock players</h2>
 * Neither built-in factory can produce what the End Veil tests need - a survival player that can also
 * carry a mob effect:
 * <ul>
 *   <li>{@code makeMockServerPlayerInLevel()} builds an anonymous subclass whose {@code gameMode()} is
 *       hard-coded to {@code CREATIVE}, so {@code isCreative()} is permanently true no matter what
 *       {@code setGameMode} or {@code updatePlayerAbilities} are told. {@code EnderManMixin}
 *       short-circuits for creative players, which would make every End Veil test pass without
 *       exercising the mechanic at all.</li>
 *   <li>{@code makeMockServerPlayer(GameType)} honours the requested mode, but never registers the
 *       player with the {@code PlayerList}, so its {@code connection} is null and
 *       {@code addEffect} NPEs inside {@code ServerPlayer#onEffectAdded} when it tries to send a
 *       packet.</li>
 * </ul>
 * This class takes the second approach and closes the gap by suppressing the three effect-sync hooks,
 * which exist purely to notify a client that a test double does not have.
 *
 * <h2>Do not use this for equipment</h2>
 * Mock players never process equipment changes: put a vanilla netherite helmet on one and its
 * {@code minecraft:armor} stays 0. Anything driven by {@code LivingEntity#detectEquipmentUpdates} -
 * item attribute modifiers, enchantment {@code EnchantmentAttributeEffect}s, BetterEnd's Crystalite
 * effect mixin - never fires, no matter how long the test idles. All of that is
 * {@link net.minecraft.world.entity.LivingEntity}-level behaviour, so test it on a real mob instead
 * (see {@link CrystaliteEffectGameTest}).
 */
public final class MockPlayers {
    private MockPlayers() {}

    /** A survival-mode player standing at {@code relativePos} within the test structure. */
    public static ServerPlayer survival(GameTestHelper helper, BlockPos relativePos) {
        final ServerPlayer player = new SurvivalTestPlayer(helper);

        GameType.SURVIVAL.updatePlayerAbilities(player.getAbilities());

        final BlockPos abs = helper.absolutePos(relativePos);
        player.snapTo(abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5, 0.0f, 0.0f);
        return player;
    }

    /**
     * Points the player's eyes at {@code target}.
     * <p>
     * {@code ServerPlayer#lookAt} cannot be used: it pushes a teleport packet down the connection this
     * test double does not have. The rotation is therefore computed directly and applied with
     * {@code snapTo}, which is purely local state.
     */
    public static void lookAt(ServerPlayer player, Vec3 target) {
        final Vec3 eye = player.getEyePosition();
        final double dx = target.x - eye.x;
        final double dy = target.y - eye.y;
        final double dz = target.z - eye.z;
        final double horizontal = Math.sqrt(dx * dx + dz * dz);

        final float yRot = Mth.wrapDegrees((float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f);
        final float xRot = Mth.wrapDegrees((float) (-(Mth.atan2(dy, horizontal) * (180.0 / Math.PI))));

        player.snapTo(player.getX(), player.getY(), player.getZ(), yRot, xRot);
        player.setYHeadRot(yRot);
    }

    private static final class SurvivalTestPlayer extends ServerPlayer {
        private SurvivalTestPlayer(GameTestHelper helper) {
            super(
                    helper.getLevel().getServer(),
                    helper.getLevel(),
                    new GameProfile(UUID.randomUUID(), "test-survival-player"),
                    ClientInformation.createDefault()
            );
        }

        @Override
        public GameType gameMode() {
            return GameType.SURVIVAL;
        }

        // The three hooks below only exist to push effect updates down a client connection. This player
        // has none, so they would NPE; the effect itself is still stored on the entity either way.
        @Override
        protected void onEffectAdded(MobEffectInstance instance, Entity source) {
        }

        @Override
        protected void onEffectUpdated(MobEffectInstance instance, boolean forced, Entity source) {
        }

        @Override
        protected void onEffectsRemoved(Collection<MobEffectInstance> instances) {
        }
    }
}
