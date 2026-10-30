package org.betterx.betterend.world.features.terrain.caves;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import java.util.function.Predicate;

/**
 * The vanilla vegetation patch, but it skips every column whose patch would end up out in the open.
 * <p>
 * The cave biomes are vertical: the column of a cave is the same biome all the way up to the cave-biome top,
 * so a cave patch also finds the top of a low island, the rim of a slope next to a cave or a thin shelf
 * hanging in the void - and covered them with cave moss, cave grass and cave bushes (#615). Filtering the
 * patch as a whole (by its origin or footprint) also threw away the patches of every cave with a thin roof or
 * an edge nearby, so the check is made per column instead: a column only gets its ground block (and with it
 * the vegetation on top) if there is still solid ground above the topmost block the patch would replace.
 */
public class CaveVegetationPatchFeature extends VegetationPatchFeature {
    public CaveVegetationPatchFeature() {
        super(VegetationPatchConfiguration.CODEC);
    }

    @Override
    protected boolean placeGround(
            WorldGenLevel level,
            VegetationPatchConfiguration config,
            Predicate<BlockState> replaceable,
            RandomSource random,
            BlockPos.MutableBlockPos belowPos,
            int depth
    ) {
        if (isExposed(level, belowPos, config.surface(), depth)) {
            return false;
        }
        return super.placeGround(level, config, replaceable, random, belowPos, depth);
    }

    private static boolean isExposed(
            WorldGenLevel level,
            BlockPos groundPos,
            CaveSurface surface,
            int depth
    ) {
        // A floor patch replaces downwards from the ground block, a ceiling patch upwards from it.
        final int topY = surface == CaveSurface.CEILING ? groundPos.getY() + Math.max(depth, 1) - 1 : groundPos.getY();
        return level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, groundPos.getX(), groundPos.getZ()) <= topY + 1;
    }
}
