package org.betterx.betterend.blocks.entities;

import org.betterx.bclib.recipes.AlloyingRecipe;
import org.betterx.bclib.recipes.AlloyingRecipeInput;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.blocks.EndStoneSmelter;
import org.betterx.betterend.client.gui.EndStoneSmelterMenu;
import org.betterx.betterend.registry.EndBlockEntities;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import static net.minecraft.world.level.block.Block.UPDATE_ALL;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

// This smelter can handle different kinds of recipes. It is made for Alloying recipes,
// but it can also act as a regular furnace for blasting recipes. To simplify the code,
// we use a MultiRecipeInfo to hold both types of recipes and their inputs. And let
//proxy function decide witch recipe to use based on the input type.
record BlastingRecipeInfo(SingleRecipeInput input, RecipeHolder<? extends AbstractCookingRecipe> holder) {
    public int growsResultBy() {
        return 1;
    }

    public int burnTimeMultiplicator() {
        return 2;
    }

    public int cookTimeIncrement() {
        return 2;
    }
}

record AlloyingRecipeInfo(AlloyingRecipeInput input, RecipeHolder<? extends AlloyingRecipe> holder) {
    public int growsResultBy() {
        return 3;
    }

    public int burnTimeMultiplicator() {
        return 1;
    }

    public int cookTimeIncrement() {
        return 1;
    }
}

record InputState(ItemStack inputA, ItemStack inputB) {

    public ItemStack singleInput() {
        return inputA.isEmpty() ? inputB : inputA;
    }

    public boolean hasOneInput() {
        return (!inputA.isEmpty() && inputB.isEmpty()) || (inputA.isEmpty() && !inputB.isEmpty());
    }

    public boolean hasBothInputs() {
        return !inputA.isEmpty() && !inputB.isEmpty();
    }

    public boolean hasInput() {
        return hasOneInput() || hasBothInputs();
    }
}

class MultiRecipeInfo {
    public final BlastingRecipeInfo blasting;
    public final AlloyingRecipeInfo alloying;
    public final InputState inputState;

    private MultiRecipeInfo(InputState state, BlastingRecipeInfo blasting, AlloyingRecipeInfo alloying) {
        this.inputState = state;
        this.blasting = blasting;
        this.alloying = alloying;
    }

    public boolean isBlasting() {
        return blasting != null;
    }

    public int growsResultBy() {
        return isBlasting() ? blasting.growsResultBy() : alloying.growsResultBy();
    }

    public int burnTimeMultiplicator() {
        return isBlasting() ? blasting.burnTimeMultiplicator() : alloying.burnTimeMultiplicator();
    }

    public int cookTimeIncrement() {
        return isBlasting() ? blasting.cookTimeIncrement() : alloying.cookTimeIncrement();
    }

    public static MultiRecipeInfo of(
            InputState inputState,
            SingleRecipeInput singleRecipeInput,
            RecipeHolder<? extends AbstractCookingRecipe> recipeHolder
    ) {
        return new MultiRecipeInfo(
                inputState,
                new BlastingRecipeInfo(singleRecipeInput, recipeHolder),
                null
        );
    }

    public static MultiRecipeInfo of(
            InputState inputState,
            AlloyingRecipeInput alloyingRecipeInput,
            RecipeHolder<? extends AlloyingRecipe> recipeHolder
    ) {
        return new MultiRecipeInfo(
                inputState,
                null,
                new AlloyingRecipeInfo(alloyingRecipeInput, recipeHolder)
        );
    }
}

