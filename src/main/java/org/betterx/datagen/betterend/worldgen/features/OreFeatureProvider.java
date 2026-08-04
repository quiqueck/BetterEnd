package org.betterx.datagen.betterend.worldgen.features;


import org.betterx.betterend.registry.block.EndMetalBlocks;
import org.betterx.betterend.registry.block.EndOreBlocks;
import org.betterx.betterend.registry.block.EndStoneBlocks;
import static org.betterx.betterend.complexmaterials.MetalMaterial.ORE;
import org.betterx.betterend.complexmaterials.StoneMaterial;
import org.betterx.betterend.registry.EndBlocks;
import org.betterx.betterend.registry.EndFeatures;
import org.betterx.betterend.registry.features.EndOreFeatures;
import org.betterx.betterend.world.features.terrain.OreLayerFeatureConfig;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class OreFeatureProvider extends WoverFeatureProvider {
    public OreFeatureProvider(@NotNull ModCore modCore) {
        super(modCore, modCore.id("ores"));
    }

    @Override
    protected void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {

    }

    @Override
    protected void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        registerOre(context, EndOreFeatures.THALLASIUM_ORE, EndMetalBlocks.THALLASIUM.getBlock(ORE), 24, 8);
        registerOre(context, EndOreFeatures.ENDER_ORE, EndOreBlocks.ENDER_ORE, 12, 4);
        registerOre(context, EndOreFeatures.AMBER_ORE, EndOreBlocks.AMBER_ORE, 60, 6);
        registerOre(context, EndOreFeatures.DRAGON_BONE_BLOCK_ORE, EndStoneBlocks.DRAGON_BONE_BLOCK, 24, 8);

        registerLayer(context, EndOreFeatures.VIOLECITE_LAYER, EndStoneBlocks.VIOLECITE, 15, 16, 128, 8);
        registerLayer(context, EndOreFeatures.FLAVOLITE_LAYER, EndStoneBlocks.FLAVOLITE, 12, 16, 128, 6);
    }

    private void registerOre(
            BootstrapContext<PlacedFeature> context,
            PlacedFeatureKey key,
            Block blockOre,
            int veins,
            int veinSize
    ) {
        key.inlineConfiguration(context)
           .ore()
           .add(Blocks.END_STONE, blockOre)
           .veinSize(veinSize)
           .discardChanceOnAirExposure(0)
           .inlinePlace()
           .count(veins)
           .randomHeight8FromFloorCeil()
           .squarePlacement()
           .onlyInBiome()
           .register();
    }

    private static void registerLayer(
            BootstrapContext<PlacedFeature> context, PlacedFeatureKey key,
            Block block, float radius, int minY, int maxY, int count
    ) {
        key.inlineConfiguration(context)
           .withFeature(EndFeatures.LAYERED_ORE_FEATURE)
           .configuration(new OreLayerFeatureConfig(block.defaultBlockState(), radius, minY, maxY))
           .inlinePlace()
           .onlyInBiome()
           .count(count)
           .register();
    }

    private static void registerLayer(
            BootstrapContext<PlacedFeature> context, PlacedFeatureKey key,
            StoneMaterial material, float radius, int minY, int maxY, int count
    ) {
        registerLayer(context, key, material.getBlock(SlotType.SOURCE), radius, minY, maxY, count);
    }
}
