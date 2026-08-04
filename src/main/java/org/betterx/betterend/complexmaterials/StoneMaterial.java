package org.betterx.betterend.complexmaterials;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.complexmaterials.types.FlowerPot;
import org.betterx.betterend.complexmaterials.types.Furnace;
import org.betterx.betterend.complexmaterials.types.Pedestal;
import org.betterx.betterend.complexmaterials.types.StoneLantern;
import org.betterx.bclib.trait.block.CopyPropertiesBlockTrait;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.slots.StoneSlots;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

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
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);
        blockDefinition
                // Base-copy established as a trait: it runs during build() (guard-exempt) and its eager copy
                // still lands before the phase-2 setters below, so they layer on top of END_STONE's base.
                .addTrait(CopyPropertiesBlockTrait.of(Blocks.END_STONE))
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