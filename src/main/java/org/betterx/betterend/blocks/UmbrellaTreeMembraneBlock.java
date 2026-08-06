package org.betterx.betterend.blocks;

import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.noise.OpenSimplexNoise;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

public class UmbrellaTreeMembraneBlock extends SlimeBlock {
    public static final IntegerProperty COLOR = EndBlockProperties.COLOR;
    private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(0);

    public UmbrellaTreeMembraneBlock(Properties props) {
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

    /**
     * One {@code block/cube_all} model per {@link #COLOR} value, textured with that value's own sprite.
     * <p>
     * The eight {@code umbrella_tree_membrane_0..7} sprites are not just a colour gradient (green in the middle
     * of a canopy, blue at its rim): {@code _0} is fully opaque (alpha 255) while {@code _1..7} are alpha 180.
     * The wover migration collapsed the block's eight {@code color=N} blockstate variants into a single model
     * bound to the flat {@code block/umbrella_tree_membrane} sprite, so every state - including {@code color=0},
     * which is the default state and therefore also the inventory item - rendered at 70% opacity. That is what
     * made the canopy centres and the held/inventory block look see-through. The block's own
     * {@link #propagatesSkylightDown} and {@link #skipRendering} overrides special-case {@code color == 0} for
     * exactly the same reason: it is the opaque variant.
     * <p>
     * The render layer itself needs no declaration - since 26.1 Minecraft derives a quad's chunk layer from its
     * sprite's alpha ({@code FaceBakery#computeMaterialTransparency} -> {@code ChunkSectionLayer#byTransparency}),
     * so the alpha-180 sprites land on TRANSLUCENT and {@code _0} on SOLID on their own.
     */
    @Environment(EnvType.CLIENT)
    public static void provideBlockModel(WoverBlockModelGenerators generator, Block block) {
        final int defaultColor = block.defaultBlockState().getValue(COLOR);
        Identifier defaultModel = null;
        final var dispatch = PropertyDispatch.initial(COLOR);
        for (int color : COLOR.getPossibleValues()) {
            final var model = ModelTemplates.CUBE_ALL.createWithSuffix(
                    block,
                    "_" + color,
                    new TextureMapping().put(
                            TextureSlot.ALL,
                            new Material(BetterEnd.C.mk("block/umbrella_tree_membrane_" + color))
                    ),
                    generator.modelOutput()
            );
            if (color == defaultColor) {
                defaultModel = model;
            }
            dispatch.select(color, new MultiVariant(WeightedList.of(new Variant(model))));
        }

        generator.acceptBlockState(MultiVariantGenerator.dispatch(block).with(dispatch));
        // A 3D block in the inventory and in hand, as before - but pointing at the color=0 (opaque) model, which
        // is the block's default state, instead of at the flat block/umbrella_tree_membrane sprite.
        generator.delegateItemModel(block, defaultModel);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState blockState) {
        return blockState.getValue(COLOR) > 0;
    }

    @Environment(EnvType.CLIENT)
    public boolean skipRendering(BlockState state, @NotNull BlockState stateFrom, @NotNull Direction direction) {
        if (state.getValue(COLOR) > 0) {
            return super.skipRendering(state, stateFrom, direction);
        } else {
            return false;
        }
    }
}
