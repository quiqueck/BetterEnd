package org.betterx.betterend.world.structures.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

/**
 * Deterministic footprint of one {@link EndBridgePiece} span. Everything here derives from the two
 * endpoints alone - no world reads, no RNG - so any chunk can evaluate any column, including columns
 * that belong to a neighbouring chunk. {@link #isEdge} needs exactly that: it asks about the four
 * orthogonal neighbours of a column, which may sit across a chunk border.
 * <p>
 * Kept out of {@link EndBridgePiece} so it stays free of Minecraft state (that class' static block
 * states need a registry bootstrap), which lets EndBridgeWalkwayTest walk the geometry as pure maths.
 * <p>
 * The deck is a band of constant perpendicular half-width around the centre line, so it is 5 columns
 * wide whatever the bridge angle. Railings go on the outer ring only ({@link #isEdge}), leaving the
 * remaining interior as a walkway that is orthogonally connected end to end - a 3-wide corridor on a
 * straight span, and a staircase of overlapping 3-wide runs on a diagonal one.
 */
final class EndBridgeGeometry {
    /** Last N blocks of each end widen into a ramp landing. */
    static final int LANDING_LEN = 4;

    /** Perpendicular half-widths: 1.5 -> a 5-column deck, so the walkway inside the railings is 3. */
    static final double DECK_HALF_WIDTH = 1.5;
    static final double LANDING_HALF_WIDTH = 2.5;

    private final double ax, az, ay, by, dx, dz, lenSq, len, rise;

    EndBridgeGeometry(BlockPos start, BlockPos end) {
        this.ax = start.getX() + 0.5;
        this.az = start.getZ() + 0.5;
        this.ay = start.getY();
        this.by = end.getY();
        this.dx = (end.getX() + 0.5) - ax;
        this.dz = (end.getZ() + 0.5) - az;
        this.lenSq = dx * dx + dz * dz;
        this.len = Math.sqrt(lenSq);
        this.rise = Mth.clamp(len / 20.0, 2.0, 5.0);
    }

    /** Squared horizontal length of the span; a degenerate span builds nothing. */
    double lengthSq() {
        return lenSq;
    }

    /** Position of a column along the span, clamped to [0, 1]. */
    double t(int x, int z) {
        return Mth.clamp(((x + 0.5 - ax) * dx + (z + 0.5 - az) * dz) / lenSq, 0.0, 1.0);
    }

    /** Perpendicular distance of a column from the (clamped) centre line. */
    double perp(int x, int z, double t) {
        final double px = x + 0.5 - (ax + t * dx);
        final double pz = z + 0.5 - (az + t * dz);
        return Math.sqrt(px * px + pz * pz);
    }

    /** Distance of a column from the start endpoint, measured along the centre line. */
    double along(double t) {
        return t * len;
    }

    boolean isLanding(double t) {
        final double along = along(t);
        return Math.min(along, len - along) < LANDING_LEN;
    }

    double halfWidth(double t) {
        return isLanding(t) ? LANDING_HALF_WIDTH : DECK_HALF_WIDTH;
    }

    /** True when the column carries deck, regardless of what the world already holds there. */
    boolean isDeck(int x, int z) {
        final double t = t(x, z);
        return perp(x, z, t) <= halfWidth(t) + 0.5;
    }

    /**
     * A column is on the deck edge when at least one of its four orthogonal neighbours is off the deck.
     * Purely geometric, so neighbouring chunks agree on it.
     */
    boolean isEdge(int x, int z) {
        return !isDeck(x - 1, z)
                || !isDeck(x + 1, z)
                || !isDeck(x, z - 1)
                || !isDeck(x, z + 1);
    }

    /** Deck height at a position along the span: endpoint interpolation plus a gentle arch. */
    int deckY(double t) {
        return Mth.floor(Mth.lerp(t, ay, by) + rise * 4.0 * t * (1.0 - t) + 0.5);
    }
}
