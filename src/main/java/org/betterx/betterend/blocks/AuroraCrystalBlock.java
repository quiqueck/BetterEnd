package org.betterx.betterend.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AuroraCrystalBlock extends TransparentBlock {
    public static final Vec3i[] COLORS;

    public AuroraCrystalBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    @Deprecated
    public VoxelShape getVisualShape(
            BlockState blockState,
            BlockGetter blockGetter,
            BlockPos blockPos,
            CollisionContext collisionContext
    ) {
        return this.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    static {
        COLORS = new Vec3i[]{
                new Vec3i(247, 77, 161),
                new Vec3i(120, 184, 255),
                new Vec3i(120, 255, 168),
                new Vec3i(243, 58, 255)
        };
    }
}
