package org.betterx.betterend.world.structures.piece;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * A stepped skirt that lets a raised footprint (a lake's water surface, a platform) step down into the
 * surrounding terrain in irregular terraces instead of ending in a vertical wall.
 * <p>
 * It follows the semantics of WorldWeaver's {@code wover:fill_to_ground} processor
 * ({@code FillToGroundProcessor}): a column is walked down through air, plants/replaceable blocks, leaves and
 * (optionally) fluids to the ground; the fill uses the sampled ground material ({@code under} = the block below
 * the ground's top block, so a mossy surface is continued with the stone underneath); a column that finds no
 * ground within reach is not filled at all. It only ever reads and writes the one column it is given, in the
 * chunk that is being generated, so the result does not depend on chunk generation order.
 * <p>
 * Unlike {@code fill_to_ground}'s fixed rings, the skirt height is a function of the distance to the footprint
 * edge (one block down per {@link #RUN} blocks, outlines shifted by noise), so it is computed per column without
 * reading any neighbour and still never differs by more than one block between neighbouring columns.
 * <p>
 * Interface: footprint edge distance + footprint height -> {@link #height}; max with the probed ground -> the
 * skirt top ->
 * {@link #fill} (the column, with sampled materials). Kept self-contained so it can be swapped for the shared
 * WorldWeaver {@code terrain_platform} helper once that exists.
 */
public final class SteppedSkirt {
    /** {@link Ground#groundY} when no solid ground was found within reach. */
    public static final int NO_GROUND = Integer.MIN_VALUE;
    /** {@link Ground#fluidTop} when the column has no fluid above its ground. */
    public static final int NO_FLUID = Integer.MIN_VALUE;

    /**
     * What a column stands on.
     *
     * @param groundY  y of the first solid (non-fillable) block, or {@link #NO_GROUND}
     * @param fluidTop y of the topmost fluid block above the ground (another lake), or {@link #NO_FLUID}
     * @param under    the sampled fill material for everything below an exposed top
     */
    public record Ground(int groundY, int fluidTop, BlockState under) {
        public boolean found() {
            return groundY != NO_GROUND;
        }

        public boolean overFluid() {
            return fluidTop != NO_FLUID;
        }

        /** The height the skirt steps down to: the fluid surface if there is one, else the ground. */
        public int reference() {
            return overFluid() ? fluidTop : groundY;
        }
    }

    /** Blocks of distance per one-block step down (the minimum tread width). */
    public static final double RUN = 2.0;
    /** Amplitude of the noise shift of the terraces, in blocks of distance. */
    public static final double JITTER = 1.5;

    private final OpenSimplexNoise noise;
    private final double flush;

    /**
     * @param noise the footprint's own noise (world coordinates are used, so seams across chunks match)
     * @param flush how far (blocks) outside the footprint edge the skirt stays at full height, so the footprint
     *              (water) is always sealed by a full-height rim
     */
    public SteppedSkirt(OpenSimplexNoise noise, double flush) {
        this.noise = noise;
        this.flush = flush;
    }

    /**
     * The top y of the skirt at a column: {@code high} up to {@code flush} blocks from the footprint, then one
     * block lower per {@link #RUN} blocks of distance, without a lower limit (the caller takes the max with the
     * ground). The distance is shifted by a smooth noise of up to +-{@link #JITTER} blocks, so the terrace
     * outlines wander instead of copying the footprint's outline.
     * <p>
     * As long as {@code distOut} changes by at most about one block between neighbouring columns (a real
     * distance field), the height changes by at most one block between them: every riser is one block, every
     * tread at least about {@link #RUN} blocks wide, and there is no band edge where a wall could form - the
     * skirt simply runs into the ground.
     */
    public int height(int wx, int wz, double distOut, int high) {
        final double jitter = noise.eval(wx * 0.05, wz * 0.05, 400) * JITTER;
        final double d = distOut - flush + jitter;
        if (d <= 0) return high;
        return high - (int) Math.floor(d / RUN);
    }

    /**
     * The final top of a column: the skirt {@code profile} where it lies at most {@code maxRaise} above the
     * {@code ground}; beyond that the raise falls off again by one block per block of excess, down to nothing.
     * The result is continuous in both the profile and the ground, so a skirt never ends in a wall where it
     * runs into a cliff or a much lower terrain layer - it fades out and leaves the natural drop alone.
     */
    public static int taper(int ground, int profile, int maxRaise) {
        final int excess = profile - ground;
        if (excess <= 0) return ground;
        final int raise = excess <= maxRaise ? excess : Math.max(0, 2 * maxRaise - excess);
        return ground + raise;
    }

    /**
     * Walks a column (chunk-local x/z in {@code mut}) down from {@code fromY} for at most {@code maxDepth} blocks.
     * Air, plants/replaceable blocks and leaves are passed; fluids are passed and remembered; the first other
     * block is the ground.
     */
    public static Ground probe(ChunkAccess chunk, MutableBlockPos mut, int fromY, int maxDepth) {
        final int origY = mut.getY();
        int fluidTop = NO_FLUID;
        int groundY = NO_GROUND;
        final int floor = Math.max(fromY - maxDepth, chunk.getMinY());
        for (int y = fromY; y > floor; y--) {
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            if (!state.getFluidState().isEmpty()) {
                if (fluidTop == NO_FLUID) fluidTop = y;
                continue;
            }
            if (passable(state)) continue;
            groundY = y;
            break;
        }
        BlockState under = Blocks.END_STONE.defaultBlockState();
        if (groundY != NO_GROUND) {
            mut.setY(groundY);
            final BlockState ground = chunk.getBlockState(mut);
            mut.setY(groundY - 1);
            final BlockState below = chunk.getBlockState(mut);
            if (below.getFluidState().isEmpty() && below.canOcclude() && !passable(below)) {
                under = below.getBlock().defaultBlockState();
            } else if (ground.canOcclude()) {
                under = ground.getBlock().defaultBlockState();
            }
        }
        mut.setY(origY);
        return new Ground(groundY, fluidTop, under);
    }

    /**
     * Fills a column from {@code top} down to the ground: {@code topBlock} on the exposed top, the sampled
     * {@code under} material below it. Fluids are filled only when the column stands over a fluid (a dike into
     * a lower lake); a column without ground gets at most {@code shellDepth} blocks below the fluid surface,
     * and nothing at all over open air.
     */
    public static void fill(
            ChunkAccess chunk,
            MutableBlockPos mut,
            int top,
            Ground ground,
            BlockState topBlock,
            int shellDepth
    ) {
        final int origY = mut.getY();
        final int bottom;
        if (ground.found()) bottom = ground.groundY() + 1;
        else if (ground.overFluid()) bottom = ground.fluidTop() - shellDepth + 1;
        else return;
        for (int y = top; y >= bottom; y--) {
            mut.setY(y);
            final BlockState state = chunk.getBlockState(mut);
            final boolean fluid = !state.getFluidState().isEmpty();
            if (fluid ? !ground.overFluid() : !passable(state)) continue;
            BasePiece.setChunkBlock(chunk, mut, y == top ? topBlock : ground.under());
        }
        mut.setY(origY);
    }

    /** Blocks the skirt fills through: air, plants and other replaceable blocks, leaves. */
    public static boolean passable(BlockState state) {
        return state.isAir() || BlocksHelper.replaceableOrPlant(state) || state.is(BlockTags.LEAVES);
    }
}
