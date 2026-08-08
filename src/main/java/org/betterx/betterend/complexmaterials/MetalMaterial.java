package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.BulbVineLanternBlock;
import org.betterx.betterend.blocks.BulbVineLanternColoredBlock;
import org.betterx.betterend.complexmaterials.types.*;
import org.betterx.betterend.item.material.ToolsWithHeadsSet;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.complex.api.equipment.ArmorTier;
import de.ambertation.wover.complex.api.equipment.ToolTier;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.sets.api.blocks.*;
import de.ambertation.wover.sets.api.blocks.slots.MetalSlots;
import de.ambertation.wover.sets.api.blocks.slots.StoneSlots;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.tag.api.TagManager;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetalMaterial extends MetalBlockSet<MetalMaterial> implements MaterialManager.Material {
    public static final SlotType TILE = new SlotType("tile");
    public static final SlotType ORE = SlotType.ORE;
    public static final SlotType BARS = SlotType.BARS;
    public static final SlotType CHAIN = SlotType.CHAIN;
    public static final SlotType CHANDELIER = new SlotType("chandelier");
    public static final SlotType BULB_LANTERN = new SlotType("bulb_lantern");
    public static final SlotType ANVIL = new SlotType("anvil");

    public static final SlotType INGOT = SlotType.INGOT;
    public static final SlotType NUGGET = SlotType.NUGGET;

    public ColoredMaterial bulb_lantern_colored;

    public final Item rawOre;
    public final TagKey<Item> alloyingOre;
    public final boolean hasOre;

    public final ToolsWithHeadsSet equipment;

    private final Consumer<BlockDefinition<?, ?>> settingsSupplier;
    /** Vanilla-style harvest-tier gate ({@code needs_stone_tool}/{@code needs_iron_tool}/...) applied to
     *  every common block definition in this material's family (block, ore, tile, chain, bars, chandelier,
     *  bulb lantern, anvil, slab, stairs, door, trapdoor, pressure plate), or {@code null} for wood-tier. */
    private final @Nullable TagKey<Block> harvestTierTag;

    public static MetalMaterial makeNormal(
            String name,
            MapColor color,
            ToolTier material,
            ArmorTier armor,
            TagKey<Item> anvilTools,
            Supplier<SmithingTemplateItem> swordHandleTemplate
    ) {
        return makeNormal(name, color, material, armor, anvilTools, swordHandleTemplate, null);
    }

    public static MetalMaterial makeNormal(
            String name,
            MapColor color,
            ToolTier material,
            ArmorTier armor,
            TagKey<Item> anvilTools,
            Supplier<SmithingTemplateItem> swordHandleTemplate,
            @Nullable TagKey<Block> harvestTierTag
    ) {
        return new MetalMaterial(
                name,
                true,
                (def) -> def.mapColor(color),
                material,
                armor,
                Items.STICK,
                anvilTools,
                () -> EndTemplates.HANDLE_ATTACHMENT,
                swordHandleTemplate,
                harvestTierTag
        );
    }


    public static MetalMaterial makeOreless(
            String name,
            MapColor color,
            float hardness,
            float resistance,
            ToolTier material,
            ArmorTier armor,
            TagKey<Item> anvilTools,
            Supplier<SmithingTemplateItem> swordHandleTemplate
    ) {
        return makeOreless(name, color, hardness, resistance, material, armor, anvilTools, swordHandleTemplate, null);
    }

    public static MetalMaterial makeOreless(
            String name,
            MapColor color,
            float hardness,
            float resistance,
            ToolTier material,
            ArmorTier armor,
            TagKey<Item> anvilTools,
            Supplier<SmithingTemplateItem> swordHandleTemplate,
            @Nullable TagKey<Block> harvestTierTag
    ) {
        return new MetalMaterial(
                name,
                false,
                (def) -> def
                        .mapColor(color)
                        .destroyTime(hardness)
                        .explosionResistance(resistance),
                material,
                armor,
                Items.STICK,
                anvilTools,
                () -> EndTemplates.HANDLE_ATTACHMENT,
                swordHandleTemplate,
                harvestTierTag
        );
    }

    private MetalMaterial(
            String name,
            boolean hasOre,
            Consumer<BlockDefinition<?, ?>> settingsSupplier,
            ToolTier material,
            ArmorTier armor,
            Item handleItem,
            TagKey<Item> anvilTools,
            Supplier<SmithingTemplateItem> handleTemplate,
            Supplier<SmithingTemplateItem> swordHandleTemplate,
            @Nullable TagKey<Block> harvestTierTag
    ) {
        super(BetterEnd.C, name, SlotType.SOURCE);
        this.settingsSupplier = settingsSupplier;
        this.harvestTierTag = harvestTierTag;
        equipment = new ToolsWithHeadsSet(
                name, material, armor,
                handleItem, handleTemplate, swordHandleTemplate,
                true,
                anvilTools,
                null
        );


        this.hasOre = hasOre;
        alloyingOre = hasOre ? TagManager.ITEMS.makeTag(BetterEnd.C, name + "_alloying") : null;
        rawOre = hasOre
                ? EndItems.defineEndItem(name + "_raw")
                          .addTags(alloyingOre)
                          .buildAndRegister()
                : null;

        MaterialManager.register(this);
        this.buildAndRegister();
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);

        blockDefinition
                .addTrait(BlockTraits.METAL_BLOCK)
                .mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .strength(5.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.IRON);

        // Anvils are excluded: vanilla's own anvil/chipped_anvil/damaged_anvil carry no needs_*_tool tag
        // (mineable/pickaxe only, any tier works) and aeternium_anvil - the one anvil the mineable-tags
        // review does tier - gets it explicitly at its own registration site instead.
        if (harvestTierTag != null && !slot.equals(ANVIL)) {
            blockDefinition.addTags(harvestTierTag);
        }

        settingsSupplier.accept(blockDefinition);
    }

    private SlotFactory oreSlot() {
        if (!hasOre) {
            return null;
        }
        return new Ore(this);
    }


    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(
                new SourceBlock(this),
                oreSlot(),
                new Tile(this),
                MetalSlots.CHAIN,
                MetalSlots.BARS,
                Chandelier.SLOT,
                BulbLantern.SLOT,
                new Anvil(this.equipment.toolTier.level),
                StoneSlots.SLAB,
                StoneSlots.STAIRS,
                WoodSlots.DOOR,
                WoodSlots.TRAPDOOR,
                WoodSlots.PRESSURE_PLATE
        );
    }

    @Override
    public MetalMaterial buildAndRegister() {
        super.buildAndRegister();

        bulb_lantern_colored = new ColoredMaterial(
                BulbVineLanternColoredBlock::new,
                getBlock(BULB_LANTERN),
                false,
                def -> {
                    def.addTrait(ModCore.isDatagen() ? BulbVineLanternBlock.buildModel(null, null) : null)
                       .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe());
                    // The colored variants are built via replacePropertiesWithCopy(source), which does not
                    // carry tags - so the family's harvest-tier gate must be re-applied here explicitly,
                    // same as the base bulb_lantern gets it through addCommonBlockDefinitions.
                    if (harvestTierTag != null) {
                        def.addTags(harvestTierTag);
                    }
                },
                // ColoredMaterial attaches the CONST_COLOR tint itself; the lanterns only need their dye colour
                // brightened first (bulb_vine_lantern_bulb is near-grayscale and tinted via "tintindex": 0).
                BulbVineLanternColoredBlock::boostLanternColor
        );

        return this;
    }

    @Override
    public void registerRecipes(RecipeBuilder.Context context) {
        equipment.registerRecipes(context);

        if (hasOre) {
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_ingot_furnace_ore"), equipment.ingot)
                         .input(getBlock(ORE))
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_ingot_furnace_raw"), equipment.ingot)
                         .input(rawOre)
                         .build(context);
            BCLRecipeBuilder.alloying(BetterEnd.C.mk(this.baseName + "_ingot_alloy"), equipment.ingot)
                            .setInput(alloyingOre, alloyingOre)
                            .outputCount(3)
                            .setExperience(2.1F)
                            .build(context);
        }

        // Basic recipes
        RecipeBuilder.crafting(BetterEnd.C.mk(this.baseName + "_ingot_from_block"), equipment.ingot)
                     .outputCount(9)
                     .shapeless()
                     .addMaterial('#', getBaseBlock())
                     .group("end_metal_ingots")
                     .build(context);
    }

    @Override
    public void registerBlockTags(TagBootstrapContext<Block> context) {
    }

    @Override
    public void registerItemTags(ItemTagBootstrapContext context) {

    }

    @Override
    public @Nullable Item getItem(@NotNull SlotType type) {
        if (type.equals(NUGGET)) {
            return equipment.nugget;
        } else if (type.equals(INGOT)) {
            return equipment.ingot;
        }
        return super.getItem(type);
    }
}