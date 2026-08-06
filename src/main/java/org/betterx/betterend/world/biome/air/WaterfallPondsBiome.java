package org.betterx.betterend.world.biome.air;


import org.betterx.betterend.registry.block.EndTerrainBlocks;
import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.registry.EndParticles;
import org.betterx.betterend.registry.EndSounds;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndTerrainFeatures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A LUSH void-ring small-island biome: tiny end-stone islands cradling shallow rim ponds
 * that spill thin waterfalls over the island edge into the void. Carries a single kind of tree -
 * the dragon-helix tree (helix geometry in DRAGON_TREE wood + LUCERNIA leaves) - over an otherwise
 * treeless shore. Routed into the WoverEndBiomeSource small-island/void picker via
 * the {@code IS_SMALL_END_ISLAND} biome tag (see
 * {@link org.betterx.datagen.betterend.worldgen.EndBiomesProvider}); opts into the
 * {@link EndStructures#END_BRIDGE} structure so bridges can anchor across the void ring.
 * The pond bowls are carved by
 * {@link org.betterx.betterend.world.features.terrain.PondWithWaterfallFeature}.
 */
public class WaterfallPondsBiome extends EndBiome.Config {
    public WaterfallPondsBiome() {
        super();
    }

    @Override
    public boolean hasCaves() {
        return false;
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {
        builder
                .fogColor(120, 200, 230)
                .fogDensity(1.1F)
                .particles(EndParticles.FIREFLY, 0.0008F)
                .music(EndSounds.MUSIC_WATER)
                .waterAndFogColor(80, 210, 220)
                .plantsColor(110, 195, 150)
                // Waterfall:flower island ratio 1:2 - flower_islets stays at 0.4.
                // ~1/3 of flower_islets' 0.4 weight in the void picker: flower islands clearly dominate.
                .genChance(0.13F)
                .structure(EndStructures.END_BRIDGE)
                // Self-supporting SDF islands: this void-ring biome grows its own flat-topped
                // end-stone islands (RAW_GENERATION); the pond feature then carves the flat top
                // into a rim pond with spill-over waterfalls.
                .structure(EndStructures.SMALL_ISLAND)
                // Rim ponds with spill-over waterfalls (RAW/LAKES terrain feature)
                .feature(EndTerrainFeatures.POND_WITH_WATERFALL)
                // Lush shore vegetation plus the dragon-helix tree.
                // ORDER MATTERS: FeatureSorter requires every pair of features shared with
                // another biome to appear in the same relative order there. Constraints:
                // neon_oasis/megalake/foggy_mushroomland: umbrella_moss -> creeping_moss,
                // end_lily -> bubble_coral -> charnia_cyan -> charnia_green;
                // flower_islets (sibling): cooksonia -> salteago -> umbrella_moss.
                // Dragon-helix tree: helix-shaped canopy built from DRAGON_TREE wood + LUCERNIA leaves.
                // A new, unique feature (shared with no other biome), so it carries no FeatureSorter
                // ordering constraint.
                .feature(EndVegetationFeatures.DRAGON_HELIX_TREE)
                .feature(EndVegetationFeatures.BLOOMING_COOKSONIA)
                .feature(EndVegetationFeatures.SALTEAGO)
                .feature(EndVegetationFeatures.GLOBULAGUS)
                .feature(EndVegetationFeatures.UMBRELLA_MOSS)
                .feature(EndVegetationFeatures.CREEPING_MOSS)
                // Water plants that populate the pond bowls
                .feature(EndVegetationFeatures.END_LILY)
                .feature(EndVegetationFeatures.POND_ANEMONE)
                .feature(EndVegetationFeatures.BUBBLE_CORAL)
                .feature(EndVegetationFeatures.CHARNIA_CYAN)
                .feature(EndVegetationFeatures.CHARNIA_GREEN)
                .spawn(EntityTypes.ENDERMAN, 3, 1, 2);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return EndTerrainBlocks.END_MOSS.defaultBlockState();
            }
        };
    }
}
