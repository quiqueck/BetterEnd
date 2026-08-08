package org.betterx.betterend.item.material;

import org.betterx.bclib.recipes.BCLRecipeBuilder;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndItems;
import org.betterx.betterend.registry.EndTemplates;
import de.ambertation.wover.complex.api.equipment.*;
import de.ambertation.wover.item.api.trait.ItemRecipeTrait;
import de.ambertation.wover.item.api.trait.ItemTraits;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolsWithHeadsSet extends EquipmentSet {
    public final TagKey<Item> anvilTools;
    public final Item forgedPlate;
    public final Item nugget;
    public final Item ingot;


    public final Item shovelHead;
    public final Item pickaxeHead;
    public final Item axeHead;
    public final Item hoeHead;
    public final Item swordBlade;
    public final Item swordHandle;
    public final Item hammerHead;
    public final Item spearTip;

    public final Supplier<SmithingTemplateItem> handleTemplate;
    public final Supplier<SmithingTemplateItem> swordHandleTemplate;

    protected final boolean withNuggets;

    public ToolsWithHeadsSet(
            @NotNull String baseName,
            @NotNull ToolTier toolTier,
            @NotNull ArmorTier armorTier,
            @NotNull ItemLike handleItem,
            @NotNull Supplier<SmithingTemplateItem> handleTemplate,
            @NotNull Supplier<SmithingTemplateItem> swordHandleTemplate,
            boolean withNuggets,
            TagKey<Item> anvilTools,
            @Nullable Supplier<EquipmentSet> templateBaseSet
    ) {
        super(BetterEnd.C, baseName, toolTier, armorTier, handleItem, templateBaseSet);
        this.anvilTools = anvilTools;
        this.withNuggets = withNuggets;
        if (withNuggets) {
            nugget = EndItems.registerEndItem(baseName + "_nugget");
        } else {
            nugget = null;
        }

        ingot = EndItems.defineEndItem(baseName + "_ingot")
                        .addTags(ItemTags.BEACON_PAYMENT_ITEMS)
                        .buildAndRegister();

        forgedPlate = EndItems
                .defineEndItem(baseName + "_forged_plate")
                .addTags(toolTier.toolMaterial.repairItems())
                .buildAndRegister();

        this.handleTemplate = handleTemplate;
        this.swordHandleTemplate = swordHandleTemplate;

        shovelHead = EndItems.defineEndItem(baseName + "_shovel_head")
                             .addTrait(ItemTraits.RECIPE_ITEM.with(buildHeadRecipe(1)))
                             .buildAndRegister();
        pickaxeHead = EndItems.defineEndItem(baseName + "_pickaxe_head")
                              .addTrait(ItemTraits.RECIPE_ITEM.with(buildHeadRecipe(3)))
                              .buildAndRegister();
        axeHead = EndItems.defineEndItem(baseName + "_axe_head")
                          .addTrait(ItemTraits.RECIPE_ITEM.with(buildHeadRecipe(3)))
                          .buildAndRegister();
        hoeHead = EndItems.defineEndItem(baseName + "_hoe_head")
                          .addTrait(ItemTraits.RECIPE_ITEM.with(buildHeadRecipe(1)))
                          .buildAndRegister();
        swordBlade = EndItems.defineEndItem(baseName + "_sword_blade")
                             .addTrait(ItemTraits.RECIPE_ITEM.with(buildHeadRecipe(2)))
                             .buildAndRegister();
        swordHandle = EndItems.defineEndItem(baseName + "_sword_handle")
                              .addTrait(ItemTraits.RECIPE_ITEM.with((key, item, context) -> {
                                  // Queried here rather than when the trait is created, so that the value is read
                                  // once the set is fully constructed.
                                  if (!hasAutoSwordHandleRecipe()) return;

                                  RecipeBuilder
                                          .smithing(key.identifier(), item)
                                          .template(swordHandleTemplate.get())
                                          .base(handleItem)
                                          .addon(ingot)
                                          .build(context);
                              }))
                              .buildAndRegister();
        hammerHead = EndItems.defineEndItem(baseName + "_hammer_head")
                             .addTrait(ItemTraits.RECIPE_ITEM.with(buildHeadRecipe(3)))
                             .buildAndRegister();
        spearTip = EndItems.defineEndItem(baseName + "_spear_tip")
                           .addTrait(ItemTraits.RECIPE_ITEM.with(buildHeadRecipe(2)))
                           .buildAndRegister();

        add(
                ToolSlot.PICKAXE_SLOT,
                ItemTraits.RECIPE_ITEM.with(
                        (key, item, context) -> RecipeBuilder
                                .smithing(key.identifier(), item)
                                .template(handleTemplate.get())
                                .base(pickaxeHead)
                                .addon(handleItem)
                                .build(context)
                )
        );
        add(
                ToolSlot.AXE_SLOT,
                ItemTraits.RECIPE_ITEM.with(
                        (key, item, context) -> RecipeBuilder
                                .smithing(key.identifier(), item)
                                .template(handleTemplate.get())
                                .base(axeHead)
                                .addon(handleItem)
                                .build(context)
                )
        );
        add(
                ToolSlot.SHOVEL_SLOT,
                ItemTraits.RECIPE_ITEM.with(
                        (key, item, context) -> RecipeBuilder
                                .smithing(key.identifier(), item)
                                .template(handleTemplate.get())
                                .base(shovelHead)
                                .addon(handleItem)
                                .build(context)
                )
        );
        add(
                ToolSlot.HOE_SLOT,
                ItemTraits.RECIPE_ITEM.with(
                        (key, item, context) -> RecipeBuilder
                                .smithing(key.identifier(), item)
                                .template(handleTemplate.get())
                                .base(hoeHead)
                                .addon(handleItem)
                                .build(context)
                )
        );
        add(
                ToolSlot.HAMMER_SLOT,
                ItemTraits.RECIPE_ITEM.with(
                        (key, item, context) -> RecipeBuilder.smithing(key.identifier(), item)
                                                             .template(handleTemplate.get())
                                                             .base(hammerHead)
                                                             .addon(handleItem)
                                                             .build(context)
                )
        );
        add(
                ToolSlot.SWORD_SLOT,
                ItemTraits.RECIPE_ITEM.with(
                        (key, item, context) -> RecipeBuilder
                                .smithing(key.identifier(), item)
                                .template(EndTemplates.TOOL_ASSEMBLY)
                                .base(swordBlade)
                                .addon(swordHandle)
                                .build(context)
                )
        );
        add(
                ToolSlot.SPEAR_SLOT,
                ItemTraits.RECIPE_ITEM.with(
                        (key, item, context) -> RecipeBuilder
                                .smithing(key.identifier(), item)
                                .template(handleTemplate.get())
                                .base(spearTip)
                                .addon(handleItem)
                                .build(context)
                )
        );


        buildArmor();
    }

    protected void buildArmor() {
        add(ArmorSlot.HELMET_SLOT);
        add(ArmorSlot.CHESTPLATE_SLOT);
        add(ArmorSlot.LEGGINGS_SLOT);
        add(ArmorSlot.BOOTS_SLOT);
    }

    public void registerRecipes(@NotNull RecipeBuilder.Context context) {
        if (withNuggets) {
            RecipeBuilder.crafting(BetterEnd.C.mk(this.baseName + "_ingot_from_nuggets"), ingot)
                         .shape("###", "###", "###")
                         .addMaterial('#', nugget)
                         .group("end_metal_nugget")
                         .build(context);
            RecipeBuilder.crafting(BetterEnd.C.mk(this.baseName + "_nuggets_from_ingot"), nugget)
                         .outputCount(9)
                         .shapeless()
                         .addMaterial('#', ingot)
                         .group("end_metal_ingot")
                         .build(context);

            // Tools & armor into nuggets
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_axe_nugget"), nugget)
                         .input(get(ToolSlot.AXE_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_hoe_nugget"), nugget)
                         .input(get(ToolSlot.HOE_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_pickaxe_nugget"), nugget)
                         .input(get(ToolSlot.PICKAXE_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_sword_nugget"), nugget)
                         .input(get(ToolSlot.SWORD_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_spear_nugget"), nugget)
                         .input(get(ToolSlot.SPEAR_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_hammer_nugget"), nugget)
                         .input(get(ToolSlot.HAMMER_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_helmet_nugget"), nugget)
                         .input(get(ArmorSlot.HELMET_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_chestplate_nugget"), nugget)
                         .input(get(ArmorSlot.CHESTPLATE_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_leggings_nugget"), nugget)
                         .input(get(ArmorSlot.LEGGINGS_SLOT).asItem())
                         .build(context);
            RecipeBuilder.blasting(BetterEnd.C.mk(this.baseName + "_boots_nugget"), nugget)
                         .input(get(ArmorSlot.BOOTS_SLOT).asItem())
                         .build(context);
        }
    }


    /**
     * Whether the head/blade anvil recipes are derived from the items' recipe traits.
     * <p>
     * A set whose head recipes are hand-written in a recipe provider must return {@code false} here: otherwise
     * the trait and the provider both claim the same recipe path, and which file ships is decided by provider
     * order rather than by design.
     *
     * @return {@code true} to derive the head recipes from the trait
     */
    protected boolean hasAutoHeadRecipes() {
        return true;
    }

    /**
     * Whether the sword handle recipe is derived from the item's recipe trait.
     * <p>
     * The derived recipe forges the set's own handle item with the set's own ingot under
     * {@link #swordHandleTemplate}. A set that is instead forged from the ingot of a *lower* tier must return
     * {@code false} here and let a recipe provider own the recipe, or both would claim the same recipe path.
     *
     * @return {@code true} to derive the sword handle recipe from the trait
     */
    protected boolean hasAutoSwordHandleRecipe() {
        return true;
    }

    private ItemRecipeTrait.RecipeFactory buildHeadRecipe(int inputCount) {
        return (key, item, context) -> {
            // Queried here rather than when the trait is created, so that the value is read once the set is
            // fully constructed.
            if (!hasAutoHeadRecipes()) return;

            BCLRecipeBuilder
                    .anvil(key.identifier(), item)
                    .setPrimaryInput(ingot)
                    .setInputCount(inputCount)
                    .setAnvilLevel(toolTier.level)
                    .setAllowedTools(this.anvilTools)
                    .setDamage(toolTier.level)
                    .build(context);
        };
    }
}
