package org.betterx.betterend.recipe.builders;

import org.betterx.bclib.interfaces.UnknownReceipBookCategory;
import org.betterx.bclib.recipes.BCLBaseRecipeBuilder;
import org.betterx.bclib.recipes.BCLRecipeManager;
import org.betterx.bclib.util.ItemStackCodec;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.rituals.InfusionRitual;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.item.api.ItemStackHelper;
import de.ambertation.wover.recipe.api.BaseRecipeBuilder;
import de.ambertation.wover.recipe.api.BaseUnlockableRecipeBuilder;
import de.ambertation.wover.recipe.impl.CraftingRecipeBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfusionRecipe implements Recipe<InfusionRitual.InfusionInput>, UnknownReceipBookCategory {
    public final static String GROUP = "infusion";
    public static final RecipeBookCategory INFUSION_CATEGORY = BCLRecipeManager.registerCategory(BetterEnd.C.mk(GROUP));
    public final static RecipeType<InfusionRecipe> TYPE = BCLRecipeManager.registerType(BetterEnd.MOD_ID, GROUP);
    public final static RecipeSerializer<InfusionRecipe> SERIALIZER = BCLRecipeManager.registerSerializer(
            BetterEnd.MOD_ID,
            GROUP,
            new RecipeSerializer<>(Serializer.CODEC, Serializer.STREAM_CODEC)
    );

    private final Ingredient[] catalysts;
    final private Ingredient input;
    final private Item outputItem;
    final private int outputCount;
    final private DataComponentPatch outputPatch;
    final private int time;
    final private String group;
    private PlacementInfo placementInfo;

    private InfusionRecipe(
            Ingredient input,
            Item outputItem,
            int outputCount,
            DataComponentPatch outputPatch,
            Ingredient[] catalysts,
            int time,
            String group
    ) {
        this.input = input;
        this.outputItem = outputItem;
        this.outputCount = outputCount;
        this.outputPatch = outputPatch;
        this.catalysts = catalysts;
        this.time = time;
        this.group = group;
    }

    public static Builder create(String id, ItemLike output) {
        return create(BetterEnd.C.mk(id), output);
    }

    public static Builder create(Identifier id, ItemLike output) {
        return new BuilderImpl(id, output);
    }

    public static Builder create(String id, ItemStack output) {
        return create(BetterEnd.C.mk(id), output);
    }

    public static Builder create(Identifier id, ItemStack output) {
        return new BuilderImpl(id, output);
    }

    public static Builder create(
            String id,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        return create(BetterEnd.C.mk(id), enchantment, level, lookup);
    }

    public static Builder create(
            Identifier id,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        BuilderImpl builder = new BuilderImpl(id, Items.ENCHANTED_BOOK);
        builder.outputPatch = enchantmentPatch(enchantment, level, lookup);
        return builder;
    }

    // Builds the ENCHANTMENTS component patch directly (rather than via EnchantmentHelper.
    // updateEnchantments(ItemStack, ...), which needs a real ItemStack) - recipe *building*
    // (including datagen) runs before DataComponents are bound onto registry Holders, so
    // constructing an ItemStack here would fail the same way a bare `new ItemStack(item)` does.
    private static DataComponentPatch enchantmentPatch(
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(EnchantmentUtils.getEnchantment(lookup, enchantment), level);
        // Items.ENCHANTED_BOOK stores its enchantments under STORED_ENCHANTMENTS, not ENCHANTMENTS
        // (that's vanilla's own EnchantmentHelper.getComponentType(ItemStack) distinction - this
        // factory is only ever used to build enchanted books, so it's hardcoded here).
        return DataComponentPatch.builder().set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable()).build();
    }

    public int getInfusionTime() {
        return this.time;
    }

    public Ingredient getInput() {
        return this.input;
    }

    /**
     * Indexed by {@link CatalystSlot#index} (compass position around the pedestal ring); entries
     * are {@code null} for unused slots.
     */
    public Ingredient[] getCatalysts() {
        return this.catalysts;
    }

    @Override
    public boolean matches(InfusionRitual.InfusionInput inv, Level world) {
        boolean valid = this.input.test(inv.getItem(0));
        if (!valid) return false;
        for (int i = 0; i < 8; i++) {
            valid &= Ingredient.testOptionalIngredient(Optional.ofNullable(this.catalysts[i]), inv.getItem(i + 1));
        }
        return valid;
    }

    @Override
    public @NotNull ItemStack assemble(InfusionRitual.InfusionInput recipeInput) {
        ItemStack stack = new ItemStack(outputItem, outputCount);
        if (!outputPatch.isEmpty()) stack.applyComponents(outputPatch);
        return ItemStackHelper.callItemStackSetupIfPossible(stack);
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public @NotNull String group() {
        return this.group;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<InfusionRitual.InfusionInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<InfusionRitual.InfusionInput>> getType() {
        return TYPE;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(input);
            for (Ingredient catalyst : catalysts) {
                if (catalyst != null) {
                    ingredients.add(catalyst);
                }
            }
            this.placementInfo = PlacementInfo.create(ingredients);
        }

        return this.placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return INFUSION_CATEGORY;
    }

    public interface Builder extends BaseRecipeBuilder<Builder>, BaseUnlockableRecipeBuilder<Builder> {
        Builder group(@Nullable String group);

        Builder setPrimaryInput(ItemLike... inputs);
        Builder setPrimaryInput(TagKey<Item> input);
        Builder setPrimaryInputAndUnlock(TagKey<Item> input);
        Builder setPrimaryInputAndUnlock(ItemLike... inputs);

        Builder setTime(int time);
        Builder addCatalyst(CatalystSlot slot, ItemLike... items);
        Builder addCatalyst(CatalystSlot slot, ItemStack stack);
        Builder addCatalyst(CatalystSlot slot, TagKey<Item> tag);
    }

    public static class BuilderImpl extends BCLBaseRecipeBuilder<Builder, InfusionRecipe> implements Builder {
        private final CraftingRecipeBuilderImpl.IngredientFactory[] catalysts;
        private int time;
        private DataComponentPatch outputPatch = DataComponentPatch.EMPTY;

        protected BuilderImpl(Identifier id, ItemLike output) {
            // Not new ItemStack(output, 1): recipe *building* (including datagen) runs before
            // DataComponents are bound onto registry Holders, and every ItemStack constructor
            // touches that binding - super(id, output, false) only reads output.asItem().
            super(id, output, false);
            this.catalysts = new CraftingRecipeBuilderImpl.IngredientFactory[]{
                    ctx -> null, ctx -> null, ctx -> null, ctx -> null,
                    ctx -> null, ctx -> null, ctx -> null, ctx -> null
            };
            this.time = 1;
        }

        protected BuilderImpl(Identifier id, ItemStack output) {
            super(id, output, false);
            this.catalysts = new CraftingRecipeBuilderImpl.IngredientFactory[]{
                    ctx -> null, ctx -> null, ctx -> null, ctx -> null,
                    ctx -> null, ctx -> null, ctx -> null, ctx -> null
            };
            this.time = 1;
            this.outputPatch = output.getComponentsPatch();
        }


        public Builder setTime(int time) {
            this.time = time;
            return this;
        }

        public Builder addCatalyst(CatalystSlot slot, ItemLike... items) {
            this.catalysts[slot.index] = ctx -> Ingredient.of(items);
            return this;
        }

        public Builder addCatalyst(CatalystSlot slot, ItemStack stack) {
            this.catalysts[slot.index] = ctx -> Ingredient.of(stack.getItem());
            return this;
        }

        public Builder addCatalyst(CatalystSlot slot, TagKey<Item> tag) {
            this.catalysts[slot.index] = ctx -> ctx.tag(tag);
            return this;
        }

        @Override
        protected void validate() {
            super.validate();
            if (time < 0) {
                throwIllegalStateException(
                        "Time should be positive, recipe {} will be ignored!"
                );
            }
        }

        @Override
        protected InfusionRecipe createRecipe(de.ambertation.wover.recipe.api.RecipeBuilder.Context ctx) {
            Ingredient[] resolvedCatalysts = new Ingredient[catalysts.length];
            for (int i = 0; i < catalysts.length; i++) {
                resolvedCatalysts[i] = catalysts[i].createIngredient(ctx);
            }
            return new InfusionRecipe(
                    this.primaryInput.createIngredient(ctx),
                    outputItem,
                    outputCount,
                    outputPatch,
                    resolvedCatalysts,
                    this.time,
                    this.group
            );
        }
    }


    public enum CatalystSlot implements StringRepresentable {
        NORTH(0, "north"),
        NORTH_EAST(1, "north_east"),
        EAST(2, "east"),
        SOUTH_EAST(3, "south_east"),
        SOUTH(4, "south"),
        SOUTH_WEST(5, "south_west"),
        WEST(6, "west"),
        NORTH_WEST(7, "north_west");

        public static final Codec<CatalystSlot> CODEC = StringRepresentable.fromEnum(CatalystSlot::values);

        public final int index;
        public final String key;

        CatalystSlot(int index, String key) {
            this.index = index;
            this.key = key;
        }

        @Override
        public @NotNull String getSerializedName() {
            return key;
        }
    }

    public static class Serializer {
        public static @NotNull InfusionRecipe fromNetwork(RegistryFriendlyByteBuf packetBuffer) {
            final Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(packetBuffer);
            final Item outputItem = ItemStackCodec.ITEM_STREAM_CODEC.decode(packetBuffer);
            final int outputCount = packetBuffer.readVarInt();
            final DataComponentPatch outputPatch = DataComponentPatch.STREAM_CODEC.decode(packetBuffer);
            final String group = packetBuffer.readUtf();
            final int time = packetBuffer.readVarInt();
            final Ingredient[] catalysts = new Ingredient[8];
            for (int i = 0; i < 8; i++) {
                catalysts[i] = Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.decode(packetBuffer).orElse(null);
            }
            return new InfusionRecipe(input, outputItem, outputCount, outputPatch, catalysts, time, group);
        }

        public static void toNetwork(RegistryFriendlyByteBuf packetBuffer, InfusionRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(packetBuffer, recipe.input);
            ItemStackCodec.ITEM_STREAM_CODEC.encode(packetBuffer, recipe.outputItem);
            packetBuffer.writeVarInt(recipe.outputCount);
            DataComponentPatch.STREAM_CODEC.encode(packetBuffer, recipe.outputPatch);
            packetBuffer.writeUtf(recipe.group);
            packetBuffer.writeVarInt(recipe.time);
            for (int i = 0; i < 8; i++) {
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.encode(packetBuffer, Optional.ofNullable(recipe.catalysts[i]));
            }
        }

        public static final MapCodec<Ingredient[]> CODEC_CATALYSTS = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.NORTH.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.NORTH.index])),
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.NORTH_EAST.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.NORTH_EAST.index])),
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.EAST.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.EAST.index])),
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.SOUTH_EAST.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.SOUTH_EAST.index])),
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.SOUTH.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.SOUTH.index])),
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.SOUTH_WEST.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.SOUTH_WEST.index])),
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.WEST.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.WEST.index])),
                        Ingredient.CODEC
                                .optionalFieldOf(CatalystSlot.NORTH_WEST.key)
                                .forGetter(catalysts -> Optional.ofNullable(catalysts[CatalystSlot.NORTH_WEST.index]))
                )
                .apply(
                        instance,
                        (n, ne, e, se, s, sw, w, nw) -> new Ingredient[]{
                                n.orElse(null), ne.orElse(null), e.orElse(null), se.orElse(null),
                                s.orElse(null), sw.orElse(null), w.orElse(null), nw.orElse(null)
                        }
                ));

        // Item + count + an optional component patch (only ever populated by the enchanted-book
        // recipes) - not an ItemStack, since recipe *building* (including datagen) runs before
        // DataComponents are bound onto registry Holders, and every ItemStack constructor touches
        // that binding.
        private record Result(Item item, int count, DataComponentPatch patch) {
            static final MapCodec<Result> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(Result::item),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(Result::count),
                    DataComponentPatch.CODEC
                            .optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(Result::patch)
            ).apply(instance, Result::new));
        }

        public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                Result.CODEC
                      .fieldOf("result")
                      .forGetter(recipe -> new Result(recipe.outputItem, recipe.outputCount, recipe.outputPatch)),
                CODEC_CATALYSTS.fieldOf("catalysts").forGetter(recipe -> recipe.catalysts),
                Codec.INT.optionalFieldOf("time", 1).forGetter(recipe -> recipe.time),
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group)
        ).apply(instance, (input, result, catalysts, time, group) -> new InfusionRecipe(
                input, result.item(), result.count(), result.patch(), catalysts, time, group
        )));

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.of(InfusionRecipe.Serializer::toNetwork, InfusionRecipe.Serializer::fromNetwork);
    }

    public static void register() {
        //we call this to make sure that TYPE is initialized
        // SERIALIZER's own registration also declares it for client sync (see
        // BCLRecipeManager.registerSerializer), which is what lets the infusion recipe book list
        // anything on a dedicated server.
    }
}
