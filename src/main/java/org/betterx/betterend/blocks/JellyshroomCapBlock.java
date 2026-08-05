package org.betterx.betterend.blocks;

import org.betterx.bclib.interfaces.CustomColorProvider;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.client.models.EndModels;
import org.betterx.betterend.noise.OpenSimplexNoise;
import org.betterx.ui.ColorUtil;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class JellyshroomCapBlock extends SlimeBlock implements CustomColorProvider {
    public static final IntegerProperty COLOR = EndBlockProperties.COLOR;
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(0);
    private final Vec3i colorStart;
    private final Vec3i colorEnd;
    private final int coloritem;

    public JellyshroomCapBlock(Properties props, int r1, int g1, int b1, int r2, int g2, int b2) {
        super(props);
        colorStart = new Vec3i(r1, g1, b1);
        colorEnd = new Vec3i(r2, g2, b2);
        coloritem = ColorUtil.color((r1 + r2) >> 1, (g1 + g2) >> 1, (b1 + b2) >> 1);
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

    @Override
    @Environment(EnvType.CLIENT)
    public BlockTintSource getProvider() {
        return (state) -> {
            float delta = (float) state.getValue(COLOR) / 7F;
            int r = Mth.floor(Mth.lerp(delta, colorStart.getX() / 255F, colorEnd.getX() / 255F) * 255F);
            int g = Mth.floor(Mth.lerp(delta, colorStart.getY() / 255F, colorEnd.getY() / 255F) * 255F);
            int b = Mth.floor(Mth.lerp(delta, colorStart.getZ() / 255F, colorEnd.getZ() / 255F) * 255F);
            return ColorUtil.color(r, g, b);
        };
    }
}