public class EndStoneSmelterBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(
            Recipe.KEY_CODEC,
            Codec.INT
    );
    private static final int[] TOP_SLOTS = new int[]{
            EndStoneSmelterMenu.INGREDIENT_SLOT_B,
            EndStoneSmelterMenu.INGREDIENT_SLOT_A
    };
    private static final int[] BOTTOM_SLOTS = new int[]{EndStoneSmelterMenu.FUEL_SLOT, EndStoneSmelterMenu.RESULT_SLOT};
    private static final int[] SIDE_SLOTS = new int[]{
            EndStoneSmelterMenu.FUEL_SLOT
    };
    private static final Map<Item, Integer> AVAILABLE_FUELS = Maps.newHashMap();

    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed;
    private final RecipeManager.CachedCheck<AlloyingRecipeInput, ? extends AlloyingRecipe> quickCheckAlloying;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheckBlastFurnace;
    protected NonNullList<ItemStack> inventory;
    protected final ContainerData propertyDelegate;
    private RecipeHolder<?> lastRecipe;
    private int cookingTotalTime;
    private int cookingTimer;
    private int litTimeRemaining;
    private int litTotalTime;

    public EndStoneSmelterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EndBlockEntities.END_STONE_SMELTER, blockPos, blockState);
        this.quickCheckAlloying = RecipeManager.createCheck(AlloyingRecipe.TYPE);
        this.quickCheckBlastFurnace = RecipeManager.createCheck(RecipeType.BLASTING);
        this.inventory = NonNullList.withSize(EndStoneSmelterMenu.SLOT_COUNT, ItemStack.EMPTY);
        this.recipesUsed = new Reference2IntOpenHashMap<>();
        this.propertyDelegate = new ContainerData() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> EndStoneSmelterBlockEntity.this.litTimeRemaining;
                    case 1 -> EndStoneSmelterBlockEntity.this.litTotalTime;
                    case 2 -> EndStoneSmelterBlockEntity.this.cookingTimer;
                    case 3 -> EndStoneSmelterBlockEntity.this.cookingTotalTime;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> EndStoneSmelterBlockEntity.this.litTimeRemaining = value;
                    case 1 -> EndStoneSmelterBlockEntity.this.litTotalTime = value;
                    case 2 -> EndStoneSmelterBlockEntity.this.cookingTimer = value;
                    case 3 -> EndStoneSmelterBlockEntity.this.cookingTotalTime = value;
                }
            }

            public int getCount() {
                return 4;
            }
        };
    }

    private boolean isLit() {
        return litTimeRemaining > 0;
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator<ItemStack> iterator = inventory.iterator();
        ItemStack itemStack;
        do {
            if (!iterator.hasNext()) {
                return true;
            }
            itemStack = iterator.next();
        } while (itemStack.isEmpty());

        return false;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(inventory, slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory, slot);
    }


    private static int getTotalCookTime(
            MultiRecipeInfo recipeInfo
    ) {
        if (recipeInfo.alloying != null && recipeInfo.alloying.holder() != null) {
            return recipeInfo.alloying.holder().value().getSmeltTime();
        } else if (recipeInfo.blasting != null && recipeInfo.blasting.holder() != null) {
            return recipeInfo.blasting.holder().value().cookingTime() / 3;
        }
        return 200;
    }

    private void setLitState(boolean lit) {
        if (level == null || this.worldPosition == null || this.getBlockState() == null) return;
        this.level.setBlock(this.worldPosition, this.getBlockState().setValue(EndStoneSmelter.LIT, lit), 3);
        this.setBlockState(this.getBlockState().setValue(EndStoneSmelter.LIT, lit));

    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemStack = inventory.get(slot);
        boolean stackValid = !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemStack);
        inventory.set(slot, stack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));

        if ((slot == EndStoneSmelterMenu.INGREDIENT_SLOT_A || slot == EndStoneSmelterMenu.INGREDIENT_SLOT_B) && !stackValid) {
            if (level instanceof ServerLevel serverLevel) {
                cookingTotalTime = getSmeltTime(serverLevel);
                cookingTimer = 0;
                setChanged();
            }
        }
    }

    protected int getSmeltTime(@NotNull ServerLevel level) {
        final AlloyingRecipeInput input = new AlloyingRecipeInput(
                this.inventory.get(EndStoneSmelterMenu.INGREDIENT_SLOT_A),
                this.inventory.get(EndStoneSmelterMenu.INGREDIENT_SLOT_B)
        );
        int smeltTime = level.recipeAccess()
                             .getRecipeFor(
                                     AlloyingRecipe.TYPE,
                                     input,
                                     level
                             )
                             .map(r -> r.value().getSmeltTime())
                             .orElse(0);
        if (smeltTime == 0) {
            smeltTime = level.recipeAccess()
                             .getRecipeFor(
                                     RecipeType.BLASTING,
                                     new SingleRecipeInput(input.any()),
                                     level
                             )
                             .map(r -> r.value().cookingTime())
                             .orElse(200);
            smeltTime = (int) (smeltTime / 1.5f);
        }
        return smeltTime;
    }


    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel serverLevel, Vec3 position) {
        List<RecipeHolder<?>> list = Lists.newArrayList();
        for (var entry : recipesUsed.reference2IntEntrySet()) {
            serverLevel.recipeAccess().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                if (recipe.value() instanceof AlloyingRecipe alloying) {
                    dropExperience(
                            serverLevel, position, entry.getIntValue(),
                            alloying.experience()
                    );
                } else {
                    BlastingRecipe blasting = (BlastingRecipe) recipe.value();
                    dropExperience(
                            serverLevel, position, entry.getIntValue(),
                            blasting.experience()
                    );
                }
            });
        }

        return list;
    }

    public void dropExperience(ServerPlayer serverPlayer) {
        List<RecipeHolder<?>> list = this.getRecipesToAwardAndPopExperience(
                serverPlayer.level(),
                serverPlayer.position()
        );
        serverPlayer.awardRecipes(list);

        for (RecipeHolder<?> recipeHolder : list) {
            if (recipeHolder != null) {
                serverPlayer.triggerRecipeCrafted(recipeHolder, this.inventory);
            }
        }

        recipesUsed.clear();
    }


    private void dropExperience(ServerLevel serverLevel, Vec3 position, int count, float amount) {
        int expTotal = Mth.floor(count * amount);
        float extraXPChance = Mth.frac(count * amount);
        if (extraXPChance != 0.0F && Math.random() < extraXPChance) {
            expTotal++;
        }

        ExperienceOrb.award(serverLevel, position, expTotal);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (level != null && level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(
                worldPosition.getX() + 0.5D,
                worldPosition.getY() + 0.5D,
                worldPosition.getZ() + 0.5D
        ) <= 64.0D;
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super.preRemoveSideEffects(blockPos, blockState);
        if (this.level instanceof ServerLevel serverLevel) {
            this.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(blockPos));
        }
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable(String.format("block.%s.%s", BetterEnd.MOD_ID, EndStoneSmelter.ID));
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.inventory = nonNullList;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory playerInventory) {
        return new EndStoneSmelterMenu(syncId, playerInventory, this, propertyDelegate);
    }

    public static void serverTick(
            ServerLevel tickLevel,
            BlockPos tickPos,
            BlockState tickState,
            EndStoneSmelterBlockEntity smelterEntity
    ) {
        final boolean initialBurning = smelterEntity.isLit();
        boolean didChange = false;
        if (initialBurning) {
            smelterEntity.litTimeRemaining--;
        }

        final ItemStack fuelStack = smelterEntity.inventory.get(EndStoneSmelterMenu.FUEL_SLOT);
        final boolean hasFuel = !fuelStack.isEmpty() && canUseAsFuel(tickLevel, fuelStack);

        final InputState inputState = new InputState(
                smelterEntity.inventory.get(EndStoneSmelterMenu.INGREDIENT_SLOT_A),
                smelterEntity.inventory.get(EndStoneSmelterMenu.INGREDIENT_SLOT_B)
        );

        if (initialBurning || hasFuel && inputState.hasInput()) {
            MultiRecipeInfo recipeInfo;
            int maxStackSize = smelterEntity.getMaxStackSize();
            if (inputState.hasBothInputs()) {
                final var recipeInput = new AlloyingRecipeInput(inputState.inputA(), inputState.inputB());
                recipeInfo = MultiRecipeInfo.of(
                        inputState, recipeInput,
                        smelterEntity.quickCheckAlloying.getRecipeFor(recipeInput, tickLevel).orElse(null)
                );
            } else {
                final var recipeInput = new SingleRecipeInput(inputState.singleInput());
                recipeInfo = MultiRecipeInfo.of(
                        inputState, recipeInput,
                        smelterEntity.quickCheckBlastFurnace.getRecipeFor(recipeInput, tickLevel).orElse(null)
                );
            }

            if (!smelterEntity.isLit()
                    && canBurn(tickLevel.registryAccess(), recipeInfo, smelterEntity.inventory, maxStackSize)
            ) {
                smelterEntity.litTimeRemaining = getBurnDuration(tickLevel.fuelValues(), fuelStack, recipeInfo);
                smelterEntity.litTotalTime = smelterEntity.litTimeRemaining;
                if (smelterEntity.isLit()) {
                    didChange = true;
                    if (hasFuel) {
                        Item item = fuelStack.getItem();
                        fuelStack.shrink(1);
                        if (fuelStack.isEmpty()) {
                            var remainder = item.getCraftingRemainder();
                            smelterEntity.inventory.set(
                                    EndStoneSmelterMenu.FUEL_SLOT,
                                    remainder == null ? ItemStack.EMPTY : remainder.create()
                            );
                        }
                    }
                }
            }

            if (smelterEntity.isLit() && canBurn(
                    tickLevel.registryAccess(),
                    recipeInfo,
                    smelterEntity.inventory,
                    maxStackSize
            )) {
                smelterEntity.cookingTimer += recipeInfo.cookTimeIncrement();
                if (smelterEntity.cookingTimer >= smelterEntity.cookingTotalTime) {
                    smelterEntity.cookingTimer = 0;
                    smelterEntity.cookingTotalTime = getTotalCookTime(recipeInfo);
                    var burnRecipeUsed = burn(tickLevel.registryAccess(), recipeInfo, smelterEntity.inventory);
                    if (burnRecipeUsed != null) {
                        smelterEntity.setRecipeUsed(burnRecipeUsed);
                    }

                    didChange = true;
                }
            } else {
                smelterEntity.cookingTimer = 0;
            }
        } else if (!smelterEntity.isLit() && smelterEntity.cookingTimer > 0) {
            smelterEntity.cookingTimer = Mth.clamp(smelterEntity.cookingTimer - 2, 0, smelterEntity.cookingTotalTime);
        }

        if (initialBurning != smelterEntity.isLit()) {
            didChange = true;
            tickState = tickState.setValue(AbstractFurnaceBlock.LIT, smelterEntity.isLit());
            tickLevel.setBlock(tickPos, tickState, UPDATE_ALL);
        }

        if (didChange) {
            setChanged(tickLevel, tickPos, tickState);
        }
    }

    private static RecipeHolder<?> burn(
            RegistryAccess access,
            MultiRecipeInfo recipeInfo,
            NonNullList<ItemStack> inventory
    ) {
        if (recipeInfo.isBlasting()) return burn(access, recipeInfo.inputState, recipeInfo.blasting, inventory);
        return burn(access, recipeInfo.inputState, recipeInfo.alloying, inventory);
    }

    private static RecipeHolder<?> burn(
            RegistryAccess access,
            InputState inputState,
            AlloyingRecipeInfo recipeInfo,
            NonNullList<ItemStack> inventory
    ) {
        if (recipeInfo != null) {
            ItemStack resultItem
                    = recipeInfo.holder().value().assemble(recipeInfo.input());
            createResultItem(inventory, resultItem, recipeInfo.growsResultBy());

            inputState.inputA().shrink(1);
            inputState.inputB().shrink(1);

            return recipeInfo.holder();
        } else {
            return null;
        }
    }

    private static RecipeHolder<?> burn(
            RegistryAccess access,
            InputState inputState,
            BlastingRecipeInfo recipeInfo,
            NonNullList<ItemStack> inventory
    ) {
        if (recipeInfo != null) {
            ItemStack resultItem = recipeInfo.holder().value().assemble(recipeInfo.input());
            createResultItem(inventory, resultItem, recipeInfo.growsResultBy());

            if (inputState.singleInput().is(Blocks.WET_SPONGE.asItem())
                    && !inventory.get(EndStoneSmelterMenu.FUEL_SLOT).isEmpty()
                    && inventory.get(EndStoneSmelterMenu.FUEL_SLOT).is(Items.BUCKET)) {
                inventory.set(EndStoneSmelterMenu.FUEL_SLOT, new ItemStack(Items.WATER_BUCKET));
            }

            inputState.singleInput().shrink(1);
            return recipeInfo.holder();
        } else {
            return null;
        }
    }

    private static void createResultItem(NonNullList<ItemStack> inventory, ItemStack resultItem, int growBy) {
        ItemStack storedResults = inventory.get(EndStoneSmelterMenu.RESULT_SLOT);
        if (storedResults.isEmpty()) {
            inventory.set(EndStoneSmelterMenu.RESULT_SLOT, resultItem.copy());
        } else if (ItemStack.isSameItemSameComponents(storedResults, resultItem)) {
            storedResults.grow(growBy);
        }
    }

    private static boolean canBurn(
            RegistryAccess lookup,
            MultiRecipeInfo recipeInfo,
            NonNullList<ItemStack> inventory,
            int maxStackSize
    ) {
        if (recipeInfo.isBlasting())
            return canBurn(lookup, recipeInfo.inputState, recipeInfo.blasting, inventory, maxStackSize);
        return canBurn(lookup, recipeInfo.inputState, recipeInfo.alloying, inventory, maxStackSize);
    }

    private static boolean canBurn(
            RegistryAccess lookup,
            InputState inputState,
            AlloyingRecipeInfo recipeInfo,
            NonNullList<ItemStack> inventory,
            int maxStackSize
    ) {
        if (recipeInfo.holder() != null && inputState.hasBothInputs()) {
            ItemStack resultItem = recipeInfo.holder().value().assemble(recipeInfo.input());
            return canStore(inventory, maxStackSize, resultItem, 3);
        } else {
            return false;
        }
    }

    private static boolean canBurn(
            RegistryAccess lookup,
            InputState inputState,
            BlastingRecipeInfo recipeInfo,
            NonNullList<ItemStack> inventory,
            int maxStackSize
    ) {
        if (recipeInfo.holder() != null && inputState.hasOneInput()) {
            ItemStack resultItem = recipeInfo.holder().value().assemble(recipeInfo.input());
            return canStore(inventory, maxStackSize, resultItem, 1);
        } else {
            return false;
        }
    }

    private static boolean canStore(
            NonNullList<ItemStack> inventory,
            int maxStackSize,
            ItemStack resultItem,
            int growBy
    ) {
        if (resultItem.isEmpty()) {
            return false;
        } else {
            ItemStack storedResults = inventory.get(EndStoneSmelterMenu.RESULT_SLOT);
            if (storedResults.isEmpty()) {
                return true;
            } else if (!ItemStack.isSameItemSameComponents(storedResults, resultItem)) {
                return false;
            } else {
                int newCount = storedResults.getCount() + growBy;
                return newCount < maxStackSize
                        && newCount < storedResults.getMaxStackSize()
                        || newCount < resultItem.getMaxStackSize();
            }
        }
    }

    @Override
    public void fillStackedContents(@NotNull StackedItemContents finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.accountStack(itemStack);
        }
    }

    @Override
    public void setRecipeUsed(RecipeHolder<?> recipe) {
        if (recipe != null) {
            recipesUsed.addTo(recipe.id(), 1);
            lastRecipe = recipe;
        }
    }

    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return this.lastRecipe;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return switch (side) {
            case DOWN -> BOTTOM_SLOTS;
            case UP -> TOP_SLOTS;
            default -> SIDE_SLOTS;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction dir) {
        if (dir == Direction.DOWN && slot == EndStoneSmelterMenu.FUEL_SLOT) {
            return stack.is(Items.WATER_BUCKET) || stack.getItem() == Items.BUCKET;
        }
        return true;
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(valueInput, inventory);
        litTimeRemaining = valueInput.getShortOr("BurnTime", (short) 0);
        litTotalTime = valueInput.getShortOr("FuelTime", (short) 0);
        cookingTimer = valueInput.getShortOr("SmeltTime", (short) 200);
        cookingTotalTime = valueInput.getShortOr("SmeltTimeTotal", (short) 0);

        this.recipesUsed.clear();
        try {
            this.recipesUsed.putAll(valueInput.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
        } catch (Exception e) {
            BetterEnd.LOGGER.error("Failed to load recipes used for End Stone Smelter at " + this.worldPosition, e);
        }
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

        valueOutput.putShort("BurnTime", (short) litTimeRemaining);
        valueOutput.putShort("FuelTime", (short) litTotalTime);
        valueOutput.putShort("SmeltTime", (short) cookingTimer);
        valueOutput.putShort("SmeltTimeTotal", (short) cookingTotalTime);
        ContainerHelper.saveAllItems(valueOutput, inventory);
        valueOutput.store("RecipesUsed", RECIPES_USED_CODEC, this.recipesUsed);
    }

    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (slot == EndStoneSmelterMenu.RESULT_SLOT) {
            return false;
        } else if (slot != EndStoneSmelterMenu.FUEL_SLOT) {
            return true;
        }
        ItemStack itemStack = this.inventory.get(EndStoneSmelterMenu.FUEL_SLOT);
        return canUseAsFuel(this.level, stack)
                || stack.is(Items.BUCKET) && !itemStack.is(Items.BUCKET);
    }

    public static boolean canUseAsFuel(Level level, ItemStack fuelStack) {
        return AVAILABLE_FUELS.containsKey(fuelStack.getItem())
                || level.fuelValues().burnDuration(fuelStack) > 2000;
    }

    static int getBurnDuration(FuelValues fuelValues, ItemStack fuelStack, MultiRecipeInfo recipeInfo) {
        if (fuelStack.isEmpty()) return 0;

        return AVAILABLE_FUELS.getOrDefault(
                fuelStack.getItem(),
                fuelValues.burnDuration(fuelStack)
        ) * recipeInfo.burnTimeMultiplicator();
    }

    public static void registerFuel(ItemLike fuel, int time) {
        AVAILABLE_FUELS.put(fuel.asItem(), time);
    }

    public static Map<Item, Integer> availableFuels() {
        return AVAILABLE_FUELS;
    }
}
