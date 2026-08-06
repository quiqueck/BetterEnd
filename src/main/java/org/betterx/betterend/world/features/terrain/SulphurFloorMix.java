package org.betterx.betterend.world.features.terrain;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import de.ambertation.wover.feature.api.WriteZone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The shared "buried sulphur floor" rule for the sulphur springs: below the first couple of blocks,
 * a brimstone floor is really a noise mix of brimstone and 26.2's vanilla sulfur, with sparser
 * pockets of cinnabar.
 * <p>
 * Used by both {@link SulphuricLakeFeature} and {@link GeyserFeature} so the lake bed and the geyser
 * bowl read as the same deposit. This is deliberately a <b>feature-level</b> rule and not a surface
 * rule - it only ever rewrites blocks a feature has already placed, so the biome's own surface stays
 * exactly as it was.
 * <p>
 * Two guards keep the vanilla blocks out of sight:
 * <ul>
 *     <li>the position must be buried more than {@link #MIN_DEPTH} blocks, so the visible top of the
 *     bed stays brimstone;</li>
 *     <li>the position must not touch air on any side, so an exposed wall or an overhang never shows
 *     vanilla sulfur or cinnabar.</li>
 * </ul>
 * Active (water-touching) brimstone is left alone by the callers - that is the block the hydrothermal
 * look and the sulphur crystal shards hang off.
 */
public final class SulphurFloorMix {
    /** Separate seeds from the lake's own shape noise, so the deposit does not echo the bed's outline. */
    private static final OpenSimplexNoise TYPE_NOISE = new OpenSimplexNoise(70021);
    private static final OpenSimplexNoise CINNABAR_NOISE = new OpenSimplexNoise(70022);

    /**
     * How far below the open surface a position must sit. "Below the surface" means below water or
     * rock, not below rock specifically - the point of the deposit is that it <b>shows on the lake
     * bed</b>, so the bed's top block qualifies as long as there is enough water over it. Requiring
     * solid cover instead buried the whole thing inside the rock.
     */
    public static final int MIN_DEPTH = 3;

    private static final double TYPE_SCALE = 0.11;
    private static final double CINNABAR_SCALE = 0.14;
    /**
     * Tuned so cinnabar is clearly the minority - roughly 8% of the deposit, in pockets of its own,
     * against ~46% each for sulfur and brimstone.
     * <p>
     * Measured rather than guessed: this noise only spans about -0.88..0.92 in practice, so the
     * value has to sit far lower than a "high threshold on a [-1,1] range" intuition suggests.
     * Sampled over 864k positions, {@code >0.72} matched 0.29% and {@code >0.6} matched 1.9% - both
     * effectively never once the buried and exposure filters cut them further. {@code >0.45} lands
     * near 8%.
     */
    private static final double CINNABAR_THRESHOLD = 0.45;

    private SulphurFloorMix() {
    }

    /**
     * The block a buried floor position should hold, or {@code null} to leave it as brimstone.
     * Returns {@code null} for anything too shallow or exposed to air.
     */
    public static BlockState pick(WorldGenLevel world, WriteZone zone, BlockPos pos) {
        if (!isBuried(world, pos) || facesOpenSpace(world, zone, pos)) {
            return null;
        }
        // Never overwrite a position that is currently open. The lake queues floor positions before it
        // writes its water, so by the time the floor pass runs some of them are water - converting one
        // of those left a sulfur block floating mid-column.
        BlockState current = world.getBlockState(pos);
        if (current.isAir() || !current.getFluidState().isEmpty()) {
            return null;
        }
        return pickByNoise(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * The deposit's block for a world position, or {@code null} for brimstone - the noise decision on
     * its own, with no regard for cover or exposure.
     * <p>
     * Split out because the sulphuric cave builds through a {@link net.minecraft.world.level.chunk.ChunkAccess}
     * rather than a {@link WorldGenLevel} and has to run its own cover and exposure checks; sharing this
     * keeps the cave walls and the lake bed the same deposit rather than two lookalikes that drift apart.
     * Position-seeded only, so it stays stable across reloads and identical for the same seed.
     */
    public static BlockState pickByNoise(int bx, int by, int bz) {
        final double x = bx;
        final double y = by;
        final double z = bz;

        if (CINNABAR_NOISE.eval(x * CINNABAR_SCALE, y * CINNABAR_SCALE, z * CINNABAR_SCALE) > CINNABAR_THRESHOLD) {
            return Blocks.CINNABAR.defaultBlockState();
        }
        if (TYPE_NOISE.eval(x * TYPE_SCALE, y * TYPE_SCALE, z * TYPE_SCALE) > 0) {
            return Blocks.SULFUR.defaultBlockState();
        }
        return null;
    }

    /**
     * Rewrites an already-placed floor block in place when it qualifies. Safe to call on anything -
     * positions that do not qualify are left untouched.
     */
    public static void apply(WorldGenLevel world, WriteZone zone, BlockPos pos) {
        BlockState state = pick(world, zone, pos);
        if (state != null) {
            BlocksHelper.setWithoutUpdate(world, pos, state);
        }
    }

    /**
     * True when the {@link #MIN_DEPTH} blocks directly above are all solid, i.e. this position sits
     * deeper than that under its own cover.
     * <p>
     * Deliberately a local upward scan rather than a {@code getYOnSurface} comparison: it needs no
     * write-zone clamping (x/z never move), and it measures the cover actually above <em>this</em>
     * block rather than the terrain height of the column, which for a lake bed under water is not
     * the same thing.
     */
    public static boolean isBuried(WorldGenLevel world, BlockPos pos) {
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
        for (int i = 1; i <= MIN_DEPTH; i++) {
            above.set(pos.getX(), pos.getY() + i, pos.getZ());
            // Water counts as cover, rock counts as cover, air does not. So the top of a lake bed
            // qualifies once it is MIN_DEPTH under the water line, while the shallow rim and anything
            // open to the sky does not.
            if (world.getBlockState(above).isAir()) {
                return false;
            }
        }
        return true;
    }

    /** The deposit blocks this rule can produce - what a cleanup pass has to look for. */
    public static boolean isDepositBlock(BlockState state) {
        return state.is(Blocks.SULFUR) || state.is(Blocks.CINNABAR);
    }

    /**
     * True when any of the six neighbours is air. Horizontal peeks are clamped to the write zone.
     * <p>
     * Air only, deliberately - a lake bed is full of positions whose sides are water, and counting
     * water as exposure hid the whole deposit inside the rock (measured: sulfur and cinnabar still
     * generated, but nothing was visible without digging in). The floating sulfur that prompted the
     * wider check had a different cause, fixed in {@link #pick} by refusing to overwrite a position
     * that is already air or fluid.
     */
    public static boolean facesOpenSpace(WorldGenLevel world, WriteZone zone, BlockPos pos) {
        BlockPos.MutableBlockPos side = new BlockPos.MutableBlockPos();
        for (Direction dir : BlocksHelper.DIRECTIONS) {
            side.set(
                    zone.clampX(pos.getX() + dir.getStepX()),
                    pos.getY() + dir.getStepY(),
                    zone.clampZ(pos.getZ() + dir.getStepZ())
            );
            if (world.getBlockState(side).isAir()) {
                return true;
            }
        }
        return false;
    }
}
