package org.betterx.betterend.client.render;

import de.ambertation.wunderlib.ui.ColorHelper;
import org.betterx.betterend.blocks.EternalPedestal;
import org.betterx.betterend.blocks.basis.PedestalBlock;
import org.betterx.betterend.blocks.entities.PedestalBlockEntity;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndItems;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PedestalItemRenderer<T extends PedestalBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemModelResolver itemModelResolver;
    private final ItemStackRenderState renderState = new ItemStackRenderState();

    public PedestalItemRenderer(BlockEntityRendererProvider.Context ctx) {
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
            net.minecraft.world.phys.Vec3 cameraPos
    ) {
        Level world = blockEntity.getLevel();
        if (world == null || blockEntity.isEmpty()) return;

        BlockState state = world.getBlockState(blockEntity.getBlockPos());
        if (!(state.getBlock() instanceof PedestalBlock)) return;

        ItemStack activeItem = blockEntity.getItem(0);

        matrices.pushPose();
        this.itemModelResolver.updateForTopItem(
                this.renderState,
                activeItem,
                ItemDisplayContext.GROUND,
                world,
                null,
                blockEntity.getBlockPos().hashCode()
        );
        PedestalBlock pedestal = (PedestalBlock) state.getBlock();
        matrices.translate(0.5, pedestal.getHeight(state), 0.5);
        if (activeItem.getItem() instanceof BlockItem) {
            matrices.scale(1.5F, 1.5F, 1.5F);
        } else {
            matrices.scale(1.25F, 1.25F, 1.25F);
        }
        int age = getGemAge();
        if (state.is(EndBlocks.ETERNAL_PEDESTAL) && state.getValue(EternalPedestal.ACTIVATED)) {
            float[] colors = ColorHelper.toFloatArrayRGBA(EternalCrystalRenderer.colors(age));
            int y = blockEntity.getBlockPos().getY();

            BeamRenderer.renderLightBeam(
                    matrices,
                    vertexConsumers,
                    age,
                    tickDelta,
                    -y,
                    1024 - y,
                    colors,
                    0.25F,
                    0.13F,
                    0.16F
            );
            float altitude = Mth.sin((age + tickDelta) / 10.0F) * 0.1F + 0.1F;
            matrices.translate(0.0D, altitude, 0.0D);
        }
        if (activeItem.getItem() == Items.END_CRYSTAL) {
            EndCrystalRenderer.render(age, 314, tickDelta, matrices, vertexConsumers, light);
        } else if (activeItem.getItem() == EndItems.ETERNAL_CRYSTAL) {
            EternalCrystalRenderer.render(age, tickDelta, matrices, vertexConsumers, light);
        } else {
            float rotation = (age + tickDelta) / 25.0F + 6.0F;
            matrices.mulPose(Axis.YP.rotation(rotation));
            this.renderState.render(matrices, vertexConsumers, light, overlay);
        }
        matrices.popPose();
    }

    public static int getGemAge() {
        return (int) (Minecraft.getInstance().level.getGameTime() % 314);
    }
}
