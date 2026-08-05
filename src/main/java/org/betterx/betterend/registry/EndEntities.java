package org.betterx.betterend.registry;

import org.betterx.bclib.api.v2.spawning.SpawnRuleBuilder;
import org.betterx.bclib.entity.BCLEntityWrapper;
import org.betterx.betterend.BetterEnd;
import org.betterx.betterend.entity.*;
import org.betterx.ui.ColorUtil;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.levelgen.Heightmap.Types;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;

public class EndEntities {
    public static final BCLEntityWrapper<DragonflyEntity> DRAGONFLY = register(
            "dragonfly",
            MobCategory.AMBIENT,
            0.6F,
            0.5F,
            DragonflyEntity::new,
            DragonflyEntity.createMobAttributes(),
            true,
            ColorUtil.color(32, 42, 176),
            ColorUtil.color(115, 225, 249)
    );
    public static final BCLEntityWrapper<EndSlimeEntity> END_SLIME = register(
            "end_slime",
            MobCategory.MONSTER,
            0.5F,
            0.5F,
            EndSlimeEntity::new,
            EndSlimeEntity.createMobAttributes(),
            false,
            ColorUtil.color(28, 28, 28),
            ColorUtil.color(99, 11, 99)
    );
    public static final BCLEntityWrapper<EndFishEntity> END_FISH = register(
            "end_fish",
            MobCategory.WATER_AMBIENT,
            0.5F,
            0.5F,
            EndFishEntity::new,
            EndFishEntity.createMobAttributes(),
            true,
            ColorUtil.color(3, 50, 76),
            ColorUtil.color(120, 206, 255)
    );
    public static final BCLEntityWrapper<ShadowWalkerEntity> SHADOW_WALKER = register(
            "shadow_walker",
            MobCategory.MONSTER,
            0.6F,
            1.95F,
            ShadowWalkerEntity::new,
            ShadowWalkerEntity.createMobAttributes(),
            true,
            ColorUtil.color(30, 30, 30),
            ColorUtil.color(5, 5, 5)
    );
    public static final BCLEntityWrapper<CubozoaEntity> CUBOZOA = register(
            "cubozoa",
            MobCategory.WATER_AMBIENT,
            0.6F,
            1F,
            CubozoaEntity::new,
            CubozoaEntity.createMobAttributes(),
            true,
            ColorUtil.color(151, 77, 181),
            ColorUtil.color(93, 176, 238)
    );
    public static final BCLEntityWrapper<SilkMothEntity> SILK_MOTH = register(
            "silk_moth",
            MobCategory.AMBIENT,
            0.6F,
            0.6F,
            SilkMothEntity::new,
            SilkMothEntity.createMobAttributes(),
            true,
            ColorUtil.color(198, 138, 204),
            ColorUtil.color(242, 220, 236)
    );

    public static void register() {
        // Air //
        SpawnRuleBuilder.start(DRAGONFLY).aboveGround(2).maxNearby(4, 32).buildNoRestrictions(Types.MOTION_BLOCKING);
        SpawnRuleBuilder.start(SILK_MOTH).aboveGround(2).maxNearby(4, 32).buildNoRestrictions(Types.MOTION_BLOCKING);

        // Land //
        SpawnRuleBuilder
                .start(END_SLIME)
                .notPeaceful()
                .maxNearby(6, 32)
                .onlyOnValidBlocks()
                .customRule(EndSlimeEntity::canSpawn)
                .buildOnGround(Types.MOTION_BLOCKING_NO_LEAVES);

        SpawnRuleBuilder.start(SHADOW_WALKER)
                        .vanillaHostile()
                        .onlyOnValidBlocks()
                        .maxNearby(8, 64)
                        .buildOnGround(Types.MOTION_BLOCKING);

        // Water //
        SpawnRuleBuilder.start(END_FISH).maxNearby(16, 16).buildInWater(Types.MOTION_BLOCKING_NO_LEAVES);
        SpawnRuleBuilder.start(CUBOZOA).maxNearby(16, 16).buildInWater(Types.MOTION_BLOCKING_NO_LEAVES);
    }

    protected static <T extends Entity> EntityType<T> register(
            String name,
            MobCategory group,
            float width,
            float height,
            EntityFactory<T> entity
    ) {
        Identifier id = BetterEnd.C.mk(name);
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType<T> type = FabricEntityTypeBuilder
                .create(group, entity)
                .dimensions(EntityDimensions.fixed(width, height))
                .build(key);

        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
    }

    private static <T extends Mob> BCLEntityWrapper<T> register(
            String name,
            MobCategory group,
            float width,
            float height,
            EntityFactory<T> entity,
            Builder attributes,
            boolean fixedSize,
            int eggColor,
            int dotsColor
    ) {
        Identifier id = BetterEnd.C.mk(name);
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType<T> type = FabricEntityTypeBuilder
                .create(group, entity)
                .dimensions(fixedSize
                        ? EntityDimensions.fixed(width, height)
                        : EntityDimensions.scalable(width, height))
                .build(key);
        FabricDefaultAttributeRegistry.register(type, attributes);
        EndItems.registerEndEgg("spawn_egg_" + name, type, eggColor, dotsColor);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, BetterEnd.C.mk(name), type);

        return new BCLEntityWrapper<>(type, true);
    }
}
