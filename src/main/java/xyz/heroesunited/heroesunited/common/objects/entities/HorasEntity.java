package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.gui.HorasScreen;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
public class HorasEntity extends PathfinderMob implements FlyingAnimal {
    private float flapSpeed, flapping = 1.0F;
    private Player playerEntity;
    private final PathNavigation navigator;
    private int timeToRecalcPath;

    public HorasEntity(EntityType<? extends HorasEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.navigator = this.getNavigation();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {
        if (reason == MobSpawnType.MOB_SUMMONED || reason == MobSpawnType.SPAWN_EGG)
            this.playerEntity = worldIn.getNearestPlayer(this, 10D);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!player.level.isClientSide) return InteractionResult.PASS;
        Minecraft.getInstance().forceSetScreen(new HorasScreen(this));
        return InteractionResult.SUCCESS;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8D).add(Attributes.MOVEMENT_SPEED, .23F).add(Attributes.FLYING_SPEED, .23F);
    }

    @Override
    public void baseTick() {
        if (this.playerEntity != null && !this.playerEntity.isSpectator()) {
            List<Player> playerEntities = this.getCommandSenderWorld().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10D));
            if (!playerEntities.contains(this.playerEntity)) {
                if (this.playerEntity != null && !this.isLeashed()) {
                    this.lookControl.setLookAt(this.playerEntity, 10.0F, (float) this.getHeadRotSpeed());
                    if (--this.timeToRecalcPath <= 0) {
                        this.timeToRecalcPath = 10;
                        double d0 = this.getX() - this.playerEntity.getX();
                        double d1 = this.getY() - this.playerEntity.getY();
                        double d2 = this.getZ() - this.playerEntity.getZ();
                        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                        if (!(d3 <= (2D * 2D))) {
                            this.navigator.moveTo(this.playerEntity, 1.23D);
                        } else {
                            this.navigator.stop();
                            LookControl lookcontroller = this.lookControl;
                            if (d3 <= 5D || lookcontroller.getWantedX() == this.getX() && lookcontroller.getWantedY() == this.getY() && lookcontroller.getWantedZ() == this.getZ()) {
                                double d4 = this.playerEntity.getX() - this.getX();
                                double d5 = this.playerEntity.getZ() - this.getZ();
                                this.navigator.moveTo(this.getX() - d4, this.getY(), this.getZ() - d5, 1.23D);
                            }

                        }
                    }
                }
            }
        }
        super.baseTick();
        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.flapSpeed = (float) ((double) this.flapSpeed + (double) (!this.onGround && !this.isPassenger() ? 4 : -1) * 0.3D);
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }
        this.flapping = (float) ((double) this.flapping * 0.9D);
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("owner")) {
            UUID uuid = compound.getUUID("owner");
            this.playerEntity = this.level.getPlayerByUUID(uuid);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.playerEntity != null)
            compound.putUUID("owner", this.playerEntity.getUUID());
    }

    @Override
    public boolean isFlying() {
        return !this.onGround;
    }
}