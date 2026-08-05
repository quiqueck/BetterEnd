package org.betterx.betterend.mixin.common;


import org.betterx.betterend.registry.block.EndDecorBlocks;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.world.generator.GeneratorOptions;
import org.betterx.betterend.world.generator.TerrainGenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    private final static List<ResourceKey<DimensionType>> BE_TEST_DIMENSIONS = List.of(
            BuiltinDimensionTypes.OVERWORLD,
            BuiltinDimensionTypes.OVERWORLD_CAVES,
            BuiltinDimensionTypes.NETHER
    );

    protected ServerLevelMixin(
            WritableLevelData writableLevelData,
            ResourceKey<Level> resourceKey,
            RegistryAccess registryAccess,
            Holder<DimensionType> holder,
            boolean bl,
            boolean bl2,
            long l,
            int i
    ) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    // 26.1 retarget: the constructor no longer branches on Holder.is(ResourceKey) for the End
    // dimension type - it now asks the DimensionType itself via hasEnderDragonFight(). Redirect
    // that call instead of modifying an argument that no longer exists.
    @Redirect(method = "<init>*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasEnderDragonFight()Z"))
    private boolean be_hasEnderDragonFight(DimensionType dimensionType) {
        if (!GeneratorOptions.hasDragonFights() && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
            return false;
        }
        return dimensionType.hasEnderDragonFight();
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void be_onServerWorldInit(
            MinecraftServer minecraftServer,
            Executor executor,
            LevelStorageAccess levelStorageAccess,
            ServerLevelData serverLevelData,
            ResourceKey resourceKey,
            LevelStem levelStem,
            boolean bl,
            long seed,
            List list,
            boolean bl2,
            CallbackInfo ci
    ) {
        TerrainGenerator.onServerLevelInit(ServerLevel.class.cast(this), levelStem, seed);
    }

    @ModifyArg(method = "tickPrecipitation", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private BlockState be_modifyTickState(BlockPos pos, BlockState state) {
        if (state.is(Blocks.ICE)) {
            Identifier biome = getBiome(pos).unwrapKey().orElseThrow().identifier();
            if (biome.getNamespace().equals(BetterEnd.MOD_ID)) {
                state = EndDecorBlocks.EMERALD_ICE.defaultBlockState();
            }
        }
        return state;
    }
}
