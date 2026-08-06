package org.betterx.betterend.entity;

import org.betterx.bclib.util.BlocksHelper;
import org.betterx.bclib.util.MHelper;
import org.betterx.betterend.interfaces.ISlime;
import org.betterx.betterend.registry.EndBiomes;
import org.betterx.betterend.util.GlobalState;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.EnumSet;
import org.jetbrains.annotations.Nullable;

public class EndSlimeEntity extends Slime {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(
            EndSlimeEntity.class,
            EntityDataSerializers.INT
    );

    public EndSlimeEntity(EntityType<EndSlimeEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new EndSlimeMoveControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimmingGoal());
        this.goalSelector.addGoal(2, new FaceTowardTargetGoal());
        this.goalSelector.addGoal(3, new RandomLookGoal());
        this.goalSelector.addGoal(5, new MoveGoal());
        this.targetSelector.addGoal(
                1,
                new NearestAttackableTargetGoal<>(
                        this,
                        Player.class,
                        10,
                        true,
                        false,
                        (livingEntity, level) -> Math.abs(livingEntity.getY() - this.getY()) <= 4.0D
                )
        );
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return LivingEntity
                .createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData entityData
    ) {
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, spawnReason, entityData);

        Holder<Biome> biome = world.getBiome(blockPosition());
        if (biome.unwrapKey().isPresent()) {
            if (biome.is(EndBiomes.FOGGY_MUSHROOMLAND.key)) {
                this.setMossy();
            } else if (biome.is(EndBiomes.MEGALAKE.key) || biome.is(EndBiomes.MEGALAKE_GROVE.key)) {
                this.setLake();
            } else if (biome.is(EndBiomes.AMBER_LAND.key)) {
                this.setAmber();
            }
            this.refreshDimensions();
        }
        return data;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", getSlimeType());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(VARIANT, input.getIntOr("Variant", this.entityData.get(VARIANT)));
    }

    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.PORTAL;
    }

    @Override
    public void remove(RemovalReason reason) {
        int i = this.getSize();
        if (!this.level().isClientSide() && i > 1 && this.isDeadOrDying()) {
            Component text = this.getCustomName();
            boolean bl = this.isNoAi();
            float f = (float) i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);
            int type = this.getSlimeType();

            for (int l = 0; l < k; ++l) {
                float g = ((float) (l % 2) - 0.5F) * f;
                float h = ((float) (l / 2) - 0.5F) * f;
                EndSlimeEntity slimeEntity = (EndSlimeEntity) this.getType().create(this.level(), EntitySpawnReason.TRIGGERED);
                if (this.isPersistenceRequired()) {
                    slimeEntity.setPersistenceRequired();
                }

                slimeEntity.setSlimeType(type);
                slimeEntity.setCustomName(text);
                slimeEntity.setNoAi(bl);
                slimeEntity.setInvulnerable(this.isInvulnerable());
                ((ISlime) slimeEntity).be_setSlimeSize(j, true);
                slimeEntity.refreshDimensions();
                slimeEntity.snapTo(
                        this.getX() + (double) g,
                        this.getY() + 0.5D,
                        this.getZ() + (double) h,
                        this.random.nextFloat() * 360.0F,
                        0.0F
                );
                this.level().addFreshEntity(slimeEntity);
            }
        }

        ((ISlime) this).entityRemove(reason);
    }

    @Override
    protected void dropFromLootTable(ServerLevel serverLevel, DamageSource source, boolean causedByPlayer) {
        int maxCount = this.getSize();
        int minCount = maxCount >> 1;
        if (minCount < 1) {
            minCount = 1;
        }
        Player lastHurtByPlayer = this.getLastHurtByPlayer();
        if (causedByPlayer && lastHurtByPlayer != null) {
            int looting = EnchantmentUtils.getItemEnchantmentLevel(
                    lastHurtByPlayer.level(),
                    Enchantments.LOOTING,
                    lastHurtByPlayer
            );
            minCount += looting;
        }
        int count = minCount < maxCount ? MHelper.randRange(minCount, maxCount, random) : maxCount;
        ItemEntity drop = new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(Items.SLIME_BALL, count));
        this.level().addFreshEntity(drop);
    }

    public int getSlimeType() {
        return this.entityData.get(VARIANT);
    }

    public void setSlimeType(int value) {
        this.entityData.set(VARIANT, value);
    }

    protected void setMossy() {
        setSlimeType(1);
    }

    public boolean isMossy() {
        return getSlimeType() == 1;
    }

    protected void setLake() {
        setSlimeType(2);
    }

    public boolean isLake() {
        return getSlimeType() == 2;
    }

    protected void setAmber() {
        setSlimeType(3);
    }

    public boolean isAmber() {
        return this.entityData.get(VARIANT) == 3;
    }

    public boolean isChorus() {
        return this.entityData.get(VARIANT) == 0;
    }

    public static boolean canSpawn(
            EntityType entityType,
            LevelAccessor world,
            EntitySpawnReason spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        if (!world.getBlockState(pos.below()).is(CommonBlockTags.END_STONES)) {
            return false;
        }
        Holder<Biome> biome = world.getBiome(pos);
        if (biome.unwrapKey().isPresent()) {
            if (biome.equals(EndBiomes.CHORUS_FOREST) || biome.equals(EndBiomes.MEGALAKE)) {
                return true;
            }
            if (biome.equals(EndBiomes.MEGALAKE_GROVE) && random.nextBoolean()) {
                return true;
            }
            // Foggy Mushroomland (mossy) and Amber Land (amber) assign their own slime variant in
            // finalizeSpawn, but are dry, so the water-near fallback below almost never fires there
            // - especially Amber Land. Give them a low waterless spawn rate so those variants
            // actually appear. Tune the 1-in-8 chance to taste.
            if (biome.is(EndBiomes.FOGGY_MUSHROOMLAND.key) || biome.is(EndBiomes.AMBER_LAND.key)) {
                return random.nextInt(8) == 0;
            }
        }
        return random.nextInt(4) == 0 && isWaterNear(world, pos);
    }

    private static boolean isWaterNear(LevelAccessor world, BlockPos pos) {
        final MutableBlockPos POS = GlobalState.stateForThread().POS;

        for (int x = pos.getX() - 32; x <= pos.getX() + 32; x++) {
            POS.setX(x);
            for (int z = pos.getZ() - 32; z <= pos.getZ() + 32; z++) {
                POS.setZ(z);
                for (int y = pos.getY() - 8; y <= pos.getY() + 8; y++) {
                    POS.setY(y);
                    if (world.getBlockState(POS).getBlock() == Blocks.WATER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    class MoveGoal extends Goal {
        public MoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EndSlimeEntity.this.isPassenger()) {
                return false;
            }

            float yaw = EndSlimeEntity.this.getYHeadRot();
            float speed = EndSlimeEntity.this.getSpeed();
            if (speed > 0.1) {
                float dx = Mth.sin(-yaw * 0.017453292F);
                float dz = Mth.cos(-yaw * 0.017453292F);
                BlockPos pos = EndSlimeEntity.this.blockPosition().offset(
                        (int) (dx * speed * 4),
                        0,
                        (int) (dz * speed * 4)
                );
                int down = BlocksHelper.downRay(EndSlimeEntity.this.level(), pos, 16);
                return down < 5;
            }

            return true;
        }

        public void tick() {
            ((EndSlimeMoveControl) EndSlimeEntity.this.getMoveControl()).move(1.0D);
        }
    }

    class SwimmingGoal extends Goal {
        public SwimmingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            EndSlimeEntity.this.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (EndSlimeEntity.this.isInWater() || EndSlimeEntity.this.isInLava()) && EndSlimeEntity.this.getMoveControl() instanceof EndSlimeMoveControl;
        }

        public void tick() {
            if (EndSlimeEntity.this.getRandom().nextFloat() < 0.8F) {
                EndSlimeEntity.this.getJumpControl().jump();
            }

            ((EndSlimeMoveControl) EndSlimeEntity.this.getMoveControl()).move(1.2D);
        }
    }

    class RandomLookGoal extends Goal {
        private float targetYaw;
        private int timer;

        public RandomLookGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return EndSlimeEntity.this.getTarget() == null && (EndSlimeEntity.this.onGround() || EndSlimeEntity.this.isInWater() || EndSlimeEntity.this
                    .isInLava() || EndSlimeEntity.this.hasEffect(MobEffects.LEVITATION)) && EndSlimeEntity.this.getMoveControl() instanceof EndSlimeMoveControl;
        }

        public void tick() {
            if (--this.timer <= 0) {
                this.timer = 40 + EndSlimeEntity.this.getRandom().nextInt(60);
                this.targetYaw = (float) EndSlimeEntity.this.getRandom().nextInt(360);
            }

            ((EndSlimeMoveControl) EndSlimeEntity.this.getMoveControl()).look(this.targetYaw, false);
        }
    }

    class FaceTowardTargetGoal extends Goal {
        private int ticksLeft;

        public FaceTowardTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingEntity = EndSlimeEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                return (!(livingEntity instanceof Player) || !((Player) livingEntity).getAbilities().invulnerable) && EndSlimeEntity.this
                        .getMoveControl() instanceof EndSlimeMoveControl;
            }
        }

        public void start() {
            this.ticksLeft = 300;
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingEntity = EndSlimeEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (livingEntity instanceof Player && ((Player) livingEntity).getAbilities().invulnerable) {
                return false;
            } else {
                return --this.ticksLeft > 0;
            }
        }

        public void tick() {
            EndSlimeEntity.this.lookAt(EndSlimeEntity.this.getTarget(), 10.0F, 10.0F);
            ((EndSlimeMoveControl) EndSlimeEntity.this.getMoveControl()).look(
                    EndSlimeEntity.this.getYRot(),
                    EndSlimeEntity.this.isDealsDamage()
            );
        }
    }

    class EndSlimeMoveControl extends MoveControl {
        private float targetYaw;
        private int ticksUntilJump;
        private boolean jumpOften;

        public EndSlimeMoveControl(EndSlimeEntity slime) {
            super(slime);
            this.targetYaw = 180.0F * slime.getYRot() / 3.1415927F;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speedModifier = speed;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.targetYaw, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.ticksUntilJump-- <= 0) {
                        this.ticksUntilJump = EndSlimeEntity.this.getJumpDelay();
                        if (this.jumpOften) {
                            this.ticksUntilJump /= 3;
                        }

                        EndSlimeEntity.this.getJumpControl().jump();
                        if (EndSlimeEntity.this.doPlayJumpSound()) {
                            EndSlimeEntity.this.playSound(
                                    EndSlimeEntity.this.getJumpSound(),
                                    EndSlimeEntity.this.getSoundVolume(),
                                    getJumpSoundPitch()
                            );
                        }
                    } else {
                        EndSlimeEntity.this.xxa = 0.0F;
                        EndSlimeEntity.this.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }

        private float getJumpSoundPitch() {
            float f = EndSlimeEntity.this.isTiny() ? 1.4F : 0.8F;
            return ((EndSlimeEntity.this.random.nextFloat() - EndSlimeEntity.this.random.nextFloat()) * 0.2F + 1.0F) * f;
        }
    }
}
