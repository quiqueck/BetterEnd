package org.betterx.betterend.complexmaterials;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.BulbVineLanternBlock;
import org.betterx.betterend.blocks.BulbVineLanternColoredBlock;
import org.betterx.betterend.complexmaterials.types.*;
import org.betterx.betterend.item.material.ToolsWithHeadsSet;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.complex.api.equipment.ArmorTier;
import org.betterx.wover.complex.api.equipment.ToolTier;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFactory;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.slots.StoneSlots;
import org.betterx.wover.sets.api.blocks.slots.WoodSlots;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

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

public class MetalMaterial extends BlockSet<MetalMaterial> implements MaterialManager.Material {
    public static final SlotType TILE = new SlotType("tile");
    public static final SlotType ORE = new SlotType("ore");
    public static final SlotType BARS = new SlotType("bars");
    public static final SlotType CHAIN = new SlotType("chain");
    public static final SlotType CHANDELIER = new SlotType("chandelier");
    public static final SlotType BULB_LANTERN = new SlotType("bulb_lantern");
    public static final SlotType ANVIL = new SlotType("anvil");

    public static final SlotType INGOT = new SlotType("ingot");
    public static final SlotType NUGGET = new SlotType("nugget");

    public ColoredMaterial bulb_lantern_colored;

    public final Item rawOre;
    public final TagKey<Item> alloyingOre;
    public final boolean hasOre;

    public final ToolsWithHeadsSet equipment;

    private final Consumer<BlockDefinition<?, ?>> settingsSupplier;

    public static MetalMaterial makeNormal(
            String name,
            MapColor color,
            ToolTier material,
            ArmorTier armor,
            TagKey<Item> anvilTools,
            Supplier<SmithingTemplateItem> swordHandleTemplate
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
                swordHandleTemplate
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
                swordHandleTemplate
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
            Supplier<SmithingTemplateItem> swordHandleTemplate
    ) {
        super(BetterEnd.C, name, SlotType.SOURCE);
        this.settingsSupplier = settingsSupplier;
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
                new Chain(this),
                new Bars(this),
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
                def -> def.addTrait(ModCore.isDatagen() ? BulbVineLanternBlock.buildModel(null, null) : null)
                          .addTrait(BlockTraits.MINEABLE_WITH.needsPickAxe())
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