package org.betterx.betterend.client.render;

import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.blocks.entities.FlowerPotBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.joml.Matrix4f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class FlowerPotItemRenderer<T extends FlowerPotBlockEntity> implements BlockEntityRenderer<T> {
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
    public void render(
            T blockEntity,
            float tickDelta,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay,
            Vec3 cameraPos
    ) {
        Level world = blockEntity.getLevel();
        if (world == null) return;

        BlockState state = world.getBlockState(blockEntity.getBlockPos());
        if (!(state.getBlock() instanceof FlowerPotBlock)) return;

        long seed = blockEntity.getBlockPos().asLong();

        java.util.Optional<Block> plantOpt = blockEntity.getPlantBlock();
        // A vanilla potted model brings its own pot + dirt, so drawing the BetterEnd soil
        // plane underneath it would duplicate/z-fight the soil. Suppress it in that case.
        boolean vanillaPotted = plantOpt.map(EndFlowerPotModels::hasVanillaPottedModel).orElse(false);

        if (!vanillaPotted) {
            blockEntity.getSoilBlock().ifPresent(soil -> renderSoilPlane(
                    soil, matrices, vertexConsumers, light, overlay, seed
            ));
        }
        plantOpt.ifPresent(plant -> renderPlantModel(
                plant, matrices, vertexConsumers, light, overlay
        ));
    }

    /**
     * Draws a single horizontal quad textured with the soil block's TOP sprite, seated
     * inside the pot cavity so it reads as a patch of dirt/moss filling the pot.
     */
    private void renderSoilPlane(
            Block soil,
            PoseStack matrices,
            MultiBufferSource buffers,
            int light,
            int overlay,
            long seed
    ) {
        BlockState soilState = soil.defaultBlockState();
        TextureAtlasSprite sprite = getTopSprite(soilState, seed);
        if (sprite == null) return;

        VertexConsumer vc = buffers.getBuffer(ItemBlockRenderTypes.getRenderType(soilState));
        PoseStack.Pose pose = matrices.last();
        Matrix4f m = pose.pose();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // Top face (normal +Y). Emitted double-sided so it is visible regardless of the
        // render type's backface culling.
        emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MAX, u0, v1, light, overlay, 1.0F);
        emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MAX, u1, v1, light, overlay, 1.0F);
        emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MIN, u1, v0, light, overlay, 1.0F);
        emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MIN, u0, v0, light, overlay, 1.0F);
        // Underside (normal -Y), reversed winding.
        emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MIN, u0, v0, light, overlay, -1.0F);
        emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MIN, u1, v0, light, overlay, -1.0F);
        emitVertex(vc, m, pose, INNER_MAX, SOIL_Y, INNER_MAX, u1, v1, light, overlay, -1.0F);
        emitVertex(vc, m, pose, INNER_MIN, SOIL_Y, INNER_MAX, u0, v1, light, overlay, -1.0F);
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
            int overlay,
            float ny
    ) {
        vc.addVertex(m, x, y, z)
          .setColor(1.0F, 1.0F, 1.0F, 1.0F)
          .setUv(u, v)
          .setOverlay(overlay)
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
            MultiBufferSource buffers,
            int light,
            int overlay
    ) {
        // Vanilla plants: draw the vanilla block/potted_<name> model, which is a complete
        // flower pot (pot + dirt + plant) authored to fill the 0..16 block, so render it at
        // full scale anchored at the block origin (no soil-plane seating).
        BlockStateModel vanillaPotted = EndFlowerPotModels.getVanillaPottedModel(plant);
        if (vanillaPotted != null) {
            renderVanillaPottedModel(vanillaPotted, plant, matrices, buffers, light, overlay);
            return;
        }

        BlockStateModel potted = EndFlowerPotModels.getPottedModel(plant);
        if (potted != null) {
            renderPottedModel(potted, plant, matrices, buffers, light, overlay);
        } else {
            renderFallbackModel(plant, matrices, buffers, light, overlay);
        }
    }

    /**
     * Draws a vanilla {@code block/potted_<name>} model. That model already contains the pot,
     * the dirt and the plant filling the whole block, so it is rendered at full scale with no
     * extra transform (and the BetterEnd soil plane is suppressed for it in {@link #render}).
     */
    private void renderVanillaPottedModel(
            BlockStateModel model,
            Block plant,
            PoseStack matrices,
            MultiBufferSource buffers,
            int light,
            int overlay
    ) {
        RenderType renderType = ItemBlockRenderTypes.getRenderType(plant.defaultBlockState());
        VertexConsumer vc = buffers.getBuffer(renderType);
        ModelBlockRenderer.renderModel(matrices.last(), vc, model, 1.0F, 1.0F, 1.0F, light, overlay);
    }

    /**
     * Draws the baked {@code _potted} model's quads directly into the pot, seated on the soil
     * plane. Uses the plant's normal render type so cutout (cross) plants render correctly.
     */
    private void renderPottedModel(
            BlockStateModel model,
            Block plant,
            PoseStack matrices,
            MultiBufferSource buffers,
            int light,
            int overlay
    ) {
        RenderType renderType = ItemBlockRenderTypes.getRenderType(plant.defaultBlockState());
        VertexConsumer vc = buffers.getBuffer(renderType);

        matrices.pushPose();
        // Seat the model base-center on the soil plane, scaled about that point.
        matrices.translate(0.5, SOIL_Y, 0.5);
        matrices.scale(PLANT_SCALE, PLANT_SCALE, PLANT_SCALE);
        matrices.translate(-0.5, 0.0, -0.5);
        ModelBlockRenderer.renderModel(matrices.last(), vc, model, 1.0F, 1.0F, 1.0F, light, overlay);
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
            MultiBufferSource buffers,
            int light,
            int overlay
    ) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        matrices.pushPose();
        // Seat the model base-center on the soil plane, scaled about that point.
        matrices.translate(0.5, SOIL_Y, 0.5);
        matrices.scale(FALLBACK_PLANT_SCALE, FALLBACK_PLANT_SCALE, FALLBACK_PLANT_SCALE);
        matrices.translate(-0.5, 0.0, -0.5);
        dispatcher.renderSingleBlock(plant.defaultBlockState(), matrices, buffers, light, overlay);
        matrices.popPose();
    }

    /**
     * Resolves the top-face sprite of a soil block. Falls back to the model's particle
     * sprite when the model has no UP-facing quad.
     */
    private static TextureAtlasSprite getTopSprite(BlockState soilState, long seed) {
        BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(soilState);
        if (model == null) return null;

        RandomSource random = RandomSource.create(seed);
        List<BlockModelPart> parts = model.collectParts(random);

        for (BlockModelPart part : parts) {
            List<BakedQuad> up = part.getQuads(Direction.UP);
            if (!up.isEmpty()) {
                return up.get(0).sprite();
            }
        }
        return model.particleIcon();
    }
}
