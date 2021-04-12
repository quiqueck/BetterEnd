package ru.betterend.mixin.client;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.Quaternion;
import ru.betterend.BetterEnd;
import ru.betterend.client.ClientOptions;
import ru.betterend.util.BackgroundInfo;
import ru.betterend.util.MHelper;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	private static final ResourceLocation NEBULA_1 = BetterEnd.makeID("textures/sky/nebula_2.png");
	private static final ResourceLocation NEBULA_2 = BetterEnd.makeID("textures/sky/nebula_3.png");
	private static final ResourceLocation HORIZON = BetterEnd.makeID("textures/sky/nebula_1.png");
	private static final ResourceLocation STARS = BetterEnd.makeID("textures/sky/stars.png");
	private static final ResourceLocation FOG = BetterEnd.makeID("textures/sky/fog.png");

	private static VertexBuffer stars1;
	private static VertexBuffer stars2;
	private static VertexBuffer stars3;
	private static VertexBuffer stars4;
	private static VertexBuffer nebulas1;
	private static VertexBuffer nebulas2;
	private static VertexBuffer horizon;
	private static VertexBuffer fog;
	private static Vector3f axis1;
	private static Vector3f axis2;
	private static Vector3f axis3;
	private static Vector3f axis4;
	private static float time;
	private static float time2;
	private static float time3;
	private static float blind02;
	private static float blind06;
	private static boolean directOpenGL = false;

	@Shadow
	@Final
	private Minecraft client;

	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow
	private ClientLevel world;

	@Shadow
	private int ticks;

	@Inject(method = "<init>*", at = @At("TAIL"))
	private void be_onInit(Minecraft client, BufferBuilderStorage bufferBuilders, CallbackInfo info) {
		be_initStars();
		Random random = new Random(131);
		axis1 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
		axis2 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
		axis3 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
		axis4 = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
		axis1.normalize();
		axis2.normalize();
		axis3.normalize();
		axis4.normalize();

		directOpenGL = FabricLoader.getInstance().isModLoaded("optifabric")
				|| FabricLoader.getInstance().isModLoaded("immersive_portals");
	}

	@Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
	private void be_renderBetterEndSky(MatrixStack matrices, float tickDelta, CallbackInfo info) {
		if (ClientOptions.isCustomSky() && client.world.getSkyProperties().getSkyType() == SkyProperties.SkyType.END) {
			time = (ticks % 360000) * 0.000017453292F;
			time2 = time * 2;
			time3 = time * 3;

			BackgroundRenderer.setFogBlack();
			RenderSystem.enableTexture();

			if (directOpenGL) {
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glAlphaFunc(516, 0.0F);
				GL11.glEnable(GL11.GL_BLEND);
				RenderSystem.depthMask(false);
			} else {
				RenderSystem.enableAlphaTest();
				RenderSystem.alphaFunc(516, 0.0F);
				RenderSystem.enableBlend();
			}

			float blindA = 1F - BackgroundInfo.blindness;
			blind02 = blindA * 0.2F;
			blind06 = blindA * 0.6F;

			if (blindA > 0) {
				matrices.push();
				matrices.multiply(new Quaternion(0, time, 0, false));
				textureManager.bindTexture(HORIZON);
				be_renderBuffer(matrices, horizon, VertexFormats.POSITION_TEXTURE, 0.77F, 0.31F, 0.73F, 0.7F * blindA);
				matrices.pop();

				matrices.push();
				matrices.multiply(new Quaternion(0, -time, 0, false));
				textureManager.bindTexture(NEBULA_1);
				be_renderBuffer(matrices, nebulas1, VertexFormats.POSITION_TEXTURE, 0.77F, 0.31F, 0.73F, blind02);
				matrices.pop();

				matrices.push();
				matrices.multiply(new Quaternion(0, time2, 0, false));
				textureManager.bindTexture(NEBULA_2);
				be_renderBuffer(matrices, nebulas2, VertexFormats.POSITION_TEXTURE, 0.77F, 0.31F, 0.73F, blind02);
				matrices.pop();

				textureManager.bindTexture(STARS);

				matrices.push();
				matrices.multiply(axis3.getRadialQuaternion(time));
				be_renderBuffer(matrices, stars3, VertexFormats.POSITION_TEXTURE, 0.77F, 0.31F, 0.73F, blind06);
				matrices.pop();

				matrices.push();
				matrices.multiply(axis4.getRadialQuaternion(time2));
				be_renderBuffer(matrices, stars4, VertexFormats.POSITION_TEXTURE, 1F, 1F, 1F, blind06);
				matrices.pop();
			}

			float a = (BackgroundInfo.fog - 1F);
			if (a > 0) {
				if (a > 1)
					a = 1;
				textureManager.bindTexture(FOG);
				be_renderBuffer(matrices, fog, VertexFormats.POSITION_TEXTURE, BackgroundInfo.red, BackgroundInfo.green,
						BackgroundInfo.blue, a);
			}

			RenderSystem.disableTexture();

			if (blindA > 0) {
				matrices.push();
				matrices.multiply(axis1.getRadialQuaternion(time3));
				be_renderBuffer(matrices, stars1, VertexFormats.POSITION, 1, 1, 1, blind06);
				matrices.pop();

				matrices.push();
				matrices.multiply(axis2.getRadialQuaternion(time2));
				be_renderBuffer(matrices, stars2, VertexFormats.POSITION, 0.95F, 0.64F, 0.93F, blind06);
				matrices.pop();
			}

			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);

			info.cancel();
		}
	}

	private void be_renderBuffer(MatrixStack matrices, VertexBuffer buffer, VertexFormat format, float r, float g,
			float b, float a) {
		RenderSystem.color4f(r, g, b, a);
		buffer.bind();
		format.startDrawing(0L);
		buffer.draw(matrices.peek().getModel(), 7);
		VertexBuffer.unbind();
		format.endDrawing();
	}

	private void be_initStars() {
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		stars1 = be_buildBufferStars(buffer, stars1, 0.1, 0.30, 3500, 41315);
		stars2 = be_buildBufferStars(buffer, stars2, 0.1, 0.35, 2000, 35151);
		stars3 = be_buildBufferUVStars(buffer, stars3, 0.4, 1.2, 1000, 61354);
		stars4 = be_buildBufferUVStars(buffer, stars4, 0.4, 1.2, 1000, 61355);
		nebulas1 = be_buildBufferFarFog(buffer, nebulas1, 40, 60, 30, 11515);
		nebulas2 = be_buildBufferFarFog(buffer, nebulas2, 40, 60, 10, 14151);
		horizon = be_buildBufferHorizon(buffer, horizon);
		fog = be_buildBufferFog(buffer, fog);
	}

	private VertexBuffer be_buildBufferStars(BufferBuilder bufferBuilder, VertexBuffer buffer, double minSize,
			double maxSize, int count, long seed) {
		if (buffer != null) {
			buffer.close();
		}

		buffer = new VertexBuffer(VertexFormats.POSITION);
		be_makeStars(bufferBuilder, minSize, maxSize, count, seed);
		bufferBuilder.end();
		buffer.upload(bufferBuilder);

		return buffer;
	}

	private VertexBuffer be_buildBufferUVStars(BufferBuilder bufferBuilder, VertexBuffer buffer, double minSize,
			double maxSize, int count, long seed) {
		if (buffer != null) {
			buffer.close();
		}

		buffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE);
		be_makeUVStars(bufferBuilder, minSize, maxSize, count, seed);
		bufferBuilder.end();
		buffer.upload(bufferBuilder);

		return buffer;
	}

	private VertexBuffer be_buildBufferFarFog(BufferBuilder bufferBuilder, VertexBuffer buffer, double minSize,
			double maxSize, int count, long seed) {
		if (buffer != null) {
			buffer.close();
		}

		buffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE);
		be_makeFarFog(bufferBuilder, minSize, maxSize, count, seed);
		bufferBuilder.end();
		buffer.upload(bufferBuilder);

		return buffer;
	}

	private VertexBuffer be_buildBufferHorizon(BufferBuilder bufferBuilder, VertexBuffer buffer) {
		if (buffer != null) {
			buffer.close();
		}

		buffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE);
		be_makeCylinder(bufferBuilder, 16, 50, 100);
		bufferBuilder.end();
		buffer.upload(bufferBuilder);

		return buffer;
	}

	private VertexBuffer be_buildBufferFog(BufferBuilder bufferBuilder, VertexBuffer buffer) {
		if (buffer != null) {
			buffer.close();
		}

		buffer = new VertexBuffer(VertexFormats.POSITION_TEXTURE);
		be_makeCylinder(bufferBuilder, 16, 50, 70);
		bufferBuilder.end();
		buffer.upload(bufferBuilder);

		return buffer;
	}

	private void be_makeStars(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
		Random random = new Random(seed);
		buffer.begin(7, VertexFormats.POSITION);

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
				double j = posX * 100.0;
				double k = posY * 100.0;
				double l = posZ * 100.0;
				double m = Math.atan2(posX, posZ);
				double n = Math.sin(m);
				double o = Math.cos(m);
				double p = Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
				double q = Math.sin(p);
				double r = Math.cos(p);
				double s = random.nextDouble() * Math.PI * 2.0;
				double t = Math.sin(s);
				double u = Math.cos(s);

				for (int v = 0; v < 4; ++v) {
					double x = (double) ((v & 2) - 1) * size;
					double y = (double) ((v + 1 & 2) - 1) * size;
					double aa = x * u - y * t;
					double ab = y * u + x * t;
					double ad = aa * q + 0.0 * r;
					double ae = 0.0 * q - aa * r;
					double af = ae * n - ab * o;
					double ah = ab * n + ae * o;
					buffer.vertex(j + af, k + ad, l + ah).next();
				}
			}
		}
	}

	private void be_makeUVStars(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
		Random random = new Random(seed);
		buffer.begin(7, VertexFormats.POSITION_TEXTURE);

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
				double j = posX * 100.0;
				double k = posY * 100.0;
				double l = posZ * 100.0;
				double m = Math.atan2(posX, posZ);
				double n = Math.sin(m);
				double o = Math.cos(m);
				double p = Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
				double q = Math.sin(p);
				double r = Math.cos(p);
				double s = random.nextDouble() * Math.PI * 2.0;
				double t = Math.sin(s);
				double u = Math.cos(s);

				int pos = 0;
				float minV = random.nextInt(4) / 4F;
				for (int v = 0; v < 4; ++v) {
					double x = (double) ((v & 2) - 1) * size;
					double y = (double) ((v + 1 & 2) - 1) * size;
					double aa = x * u - y * t;
					double ab = y * u + x * t;
					double ad = aa * q + 0.0 * r;
					double ae = 0.0 * q - aa * r;
					double af = ae * n - ab * o;
					double ah = ab * n + ae * o;
					float texU = (pos >> 1) & 1;
					float texV = (((pos + 1) >> 1) & 1) / 4F + minV;
					pos++;
					buffer.vertex(j + af, k + ad, l + ah).texture(texU, texV).next();
				}
			}
		}
	}

	private void be_makeFarFog(BufferBuilder buffer, double minSize, double maxSize, int count, long seed) {
		Random random = new Random(seed);
		buffer.begin(7, VertexFormats.POSITION_TEXTURE);

		for (int i = 0; i < count; ++i) {
			double posX = random.nextDouble() * 2.0 - 1.0;
			double posY = random.nextDouble() - 0.5;
			double posZ = random.nextDouble() * 2.0 - 1.0;
			double size = MHelper.randRange(minSize, maxSize, random);
			double length = posX * posX + posY * posY + posZ * posZ;
			double distance = 2.0;
			double delta = 1.0 / count;
			if (length < 1.0 && length > 0.001) {
				length = distance / Math.sqrt(length);
				size *= distance;
				distance -= delta;
				posX *= length;
				posY *= length;
				posZ *= length;
				double j = posX * 100.0;
				double k = posY * 100.0;
				double l = posZ * 100.0;
				double m = Math.atan2(posX, posZ);
				double n = Math.sin(m);
				double o = Math.cos(m);
				double p = Math.atan2(Math.sqrt(posX * posX + posZ * posZ), posY);
				double q = Math.sin(p);
				double r = Math.cos(p);
				double s = random.nextDouble() * Math.PI * 2.0;
				double t = Math.sin(s);
				double u = Math.cos(s);

				int pos = 0;
				for (int v = 0; v < 4; ++v) {
					double x = (double) ((v & 2) - 1) * size;
					double y = (double) ((v + 1 & 2) - 1) * size;
					double aa = x * u - y * t;
					double ab = y * u + x * t;
					double ad = aa * q + 0.0 * r;
					double ae = 0.0 * q - aa * r;
					double af = ae * n - ab * o;
					double ah = ab * n + ae * o;
					float texU = (pos >> 1) & 1;
					float texV = ((pos + 1) >> 1) & 1;
					pos++;
					buffer.vertex(j + af, k + ad, l + ah).texture(texU, texV).next();
				}
			}
		}
	}

	private void be_makeCylinder(BufferBuilder buffer, int segments, double height, double radius) {
		buffer.begin(7, VertexFormats.POSITION_TEXTURE);
		for (int i = 0; i < segments; i++) {
			double a1 = (double) i * Math.PI * 2.0 / (double) segments;
			double a2 = (double) (i + 1) * Math.PI * 2.0 / (double) segments;
			double px1 = Math.sin(a1) * radius;
			double pz1 = Math.cos(a1) * radius;
			double px2 = Math.sin(a2) * radius;
			double pz2 = Math.cos(a2) * radius;

			float u0 = (float) i / (float) segments;
			float u1 = (float) (i + 1) / (float) segments;

			buffer.vertex(px1, -height, pz1).texture(u0, 0).next();
			buffer.vertex(px1, height, pz1).texture(u0, 1).next();
			buffer.vertex(px2, height, pz2).texture(u1, 1).next();
			buffer.vertex(px2, -height, pz2).texture(u1, 0).next();
		}
	}
}
