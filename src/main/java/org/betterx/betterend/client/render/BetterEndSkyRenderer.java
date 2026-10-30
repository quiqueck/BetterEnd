package org.betterx.betterend.client.render;

import org.betterx.bclib.util.BackgroundInfo;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BetterEndSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    @FunctionalInterface
    interface BufferFunction {
        void make(BufferBuilder bufferBuilder, double minSize, double maxSize, int count, long seed);
    }

    private static final ResourceLocation NEBULA_1 = BetterEnd.makeID("textures/sky/nebula_2.png");
    private static final ResourceLocation NEBULA_2 = BetterEnd.makeID("textures/sky/nebula_3.png");
    private static final ResourceLocation HORIZON = BetterEnd.makeID("textures/sky/nebula_1.png");
    private static final ResourceLocation STARS = BetterEnd.makeID("textures/sky/stars.png");
    private static final ResourceLocation FOG = BetterEnd.makeID("textures/sky/fog.png");

    private VertexBuffer nebula1;
    private VertexBuffer nebula2;
    private VertexBuffer horizon;
    private VertexBuffer stars1;
    private VertexBuffer stars2;
    private VertexBuffer stars3;
    private VertexBuffer stars4;
    private VertexBuffer fog;
    private Vector3f axis1;
    private Vector3f axis2;
    private Vector3f axis3;
    private Vector3f axis4;

    private boolean initialised;

    // Average colour of the sky at the horizon (measured from a rendered frame), published to BCLib's End fog
    private static final float HORIZON_RED = 0.38f;
    private static final float HORIZON_GREEN = 0.13f;
    private static final float HORIZON_BLUE = 0.40f;

    private void initialise() {
        if (!initialised) {
            initStars();
            RandomSource random = new LegacyRandomSource(131);
            axis1 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis2 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis3 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis4 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
            axis1.normalize();
            axis2.normalize();
            axis3.normalize();
            axis4.normalize();
            initialised = true;
        }
    }

    @Override
    public void render(WorldRenderContext context) {
        if (context.world() == null || context.matrixStack() == null) {
            return;
        }

        initialise();

        BackgroundInfo.skyHorizonRed = HORIZON_RED;
        BackgroundInfo.skyHorizonGreen = HORIZON_GREEN;
        BackgroundInfo.skyHorizonBlue = HORIZON_BLUE;
        BackgroundInfo.hasSkyHorizonColor = true;

        Matrix4f projectionMatrix = context.projectionMatrix();
        PoseStack matrices = context.matrixStack();

        float time = ((context.world().getDayTime() + context.tickDelta()) % 360000) * 0.000017453292F;
        float time2 = time * 2;
        float time3 = time * 3;

        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float blindA = 1F - BackgroundInfo.blindness;
        float blind02 = blindA * 0.2F;
        float blind06 = blindA * 0.6F;

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time, 0));
            RenderSystem.setShaderTexture(0, HORIZON);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    horizon,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77F,
                    0.31F,
                    0.73F,
                    0.7F * blindA
            );
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, -time, 0));
            RenderSystem.setShaderTexture(0, NEBULA_1);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    nebula1,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77F,
                    0.31F,
                    0.73F,
                    blind02
            );
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time2, 0));
            RenderSystem.setShaderTexture(0, NEBULA_2);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    nebula2,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77F,
                    0.31F,
                    0.73F,
                    blind02
            );
            matrices.popPose();

            RenderSystem.setShaderTexture(0, STARS);

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time, axis3.x, axis3.y, axis3.z));
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    stars3,
                    DefaultVertexFormat.POSITION_TEX,
                    0.77F,
                    0.31F,
                    0.73F,
                    blind06
            );
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis4.x, axis4.y, axis4.z));
            renderBuffer(matrices, projectionMatrix, stars4, DefaultVertexFormat.POSITION_TEX, 1F, 1F, 1F, blind06);
            matrices.popPose();
        }

        float a = (BackgroundInfo.fogDensity - 1F);
        if (a > 0) {
            if (a > 1) a = 1;
            RenderSystem.setShaderTexture(0, FOG);
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    fog,
                    DefaultVertexFormat.POSITION_TEX,
                    BackgroundInfo.fogColorRed,
                    BackgroundInfo.fogColorGreen,
                    BackgroundInfo.fogColorBlue,
                    a
            );
        }

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time3, axis1.x, axis1.y, axis1.z));
            renderBuffer(matrices, projectionMatrix, stars1, DefaultVertexFormat.POSITION, 1, 1, 1, blind06);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis2.x, axis2.y, axis2.z));
            renderBuffer(
                    matrices,
                    projectionMatrix,
                    stars2,
                    DefaultVertexFormat.POSITION,
                    0.95F,
                    0.64F,
                    0.93F,
                    blind06
            );
            matrices.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderBuffer(
            PoseStack matrices,
            Matrix4f matrix4f,
            VertexBuffer buffer,
            VertexFormat format,
            float r,
            float g,
            float b,
            float a
    ) {
        RenderSystem.setShaderColor(r, g, b, a);
        buffer.bind();
        if (format == DefaultVertexFormat.POSITION) {
            buffer.drawWithShader(matrices.last().pose(), matrix4f, GameRenderer.getPositionShader());
        } else {
            buffer.drawWithShader(matrices.last().pose(), matrix4f, GameRenderer.getPositionTexShader());
        }
        VertexBuffer.unbind();
    }

    private void initStars() {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        stars1 = buildBuffer(buffer, stars1, 0.1, 0.30, 3500, 41315, this::makeStars);
        stars2 = buildBuffer(buffer, stars2, 0.1, 0.35, 2000, 35151, this::makeStars);
        stars3 = buildBuffer(buffer, stars3, 0.4, 1.2, 1000, 61354, this::makeUVStars);
        stars4 = buildBuffer(buffer, stars4, 0.4, 1.2, 1000, 61355, this::makeUVStars);
        nebula1 = buildBuffer(buffer, nebula1, 40, 60, 30, 11515, this::makeFarFog);
        nebula2 = buildBuffer(buffer, nebula2, 40, 60, 10, 14151, this::makeFarFog);
        horizon = buildBufferHorizon(buffer, horizon);
        fog = buildBufferFog(buffer, fog);
    }

    private VertexBuffer buildBuffer(
            BufferBuilder bufferBuilder,
            VertexBuffer buffer,
            double minSize,
            double maxSize,
            int count,
            long seed,
            BufferFunction fkt
    ) {
        if (buffer != null) {
            buffer.close();
        }

        buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        fkt.make(bufferBuilder, minSize, maxSize, count, seed);
        BufferBuilder.RenderedBuffer renderedBuffer = bufferBuilder.end();
        buffer.bind();
        buffer.upload(renderedBuffer);

        return buffer;
    }


    private VertexBuffer buildBufferHorizon(BufferBuilder bufferBuilder, VertexBuffer buffer) {
        return buildBuffer(
                bufferBuilder, buffer, 0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 100)
        );

    }

    private VertexBuffer buildBufferFog(BufferBuilder bufferBuilder, VertexBuffer buffer) {
        return buildBuffer(
                bufferBuilder, buffer, 0, 0, 0, 0,
                (_builder, _minSize, _maxSize, _count, _seed) -> makeCylinder(_builder, 16, 50, 70)
        );
    }

    private void makeStars(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int i = 0; i < count; ++i) {
            double posX = random.nextDouble() * 2.0 - 1.0;
            double posY = random.nextDouble() * 2.0 - 1.0;
            double posZ = random.nextDouble() * 2.0 - 1.0;
            double size = MHelper.randRange(minSize, maxSize, random);
            double length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0 && length > 0.001) {
                length = 1.0 / Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                double px = posX * 100.0;
                double py = posY * 100.0;
                double pz = posZ * 100.0;

                double angle = Math.atan2(posX, posZ);
                double sin1 = Math.sin(angle);
                double cos1 = Math.cos(angle);
                angle = Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                double sin2 = Math.sin(angle);
                double cos2 = Math.cos(angle);
                angle = random.nextDouble() * Math.PI * 2.0;
                double sin3 = Math.sin(angle);
                double cos3 = Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    double x = (double) ((index & 2) - 1) * size;
                    double y = (double) ((index + 1 & 2) - 1) * size;
                    double aa = x * cos3 - y * sin3;
                    double ab = y * cos3 + x * sin3;
                    double dy = aa * sin2 + 0.0 * cos2;
                    double ae = 0.0 * sin2 - aa * cos2;
                    double dx = ae * sin1 - ab * cos1;
                    double dz = ab * sin1 + ae * cos1;
                    buffer.vertex(px + dx, py + dy, pz + dz).endVertex();
                }
            }
        }
    }

    private void makeUVStars(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < count; ++i) {
            double posX = random.nextDouble() * 2.0 - 1.0;
            double posY = random.nextDouble() * 2.0 - 1.0;
            double posZ = random.nextDouble() * 2.0 - 1.0;
            double size = MHelper.randRange(minSize, maxSize, random);
            double length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0 && length > 0.001) {
                length = 1.0 / Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                double px = posX * 100.0;
                double py = posY * 100.0;
                double pz = posZ * 100.0;

                double angle = Math.atan2(posX, posZ);
                double sin1 = Math.sin(angle);
                double cos1 = Math.cos(angle);
                angle = Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                double sin2 = Math.sin(angle);
                double cos2 = Math.cos(angle);
                angle = random.nextDouble() * Math.PI * 2.0;
                double sin3 = Math.sin(angle);
                double cos3 = Math.cos(angle);

                float minV = random.nextInt(4) / 4F;
                for (int index = 0; index < 4; ++index) {
                    double x = (double) ((index & 2) - 1) * size;
                    double y = (double) ((index + 1 & 2) - 1) * size;
                    double aa = x * cos3 - y * sin3;
                    double ab = y * cos3 + x * sin3;
                    double dy = aa * sin2 + 0.0 * cos2;
                    double ae = 0.0 * sin2 - aa * cos2;
                    double dx = ae * sin1 - ab * cos1;
                    double dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = (((index + 1) >> 1) & 1) / 4F + minV;
                    buffer.vertex(px + dx, py + dy, pz + dz).uv(texU, texV).endVertex();
                }
            }
        }
    }

    private void makeFarFog(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < count; ++i) {
            double posX = random.nextDouble() * 2.0 - 1.0;
            double posY = random.nextDouble() - 0.5;
            double posZ = random.nextDouble() * 2.0 - 1.0;
            double size = MHelper.randRange(minSize, maxSize, random);
            double length = posX * posX + posY * posY + posZ * posZ;
            double distance = 2.0;

            if (length < 1.0 && length > 0.001) {
                length = distance / Math.sqrt(length);
                size *= distance;
                posX *= length;
                posY *= length;
                posZ *= length;

                double px = posX * 100.0;
                double py = posY * 100.0;
                double pz = posZ * 100.0;

                double angle = Math.atan2(posX, posZ);
                double sin1 = Math.sin(angle);
                double cos1 = Math.cos(angle);
                angle = Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                double sin2 = Math.sin(angle);
                double cos2 = Math.cos(angle);
                angle = random.nextDouble() * Math.PI * 2.0;
                double sin3 = Math.sin(angle);
                double cos3 = Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    double x = (double) ((index & 2) - 1) * size;
                    double y = (double) ((index + 1 & 2) - 1) * size;
                    double aa = x * cos3 - y * sin3;
                    double ab = y * cos3 + x * sin3;
                    double dy = aa * sin2 + 0.0 * cos2;
                    double ae = 0.0 * sin2 - aa * cos2;
                    double dx = ae * sin1 - ab * cos1;
                    double dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = ((index + 1) >> 1) & 1;
                    buffer.vertex(px + dx, py + dy, pz + dz).uv(texU, texV).endVertex();
                }
            }
        }
    }

    private void makeCylinder(BufferBuilder buffer, int segments, double height, double radius) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (int i = 0; i < segments; i++) {
            double a1 = (double) i * Math.PI * 2.0 / (double) segments;
            double a2 = (double) (i + 1) * Math.PI * 2.0 / (double) segments;
            double px1 = Math.sin(a1) * radius;
            double pz1 = Math.cos(a1) * radius;
            double px2 = Math.sin(a2) * radius;
            double pz2 = Math.cos(a2) * radius;

            float u0 = (float) i / (float) segments;
            float u1 = (float) (i + 1) / (float) segments;

            buffer.vertex(px1, -height, pz1).uv(u0, 0).endVertex();
            buffer.vertex(px1, height, pz1).uv(u0, 1).endVertex();
            buffer.vertex(px2, height, pz2).uv(u1, 1).endVertex();
            buffer.vertex(px2, -height, pz2).uv(u1, 0).endVertex();
        }
    }
}
