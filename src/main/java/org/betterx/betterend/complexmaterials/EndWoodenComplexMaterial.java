package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.furniture.slots.BarStool;
import org.betterx.bclib.furniture.slots.Chair;
import org.betterx.bclib.furniture.slots.Taburet;
import org.betterx.betterend.BetterEnd;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class EndWoodenComplexMaterial extends WoodenBlockSet<EndWoodenComplexMaterial> implements MaterialManager.Material {
    private final MapColor plankColor;
    protected Block furnitureCloth = Blocks.WHITE_WOOL;
    private Block bark;
    private Block log;

    public EndWoodenComplexMaterial(String name, MapColor woodColor, MapColor planksColor) {
        super(BetterEnd.C, name, woodColor);
        setPlanksColor(planksColor);
        this.plankColor = planksColor;

        MaterialManager.register(this);
    }

    /**
     * Sets the block whose texture the upholstery of this set's furniture (chair/bar-stool) uses. Must be
     * called before {@link #buildAndRegister()}, since that is when the slots are built.
     */
    public EndWoodenComplexMaterial setFurnitureCloth(Block clothMaterial) {
        this.furnitureCloth = clothMaterial;
        return this;
    }

    /**
     * Adds the three wooden furniture slots (taburet/chair/bar-stool) to {@code slots}. The slot classes live in
     * bclib (BetterNether shares them); wover-sets-api has no furniture of its own. Each is built from this set's
     * slab and textured with its planks, matching what {@code WoodSlots.TABURET}/{@code CHAIR}/{@code BAR_STOOL}
     * used to do before the wover migration.
     *
     * @param slots the map to add to
     * @return {@code slots}, for chaining
     */
    protected SlotMap addFurniture(SlotMap slots) {
        return slots
                .add(new Taburet())
                // read the cloth lazily: subclasses may set it after this map is built
                .add(new Chair(() -> furnitureCloth))
                .add(new BarStool(() -> furnitureCloth));
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return addFurniture(super.createDefaultDefinitions());
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);

        if (isFurniture(slot)) {
            // The furniture is made of (and copies its properties from) the slab, so it takes the plank color
            // rather than the log's. super() defaults everything but the planks to woodColor, so correct it here.
            blockDefinition.mapColor(plankColor);
        }
    }

    private static boolean isFurniture(SlotType slot) {
        return slot == SlotType.TABURET || slot == SlotType.CHAIR || slot == SlotType.BAR_STOOL;
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
