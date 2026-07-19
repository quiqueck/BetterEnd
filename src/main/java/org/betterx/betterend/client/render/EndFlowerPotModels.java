package org.betterx.betterend.client.render;

import org.betterx.betterend.BetterEnd;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Registers BetterEnd's standalone {@code block/<plant>_potted} models so that the vanilla
 * baking pipeline actually bakes them (nothing references these models from a blockstate or
 * item, so they would otherwise be dropped), and exposes a lookup for the flower-pot renderer.
 *
 * <p>The set of potted models is discovered by scanning the resource pack for
 * {@code assets/betterend/models/block/*_potted.json} rather than hard-coding it, so the list
 * stays in sync with whatever art actually ships. Only plants that have a dedicated potted
 * model get a key; everything else resolves to {@code null} and the renderer falls back to the
 * plant's ground model.
 */
@Environment(EnvType.CLIENT)
public final class EndFlowerPotModels {
    private static final String MODELS_PREFIX = "models/";
    private static final String MODELS_DIR = "models/block";
    private static final String POTTED_SUFFIX = "_potted";
    private static final String JSON_SUFFIX = ".json";

    private static final String MINECRAFT_NAMESPACE = "minecraft";
    private static final String VANILLA_POTTED_PREFIX = "potted_";
    // Vanilla plants whose potted block/model is NOT named potted_<blockname>.
    // (block id -> potted block/model id)
    private static final Map<String, String> VANILLA_POTTED_OVERRIDES = Map.of(
            "azalea", "potted_azalea_bush",
            "flowering_azalea", "potted_flowering_azalea_bush"
    );

    // Fully-qualified potted model id (e.g. betterend:block/amber_grass_potted) -> baking key.
    private static final Map<ResourceLocation, ExtraModelKey<BlockStateModel>> POTTED_KEYS = new HashMap<>();

    private EndFlowerPotModels() {
    }

    /**
     * Hooks the potted-model discovery into Fabric's model-loading pipeline. Call from client init.
     */
    public static void register() {
        PreparableModelLoadingPlugin.register(EndFlowerPotModels::scan, EndFlowerPotModels::initialize);
    }

    // Runs off-thread during resource reload: collect every betterend "_potted" block model id.
    private static CompletableFuture<Set<ResourceLocation>> scan(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Set<ResourceLocation> ids = new HashSet<>();
            resourceManager.listResources(
                    MODELS_DIR,
                    loc -> loc.getNamespace().equals(BetterEnd.C.modId)
                            && loc.getPath().endsWith(POTTED_SUFFIX + JSON_SUFFIX)
            ).keySet().forEach(loc -> {
                // betterend:models/block/foo_potted.json -> betterend:block/foo_potted
                String path = loc.getPath();
                path = path.substring(MODELS_PREFIX.length(), path.length() - JSON_SUFFIX.length());
                ids.add(ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), path));
            });
            return ids;
        }, executor);
    }

    // Runs on the model-loading thread: register each discovered model for baking.
    private static void initialize(Set<ResourceLocation> ids, ModelLoadingPlugin.Context context) {
        POTTED_KEYS.clear();
        for (ResourceLocation id : ids) {
            ExtraModelKey<BlockStateModel> key = ExtraModelKey.create(id::toString);
            context.addModel(key, SimpleUnbakedExtraModel.blockStateModel(id));
            POTTED_KEYS.put(id, key);
        }
    }

    /**
     * Resolves the baked {@code _potted} model for a seated plant block following the naming
     * convention {@code betterend:<path>} -> {@code betterend:block/<path>_potted}.
     *
     * @return the baked potted model, or {@code null} when the plant has no dedicated potted
     * model (or the model manager is not ready), signalling the renderer to use the ground fallback.
     */
    public static BlockStateModel getPottedModel(Block plant) {
        if (plant == null) return null;

        ResourceLocation plantId = BuiltInRegistries.BLOCK.getKey(plant);
        if (plantId == null) return null;

        ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                plantId.getNamespace(), "block/" + plantId.getPath() + POTTED_SUFFIX
        );
        ExtraModelKey<BlockStateModel> key = POTTED_KEYS.get(modelId);
        if (key == null) return null;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getModelManager() == null) return null;

        return ((FabricBakedModelManager) mc.getModelManager()).getModel(key);
    }

    /**
     * Resolves the baked vanilla {@code minecraft:block/potted_<name>} model for a vanilla
     * flower-pot plant.
     *
     * <p>Unlike BetterEnd's {@code _potted} models (bare plant geometry), a vanilla potted
     * model already contains the whole flower pot (pot walls + dirt + plant). There is no
     * public API in 1.21.8 to fetch a stand-alone {@code block/...} model by
     * {@link ResourceLocation}; those models are only baked as the blockstate model of the
     * corresponding {@code potted_*} block. We therefore resolve it through the block-model
     * shaper via that potted block's default state (it references exactly
     * {@code block/potted_<name>}).
     *
     * @return the baked vanilla potted model, or {@code null} when the plant is not a vanilla
     * block, has no {@code potted_*} counterpart, or the renderer is not ready.
     */
    public static BlockStateModel getVanillaPottedModel(Block plant) {
        // Intentionally disabled: vanilla potted_* models (e.g. potted_dandelion -> flower_pot_cross,
        // potted_cactus) bake the WHOLE vanilla terracotta flower pot + dirt, not just the plant - drawing
        // them inside BetterEnd's pot produces a pot-inside-a-pot. So vanilla plants fall through to the
        // scaled ground-model fallback (FALLBACK_PLANT_SCALE) on the BetterEnd soil plane, like any other
        // plant without a dedicated cross-only _potted model.
        return null;
    }

    /**
     * Whether the given plant will be drawn from a vanilla potted model. Such a model already
     * includes its own pot and dirt, so the renderer must suppress the BetterEnd soil plane
     * for it.
     */
    public static boolean hasVanillaPottedModel(Block plant) {
        return getVanillaPottedModel(plant) != null;
    }
}
