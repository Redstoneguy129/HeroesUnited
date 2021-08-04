package xyz.heroesunited.heroesunited.common.objects.entities;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FlyOntoTreeGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.heroesunited.heroesunited.client.gui.HorasScreen;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
public class Horas extends PathAwareEntity implements Flutterer {
    private float flap, flapSpeed, flapping = 1.0F;
    private PlayerEntity playerEntity;
    private final EntityNavigation navigator;
    private int timeToRecalcPath;

    public Horas(EntityType<? extends Horas> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 10, false);
        this.navigator = this.getNavigation();
    }

    @Override
    public EntityData initialize(ServerWorldAccess worldIn, LocalDifficulty difficultyIn, SpawnReason reason, EntityData spawnDataIn, NbtCompound dataTag) {
        if (reason == SpawnReason.MOB_SUMMONED || reason == SpawnReason.SPAWN_EGG) this.playerEntity = worldIn.getClosestPlayer(this, 10D);
        return super.initialize(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25D));
        this.goalSelector.add(2, new FlyOntoTreeGoal(this, 1.0D));
    }

    @Override
    public boolean handleFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d vec, Hand hand) {
        if (!player.world.isClient) return ActionResult.PASS;
        MinecraftClient.getInstance().method_29970(new HorasScreen(this));
        return ActionResult.SUCCESS;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, .23F).add(EntityAttributes.GENERIC_FLYING_SPEED, .23F);
    }

    @Override
    public void baseTick() {
        if (this.playerEntity != null && !this.playerEntity.isSpectator()) {
            List<PlayerEntity> playerEntities = this.getEntityWorld().getNonSpectatingEntities(PlayerEntity.class, this.getBoundingBox().expand(10D));
            if (!playerEntities.contains(this.playerEntity)) {
                if (this.playerEntity != null && !this.isLeashed()) {
                    this.lookControl.lookAt(this.playerEntity, 10.0F, (float) this.getLookYawSpeed());
                    if (--this.timeToRecalcPath <= 0) {
                        this.timeToRecalcPath = 10;
                        double d0 = this.getX() - this.playerEntity.getX();
                        double d1 = this.getY() - this.playerEntity.getY();
                        double d2 = this.getZ() - this.playerEntity.getZ();
                        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                        if (!(d3 <= (2D * 2D))) {
                            this.navigator.startMovingTo(this.playerEntity, 1.23D);
                        } else {
                            this.navigator.stop();
                            LookControl lookcontroller = this.lookControl;
                            if (d3 <= 5D || lookcontroller.getLookX() == this.getX() && lookcontroller.getLookY() == this.getY() && lookcontroller.getLookZ() == this.getZ()) {
                                double d4 = this.playerEntity.getX() - this.getX();
                                double d5 = this.playerEntity.getZ() - this.getZ();
                                this.navigator.startMovingTo(this.getX() - d4, this.getY(), this.getZ() - d5, 1.23D);
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
        this.flapSpeed = (float) ((double) this.flapSpeed + (double) (!this.onGround && !this.hasVehicle() ? 4 : -1) * 0.3D);
        this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }
        this.flapping = (float) ((double) this.flapping * 0.9D);
        Vec3d vector3d = this.getVelocity();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setVelocity(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }
        this.flap += this.flapping * 2.0F;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        if (compound.contains("owner")) {
            UUID uuid = compound.getUuid("owner");
            this.playerEntity = this.world.getPlayerByUuid(uuid);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        if (this.playerEntity != null)
            compound.putUuid("owner", this.playerEntity.getUuid());
    }

    @Override
    public boolean isInAir() {
        return !isOnGround();
    }
}