package org.betterx.betterend.registry.features;

import org.betterx.betterend.BetterEnd;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.world.level.levelgen.GenerationStep;

public class EndOreFeatures {
    public static final PlacedFeatureKey THALLASIUM_ORE = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("thallasium_ore"))
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_ORES);

    public static final PlacedFeatureKey ENDER_ORE = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("ender_ore"))
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_ORES);

    public static final PlacedFeatureKey AMBER_ORE = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("amber_ore"))
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_ORES);

    public static final PlacedFeatureKey DRAGON_BONE_BLOCK_ORE = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("dragon_bone_ore"))
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_ORES);
    
    public static final PlacedFeatureKey VIOLECITE_LAYER = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("violecite_layer"))
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_ORES);

    public static final PlacedFeatureKey FLAVOLITE_LAYER = PlacedFeatureManager
            .createKey(BetterEnd.C.mk("flavolite_layer"))
            .setDecoration(GenerationStep.Decoration.UNDERGROUND_ORES);
}
