package org.betterx.betterend.world.features.bushes;


import org.betterx.betterend.registry.block.EndLightBlocks;
import org.betterx.betterend.registry.block.EndMushroomBlocks;
import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.api.v2.levelgen.features.features.DefaultFeature;
import org.betterx.bclib.blocks.BaseAttachedBlock;
import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A ground patch of small-to-medium amaranita mushrooms, used to give the {@code flower_islets}
 * void-ring islands a cluster of the same fungus that grows large elsewhere (see
 * {@link org.betterx.betterend.world.features.trees.GiganticAmaranitaFeature}). The individual
 * mushrooms mirror the authentic amaranita anatomy - a hyphae-footed {@link EndMushroomBlocks#AMARANITA_STEM}
 * column capped by an {@link EndMushroomBlocks#AMARANITA_CAP} dome whose underside carries
 * {@link EndMushroomBlocks#AMARANITA_HYMENOPHORE} gills and the occasional glowing
 * {@link EndLightBlocks#AMARANITA_LANTERN} + {@link EndMushroomBlocks#AMARANITA_FUR} - but at a small scale
 * (stem height 2..6) so a whole cluster fits a radius 6..12 island. Each mushroom in the patch draws its own
 * height, so the patch reads as "various heights".
 * <p>
 * The {@code flower_islets} island tops are sangnum/pallidium (SmallIslandStructure's flower coat); both,
 * like END_MOSS, carry {@link BlockTags#NYLIUM}, so the per-mushroom ground gate accepts NYLIUM (plus
 * END_MOSS / END_STONES for robustness). Each scatter point is snapped to the WG surface heightmap (the
 * VoxelPiece re-primes WORLD_SURFACE_WG after filling the island), so the mushrooms follow the wavy island
 * top instead of a single flat plane.
 */
public class AmaranitaPatchFeature extends DefaultFeature {
    private static final int MIN_COUNT = 4;
    private static final int MAX_COUNT = 8;
    private static final int PATCH_RADIUS = 4;

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featureConfig) {
        final RandomSource random = featureConfig.random();
        final BlockPos origin = featureConfig.origin();
        final WorldGenLevel world = featureConfig.level();

        if (!isGround(world.getBlockState(origin.below()))) {
            return false;
        }

        final int count = MHelper.randRange(MIN_COUNT, MAX_COUNT, random);
        boolean placedAny = false;
        for (int i = 0; i < count; i++) {
            final int ox = MHelper.randRange(-PATCH_RADIUS, PATCH_RADIUS, random);
            final int oz = MHelper.randRange(-PATCH_RADIUS, PATCH_RADIUS, random);
            if (ox * ox + oz * oz > PATCH_RADIUS * PATCH_RADIUS) {
                continue;
            }

            final BlockPos surface = getPosOnSurfaceWG(world, origin.offset(ox, 0, oz));
            if (!isGround(world.getBlockState(surface.below()))) {
                continue;
            }
            if (!world.getBlockState(surface).canBeReplaced()) {
                continue;
            }

            final int height = MHelper.randRange(2, 6, random);
            if (growMushroom(world, surface, height, random)) {
                placedAny = true;
            }
        }
        return placedAny;
    }

    private static boolean isGround(BlockState state) {
        return state.is(BlockTags.NYLIUM)
                || state.is(EndTerrainBlocks.END_MOSS)
                || state.is(CommonBlockTags.END_STONES);
    }

    /**
     * Grows one small amaranita mushroom: a stem (hyphae foot + head, stem in the middle) of the given
     * height, topped by a two-layer cap dome with hymenophore gills underneath and, sometimes, a glowing
     * lantern + fur. Returns false without writing anything if the stem column is not clear.
     */
    private static boolean growMushroom(WorldGenLevel world, BlockPos base, int height, RandomSource random) {
        final MutableBlockPos mut = new MutableBlockPos();

        // Clearance check for the whole stem column first, so we never leave a half mushroom.
        for (int y = 0; y < height + 1; y++) {
            mut.set(base).move(Direction.UP, y);
            if (!world.getBlockState(mut).canBeReplaced()) {
                return false;
            }
        }

        // Stem: hyphae at the foot and just under the cap, stem in between (mirrors the gigantic amaranita's
        // POST rule that turns the stem ends into hyphae).
        for (int y = 0; y < height; y++) {
            mut.set(base).move(Direction.UP, y);
            final BlockState stem = (y == 0 || y == height - 1)
                    ? EndMushroomBlocks.AMARANITA_HYPHAE.defaultBlockState()
                    : EndMushroomBlocks.AMARANITA_STEM.defaultBlockState();
            BlocksHelper.setWithoutUpdate(world, mut, stem);
        }

        final BlockPos cap = base.above(height);

        // Gills (and an occasional lantern) hang beside the stem top, one below the cap rim.
        final boolean glowing = random.nextInt(3) == 0;
        final Direction lanternSide = BlocksHelper.HORIZONTAL[random.nextInt(4)];
        for (Direction dir : BlocksHelper.HORIZONTAL) {
            mut.set(cap).move(Direction.DOWN).move(dir);
            if (!world.getBlockState(mut).canBeReplaced()) {
                continue;
            }
            if (glowing && dir == lanternSide) {
                BlocksHelper.setWithoutUpdate(world, mut, EndLightBlocks.AMARANITA_LANTERN);
                mut.move(Direction.DOWN);
                if (world.getBlockState(mut).canBeReplaced()) {
                    BlocksHelper.setWithoutUpdate(
                            world,
                            mut,
                            EndMushroomBlocks.AMARANITA_FUR.defaultBlockState()
                                                          .setValue(BaseAttachedBlock.FACING, Direction.DOWN)
                    );
                }
            } else {
                BlocksHelper.setWithoutUpdate(world, mut, EndMushroomBlocks.AMARANITA_HYMENOPHORE);
            }
        }

        // Cap dome: a 3x3 layer at the stem top, a plus-shaped layer above it.
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                mut.set(cap).move(x, 0, z);
                if (world.getBlockState(mut).canBeReplaced()) {
                    BlocksHelper.setWithoutUpdate(world, mut, EndMushroomBlocks.AMARANITA_CAP);
                }
            }
        }
        for (Direction dir : BlocksHelper.HORIZONTAL) {
            mut.set(cap).move(Direction.UP).move(dir);
            if (world.getBlockState(mut).canBeReplaced()) {
                BlocksHelper.setWithoutUpdate(world, mut, EndMushroomBlocks.AMARANITA_CAP);
            }
        }
        mut.set(cap).move(Direction.UP);
        if (world.getBlockState(mut).canBeReplaced()) {
            BlocksHelper.setWithoutUpdate(world, mut, EndMushroomBlocks.AMARANITA_CAP);
        }

        return true;
    }
}
