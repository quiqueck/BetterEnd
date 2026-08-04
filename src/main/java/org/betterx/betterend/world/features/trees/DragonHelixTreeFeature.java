package org.betterx.betterend.world.features.trees;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.betterend.registry.block.EndVineBlocks;
import org.betterx.betterend.registry.block.EndWoodBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.blocks.BaseVineBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import de.ambertation.wover.block.api.BlockProperties.TripleShape;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * A small conifer ("pine"/christmas-tree) grown on the {@code waterfall_ponds} islands: a straight
 * {@link EndWoodBlocks#DRAGON_TREE} trunk carrying tiered rings of {@link EndWoodBlocks#LUCERNIA_LEAVES}
 * that widen towards the base and taper to a point, with short glowing {@link EndVineBlocks#BULB_VINE}
 * strands hanging from the underside of the lowest (widest) leaf ring so the tree reads as strung with
 * "christmas lights".
 * <p>
 * This replaces the earlier helix-shaped tree the feature used to build (it kept growing far too tall for
 * the small island tops). The registry id stays {@code dragon_helix_tree} so no datagen/biome wiring
 * changes - only the geometry this feature paints is different.
 * <p>
 * The {@code LUCERNIA_LEAVES} are placed {@link LeavesBlock#PERSISTENT} so the wide skirt (up to
 * {@code maxRadius} blocks from the trunk, beyond the leaf decay distance of 7) does not decay away.
 * Ground gate: island tops are {@link EndTerrainBlocks#END_MOSS} (which carries {@link BlockTags#NYLIUM});
 * NYLIUM, END_MOSS and END_STONES are all accepted so the tree roots on any island surface.
 */
public class DragonHelixTreeFeature extends DefaultFeature {
    /** Chance a lowest-ring leaf column sprouts a hanging bulb-vine light strand. */
    private static final float LIGHT_CHANCE = 0.6F;
    /** Radians the tier bulge rotates per vertical block - the "helix" twist of the foliage. */
    private static final double HELIX_TURN = 1.15;
    /** How far (blocks) the foliage reaches out on the side facing the helix angle at each level. */
    private static final double HELIX_REACH = 1.8;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        final var random = ctx.random();
        final BlockPos pos = ctx.origin();
        final WorldGenLevel world = ctx.level();
        final BlockState below = world.getBlockState(pos.below());
        if (!below.is(BlockTags.NYLIUM)
                && !below.is(EndTerrainBlocks.END_MOSS)
                && !below.is(CommonBlockTags.END_STONES)) {
            return false;
        }

        final BlockState log = EndWoodBlocks.DRAGON_TREE.getLog().defaultBlockState();
        final BlockState leaf = EndWoodBlocks.LUCERNIA_LEAVES.defaultBlockState()
                                                             .setValue(LeavesBlock.PERSISTENT, true);

        final int height = MHelper.randRange(11, 17, random);
        final int maxRadius = MHelper.randRange(3, 4, random);
        // Leaves begin a couple of blocks up the trunk so a bit of bare stem shows underneath.
        final int foliageStart = MHelper.randRange(2, 3, random);
        final int topY = pos.getY() + height;
        // Random phase so neighbouring trees don't all twist in lock-step.
        final double helixPhase = random.nextDouble() * Math.PI * 2;
        final MutableBlockPos p = new MutableBlockPos();

        // Straight trunk.
        for (int i = 0; i <= height; i++) {
            p.set(pos.getX(), pos.getY() + i, pos.getZ());
            if (world.getBlockState(p).canBeReplaced()) {
                BlocksHelper.setWithoutUpdate(world, p, log);
            }
        }

        // Tiered canopy: radius grows every ~2 layers going down (ceil(fromTop/2)), capped at maxRadius,
        // giving the stepped christmas-tree silhouette. On top of that stepped radius each level bulges
        // outward on the side facing a helix angle that rotates with height, so the foliage spirals while
        // still reading as leveled tiers. The outermost columns of the LOWEST ring are collected so we can
        // hang light strands from them afterwards.
        final List<BlockPos> skirt = new ArrayList<>();
        final int lowestRingY = pos.getY() + foliageStart;
        for (int y = lowestRingY; y <= topY; y++) {
            final int fromTop = topY - y;
            final int radius = Math.min(maxRadius, (fromTop + 1) / 2);
            final double helixAngle = helixPhase + (y - lowestRingY) * HELIX_TURN;
            placeLeafRing(world, pos, y, radius, helixAngle, leaf, y == lowestRingY ? skirt : null);
        }
        // Pointed tip above the trunk top.
        for (int t = 1; t <= 2; t++) {
            p.set(pos.getX(), topY + t, pos.getZ());
            if (world.getBlockState(p).canBeReplaced()) {
                BlocksHelper.setWithoutUpdate(world, p, leaf);
            }
        }

        // Christmas lights: short glowing bulb-vine strands under the lowest ring's rim leaves.
        for (BlockPos leafPos : skirt) {
            if (random.nextFloat() < LIGHT_CHANCE && world.getBlockState(leafPos.below()).canBeReplaced()) {
                hangBulbVine(world, leafPos, MHelper.randRange(1, 3, random));
            }
        }

        // Re-flood any leaves/trunk left dry inside a shore pond (mirrors HelixTreeFeature).
        EndTreeHelper.waterlogSubmerged(world, pos, maxRadius + (int) Math.ceil(HELIX_REACH) + 1);
        return true;
    }

    /**
     * Paints one leaf tier at height {@code y}. The tier's radius is {@code baseRadius} everywhere, but it
     * reaches out an extra {@link #HELIX_REACH} on the side facing {@code helixAngle} (which rotates with
     * height), giving the canopy a spiralling, helix-like edge while keeping the stepped pine tiers. When
     * {@code rimOut} is non-null (the lowest tier), the outermost columns are recorded so light strands can
     * hang from the skirt.
     */
    private void placeLeafRing(
            WorldGenLevel world,
            BlockPos center,
            int y,
            int baseRadius,
            double helixAngle,
            BlockState leaf,
            List<BlockPos> rimOut
    ) {
        final MutableBlockPos p = new MutableBlockPos();
        final int scan = baseRadius + (int) Math.ceil(HELIX_REACH);
        for (int dx = -scan; dx <= scan; dx++) {
            for (int dz = -scan; dz <= scan; dz++) {
                if (dx == 0 && dz == 0) {
                    continue; // trunk column
                }
                final int d2 = dx * dx + dz * dz;
                // Per-direction reach: baseRadius plus the helix bulge on the side facing helixAngle.
                final double align = Math.max(0, Math.cos(Math.atan2(dz, dx) - helixAngle));
                final double effR = baseRadius + align * HELIX_REACH;
                if (d2 > effR * effR) continue;
                p.set(center.getX() + dx, y, center.getZ() + dz);
                if (world.getBlockState(p).canBeReplaced()) {
                    BlocksHelper.setWithoutUpdate(world, p, leaf);
                }
                // Outer shell of this tier (within ~1 block of its edge) feeds the hanging lights.
                if (rimOut != null && d2 >= (effR - 1) * (effR - 1)) {
                    rimOut.add(new BlockPos(center.getX() + dx, y, center.getZ() + dz));
                }
            }
        }
    }

    /**
     * Hangs a glowing {@link EndVineBlocks#BULB_VINE} strand of {@code length} blocks from beneath
     * {@code leafPos}. States are set manually (TOP/MIDDLE/BOTTOM) because worldgen writes bypass
     * updateShape(); the BOTTOM segment carries the vine's glowing bulb - the "light".
     */
    private void hangBulbVine(WorldGenLevel world, BlockPos leafPos, int length) {
        final MutableBlockPos p = new MutableBlockPos();
        for (int k = 1; k <= length; k++) {
            p.set(leafPos.getX(), leafPos.getY() - k, leafPos.getZ());
            if (!world.getBlockState(p).canBeReplaced()) break;
            final TripleShape shape = (k == length)
                    ? TripleShape.BOTTOM
                    : (k == 1 ? TripleShape.TOP : TripleShape.MIDDLE);
            BlocksHelper.setWithoutUpdate(
                    world, p,
                    EndVineBlocks.BULB_VINE.defaultBlockState().setValue(BaseVineBlock.SHAPE, shape)
            );
        }
    }
}
