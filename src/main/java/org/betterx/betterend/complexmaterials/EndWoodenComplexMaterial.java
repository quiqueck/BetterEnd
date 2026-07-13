package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class EndWoodenComplexMaterial extends WoodenBlockSet<EndWoodenComplexMaterial> implements MaterialManager.Material {
    private Block bark;
    private Block log;

    public EndWoodenComplexMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(BetterEnd.C, name, woodColor);
        setPlanksColor(planksColor);

        MaterialManager.register(this);
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);
    }

    @Override
    public void registerRecipes(RecipeBuilder.Context context) {
    }

    @Override
    public void registerBlockTags(TagBootstrapContext<Block> context) {
    }

    @Override
    public void registerItemTags(ItemTagBootstrapContext context) {
    }

    public boolean isTreeLog(Block block) {
        return block == getLog() || block == getBark();
    }

    public boolean isTreeLog(BlockState state) {
        return isTreeLog(state.getBlock());
    }

    public Block getLog() {
        if (log == null) {
            log = getBlock(SlotType.LOG);
        }
        return log;
    }

    public Block getBark() {
        if (bark == null) {
            bark = getBlock(SlotType.BARK);
        }
        return bark;
    }
}
