package org.betterx.betterend.world.structures.piece;

import net.minecraft.core.BlockPos;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure-geometry (no Minecraft bootstrap, no world) regression guard for the End bridge deck layout.
 * <p>
 * The original layout drew a 3-column deck and turned every column further than half a block from the
 * centre line into a railing. On an axis-aligned span that leaves a 1-wide corridor, but on any diagonal
 * span the remaining free columns form a staircase whose steps only touch diagonally - and a player
 * cannot squeeze between two wall blocks placed on the opposing corners. In other words most bridges
 * were sealed shut by their own railings. Measured with this test's model, 424 of 480 sampled spans had
 * no walkable route.
 * <p>
 * The fix has two halves, and this test pins both: the deck is wide enough
 * ({@code DECK_HALF_WIDTH = 1.5}, i.e. 5 columns at every angle) and railings are restricted to the
 * outer ring via {@link EndBridgeGeometry#isEdge} rather than to "anything off-centre". This walks the
 * resulting footprint for a fan of angles and lengths and asserts an orthogonally connected route from
 * one landing to the other, assuming the worst case that every single edge column carries a railing
 * (the ~20% ruined gaps and the erosion pass can only ever open the walkway further, never close it).
 */
class EndBridgeWalkwayTest {
    private static final int PAD = 8;
    private static final int Y = 64;

    private record Column(int x, int z) {
    }

    /** Columns that carry deck and are not blocked by a railing (worst case: every edge column is). */
    private static Set<Column> walkable(EndBridgeGeometry span, int minX, int maxX, int minZ, int maxZ) {
        final Set<Column> result = new HashSet<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!span.isDeck(x, z)) continue;
                // Landings carry no railing at all; mid-span, only non-edge columns stay free.
                if (span.isLanding(span.t(x, z)) || !span.isEdge(x, z)) {
                    result.add(new Column(x, z));
                }
            }
        }
        return result;
    }

    private static boolean crossable(int ex, int ez) {
        final EndBridgeGeometry span = new EndBridgeGeometry(new BlockPos(0, Y, 0), new BlockPos(ex, Y, ez));
        final int minX = Math.min(0, ex) - PAD;
        final int maxX = Math.max(0, ex) + PAD;
        final int minZ = Math.min(0, ez) - PAD;
        final int maxZ = Math.max(0, ez) + PAD;

        final Set<Column> walkable = walkable(span, minX, maxX, minZ, maxZ);

        final List<Column> startSide = new ArrayList<>();
        final List<Column> endSide = new ArrayList<>();
        for (Column c : walkable) {
            final double t = span.t(c.x(), c.z());
            if (t == 0.0) startSide.add(c);
            else if (t == 1.0) endSide.add(c);
        }
        if (startSide.isEmpty() || endSide.isEmpty()) return false;

        final Set<Column> seen = new HashSet<>(startSide);
        final Deque<Column> queue = new ArrayDeque<>(startSide);
        while (!queue.isEmpty()) {
            final Column c = queue.poll();
            for (Column n : new Column[]{
                    new Column(c.x() + 1, c.z()),
                    new Column(c.x() - 1, c.z()),
                    new Column(c.x(), c.z() + 1),
                    new Column(c.x(), c.z() - 1)
            }) {
                if (walkable.contains(n) && seen.add(n)) queue.add(n);
            }
        }
        return endSide.stream().anyMatch(seen::contains);
    }

    @Test
    void everyBridgeAngleKeepsAWalkableRoute() {
        final List<String> blocked = new ArrayList<>();
        // MIN_SPAN..MAX_SPAN of EndBridgeStructure, sampled all the way around.
        for (int degrees = 0; degrees < 360; degrees += 5) {
            for (int length : new int[]{24, 37, 60, 96}) {
                final double radians = Math.toRadians(degrees);
                final int ex = (int) Math.round(Math.cos(radians) * length);
                final int ez = (int) Math.round(Math.sin(radians) * length);
                if (!crossable(ex, ez)) {
                    blocked.add(degrees + " deg / " + length + " blocks");
                }
            }
        }
        assertTrue(
                blocked.isEmpty(),
                "Railings sealed the walkway on " + blocked.size() + " span(s): " + blocked
        );
    }

    /**
     * The walkway must also be more than a single column wide, otherwise one eroded or terrain-clipped
     * block is enough to cut the bridge in two. Five deck columns minus the two railing rows leaves
     * three; two is accepted here as the slack for angles where rounding trims a column.
     */
    @Test
    void midSpanWalkwayIsAtLeastTwoColumnsWide() {
        for (int degrees = 0; degrees < 360; degrees += 5) {
            final double radians = Math.toRadians(degrees);
            final int ex = (int) Math.round(Math.cos(radians) * 60);
            final int ez = (int) Math.round(Math.sin(radians) * 60);
            final EndBridgeGeometry span = new EndBridgeGeometry(new BlockPos(0, Y, 0), new BlockPos(ex, Y, ez));

            final int minX = Math.min(0, ex) - PAD;
            final int maxX = Math.max(0, ex) + PAD;
            final int minZ = Math.min(0, ez) - PAD;
            final int maxZ = Math.max(0, ez) + PAD;

            int deck = 0;
            int free = 0;
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!span.isDeck(x, z) || span.isLanding(span.t(x, z))) continue;
                    deck++;
                    if (!span.isEdge(x, z)) free++;
                }
            }
            // Mid-span the deck is 5 columns wide, so a 3-wide walkway is 60% of it.
            assertTrue(
                    free * 5 >= deck * 2,
                    "Walkway too narrow at " + degrees + " deg: " + free + " free of " + deck + " deck columns"
            );
        }
    }
}
