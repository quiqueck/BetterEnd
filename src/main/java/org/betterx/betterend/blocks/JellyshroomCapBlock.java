package org.betterx.betterend.blocks;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.betterend.noise.OpenSimplexNoise;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class JellyshroomCapBlock extends SlimeBlock {
    public static final IntegerProperty COLOR = EndBlockProperties.COLOR;
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(0);

    // The colour ramp moved to the block's TINT binding (TinterKeys.GRADIENT) at registration - the block no
    // longer needs to know its own colours.
    public JellyshroomCapBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        double px = ctx.getClickedPos().getX() * 0.1;
        double py = ctx.getClickedPos().getY() * 0.1;
        double pz = ctx.getClickedPos().getZ() * 0.1;
        return this.defaultBlockState().setValue(COLOR, MHelper.floor(NOISE.eval(px, py, pz) * 3.5 + 4));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(COLOR);
    }

    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(WoverBlockModelGenerators generator, Block block) {
        final var location = generator.createSimpleTemplatedBlock(
                block,
                EndModels.PETAL_COLORED,
                new TextureMapping().put(TextureSlot.TEXTURE, new Material(BetterEnd.C.mk("block/jellyshroom_cap")))
        );
        generator.delegateItemModel(block, location);
    }
}
