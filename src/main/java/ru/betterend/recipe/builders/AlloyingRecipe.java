package ru.betterend.recipe.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.level.Level;
import ru.betterend.BetterEnd;
import ru.betterend.config.Configs;
import ru.betterend.interfaces.BetterEndRecipe;
import ru.betterend.recipe.EndRecipeManager;
import ru.betterend.registry.EndBlocks;
import ru.betterend.util.ItemUtil;
import ru.betterend.util.RecipeHelper;

public class AlloyingRecipe implements Recipe<Inventory>, BetterEndRecipe {

	public final static String GROUP = "alloying";
	public final static RecipeType<AlloyingRecipe> TYPE = EndRecipeManager.registerType(GROUP);
	public final static Serializer SERIALIZER = EndRecipeManager.registerSerializer(GROUP, new Serializer());
	public final static ResourceLocation ID = BetterEnd.makeID(GROUP);

	protected final RecipeType<?> type;
	protected final ResourceLocation id;
	protected final Ingredient primaryInput;
	protected final Ingredient secondaryInput;
	protected final ItemStack output;
	protected final String group;
	protected final float experience;
	protected final int smeltTime;

	public AlloyingRecipe(ResourceLocation id, String group, Ingredient primaryInput, Ingredient secondaryInput,
			ItemStack output, float experience, int smeltTime) {
		this.group = group;
		this.id = id;
		this.primaryInput = primaryInput;
		this.secondaryInput = secondaryInput;
		this.output = output;
		this.experience = experience;
		this.smeltTime = smeltTime;
		this.type = TYPE;
	}

	public float getExperience() {
		return this.experience;
	}

	public int getSmeltTime() {
		return this.smeltTime;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(primaryInput);
		defaultedList.add(secondaryInput);

		return defaultedList;
	}

