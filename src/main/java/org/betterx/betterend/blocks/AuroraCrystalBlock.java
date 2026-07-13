package org.betterx.betterend.blocks;

import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.util.MHelper;
import org.betterx.ui.ColorUtil;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AuroraCrystalBlock extends TransparentBlock implements CustomColorProvider {
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

    @Override
    public BlockColor getProvider() {
        return (state, world, pos, tintIndex) -> {
            if (pos == null) {
                pos = BlockPos.ZERO;
            }

            long i = (long) pos.getX() + (long) pos.getY() + (long) pos.getZ();
            double delta = i * 0.1;
            int index = MHelper.floor(delta);
            int index2 = (index + 1) & 3;
            delta -= index;
            index &= 3;

            Vec3i color1 = COLORS[index];
            Vec3i color2 = COLORS[index2];

            int r = MHelper.floor(Mth.lerp(delta, color1.getX(), color2.getX()));
            int g = MHelper.floor(Mth.lerp(delta, color1.getY(), color2.getY()));
            int b = MHelper.floor(Mth.lerp(delta, color1.getZ(), color2.getZ()));

            return ColorUtil.color(r, g, b);
        };
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
