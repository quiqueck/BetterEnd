package org.betterx.betterend.client.render;

import org.betterx.bclib.util.BackgroundInfo;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.config.Configs;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelTerrainRenderContext;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Fabric's {@code DimensionRenderingRegistry.SkyRenderer} (a per-dimension sky-render callback)
 * was removed with the 26.1 render pipeline rework in favor of the generic
 * {@link LevelRenderEvents}. There is no longer a dedicated "replace the sky" extension point, so
 * this hooks {@link LevelRenderEvents#START_MAIN} and manually checks for the End dimension.
 * <p>
 * START_MAIN fires at the very start of the main (terrain) framegraph pass, i.e. right after the
 * separate sky pass ({@code SkyRenderer.renderEndSky()}) and immediately before opaque terrain is
 * drawn. That reproduces vanilla's ordering: our custom sky paints over the vanilla End sky, and
 * the opaque terrain drawn afterwards paints over ours — so terrain occludes the sky naturally at
 * every distance, with no depth test needed (the vanilla END_SKY/STARS pipelines carry no
 * depth-stencil state precisely because they are meant to run before terrain).
 */
public class BetterEndSkyRenderer implements LevelRenderEvents.StartMain {
    private record SkyMesh(GpuBuffer buffer, int indexCount) {
    }

    @FunctionalInterface
    interface BufferFunction {
        MeshData make(BufferBuilder builder, float minSize, float maxSize, int count, long seed);
    }

    private static final Identifier NEBULA_1 = BetterEnd.C.mk("textures/sky/nebula_2.png");
    private static final Identifier NEBULA_2 = BetterEnd.C.mk("textures/sky/nebula_3.png");
    private static final Identifier HORIZON = BetterEnd.C.mk("textures/sky/nebula_1.png");
    private static final Identifier STARS = BetterEnd.C.mk("textures/sky/stars.png");
    private static final Identifier FOG = BetterEnd.C.mk("textures/sky/fog.png");

    private SkyMesh nebula1;
    private SkyMesh nebula2;
    private SkyMesh horizon;
    private SkyMesh stars1;
    private SkyMesh stars2;
    private SkyMesh stars3;
    private SkyMesh stars4;
    private SkyMesh fog;
    private Vector3f axis1;
    private Vector3f axis2;
    private Vector3f axis3;
    private Vector3f axis4;

    private boolean initialised;

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

    public static void register() {
        LevelRenderEvents.START_MAIN.register(new BetterEndSkyRenderer());
    }

    @Override
    public void startMain(LevelTerrainRenderContext context) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null || world.dimension() != Level.END || !Configs.CLIENT_CONFIG.customSky.get()) {
            return;
        }

        CameraRenderState cameraRenderState = context.levelState().cameraRenderState;
        if (cameraRenderState == null || cameraRenderState.viewRotationMatrix == null) {
            return;
        }

        initialise();

        PoseStack matrices = new PoseStack();
        matrices.mulPose(cameraRenderState.viewRotationMatrix);

        float time = ((world.getOverworldClockTime() + Minecraft.getInstance()
                .getDeltaTracker()
                .getRealtimeDeltaTicks()) % 360000) * 0.000017453292f;
        float time2 = time * 2;
        float time3 = time * 3;

        float blindA = 1F - BackgroundInfo.blindness;
        float blind02 = blindA * 0.2f;
        float blind06 = blindA * 0.6f;

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time, 0));
            renderTexturedMesh(matrices, horizon, HORIZON, 0.77f, 0.31f, 0.73f, 0.7f * blindA);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, -time, 0));
            renderTexturedMesh(matrices, nebula1, NEBULA_1, 0.77f, 0.31f, 0.73f, blind02);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().rotationXYZ(0, time2, 0));
            renderTexturedMesh(matrices, nebula2, NEBULA_2, 0.77f, 0.31f, 0.73f, blind02);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time, axis3.x, axis3.y, axis3.z));
            renderTexturedMesh(matrices, stars3, STARS, 0.77f, 0.31f, 0.73f, blind06);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis4.x, axis4.y, axis4.z));
            renderTexturedMesh(matrices, stars4, STARS, 1F, 1F, 1F, blind06);
            matrices.popPose();
        }

        float a = (BackgroundInfo.fogDensity - 1F);
        if (a > 0) {
            if (a > 1) a = 1;
            renderTexturedMesh(
                    matrices,
                    fog,
                    FOG,
                    BackgroundInfo.fogColorRed,
                    BackgroundInfo.fogColorGreen,
                    BackgroundInfo.fogColorBlue,
                    a
            );
        }

        if (blindA > 0) {
            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time3, axis1.x, axis1.y, axis1.z));
            renderStarMesh(matrices, stars1, 1, 1, 1, blind06);
            matrices.popPose();

            matrices.pushPose();
            matrices.mulPose(new Quaternionf().setAngleAxis(time2, axis2.x, axis2.y, axis2.z));
            renderStarMesh(matrices, stars2, 0.95f, 0.64f, 0.93f, blind06);
            matrices.popPose();
        }
    }

    private void renderTexturedMesh(
            PoseStack matrices,
            SkyMesh mesh,
            Identifier texture,
            float r,
            float g,
            float b,
            float a
    ) {
        AbstractTexture abstractTexture = Minecraft.getInstance().getTextureManager().getTexture(texture);
        GpuTextureView colorView = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView depthView = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBufferSlice transform = RenderSystem.getDynamicUniforms()
                                                .writeTransform(
                                                        matrices.last().pose(),
                                                        new Vector4f(r, g, b, a),
                                                        new Vector3f(),
                                                        new Matrix4f()
                                                );

        RenderSystem.AutoStorageIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer indices = indexBuffer.getBuffer(mesh.indexCount());

        try (RenderPass pass = RenderSystem.getDevice()
                                            .createCommandEncoder()
                                            .createRenderPass(
                                                    () -> "BetterEnd sky",
                                                    colorView,
                                                    OptionalInt.empty(),
                                                    depthView,
                                                    OptionalDouble.empty()
                                            )) {
            pass.setPipeline(RenderPipelines.END_SKY);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transform);
            pass.bindTexture("Sampler0", abstractTexture.getTextureView(), abstractTexture.getSampler());
            pass.setVertexBuffer(0, mesh.buffer());
            pass.setIndexBuffer(indices, indexBuffer.type());
            pass.drawIndexed(0, 0, mesh.indexCount(), 1);
        }
    }

    private void renderStarMesh(PoseStack matrices, SkyMesh mesh, float r, float g, float b, float a) {
        GpuTextureView colorView = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView depthView = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBufferSlice transform = RenderSystem.getDynamicUniforms()
                                                .writeTransform(
                                                        matrices.last().pose(),
                                                        new Vector4f(r, g, b, a),
                                                        new Vector3f(),
                                                        new Matrix4f()
                                                );

        RenderSystem.AutoStorageIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
        GpuBuffer indices = indexBuffer.getBuffer(mesh.indexCount());

        try (RenderPass pass = RenderSystem.getDevice()
                                            .createCommandEncoder()
                                            .createRenderPass(
                                                    () -> "BetterEnd stars",
                                                    colorView,
                                                    OptionalInt.empty(),
                                                    depthView,
                                                    OptionalDouble.empty()
                                            )) {
            pass.setPipeline(RenderPipelines.STARS);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transform);
            pass.setVertexBuffer(0, mesh.buffer());
            pass.setIndexBuffer(indices, indexBuffer.type());
            pass.drawIndexed(0, 0, mesh.indexCount(), 1);
        }
    }

    private void initStars() {
        stars1 = buildStarMesh(0.1f, 0.30f, 3500, 41315);
        stars2 = buildStarMesh(0.1f, 0.35f, 2000, 35151);
        stars3 = buildTexturedMesh(0.4f, 1.2f, 1000, 61354, this::makeUVStars);
        stars4 = buildTexturedMesh(0.4f, 1.2f, 1000, 61355, this::makeUVStars);
        nebula1 = buildTexturedMesh(40, 60, 30, 11515, this::makeFarFog);
        nebula2 = buildTexturedMesh(40, 60, 10, 14151, this::makeFarFog);
        horizon = buildTexturedMesh(0, 0, 0, 0, (b, min, max, count, seed) -> makeCylinder(b, 16, 50, 100));
        fog = buildTexturedMesh(0, 0, 0, 0, (b, min, max, count, seed) -> makeCylinder(b, 16, 50, 70));
    }

    private SkyMesh buildStarMesh(float minSize, float maxSize, int count, long seed) {
        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(
                DefaultVertexFormat.POSITION.getVertexSize() * count * 4
        )) {
            BufferBuilder builder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            MeshData meshData = makeStars(builder, minSize, maxSize, count, seed);
            return uploadMesh(meshData, "BetterEnd stars vertex buffer");
        }
    }

    private SkyMesh buildTexturedMesh(float minSize, float maxSize, int count, long seed, BufferFunction fkt) {
        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(
                DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize() * Math.max(count, 64) * 4
        )) {
            BufferBuilder builder = new BufferBuilder(
                    byteBufferBuilder,
                    VertexFormat.Mode.QUADS,
                    DefaultVertexFormat.POSITION_TEX_COLOR
            );
            MeshData meshData = fkt.make(builder, minSize, maxSize, count, seed);
            return uploadMesh(meshData, "BetterEnd sky vertex buffer");
        }
    }

    private SkyMesh uploadMesh(MeshData meshData, String label) {
        try (meshData) {
            int indexCount = meshData.drawState().indexCount();
            GpuBuffer buffer = RenderSystem.getDevice()
                                            .createBuffer(() -> label, GpuBuffer.USAGE_VERTEX, meshData.vertexBuffer());
            return new SkyMesh(buffer, indexCount);
        }
    }

    private MeshData makeStars(BufferBuilder buffer, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() * 2.0f - 1.0f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0f && length > 0.001f) {
                length = 1.0f / (float) Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    buffer.addVertex(px + dx, py + dy, pz + dz);
                }
            }
        }

        return buffer.buildOrThrow();
    }

    private MeshData makeUVStars(BufferBuilder buffer, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        int white = ARGB.colorFromFloat(1F, 1F, 1F, 1F);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() * 2.0f - 1.0f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;

            if (length < 1.0f && length > 0.001f) {
                length = 1.0f / (float) Math.sqrt(length);
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                float minV = random.nextInt(4) / 4F;
                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = (((index + 1) >> 1) & 1) / 4F + minV;
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV).setColor(white);
                }
            }
        }

        return buffer.buildOrThrow();
    }

    private MeshData makeFarFog(BufferBuilder buffer, float minSize, float maxSize, int count, long seed) {
        RandomSource random = new LegacyRandomSource(seed);
        int white = ARGB.colorFromFloat(1F, 1F, 1F, 1F);

        for (int i = 0; i < count; ++i) {
            float posX = random.nextFloat() * 2.0f - 1.0f;
            float posY = random.nextFloat() - 0.5f;
            float posZ = random.nextFloat() * 2.0f - 1.0f;
            float size = MHelper.randRange(minSize, maxSize, random);
            float length = posX * posX + posY * posY + posZ * posZ;
            float distance = 2.0f;

            if (length < 1.0f && length > 0.001f) {
                length = distance / (float) Math.sqrt(length);
                size *= distance;
                posX *= length;
                posY *= length;
                posZ *= length;

                float px = posX * 100.0f;
                float py = posY * 100.0f;
                float pz = posZ * 100.0f;

                float angle = (float) Math.atan2(posX, posZ);
                float sin1 = (float) Math.sin(angle);
                float cos1 = (float) Math.cos(angle);
                angle = (float) Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
                float sin2 = (float) Math.sin(angle);
                float cos2 = (float) Math.cos(angle);
                angle = random.nextFloat() * (float) Math.PI * 2.0f;
                float sin3 = (float) Math.sin(angle);
                float cos3 = (float) Math.cos(angle);

                for (int index = 0; index < 4; ++index) {
                    float x = (float) ((index & 2) - 1) * size;
                    float y = (float) ((index + 1 & 2) - 1) * size;
                    float aa = x * cos3 - y * sin3;
                    float ab = y * cos3 + x * sin3;
                    float dy = aa * sin2 + 0.0f * cos2;
                    float ae = 0.0f * sin2 - aa * cos2;
                    float dx = ae * sin1 - ab * cos1;
                    float dz = ab * sin1 + ae * cos1;
                    float texU = (index >> 1) & 1;
                    float texV = ((index + 1) >> 1) & 1;
                    buffer.addVertex(px + dx, py + dy, pz + dz).setUv(texU, texV).setColor(white);
                }
            }
        }
        return buffer.buildOrThrow();
    }

    private MeshData makeCylinder(BufferBuilder buffer, int segments, float height, float radius) {
        int white = ARGB.colorFromFloat(1F, 1F, 1F, 1F);
        for (int i = 0; i < segments; i++) {
            float a1 = (float) i * (float) Math.PI * 2.0f / (float) segments;
            float a2 = (float) (i + 1) * (float) Math.PI * 2.0f / (float) segments;
            float px1 = (float) Math.sin(a1) * radius;
            float pz1 = (float) Math.cos(a1) * radius;
            float px2 = (float) Math.sin(a2) * radius;
            float pz2 = (float) Math.cos(a2) * radius;

            float u0 = (float) i / (float) segments;
            float u1 = (float) (i + 1) / (float) segments;

            buffer.addVertex(px1, -height, pz1).setUv(u0, 0).setColor(white);
            buffer.addVertex(px1, height, pz1).setUv(u0, 1).setColor(white);
            buffer.addVertex(px2, height, pz2).setUv(u1, 1).setColor(white);
            buffer.addVertex(px2, -height, pz2).setUv(u1, 0).setColor(white);
        }
        return buffer.buildOrThrow();
    }
}
