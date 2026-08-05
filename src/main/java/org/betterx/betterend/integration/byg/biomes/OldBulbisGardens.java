package org.betterx.betterend.integration.byg.biomes;

import org.betterx.bclib.interfaces.SurfaceMaterialProvider;
import org.betterx.betterend.integration.Integrations;
import org.betterx.betterend.integration.byg.features.BYGFeatures;
import org.betterx.betterend.registry.EndStructures;
import org.betterx.betterend.registry.features.EndVegetationFeatures;
import org.betterx.betterend.world.biome.EndBiome;
import org.betterx.betterend.world.biome.EndBiomeBuilder;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.surface.api.Conditions;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.util.random.Weighted;

import java.util.List;


public class OldBulbisGardens extends EndBiome.Config {
    public OldBulbisGardens() {
        super();
    }

    @Override
    public void addCustomBuildData(EndBiomeBuilder builder) {


        builder.fogColor(215, 132, 207)
               .fogDensity(1.8F)
               .waterAndFogColor(40, 0, 56)
               .foliageColorOverride(122, 17, 155)
               .particles(
                       ParticleTypes.REVERSE_PORTAL,
                       0.002F
               )
               .structure(EndStructures.END_LAKE_RARE)
               .feature(BYGFeatures.OLD_BULBIS_TREE);

        Holder<Biome> biome = Integrations.BYG.getBiome("bulbis_gardens");
        if (biome == null) return;

        if (ModCore.isClient()) {
            EnvironmentAttributeMap attributes = biome.value().getAttributes();

            AmbientSounds sounds = attributes.contains(EnvironmentAttributes.AMBIENT_SOUNDS)
                    ? attributes.get(EnvironmentAttributes.AMBIENT_SOUNDS)
                                .applyModifier(EnvironmentAttributes.AMBIENT_SOUNDS.defaultValue())
                    : EnvironmentAttributes.AMBIENT_SOUNDS.defaultValue();
            BackgroundMusic music = attributes.contains(EnvironmentAttributes.BACKGROUND_MUSIC)
                    ? attributes.get(EnvironmentAttributes.BACKGROUND_MUSIC)
                                .applyModifier(EnvironmentAttributes.BACKGROUND_MUSIC.defaultValue())
                    : EnvironmentAttributes.BACKGROUND_MUSIC.defaultValue();

            sounds.loop().ifPresent(builder::loop);
            music.defaultMusic().ifPresent(m -> builder.music(m.sound()));
            sounds.additions().stream().findFirst().ifPresent(a -> builder.additions(a.soundEvent()));
            sounds.mood().ifPresent(m -> builder.mood(m.soundEvent()));
        }

        for (MobCategory group : MobCategory.values()) {
            List<Weighted<SpawnerData>> list = biome.value()
                                                     .getMobSettings()
                                                     .getMobs(group)
                                                     .unwrap();
            list.forEach((weighted) -> {
                SpawnerData entry = weighted.value();
                builder.spawn((EntityType<? extends Mob>) entry.type(), 1, entry.minCount(), entry.maxCount());
            });
        }

        List<HolderSet<PlacedFeature>> features = biome.value().getGenerationSettings()
                                                       .features();
        HolderSet<PlacedFeature> vegetal = features.get(Decoration.VEGETAL_DECORATION.ordinal());
//        if (vegetal.size() > 2) {
//            Supplier<PlacedFeature> getter;
        for (var feature : vegetal) {
            builder.feature(Decoration.VEGETAL_DECORATION, feature);
        }
//			// Trees (first two features)
//			// I couldn't process them with conditions, so that's why they are hardcoded (paulevs)
//			for (int i = 0; i < 2; i++) {
//				getter = vegetal.get(i);
//				Holder<PlacedFeature> feature = getter.get();
//				ResourceLocation id = BetterEnd.makeID("obg_feature_" + i);
//				feature = Registry.register(
//						BuiltinRegistries.PLACED_FEATURE,
//						id,
//						feature
//				);
//				builder.feature(Decoration.VEGETAL_DECORATION, feature);
//			}
//			// Grasses and other features
//			for (int i = 2; i < vegetal.size(); i++) {
//				getter = vegetal.get(i);
//				Holder<PlacedFeature> feature = getter.get();
//				builder.feature(Decoration.VEGETAL_DECORATION, feature);
//			}
//        }

        builder.feature(EndVegetationFeatures.PURPLE_POLYPORE)
               .feature(BYGFeatures.IVIS_MOSS_WOOD)
               .feature(BYGFeatures.IVIS_MOSS)
               .feature(BYGFeatures.IVIS_VINE)
               .feature(BYGFeatures.IVIS_SPROUT);
    }

    @Override
    public SurfaceMaterialProvider surfaceMaterial() {
        return new EndBiome.DefaultSurfaceMaterialProvider() {
            @Override
            public BlockState getTopMaterial() {
                return Integrations.BYG.getBlock("ivis_phylium").defaultBlockState();
            }

            @Override
            public BlockState getAltTopMaterial() {
                return Integrations.BYG.getBlock("bulbis_phycelium").defaultBlockState();
            }

            @Override
            public SurfaceRuleBuilder surface() {
                return SurfaceRuleBuilder
                        .start()
                        .rule(
                                SurfaceRules.sequence(SurfaceRules.ifTrue(
                                                BYGBiomes.BYG_WATER_CHECK,
                                                SurfaceRules.ifTrue(
                                                        SurfaceRules.ON_FLOOR,
                                                        SurfaceRules.sequence(
                                                                SurfaceRules.ifTrue(
                                                                        Conditions.roughNoise(Noises.NETHERRACK, 0.19),
                                                                        SurfaceRules.state(getTopMaterial())
                                                                ),
                                                                SurfaceRules.state(getAltTopMaterial())
                                                        )
                                                )
                                        )
                                ),
                                4
                        );
            }
        };
    }

}
