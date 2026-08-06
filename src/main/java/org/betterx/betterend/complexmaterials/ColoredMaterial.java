package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import de.ambertation.wover.block.api.DefaultBlockDefinition;
import de.ambertation.wover.block.api.render.BlockRenderTraits;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ColoredMaterial implements MaterialManager.Material {
    // Insertion-ordered: colors.forEach() below is what registers the tinted blocks, so this map's
    // iteration order becomes their registration order and - via ItemRegistry's own insertion order -
    // the order they appear in the creative tab. A HashMap keyed by RGB int shuffled them into
    // black, lime, cyan, gray, ... instead of the DyeColor order the static block fills them in.
    private static final Map<Integer, ItemLike> DYES = Maps.newLinkedHashMap();
    private static final Map<Integer, String> COLORS = Maps.newLinkedHashMap();
    private final Map<Integer, Block> colors = Maps.newLinkedHashMap();

    public ColoredMaterial(Function<BlockBehaviour.Properties, Block> constructor, Block source, boolean craftEight) {
        this(constructor, source, COLORS, DYES, craftEight, null, null);
    }

    public ColoredMaterial(
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            boolean craftEight,
            Consumer<DefaultBlockDefinition<Block>> customizer
    ) {
        this(constructor, source, COLORS, DYES, craftEight, customizer, null);
    }

    /**
     * @param colorTransform adjusts each dye colour before it becomes the block's tint (e.g. a saturation boost)
     */
    public ColoredMaterial(
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            boolean craftEight,
            Consumer<DefaultBlockDefinition<Block>> customizer,
            java.util.function.IntUnaryOperator colorTransform
    ) {
        this(constructor, source, COLORS, DYES, craftEight, customizer, colorTransform);
    }

    private List<MaterialManager.MaterialRecipe> RECIPES;
    /** Adjusts the raw dye colour before it becomes the block's tint, or {@code null} to use it as-is. */
    private final java.util.function.IntUnaryOperator colorTransform;

    public ColoredMaterial(
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            Map<Integer, String> colors,
            Map<Integer, ItemLike> dyes,
            boolean craftEight
    ) {
        this(constructor, source, colors, dyes, craftEight, null, null);
    }


    public ColoredMaterial(
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            Map<Integer, String> colors,
            Map<Integer, ItemLike> dyes,
            boolean craftEight,
            Consumer<DefaultBlockDefinition<Block>> customizer
    ) {
        this(constructor, source, colors, dyes, craftEight, customizer, null);
    }

    public ColoredMaterial(
            Function<BlockBehaviour.Properties, Block> constructor,
            Block source,
            Map<Integer, String> colors,
            Map<Integer, ItemLike> dyes,
            boolean craftEight,
            Consumer<DefaultBlockDefinition<Block>> customizer,
            java.util.function.IntUnaryOperator colorTransform
    ) {
        this.colorTransform = colorTransform;
        if (ModCore.isDatagen()) {
            RECIPES = new ArrayList<>(colors.size());
            MaterialManager.register(this);
        }
        String id = BuiltInRegistries.BLOCK.getKey(source).getPath();
        colors.forEach((color, name) -> {
            String blockName = id + "_" + name;
            DefaultBlockDefinition<Block> definition = EndBlocks.defineBlock(blockName, constructor)
                                                                 .replacePropertiesWithCopy(source)
                                                                 .mapColor(MapColor.COLOR_BLACK)
                                                                 // Every ColoredMaterial block shares one
                                                                 // grayscale texture and gets its colour from
                                                                 // the tint, so the binding belongs here rather
                                                                 // than at each call site. colorTransform lets a
                                                                 // family adjust the dye colour (the bulb
                                                                 // lanterns boost saturation); the result is
                                                                 // baked once, at definition time.
                                                                 .addTrait(BlockRenderTraits.TINT.constColor(
                                                                         colorTransform == null
                                                                                 ? color
                                                                                 : colorTransform.applyAsInt(color)
                                                                 ));
            if (customizer != null) {
                customizer.accept(definition);
            }
            Block block = definition.buildAndRegister();
            if (ModCore.isDatagen()) {
                RECIPES.add(context -> {
                    if (craftEight) {
                        RecipeBuilder.crafting(BetterEnd.C.mk(blockName), block)
                                     .outputCount(8)
                                     .shape("###", "#D#", "###")
                                     .addMaterial('#', source)
                                     .addMaterial('D', dyes.get(color))
                                     .build(context);
                    } else {
                        RecipeBuilder.crafting(BetterEnd.C.mk(blockName), block)
                                     .shapeless()
                                     .addMaterial('#', source)
                                     .addMaterial('D', dyes.get(color))
                                     .build(context);
                    }
                });
            }
            this.colors.put(color, block);
            BlocksHelper.addBlockColor(block, color);
        });
    }

    public Block getByColor(DyeColor color) {
        return colors.get(color.getMapColor().col);
    }

    public Block getByColor(int color) {
        return colors.get(color);
    }

    static {
        for (DyeColor color : DyeColor.values()) {
            int colorRGB = color.getMapColor().col;
            COLORS.put(colorRGB, color.getName());
            DYES.put(colorRGB, BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color.getName() + "_dye")));
        }
    }

    @Override
    public void registerRecipes(RecipeBuilder.Context context) {
        if (RECIPES != null) {
            RECIPES.forEach(r -> r.registerRecipes(context));
        }
    }

    @Override
    public void registerBlockTags(TagBootstrapContext<Block> context) {

    }

    @Override
    public void registerItemTags(ItemTagBootstrapContext context) {

    }
}