	@Override
	public boolean matches(Inventory inv, Level world) {
		return this.primaryInput.test(inv.getStack(0)) && this.secondaryInput.test(inv.getStack(1))
				|| this.primaryInput.test(inv.getStack(1)) && this.secondaryInput.test(inv.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return this.output.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return this.type;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public String getGroup() {
		return this.group;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(EndBlocks.END_STONE_SMELTER);
	}

	public static class Builder {
		private final static Builder INSTANCE = new Builder();
		private static boolean exist;

		public static Builder create(ResourceLocation id) {
			INSTANCE.id = id;
			INSTANCE.group = String.format("%s_%s", GROUP, id);
			INSTANCE.primaryInput = null;
			INSTANCE.secondaryInput = null;
			INSTANCE.output = null;
			INSTANCE.experience = 0.0F;
			INSTANCE.smeltTime = 350;
			exist = Configs.RECIPE_CONFIG.getBoolean("alloying", id.getPath(), true);

			return INSTANCE;
		}

		public static Builder create(String id) {
			return create(BetterEnd.makeID(id));
		}

		private ResourceLocation id;
		private Ingredient primaryInput;
		private Ingredient secondaryInput;
		private ItemStack output;
		private String group;
		private float experience;
		private int smeltTime;
		private boolean alright = true;

		private Builder() {
		}

		public Builder setGroup(String group) {
			this.group = group;
			return this;
		}

		public Builder setPrimaryInput(ItemLike... inputs) {
			for (ItemLike item : inputs) {
				this.alright &= RecipeHelper.exists(item);
			}
			this.primaryInput = Ingredient.of(inputs);
			return this;
		}

		public Builder setSecondaryInput(ItemLike... inputs) {
			for (ItemLike item : inputs) {
				this.alright &= RecipeHelper.exists(item);
			}
			this.secondaryInput = Ingredient.of(inputs);
			return this;
		}

		public Builder setPrimaryInput(Tag<Item> input) {
			this.primaryInput = Ingredient.fromTag(input);
			return this;
		}

		public Builder setSecondaryInput(Tag<Item> input) {
			this.secondaryInput = Ingredient.fromTag(input);
			return this;
		}

		public Builder setInput(ItemLike primaryInput, ItemLike secondaryInput) {
			this.setPrimaryInput(primaryInput);
			this.setSecondaryInput(secondaryInput);
			return this;
		}

		public Builder setInput(Tag<Item> primaryInput, Tag<Item> secondaryInput) {
			this.setPrimaryInput(primaryInput);
			this.setSecondaryInput(secondaryInput);
			return this;
		}

		public Builder setOutput(ItemLike output, int amount) {
			this.alright &= RecipeHelper.exists(output);
			this.output = new ItemStack(output, amount);
			return this;
		}

		public Builder setExpiriense(float amount) {
			this.experience = amount;
			return this;
		}

		public Builder setSmeltTime(int time) {
			this.smeltTime = time;
			return this;
		}

		public void build() {
			if (exist) {
				if (primaryInput == null) {
					BetterEnd.LOGGER.warning(
							"Primary input for Alloying recipe can't be 'null', recipe {} will be ignored!", id);
					return;
				}
				if (secondaryInput == null) {
					BetterEnd.LOGGER.warning("Secondary input for Alloying can't be 'null', recipe {} will be ignored!",
							id);
					return;
				}
				if (output == null) {
					BetterEnd.LOGGER.warning("Output for Alloying can't be 'null', recipe {} will be ignored!", id);
					return;
				}
				if (EndRecipeManager.getRecipe(TYPE, id) != null) {
					BetterEnd.LOGGER.warning("Can't add Alloying recipe! Id {} already exists!", id);
					return;
				}
				if (!alright) {
					BetterEnd.LOGGER.debug("Can't add Alloying recipe {}! Ingeredient or output not exists.", id);
					return;
				}
				EndRecipeManager.addRecipe(TYPE,
						new AlloyingRecipe(id, group, primaryInput, secondaryInput, output, experience, smeltTime));
			}
		}
	}

	public static class Serializer implements RecipeSerializer<AlloyingRecipe> {
		@Override
		public AlloyingRecipe read(ResourceLocation id, JsonObject json) {
			JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
			Ingredient primaryInput = Ingredient.fromJson(ingredients.get(0));
			Ingredient secondaryInput = Ingredient.fromJson(ingredients.get(1));
			String group = JsonHelper.getString(json, "group", "");
			JsonObject result = JsonHelper.getObject(json, "result");
			ItemStack output = ItemUtil.fromJsonRecipe(result);
			if (output == null) {
				throw new IllegalStateException("Output item does not exists!");
			}
			float experience = JsonHelper.getFloat(json, "experience", 0.0F);
			int smeltTime = JsonHelper.getInt(json, "smelttime", 350);

			return new AlloyingRecipe(id, group, primaryInput, secondaryInput, output, experience, smeltTime);
		}

		@Override
		public AlloyingRecipe read(ResourceLocation id, PacketByteBuf packetBuffer) {
			String group = packetBuffer.readString(32767);
			Ingredient primary = Ingredient.fromPacket(packetBuffer);
			Ingredient secondary = Ingredient.fromPacket(packetBuffer);
			ItemStack output = packetBuffer.readItemStack();
			float experience = packetBuffer.readFloat();
			int smeltTime = packetBuffer.readVarInt();

			return new AlloyingRecipe(id, group, primary, secondary, output, experience, smeltTime);
		}

		@Override
		public void write(PacketByteBuf packetBuffer, AlloyingRecipe recipe) {
			packetBuffer.writeString(recipe.group);
			recipe.primaryInput.write(packetBuffer);
			recipe.secondaryInput.write(packetBuffer);
			packetBuffer.writeItemStack(recipe.output);
			packetBuffer.writeFloat(recipe.experience);
			packetBuffer.writeVarInt(recipe.smeltTime);
		}
	}
}
