package org.betterx.betterend.mixin.client;

import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.world.generator.GeneratorOptions;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * {@link BlockStateModelLoader#loadBlockStates} discovers blockstate json files by scanning
 * every pack, then maps each file back to the block id it defines via {@code fileToId}. To make
 * betterend's {@code custom_chorus_plant}/{@code custom_chorus_flower} files (shipped in the
 * betterend namespace) act as the definition for {@code minecraft:chorus_plant}/{@code chorus_flower},
 * we redirect that file-to-id resolution: the vanilla files are pointed at a discarded id (so they're
 * ignored, since no such block exists) and the custom files are pointed at the real vanilla block id.
 * <p>
 * The {@code fileToId} call lives inside the per-entry lambda passed to
 * {@code CompletableFuture.supplyAsync} in {@code loadBlockStates}, not in {@code loadBlockStates}
 * itself, so the mixin must target that synthetic lambda method ({@code method_65720} as of 1.21.7)
 * rather than {@code loadBlockStates} directly.
 */
@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    @Redirect(method = "method_65720", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/FileToIdConverter;fileToId(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation be_switchModelOnLoad(FileToIdConverter instance, ResourceLocation file) {
        ResourceLocation id = instance.fileToId(file);
        if (GeneratorOptions.changeChorusPlant()) {
            if (id.getNamespace().equals("minecraft") && be_isChorusPath(id.getPath())) {
                return BetterEnd.C.mk("discarded_" + id.getPath());
            }
            if (id.getNamespace().equals(BetterEnd.MOD_ID) && id.getPath().startsWith("custom_")) {
                String path = id.getPath().substring("custom_".length());
                if (be_isChorusPath(path)) {
                    return ResourceLocation.withDefaultNamespace(path);
                }
            }
        }
        return id;
    }

    @Unique
    private static boolean be_isChorusPath(String path) {
        return path.equals("chorus_plant") || path.equals("chorus_flower");
    }
}
