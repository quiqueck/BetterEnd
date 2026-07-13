package org.betterx.betterend.client.render;

import org.betterx.betterend.blocks.FlowerPotBlock;
import org.betterx.betterend.blocks.entities.FlowerPotBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FlowerPotItemRenderer<T extends FlowerPotBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemModelResolver itemModelResolver;
    private final ItemStackRenderState renderState = new ItemStackRenderState();

    public FlowerPotItemRenderer(BlockEntityRendererProvider.Context ctx) {
        super();
        this.itemModelResolver = ctx.getItemModelResolver();
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

        blockEntity.getSoilBlock().ifPresent(soil -> renderStack(
                soil, world, blockEntity, matrices, vertexConsumers, light, overlay, 0.5, 0.5F
        ));
        blockEntity.getPlantBlock().ifPresent(plant -> renderStack(
                plant, world, blockEntity, matrices, vertexConsumers, light, overlay, 0.75, 0.5F
        ));
    }

    private void renderStack(
            Block block,
            Level world,
            T blockEntity,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay,
            double yOffset,
            float scale
    ) {
        matrices.pushPose();
        this.itemModelResolver.updateForTopItem(
                this.renderState,
                new ItemStack(block),
                ItemDisplayContext.GROUND,
                world,
                null,
                blockEntity.getBlockPos().hashCode()
        );
        matrices.translate(0.5, yOffset, 0.5);
        matrices.scale(scale, scale, scale);
        this.renderState.render(matrices, vertexConsumers, light, overlay);
        matrices.popPose();
    }
}
