package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.furniture.slots.BarStool;
import org.betterx.bclib.furniture.slots.Chair;
import org.betterx.bclib.furniture.slots.Taburet;
import org.betterx.bclib.trait.block.WeightedBark;
import org.betterx.bclib.trait.block.WeightedLog;
import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.behaviour.FlammableBlockTrait;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class EndWoodenComplexMaterial extends WoodenBlockSet<EndWoodenComplexMaterial> implements MaterialManager.Material {
    private final MapColor plankColor;
    protected Block furnitureCloth = Blocks.WHITE_WOOL;
    private Block bark;
    private Block log;
    private int[] logVariantWeights;
    private int[] strippedVariantWeights;
    private boolean isRaft = false;

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

    /**
     * Opts this set's {@code LOG}/{@code BARK} slots into the weighted, multi-variant log/bark model (restoring the
     * randomized-log look lost in the wover migration). {@code weights.length} is the number of variants (base plus
     * hand-authored {@code _2..._length} models); {@code weights[0]} weights the base model, {@code weights[i]}
     * model {@code _<i+1>}. Must be called before {@link #buildAndRegister()}.
     *
     * @param weights the per-variant weights (e.g. {@code 1,1,1,1} for four equally-weighted variants, or
     *                {@code 16,1,16,1} for lucernia's dominant/rare pairing)
     */
    public EndWoodenComplexMaterial setLogVariants(int... weights) {
        this.logVariantWeights = weights;
        return this;
    }

    /**
     * Like {@link #setLogVariants}, but for the {@code STRIPPED_LOG}/{@code STRIPPED_BARK} slots.
     *
     * @param weights the per-variant weights for the stripped log/bark
     */
    public EndWoodenComplexMaterial setStrippedVariants(int... weights) {
        this.strippedVariantWeights = weights;
        return this;
    }

    /**
     * Marks this set's boat as a raft (e.g. vanilla's bamboo raft): rendered with {@code RaftRenderer}/
     * {@code RaftModel} and backed by a {@code Raft}/{@code ChestRaft} entity instead of {@code Boat}/
     * {@code ChestBoat}. Must be called before {@link #buildAndRegister()}, since that is when the slots are built.
     */
    public EndWoodenComplexMaterial useRaft() {
        this.isRaft = true;
        return this;
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        // WoodSlots.WALL is not part of the wover default set (vanilla has no wooden walls), so re-add it here
        // to restore BetterEnd's decorative wooden walls (e.g. dragon_tree_wall, end_lotus_wall) that the wover
        // migration dropped. The Wall slot builds a WallBlock textured with this set's planks and a plank-over-fence
        // recipe; village structures reference these blocks, and their lang entries were never removed.
        final SlotMap map = addFurniture(super.createDefaultDefinitions()).add(WoodSlots.WALL);
        if (logVariantWeights != null) {
            map.replace(new WeightedLog(true, logVariantWeights))
               .replace(new WeightedBark(true, logVariantWeights));
        }
        if (strippedVariantWeights != null) {
            map.replace(new WeightedLog(false, strippedVariantWeights))
               .replace(new WeightedBark(false, strippedVariantWeights));
        }
        if (isRaft) {
            map.remove(SlotType.BOAT)
               .remove(SlotType.CHEST_BOAT)
               .add(WoodSlots.RAFT)
               .add(WoodSlots.CHEST_RAFT);
        }
        return map;
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
    protected FlammableBlockTrait flammableTrait(SlotType slot) {
        // Vanilla differentiates fire behaviour by wood part rather than using one flat value for the
        // whole tree: logs/bark keep the generic default (5/5, matching e.g. oak_log/oak_wood), while every
        // planks-derived part of the set (planks, slabs, stairs, fences, doors, furniture, ...) catches and
        // spreads fire faster (5/20, matching oak_planks/oak_slab/oak_stairs/oak_fence). Leaves are not part
        // of this wood-set slot map (see LeavesBlockTrait for their own 30/60 value).
        return isLogSlot(slot) ? BlockTraits.FLAMMABLE.withDefault() : BlockTraits.FLAMMABLE.with(5, 20);
    }

    private static boolean isLogSlot(SlotType slot) {
        return slot == SlotType.LOG
                || slot == SlotType.BARK
                || slot == SlotType.STRIPPED_LOG
                || slot == SlotType.STRIPPED_BARK;
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
