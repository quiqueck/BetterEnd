package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.types.FlowerPot;
import org.betterx.betterend.complexmaterials.types.Furnace;
import org.betterx.betterend.complexmaterials.types.Pedestal;
import org.betterx.betterend.complexmaterials.types.StoneLantern;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.slots.StoneSlots;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

public class StoneMaterial extends BlockSet<StoneMaterial> implements MaterialManager.Material {
    public static final SlotType PEDESTAL = new SlotType("pedestal");
    public static final SlotType LANTERN = new SlotType("lantern");
    public static final SlotType FURNACE = new SlotType("furnace");
    public static final SlotType FLOWER_POT = new SlotType("flower_pot");
    public final MapColor color;


    public StoneMaterial(String name, MapColor color) {
        super(BetterEnd.C, name, SlotType.SOURCE);
        this.color = color;

        MaterialManager.register(this);
        this.buildAndRegister();
    }

    @Override
    protected void addBaseBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        // The base-copy must be the chain-start (it is the eager base). This hook runs before the slot's
        // own configuration, so END_STONE's properties are the base that the slot trait + the classification
        // below layer over - same result as before, but the copy is now genuinely the first operation.
        blockDefinition.replacePropertiesWithCopy(Blocks.END_STONE);
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);
        blockDefinition
                .mapColor(color)
                .addTags(BlockTags.DRAGON_IMMUNE)
                .addTrait(BlockTraits.STONE_BLOCK);

        if (slot == SlotType.SOURCE) {
            blockDefinition
                    .addTags(CommonBlockTags.END_STONES)
                    .addItemTags(ItemTags.STONE_CRAFTING_MATERIALS, ItemTags.STONE_TOOL_MATERIALS);
        }
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(
                StoneSlots.SOURCE,
                StoneSlots.POLISHED_SOURCE,
                StoneSlots.TILES_SOURCE,
                StoneSlots.SLAB,
                StoneSlots.STAIRS,
                StoneSlots.WALL,
                StoneSlots.PILLAR,
                StoneSlots.BUTTON,
                StoneSlots.PRESSURE_PLATE,
                StoneSlots.BRICK_SOURCE,
                StoneSlots.BRICK_SLAB,
                StoneSlots.BRICK_STAIRS,
                StoneSlots.BRICK_WALL,
                Pedestal.SLOT,
                StoneLantern.SLOT,
                Furnace.SLOT,
                FlowerPot.SLOT
        );
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
}