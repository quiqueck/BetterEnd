package org.betterx.betterend.blocks;

import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.ui.ColorUtil;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class HelixTreeLeavesBlock extends Block implements CustomColorProvider {
    public static final IntegerProperty COLOR = EndBlockProperties.COLOR;
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(0);

    public HelixTreeLeavesBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(COLOR);
    }

    @Override
    public BlockColor getProvider() {
        return (state, world, pos, tintIndex) -> ColorUtil.color(237, getGreen(state.getValue(COLOR)), 20);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        double px = ctx.getClickedPos().getX() * 0.1;
        double py = ctx.getClickedPos().getY() * 0.1;
        double pz = ctx.getClickedPos().getZ() * 0.1;
        return this.defaultBlockState().setValue(COLOR, MHelper.floor(NOISE.eval(px, py, pz) * 3.5 + 4));
    }

    private int getGreen(int color) {
        float delta = color / 7F;
        return (int) Mth.lerp(delta, 80, 158);
    }
}
