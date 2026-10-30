package org.betterx.betterend.mixin.common;

import org.betterx.betterend.registry.EndBiomeBuildings;
import org.betterx.betterend.registry.EndStructures;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Keeps the biome buildings ({@link EndBiomeBuildings}) and the crashed ship on solid ground: Minecraft 1.20 has no
 * {@code dimension_padding} for jigsaw structures (the newer versions use it for this), so a start whose floor
 * lies below {@link EndBiomeBuildings#MIN_GROUND_Y} - or over the void, where the heightmap reports the bottom
 * of the world - is rejected here.
 */
@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {
    @Shadow
    @Final
    private Holder<StructureTemplatePool> startPool;

    @Shadow
    @Final
    private Optional<Heightmap.Types> projectStartToHeightmap;

    @Inject(method = "findGenerationPoint", at = @At("RETURN"), cancellable = true)
    private void be_keepBuildingsOnGround(
            Structure.GenerationContext context,
            CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir
    ) {
        if (projectStartToHeightmap.isEmpty()) return;
        final int minGround;
        if (EndBiomeBuildings.isBuildingPool(startPool)) minGround = EndBiomeBuildings.MIN_GROUND_Y;
        else if (startPool.is(EndStructures.CRASHED_SHIP_POOL)) minGround = EndStructures.CRASHED_SHIP_MIN_GROUND_Y;
        else return;
        // the stub sits one block above the floor of the start piece; the newer versions' dimension_padding
        // rejects a floor below the minimum, so the stub must be above it
        final Optional<Structure.GenerationStub> stub = cir.getReturnValue();
        if (stub.isPresent() && stub.get().position().getY() <= minGround) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
