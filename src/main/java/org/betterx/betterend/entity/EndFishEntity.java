package org.betterx.betterend.entity;

import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.registry.EndItems;
import org.betterx.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

public class EndFishEntity extends AbstractSchoolingFish {
    public static final int VARIANTS_NORMAL = 5;
    public static final int VARIANTS_SULPHUR = 3;
    public static final int VARIANTS = VARIANTS_NORMAL + VARIANTS_SULPHUR;
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(
            EndFishEntity.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Integer> SCALE = SynchedEntityData.defineId(
            EndFishEntity.class,
            EntityDataSerializers.INT
    );

    public EndFishEntity(EntityType<EndFishEntity> entityType, Level world) {
        super(entityType, world);
        updateScaleAttribute();
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            SpawnGroupData entityData
    ) {
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, spawnReason, entityData);

        Holder<Biome> biome = world.getBiome(blockPosition());
        if (biome.is(EndBiomes.SULPHUR_SPRINGS.key)) {
            this.entityData.set(VARIANT, (random.nextInt(VARIANTS_SULPHUR) + VARIANTS_NORMAL));
        }

        this.refreshDimensions();
        return data;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, this.getRandom().nextInt(VARIANTS_NORMAL));
        builder.define(SCALE,  this.getRandom().nextInt(16));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", getVariant());
        output.putInt("Scale", this.entityData.get(SCALE));
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(VARIANT, input.getIntOr("Variant", this.entityData.get(VARIANT)));
        this.entityData.set(SCALE, input.getIntOr("Scale", this.entityData.get(SCALE)));
        updateScaleAttribute();
    }

    @Override
    public void saveToBucketTag(ItemStack itemStack) {
        super.saveToBucketTag(itemStack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, itemStack, (tag) -> {
            tag.putInt("variant", entityData.get(VARIANT));
            tag.putInt("scale", entityData.get(SCALE));
        });
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        ItemStack bucket = EndItems.BUCKET_END_FISH.getDefaultInstance();
//        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
//            tag.putByte("variant", entityData.get(VARIANT));
//            tag.putByte("scale", entityData.get(SCALE));
//        });
        return bucket;
    }

    @Override
    protected @NotNull SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SALMON_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SALMON_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SALMON_HURT;
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextInt(8) == 0 && getInBlockState().is(Blocks.WATER)) {
            double x = getX() + random.nextGaussian() * 0.2;
            double y = getY() + random.nextGaussian() * 0.2;
            double z = getZ() + random.nextGaussian() * 0.2;
            level().addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
        }
    }

    public static AttributeSupplier.@NotNull Builder createMobAttributes() {
        return LivingEntity
                .createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.75);
    }

    public int getVariant() {
        return (int) this.entityData.get(VARIANT);
    }

    private void updateScaleAttribute() {
        AttributeInstance scaleAttribute = this.getAttribute(Attributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(this.entityData.get(SCALE) / 32F + 0.75F);
        }
    }

    @Override
    protected void dropFromLootTable(ServerLevel serverLevel, DamageSource source, boolean causedByPlayer) {
        Item item = source.is(DamageTypeTags.IS_FIRE) ? EndItems.END_FISH_COOKED : EndItems.END_FISH_RAW;
        if (causedByPlayer) {
            ItemStack handItem = ((Player) source.getEntity()).getItemInHand(InteractionHand.MAIN_HAND);
            if (EnchantmentUtils.getItemEnchantmentLevel(
                    source.getEntity().level(),
                    Enchantments.FIRE_ASPECT,
                    handItem
            ) > 0) {
                item = EndItems.END_FISH_COOKED;
            }
        }
        ItemEntity drop = new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(item));
        this.level().addFreshEntity(drop);
    }
}
