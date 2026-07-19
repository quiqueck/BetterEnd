package org.betterx.datagen.betterend;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;

import com.google.common.hash.Hashing;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Writes a single, deterministic {@code block_properties.txt} snapshot of the runtime {@link BlockBehaviour}
 * properties of <em>every</em> block in {@link BuiltInRegistries#BLOCK} (vanilla + this mod + any other mod
 * present at datagen time), one line per block sorted by id.
 *
 * <p>Block property changes have no other datagen surface, so a change to e.g. a block's destroy time is
 * otherwise invisible to a {@code diff -rq} of the generated assets. This file makes such changes show up as a
 * reviewable git diff. It is committed under {@code src/main/generated} but excluded from the packed jar via
 * {@code processResources { exclude 'block_properties.txt' }} in build.gradle.
 */
public class BlockPropertiesProvider implements WoverDataProvider<DataProvider> {
    /**
     * {@code hasCollision} has no public getter on {@link BlockBehaviour}; read the protected final field
     * reflectively. Datagen runs in the named (mojmap) dev environment, so the field name is stable here.
     */
    private static final Field HAS_COLLISION_FIELD = resolveHasCollisionField();

    private static Field resolveHasCollisionField() {
        try {
            final Field field = BlockBehaviour.class.getDeclaredField("hasCollision");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Could not resolve BlockBehaviour.hasCollision for the block audit", e);
        }
    }

    /**
     * A stable name for every {@link SoundType} constant, resolved by reflecting over {@link SoundType}'s
     * public static final fields and keying by identity. Lets the audit print e.g. {@code sound=METAL} /
     * {@code sound=NETHERRACK} instead of an unstable {@code toString()}, so a change to a block's
     * {@link SoundType} shows up as a reviewable diff.
     */
    private static final Map<SoundType, String> SOUND_TYPE_NAMES = resolveSoundTypeNames();

    private static Map<SoundType, String> resolveSoundTypeNames() {
        final Map<SoundType, String> map = new IdentityHashMap<>();
        for (Field field : SoundType.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())
                    && SoundType.class.equals(field.getType())) {
                try {
                    map.put((SoundType) field.get(null), field.getName());
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not read SoundType." + field.getName(), e);
                }
            }
        }
        return map;
    }

    /**
     * The stable name of the block's {@link SoundType} (e.g. {@code METAL}, {@code STONE}, {@code NETHERRACK}),
     * or, for a {@link SoundType} that is not one of {@link SoundType}'s named constants (a modded or inline
     * instance), a lowercase {@code path:<break-sound-path>} fallback so the value is still deterministic.
     */
    private static String soundName(Block block) {
        final SoundType soundType = block.defaultBlockState().getSoundType();
        final String name = SOUND_TYPE_NAMES.get(soundType);
        if (name != null) return name;
        return "path:" + soundType.getBreakSound().location().getPath();
    }

    protected final ModCore modCore;

    public BlockPropertiesProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    @Override
    public DataProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output);
    }

    private static boolean hasCollision(Block block) {
        try {
            return HAS_COLLISION_FIELD.getBoolean(block);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not read BlockBehaviour.hasCollision for " + block, e);
        }
    }

    private static String lineFor(ResourceLocation id, Block block) {
        final BlockState state = block.defaultBlockState();
        // A block's map-color function may return null for its default state (some modded blocks do); emit -1
        // rather than NPE, so the audit still covers every block.
        final var mapColor = block.defaultMapColor();
        // Locale-independent formatting throughout (String.valueOf / Float.toString use '.', never a locale
        // decimal comma), so the file is byte-identical regardless of the machine's default locale.
        return id
                + "  class=" + block.getClass().getSimpleName()
                + "  destroyTime=" + block.defaultDestroyTime()
                + "  resistance=" + block.getExplosionResistance()
                + "  reqTool=" + state.requiresCorrectToolForDrops()
                + "  mapColor=" + (mapColor == null ? -1 : mapColor.id)
                + "  instrument=" + state.instrument().name()
                + "  sound=" + soundName(block)
                + "  friction=" + block.getFriction()
                + "  speed=" + block.getSpeedFactor()
                + "  jump=" + block.getJumpFactor()
                + "  ignitedByLava=" + state.ignitedByLava()
                + "  randomlyTicks=" + state.isRandomlyTicking()
                + "  hasCollision=" + hasCollision(block)
                + "  canOcclude=" + state.canOcclude()
                // The effective chunk render layer (SOLID/CUTOUT/TRANSLUCENT), read from the vanilla registry
                // that both the RenderLayerProvider scan and the RENDER_LAYER trait populate via
                // BlockRenderLayerMap - so migrating a block from the interface to the trait shows up here as a
                // diff only if the layer actually changed. Datagen always runs client-side, so this is safe.
                + "  renderLayer=" + ItemBlockRenderTypes.getChunkRenderType(state).name();
    }

    private class Provider implements DataProvider {
        private final FabricDataOutput output;

        private Provider(FabricDataOutput output) {
            this.output = output;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            final List<String> lines = new ArrayList<>();
            for (Block block : BuiltInRegistries.BLOCK) {
                final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                lines.add(lineFor(id, block));
            }
            // Sorting by the full line orders by the (unique) id prefix, so the file is fully deterministic.
            Collections.sort(lines);

            final byte[] bytes = (String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8);
            final Path path = output.getOutputFolder().resolve("block_properties.txt");
            try {
                writer.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public @NotNull String getName() {
            return "Block Properties Audit (" + modCore.namespace + ")";
        }
    }
}
