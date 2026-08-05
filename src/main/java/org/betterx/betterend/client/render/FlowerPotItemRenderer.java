package org.betterx.betterend.client.render;

import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.blocks.entities.FlowerPotBlockEntity;
import org.betterx.betterend.client.render.state.FlowerPotRenderState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class FlowerPotItemRenderer implements BlockEntityRenderer<FlowerPotBlockEntity, FlowerPotRenderState> {
    // Pot interior, derived from assets/betterend/models/block/flower_pot.json.
    // The inner-wall element ("Box14", from [11,8,11] to [5,1,5]) hollows the cavity to
    // x/z in [5,11] and y in [1,8] (block/16 units). The base box top sits at y=1 and the
    // rim top at y=8, so the soil plane is placed near the rim to make the pot look filled.
    private static final float INNER_MIN = 5.0F / 16.0F;   // 0.3125
    private static final float INNER_MAX = 11.0F / 16.0F;  // 0.6875
    private static final float SOIL_Y = 7.0F / 16.0F;      // 0.4375 (just below the 8/16 rim)
    // A plant block is 1x1x1; scale it down and seat its base on the soil plane.
    // PLANT_SCALE is used for the dedicated block/cross "_potted" models.
    private static final float PLANT_SCALE = 0.85F;
    // FALLBACK_PLANT_SCALE is used for plants that have NO dedicated "_potted" model and are
    // drawn from their full ground model instead. Slightly larger so the ground model still fills
    // the pot; tune after a live check.
    private static final float FALLBACK_PLANT_SCALE = 0.7F;

    public FlowerPotItemRenderer(BlockEntityRendererProvider.Context ctx) {
        super();
    }

    @Override
    public FlowerPotRenderState createRenderState() {
        return new FlowerPotRenderState();
    }

    @Override
    public void extractRenderState(
            FlowerPotBlockEntity blockEntity,
            FlowerPotRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, breakProgress);

        Level world = blockEntity.getLevel();
        if (world == null) {
            state.plant = null;
            state.soil = null;
            return;
        }

        BlockState blockState = world.getBlockState(blockEntity.getBlockPos());
        if (!(blockState.getBlock() instanceof FlowerPotBlock)) {
            state.plant = null;
            state.soil = null;
            return;
        }

        state.plant = blockEntity.getPlantBlock().orElse(null);
        state.soil = blockEntity.getSoilBlock().orElse(null);
        // A vanilla potted model brings its own pot + dirt, so drawing the BetterEnd soil
        // plane underneath it would duplicate/z-fight the soil. Suppress it in that case.
        state.vanillaPotted = state.plant != null && EndFlowerPotModels.hasVanillaPottedModel(state.plant);
        state.seed = blockEntity.getBlockPos().asLong();
    }

    @Override
    public void submit(
            FlowerPotRenderState state,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState
    ) {
        if (!state.vanillaPotted && state.soil != null) {
            renderSoilPlane(state.soil, matrices, submitNodeCollector, state.lightCoords, state.seed);
        }
        if (state.plant != null) {
            renderPlantModel(state.plant, matrices, submitNodeCollector, state.lightCoords);
        }
    }

    /**
     * Draws a single horizontal quad textured with the soil block's TOP sprite, seated
     * inside the pot cavity so it reads as a patch of dirt/moss filling the pot.
     */
    private void renderSoilPlane(
            Block soil,
            PoseStack matrices,
            SubmitNodeCollector collector,
            int light,
            long seed
    ) {
        List<BlockStateModelPart> parts = collectModelParts(soil.defaultBlockState(), seed);
        if (parts.isEmpty()) return;

        TextureAtlasSprite sprite = topSprite(parts);
        RenderType renderType = firstRenderType(parts);
        if (sprite == null || renderType == null) return;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        collector.submitCustomGeometry(matrices, renderType, (pose, vc) -> {
            Matrix4f m = pose.pose();

            // Top face (normal +Y). Emitted double-sided so it is visible regardless of the
            // render type's backface culling.
            emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MAX, u0, v1, light, 1.0F);
            emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MAX, u1, v1, light, 1.0F);
            emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MIN, u1, v0, light, 1.0F);
            emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MIN, u0, v0, light, 1.0F);
            // Underside (normal -Y), reversed winding.
            emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MIN, u0, v0, light, -1.0F);
            emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MIN, u1, v0, light, -1.0F);
            emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MAX, u1, v1, light, -1.0F);
            emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MAX, u0, v1, light, -1.0F);
        });
    }

    private static void emitVertex(
            VertexConsumer vc,
            Matrix4f m,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            int light,
            float ny
    ) {
        vc.addVertex(m, x, y, z)
          .setColor(1.0F, 1.0F, 1.0F, 1.0F)
          .setUv(u, v)
          .setOverlay(OverlayTexture.NO_OVERLAY)
          .setLight(light)
          .setNormal(pose, 0.0F, ny, 0.0F);
    }

    /**
     * Renders the seated plant. Prefers the plant's dedicated {@code _potted} model (a
     * {@code block/cross} tuned to sit in a pot); if no such model exists (or it is not baked),
     * falls back to the plant's full ground model so nothing regresses. In both cases the model is
     * scaled down and seated base-centered on the soil plane so the plant looks like it is growing.
     */
    private void renderPlantModel(
            Block plant,
            PoseStack matrices,
            SubmitNodeCollector collector,
            int light
    ) {
        // Vanilla plants: draw the vanilla block/potted_<name> model, which is a complete
        // flower pot (pot + dirt + plant) authored to fill the 0..16 block, so render it at
        // full scale anchored at the block origin (no soil-plane seating).
        BlockStateModel vanillaPotted = EndFlowerPotModels.getVanillaPottedModel(plant);
        if (vanillaPotted != null) {
            renderBakedModel(vanillaPotted, plant, matrices, collector, light, false);
            return;
        }

        BlockStateModel potted = EndFlowerPotModels.getPottedModel(plant);
        if (potted != null) {
            renderBakedModel(potted, plant, matrices, collector, light, true);
        } else {
            renderFallbackModel(plant, matrices, collector, light);
        }
    }

    /**
     * Draws the baked model's quads directly. When {@code seatOnSoilPlane} is set the model is
     * scaled down and seated base-centered on the soil plane; otherwise it is drawn at full scale
     * anchored at the block origin (used for complete vanilla potted models). Uses the model's own
     * render type so cutout (cross) plants render correctly.
     */
    private void renderBakedModel(
            BlockStateModel model,
            Block plant,
            PoseStack matrices,
            SubmitNodeCollector collector,
            int light,
            boolean seatOnSoilPlane
    ) {
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(plant.hashCode()), parts);
        if (parts.isEmpty()) return;

        RenderType renderType = firstRenderType(parts);
        if (renderType == null) return;

        matrices.pushPose();
        if (seatOnSoilPlane) {
            // Seat the model base-center on the soil plane, scaled about that point.
            matrices.translate(0.5, SOIL_Y, 0.5);
            matrices.scale(PLANT_SCALE, PLANT_SCALE, PLANT_SCALE);
            matrices.translate(-0.5, 0.0, -0.5);
        }
        collector.submitBlockModel(
                matrices, renderType, parts, BlockModelRenderState.EMPTY_TINTS, light, OverlayTexture.NO_OVERLAY, 0
        );
        matrices.popPose();
    }

    /**
     * Fallback for the plants without a dedicated {@code _potted} model: render the plant's full
     * ground model, scaled down and seated centered on the soil plane so it fits the pot rather
     * than looking ground-placed.
     */
    private void renderFallbackModel(
            Block plant,
            PoseStack matrices,
            SubmitNodeCollector collector,
            int light
    ) {
        BlockModelRenderState renderState = new BlockModelRenderState();
        Minecraft.getInstance().blockModelResolver.update(
                renderState, plant.defaultBlockState(), BlockDisplayContext.create()
        );

        matrices.pushPose();
        // Seat the model base-center on the soil plane, scaled about that point.
        matrices.translate(0.5, SOIL_Y, 0.5);
        matrices.scale(FALLBACK_PLANT_SCALE, FALLBACK_PLANT_SCALE, FALLBACK_PLANT_SCALE);
        matrices.translate(-0.5, 0.0, -0.5);
        renderState.submit(matrices, collector, light, OverlayTexture.NO_OVERLAY, 0);
        matrices.popPose();
    }

    /**
     * Resolves the baked model parts of a block state, seeded the same way the vanilla block
     * renderer would (so weighted multipart/variant selection stays stable per position).
     */
    private static List<BlockStateModelPart> collectModelParts(BlockState state, long seed) {
        BlockStateModel model = Minecraft.getInstance()
                                          .getModelManager()
                                          .getBlockStateModelSet()
                                          .get(state);
        if (model == null) return List.of();

        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(seed), parts);
        return parts;
    }

    /**
     * Resolves the top-face sprite of a set of model parts. Falls back to the first part's
     * particle sprite when none of them have an UP-facing quad.
     */
    private static TextureAtlasSprite topSprite(List<BlockStateModelPart> parts) {
        for (BlockStateModelPart part : parts) {
            List<BakedQuad> up = part.getQuads(Direction.UP);
            if (!up.isEmpty()) {
                return up.get(0).materialInfo().sprite();
            }
        }
        return parts.isEmpty() ? null : parts.get(0).particleMaterial().sprite();
    }

    /**
     * Resolves the render type used by a set of model parts, read off the first quad found (all
     * quads of a single-material model share the same render type).
     */
    private static RenderType firstRenderType(List<BlockStateModelPart> parts) {
        for (BlockStateModelPart part : parts) {
            List<BakedQuad> unculled = part.getQuads(null);
            if (!unculled.isEmpty()) return unculled.get(0).materialInfo().itemRenderType();
            for (Direction direction : Direction.values()) {
                List<BakedQuad> quads = part.getQuads(direction);
                if (!quads.isEmpty()) return quads.get(0).materialInfo().itemRenderType();
            }
        }
        return null;
    }
}
