package org.betterx.betterend.blocks.basis;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

/**
 * Base for the End's small ground plants. Extends vanilla {@link BushBlock} (which supplies
 * {@code canSurvive}/{@code updateShape}/{@code codec}). Survival ground is decided entirely by the block's
 * {@code SurvivesOnBlockTrait} added at registration - {@code VegetationBlockMixin} injects that check into
 * {@code mayPlaceOn}, so no override is needed here. Render layer / loot are attached as traits at
 * registration. Bonemeal drops a copy of the plant (matching the historic BCLib {@code BasePlantBlock}
 * behaviour) rather than spreading like a vanilla bush.
 */
public class EndPlantBlock extends BushBlock {
    private static final VoxelShape SHAPE = box(4, 0, 4, 12, 14, 12);

    public EndPlantBlock(Properties props) {
        super(props);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        Vec3 vec3d = state.getOffset(pos);
        return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        ItemEntity item = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                new ItemStack(this)
        );
        level.addFreshEntity(item);
    }
}
