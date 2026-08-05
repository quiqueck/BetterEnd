package org.betterx.betterend.client.render;

import org.betterx.betterend.registry.block.EndFunctionalBlocks;
import org.betterx.betterend.registry.item.EndResourceItems;
import de.ambertation.wunderlib.ui.ColorHelper;
import org.betterx.betterend.blocks.EternalPedestal;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.entities.PedestalBlockEntity;
import org.betterx.betterend.blocks.entities.EternalPedestalEntity;
import org.betterx.betterend.client.effects.EternalHint;
import org.betterx.betterend.client.effects.InfusionHint;
import org.betterx.betterend.client.render.state.PedestalRenderState;
import org.betterx.betterend.recipe.builders.InfusionRecipe;
import org.betterx.betterend.rituals.InfusionRitual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PedestalItemRenderer implements BlockEntityRenderer<PedestalBlockEntity, PedestalRenderState> {
    /** Warm gold for sockets still waiting for a pedestal, dimmed once one is standing there. */
    private static final int SOCKET_MISSING_RGB = 0xFFE9A0;
    private static final int SOCKET_PLACED_RGB = 0xC9A94E;
    /**
     * North is called out in purple. Every recipe layout is written relative to it, so without a marked
     * side the ring is eight interchangeable positions and a player has no way to align what the
     * recipe book shows with what is in front of them.
     */
    private static final int SOCKET_NORTH_MISSING_RGB = 0xC58BFF;
    private static final int SOCKET_NORTH_PLACED_RGB = 0x8A5FC9;
    /** Runed-flavolite violet, so a gap in the portal frame reads as the block it is asking for. */
    private static final int FRAME_MISSING_RGB = 0xB98BE8;
    /** Ceiling on the ghost crystals' opacity - they are a vision, not six real crystals. */
    private static final float CRYSTAL_GHOST_ALPHA = 0.55F;

    private final ItemModelResolver itemModelResolver;

    public PedestalItemRenderer(BlockEntityRendererProvider.Context ctx) {
        super();
        this.itemModelResolver = ctx.itemModelResolver();
    }

    @Override
    public PedestalRenderState createRenderState() {
        return new PedestalRenderState();
    }

    @Override
    public void extractRenderState(
            PedestalBlockEntity blockEntity,
            PedestalRenderState state,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, breakProgress);

        Level world = blockEntity.getLevel();
        extractInfusionHint(world, blockEntity.getBlockPos(), state, partialTick);
        extractEternalVision(world, blockEntity, state, partialTick);

        state.empty = world == null || blockEntity.isEmpty();
        if (state.empty) return;

        BlockState blockState = world.getBlockState(blockEntity.getBlockPos());
        if (!(blockState.getBlock() instanceof PedestalBlock pedestal)) {
            state.empty = true;
            return;
        }

        ItemStack activeItem = blockEntity.getItem(0);
        this.itemModelResolver.updateForTopItem(
                state.item,
                activeItem,
                ItemDisplayContext.GROUND,
                world,
                null,
                blockEntity.getBlockPos().hashCode()
        );

        state.height = pedestal.getHeight(blockState);
        state.isBlockItem = activeItem.getItem() instanceof BlockItem;
        state.isEndCrystal = activeItem.getItem() == Items.END_CRYSTAL;
        state.isEternalCrystal = activeItem.getItem() == EndResourceItems.ETERNAL_CRYSTAL;
        state.activated = blockState.is(EndFunctionalBlocks.ETERNAL_PEDESTAL)
                && blockState.getValue(EternalPedestal.ACTIVATED);
        state.age = getGemAge();
        state.tickDelta = partialTick;
    }

    /**
     * The socket outlines are extracted here rather than from a dedicated renderer because the
     * infusion pedestal already has a block entity renderer, and its pose is anchored exactly where
     * the socket offsets are measured from.
     */
    private static void extractInfusionHint(Level world, BlockPos pos, PedestalRenderState state, float partialTick) {
        state.hintIntensity = 0;
        if (world == null || !InfusionHint.isActiveAt(pos)) return;

        float[] outline = InfusionHint.outline(partialTick);
        if (outline == null || outline[0] <= 0) return;

        state.hintIntensity = outline[0];
        state.hintPulse = outline[1];

        // Recomputed every frame instead of snapshotted at trigger time, so a socket switches from
        // "missing" to "placed" the moment the player fills it.
        boolean[] present = InfusionRitual.socketsPresent(world, pos);
        System.arraycopy(present, 0, state.hintSockets, 0, state.hintSockets.length);
    }

    /**
     * The eternal portal vision, anchored to whichever pedestal is nearest the portal centre. It has
     * to hang off exactly one block entity - drawn from every pedestal it would be drawn six times -
     * and the nearest one is both stable and the most likely to be on screen.
     */
    private static void extractEternalVision(
            Level world,
            PedestalBlockEntity blockEntity,
            PedestalRenderState state,
            float partialTick
    ) {
        state.visionIntensity = 0;
        state.visionCrystals.clear();
        state.visionFrame.clear();
        if (world == null || !(blockEntity instanceof EternalPedestalEntity)) return;

        BlockPos anchor = EternalHint.anchorPedestal(world);
        if (anchor == null || !anchor.equals(blockEntity.getBlockPos())) return;

        state.visionIntensity = EternalHint.intensity(partialTick);
        if (state.visionIntensity <= 0) return;

        BlockPos here = blockEntity.getBlockPos();
        for (BlockPos pos : EternalHint.pedestalPositions()) state.visionCrystals.add(pos.subtract(here));
        for (BlockPos pos : EternalHint.missingFrame(world)) state.visionFrame.add(pos.subtract(here));
        state.age = getGemAge();
        state.tickDelta = partialTick;
    }

    private static void submitEternalVision(
            PedestalRenderState state,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector
    ) {
        float lineWidth = Minecraft.getInstance().getWindow().getAppropriateLineWidth();
        int frameColor = ARGB.color((int) (state.visionIntensity * 0.75F * 255), FRAME_MISSING_RGB);

        for (BlockPos offset : state.visionFrame) {
            matrices.pushPose();
            matrices.translate(offset.getX(), offset.getY(), offset.getZ());
            submitOutline(submitNodeCollector, matrices, Shapes.block(), frameColor, lineWidth);
            matrices.popPose();
        }

        for (BlockPos offset : state.visionCrystals) {
            matrices.pushPose();
            matrices.translate(
                    offset.getX() + 0.5,
                    offset.getY() + EternalHint.CRYSTAL_HEIGHT,
                    offset.getZ() + 0.5
            );
            // The same renderer a real eternal crystal uses, so the ghosts turn exactly as the
            // finished ritual's do rather than being a separate approximation of it.
            EternalCrystalRenderer.render(
                    state.age,
                    state.tickDelta,
                    matrices,
                    submitNodeCollector,
                    state.lightCoords,
                    state.visionIntensity * CRYSTAL_GHOST_ALPHA
            );
            matrices.popPose();
        }
    }

    private static void submitInfusionHint(
            PedestalRenderState state,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector
    ) {
        // linesTranslucent, not lines: 26.3 split the line pipelines and left the plain one with no
        // blend function, so the alpha in the outline colour was simply discarded there. The
        // translucent variant blends on every version - at the cost of not writing depth, which for a
        // ghost overlay is the behaviour you want anyway.
        VoxelShape shape = PedestalBlock.defaultShape();
        float lineWidth = Minecraft.getInstance().getWindow().getAppropriateLineWidth();

        for (int i = 0; i < state.hintSockets.length; i++) {
            BlockPos offset = InfusionRitual.socketPos(BlockPos.ZERO, i);
            // Sockets that still need a pedestal shimmer; the ones already filled sit back quietly.
            boolean placed = state.hintSockets[i];
            boolean north = i == InfusionRecipe.CatalystSlot.NORTH.index;
            int rgb = north
                    ? (placed ? SOCKET_NORTH_PLACED_RGB : SOCKET_NORTH_MISSING_RGB)
                    : (placed ? SOCKET_PLACED_RGB : SOCKET_MISSING_RGB);
            int color = ARGB.color((int) (state.hintIntensity * (placed ? 0.35F : state.hintPulse) * 255), rgb);

            matrices.pushPose();
            matrices.translate(offset.getX(), offset.getY(), offset.getZ());
            submitOutline(submitNodeCollector, matrices, shape, color, lineWidth);
            matrices.popPose();
        }
    }

    /**
     * Draws one socket outline. Isolated because this is the one part of the hint that differs
     * between game versions: 26.1 has no {@code submitShapeOutline} node, so the shape is emitted
     * into a custom-geometry callback with {@link ShapeRenderer} instead.
     */
    private static void submitOutline(
            SubmitNodeCollector submitNodeCollector,
            PoseStack matrices,
            VoxelShape shape,
            int color,
            float lineWidth
    ) {
        submitNodeCollector.submitCustomGeometry(matrices, RenderTypes.linesTranslucent(), (pose, buffer) -> {
            // ShapeRenderer wants a PoseStack but custom geometry hands out the flattened Pose, so
            // the transform is folded back into a throwaway stack (mulPose, not a raw matrix set, so
            // the normal matrix comes along - the line shader needs it).
            PoseStack local = new PoseStack();
            local.mulPose(pose.pose());
            ShapeRenderer.renderShape(local, buffer, shape, 0, 0, 0, color, lineWidth);
        });
    }

    @Override
    public void submit(
            PedestalRenderState state,
            PoseStack matrices,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState cameraRenderState
    ) {
        if (state.hintIntensity > 0) {
            submitInfusionHint(state, matrices, submitNodeCollector);
        }
        if (state.visionIntensity > 0) {
            submitEternalVision(state, matrices, submitNodeCollector);
        }
        if (state.empty) return;

        matrices.pushPose();
        matrices.translate(0.5, state.height, 0.5);
        if (state.isBlockItem) {
            matrices.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrices.scale(1.25F, 1.25F, 1.25F);
        }
        if (state.activated) {
            float[] colors = ColorHelper.toFloatArrayRGBA(EternalCrystalRenderer.colors(state.age));
            int y = state.blockPos.getY();

            BeamRenderer.renderLightBeam(
                    matrices,
                    submitNodeCollector,
                    state.age,
                    state.tickDelta,
                    -y,
                    1024 - y,
                    colors,
                    0.25F,
                    0.13F,
                    0.16F
            );
            float altitude = Mth.sin((state.age + state.tickDelta) / 10.0F) * 0.1F + 0.1F;
            matrices.translate(0.0D, altitude, 0.0D);
        }
        if (state.isEndCrystal) {
            EndCrystalRenderer.render(state.age, 314, state.tickDelta, matrices, submitNodeCollector, state.lightCoords);
        } else if (state.isEternalCrystal) {
            EternalCrystalRenderer.render(state.age, state.tickDelta, matrices, submitNodeCollector, state.lightCoords);
        } else {
            float rotation = (state.age + state.tickDelta) / 25.0F + 6.0F;
            matrices.mulPose(Axis.YP.rotation(rotation));
            state.item.submit(matrices, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        matrices.popPose();
    }

    public static int getGemAge() {
        return (int) (Minecraft.getInstance().level.getGameTime() % 314);
    }
}
